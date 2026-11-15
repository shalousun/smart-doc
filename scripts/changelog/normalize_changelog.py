#!/usr/bin/env python3
"""Normalise the existing CHANGELOG.md to the Keep a Changelog house style.

Structural changes only: heading levels, version headings with dates and compare
links, H3 change-type groups, unordered lists, and issue/PR links labelled with
the number instead of a repeated word. Entry text and every link target are
preserved byte-for-byte.
"""

from __future__ import annotations

import argparse
import os
import re
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

import generate_changelog as gc  # noqa: E402

TITLE = "# smart-doc Changelog"
PREAMBLE = (
    "All notable changes to this project are documented in this file. The format is based on\n"
    "[Keep a Changelog](https://keepachangelog.com/en/1.1.0/). Versions are listed newest first;\n"
    "releases up to 3.1.2 were published from the archived\n"
    "[TongchengOpenSource/smart-doc](https://github.com/TongchengOpenSource/smart-doc) repository,\n"
    "and 4.0.0 onward from [smart-doc-group/smart-doc](https://github.com/smart-doc-group/smart-doc)."
)

VERSION_HEADING = re.compile(
    r"^#{3,4}\s*(?:https?://\S*/issues/\s*)?(?:版本号：\s*)?([0-9][^\s]*)\s*$",
    re.MULTILINE,
)
VERSION_RANGE = re.compile(r"^(\d+(?:\.\d+)+)\s*-\s*(\d+(?:\.\d+)+)$")
DATE_LINE = re.compile(r"^\s*[-*]\s+更新日期[:：]\s*(\S+)\s*$")
RELEASED_LINE = re.compile(r"^\s*[-*]\s+Released:\s*(\S+)\s*$")
NUMBERED_ITEM = re.compile(r"^(\s*)\d+\.\s+(.*)$")
BULLET_ITEM = re.compile(r"^(\s*)[-*+]\s+(.*)$")
PR_LINK = re.compile(r"\(\[PR\]\((https://github\.com/[^)]+/(?:pull|issues)/\d+)\)\)")
ANY_LINK = re.compile(r"\[([^\]]+)\]\((https?://[^)]+)\)")
GROUP_ORDER = ("Breaking changes", "Added", "Changed", "Fixed", "Other")
BREAKING_MARKERS = ("破坏性变更", "BREAKING", "Breaking change")
SKIP_META_PREFIXES = (
    "更新日期", "更新内容", "Released:", "Maven coordinates:",
    "Source:", "Changes:", "Full changelog:", "Previous release:",
)


def classify(text: str) -> str:
    """Assign a change to a Keep a Changelog-style group."""
    if any(marker in text for marker in BREAKING_MARKERS):
        return "Breaking changes"
    lowered = text.lower()
    if re.match(r"^(feat|feature)(\([^)]*\))?\s*:", lowered) or "新功能" in text or "新增" in text:
        return "Added"
    if re.match(r"^(fix|bugfix)(\([^)]*\))?\s*:", lowered) or "修复" in text or "修正" in text:
        return "Fixed"
    if re.match(r"^(perf|refactor|revert|style|docs|chore|build|ci|test)(\([^)]*\))?\s*:", lowered):
        return "Changed"
    if re.match(r"^(update|optimize|improve|调整|优化|改进|重构|升级|支持|增加|添加)", text, re.I):
        return "Changed"
    return "Other"


def fix_malformed_links(text: str) -> str:
    """Repair links whose closing parenthesis was written as a full-width one.

    Several historical entries use `)` instead of `)`, which stops Markdown
    from rendering the link at all.
    """
    return re.sub(r"\((https://github\.com/[^)\s]+)\uFF09", r"(\1)", text)


def relabel_pr_links(text: str) -> str:
    """Replace ([PR](url)) with ([#number](url)), preserving the URL."""
    def repl(match: re.Match[str]) -> str:
        url = match.group(1)
        number = re.search(r"/(?:pull|issues)/(\d+)$", url)
        return f"([#{number.group(1)}]({url}))" if number else match.group(0)

    return PR_LINK.sub(repl, text)


def iso_date(raw: str) -> str:
    """Normalise YYYY-M-D to YYYY-MM-DD; keep non-numeric markers verbatim."""
    match = re.match(r"^(\d{4})-(\d{1,2})-(\d{1,2})$", raw)
    if not match:
        return raw
    year, month, day = match.groups()
    return f"{year}-{int(month):02d}-{int(day):02d}"


