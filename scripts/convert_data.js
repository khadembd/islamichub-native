#!/usr/bin/env node
/**
 * Islamic Hub — JS data → JSON converter
 * --------------------------------------------------
 * Reads every `*-data.js` (and other data-style JS) file from the
 * uploaded Capacitor source, evaluates it in a sandboxed Node VM
 * context (no `window`, no `document`, no `Capacitor`), captures
 * the declared top-level global(s), and writes clean JSON files
 * into `app/src/main/assets/data/`.
 *
 * Also produces a `migration-report.json` listing source file,
 * captured variable name, output file, record counts, and SHA-256
 * checksums for parity validation.
 *
 * Usage:  node scripts/convert_data.js <source_dir> <output_dir>
 */

const fs = require('fs');
const path = require('path');
const vm = require('vm');
const crypto = require('crypto');

const SOURCE_DIR = process.argv[2] || '/home/z/my-project/sources/islamic-hub-source/islamichub';
const OUTPUT_DIR = process.argv[3] || path.resolve(__dirname, '../app/src/main/assets/data');

if (!fs.existsSync(SOURCE_DIR)) {
  console.error('Source dir not found:', SOURCE_DIR);
  process.exit(1);
}

fs.mkdirSync(OUTPUT_DIR, { recursive: true });

// Mapping: source JS file → { globals to capture, output filename }
const FILE_MAP = [
  { src: 'kalima-data.js',           globals: ['kalimaData'],           out: 'kalima.json' },
  { src: 'dua-data.js',              globals: ['DUA_DATA'],             out: 'dua.json' },
  { src: 'asmaul-husna-data.js',     globals: ['asmaulHusnaData'],      out: 'asmaul_husna.json' },
  { src: 'namaz-data.js',            globals: ['namazData'],            out: 'namaz.json' },
  { src: 'namaz-extras-data.js',     globals: ['namazExtras'],          out: 'namaz_extras.json' },
  { src: 'extended-namaz-data.js',   globals: ['extendedNamazData'],    out: 'extended_namaz.json' },
  { src: 'namazshikkha-data.js',     globals: ['namazData'],            out: 'namaz_shikkha.json' }, // overrides window.namazData from namaz-data.js, kept separately per migration plan §15
  { src: 'hadith-data.js',           globals: ['hadithData'],           out: 'hadith.json' },
  { src: 'extended-hadith-data.js',  globals: ['extendedHadithData'],   out: 'extended_hadith.json' },
  { src: 'question-data.js',         globals: ['questionData'],         out: 'questions.json' },
  { src: 'ans-data.js',              globals: ['ansData'],              out: 'answers.json' },
  { src: 'misconceptions-data.js',   globals: ['misconceptionsData'],   out: 'misconceptions.json' },
  { src: 'islamic-stories-data.js',  globals: ['IslamicStoriesData'],   out: 'islamic_stories.json' },
  { src: 'location-data.js',         globals: ['BANGLADESH_LOCATIONS'], out: 'locations.json' },
  // daily-content.js is a logic module (IIFE) with no top-level data array — skipped from data conversion, will be implemented natively
];

function countRecords(value) {
  if (Array.isArray(value)) return value.length;
  if (value && typeof value === 'object') {
    // try common nested array keys
    for (const k of ['items', 'hadiths', 'duas', 'categories', 'kalimas', 'prayers', 'questions', 'stories', 'prophets', 'khalifas', 'data', 'entries', 'records', 'list']) {
      if (Array.isArray(value[k])) return value[k].length;
    }
    return Object.keys(value).length;
  }
  return 0;
}

function sha256(buf) {
  return crypto.createHash('sha256').update(buf).digest('hex');
}

const report = [];
let totalConverted = 0;
let totalErrors = 0;

