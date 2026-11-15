#!/usr/bin/env python3
"""Repair historical GitHub links in CHANGELOG.md.

The project moved repositories twice. Links written against the old organisation
name now 404, so they are repointed at the repository that actually owns the
number. Issues that were recorded as pull requests are corrected, and anything
that cannot be recovered falls back to a commit link, the way Vue records its
changelog entries.
"""

from __future__ import annotations

import re
import subprocess
from dataclasses import dataclass
from pathlib import Path

GITHUB_LINK = re.compile(
    r"\[(?P<label>[^\]]+)\]\(https://github\.com/"
    r"(?P<repo>[^/]+/[^/]+)/(?P<kind>pull|issues|issue|commit|commits|compare)/(?P<rest>[^)]+)\)"
)
ARCHIVED_REPO = "TongchengOpenSource/smart-doc"
CURRENT_REPO = "smart-doc-group/smart-doc"
PLUGIN_REPOS = ("smart-doc-group/smart-doc-maven-plugin", "smart-doc-group/smart-doc-gradle-plugin")
ARCHIVED_PLUGIN_REPOS = (
    "TongchengOpenSource/smart-doc-maven-plugin",
    "TongchengOpenSource/smart-doc-gradle-plugin",
)
# Plugin repositories keep their own numbering, so a plugin number is never
# resolved against a core repository and vice versa.
PLUGIN_KIND = {
    "smart-doc-group/smart-doc-maven-plugin": "maven",
    "smart-doc-group/smart-doc-gradle-plugin": "gradle",
    "TongchengOpenSource/smart-doc-maven-plugin": "maven",
    "TongchengOpenSource/smart-doc-gradle-plugin": "gradle",
}


@dataclass(frozen=True)
class RepoNumbers:
    """Known pull request and issue numbers per repository."""

    pulls: dict[str, set[str]]
    issues: dict[str, set[str]]

    def has_pull(self, repo: str, number: str) -> bool:
        return number in self.pulls.get(repo, set())

    def has_issue(self, repo: str, number: str) -> bool:
        return number in self.issues.get(repo, set())


def load_numbers(repo_root: Path) -> RepoNumbers:
    """Read the cached number lists produced by the tooling."""
    pulls: dict[str, set[str]] = {}
    issues: dict[str, set[str]] = {}
    mapping = {
        "tc-prs.txt": ARCHIVED_REPO,
        "tc-issues.txt": ARCHIVED_REPO,
        "up-prs.txt": CURRENT_REPO,
        "up-issues.txt": CURRENT_REPO,
        "plugin-prs.txt": "smart-doc-group/smart-doc-maven-plugin",
        "plugin-issues.txt": "smart-doc-group/smart-doc-maven-plugin",
        "gradle-prs.txt": "smart-doc-group/smart-doc-gradle-plugin",
        "gradle-issues.txt": "smart-doc-group/smart-doc-gradle-plugin",
        "tc-plugin-prs.txt": "TongchengOpenSource/smart-doc-maven-plugin",
        "tc-plugin-issues.txt": "TongchengOpenSource/smart-doc-maven-plugin",
        "tc-gradle-prs.txt": "TongchengOpenSource/smart-doc-gradle-plugin",
        "tc-gradle-issues.txt": "TongchengOpenSource/smart-doc-gradle-plugin",
    }
    for name, repo in mapping.items():
        path = repo_root / name
        if path.exists():
            values = set(path.read_text(encoding="utf-8").split())
        else:
            values = set()
        if "prs" in name:
            pulls[repo] = values
        else:
            issues[repo] = values
    return RepoNumbers(pulls=pulls, issues=issues)


