#!/usr/bin/env python3
"""Generate a CHANGELOG.md section for a released version.

The generator reads GitHub Release Notes when available and falls back to git
commit subjects otherwise. It never modifies pom.xml or any version property.
"""

from __future__ import annotations

import argparse
import datetime as _dt
import os
import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable
from urllib.parse import urlparse

RELEASE_HEADINGS = {
    "what's changed",
    "bug fixes",
    "enhancement",
    "enhancements",
    "features",
    "improvements",
}
SKIP_HEADINGS = {"new contributors"}
ITEM_PREFIX = re.compile(r"^\s*[*+-]\s+(.*)$")
BY_IN_URL = re.compile(r"\s+by\s+@[A-Za-z0-9_\[\]-]+\s+in\s+(https?://\S+)\s*$")
PR_URL = re.compile(r"https://github\.com/([^/]+/[^/]+)/(?:pull|issues)/(\d+)")
TAG_HEADING = re.compile(r"^##\s+\[?(\d+(?:\.\d+)+)\]?.*$", re.MULTILINE)
VERSION_RE = re.compile(r"^\d+(?:\.\d+)+$")
TRUNCATION_MARK = "\u2026"

# 1.6.3 is a mislabeled tag whose pom version is 1.6.4 (already documented).
# 2.0.3 and 2.0.4 are covered by the combined "2.0.3-2.0.4" section.
BACKFILL_TAGS = (
    "1.9.2", "1.9.2.1", "1.9.9.2",
    "3.0.4", "3.0.5", "3.0.6", "3.0.7", "3.0.8", "3.0.9",
    "3.1.0", "3.1.1", "3.1.2",
    "4.0.0", "4.0.1",
)


@dataclass(frozen=True)
class ChangeItem:
    """A single change entry and, when known, its source PR URL."""

    text: str
    url: str | None = None
    kind: str = "change"


@dataclass(frozen=True)
class ReleaseData:
    """Normalised release input used to render a section."""

    tag: str
    date: str
    items: tuple[ChangeItem, ...]
    source: str
    repository: str
    previous_tag: str | None


def run(*args: str, cwd: Path | None = None, check: bool = True) -> str:
    """Run a subprocess and return decoded stdout."""
    result = subprocess.run(
        args,
        cwd=cwd,
        check=check,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
        encoding="utf-8",
        errors="replace",
    )
    return result.stdout.strip()


def gh(args: list[str], cwd: Path | None = None) -> str | None:
    """Call gh, returning None when the resource does not exist."""
    result = subprocess.run(
        ["gh", *args],
        cwd=cwd,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
        encoding="utf-8",
        errors="replace",
    )
    if result.returncode != 0:
        return None
    return result.stdout.strip()


def tag_date(tag: str, repo_root: Path) -> str:
    """Read the tagger/commit date as YYYY-MM-DD."""
    raw = run(
        "git",
        "for-each-ref",
        "--format=%(creatordate:short)",
        f"refs/tags/{tag}",
        cwd=repo_root,
    )
    return raw or _dt.date.today().isoformat()


def version_key(tag: str) -> tuple[int, ...]:
    """Convert a numeric tag into a comparable tuple."""
    return tuple(int(part) for part in tag.split("."))


def previous_tag(tag: str, repo_root: Path) -> str | None:
    """Find the nearest preceding version tag by semantic version order."""
    tags = [
        name
        for name in run("git", "tag", "--merged", "HEAD", cwd=repo_root).splitlines()
        if VERSION_RE.match(name)
    ]
    current = version_key(tag)
    older = [name for name in tags if version_key(name) < current]
    if not older:
        return None
    return max(older, key=version_key)


def repo_for_url(url: str) -> str | None:
    """Extract OWNER/REPO from a GitHub pull/issue URL."""
    match = PR_URL.search(url)
    if not match:
        return None
    return match.group(1)


