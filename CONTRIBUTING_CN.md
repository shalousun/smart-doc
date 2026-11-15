# 贡献指南

感谢您对参与 `smart-doc` 贡献的兴趣！详细的贡献指引请参考我们的官方 [**贡献指南文档**](https://smart-doc-group.github.io/zh/guide/community/contributing)。

## 核心要求
提交 Pull Request (PR) 前请确保：
- 📌 **新功能需先讨论**：通过 [Issue](https://github.com/smart-doc-group/smart-doc/issues) 与社区对齐需求，避免重复劳动。
- 🧩 **单焦点 PR**：每次提交仅包含一个修复或功能，简化代码审查。
- 🌍 **英文注释**：代码注释使用英文，支持国际化协作。
- ✅ **代码质量**：遵循 [Spring Java Format](https://github.com/spring-io/java-format) 风格，添加单元测试。
- 📄 **文档同步更新**：若涉及功能变更，请同步更新 [`smart-doc-group.github.io`](https://github.com/smart-doc-group/smart-doc-group.github.io/tree/master/docs) 文档。
- 📝 **提交信息规范**：所有提交需严格遵循 [Angular 提交规范](https://github.com/angular/angular/blob/main/CONTRIBUTING.md#commit)。
	- 使用标准类型：`feat`（新功能）、`fix`（修复）、`docs`（文档）、`style`（格式调整）、`refactor`（重构）、`test`（测试）、`chore`（构建/依赖管理）等。
	- 主题与正文需简洁明确，避免“更新代码”等模糊描述。
	- 若关联 Issue，需在页脚注明（如 `Closes #1234`）。

## 文档与示例要求
若贡献包含以下内容：
- 🛠️ **配置项变更**：
	- 检查 PR 是否新增/修改配置（如新增参数、修改默认值）。
	- 在 [`smart-doc-group.github.io`](https://github.com/smart-doc-group/smart-doc-group.github.io/tree/master/docs) 中补充或更新对应文档。
- 📚 **新功能实现**：
	- 在 [`smart-doc-example-cn`](https://github.com/smart-doc-group/smart-doc-example-cn) 仓库提供使用示例，便于其他用户快速理解。

## 贡献步骤
1. Fork 仓库并本地克隆。
2. 创建新分支进行修改。
3. 使用 `smart-doc` 测试改动（参考 [快速入门](https://smart-doc-group.github.io/zh/guide/getting-started)）。
4. 执行 `mvn spring-javaformat:apply` 格式化代码。
5. 向主仓库提交 PR。

## 发布说明与更新日志
`CHANGELOG.md` 遵循 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/) 规范，
由发布流水线自动维护，而非通过单个 PR 手动编辑。当创建 GitHub Release 时，
`Update CHANGELOG` 工作流会根据 Release Notes 生成对应版本章节，按版本号降序插入并推送到 `master`。
生成器是幂等的，因此重复运行工作流不会重复生成章节。

每个版本章节格式如下：

```markdown
## [4.0.1](https://github.com/smart-doc-group/smart-doc/compare/4.0.0...4.0.1) - 2026-10-31

Maven coordinates: `com.github.shalousun:smart-doc:4.0.1`

### Added

- feat: add a feature ([#19](https://github.com/smart-doc-group/smart-doc/pull/19))

### Fixed

- fix: repair a defect ([#20](https://github.com/smart-doc-group/smart-doc/pull/20))
```

- 版本标题为二级标题，变更分组为三级标题，每条变更均为无序列表项，请勿使用手动编号。
- Issue 与 Pull Request 引用以编号作为链接文本，即 `[#19](url)` 而非重复的 `[PR](url)`。
- 3.1.2 及更早版本发布于已归档的 [TongchengOpenSource/smart-doc](https://github.com/TongchengOpenSource/smart-doc) 仓库，因此这些版本的链接指向该仓库。
- 贡献者无需为某个功能或修复手动编辑 `CHANGELOG.md`，发布说明会在发版时统一汇总生成。
- 维护者可通过 `workflow_dispatch` 手动重跑工作流（可传入 tag 参数），用于补生成某次遗漏的章节。
- 生成器会记录每个版本真实的 Maven groupId，并将两次坐标迁移（3.0.0 与 4.0.0）标记为破坏性变更。

本地验证生成器：
```bash
python -m unittest discover -s scripts/changelog -p 'test_*.py'
python scripts/changelog/generate_changelog.py --tag <tag> --dry-run
```

## 社区互动
- 📰 在 [Discussions](https://github.com/smart-doc-group/smart-doc/discussions) 分享使用案例或技术文章。
- ❓ 在 `Help` 分类帮助解答用户疑问。
- 💡 在 `Ideas` 或 `Show and tell` 提出建议或展示相关工具。

您的贡献将帮助 `smart-doc` 更好地服务全球开发者！感谢支持！