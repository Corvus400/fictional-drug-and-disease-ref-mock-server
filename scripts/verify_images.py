#!/usr/bin/env python3
import argparse
import subprocess
import sys
from pathlib import Path


EXPECTED_SIZES = {
    "S": (64, 96),
    "M": (128, 192),
    "Original": (512, 768),
}


def png_chunks(path: Path) -> list[str]:
    data = path.read_bytes()
    if data[:8] != b"\x89PNG\r\n\x1a\n":
        raise AssertionError(f"{path}: not a PNG")
    chunks = []
    index = 8
    while index + 8 <= len(data):
        length = int.from_bytes(data[index:index + 4], "big")
        chunk_type = data[index + 4:index + 8].decode("latin1")
        chunks.append(chunk_type)
        index += 8 + length + 4
    return chunks


def png_color_type(path: Path) -> int:
    data = path.read_bytes()
    if data[:8] != b"\x89PNG\r\n\x1a\n":
        raise AssertionError(f"{path}: not a PNG")
    return data[25]


def image_info(path: Path) -> tuple[int, int, str, bool, int]:
    output = subprocess.check_output(
        [
            "identify",
            "-format",
            "%w|%h|%[channels]|%[opaque]|%[max]",
            str(path),
        ],
        text=True,
    )
    width, height, channels, opaque, maximum = output.split("|")
    return int(width), int(height), channels, opaque == "True", int(float(maximum))


def source_has_alpha(path: Path) -> bool:
    color_type = png_color_type(path)
    return color_type in {4, 6} or "tRNS" in png_chunks(path)


def source_for(delivered: Path, dosage_dir: Path, drug_dir: Path) -> tuple[Path, str]:
    stem = delivered.stem
    for size in EXPECTED_SIZES:
        suffix = f"-{size}"
        if stem.endswith(suffix):
            image_id = stem[:-len(suffix)]
            if image_id.startswith("df-"):
                return dosage_dir / f"{image_id[3:]}.png", size
            if image_id.startswith("drug-"):
                return drug_dir / f"{image_id[5:]}.png", size
    raise AssertionError(f"{delivered}: unexpected delivered file name")


def verify_one(delivered: Path, dosage_dir: Path, drug_dir: Path) -> None:
    source, size = source_for(delivered, dosage_dir, drug_dir)
    if not source.exists():
        raise AssertionError(f"{delivered}: source resource not found: {source}")

    png_chunks(delivered)
    width, height, channels, opaque, maximum = image_info(delivered)
    expected_width, expected_height = EXPECTED_SIZES[size]
    if (width, height) != (expected_width, expected_height):
        raise AssertionError(f"{delivered}: got {(width, height)}, want {(expected_width, expected_height)}")

    if size == "Original":
        if delivered.read_bytes() != source.read_bytes():
            raise AssertionError(f"{delivered}: Original is not byte-identical to {source}")
        return

    if maximum == 0:
        raise AssertionError(f"{delivered}: resized image is all black")
    if source_has_alpha(source) and "a" not in channels.lower() and opaque:
        raise AssertionError(f"{delivered}: alpha channel lost")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("delivered_dir", type=Path)
    parser.add_argument("dosage_dir", type=Path)
    parser.add_argument("drug_dir", type=Path)
    args = parser.parse_args()

    delivered = sorted(args.delivered_dir.glob("*.png"))
    if len(delivered) != 45:
        raise AssertionError(f"{args.delivered_dir}: got {len(delivered)} PNG files, want 45")

    for image in delivered:
        verify_one(image, args.dosage_dir, args.drug_dir)

    print(f"verified {len(delivered)} delivered images")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except AssertionError as error:
        print(error, file=sys.stderr)
        raise SystemExit(1)