def complete_truncated_title(prefix: str, url: str | None, repo_root: Path) -> str:
    """Restore a title that GitHub truncated in the Release Notes.

    GitHub shortens long pull request titles with an ellipsis. When the commit
    is available locally its subject holds the full title, so prefer it over
    publishing a broken sentence.
    """
    if not prefix.endswith(TRUNCATION_MARK) or not url:
        return prefix
    match = re.search(r"/(?:pull|issues)/(\d+)$", url)
    if not match:
        return prefix
    number = match.group(1)
    stem = prefix[: -len(TRUNCATION_MARK)].rstrip()

    # A pull request title usually comes from its merge commit's subject, and
    # the full text lives on the merged commit rather than on the merge itself.
    candidates: list[str] = []
    merge = subprocess.run(
        ["git", "log", "--all", "--format=%H", "--grep", f"Merge pull request #{number}"],
        cwd=repo_root,
        stdout=subprocess.PIPE,
        stderr=subprocess.DEVNULL,
        text=True,
        encoding="utf-8",
        errors="replace",
    )
    for sha in merge.stdout.splitlines()[:3]:
        subject = run("git", "log", "-1", "--format=%s", sha, cwd=repo_root)
        candidates.append(subject)
        parents = run("git", "log", "-1", "--format=%P", sha, cwd=repo_root).split()
        if len(parents) > 1:
            candidates.append(run("git", "log", "-1", "--format=%s", parents[1], cwd=repo_root))

    for subject in candidates:
        if subject.lower().startswith(stem.lower()) and len(subject) > len(stem):
            return subject
    return stem


def clean_text(text: str) -> str:
    """Remove trailing author attribution, keeping the truncation mark.

    The mark is kept so the caller can restore the full title from git before
    it is finally stripped.
    """
    text = BY_IN_URL.sub("", text).strip()
    text = re.sub(r"\s+by\s+@[A-Za-z0-9_\[\]-]+\s*$", "", text).strip()
    return re.sub(r"\s{2,}", " ", text)


def parse_release_body(body: str) -> tuple[list[ChangeItem], str | None]:
    """Parse both GitHub-generated and hand-written Release Notes.

    Returns the change items and the canonical full-changelog comparison URL,
    which is intentionally recreated by the caller rather than copied verbatim.
    Plain-text release bodies without list markers are kept as a single note.
    """
    items: list[ChangeItem] = []
    comparison: str | None = None
    in_released_section = False
    in_new_contributors = False
    current: ChangeItem | None = None
    prose: list[str] = []
    saw_heading = False

    def flush() -> None:
        nonlocal current
        if current is not None:
            items.append(current)
            current = None

    for raw_line in body.replace("\r\n", "\n").splitlines():
        line = raw_line.rstrip()
        heading = line.lstrip("#").strip().lower() if line.startswith("#") else None
        if heading is not None:
            saw_heading = True
            flush()
            if heading in SKIP_HEADINGS:
                in_released_section = False
                in_new_contributors = True
                continue
            in_new_contributors = False
            in_released_section = heading in RELEASE_HEADINGS
            continue

        if line.startswith("**Full Changelog**"):
            match = re.search(r"https?://\S+", line)
            comparison = match.group(0) if match else None
            flush()
            continue

        if in_new_contributors:
            continue

        item_match = ITEM_PREFIX.match(line)
        if item_match:
            if current is not None:
                items.append(current)
            content = item_match.group(1).strip()
            url_match = BY_IN_URL.search(content)
            url = url_match.group(1) if url_match else None
            text = clean_text(content)
            if in_released_section or not heading:
                current = ChangeItem(text=text, url=url, kind="change")
            else:
                current = None
            continue

        if current is not None and line.strip() and not line.startswith((" ", "\t")):
            items.append(current)
            current = None
            continue

        if not saw_heading and line.strip():
            prose.append(line.strip())

    if current is not None:
        items.append(current)

    if not items and prose:
        items.append(ChangeItem(text=clean_text(" ".join(prose)), kind="note"))

    return items, comparison