def owning_repository(number: str, numbers: RepoNumbers, source: str) -> str | None:
    """Find the repository that owns a pull request or issue number.

    Core links are resolved against the two core repositories. Plugin links are
    resolved only against the matching plugin repository family, so a plugin
    number is never repointed at a core pull request that shares the number.
    The archived Tongcheng plugin repositories are searched too, because the
    plugins moved from that organisation to the community one.
    """
    kind = PLUGIN_KIND.get(source)
    if kind is not None:
        if kind == "maven":
            candidates = (
                "smart-doc-group/smart-doc-maven-plugin",
                "TongchengOpenSource/smart-doc-maven-plugin",
            )
        else:
            candidates = (
                "smart-doc-group/smart-doc-gradle-plugin",
                "TongchengOpenSource/smart-doc-gradle-plugin",
            )
        for candidate in candidates:
            if numbers.has_pull(candidate, number):
                return candidate
        for candidate in candidates:
            if numbers.has_issue(candidate, number):
                return candidate
        return None

    if numbers.has_pull(ARCHIVED_REPO, number):
        return ARCHIVED_REPO
    if numbers.has_issue(ARCHIVED_REPO, number):
        return ARCHIVED_REPO
    if numbers.has_pull(CURRENT_REPO, number):
        return CURRENT_REPO
    if numbers.has_issue(CURRENT_REPO, number):
        return CURRENT_REPO
    return None


def commit_for_number(number: str, repo_root: Path, window: tuple[int, int]) -> tuple[str, str] | None:
    """Find a commit that explicitly references an issue or pull request.

    The commit subject must mention the number itself, so a commit that merely
    shares digits with it is never used as a substitute anchor. Merge commits
    are preferred because they identify the pull request unambiguously.

    ``window`` restricts the search to commits created while the numbering
    scheme was in use, which prevents a later issue that happens to reuse the
    same number from being matched.
    """
    merge_pattern = re.compile(rf"Merge pull request #{re.escape(number)}\b")
    number_pattern = re.compile(rf"(?:^|\s)#\s*{re.escape(number)}\b")
    result = subprocess.run(
        ["git", "log", "--all", "--format=%H%x09%at%x09%s", "--grep", f"#{number}"],
        cwd=repo_root,
        stdout=subprocess.PIPE,
        stderr=subprocess.DEVNULL,
        text=True,
        encoding="utf-8",
        errors="replace",
    )
    fallback: tuple[str, str] | None = None
    for line in result.stdout.splitlines():
        parts = line.split("\t", 2)
        if len(parts) != 3:
            continue
        sha, timestamp, subject = parts
        stamp = int(timestamp) if timestamp.isdigit() else 0
        if not window[0] <= stamp <= window[1]:
            continue
        if merge_pattern.search(subject):
            return sha, subject
        if fallback is None and number_pattern.search(subject):
            fallback = (sha, subject)
    return fallback


def commit_repository(sha: str) -> str:
    """Return the repository that hosts a commit reachable from this checkout.

    The core history is shared with the archived repository, so a commit found
    in this checkout is addressable there.
    """
    return ARCHIVED_REPO


def version_windows(repo_root: Path) -> dict[str, tuple[int, int]]:
    """Map each released version to the timestamp range of its changes.

    The window spans from the previous release to the release itself, so a
    commit outside the window cannot be the change a version entry refers to.
    """
    raw = subprocess.run(
        ["git", "for-each-ref", "--sort=creatordate", "--format=%(refname:short) %(creatordate:unix)", "refs/tags"],
        cwd=repo_root,
        stdout=subprocess.PIPE,
        stderr=subprocess.DEVNULL,
        text=True,
        encoding="utf-8",
        errors="replace",
    ).stdout.splitlines()
    entries: list[tuple[str, int]] = []
    for line in raw:
        parts = line.split()
        if len(parts) != 2 or not parts[1].isdigit():
            continue
        entries.append((parts[0], int(parts[1])))
    windows: dict[str, tuple[int, int]] = {}
    for index, (tag, stamp) in enumerate(entries):
        lower = entries[index - 1][1] if index else 0
        windows[tag] = (lower - 86400 * 120, stamp + 86400 * 120)
    return windows


def issue_label(label: str, number: str) -> str:
    """Return the label text for a link, keyed by the number in the URL.

    A few historical entries show a number that does not match the link they
    point at. The URL identifies the resource, so the label is corrected to
    match it rather than repeating the wrong number.
    """
    if re.fullmatch(r"#?\d+", label.strip()):
        return f"#{number}"
    return label


