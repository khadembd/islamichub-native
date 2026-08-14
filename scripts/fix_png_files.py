#!/usr/bin/env python3
"""
Convert all .png files in drawable-nodpi/ that are actually JPEG-encoded
to true PNG format. AAPT refuses to compile JPEG-data masquerading as .png
inside res/drawable-*/, so we need to re-encode them properly.

Also detects any other suspicious files and reports them.
"""
import os
import sys
from PIL import Image

DRAWABLE_DIR = "/home/z/my-project/islamichub-native/app/src/main/res/drawable-nodpi"

def png_signature_ok(path):
    """Real PNG starts with \\x89PNG\\r\\n\\x1a\\n"""
    with open(path, "rb") as f:
        return f.read(8) == b"\x89PNG\r\n\x1a\n"

def is_jpeg(path):
    with open(path, "rb") as f:
        return f.read(3) == b"\xff\xd8\xff"

fixed = 0
errors = 0
for name in sorted(os.listdir(DRAWABLE_DIR)):
    if not name.endswith(".png"):
        continue
    path = os.path.join(DRAWABLE_DIR, name)
    if png_signature_ok(path):
        continue
    kind = "JPEG" if is_jpeg(path) else "UNKNOWN"
    print(f"  [fix] {name} ({kind}-as-png, {os.path.getsize(path)}B) → re-encoding as PNG")
    try:
        img = Image.open(path)
        img.load()  # force decode
        # RGBA if mode has alpha, else RGB
        if img.mode in ("RGBA", "LA") or (img.mode == "P" and "transparency" in img.info):
            img = img.convert("RGBA")
        else:
            img = img.convert("RGB")
        img.save(path, "PNG", optimize=True)
        fixed += 1
        print(f"         OK — now {os.path.getsize(path)}B")
    except Exception as e:
        print(f"         FAIL: {e}")
        errors += 1

print()
print(f"Fixed: {fixed}  Errors: {errors}")

# Also report any other drawable files
print()
print("=== All drawable-nodpi files ===")
for name in sorted(os.listdir(DRAWABLE_DIR)):
    path = os.path.join(DRAWABLE_DIR, name)
    with open(path, "rb") as f:
        head = f.read(8)
    if head.startswith(b"\x89PNG"):
        kind = "PNG"
    elif head.startswith(b"\xff\xd8\xff"):
        kind = "JPEG"
    elif head.startswith(b"RIFF") and head[8:12] == b"WEBP":
        kind = "WebP"
    elif head.startswith(b"<svg") or head.startswith(b"<?xml"):
        kind = "XML"
    else:
        kind = f"?{head[:4]!r}"
    print(f"  {name:50s} {kind}")