def parse_git_history(previous: str, tag: str, repo_root: Path) -> list[ChangeItem]:
    """Build change items from commit subjects when no Release Notes exist."""
    log = run(
        "git",
        "log",
        "--no-merges",
        "--format=%s",
        f"{previous}..{tag}",
        cwd=repo_root,
    )
    items: list[ChangeItem] = []
    for subject in log.splitlines():
        clean = clean_text(subject)
        if not clean:
            continue
        lowered = clean.lower()
        if lowered.startswith(("merge pull request", "release ", "update changelog")):
            continue
        items.append(ChangeItem(text=clean, kind="generated"))
    return items


def fetch_release(repo: str, tag: str) -> tuple[str, str | None] | None:
    """Fetch a release body and its published date via gh."""
    payload = gh(
        [
            "api",
            f"repos/{repo}/releases/tags/{tag}",
            "--jq",
            '.body + "\\u001f" + .published_at',
        ]
    )
    if not payload or "\x1f" not in payload:
        return None
    body, published = payload.split("\x1f", 1)
    return body, published[:10]


def build_release_data(
    tag: str,
    repo: str,
    repo_root: Path,
    previous: str | None,
    release_body: str | None,
    published: str | None,
) -> ReleaseData | None:
    """Normalise a release into a renderable structure."""
    if previous is None:
        previous = previous_tag(tag, repo_root)
    date = published or tag_date(tag, repo_root)

    if release_body:
        items, _comparison = parse_release_body(release_body)
        if items:
            owners = {
                owner
                for item in items
                if item.url and (owner := repo_for_url(item.url)) is not None
            }
            owner = sorted(owners)[0] if len(owners) == 1 else repo
            completed = tuple(
                ChangeItem(
                    text=complete_truncated_title(item.text, item.url, repo_root),
                    url=item.url,
                    kind=item.kind,
                )
                for item in items
            )
            return ReleaseData(
                tag=tag,
                date=date,
                items=completed,
                source="release",
                repository=owner,
                previous_tag=previous,
            )

    if previous is None:
        return None
    items = parse_git_history(previous, tag, repo_root)
    if not items:
        return None
    return ReleaseData(
        tag=tag,
        date=date,
        items=tuple(items),
        source="git",
        repository=repo,
        previous_tag=previous,
    )


GROUP_ORDER = ("Breaking changes", "Added", "Changed", "Fixed", "Other")
BREAKING_MARKERS = ("破坏性变更", "BREAKING", "Breaking change")


def classify_change(text: str) -> str:
    """Assign a change entry to a Keep a Changelog-style group."""
    if any(marker in text for marker in BREAKING_MARKERS):
        return "Breaking changes"
    lowered = text.lower()
    if re.match(r"^(feat|feature)(\([^)]*\))?\s*:", lowered):
        return "Added"
    if re.match(r"^(fix|bugfix)(\([^)]*\))?\s*:", lowered):
        return "Fixed"
    if re.match(r"^(perf|refactor|revert|style|docs|chore|build|ci|test)(\([^)]*\))?\s*:", lowered):
        return "Changed"
    if re.match(r"^(update|optimize|improve)", text, re.I):
        return "Changed"
    return "Other"


def group_id_for(tag: str) -> str:
    """Return the Maven groupId actually used by a released version."""
    key = version_key(tag)
    if (3, 0, 0) <= key < (4, 0, 0):
        return "com.ly.smart-doc"
    return "com.github.shalousun"


def repository_for_version(tag: str) -> str:
    """Return the repository that owns a released tag."""
    return (
        "smart-doc-group/smart-doc"
        if version_key(tag) >= (4, 0, 0)
        else "TongchengOpenSource/smart-doc"
    )


