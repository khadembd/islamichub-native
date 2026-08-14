#!/usr/bin/env bash
# Copy visual + audio assets from the original Capacitor source into the
# proper Android resource directories.
#
# - Images (PNG / WebP / SVG) → app/src/main/res/drawable-nodpi/
#   (nodpi because these are full-bleed backgrounds / banners whose
#    density is dictated by the source art, not Android qualifiers.)
# - Launcher icons (icon-192 / icon-512 / favicon) → mipmap-anydpi-v26/
#   as adaptive icon assets, plus legacy copies under mipmap-*
# - Namaz MP3 audio → app/src/main/res/raw/
#
# Resource names are lowercased and dashes → underscores so they are
# valid Android resource identifiers.

set -euo pipefail

SRC="${1:-/home/z/my-project/sources/islamic-hub-source/islamichub}"
APP_RES="$(cd "$(dirname "$0")/.." && pwd)/app/src/main/res"

mkdir -p "$APP_RES/drawable-nodpi" "$APP_RES/mipmap-anydpi-v26" "$APP_RES/raw"

count_img=0
count_audio=0

# --- root images ---
for f in favicon.png icon-192.png icon-512.png logo.png \
         islamic_banner.webp islamic_premium_bg.webp quran_banner.webp; do
  if [[ -f "$SRC/$f" ]]; then
    name=$(echo "$f" | tr '[:upper:]' '[:lower:]' | tr '-' '_' | tr '.' '_')
    # keep original extension
    ext="${f##*.}"
    base="${f%.*}"
    base=$(echo "$base" | tr '[:upper:]' '[:lower:]' | tr '-' '_')
    cp "$SRC/$f" "$APP_RES/drawable-nodpi/${base}.${ext}"
    count_img=$((count_img+1))
  fi
done

# --- img/ directory ---
if [[ -d "$SRC/img" ]]; then
  for f in "$SRC/img/"*; do
    [[ -f "$f" ]] || continue
    base=$(basename "$f")
    # Skip weird files like ht.hlml
    case "$base" in
      *.webp|*.png|*.jpg|*.jpeg|*.svg) ;;
      *) echo "  [skip] $base (not a recognized image)"; continue ;;
    esac
    name=$(echo "$base" | tr '[:upper:]' '[:lower:]' | tr '-' '_' | tr '.' '_')
    ext="${base##*.}"
    base2="${base%.*}"
    base2=$(echo "$base2" | tr '[:upper:]' '[:lower:]' | tr '-' '_')
    cp "$f" "$APP_RES/drawable-nodpi/${base2}.${ext}"
    count_img=$((count_img+1))
  done
fi

# --- namaz audio → res/raw ---
if [[ -d "$SRC/namaz-audio" ]]; then
  for f in "$SRC/namaz-audio/"*.mp3; do
    [[ -f "$f" ]] || continue
    base=$(basename "$f" .mp3)
    name=$(echo "$base" | tr '[:upper:]' '[:lower:]' | tr '-' '_' | tr '.' '_')
    cp "$f" "$APP_RES/raw/${name}.mp3"
    count_audio=$((count_audio+1))
  done
fi

# --- adaptive launcher icon foreground/background assets ---
# Reuse icon-512 as the foreground drawable (will be masked by adaptive icon)
if [[ -f "$SRC/icon-512.png" ]]; then
  cp "$SRC/icon-512.png" "$APP_RES/drawable-nodpi/ic_launcher_foreground.png"
fi
if [[ -f "$SRC/logo.png" ]]; then
  cp "$SRC/logo.png" "$APP_RES/drawable-nodpi/ic_launcher_background.png"
fi

echo "[ok] images copied: $count_img  → $APP_RES/drawable-nodpi/"
echo "[ok] audio  copied: $count_audio  → $APP_RES/raw/"
echo ""
echo "Drawable files:"
ls "$APP_RES/drawable-nodpi/" | head -20
echo "..."
echo "Total drawables: $(ls "$APP_RES/drawable-nodpi/" | wc -l)"
echo "Total raw audio: $(ls "$APP_RES/raw/" | wc -l)"
