#!/usr/bin/env python3
"""Unit tests for the CHANGELOG generator."""

from __future__ import annotations

import sys
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

import generate_changelog as gc  # noqa: E402


class ParseReleaseBodyTest(unittest.TestCase):
    """Cover both GitHub-generated and hand-written Release Notes."""

    def test_github_generated_body(self) -> None:
        body = """## What's Changed
* feat: add a feature by @alice in https://github.com/smart-doc-group/smart-doc/pull/19
* fix: :bug: repair a bug by @dependabot[bot] in https://github.com/smart-doc-group/smart-doc/pull/20

## New Contributors
* @alice made their first contribution in https://github.com/smart-doc-group/smart-doc/pull/19

**Full Changelog**: https://github.com/smart-doc-group/smart-doc/compare/4.0.0...4.0.1
"""
        items, comparison = gc.parse_release_body(body)
        self.assertEqual(len(items), 2)
        self.assertEqual(items[0].text, "feat: add a feature")
        self.assertEqual(items[0].url, "https://github.com/smart-doc-group/smart-doc/pull/19")
        self.assertEqual(items[1].text, "fix: :bug: repair a bug")
        self.assertIn("dependabot[bot]", body)
        self.assertEqual(comparison, "https://github.com/smart-doc-group/smart-doc/compare/4.0.0...4.0.1")

    def test_legacy_body(self) -> None:
        body = """## Bug Fixes\r
- Fix jsr 303 verification parsing error.\r
## Enhancement\r
- Added support for Open Api 3.0.\r
"""
        items, comparison = gc.parse_release_body(body)
        self.assertEqual(len(items), 2)
        self.assertEqual(items[0].text, "Fix jsr 303 verification parsing error.")
        self.assertEqual(items[1].text, "Added support for Open Api 3.0.")
        self.assertIsNone(comparison)

    def test_plain_prose_body(self) -> None:
        items, _comparison = gc.parse_release_body("Emergency fix version for 1.9.2")
        self.assertEqual(len(items), 1)
        self.assertEqual(items[0].text, "Emergency fix version for 1.9.2")
        self.assertEqual(items[0].kind, "note")

    def test_truncated_title_is_preserved_for_completion(self) -> None:
        body = "* fix: something rather long and truncat\u2026 by @bob in https://github.com/o/r/pull/1"
        items, _comparison = gc.parse_release_body(body)
        self.assertEqual(items[0].text, "fix: something rather long and truncat\u2026")

    def test_truncated_title_is_completed_from_git(self) -> None:
        data = gc.build_release_data(
            tag="4.0.1",
            repo="smart-doc-group/smart-doc",
            repo_root=Path(__file__).resolve().parents[2],
            previous="4.0.0",
            release_body="* fix: close resources with try-with-resources and make static caches t\u2026 by @shalousun in https://github.com/smart-doc-group/smart-doc/pull/28",
            published="2026-10-31",
        )
        self.assertIsNotNone(data)
        self.assertIn("thread-safe", data.items[0].text)  # type: ignore[union-attr]
        self.assertNotIn("\u2026", data.items[0].text)  # type: ignore[union-attr]

    def test_cross_repository_links(self) -> None:
        body = """## What's Changed
* feat: a by @a in https://github.com/TongchengOpenSource/smart-doc/pull/782
* fix: b by @b in https://github.com/TongchengOpenSource/smart-doc/pull/783
"""
        items, _comparison = gc.parse_release_body(body)
        owners = {gc.repo_for_url(item.url) for item in items if item.url}
        self.assertEqual(owners, {"TongchengOpenSource/smart-doc"})