def coordinate_migration_note(tag: str) -> str | None:
    """Describe a Maven coordinate migration when the tag introduced one."""
    if tag == "3.0.0":
        return (
            "**Breaking change**: Maven coordinates changed from "
            "`com.github.shalousun:smart-doc` to `com.ly.smart-doc:smart-doc`; "
            "update the dependency groupId when upgrading"
        )
    if tag == "4.0.0":
        return (
            "**Breaking change**: Maven coordinates changed from "
            "`com.ly.smart-doc:smart-doc` back to `com.github.shalousun:smart-doc`; "
            "update the dependency groupId when upgrading"
        )
    return None


def pr_number(url: str) -> str | None:
    """Extract the pull request or issue number from a GitHub URL."""
    match = re.search(r"/(?:pull|issues)/(\d+)$", url)
    return match.group(1) if match else None


def version_heading(release: ReleaseData) -> str:
    """Render the H2 heading with a comparison link and the release date."""
    if release.previous_tag:
        same_repository = repository_for_version(release.previous_tag) == release.repository
        if same_repository:
            url = (
                f"https://github.com/{release.repository}/compare/"
                f"{release.previous_tag}...{release.tag}"
            )
        else:
            url = f"https://github.com/{release.repository}/commits/{release.tag}"
    else:
        url = f"https://github.com/{release.repository}/commits/{release.tag}"
    return f"## [{release.tag}]({url}) - {release.date}"


def render_section(release: ReleaseData) -> str:
    """Render one version section in the Keep a Changelog house style."""
    group = group_id_for(release.tag)
    lines = [version_heading(release), ""]
    lines.append(f"Maven coordinates: `{group}:smart-doc:{release.tag}`")
    lines.append("")

    entries: list[str] = []
    migration = coordinate_migration_note(release.tag)
    if migration:
        entries.append(migration)
    for item in release.items:
        url = item.url or ""
        number = pr_number(url) if url else None
        if number:
            entries.append(f"{item.text} ([#{number}]({url}))")
        elif url:
            entries.append(f"{item.text} ([link]({url}))")
        else:
            entries.append(item.text)

    grouped: dict[str, list[str]] = {name: [] for name in GROUP_ORDER}
    for entry in entries:
        grouped[classify_change(entry)].append(entry)

    for name in GROUP_ORDER:
        if not grouped[name]:
            continue
        lines.append(f"### {name}")
        lines.append("")
        lines.extend(f"- {entry}" for entry in grouped[name])
        lines.append("")

    return "\n".join(lines).rstrip() + "\n"


def find_insertion_point(text: str) -> int:
    """Return the offset after the document title, before the newest version."""
    match = TAG_HEADING.search(text)
    if match:
        return match.start()
    title_match = re.search(r"^#\s+[^\n]+\n+", text, re.MULTILINE)
    if title_match:
        return title_match.end()
    fallback = re.search(r"^##\s+[^\n]+\n+", text, re.MULTILINE)
    return fallback.end() if fallback else len(text)


def has_section(text: str, tag: str) -> bool:
    """Check whether a version section exists in any of the historical styles."""
    return (
        re.search(rf"^##\s+\[{re.escape(tag)}\]", text, re.MULTILINE) is not None
        or re.search(rf"^##\s+{re.escape(tag)}(\s|$)", text, re.MULTILINE) is not None
        or re.search(rf"^#{{3,4}}\s+{re.escape(tag)}\s*$", text, re.MULTILINE) is not None
        or re.search(rf"^#{{3,4}}\s+版本号：\s*{re.escape(tag)}\s*$", text, re.MULTILINE) is not None
    )


ANY_VERSION_HEADING = re.compile(r"^##\s+.+$", re.MULTILINE)


def heading_version(heading: str) -> tuple[int, ...]:
    """Parse the version key from a version heading."""
    match = re.search(r"(\d+(?:\.\d+)+)", heading)
    return tuple(int(part) for part in match.group(1).split(".")) if match else (0,)