for (const entry of FILE_MAP) {
  const srcPath = path.join(SOURCE_DIR, entry.src);
  if (!fs.existsSync(srcPath)) {
    console.warn(`[skip] missing: ${entry.src}`);
    report.push({ ...entry, status: 'missing', error: 'source file not found' });
    continue;
  }

  // Transform top-level declarations so the value attaches to the sandbox
  // global object:
  //   1. Strip UTF-8 BOM if present
  //   2. Normalize CRLF / CR → LF (so `^` regex anchors work)
  //   3. Strip `"use strict"` (so `var` leaks to global)
  //   4. `const X = ` / `let X = ` → `var X = `
  //   5. `window.X = ` → `var X = ` (we re-attach to sandbox.window later)
  let code = fs.readFileSync(srcPath, 'utf8');
  if (code.charCodeAt(0) === 0xFEFF) code = code.slice(1);
  code = code.replace(/\r\n/g, '\n').replace(/\r/g, '\n');
  code = code.replace(/^['"]use strict['"];?\s*\n/m, '');
  code = code.replace(/^(const|let)\s+([A-Za-z_$][\w$]*)\s*=/gm, 'var $2 =');
  code = code.replace(/^window\.([A-Za-z_$][\w$]*)\s*=/gm, 'var $1 =');

  // Build sandbox with stubs for window/document/Capacitor so browser code does not crash
  const sandbox = {
    window: {},
    document: { addEventListener() {}, querySelector() { return null; }, createElement() { return {}; } },
    console: { log() {}, warn() {}, error() {} },
    navigator: {},
    Capacitor: { isNativePlatform: () => false, registerPlugin: () => ({}) },
    localStorage: { getItem() { return null; }, setItem() {}, removeItem() {} },
    setTimeout, clearTimeout, setInterval, clearInterval,
    JSON, Math, Date, Object, Array, String, Number, Boolean, RegExp, Map, Set,
    parseInt, parseFloat, isNaN,
  };
  sandbox.window = sandbox; // self-reference

  try {
    vm.createContext(sandbox);
    vm.runInContext(code, sandbox, { filename: entry.src, timeout: 5000 });
  } catch (e) {
    console.error(`[err ] eval failed for ${entry.src}: ${e.message}`);
    report.push({ ...entry, status: 'eval_error', error: e.message });
    totalErrors++;
    continue;
  }

  // Capture each declared global
  for (let i = 0; i < entry.globals.length; i++) {
    const g = entry.globals[i];
    const value = sandbox[g] || sandbox.window[g];
    if (typeof value === 'undefined') {
      console.warn(`[warn] global not found: ${g} in ${entry.src}`);
      report.push({ ...entry, status: 'global_missing', global: g });
      totalErrors++;
      continue;
    }
    const outName = entry.globals.length === 1 ? entry.out : `${path.basename(entry.out, '.json')}.${g}.json`;
    const json = JSON.stringify(value, null, 2);
    const buf = Buffer.from(json, 'utf8');
    const outPath = path.join(OUTPUT_DIR, outName);
    fs.writeFileSync(outPath, buf);
    const count = countRecords(value);
    const checksum = sha256(buf);
    console.log(`[ok  ] ${entry.src.padEnd(34)} → ${outName.padEnd(28)} records=${String(count).padStart(5)}  size=${buf.length}B  sha=${checksum.slice(0, 12)}`);
    report.push({ src: entry.src, global: g, out: outName, status: 'ok', records: count, bytes: buf.length, sha256: checksum });
    totalConverted++;
  }
}

// Write migration report
const reportPath = path.join(OUTPUT_DIR, 'migration-report.json');
fs.writeFileSync(reportPath, JSON.stringify({
  generatedAt: new Date().toISOString(),
  source: SOURCE_DIR,
  output: OUTPUT_DIR,
  totalConverted,
  totalErrors,
  files: report,
}, null, 2));
console.log(`\nDone. ${totalConverted} files converted, ${totalErrors} errors. Report: ${reportPath}`);