def repair_link(
    label: str,
    repo: str,
    kind: str,
    rest: str,
    numbers: RepoNumbers,
    repo_root: Path,
    window: tuple[int, int],
) -> tuple[str, str, str]:
    """Return the corrected label, repository and URL for one link."""
    if kind in ("commit", "commits", "compare"):
        return label, repo, f"https://github.com/{repo}/{kind}/{rest}"

    number = rest.split("...")[0]
    if kind in ("issues", "issue"):
        label = issue_label(label, number)
        if numbers.has_issue(repo, number):
            return label, repo, f"https://github.com/{repo}/issues/{number}"
        owner = owning_repository(number, numbers, repo)
        if owner:
            return label, owner, f"https://github.com/{owner}/issues/{number}"
        commit = commit_for_number(number, repo_root, window)
        if commit:
            sha, _subject = commit
            host = commit_repository(sha)
            return f"`#{number}`", host, f"https://github.com/{host}/commit/{sha}"
        return f"`#{number}` (link unavailable)", repo, ""

    # kind == "pull"
    label = issue_label(label, number)
    if numbers.has_pull(repo, number):
        return label, repo, f"https://github.com/{repo}/pull/{number}"
    if numbers.has_issue(repo, number):
        return label, repo, f"https://github.com/{repo}/issues/{number}"
    owner = owning_repository(number, numbers, repo)
    if owner:
        if numbers.has_pull(owner, number):
            return label, owner, f"https://github.com/{owner}/pull/{number}"
        return label, owner, f"https://github.com/{owner}/issues/{number}"
    commit = commit_for_number(number, repo_root, window)
    if commit:
        sha, _subject = commit
        host = commit_repository(sha)
        return f"`#{number}`", host, f"https://github.com/{host}/commit/{sha}"
    # Nothing resolves the number: keep the text but drop the dead link.
    return f"`#{number}` (link unavailable)", repo, ""


def repair_text(
    text: str,
    numbers: RepoNumbers,
    repo_root: Path,
    windows: dict[str, tuple[int, int]],
) -> tuple[str, dict[str, int]]:
    """Repair every GitHub link in a document, reporting what changed.

    Links are repaired per version block so that a commit fallback is only
    allowed inside the time range of the release that mentions it.
    """
    counters = {"reponted": 0, "issue_fixed": 0, "commit_fallback": 0, "dropped": 0}
    heading = re.compile(r"^##\s+\[?(\d+(?:\.\d+)+)\]?.*$", re.MULTILINE)
    matches = list(heading.finditer(text))
    if not matches:
        return text, counters

    def repair_segment(segment: str, window: tuple[int, int]) -> str:
        def repl(match: re.Match[str]) -> str:
            label = match.group("label")
            repo = match.group("repo")
            kind = match.group("kind")
            rest = match.group("rest")
            new_label, _new_repo, new_url = repair_link(
                label, repo, kind, rest, numbers, repo_root, window
            )
            if not new_url:
                counters["dropped"] += 1
                return new_label
            original_url = match.group(0).split("(", 1)[1][:-1]
            if new_url != original_url:
                if kind == "pull" and new_url.endswith(f"/issues/{rest}"):
                    counters["issue_fixed"] += 1
                elif "/commit/" in new_url:
                    counters["commit_fallback"] += 1
                else:
                    counters["reponted"] += 1
            return f"[{new_label}]({new_url})"

        return GITHUB_LINK.sub(repl, segment)

    result: list[str] = [text[: matches[0].start()]]
    for index, match in enumerate(matches):
        end = matches[index + 1].start() if index + 1 < len(matches) else len(text)
        version = match.group(1)
        window = windows.get(version, (0, 4_102_444_800))
        result.append(repair_segment(text[match.start() : end], window))
    return "".join(result), counters


def main() -> int:
    """Entry point for the link repair tool."""
    import argparse

    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--changelog", default="CHANGELOG.md")
    parser.add_argument("--numbers-dir", default=".workbuddy")
    parser.add_argument("--dry-run", action="store_true")
    args = parser.parse_args()

    repo_root = Path(__file__).resolve().parents[2]
    numbers = load_numbers(repo_root / args.numbers_dir)
    path = Path(args.changelog)
    if not path.is_absolute():
        path = repo_root / path
    text = path.read_text(encoding="utf-8")
    windows = version_windows(repo_root)
    result, counters = repair_text(text, numbers, repo_root, windows)
    if args.dry_run:
        print(f"reponted={counters['reponted']} issue_fixed={counters['issue_fixed']} commit_fallback={counters['commit_fallback']}")
        return 0
    path.write_text(result, encoding="utf-8")
    print(f"reponted={counters['reponted']} issue_fixed={counters['issue_fixed']} commit_fallback={counters['commit_fallback']}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
