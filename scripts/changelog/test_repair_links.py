#!/usr/bin/env python3
"""Unit tests for the historical link repair tool."""

from __future__ import annotations

import sys
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

import repair_links as rl  # noqa: E402


def numbers() -> rl.RepoNumbers:
    """Build a number table that mirrors the real repositories."""
    return rl.RepoNumbers(
        pulls={
            rl.ARCHIVED_REPO: {"100", "101"},
            rl.CURRENT_REPO: {"1", "2"},
            "smart-doc-group/smart-doc-maven-plugin": {"5"},
            "smart-doc-group/smart-doc-gradle-plugin": {"2"},
            "TongchengOpenSource/smart-doc-maven-plugin": {"15", "17", "19"},
            "TongchengOpenSource/smart-doc-gradle-plugin": {"15"},
        },
        issues={
            rl.ARCHIVED_REPO: {"100", "101", "300", "573"},
            rl.CURRENT_REPO: {"1", "2"},
            "smart-doc-group/smart-doc-maven-plugin": {"5"},
            "smart-doc-group/smart-doc-gradle-plugin": {"2"},
            "TongchengOpenSource/smart-doc-maven-plugin": {"15", "17", "19"},
            "TongchengOpenSource/smart-doc-gradle-plugin": {"15"},
        },
    )


class OwningRepositoryTest(unittest.TestCase):
    """Verify that a number is resolved against the right repository family."""

    def test_core_number_resolves_to_core(self) -> None:
        table = numbers()
        self.assertEqual(rl.owning_repository("100", table, rl.ARCHIVED_REPO), rl.ARCHIVED_REPO)
        self.assertEqual(rl.owning_repository("2", table, rl.CURRENT_REPO), rl.CURRENT_REPO)

    def test_plugin_number_never_resolves_to_core(self) -> None:
        table = numbers()
        self.assertIsNone(rl.owning_repository("100", table, "smart-doc-group/smart-doc-maven-plugin"))

    def test_plugin_number_resolves_to_archived_plugin(self) -> None:
        table = numbers()
        self.assertEqual(
            rl.owning_repository("17", table, "smart-doc-group/smart-doc-maven-plugin"),
            "TongchengOpenSource/smart-doc-maven-plugin",
        )

    def test_gradle_plugin_does_not_resolve_to_maven_plugin(self) -> None:
        table = numbers()
        self.assertEqual(
            rl.owning_repository("15", table, "smart-doc-group/smart-doc-gradle-plugin"),
            "TongchengOpenSource/smart-doc-gradle-plugin",
        )


class IssueLabelTest(unittest.TestCase):
    """Verify that a label is keyed by the number in the URL."""

    def test_matching_label_is_kept(self) -> None:
        self.assertEqual(rl.issue_label("#19", "19"), "#19")

    def test_mismatched_label_is_corrected(self) -> None:
        self.assertEqual(rl.issue_label("#340", "17"), "#17")

    def test_bare_number_is_corrected(self) -> None:
        self.assertEqual(rl.issue_label("17", "19"), "#19")

    def test_descriptive_label_is_kept(self) -> None:
        self.assertEqual(rl.issue_label("gradle #15", "15"), "gradle #15")
        self.assertEqual(rl.issue_label("docs", "15"), "docs")


class RepairLinkTest(unittest.TestCase):
    """Verify the decision order of the repair tool."""

    def setUp(self) -> None:
        self.table = numbers()
        self.root = Path(__file__).resolve().parents[2]
        self.window = (0, 4_102_444_800)

    def repair(self, label: str, repo: str, kind: str, rest: str) -> tuple[str, str, str]:
        return rl.repair_link(label, repo, kind, rest, self.table, self.root, self.window)

    def test_existing_pull_is_kept(self) -> None:
        label, repo, url = self.repair("#100", rl.ARCHIVED_REPO, "pull", "100")
        self.assertEqual(url, f"https://github.com/{rl.ARCHIVED_REPO}/pull/100")
        self.assertEqual(repo, rl.ARCHIVED_REPO)

    def test_old_organisation_is_repointed(self) -> None:
        label, repo, url = self.repair("#100", rl.CURRENT_REPO, "pull", "100")
        self.assertEqual(repo, rl.ARCHIVED_REPO)
        self.assertEqual(url, f"https://github.com/{rl.ARCHIVED_REPO}/pull/100")

    def test_issue_recorded_as_pull_is_corrected(self) -> None:
        label, repo, url = self.repair("#573", rl.ARCHIVED_REPO, "pull", "573")
        self.assertEqual(url, f"https://github.com/{rl.ARCHIVED_REPO}/issues/573")

    def test_plugin_link_moves_to_archived_plugin(self) -> None:
        label, repo, url = self.repair(
            "#17", "smart-doc-group/smart-doc-maven-plugin", "issues", "17"
        )
        self.assertEqual(repo, "TongchengOpenSource/smart-doc-maven-plugin")
        self.assertEqual(url, "https://github.com/TongchengOpenSource/smart-doc-maven-plugin/issues/17")

    def test_compare_link_is_untouched(self) -> None:
        label, repo, url = self.repair("[x]", rl.ARCHIVED_REPO, "compare", "1.0.0...1.0.1")
        self.assertEqual(url, f"https://github.com/{rl.ARCHIVED_REPO}/compare/1.0.0...1.0.1")


class VersionWindowTest(unittest.TestCase):
    """Verify the release time window used for commit fallbacks."""

    def test_windows_cover_every_tag(self) -> None:
        windows = rl.version_windows(Path(__file__).resolve().parents[2])
        self.assertIn("4.0.1", windows)
        self.assertIn("2.6.9", windows)
        for tag, (lower, upper) in windows.items():
            self.assertLess(lower, upper, tag)


if __name__ == "__main__":
    unittest.main(verbosity=2)