def parse_blocks(text: str) -> tuple[str, list[dict[str, object]]]:
    """Split the document into version blocks, retaining entry text."""
    headings = list(VERSION_HEADING.finditer(text))
    if not headings:
        raise ValueError("no version headings found")

    preamble_body = text[: headings[0].start()].strip()
    blocks: list[dict[str, object]] = []
    for index, heading in enumerate(headings):
        end = headings[index + 1].start() if index + 1 < len(headings) else len(text)
        body = text[heading.start() : end]
        raw_heading = heading.group(1).strip()
        version = gc.version_key(raw_heading) if gc.VERSION_RE.match(raw_heading) else None
        if version is None:
            range_match = VERSION_RANGE.match(raw_heading)
            if range_match:
                version = gc.version_key(range_match.group(1))
        version_text = raw_heading
        lines = body.splitlines()[1:]

        date: str | None = None
        entries: list[str] = []
        for line in lines:
            stripped = line.strip()
            if not stripped:
                continue
            date_match = DATE_LINE.match(line) or RELEASED_LINE.match(line)
            if date_match:
                date = iso_date(date_match.group(1))
                continue
            content = re.sub(r"^[-*+]\s+", "", stripped)
            if any(content.startswith(prefix) for prefix in SKIP_META_PREFIXES):
                continue
            item_match = NUMBERED_ITEM.match(line) or BULLET_ITEM.match(line)
            if item_match:
                content = relabel_pr_links(item_match.group(2).strip())
                content = fix_malformed_links(content)
                if content:
                    entries.append(content)
                continue
            # Continuation line: append to the previous entry rather than drop it.
            if entries:
                entries[-1] += " " + relabel_pr_links(fix_malformed_links(stripped))
            else:
                entries.append(relabel_pr_links(fix_malformed_links(stripped)))

        blocks.append(
            {
                "heading": raw_heading,
                "version": version_text,
                "version_key": version,
                "date": date,
                "entries": entries,
            }
        )
    return preamble_body, blocks


def repository_for(version: str) -> str:
    """Return the repository that owns a version tag."""
    return gc.repository_for_version(version)


def primary_version(version: str) -> str:
    """Return the first version of a possibly combined heading such as 2.0.3-2.0.4."""
    range_match = VERSION_RANGE.match(version)
    return range_match.group(1) if range_match else version


def heading_for(block: dict[str, object], known_tags: set[str]) -> str:
    """Render the H2 heading, adding a compare link when both tags exist."""
    version = str(block["version"])
    date = block["date"]
    version_key = block["version_key"]
    primary = primary_version(version)
    if primary not in known_tags or version_key is None:
        suffix = f" - {date}" if date and version_key is not None else ""
        return f"## {version}{suffix}"

    repo = repository_for(primary)
    previous = gc.previous_tag(primary, Path(__file__).resolve().parents[2])
    if previous and previous in known_tags and gc.repository_for_version(previous) == repo:
        url = f"https://github.com/{repo}/compare/{previous}...{primary}"
        label = f"[{version}]({url})"
    else:
        url = f"https://github.com/{repo}/commits/{primary}"
        label = f"[{version}]({url})"

    if date == "已废弃":
        return f"## {label} (deprecated)"
    if date:
        return f"## {label} - {date}"
    return f"## {label}"


def render_block(block: dict[str, object], known_tags: set[str]) -> str:
    """Render one version block in the normalised style."""
    lines = [heading_for(block, known_tags), ""]
    version = str(block["version"])
    version_key = block["version_key"]
    if version_key is not None:
        primary = primary_version(version)
        lines.append(f"Maven coordinates: `{gc.group_id_for(primary)}:smart-doc:{primary}`")
        lines.append("")

    groups: dict[str, list[str]] = {name: [] for name in GROUP_ORDER}
    for entry in block["entries"]:  # type: ignore[union-attr]
        groups[classify(str(entry))].append(str(entry))

    for name in GROUP_ORDER:
        if not groups[name]:
            continue
        lines.append(f"### {name}")
        lines.append("")
        lines.extend(f"- {entry}" for entry in groups[name])
        lines.append("")

    return "\n".join(lines).rstrip() + "\n"