class CoordinateTest(unittest.TestCase):
    """Verify Maven groupId mapping and migration notes."""

    def test_group_id_by_version(self) -> None:
        self.assertEqual(gc.group_id_for("2.7.7"), "com.github.shalousun")
        self.assertEqual(gc.group_id_for("3.0.0"), "com.ly.smart-doc")
        self.assertEqual(gc.group_id_for("3.1.2"), "com.ly.smart-doc")
        self.assertEqual(gc.group_id_for("4.0.0"), "com.github.shalousun")
        self.assertEqual(gc.group_id_for("4.0.1"), "com.github.shalousun")

    def test_migration_notes(self) -> None:
        self.assertIn("Breaking change", gc.coordinate_migration_note("3.0.0") or "")
        self.assertIn("Breaking change", gc.coordinate_migration_note("4.0.0") or "")
        self.assertIsNone(gc.coordinate_migration_note("3.0.1"))

    def test_repository_by_version(self) -> None:
        self.assertEqual(gc.repository_for_version("3.1.2"), "TongchengOpenSource/smart-doc")
        self.assertEqual(gc.repository_for_version("4.0.0"), "smart-doc-group/smart-doc")


class InsertionTest(unittest.TestCase):
    """Verify idempotency, ordering, and history preservation."""

    HEADER = "# smart-doc Changelog\n\n"

    def test_has_section(self) -> None:
        text = self.HEADER + "## [4.0.1](https://example.com) - 2026-10-31\n\n### Added\n\n- x\n\n## [3.0.3](https://example.com) - 2024-03-30\n"
        self.assertTrue(gc.has_section(text, "4.0.1"))
        self.assertTrue(gc.has_section(text, "3.0.3"))
        self.assertFalse(gc.has_section(text, "4.0.2"))

    def test_has_section_legacy_styles(self) -> None:
        legacy = "## smart-doc版本\n\n### 版本号：3.0.3\n\n- old\n\n#### 版本号：2.4.4\n"
        self.assertTrue(gc.has_section(legacy, "3.0.3"))
        self.assertTrue(gc.has_section(legacy, "2.4.4"))

    def test_insert_newer_sections_at_top(self) -> None:
        text = self.HEADER + "## [3.0.3](https://example.com) - 2024-03-30\n\n### Added\n\n- old\n"
        new = ["## [4.0.1](https://example.com) - 2026-10-31\n\n### Added\n\n- new 1\n",
               "## [4.0.0](https://example.com) - 2026-07-11\n\n### Added\n\n- new 0\n"]
        result = gc.insert_sections(text, new)
        headings = [line for line in result.splitlines() if line.startswith("## ")]
        self.assertEqual(
            headings,
            [
                "## [4.0.1](https://example.com) - 2026-10-31",
                "## [4.0.0](https://example.com) - 2026-07-11",
                "## [3.0.3](https://example.com) - 2024-03-30",
            ],
        )
        self.assertIn("- old", result)

    def test_insert_older_section_inside_history(self) -> None:
        text = self.HEADER + "## [2.0.2](https://example.com) - 2020-12-01\n\n### Added\n\n- a\n\n## [2.0.0](https://example.com) - 2020-11-01\n\n### Added\n\n- b\n"
        result = gc.insert_sections(text, ["## [1.9.9.2](https://example.com) - 2020-12-06\n\n### Fixed\n\n- hotfix\n"])
        headings = [line for line in result.splitlines() if line.startswith("## ")]
        self.assertEqual(
            headings,
            [
                "## [2.0.2](https://example.com) - 2020-12-01",
                "## [2.0.0](https://example.com) - 2020-11-01",
                "## [1.9.9.2](https://example.com) - 2020-12-06",
            ],
        )

    def test_no_duplicate_on_second_run(self) -> None:
        text = self.HEADER + "## [3.0.3](https://example.com) - 2024-03-30\n\n### Added\n\n- old\n"
        once = gc.insert_sections(text, ["## [4.0.0](https://example.com) - 2026-07-11\n\n### Added\n\n- new\n"])
        self.assertEqual(gc.insert_sections(once, []), once)

    def test_history_block_preserved_byte_for_byte(self) -> None:
        history = "## [3.0.3](https://example.com) - 2024-03-30\n\n### Added\n\n- 更新日期: 2024-3-30\n  1. 中文条目\n"
        text = self.HEADER + history
        result = gc.insert_sections(text, ["## [4.0.1](https://example.com) - 2026-10-31\n\n### Added\n\n- new\n"])
        self.assertIn(history.strip(), result)

    def test_section_count(self) -> None:
        self.assertEqual(len(gc.BACKFILL_TAGS), 14)
        self.assertNotIn("1.6.3", gc.BACKFILL_TAGS)
        self.assertNotIn("2.0.3", gc.BACKFILL_TAGS)


