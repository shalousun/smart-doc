# Contributing

Thank you for your interest in contributing to `smart-doc`! For detailed guidelines on how to contribute, please review our official [**Contribution Guide**](https://smart-doc-group.github.io/guide/community/contributing).

## Key Highlights
Before submitting a Pull Request (PR), please ensure:
- 📌 **Discuss new features**: Start with an [Issue](https://github.com/smart-doc-group/smart-doc/issues) to align on proposed changes.
- 🧩 **Single-focused PRs**: Submit one fix or feature per PR to simplify reviews.
- 🌍 **English comments**: Use English for code comments to support international collaboration.
- ✅ **Code quality**: Follow the [Spring Java Format](https://github.com/spring-io/java-format) style and add unit tests.
- 📄 **Update documentation**: Reflect your changes in relevant documentation [`smart-doc-group.github.io`](https://github.com/smart-doc-group/smart-doc-group.github.io/tree/master/docs). if needed.
- 📝 **Commit message guidelines**: All commits must follow the [Angular Commit Convention](https://github.com/angular/angular/blob/main/CONTRIBUTING.md#commit).
	- Use appropriate types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`, etc.
	- Keep subject and body concise, avoiding vague terms like "update code".
	- Reference related issues (e.g., `Closes #1234`) in the footer when applicable.

## Documentation and Examples
If your contribution involves:
- 🛠️ **Configuration changes**:
	- Check if your PR adds or modifies configurations (e.g., new parameters, default value updates).
	- Update or add corresponding documentation in the [`smart-doc-group.github.io`](https://github.com/smart-doc-group/smart-doc-group.github.io/tree/master/docs).
- 📚 **New features**:
	- Provide usage examples in the [`smart-doc-example-cn`](https://github.com/smart-doc-group/smart-doc-example-cn) repository to demonstrate implementation.


## Release notes and CHANGELOG
`CHANGELOG.md` follows the [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) layout and
is maintained by the release pipeline, not by individual PRs. When a GitHub Release is
created, the `Update CHANGELOG` workflow generates a section from the Release Notes,
inserts it in descending version order, and pushes it to `master`. The generator is
idempotent, so re-running the workflow never duplicates a section.

Each version looks like this:

```markdown
## [4.0.1](https://github.com/smart-doc-group/smart-doc/compare/4.0.0...4.0.1) - 2026-10-31

Maven coordinates: `com.github.shalousun:smart-doc:4.0.1`

### Added

- feat: add a feature ([#19](https://github.com/smart-doc-group/smart-doc/pull/19))

### Fixed

- fix: repair a defect ([#20](https://github.com/smart-doc-group/smart-doc/pull/20))
```

- Version headings are level two, change groups are level three, and every entry is an
  unordered list item. Do not use manual numbering.
- Issue and pull request references use the number as the link text, so `[#19](url)`
  rather than a repeated `[PR](url)`.
- Releases up to 3.1.2 were published from the archived
  [TongchengOpenSource/smart-doc](https://github.com/TongchengOpenSource/smart-doc)
  repository, so links for those versions point there.
- Contributors do not edit `CHANGELOG.md` for a feature or fix; the release notes are
  assembled at release time instead.
- Maintainers can re-run the workflow manually with `workflow_dispatch` (optionally
  passing a tag) if a section needs to be generated after the fact.
- The generator records the actual Maven groupId for each version and marks the two
  coordinate migrations (3.0.0 and 4.0.0) as breaking changes.

To verify the generator locally:
```bash
python -m unittest discover -s scripts/changelog -p 'test_*.py'
python scripts/changelog/generate_changelog.py --tag <tag> --dry-run
```

## How to Contribute
1. Fork the repository and clone it locally.
2. Create a new branch for your changes.
3. Test your modifications using `smart-doc` (see [Quick Start](https://smart-doc-group.github.io/guide/getting-started)).
4. Format code with `mvn spring-javaformat:apply`.
5. Submit a PR to the main repository.

## Community Engagement
- 📰 Share your use cases or articles in the [Discussions](https://github.com/smart-doc-group/smart-doc/discussions) section.
- ❓ Help answer questions in the `Help` category.
- 💡 Propose ideas or showcase tools in `Ideas` or `Show and tell`.

Your contributions are vital to making `smart-doc` better for everyone. Thank you!  