def insert_sections(text: str, sections: Iterable[str]) -> str:
    """Insert new sections and keep every version block in descending order.

    Existing section bodies are preserved byte-for-byte; only their order and
    the newly generated blocks change.
    """
    new_blocks = [section.strip("\n") for section in sections]
    if not new_blocks:
        return text

    headings = list(ANY_VERSION_HEADING.finditer(text))
    if not headings:
        point = find_insertion_point(text)
        return f"{text[:point].rstrip(chr(10))}\n\n{'\n\n'.join(new_blocks)}\n{text[point:]}"

    prefix = text[: headings[0].start()].rstrip("\n")
    blocks: list[tuple[tuple[int, ...], str]] = []
    for index, heading in enumerate(headings):
        end = headings[index + 1].start() if index + 1 < len(headings) else len(text)
        block = text[heading.start() : end].strip("\n")
        blocks.append((heading_version(heading.group(0)), block))
    for block in new_blocks:
        blocks.append((heading_version(block.splitlines()[0]), block))

    blocks.sort(key=lambda entry: entry[0], reverse=True)
    body = "\n\n".join(block for _key, block in blocks)
    return f"{prefix}\n\n{body}\n"


def parse_args(argv: list[str] | None = None) -> argparse.Namespace:
    """Parse command-line arguments."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--repo", default=os.environ.get("GITHUB_REPOSITORY"))
    parser.add_argument("--tag", default=os.environ.get("RELEASE_TAG"))
    parser.add_argument("--previous-tag", default=os.environ.get("PREVIOUS_TAG"))
    parser.add_argument("--changelog", default="CHANGELOG.md")
    parser.add_argument(
        "--release-file",
        action="append",
        default=[],
        help="Read Release Notes from a local file instead of calling gh",
    )
    parser.add_argument("--check", action="store_true", help="Validate without writing")
    parser.add_argument("--dry-run", action="store_true", help="Print the section")
    parser.add_argument("--backfill", action="store_true", help="Generate all known gaps")
    return parser.parse_args(argv)


def main(argv: list[str] | None = None) -> int:
    """Entry point for CI and local replay."""
    args = parse_args(argv)
    repo_root = Path(__file__).resolve().parents[2]
    changelog_path = Path(args.changelog)
    if not changelog_path.is_absolute():
        changelog_path = repo_root / changelog_path
    if not changelog_path.exists():
        print(f"error: CHANGELOG not found: {changelog_path}", file=sys.stderr)
        return 2

    text = changelog_path.read_text(encoding="utf-8")
    tags = [args.tag] if not args.backfill else list(BACKFILL_TAGS)
    if not args.backfill and not args.tag:
        print("error: --tag is required", file=sys.stderr)
        return 2

    release_files = {Path(p).stem: Path(p).read_text(encoding="utf-8") for p in args.release_file}
    sections: list[str] = []
    for tag in tags:
        if has_section(text, tag):
            continue
        repo = args.repo or (
            "smart-doc-group/smart-doc"
            if tag.startswith("4.")
            else "TongchengOpenSource/smart-doc"
        )
        if tag in release_files:
            fetched: tuple[str, str | None] | None = (release_files[tag], None)
        else:
            fetched = fetch_release(repo, tag)
        if fetched is None:
            print(f"warning: no Release Notes for {tag}; using git history", file=sys.stderr)
        data = build_release_data(
            tag=tag,
            repo=repo,
            repo_root=repo_root,
            previous=args.previous_tag if not args.backfill else None,
            release_body=fetched[0] if fetched else None,
            published=fetched[1] if fetched else None,
        )
        if data is None:
            print(f"error: cannot generate section for {tag}", file=sys.stderr)
            return 1
        sections.append(render_section(data))

    if args.dry_run:
        print("\n".join(sections))
        return 0
    if args.check:
        print(f"checked {len(tags)} tags; {len(sections)} section(s) to add")
        return 0

    if sections:
        changelog_path.write_text(insert_sections(text, sections), encoding="utf-8")
    print(f"added {len(sections)} section(s) to {changelog_path}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
