#!/usr/bin/env python3
"""Unit tests for the changelog normalisation tool."""

from __future__ import annotations

import sys
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

import normalize_changelog as nc  # noqa: E402


class HeadingTest(unittest.TestCase):
    """Verify that every historical heading style is recognised."""

    def test_plain_heading(self) -> None:
        match = nc.VERSION_HEADING.match("### 4.0.1")
        self.assertIsNotNone(match)
        self.assertEqual(match.group(1), "4.0.1")

    def test_chinese_heading(self) -> None:
        match = nc.VERSION_HEADING.match("#### 版本号：2.4.4")
        self.assertIsNotNone(match)
        self.assertEqual(match.group(1), "2.4.4")

    def test_malformed_heading_with_link_prefix(self) -> None:
        line = "###https://github.com/smart-doc-group/smart-doc/issues/ 版本号：1.9.2"
        match = nc.VERSION_HEADING.match(line)
        self.assertIsNotNone(match)
        self.assertEqual(match.group(1), "1.9.2")

    def test_combined_range(self) -> None:
        self.assertEqual(nc.primary_version("2.0.3-2.0.4"), "2.0.3")
        self.assertEqual(nc.primary_version("3.1.2"), "3.1.2")


class MalformedLinkTest(unittest.TestCase):
    """Verify the repair of full-width closing parentheses."""

    def test_full_width_parenthesis_is_fixed(self) -> None:
        text = "支持gson别名解析。 [#485](https://github.com/o/r/pull/485）"
        self.assertEqual(
            nc.fix_malformed_links(text),
            "支持gson别名解析。 [#485](https://github.com/o/r/pull/485)",
        )

    def test_normal_link_is_untouched(self) -> None:
        text = "ok [#1](https://github.com/o/r/pull/1)"
        self.assertEqual(nc.fix_malformed_links(text), text)


class ClassificationTest(unittest.TestCase):
    """Verify the change grouping rules."""

    def test_conventional_prefixes(self) -> None:
        self.assertEqual(nc.classify("feat: add thing"), "Added")
        self.assertEqual(nc.classify("fix: repair thing"), "Fixed")
        self.assertEqual(nc.classify("perf: speed up"), "Changed")
        self.assertEqual(nc.classify("chore(deps): bump"), "Changed")

    def test_chinese_keywords(self) -> None:
        self.assertEqual(nc.classify("新增忽略请求参数对象的配置"), "Added")
        self.assertEqual(nc.classify("修复html文档搜索后链接错误的bug"), "Fixed")
        self.assertEqual(nc.classify("优化常量获取"), "Changed")

    def test_breaking_change_wins(self) -> None:
        self.assertEqual(nc.classify("**破坏性变更 / BREAKING**: groupId 变更"), "Breaking changes")
        self.assertEqual(nc.classify("feat: x **Breaking change**"), "Breaking changes")


class MergeTest(unittest.TestCase):
    """Verify that a version recorded twice becomes one block."""

    def make_block(self, version: str, entries: list[str]) -> dict[str, object]:
        return {
            "heading": version,
            "version": version,
            "version_key": tuple(int(p) for p in version.split(".")),
            "date": "2020-08-23",
            "entries": entries,
        }

    def test_duplicate_versions_are_merged(self) -> None:
        blocks = [
            self.make_block("1.9.2", ["中文条目"]),
            self.make_block("1.9.2", ["english entry"]),
        ]
        merged = nc.merge_duplicates(blocks)
        self.assertEqual(len(merged), 1)
        self.assertEqual(merged[0]["entries"], ["中文条目", "english entry"])

    def test_translation_pairs_are_deduplicated(self) -> None:
        english = "Optimize the curl use case and add request header settings in the use case."
        chinese = "优化curl用例，增加请求头。"
        blocks = [self.make_block("1.9.2", [chinese, english])]
        merged = nc.merge_duplicates(blocks)
        self.assertEqual(merged[0]["entries"], [chinese])

    def test_distinct_versions_are_kept(self) -> None:
        blocks = [self.make_block("1.9.2", ["a"]), self.make_block("1.9.3", ["b"])]
        merged = nc.merge_duplicates(blocks)
        self.assertEqual(len(merged), 2)


if __name__ == "__main__":
    unittest.main(verbosity=2)