# A few releases were recorded twice: once by hand and once from the Release
# Notes. These pairs describe the same change in two languages, so only the
# hand-written entry is kept.
TRANSLATION_PAIRS = (
    (
        "Optimize the curl use case and add request header settings in the use case.",
        "优化curl用例，增加请求头。",
    ),
    (
        "Fix jsr 303 verification parsing error.",
        "修改前面版本修改引发的普通jsr 303验证解析错误问题。",
    ),
    (
        "Modify smart-doc to read beetl configuration files to avoid conflicts "
        "with beetl configuration in user applications.",
        "修改smart-doc的beetl配置避免和用户的业务中beetl配置冲突。",
    ),
    (
        "Added configuration for ignoring request parameter objects .",
        "新增忽略请求参数对象的配置gitee #I1RBJO。",
    ),
    (
        "ApiDataBuilder adds an interface for obtaining parameter data in tree format,#40 .",
        "新增ApiDataBuilder中获取树形格式参数数据的接口"
        "[#40](https://github.com/smart-doc-group/smart-doc/issues/40)",
    ),
    (
        "Added support for Open Api 3.0.",
        "新增对Open Api 3.0的支持。",
    ),
)


def merge_duplicates(blocks: list[dict[str, object]]) -> list[dict[str, object]]:
    """Merge blocks that document the same version.

    A few releases were recorded twice, once by hand and once from the Release
    Notes. The entries are combined, the version keeps its richer date, and an
    entry that merely repeats another in a second language is dropped.
    """
    merged: list[dict[str, object]] = []
    by_version: dict[str, dict[str, object]] = {}
    for block in blocks:
        version = str(block["version"])
        existing = by_version.get(version)
        if existing is None:
            by_version[version] = block
            merged.append(block)
            continue
        entries = list(existing["entries"])  # type: ignore[arg-type]
        for entry in block["entries"]:  # type: ignore[union-attr]
            if entry not in entries:
                entries.append(entry)
        existing["entries"] = entries
        if not existing.get("date") and block.get("date"):
            existing["date"] = block["date"]

    for block in merged:
        entries = list(block["entries"])  # type: ignore[arg-type]
        redundant = {english for english, chinese in TRANSLATION_PAIRS}
        block["entries"] = [entry for entry in entries if entry not in redundant]
    return merged


def normalise(text: str, known_tags: set[str]) -> str:
    """Produce the normalised document."""
    _preamble, blocks = parse_blocks(text)
    blocks = merge_duplicates(blocks)
    blocks.sort(key=lambda b: b["version_key"] if b["version_key"] else (0,), reverse=True)
    rendered = [TITLE, "", PREAMBLE, ""]
    rendered.extend(render_block(block, known_tags) for block in blocks)
    return "\n".join(rendered).rstrip() + "\n"


def main(argv: list[str] | None = None) -> int:
    """Entry point for the one-off migration."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--changelog", default="CHANGELOG.md")
    parser.add_argument("--tags-file", help="File with one known remote tag per line")
    parser.add_argument("--repo", default=os.environ.get("GITHUB_REPOSITORY", "smart-doc-group/smart-doc"))
    parser.add_argument("--check", action="store_true", help="Validate structure without writing")
    parser.add_argument("--dry-run", action="store_true", help="Print the normalised document")
    args = parser.parse_args(argv)

    root = Path(__file__).resolve().parents[2]
    path = Path(args.changelog)
    if not path.is_absolute():
        path = root / path
    text = path.read_text(encoding="utf-8")

    if args.tags_file:
        known_tags = set(Path(args.tags_file).read_text(encoding="utf-8").split())
    else:
        archived = gc.gh(["api", "repos/TongchengOpenSource/smart-doc/tags", "--paginate", "--jq", ".[].name"])
        current = gc.gh(["api", f"repos/{args.repo}/tags", "--paginate", "--jq", ".[].name"])
        known_tags = set((archived or "").split()) | set((current or "").split())
    known_tags |= {"4.0.0", "4.0.1"}

    result = normalise(text, known_tags)
    if args.dry_run:
        print(result)
        return 0
    if args.check:
        _preamble, blocks = parse_blocks(text)
        print(f"checked {len(blocks)} version blocks; {len(known_tags)} known tags")
        return 0
    path.write_text(result, encoding="utf-8")
    print(f"normalised {path}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
