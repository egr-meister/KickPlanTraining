#!/usr/bin/env python3
"""
Generate all Android launcher-icon and splash assets from a single master PNG.

Usage:
    python3 scripts/generate_icons.py path/to/icon_master.png

It writes:
  - Legacy square + round PNG mipmaps (mdpi..xxxhdpi)  -> API 24/25
  - Adaptive-icon bitmap foreground per density        -> API 26+
  - A solid dark adaptive background                   -> API 26+
  - A centered splash PNG per density                  -> Android 12 splash
  - ic_launcher-playstore.png (512x512)                -> Play Console listing

It also removes the old vector icon resources so there are no name clashes,
and patches the adaptive-icon XML + splash background color.

Requires Pillow:  pip install pillow --break-system-packages
"""
import os
import sys

try:
    from PIL import Image, ImageDraw
except ImportError:
    sys.exit("Pillow is required. Install with: pip install pillow --break-system-packages")

LEGACY = {"mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}
ADAPTIVE = {"mdpi": 108, "hdpi": 162, "xhdpi": 216, "xxhdpi": 324, "xxxhdpi": 432}

ADAPTIVE_XML = """<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@mipmap/ic_launcher_foreground" />
</adaptive-icon>
"""

BLACK_BG_XML = """<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path android:fillColor="#0A0A0A" android:pathData="M0,0 h108 v108 h-108 z" />
</vector>
"""


def square(img):
    w, h = img.size
    s = min(w, h)
    return img.crop(((w - s) // 2, (h - s) // 2, (w - s) // 2 + s, (h - s) // 2 + s))


def round_mask(im):
    size = im.size[0]
    mask = Image.new("L", (size, size), 0)
    ImageDraw.Draw(mask).ellipse((0, 0, size - 1, size - 1), fill=255)
    out = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    out.paste(im, (0, 0), mask)
    return out


def save(im, path):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    im.save(path)


def write_text(path, text):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)


def main():
    if len(sys.argv) < 2:
        sys.exit("Usage: python3 scripts/generate_icons.py path/to/icon_master.png")
    master = sys.argv[1]
    proj = os.path.abspath(os.path.join(os.path.dirname(os.path.abspath(__file__)), ".."))
    res = os.path.join(proj, "app", "src", "main", "res")

    img = square(Image.open(master).convert("RGBA"))

    # Legacy square + round mipmaps
    for d, sz in LEGACY.items():
        im = img.resize((sz, sz), Image.LANCZOS)
        save(im, f"{res}/mipmap-{d}/ic_launcher.png")
        save(round_mask(im), f"{res}/mipmap-{d}/ic_launcher_round.png")

    # Adaptive foreground (full-bleed; the art's own dark corners get masked)
    for d, sz in ADAPTIVE.items():
        save(img.resize((sz, sz), Image.LANCZOS), f"{res}/mipmap-{d}/ic_launcher_foreground.png")

    # Splash icon: art centered at ~66% inside the Android 12 splash circle
    for d, sz in ADAPTIVE.items():
        canvas = Image.new("RGBA", (sz, sz), (0, 0, 0, 0))
        inner = int(sz * 0.66)
        im = img.resize((inner, inner), Image.LANCZOS)
        canvas.paste(im, ((sz - inner) // 2, (sz - inner) // 2), im)
        save(canvas, f"{res}/drawable-{d}/ic_splash_football.png")

    # Play Store icon
    save(img.resize((512, 512), Image.LANCZOS).convert("RGB"), f"{proj}/ic_launcher-playstore.png")

    # Remove old vector resources that would clash with the new bitmaps
    for rel in [
        "drawable/ic_launcher_foreground.xml",
        "mipmap/ic_launcher.xml",
        "mipmap/ic_launcher_round.xml",
        "drawable/ic_splash_football.xml",
    ]:
        fp = os.path.join(res, rel)
        if os.path.exists(fp):
            os.remove(fp)

    # Dark adaptive background + adaptive XML
    write_text(f"{res}/drawable/ic_launcher_background.xml", BLACK_BG_XML)
    write_text(f"{res}/mipmap-anydpi-v26/ic_launcher.xml", ADAPTIVE_XML)
    write_text(f"{res}/mipmap-anydpi-v26/ic_launcher_round.xml", ADAPTIVE_XML)

    # Patch splash background colour to dark to match the new icon
    themes = f"{res}/values/themes.xml"
    if os.path.exists(themes):
        with open(themes, encoding="utf-8") as f:
            t = f.read()
        t = t.replace(
            "<item name=\"windowSplashScreenBackground\">@color/match_red_orange</item>",
            "<item name=\"windowSplashScreenBackground\">@color/splash_bg</item>",
        )
        with open(themes, "w", encoding="utf-8") as f:
            f.write(t)

    colors = f"{res}/values/colors.xml"
    if os.path.exists(colors):
        with open(colors, encoding="utf-8") as f:
            c = f.read()
        if "splash_bg" not in c:
            c = c.replace("</resources>", "    <color name=\"splash_bg\">#0A0A0A</color>\n</resources>")
            with open(colors, "w", encoding="utf-8") as f:
                f.write(c)

    print("Done. Generated launcher icons, adaptive layers, splash and Play Store icon.")


if __name__ == "__main__":
    main()