class RenderingTest(unittest.TestCase):
    """Verify section rendering and link selection."""

    def make_release(self, tag: str, previous: str, repository: str) -> gc.ReleaseData:
        return gc.ReleaseData(
            tag=tag,
            date="2026-10-31",
            items=(
                gc.ChangeItem(
                    text="feat: example",
                    url="https://github.com/smart-doc-group/smart-doc/pull/42",
                    kind="change",
                ),
            ),
            source="release",
            repository=repository,
            previous_tag=previous,
        )

    def test_same_repository_uses_compare(self) -> None:
        rendered = gc.render_section(self.make_release("4.0.1", "4.0.0", "smart-doc-group/smart-doc"))
        self.assertIn("compare/4.0.0...4.0.1", rendered)
        self.assertTrue(rendered.startswith("## [4.0.1]"))

    def test_cross_repository_uses_commits(self) -> None:
        rendered = gc.render_section(self.make_release("4.0.0", "3.1.2", "smart-doc-group/smart-doc"))
        self.assertIn("/commits/4.0.0", rendered)
        self.assertNotIn("compare/3.1.2", rendered)

    def test_groups_by_change_type(self) -> None:
        release = gc.ReleaseData(
            tag="4.0.2",
            date="2026-11-11",
            items=(
                gc.ChangeItem(text="feat: new thing", kind="change"),
                gc.ChangeItem(text="fix: broken thing", kind="change"),
                gc.ChangeItem(text="perf: faster thing", kind="change"),
                gc.ChangeItem(text="plain note", kind="change"),
            ),
            source="release",
            repository="smart-doc-group/smart-doc",
            previous_tag="4.0.1",
        )
        rendered = gc.render_section(release)
        self.assertIn("### Added\n\n- feat: new thing", rendered)
        self.assertIn("### Fixed\n\n- fix: broken thing", rendered)
        self.assertIn("### Changed\n\n- perf: faster thing", rendered)
        self.assertIn("### Other\n\n- plain note", rendered)

    def test_breaking_change_gets_own_group(self) -> None:
        release = gc.ReleaseData(
            tag="4.0.0",
            date="2026-07-11",
            items=(gc.ChangeItem(text="feat: example", kind="change"),),
            source="release",
            repository="smart-doc-group/smart-doc",
            previous_tag="3.1.2",
        )
        rendered = gc.render_section(release)
        self.assertIn("### Breaking changes", rendered)
        self.assertIn("**Breaking change**", rendered)

    def test_links_use_the_number_as_label(self) -> None:
        rendered = gc.render_section(self.make_release("4.0.1", "4.0.0", "smart-doc-group/smart-doc"))
        self.assertIn("([#42](https://github.com/smart-doc-group/smart-doc/pull/42))", rendered)
        self.assertNotIn("[PR]", rendered)

    def test_bullet_list_without_numbering(self) -> None:
        rendered = gc.render_section(self.make_release("4.0.1", "4.0.0", "smart-doc-group/smart-doc"))
        for line in rendered.splitlines():
            self.assertIsNone(gc.re.match(r"^\s+\d+\.\s", line), line)
            self.assertIsNone(gc.re.match(r"^\s+\d+\.\s", line), line)


class CommandLineTest(unittest.TestCase):
    """Verify the generator never touches version files."""

    def test_no_version_file_mutation(self) -> None:
        source = Path(gc.__file__).read_text(encoding="utf-8")
        for forbidden in ("smart-doc.version", "release-please", "bumpVersion"):
            self.assertNotIn(forbidden, source)
        self.assertNotIn("write_text(", source.replace('changelog_path.write_text(insert_sections(text, sections), encoding="utf-8")', ""))
        self.assertIn("never modifies pom.xml", source)


if __name__ == "__main__":
    unittest.main(verbosity=2)
