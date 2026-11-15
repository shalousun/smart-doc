# smart-doc Changelog

All notable changes to this project are documented in this file. The format is based on
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/). Versions are listed newest first;
releases up to 3.1.2 were published from the archived
[TongchengOpenSource/smart-doc](https://github.com/TongchengOpenSource/smart-doc) repository,
and 4.0.0 onward from [smart-doc-group/smart-doc](https://github.com/smart-doc-group/smart-doc).

## [4.0.1](https://github.com/smart-doc-group/smart-doc/compare/4.0.0...4.0.1) - 2026-10-31

Maven coordinates: `com.github.shalousun:smart-doc:4.0.1`

### Added

- feat: add contributing guide link to README files ([#19](https://github.com/smart-doc-group/smart-doc/pull/19))
- feat: add global server URL configuration to debug pages ([#21](https://github.com/smart-doc-group/smart-doc/pull/21))
- feat: change build-and-run-example-project.yml ([#26](https://github.com/smart-doc-group/smart-doc/pull/26))
- feat: support Spring @HttpExchange annotations ([#37](https://github.com/smart-doc-group/smart-doc/pull/37))
- feat: make debug page parameter tables collapsible ([#38](https://github.com/smart-doc-group/smart-doc/pull/38))
- feat: support configurable operationId strategy ([#39](https://github.com/smart-doc-group/smart-doc/pull/39))
- feat: add reproducible generation mode and log increment failures ([#42](https://github.com/smart-doc-group/smart-doc/pull/42))

### Changed

- chore: upgrade actions to Node 24 compatible versions ([#23](https://github.com/smart-doc-group/smart-doc/pull/23))
- chore: update copyright year to 2026 ([#27](https://github.com/smart-doc-group/smart-doc/pull/27))
- refactor: remove redundant null checks after throwing template factories ([#29](https://github.com/smart-doc-group/smart-doc/pull/29))
- perf: reuse cached pattern in DocUtil.isMatch ([#30](https://github.com/smart-doc-group/smart-doc/pull/30))
- ci: harden example-project workflow and drop org secret ([#31](https://github.com/smart-doc-group/smart-doc/pull/31))
- docs: add CLAUDE.md development and issue triage workflow ([#34](https://github.com/smart-doc-group/smart-doc/pull/34))
- refactor: share one parse across output formats and scope run state ([#40](https://github.com/smart-doc-group/smart-doc/pull/40))
- perf: avoid reloading sources and recomputing dictionaries per render ([#41](https://github.com/smart-doc-group/smart-doc/pull/41))
- perf: initialize mock field values lazily ([#43](https://github.com/smart-doc-group/smart-doc/pull/43))
- chore: release 4.0.1 ([#44](https://github.com/smart-doc-group/smart-doc/pull/44))

### Fixed

- fix: extract first line as plain-text title from Javadoc comments ([#20](https://github.com/smart-doc-group/smart-doc/pull/20))
- fix: use pull_request_target for fork PR access to org secrets ([#22](https://github.com/smart-doc-group/smart-doc/pull/22))
- fix: close resources with try-with-resources and make static caches thread-safe ([#28](https://github.com/smart-doc-group/smart-doc/pull/28))
- fix: make incremental build persist dependency tree ([#35](https://github.com/smart-doc-group/smart-doc/pull/35))
- fix: do not document request params as request body ([#36](https://github.com/smart-doc-group/smart-doc/pull/36))

## [4.0.0](https://github.com/smart-doc-group/smart-doc/commits/4.0.0) - 2026-07-11

Maven coordinates: `com.github.shalousun:smart-doc:4.0.0`

### Breaking changes

- BREAKING: Maven coordinates changed from `com.ly.smart-doc:smart-doc` back to `com.github.shalousun:smart-doc`; update the dependency groupId when upgrading

### Added

- feat: rename package ([#1](https://github.com/smart-doc-group/smart-doc/pull/1))
- feat: update readme ([#3](https://github.com/smart-doc-group/smart-doc/pull/3))
- feat: fix #11 ([#16](https://github.com/smart-doc-group/smart-doc/pull/16))
- feat: preserve HTML syntax in Javadoc comments ([#18](https://github.com/smart-doc-group/smart-doc/pull/18))

### Fixed

- fix: :bug: Prioritize explicit parameter annotations over MediaType detection ([#6](https://github.com/smart-doc-group/smart-doc/pull/6))
- fix: fix NPE in getRequestPartAnnotation for SolonDocBuildTemplate ([#17](https://github.com/smart-doc-group/smart-doc/pull/17))

## [3.1.2](https://github.com/TongchengOpenSource/smart-doc/compare/3.1.1...3.1.2) - 2025-09-10

Maven coordinates: `com.ly.smart-doc:smart-doc:3.1.2`

### Added

- feat: Support extracting the @apiNote information from Java classes as detailed descriptions. ([#1084](https://github.com/TongchengOpenSource/smart-doc/pull/1084))
- feat: add copy button plugin to code blocks ([#1090](https://github.com/TongchengOpenSource/smart-doc/pull/1090))
- feat: bump common-util to 2.2.9 ([#1091](https://github.com/TongchengOpenSource/smart-doc/pull/1091))
- feat: Fixed the pathPrefix generation error in the OpenAPI document. ([#1089](https://github.com/TongchengOpenSource/smart-doc/pull/1089))
- feat: update bug report ([#1101](https://github.com/TongchengOpenSource/smart-doc/pull/1101))
- feat:add dubboMethod field for dubbo rpc ([#1119](https://github.com/TongchengOpenSource/smart-doc/pull/1119))
- feat: release 3.1.2 ([#1120](https://github.com/TongchengOpenSource/smart-doc/pull/1120))

### Changed

- chore(deps-dev): bump org.junit.jupiter:junit-jupiter from 5.13.1 to 5.13.2 ([#1082](https://github.com/TongchengOpenSource/smart-doc/pull/1082))
- chore(deps): bump com.ibeetl:beetl from 3.19.1.RELEASE to 3.19.2.RELEASE ([#1083](https://github.com/TongchengOpenSource/smart-doc/pull/1083))
- chore(deps): bump org.apache.maven.plugins:maven-gpg-plugin from 3.2.7 to 3.2.8 ([#1088](https://github.com/TongchengOpenSource/smart-doc/pull/1088))
- chore(deps-dev): bump org.junit.jupiter:junit-jupiter from 5.13.2 to 5.13.3 ([#1087](https://github.com/TongchengOpenSource/smart-doc/pull/1087))
- chore(deps): bump org.apache.maven.plugins:maven-gpg-plugin from 3.2.7 to 3.2.8 ([#1093](https://github.com/TongchengOpenSource/smart-doc/pull/1093))
- chore(deps-dev): bump org.junit.jupiter:junit-jupiter from 5.13.3 to 5.13.4 ([#1096](https://github.com/TongchengOpenSource/smart-doc/pull/1096))
- chore(deps): bump actions/download-artifact from 4 to 5 ([#1100](https://github.com/TongchengOpenSource/smart-doc/pull/1100))
- chore(deps): bump actions/checkout from 4 to 5 ([#1107](https://github.com/TongchengOpenSource/smart-doc/pull/1107))
- chore(deps): bump org.apache.maven.plugins:maven-javadoc-plugin from 3.11.2 to 3.11.3 ([#1108](https://github.com/TongchengOpenSource/smart-doc/pull/1108))
- build: :wrench: enforce strict Javadoc validation and add parameter for Swagger support ([#1109](https://github.com/TongchengOpenSource/smart-doc/pull/1109))
- Update datafaker from 1.4.0 to 1.9.0 ([#1110](https://github.com/TongchengOpenSource/smart-doc/pull/1110))
- chore(deps): bump actions/setup-java from 4 to 5 ([#1112](https://github.com/TongchengOpenSource/smart-doc/pull/1112))

### Fixed

- fix(openapi): :bug: ensure tag names in paths match configured strategy ([#1085](https://github.com/TongchengOpenSource/smart-doc/pull/1085))
- fix(extension): :bug: resolve JsonNaming annotation value ([#1106](https://github.com/TongchengOpenSource/smart-doc/pull/1106))
- fix(enum): Fix the index out-of-bounds exception when the enumeration value is empty. ([#1111](https://github.com/TongchengOpenSource/smart-doc/pull/1111))
- fix: :bug: support path attribute in `@FeignClient` and refactor annotation constants ([#1115](https://github.com/TongchengOpenSource/smart-doc/pull/1115))
- fix(grpc): :bug: Fix gRPC doc generation for proto files organized in packages ([#1117](https://github.com/TongchengOpenSource/smart-doc/pull/1117))

### Other

- Fix incorrect display of non-JSON enum and conversion configuration errors when pushing to Torna ([#1118](https://github.com/TongchengOpenSource/smart-doc/pull/1118))

## [3.1.1](https://github.com/TongchengOpenSource/smart-doc/compare/3.1.0...3.1.1) - 2025-06-30

Maven coordinates: `com.ly.smart-doc:smart-doc:3.1.1`

### Added

- feat: set beetl classloader ([#1038](https://github.com/TongchengOpenSource/smart-doc/pull/1038))
- feat: refactor WordXmlEscape and HtmlEscape ([#1039](https://github.com/TongchengOpenSource/smart-doc/pull/1039))
- feat:Add to the OpenAPI specification : AbstractOpenApiBuilder ([#1055](https://github.com/TongchengOpenSource/smart-doc/pull/1055))
- feat(openapi): :sparkles: add support for generating tags by description or package name ([#1060](https://github.com/TongchengOpenSource/smart-doc/pull/1060))
- feat: Add allowSelfReference configuration to control self-reference ([#1065](https://github.com/TongchengOpenSource/smart-doc/pull/1065))
- feat(template): :sparkles: add method to determine if a method should be skipped in documentation ([#1074](https://github.com/TongchengOpenSource/smart-doc/pull/1074))
- feat: Optimize the bug report description ([#1077](https://github.com/TongchengOpenSource/smart-doc/pull/1077))

### Changed

- docs: :memo: add contributing guidelines in English and Chinese ([#1054](https://github.com/TongchengOpenSource/smart-doc/pull/1054))
- chore(deps): bump io.spring.javaformat:spring-javaformat-maven-plugin from 0.0.43 to 0.0.45 ([#1063](https://github.com/TongchengOpenSource/smart-doc/pull/1063))
- docs: :memo: update contribution guidelines and PR review process ([#1062](https://github.com/TongchengOpenSource/smart-doc/pull/1062))
- chore(deps-dev): bump org.junit.jupiter:junit-jupiter from 5.12.2 to 5.13.0 ([#1066](https://github.com/TongchengOpenSource/smart-doc/pull/1066))

### Fixed

- fix: :bug: Add support for default validation message placeholders ([#1044](https://github.com/TongchengOpenSource/smart-doc/pull/1044))
- fix(openapi): generate the example value based on the field type instead of always String ([#1061](https://github.com/TongchengOpenSource/smart-doc/pull/1061))
- fix: fix the key type check error for map types ([#1078](https://github.com/TongchengOpenSource/smart-doc/pull/1078))
- fix: Fix the error in document generation when the response field use ([#1080](https://github.com/TongchengOpenSource/smart-doc/pull/1080))

### Other

- Fix issue 1046 ([#1057](https://github.com/TongchengOpenSource/smart-doc/pull/1057))
- release: 3.1.1 ([#1081](https://github.com/TongchengOpenSource/smart-doc/pull/1081))

## [3.1.0](https://github.com/TongchengOpenSource/smart-doc/compare/3.0.9...3.1.0) - 2025-03-29

Maven coordinates: `com.ly.smart-doc:smart-doc:3.1.0`

### Added

- feat(markdown): Add the 'Example' field to the markdown document parameter ([#972](https://github.com/TongchengOpenSource/smart-doc/pull/972))
- feat(enum): Modify the enumeration example value generation rule ([#977](https://github.com/TongchengOpenSource/smart-doc/pull/977))
- feat(doc): 'html', 'adoc', 'doc' should support 'Example' just as 'markdown' ([#985](https://github.com/TongchengOpenSource/smart-doc/pull/985))
- feat(enum): Request header support enum ([#980](https://github.com/TongchengOpenSource/smart-doc/pull/980))
- feat: bump qdox to 2.0.3.5 and support jdk17 text block ([#987](https://github.com/TongchengOpenSource/smart-doc/pull/987))
- feat(html): html template automatic compress ([#992](https://github.com/TongchengOpenSource/smart-doc/pull/992))
- feat: add star History ([#993](https://github.com/TongchengOpenSource/smart-doc/pull/993))
- feat: bump common-util to 2.2.9 ([#1016](https://github.com/TongchengOpenSource/smart-doc/pull/1016))
- feat(validation): :sparkles: Add Support for Resolving Placeholders in Validation Messages ([#1029](https://github.com/TongchengOpenSource/smart-doc/pull/1029))
- feat(utils): :sparkles: add functionality to resolve annotation values and get parameter max length ([#1030](https://github.com/TongchengOpenSource/smart-doc/pull/1030))
- feat: release 3.1.0 ([#1037](https://github.com/TongchengOpenSource/smart-doc/pull/1037))

### Changed

- ci: :green_heart: add permissions for write comment to pr ([#963](https://github.com/TongchengOpenSource/smart-doc/pull/963))
- refactor: improve request example generation for API documentation ([#968](https://github.com/TongchengOpenSource/smart-doc/pull/968))
- chore(deps): bump org.apache.maven.plugins:maven-javadoc-plugin from 3.11.1 to 3.11.2 ([#988](https://github.com/TongchengOpenSource/smart-doc/pull/988))
- perf(enum): Added the 'enumConvertor' configuration and modified the enumeration example value logic ([#989](https://github.com/TongchengOpenSource/smart-doc/pull/989))
- chore(deps): bump com.ibeetl:beetl from 3.17.0.RELEASE to 3.18.0.RELEASE ([#997](https://github.com/TongchengOpenSource/smart-doc/pull/997))
- ci: :construction_worker: update PR comment template ([#1006](https://github.com/TongchengOpenSource/smart-doc/pull/1006))
- chore(deps-dev): bump org.junit.jupiter:junit-jupiter from 5.11.3 to 5.11.4 ([#1008](https://github.com/TongchengOpenSource/smart-doc/pull/1008))
- chore(deps): bump com.ibeetl:beetl from 3.18.0.RELEASE to 3.19.0.RELEASE ([#1007](https://github.com/TongchengOpenSource/smart-doc/pull/1007))
- chore(deps): bump org.sonatype.central:central-publishing-maven-plugin from 0.6.0 to 0.7.0 ([#1013](https://github.com/TongchengOpenSource/smart-doc/pull/1013))
- refactor(doc): :art: improve API documentation generation and enforce strict `Javadoc` validation ([#1019](https://github.com/TongchengOpenSource/smart-doc/pull/1019))
- refactor: :recycle: optimize custom field logic and update related models ([#1020](https://github.com/TongchengOpenSource/smart-doc/pull/1020))
- chore(deps): bump com.google.code.gson:gson from 2.11.0 to 2.12.1 ([#1022](https://github.com/TongchengOpenSource/smart-doc/pull/1022))
- ci: :construction_worker: update Maven build commands in GitHub Actions workflows ([#1023](https://github.com/TongchengOpenSource/smart-doc/pull/1023))
- chore: add `.editorconfig` file for code formatting consistency ([#1024](https://github.com/TongchengOpenSource/smart-doc/pull/1024))
- Optimize the code of the Dubbo Interface Word Feature ([#1021](https://github.com/TongchengOpenSource/smart-doc/pull/1021))
- chore(deps-dev): bump org.junit.jupiter:junit-jupiter from 5.11.4 to 5.12.0 ([#1032](https://github.com/TongchengOpenSource/smart-doc/pull/1032))
- chore(deps): bump org.apache.maven.plugins:maven-compiler-plugin from 3.13.0 to 3.14.0 ([#1031](https://github.com/TongchengOpenSource/smart-doc/pull/1031))
- chore(deps): bump com.ibeetl:beetl from 3.19.0.RELEASE to 3.19.1.RELEASE ([#1033](https://github.com/TongchengOpenSource/smart-doc/pull/1033))
- chore(deps): bump org.slf4j:slf4j-api from 2.0.16 to 2.0.17 ([#1034](https://github.com/TongchengOpenSource/smart-doc/pull/1034))
- chore(deps-dev): bump org.junit.jupiter:junit-jupiter from 5.12.0 to 5.12.1 ([#1035](https://github.com/TongchengOpenSource/smart-doc/pull/1035))

### Fixed

- fix(torna): :bug: retain query parameters when ContentType is multipart/form-data ([#962](https://github.com/TongchengOpenSource/smart-doc/pull/962))
- fix: :bug: correct parameter documentation for `@RequestBody` and unannotated form parameters ([#967](https://github.com/TongchengOpenSource/smart-doc/pull/967))
- fix: Fix if it was inner enumeration, classLoader load class maybe fail ([#970](https://github.com/TongchengOpenSource/smart-doc/pull/970))
- fix(enum): Fixed an issue with enumeration in multiple scenarios ([#975](https://github.com/TongchengOpenSource/smart-doc/pull/975))
- fix(enum): Fixed an issue with example values of enumerated arrays not removing quotes ([#981](https://github.com/TongchengOpenSource/smart-doc/pull/981))
- fix: Fixed the see inner enumeration className loading error ([#982](https://github.com/TongchengOpenSource/smart-doc/pull/982))
- fix(enum): Fix the json request enumeration identification error ([#990](https://github.com/TongchengOpenSource/smart-doc/pull/990))
- fix: 🐛 Enhance enum dictionary generation and improve enum class resolution ([#996](https://github.com/TongchengOpenSource/smart-doc/pull/996))
- fix(map): Fix the response result is Map<Enum, Value> is not parsed correctly ([#1005](https://github.com/TongchengOpenSource/smart-doc/pull/1005))
- fix: :bug: resolve incorrect parsing of constants in `maxLength` ([#1027](https://github.com/TongchengOpenSource/smart-doc/pull/1027))

### Other

- Fix to issue window style display error when style is "xt256" ([#1000](https://github.com/TongchengOpenSource/smart-doc/pull/1000))
- Fix the request parameter is Map<Enum, Value> is not parsed correctly ([#1003](https://github.com/TongchengOpenSource/smart-doc/pull/1003))
- Add RpcWordDocBuilder to support generate word of dubbo ([#1011](https://github.com/TongchengOpenSource/smart-doc/pull/1011))
- fix dubbo word template bug ([#1014](https://github.com/TongchengOpenSource/smart-doc/pull/1014))
- The operationId value in the openapi.json file generated by the feat:openapi plugin remains the same as the springdoc-openapi ([#1036](https://github.com/TongchengOpenSource/smart-doc/pull/1036))

## [3.0.9](https://github.com/TongchengOpenSource/smart-doc/compare/3.0.8...3.0.9) - 2024-11-24

Maven coordinates: `com.ly.smart-doc:smart-doc:3.0.9`

### Changed

- chore(deps): bump org.apache.maven.plugins:maven-gpg-plugin from 3.2.5 to 3.2.6 ([#923](https://github.com/TongchengOpenSource/smart-doc/pull/923))
- chore(deps): bump org.sonatype.central:central-publishing-maven-plugin from 0.5.0 to 0.6.0 ([#929](https://github.com/TongchengOpenSource/smart-doc/pull/929))
- chore(deps): bump org.apache.maven.plugins:maven-gpg-plugin from 3.2.6 to 3.2.7 ([#928](https://github.com/TongchengOpenSource/smart-doc/pull/928))
- chore(deps-dev): bump org.junit.jupiter:junit-jupiter from 5.11.0 to 5.11.1 ([#927](https://github.com/TongchengOpenSource/smart-doc/pull/927))
- chore(deps): bump org.apache.maven.plugins:maven-javadoc-plugin from 3.10.0 to 3.10.1 ([#931](https://github.com/TongchengOpenSource/smart-doc/pull/931))
- chore(deps-dev): bump org.junit.jupiter:junit-jupiter from 5.11.1 to 5.11.2 ([#930](https://github.com/TongchengOpenSource/smart-doc/pull/930))
- chore(deps-dev): bump org.junit.jupiter:junit-jupiter from 5.11.2 to 5.11.3 ([#933](https://github.com/TongchengOpenSource/smart-doc/pull/933))
- refactor(url): Refactor URL-related code ([#942](https://github.com/TongchengOpenSource/smart-doc/pull/942))
- ci: :construction_worker: add GitHub Actions workflow to comment on PR ([#943](https://github.com/TongchengOpenSource/smart-doc/pull/943))
- chore(deps): bump org.apache.maven.plugins:maven-javadoc-plugin from 3.10.1 to 3.11.1 ([#944](https://github.com/TongchengOpenSource/smart-doc/pull/944))
- ci: :construction_worker: add PR comment notification for build and API doc generation ([#956](https://github.com/TongchengOpenSource/smart-doc/pull/956))

### Fixed

- fix: :bug: Fix empty JSON example value for Map with enum keys and primitive values ([#924](https://github.com/TongchengOpenSource/smart-doc/pull/924))
- fix: :bug: resolve incorrect parsing of constants in `@JsonProperty` and `@JSONField` annotation ([#935](https://github.com/TongchengOpenSource/smart-doc/pull/935))
- fix: :bug: Resolve NPE in Postman API doc generation when no methods are present ([#938](https://github.com/TongchengOpenSource/smart-doc/pull/938))
- fix: :bug: Resolve error caused by `EmptyList` being final ([#941](https://github.com/TongchengOpenSource/smart-doc/pull/941))
- fix: Fix issue If the request parameter contains type "date" and the parameter name is endTime or beginTime, the resulting example value may be different from what you expect. ([#948](https://github.com/TongchengOpenSource/smart-doc/pull/948))
- fix: :bug: correct example value generation for enums annotated with `@JsonValue` ([#952](https://github.com/TongchengOpenSource/smart-doc/pull/952))
- fix: Fixed issue caused by isQueryParam error ([#957](https://github.com/TongchengOpenSource/smart-doc/pull/957))

### Other

- Fix issue If there are enumerations in the query request parameters, the generated request parameter comments will not include 'See'. ([#945](https://github.com/TongchengOpenSource/smart-doc/pull/945))
- refactor (Enum): Optimize the handling logic for enum parameter types ([#954](https://github.com/TongchengOpenSource/smart-doc/pull/954))
- release: 3.0.9 ([#959](https://github.com/TongchengOpenSource/smart-doc/pull/959))

## [3.0.8](https://github.com/TongchengOpenSource/smart-doc/compare/3.0.7...3.0.8) - 2024-09-16

Maven coordinates: `com.ly.smart-doc:smart-doc:3.0.8`

### Added

- Feat :sparkles: Split transient field serialization into request and response flags ([#904](https://github.com/TongchengOpenSource/smart-doc/pull/904))
- feat: Fix the issue where the JsonValue annotation cannot be used on an enum. ([#910](https://github.com/TongchengOpenSource/smart-doc/pull/910))
- feat: Enum in the form skips parsing of JsonValue annotation. ([#911](https://github.com/TongchengOpenSource/smart-doc/pull/911))
- feat(params): add support for nested map parameter handling ([#914](https://github.com/TongchengOpenSource/smart-doc/pull/914))
- feat(enum): Support `@JsonValue` annotation on enum fields ([#916](https://github.com/TongchengOpenSource/smart-doc/pull/916))
- feat: bump common-util to 2.2.6 ([#919](https://github.com/TongchengOpenSource/smart-doc/pull/919))
- feat: Support Postman collection grouping ([#920](https://github.com/TongchengOpenSource/smart-doc/pull/920))

### Changed

- chore(deps): bump org.apache.maven.plugins:maven-javadoc-plugin from 3.7.0 to 3.8.0 ([#894](https://github.com/TongchengOpenSource/smart-doc/pull/894))
- refactor(torna-builder): streamline Torna API documentation generation ([#903](https://github.com/TongchengOpenSource/smart-doc/pull/903))
- ci: :construction_worker: run `TornaApiTest` in CI workflow ([#905](https://github.com/TongchengOpenSource/smart-doc/pull/905))

### Other

- ignore JGit and Data Faker dependencies ([#896](https://github.com/TongchengOpenSource/smart-doc/pull/896))
- Corrected incorrect use of 'version' to 'versions' in Dependabot config ([#897](https://github.com/TongchengOpenSource/smart-doc/pull/897))
- Handle single `@ServerEndpoint` property values correctly ([#902](https://github.com/TongchengOpenSource/smart-doc/pull/902))
- Fix NATIVE_NOT_ALLOWED when generate REST JMeter.jmx on linux ([#908](https://github.com/TongchengOpenSource/smart-doc/pull/908))
- Fix resolve stack overflow from duplicate ApiParam IDs with enum key Maps ([#917](https://github.com/TongchengOpenSource/smart-doc/pull/917))
- Refine build logs handling and error reporting in GitHub Actions ([#918](https://github.com/TongchengOpenSource/smart-doc/pull/918))

## [3.0.7](https://github.com/TongchengOpenSource/smart-doc/compare/3.0.6...3.0.7) - 2024-08-17

Maven coordinates: `com.ly.smart-doc:smart-doc:3.0.7`

### Added

- feat: Switching the publishing method to Central Portal. ([#848](https://github.com/TongchengOpenSource/smart-doc/pull/848))
- feat: :sparkles: Add Grpc support ([#851](https://github.com/TongchengOpenSource/smart-doc/pull/851))
- feat: :sparkles: Add WebSocket html, ascii doc support ([#855](https://github.com/TongchengOpenSource/smart-doc/pull/855))
- feat: prevent user submitting blank issues ([#861](https://github.com/TongchengOpenSource/smart-doc/pull/861))
- feat: disable blank issue creation ([#862](https://github.com/TongchengOpenSource/smart-doc/pull/862))
- feat: 📝 update issue template And Update PR CI GitHub Actions workflow ([#868](https://github.com/TongchengOpenSource/smart-doc/pull/868))
- feat: :memo: update issue template ([#871](https://github.com/TongchengOpenSource/smart-doc/pull/871))
- feat: ✨ Add support for Map with enum type keys ([#870](https://github.com/TongchengOpenSource/smart-doc/pull/870))
- feat: ✨ Add support for `@JsonView` ([#875](https://github.com/TongchengOpenSource/smart-doc/pull/875))
- feat(github-actions): set up weekly dependency updates ([#883](https://github.com/TongchengOpenSource/smart-doc/pull/883))

### Changed

- Update SolonRequestMappingHandler.java ([#849](https://github.com/TongchengOpenSource/smart-doc/pull/849))
- chore(deps): bump io.spring.javaformat:spring-javaformat-maven-plugin from 0.0.42 to 0.0.43 ([#880](https://github.com/TongchengOpenSource/smart-doc/pull/880))
- chore(deps): bump org.eclipse.jgit:org.eclipse.jgit from 5.13.2.202306221912-r to 5.13.3.202401111512-r ([#782](https://github.com/TongchengOpenSource/smart-doc/pull/782))
- chore(deps): bump org.slf4j:slf4j-api from 2.0.13 to 2.0.16 ([#885](https://github.com/TongchengOpenSource/smart-doc/pull/885))
- chore(deps): bump com.ibeetl:beetl from 3.16.2.RELEASE to 3.17.0.RELEASE ([#886](https://github.com/TongchengOpenSource/smart-doc/pull/886))
- chore(deps): bump org.apache.maven.plugins:maven-gpg-plugin from 3.2.4 to 3.2.5 ([#887](https://github.com/TongchengOpenSource/smart-doc/pull/887))
- chore(deps-dev): bump org.junit.jupiter:junit-jupiter from 5.10.2 to 5.11.0 ([#888](https://github.com/TongchengOpenSource/smart-doc/pull/888))

### Fixed

- fix: :bug: Fix the issue where the generated interface documentation lacks descriptive information when using `Dubbo RPC` or `@javadoc` tags and overriding a parent class or interface without Javadoc comments. ([#866](https://github.com/TongchengOpenSource/smart-doc/pull/866))
- fix(rpc): Fix the ClassCastException issue that occurs during the construction of the RPC. #889 ([#890](https://github.com/TongchengOpenSource/smart-doc/pull/890))

### Other

- Feature/request part ([#864](https://github.com/TongchengOpenSource/smart-doc/pull/864))
- Grant execute permissions for protoc-gen-doc on Linux/Mac OS ([#879](https://github.com/TongchengOpenSource/smart-doc/pull/879))
- Added support for message and response parameters ([#882](https://github.com/TongchengOpenSource/smart-doc/pull/882))
- enhance `IJavadocDocTemplate` with generics for method documentation ([#891](https://github.com/TongchengOpenSource/smart-doc/pull/891))
- Ensure Java format validation reports failure explicitly ([#892](https://github.com/TongchengOpenSource/smart-doc/pull/892))

## [3.0.6](https://github.com/TongchengOpenSource/smart-doc/compare/3.0.5...3.0.6) - 2024-07-05

Maven coordinates: `com.ly.smart-doc:smart-doc:3.0.6`

### Changed

- refactor: :recycle: Add HttpStatusEnum and enhance HttpStatusUtil ([#837](https://github.com/TongchengOpenSource/smart-doc/pull/837))
- refactor(build-template): ♻️ utilize ServiceLoader for dynamic loading ([#839](https://github.com/TongchengOpenSource/smart-doc/pull/839))
- docs: :memo: update Chinese documentation links ([#842](https://github.com/TongchengOpenSource/smart-doc/pull/842))

### Fixed

- fix: rest api sort error ([#813](https://github.com/TongchengOpenSource/smart-doc/pull/813))
- fix: Tree structure parsing bug ([#814](https://github.com/TongchengOpenSource/smart-doc/pull/814))
- fix: Fix the issue where the generated OpenAPI is not sorted according to the specified order. ([#817](https://github.com/TongchengOpenSource/smart-doc/pull/817))
- fix: :bug: add jakarta validation group to fix issue #819 ([#820](https://github.com/TongchengOpenSource/smart-doc/pull/820))
- fix: It is suspected that the curl request generated under a single file cannot obtain the header correctly. Currently, the debug execution is successful under multiple files, and the curl command generated by copying cannot be requested. Changing to double quotation marks is normal ([#827](https://github.com/TongchengOpenSource/smart-doc/pull/827))
- fix: :bug: Prevent EntryAnnotation from being inherited from parent classes and interfaces ([#825](https://github.com/TongchengOpenSource/smart-doc/pull/825))
- fix: :bug: Fix display issues for Instant and EnumSet<?> types ([#829](https://github.com/TongchengOpenSource/smart-doc/pull/829))
- fix: Generate OpenAPI Paths Object tags do not match ([#831](https://github.com/TongchengOpenSource/smart-doc/pull/831))
- fix(openapi): :bug: support @JsonFormat(shape = JsonFormat.Shape.NUMBER) annotation for enum types example ([#836](https://github.com/TongchengOpenSource/smart-doc/pull/836))
- fix: :bug: ensure non-null collections for API schema properties ([#841](https://github.com/TongchengOpenSource/smart-doc/pull/841))

### Other

- deps: bump gson,slf4j-api,beetl,common-util to latest version ([#812](https://github.com/TongchengOpenSource/smart-doc/pull/812))
- deps: Bump JQuery to 3.7.1, Highlight.js to v11.9.0 ([#815](https://github.com/TongchengOpenSource/smart-doc/pull/815))
- optimised: Optimise the duplicate code in the template builders ([#818](https://github.com/TongchengOpenSource/smart-doc/pull/818))
- Fix/jsr303 required field bug ([#823](https://github.com/TongchengOpenSource/smart-doc/pull/823))
- Feature/jsonformat and json serialize ([#834](https://github.com/TongchengOpenSource/smart-doc/pull/834))

## [3.0.5](https://github.com/TongchengOpenSource/smart-doc/compare/3.0.4...3.0.5) - 2024-06-09

Maven coordinates: `com.ly.smart-doc:smart-doc:3.0.5`

### Added

- feat: Optimize the settings for ExampleBody to remove duplicated code. ([#794](https://github.com/TongchengOpenSource/smart-doc/pull/794))
- feat: Optimize and refactor constant definitions. ([#807](https://github.com/TongchengOpenSource/smart-doc/pull/807))
- feat: Optimize the document generation template return interface definition to support future API interface error configuration additions. ([#809](https://github.com/TongchengOpenSource/smart-doc/pull/809))
- feat: Supports generating descriptions for OpenAPI HTTP error statuses like 404, 500, etc. ([#810](https://github.com/TongchengOpenSource/smart-doc/pull/810))

### Changed

- Optimize: Optimize the repetitive code in RPC and Javadoc document gereration template. ([#802](https://github.com/TongchengOpenSource/smart-doc/pull/802))

### Fixed

- fix: Javadoc method definition add modifier. ([#801](https://github.com/TongchengOpenSource/smart-doc/pull/801))
- fix: Fix the bug where enum type parameter comments are missing in the query. ([#803](https://github.com/TongchengOpenSource/smart-doc/pull/803))
- fix: Generic replacement error causing parsing bug. ([#805](https://github.com/TongchengOpenSource/smart-doc/pull/805))
- fix: Fix the bug where the sequence number sorting is incorrect when using the `@order` tag. ([#806](https://github.com/TongchengOpenSource/smart-doc/pull/806))
- fix: Fix the issue where exported documents display incorrectly in tools like Postman when the content-type is form-data and urlencoded. ([#808](https://github.com/TongchengOpenSource/smart-doc/pull/808))

### Other

- If comment is empty, replace it with "undefined" ([#788](https://github.com/TongchengOpenSource/smart-doc/pull/788))
- fix #789 NPE in JsonBuildHelper ([#790](https://github.com/TongchengOpenSource/smart-doc/pull/790))
- Support `@javadoc` tag for normal service. ([#797](https://github.com/TongchengOpenSource/smart-doc/pull/797))
- release: 3.0.5. ([#811](https://github.com/TongchengOpenSource/smart-doc/pull/811))

## [3.0.4](https://github.com/TongchengOpenSource/smart-doc/compare/3.0.3...3.0.4) - 2024-04-27

Maven coordinates: `com.ly.smart-doc:smart-doc:3.0.4`

### Added

- feat: Support for JMeter Prometheus Listener ([#779](https://github.com/TongchengOpenSource/smart-doc/pull/779))
- feat: Optimize the names generated by JMeter scripts ([#781](https://github.com/TongchengOpenSource/smart-doc/pull/781))
- feat: Optimize JMeter config ([#783](https://github.com/TongchengOpenSource/smart-doc/pull/783))
- feat: Optimize Duplicate Code ([#784](https://github.com/TongchengOpenSource/smart-doc/pull/784))

### Fixed

- fix : Result<<Page<User>>> fields explain failed ([#777](https://github.com/TongchengOpenSource/smart-doc/pull/777))

### Other

- Import enum class, comment @see after writing only the class name can not find the corresponding enum ([#785](https://github.com/TongchengOpenSource/smart-doc/pull/785))

## [3.0.3](https://github.com/TongchengOpenSource/smart-doc/compare/3.0.2...3.0.3) - 2024-03-30

Maven coordinates: `com.ly.smart-doc:smart-doc:3.0.3`

### Added

- 新功能：：sparkles: 添加 WebSocket 支持并更新许可证有效期。[#752](https://github.com/TongchengOpenSource/smart-doc/pull/752)
- 新功能：优化 JSR 验证信息的提取。[#755](https://github.com/TongchengOpenSource/smart-doc/pull/755)
- 新功能：支持 RPC 中多级接口继承。[#773](https://github.com/TongchengOpenSource/smart-doc/pull/773)
- 新功能：在添加自定义注解到参数后，忽略集合参数检查。[#774](https://github.com/TongchengOpenSource/smart-doc/pull/774)
- 新功能：忽略集合参数检查。[#775](https://github.com/TongchengOpenSource/smart-doc/pull/775)

### Changed

- 优化 Javadoc 标签使用错误的提示。[#767](https://github.com/TongchengOpenSource/smart-doc/pull/767)

### Fixed

- 修复 #746：为枚举值添加分隔符。[#754](https://github.com/TongchengOpenSource/smart-doc/pull/754)
- 修复泛型 typeName 或 returnType 的错误。[#761](https://github.com/TongchengOpenSource/smart-doc/pull/761)
- 修复 Solon 框架中的非接口文档问题。[#765](https://github.com/TongchengOpenSource/smart-doc/pull/765)
- 修复 Javadoc 标签使用错误的提示。[#767](https://github.com/TongchengOpenSource/smart-doc/pull/767)
- 修复 Solon 框架中非接口文档的问题。[#771](https://github.com/TongchengOpenSource/smart-doc/pull/771)
- 更新：修复 null 参数值中多余的双引号问题。[#772](https://github.com/TongchengOpenSource/smart-doc/pull/772)

### Other

- 在参数中添加扩展以支持 openapi..[#759](https://github.com/TongchengOpenSource/smart-doc/pull/759)
- Solon 已弃用 PathVar 并替换为 Path。[#765](https://github.com/TongchengOpenSource/smart-doc/pull/765)

## [3.0.2](https://github.com/TongchengOpenSource/smart-doc/compare/3.0.1...3.0.2) - 2024-02-26

Maven coordinates: `com.ly.smart-doc:smart-doc:3.0.2`

### Added

- 新增类级别的接口排除过滤。[#736](https://github.com/TongchengOpenSource/smart-doc/pull/736)

### Changed

- 优化推送rpc文档到torna时smart-doc设置的框架名错误，[#690](https://github.com/TongchengOpenSource/smart-doc/issues/690)
- 支持分批上传文档到torna[#697](https://github.com/TongchengOpenSource/smart-doc/issues/697)
- 支持提取java record的注释[#703](https://github.com/TongchengOpenSource/smart-doc/pull/703)
- 优化第三方库枚举缺失导致生成文档被中断的问题[#707](https://github.com/TongchengOpenSource/smart-doc/pull/707)
- 重构优化word模版，生成的文档更漂亮 [#710](https://github.com/TongchengOpenSource/smart-doc/pull/710)
- 支持@JsonSerialize(using = ToStringSerializer.class)字段类型的处理。 [#715](https://github.com/TongchengOpenSource/smart-doc/pull/715)
- 优化常量获取，兼容更多用户可以在路径中使用各种常量。[#730](https://github.com/TongchengOpenSource/smart-doc/pull/730)

### Fixed

- 修复html文档搜索后链接错误的bug，[#693](https://github.com/TongchengOpenSource/smart-doc/pull/693)
- 修复mock中值双引号被删除的问题[#680](https://github.com/TongchengOpenSource/smart-doc/issues/680)
- 修复报错信息错误[#706](https://github.com/TongchengOpenSource/smart-doc/pull/706)
- 修复创建word文档错误的问题[#711](https://github.com/TongchengOpenSource/smart-doc/pull/711)
- 修复html特殊元素导致生成的jmeter无法打开的bug [#713](https://github.com/TongchengOpenSource/smart-doc/pull/713)
- 修复openApi生成的tags与paths未对packageFilters进行过滤。 [#716](https://github.com/TongchengOpenSource/smart-doc/pull/716)
- 修复项目采用git初始化未提交时生成文档报错bug。 [#719](https://github.com/TongchengOpenSource/smart-doc/issues/719)
- 修复gradle插件codePath未设置导致使用报错的问题。 [gradle #15](https://github.com/TongchengOpenSource/smart-doc-gradle-plugin/pull/15)
- 修复内部枚举类作为参数时无法正常解析的问题。[#728](https://github.com/TongchengOpenSource/smart-doc/pull/728)
- 修复void返回时返回用例生成错误。[#726](https://github.com/TongchengOpenSource/smart-doc/pull/726)

### Other

- 忽略NoClassDefFoundError错误，避免第三方代码存在问题时影响文档生成。 [#724](https://github.com/TongchengOpenSource/smart-doc/issues/724)
- 修改高版本smart-doc生成JAX-RS规范项目文档报错问题。[#729](https://github.com/TongchengOpenSource/smart-doc/pull/729)
- 修改JAX-RS绑定集合入参报错问题。 [#735](https://github.com/TongchengOpenSource/smart-doc/pull/735)
- openapi枚举类型增加取值范围展示[#741](https://github.com/TongchengOpenSource/smart-doc/pull/741)

## [3.0.1](https://github.com/TongchengOpenSource/smart-doc/compare/3.0.0...3.0.1) - 2023-12-17

Maven coordinates: `com.ly.smart-doc:smart-doc:3.0.1`

### Changed

- 优化Dubbo文档设置，[#649](https://github.com/TongchengOpenSource/smart-doc/issues/649)
- 优化Dubbo文档中字段类型展示，[#648](https://github.com/TongchengOpenSource/smart-doc/issues/648)
- 支持获取接口上RequestMapping[#681](https://github.com/TongchengOpenSource/smart-doc/issues/681)
- 优化html模版页面url策略方便分享[#658](https://github.com/TongchengOpenSource/smart-doc/issues/658)
- 支持jmeter性能测试[#653](https://github.com/TongchengOpenSource/smart-doc/issues/653)
- 支持导入文档到word中[#686](https://github.com/TongchengOpenSource/smart-doc/pull/686)
- 支持多路由场景下的openapi生成[#679](https://github.com/TongchengOpenSource/smart-doc/pull/679)
- 支持基于git管理的增量文档构建[#657](https://github.com/TongchengOpenSource/smart-doc/pull/657)

### Fixed

- 修复componentType配置为NORMAL失效的问题[#665](https://github.com/TongchengOpenSource/smart-doc/pull/665)

### Other

- 兼容swagger的tag [#674](https://github.com/TongchengOpenSource/smart-doc/pull/674)

## [3.0.0](https://github.com/TongchengOpenSource/smart-doc/compare/2.7.7...3.0.0) - 2023-10-20

Maven coordinates: `com.ly.smart-doc:smart-doc:3.0.0`

### Breaking changes

- **破坏性变更 / BREAKING**: Maven groupId 从 `com.github.shalousun` 变更为 `com.ly.smart-doc`（项目捐赠给同程旅行开源；坐标变更见 [c510f45c](https://github.com/TongchengOpenSource/smart-doc/commit/c510f45ce932e077ea6b0875609b7e2cea806b3a)）。从 2.x 升级时必须同步修改依赖的 groupId。

### Changed

- 支持获取接口的注释，[#573](https://github.com/TongchengOpenSource/smart-doc/issues/573)

### Fixed

- 修复Java 16+版本反射获取字段是jdk中的类时异常。 [#615](https://github.com/TongchengOpenSource/smart-doc/issues/615)
- 修复枚举字典显示错乱问题，维持代码中编写的顺序。[#603](https://github.com/TongchengOpenSource/smart-doc/issues/603)
- 修复生成postman collection后导入到postman缺少file文件参数问题。[#622](https://github.com/TongchengOpenSource/smart-doc/issues/622)
- 修复配置ResponseBodyAdvice后torna推送数据错误问题。[#599](https://github.com/TongchengOpenSource/smart-doc/issues/599)
- 修复customResponseFields无法忽略字段的bug。 [#625](https://github.com/TongchengOpenSource/smart-doc/issues/625)
- 修复请求参数标记@Null注解时字段没被忽略的bug。[#608](https://github.com/TongchengOpenSource/smart-doc/issues/608)

### Other

- 生成html页面支持多文件上传。[#594](https://github.com/TongchengOpenSource/smart-doc/issues/594)
- 无参请求生成时生成的OpenAPI文档错误。[#616](https://github.com/TongchengOpenSource/smart-doc/issues/616)
- 扫描文档时丢弃对JDK原生类型的反射，避免高版本JDK反射时报错。[pr#621](https://github.com/TongchengOpenSource/smart-doc/pull/621)

## [2.7.7](https://github.com/TongchengOpenSource/smart-doc/compare/2.7.6...2.7.7) - 2023-09-11

Maven coordinates: `com.github.shalousun:smart-doc:2.7.7`

### Other

- fix 2.7.6版本文档丢字段的bug.

## [2.7.6](https://github.com/TongchengOpenSource/smart-doc/compare/2.7.5...2.7.6) - 2023-09-03

Maven coordinates: `com.github.shalousun:smart-doc:2.7.6`

### Changed

- 优化smart-doc常量获取，支持静态导入的常量获取。[qdox #1](https://github.com/TongchengOpenSource/smart-doc/issues/1)

### Fixed

- 修复openapi生成summary和description设置，[#580](https://github.com/TongchengOpenSource/smart-doc/issues/580)
- 修复生成的openapi.json中接口的路径参数赋予format错误,[#578](https://github.com/TongchengOpenSource/smart-doc/issues/578)

### Other

- 无源代码第三方接口范型方法的解析支持，[pr#587](https://github.com/TongchengOpenSource/smart-doc/pull/587)
- 生成Markdown代码块添加语言类型支持，[#566](https://github.com/TongchengOpenSource/smart-doc/issues/566)

## [2.7.5](https://github.com/TongchengOpenSource/smart-doc/compare/2.7.4...2.7.5) - 2023-08-20

Maven coordinates: `com.github.shalousun:smart-doc:2.7.5`

### Other

- fix内部类嵌套泛型解析异常，[#561](https://github.com/TongchengOpenSource/smart-doc/issues/561)

## [2.7.4](https://github.com/TongchengOpenSource/smart-doc/compare/2.7.3...2.7.4) - 2023-08-01

Maven coordinates: `com.github.shalousun:smart-doc:2.7.4`

### Fixed

- 修复torna-rpc推送检查outpath错误，[#556](https://github.com/TongchengOpenSource/smart-doc/issues/556)

### Other

- 完善torna version推送 [pr#557](https://github.com/TongchengOpenSource/smart-doc/pull/557)

## [2.7.3](https://github.com/TongchengOpenSource/smart-doc/compare/2.7.2...2.7.3) - 2023-07-20

Maven coordinates: `com.github.shalousun:smart-doc:2.7.3`

### Fixed

- 修复第三方依赖无源代码下的范型解析bug，[#539](https://github.com/TongchengOpenSource/smart-doc/issues/539)

## [2.7.2](https://github.com/TongchengOpenSource/smart-doc/compare/2.7.1...2.7.2) - 2023-07-09

Maven coordinates: `com.github.shalousun:smart-doc:2.7.2`

### Changed

- 支持无源代码下的范型解析，[#539](https://github.com/TongchengOpenSource/smart-doc/issues/539)

### Fixed

- 修复[2.6.9, 2.7.1]版本dubbo rpc重复生成接口文档bug。[#531](https://github.com/TongchengOpenSource/smart-doc/issues/531)

### Other

- 修改不通类方法接口同名时归档错误问题，[#534](https://github.com/TongchengOpenSource/smart-doc/issues/534)
- torna增加version推送，[#535](https://github.com/TongchengOpenSource/smart-doc/issues/535)
- 删除自定义@required tag，@ignore tag不在支持字段忽略。使用jsr规则替代。

## [2.7.1](https://github.com/TongchengOpenSource/smart-doc/compare/2.7.0...2.7.1) - 2023-06-23

Maven coordinates: `com.github.shalousun:smart-doc:2.7.1`

### Fixed

- 修复Spring各个Mapping注解的name解析使用错误，[#518](https://github.com/TongchengOpenSource/smart-doc/issues/518)

### Other

- packageFilters 参数支持到方法级别。[#519](https://github.com/TongchengOpenSource/smart-doc/issues/519)

## [2.7.0](https://github.com/TongchengOpenSource/smart-doc/compare/2.6.9...2.7.0) - 2023-06-10

Maven coordinates: `com.github.shalousun:smart-doc:2.7.0`

### Changed

- 支持在子类使用@JsonIgnore忽略后，同时忽略父类中的同名字段，[#509](https://github.com/TongchengOpenSource/smart-doc/issues/509)

### Fixed

- 修复内部类嵌套结构formdata example错误。[pr#512](https://github.com/TongchengOpenSource/smart-doc/pull/512)
- 修复url中使用环境变量表达式时解析错误，[#515](https://github.com/TongchengOpenSource/smart-doc/issues/515)

## [2.6.9](https://github.com/TongchengOpenSource/smart-doc/compare/2.6.8...2.6.9) - 2023-05-14

Maven coordinates: `com.github.shalousun:smart-doc:2.6.9`

### Changed

- 优化插件对许多无关依赖的过滤，提升插件开箱即用的能力。
- 支持禁用随机mock值生成，[#497](https://github.com/TongchengOpenSource/smart-doc/issues/497)

### Fixed

- 修复qdox不允许将字段名命名为permits关键字的bug，[#498](https://github.com/TongchengOpenSource/smart-doc/issues/498)
- dubbo接口继承父类时，父类中接口未解析问题修复。[#493](https://github.com/TongchengOpenSource/smart-doc/issues/493)

## [2.6.8](https://github.com/TongchengOpenSource/smart-doc/compare/2.6.7...2.6.8) - 2023-05-02

Maven coordinates: `com.github.shalousun:smart-doc:2.6.8`

### Changed

- 支持扩展对DocJavaMethod进行数据自定义处理，[#488](https://github.com/TongchengOpenSource/smart-doc/issues/488)
- 支持gson别名解析。 [#485](https://github.com/TongchengOpenSource/smart-doc/pull/485)
- 支持接口中使用多个@RequestBody。 [#pr475](https://github.com/TongchengOpenSource/smart-doc/pull/475)
- 升级jquery，[#pr474](https://github.com/TongchengOpenSource/smart-doc/pull/474)

### Fixed

- 修复mock值中使用cron表达式后转义值问题。[#487](https://github.com/TongchengOpenSource/smart-doc/issues/487)
- 修复无法获取多层继承的父类接口bug。[#480](https://github.com/TongchengOpenSource/smart-doc/issues/480)
- 修复file字段required设置，[#pr470](https://github.com/TongchengOpenSource/smart-doc/pull/470)
- 修复多path错误，[#466](https://github.com/TongchengOpenSource/smart-doc/issues/466)

## [2.6.7](https://github.com/TongchengOpenSource/smart-doc/compare/2.6.6...2.6.7) - 2023-03-28

Maven coordinates: `com.github.shalousun:smart-doc:2.6.7`

### Fixed

- 修复JAX-RS Produces注解使用时contentType为空的bug，[#415](https://github.com/TongchengOpenSource/smart-doc/issues/415)
- 修复不能忽略被继承类中相应字段bug。[#453](https://github.com/TongchengOpenSource/smart-doc/issues/453)
- 修复使用@see tag时的未写注释导致空指针问腿。[#451](https://github.com/TongchengOpenSource/smart-doc/issues/451)
- 修复openapi的生成bug。[#458](https://github.com/TongchengOpenSource/smart-doc/issues/458)
- 修复openapi无法归类的问题。 [#pr460](https://github.com/TongchengOpenSource/smart-doc/pull/460)

## [2.6.6](https://github.com/TongchengOpenSource/smart-doc/compare/2.6.5...2.6.6) - 2023-03-08

Maven coordinates: `com.github.shalousun:smart-doc:2.6.6`

### Fixed

- 修复集合或者数组入参推送torna数据错误，[#415](https://github.com/TongchengOpenSource/smart-doc/issues/415)

### Other

- 请求注解consumes支持。[#424](https://github.com/TongchengOpenSource/smart-doc/issues/424)
- url中path param占位符解析优化。[#447](https://github.com/TongchengOpenSource/smart-doc/issues/447)

## [2.6.5](https://github.com/TongchengOpenSource/smart-doc/compare/2.6.4...2.6.5) - 2023-02-27

Maven coordinates: `com.github.shalousun:smart-doc:2.6.5`

### Changed

- 优化自定义字典值处理接口DictionaryValuesResolver[#pr428](https://github.com/TongchengOpenSource/smart-doc/pull/428)

### Fixed

- 修复集合或者数组入参推送torna数据错误，[#415](https://github.com/TongchengOpenSource/smart-doc/issues/415)
- 修复设置requestFieldToUnderline为true时path参数丢失，[#103](https://github.com/TongchengOpenSource/smart-doc/issues/103)
- 修复HTML模版文档的描述展示[#pr438](https://github.com/TongchengOpenSource/smart-doc/pull/438)

### Other

- 文档泛型支持数组类型标注。[#421](https://github.com/TongchengOpenSource/smart-doc/issues/421)
- list<Primitive Type> 和list<File> 类型openapi生成错误。[#423](https://github.com/TongchengOpenSource/smart-doc/issues/423)

## [2.6.4](https://github.com/TongchengOpenSource/smart-doc/compare/2.6.3...2.6.4) - 2023-01-28

Maven coordinates: `com.github.shalousun:smart-doc:2.6.4`

### Changed

- 支持没有getter方法的枚举字典提取，[#408](https://github.com/TongchengOpenSource/smart-doc/issues/408)
- 优化插件遇到源码语法错误时的报错提示。[#402](https://github.com/TongchengOpenSource/smart-doc/issues/402)
- 支持SpringBoot 3.0和Jakarta EE 10.
- 优化插件对常用无关文档依赖做加载优化。
- 支持controller继承接口中的注解[#pr392](https://github.com/TongchengOpenSource/smart-doc/pull/392)。

### Other

- URL 结尾存在多余'/' 。[#396](https://github.com/TongchengOpenSource/smart-doc/issues/396)
- 二维数据推送torna类型错误，[#380](https://github.com/TongchengOpenSource/smart-doc/issues/380)

## [2.6.3](https://github.com/TongchengOpenSource/smart-doc/compare/2.6.2...2.6.3) - 2022-12-10

Maven coordinates: `com.github.shalousun:smart-doc:2.6.3`

### Changed

- 支持SpringBoot 3.0和Jakarta EE 10.
- 优化插件对常用无关文档依赖做加载优化。
- 支持controller继承接口中的注解[#pr392](https://github.com/TongchengOpenSource/smart-doc/pull/392)。

### Other

- 单个接口推送到torna目录显示优化，[#385](https://github.com/TongchengOpenSource/smart-doc/pull/385)
- 修改timestamp类型在文档中显示错误。
- 修改枚举重复问题。
- 二维数据推送torna类型错误，[#380](https://github.com/TongchengOpenSource/smart-doc/issues/380)

## [2.6.2](https://github.com/TongchengOpenSource/smart-doc/compare/2.6.1...2.6.2) - 2022-10-29

Maven coordinates: `com.github.shalousun:smart-doc:2.6.2`

### Changed

- 优化openapi文档生成

### Fixed

- 修复枚举渲染换行问题，[#377](https://github.com/TongchengOpenSource/smart-doc/issues/377)

### Other

- 文档url显示错误，[`#379`](https://github.com/TongchengOpenSource/smart-doc/commit/57e9aff2ac69a14000cf249be54d776843f250f7)

## [2.6.1](https://github.com/TongchengOpenSource/smart-doc/compare/2.6.0...2.6.1) - 2022-10-23

Maven coordinates: `com.github.shalousun:smart-doc:2.6.1`

### Fixed

- 修复枚举渲染和rpc文档字段类型，[#377](https://github.com/TongchengOpenSource/smart-doc/issues/377)

### Other

- markdown文档字段描述填充错误，[`#378`](https://github.com/TongchengOpenSource/smart-doc/commit/21bd14e8516701a4ac6accc70d2844ac394645d0)
- 格式化markdown模板错误

## [2.6.0](https://github.com/TongchengOpenSource/smart-doc/compare/2.5.3...2.6.0) - 2022-10-15

Maven coordinates: `com.github.shalousun:smart-doc:2.6.0`

### Changed

- 支持推送@since到torna的注释中，[#344](https://github.com/TongchengOpenSource/smart-doc/issues/344)
- 优化到大量的代码，和2.5.3对比变化非常大。

### Fixed

- 修复markdown从出现换行错误导致渲染混乱，[#17](https://github.com/TongchengOpenSource/smart-doc-maven-plugin/issues/17)
- 修复jax-rs方法上绑定集合解析错误，[#358](https://github.com/TongchengOpenSource/smart-doc/issues/358)
- 修复customResponseFields配置无效错误，[#355](https://github.com/TongchengOpenSource/smart-doc/issues/355)
- 修复常量解析private字段被解析是发生异常问题，[#356](https://github.com/TongchengOpenSource/smart-doc/issues/356)

### Other

- Post请求中文参数编码错误，[#19](https://github.com/TongchengOpenSource/smart-doc-maven-plugin/issues/19)
- spring mvc mapping method数组书写方式解析错误，[#361](https://github.com/TongchengOpenSource/smart-doc/issues/361)
- 对Jackson注解@JsonNaming驼峰转下划线的支持,[#349](https://github.com/TongchengOpenSource/smart-doc/issues/349)
- curl请求用例显示混乱。
- RequestMapping中params解析出错,[#374](https://github.com/TongchengOpenSource/smart-doc/issues/374)
- 自循环依赖对象字段显示优化，[#376](https://github.com/TongchengOpenSource/smart-doc/issues/376)
- maven 插件是否能考虑支持appToken，[#354](https://github.com/TongchengOpenSource/smart-doc/issues/354)

## [2.5.3](https://github.com/TongchengOpenSource/smart-doc/compare/2.5.2...2.5.3) - 2022-09-10

Maven coordinates: `com.github.shalousun:smart-doc:2.5.3`

### Changed

- 支持推送@since到torna的注释中，[#344](https://github.com/TongchengOpenSource/smart-doc/issues/344)

### Other

- 使用@see注释内容不是枚举类名运行错误，[#340](https://github.com/TongchengOpenSource/smart-doc/issues/340)
- jsr校验注解支持常量替换，[#334](https://github.com/TongchengOpenSource/smart-doc/issues/334)

## [2.5.2](https://github.com/TongchengOpenSource/smart-doc/compare/2.5.1...2.5.2) - 2022-09-02

Maven coordinates: `com.github.shalousun:smart-doc:2.5.2`

### Changed

- 支持错误码自定义解析器。[#338](https://github.com/TongchengOpenSource/smart-doc/issues/338)

### Fixed

- 修复query参数推送到torna被设置到body中，[#336](https://github.com/TongchengOpenSource/smart-doc/issues/336)
- 修复openapi表单对象请求参数丢失。

### Other

- 请求头参数支持mock值设置，[#337](https://github.com/TongchengOpenSource/smart-doc/issues/337)
- 修改字段@see 非枚举时错误，[#340](https://github.com/TongchengOpenSource/smart-doc/issues/340)

## [2.5.1](https://github.com/TongchengOpenSource/smart-doc/compare/2.5.0...2.5.1) - 2022-08-27

Maven coordinates: `com.github.shalousun:smart-doc:2.5.1`

### Changed

- 升级qdox，解决字段名使用record报错问题，[#231](https://github.com/TongchengOpenSource/smart-doc/issues/231)
- 优化枚举在文档中的展示[#332](https://github.com/TongchengOpenSource/smart-doc/issues/332)

### Fixed

- 修复list参数入参样例错误
- 修复2.5.0不支持jdk 8的问题

### Other

- 修改date类型文档显示错误
- html特殊字符增加转义处理，解决html文档显示问题[#333](https://github.com/TongchengOpenSource/smart-doc/issues/333)

## [2.5.0](https://github.com/TongchengOpenSource/smart-doc/compare/2.4.9...2.5.0) - 2022-08-20

Maven coordinates: `com.github.shalousun:smart-doc:2.5.0`

### Changed

- 支持更多jdk 8时间类型[#315](https://github.com/TongchengOpenSource/smart-doc/issues/315)
- 支持jsr验证规则提取[#267](https://github.com/TongchengOpenSource/smart-doc/issues/267)

### Fixed

- 修复使用@see指向枚举类不存在时空指针问题，改成提示性异常处理，帮助用户可以定位问题，[#331](https://github.com/TongchengOpenSource/smart-doc/issues/331)

## [2.4.9](https://github.com/TongchengOpenSource/smart-doc/compare/2.4.8...2.4.9) - 2022-08-07

Maven coordinates: `com.github.shalousun:smart-doc:2.4.9`

### Changed

- 支持使用@see指向枚举类，[#306](https://github.com/TongchengOpenSource/smart-doc/issues/306)

### Fixed

- 修复枚举请求的用例展示错误

## [2.4.8](https://github.com/TongchengOpenSource/smart-doc/compare/2.4.7...2.4.8) - 2022-07-09

Maven coordinates: `com.github.shalousun:smart-doc:2.4.8`

### Added

- 新增对JAX-RS @PATCH、@HEAD 的支持[#pr303](https://github.com/TongchengOpenSource/smart-doc/pull/303)

### Changed

- 支持servlet 3.0 文件上传[#294](https://github.com/TongchengOpenSource/smart-doc/issues/294)

### Fixed

- 修复表单复杂对象嵌套Request-example错误问题[#284](https://github.com/TongchengOpenSource/smart-doc/issues/284)
- 修复注释换行导致html文档链接无法点击问题[#290](https://github.com/TongchengOpenSource/smart-doc/issues/290)
- 修复实现接口的枚举作为字段时生成文档报错,[#292](https://github.com/TongchengOpenSource/smart-doc/issues/292)
- 修复Delete Option json请求没有request body的问题[#300](https://github.com/TongchengOpenSource/smart-doc/issues/300)
- 修复OpenAPI导出一级菜单未使用注释名的bug,[#296](https://github.com/TongchengOpenSource/smart-doc/issues/296)

### Other

- 自增serverEnv配置用户支持在postman中设置服务器地址变量，[#280](https://github.com/TongchengOpenSource/smart-doc/issues/280)

## [2.4.7](https://github.com/TongchengOpenSource/smart-doc/compare/2.4.6...2.4.7) - 2022-06-13

Maven coordinates: `com.github.shalousun:smart-doc:2.4.7`

### Fixed

- 修复2.4.6版本使用高版本jdk出现module权限的问题
- 修复bug[#283](https://github.com/TongchengOpenSource/smart-doc/issues/283)

## [2.4.6](https://github.com/TongchengOpenSource/smart-doc/compare/2.4.4...2.4.6) - 2022-05-29

Maven coordinates: `com.github.shalousun:smart-doc:2.4.6`

### Added

- 新增枚举字典扫描，[#264](https://github.com/TongchengOpenSource/smart-doc/issues/264)

### Other

- BigDecimal类型输出优化，[#268](https://github.com/TongchengOpenSource/smart-doc/issues/268)
- 对不规范的mock注释增加检查，[#262](https://github.com/TongchengOpenSource/smart-doc/issues/262)

## 2.4.5 - 2022-05-10

Maven coordinates: `com.github.shalousun:smart-doc:2.4.5`

### Fixed

- 修复部分代码空指针问题，加固健壮性
- 修复curl example错误问题，改为方法名[#256](https://github.com/TongchengOpenSource/smart-doc/issues/256)

## [2.4.4](https://github.com/TongchengOpenSource/smart-doc/compare/2.4.3...2.4.4) - 2022-05-03

Maven coordinates: `com.github.shalousun:smart-doc:2.4.4`

### Changed

- 优化对mongodb ObjectId类型的解析[#240](https://github.com/TongchengOpenSource/smart-doc/issues/240)
- 优化OpenAPI生成时operationId值的填充，改为方法名[#235](https://github.com/TongchengOpenSource/smart-doc/issues/235)
- 支持在smart-doc.json配置中对@RequestHeader进行忽略了[#250](https://github.com/TongchengOpenSource/smart-doc/issues/250)
- 支持内部类枚举私有属性解析。

### Fixed

- 修复请求参数为Long类型数组时自定义mock提取错误[#244](https://github.com/TongchengOpenSource/smart-doc/issues/244)
- 修复文档说明生成输出多个br标签[#248](https://github.com/TongchengOpenSource/smart-doc/issues/248)
- 修复query param参数显示在Request-body中的问题[#242](https://github.com/TongchengOpenSource/smart-doc/issues/242)
- 修复Controller类上RequestMapping多path包含parameter时的解析错误[#206](https://github.com/TongchengOpenSource/smart-doc/issues/206)
- 修复多文件上传，推送到torna的类型错误[#234](https://github.com/TongchengOpenSource/smart-doc/issues/234)
- 修复分组验证在OpenAPI中不生效问题[#243](https://github.com/TongchengOpenSource/smart-doc/issues/243)
- 修复OpenAPI数据类型设置错误[#253](https://github.com/TongchengOpenSource/smart-doc/issues/253)
- 修复controller注释html文档导航链接无效的问题[#255](https://github.com/TongchengOpenSource/smart-doc/issues/255)

### Other

- 移除Spring标记过时的`application/json;charset=UTF-8`，默认改为`application/json`

## [2.4.3](https://github.com/TongchengOpenSource/smart-doc/compare/2.4.2...2.4.3) - 2022-04-17

Maven coordinates: `com.github.shalousun:smart-doc:2.4.3`

### Changed

- 支持生成openapi时不生成请求和返回用例[#233](https://github.com/TongchengOpenSource/smart-doc/issues/233)
- 优化对map返回结构的解析[#223](https://github.com/TongchengOpenSource/smart-doc/issues/223)

### Fixed

- 修复生成openapi的时候对contentType的处理转义[#232](https://github.com/TongchengOpenSource/smart-doc/issues/232)

### Other

- 修改分组归档处理[#226](https://github.com/TongchengOpenSource/smart-doc/issues/226)

## [2.4.2](https://github.com/TongchengOpenSource/smart-doc/compare/2.4.1...2.4.2) - 2022-04-04

Maven coordinates: `com.github.shalousun:smart-doc:2.4.2`

### Added

- 新增header自动常量解析。

### Changed

- 优化对文档中特殊大于和小于符号的处理。

### Other

- 修改2.4.1静态常量自动解析不完善的问题。
- 修改某些特殊情况下泛型继承解析问题[#215](https://github.com/TongchengOpenSource/smart-doc/issues/215)

## [2.4.1](https://github.com/TongchengOpenSource/smart-doc/compare/2.4.0...2.4.1) - 2022-03-26

Maven coordinates: `com.github.shalousun:smart-doc:2.4.1`

### Added

- customResponseFields新增replaceName配置支持替换字段名。

### Changed

- 支持静态常量自动解析，无需再使用apiConstants来配置。

### Fixed

- 修复获取非@param注释tag值错误bug。
- 修复dubbo rpc

## [2.4.0](https://github.com/TongchengOpenSource/smart-doc/compare/2.3.9...2.4.0) - 2022-03-06

Maven coordinates: `com.github.shalousun:smart-doc:2.4.0`

### Changed

- 支持字段多泛型嵌套private B<K,V> body;的解析

## [2.3.9](https://github.com/TongchengOpenSource/smart-doc/compare/2.3.8...2.3.9) - 2022-02-26

Maven coordinates: `com.github.shalousun:smart-doc:2.3.9`

### Changed

- 支持推送已过时的接口标记到torna。
- 支持solon，https://gitee.com/noear/solon

### Other

- rpc mock完善。
- dubbo接口文档支持导出字典。

## [2.3.8](https://github.com/TongchengOpenSource/smart-doc/compare/2.3.7...2.3.8) - 2022-02-19

Maven coordinates: `com.github.shalousun:smart-doc:2.3.8`

### Changed

- 支持多层继承解析。
- 优化生成postman post请求文档。

## [2.3.7](https://github.com/TongchengOpenSource/smart-doc/compare/2.3.6...2.3.7) - 2022-01-17

Maven coordinates: `com.github.shalousun:smart-doc:2.3.7`

### Changed

- 支持JAX-RS。
- 优化对项目文件的加载。

### Other

- 解决文件数据未获取到是数据错误的bug。

## [2.3.6](https://github.com/TongchengOpenSource/smart-doc/compare/2.3.5...2.3.6) - 2022-01-02

Maven coordinates: `com.github.shalousun:smart-doc:2.3.6`

### Other

- 导出postman时serverUrl不再强制要求设置协议。
- 修改设置responseBodyAdvice后推送torna数据错误。
- 解决gradle插件不能在kts脚本中添加include和exclude的问题。

## [2.3.5](https://github.com/TongchengOpenSource/smart-doc/compare/2.3.4...2.3.5) - 2021-12-18

Maven coordinates: `com.github.shalousun:smart-doc:2.3.5`

### Fixed

- 修复接口常量被当做字段的问题。

### Other

- 修改author名称多余的双引号。
- 增强插件对旧版本依赖jar的过滤，防止新版本qdox解析错误。

## [2.3.4](https://github.com/TongchengOpenSource/smart-doc/compare/2.3.3...2.3.4) - 2021-12-05

Maven coordinates: `com.github.shalousun:smart-doc:2.3.4`

### Fixed

- 修复将@NotEmpty放到字段最前面，后面其他注解不会被分析的bug。

### Other

- 插件增强对projectName设置。
- 增强插件对旧版本依赖jar的过滤，防止新版本qdox解析错误。

## [2.3.3](https://github.com/TongchengOpenSource/smart-doc/compare/2.3.2...2.3.3) - 2021-11-29

Maven coordinates: `com.github.shalousun:smart-doc:2.3.3`

### Changed

- 支持OffsetDateTime。

### Fixed

- 修复html文档搜索清空后目录显示不全的bug。
- 修复非分组使用时html文档目录序号错乱问题。
- 修复英文请求头转义问题。

## [2.3.2](https://github.com/TongchengOpenSource/smart-doc/compare/2.3.1...2.3.2) - 2021-11-21

Maven coordinates: `com.github.shalousun:smart-doc:2.3.2`

### Added

- 新增对@RequestAttribute参数的忽略。

### Changed

- 优化泛型类型显示，当泛型类型的实参类型是基本类型时直接显示为基本类型。

### Fixed

- 修复debug调试页搜索后目录锚点错误。
- 修复生成openapi时List<String>等基本类型数组入参时类型转换错误。
- 修复@RequstBody使用基本类型时的请求参数示例错误

## [2.3.1](https://github.com/TongchengOpenSource/smart-doc/compare/2.3.0...2.3.1) - 2021-11-13

Maven coordinates: `com.github.shalousun:smart-doc:2.3.1`

### Added

- 新增对@SessionAttribute参数做忽略。

### Changed

- 支持controller实现接口，并使用default方法。

### Fixed

- 修复debug调试不支持请求头设置中文值的bug。
- 修复response自定义tag设置返回未格式化换行问题。
- 修复枚举类型字段指定mock不生效的问题。

## [2.3.0](https://github.com/TongchengOpenSource/smart-doc/compare/2.2.9...2.3.0) - 2021-11-07

Maven coordinates: `com.github.shalousun:smart-doc:2.3.0`

### Changed

- 优化一些部分常见字段的随机值生成。

### Fixed

- 修复创建html文档丢失search.js文件问题。
- 修复packageFilters配置多个包时只有第一个生效的问题。
- 修复debug调试页面的curl指令错误。
- 修复设置了下载文件接口加download标记后，数据并未生效的bug。

## [2.2.9](https://github.com/TongchengOpenSource/smart-doc/compare/2.2.8...2.2.9) - 2021-10-31

Maven coordinates: `com.github.shalousun:smart-doc:2.2.9`

### Changed

- 优化outPath配置，只使用torna推送时可以不再要求配置outPath项。

### Fixed

- 修复生成openapi文件上传错误问题。
- 修复文件上传接口推送到torna参数被放置到query参数列表的问题。

### Other

- 修改List<T>类型参数推送到torna错误的问题

## [2.2.8](https://github.com/TongchengOpenSource/smart-doc/compare/2.2.7...2.2.8) - 2021-10-07

Maven coordinates: `com.github.shalousun:smart-doc:2.2.8`

### Fixed

- 修复html文档无接口注释时锚点跳转错误的问题。
- 修复导出postman时服务端口配置成变量报错的问题。

## [2.2.7](https://github.com/TongchengOpenSource/smart-doc/compare/2.2.6...2.2.7) - 2021-09-12

Maven coordinates: `com.github.shalousun:smart-doc:2.2.7`

### Fixed

- 修复dubbo文档css连接错误。
- 修复分组后组归错误。
- 修复路径常量相似度时替换错误的bug。

### Other

- JSR303分组优化，标记Null的分组字段将不再显示在文档中 。

## [2.2.6](https://github.com/TongchengOpenSource/smart-doc/compare/2.2.5...2.2.6) - 2021-09-05

Maven coordinates: `com.github.shalousun:smart-doc:2.2.6`

### Changed

- 优化maven插件提示 。

### Fixed

- 修复html文档静态资源链接错误。
- 修复分组后目录item搜索错误。

### Other

- 不配置分组时不显示分组。

## [2.2.5](https://github.com/TongchengOpenSource/smart-doc/compare/2.2.4...2.2.5) - 2021-08-08

Maven coordinates: `com.github.shalousun:smart-doc:2.2.5`

### Added

- 新增接口分组支持。

### Changed

- 支持在html文档中不显示参数列表。

### Fixed

- 修复Boolean类型字段命名为is前缀时，is前缀被去除的bug。
- 修复配置了字典码列表后枚举字段注释显示错误[#139](https://github.com/TongchengOpenSource/smart-doc/issues/139)
- 修复分析过程中出现的数组越界异常 。

### Other

- html文档使用的资源全部改成本地引用。

## [2.2.4](https://github.com/TongchengOpenSource/smart-doc/compare/2.2.3...2.2.4) - 2021-08-08

Maven coordinates: `com.github.shalousun:smart-doc:2.2.4`

### Added

- 新增jsr303 @size和@length支持。
- 新增isReplace配置 。

### Fixed

- 修复字典码推送torna错误 #https://gitee.com/smart-doc-team/smart-doc/issues/I43JQR。
- 修复postman错误#I41G2E 。
- 修复当存在多个jsr注解时，部分注解失效问题。

### Other

- 修改html的模板样式错误。

## [2.2.3](https://github.com/TongchengOpenSource/smart-doc/compare/2.2.2...2.2.3) - 2021-07-18

Maven coordinates: `com.github.shalousun:smart-doc:2.2.3`

### Changed

- 增加pathPrefix配置项用于配置上下文，引入该配置项后serverUrl仅用于配置服务器地址。
- 支持请求头常量设置解析。
- 支持使用JsonIgnoreProperties和JSONType注解去忽略多字段。
- 支持配置添加拦截器中设置全局请求参数，[#132](https://github.com/TongchengOpenSource/smart-doc/issues/132)

### Fixed

- 修复dubbo rpc文档模板格式错误 #https://gitee.com/smart-doc-team/smart-doc/issues/I40ZGE .
- 修复部分类型mock未推送到torna的问题。

### Other

- 修改部分文档设置allInOneDocFileName无效的问题,[#131](https://github.com/TongchengOpenSource/smart-doc/issues/131)

## [2.2.2](https://github.com/TongchengOpenSource/smart-doc/compare/2.2.1...2.2.2) - 2021-07-04

Maven coordinates: `com.github.shalousun:smart-doc:2.2.2`

### Changed

- 优化了maven插件的提示，优化后可以将加载了那些目录模块代码路径打印。
- 添加忽略HttpServlet对象。
- 支持内置替换Jpa Pageable分页对象，去除不必要的请求参数替换配置。

### Fixed

- 修复url中遇到正则表达解析错误问题.
- 修复生成的json样例部分格式化后错误的bug,gitee #I3XSE5。
- 修复doc模板错误，gitee #I3Y640。
- 修复字典模板错误，[#119](https://github.com/TongchengOpenSource/smart-doc/issues/119)
- 修复dubbo rpc文档推送到torna数据错误。
- 修复customResponseFields和customRequestFields设置时不同类同名字段覆盖bug，gitee #I3Y6AL。
- 修复高版本gradle中使用implements添加依赖时，gradle插件无法加载依赖导致返回空json的bug。

### Other

- 增强对文档中html特殊字符的处理，防止html渲染后显示错误，gitee #I3XO31。
- 请求头设置增强，支持配置urlPatterns和excludePathPatterns两个属性去匹配对应的接口。
- 提供了其它框架扩展文档解析的能力。
- packageFilters增强，可使用正则进行匹配，gitee #I3YKZ4 。

## [2.2.1](https://github.com/TongchengOpenSource/smart-doc/compare/2.2.0...2.2.1) - 2021-06-20

Maven coordinates: `com.github.shalousun:smart-doc:2.2.1`

### Other

- 移除代码中System.out.print打印.

## [2.2.0](https://github.com/TongchengOpenSource/smart-doc/compare/2.1.9...2.2.0) - 2021-06-20

Maven coordinates: `com.github.shalousun:smart-doc:2.2.0`

### Changed

- 添加@response tag。支持自己设置response example

### Fixed

- 修复参数多行注释时，注释提取错误，gitee #I3TYYP.
- 修复部分代码可能出现的空指针问题。
- 修复推送到torna请求或返回为数组时，示例显示错误
- 修复使用Quartz中JobDataMap类型解析错误。

### Other

- Character类型解析支持。
- 移除YapiBuilder。smart-doc不在支持其他第三方接口系统，请使用torna.

## [2.1.9](https://github.com/TongchengOpenSource/smart-doc/compare/2.1.8...2.1.9) - 2021-05-29

Maven coordinates: `com.github.shalousun:smart-doc:2.1.9`

### Changed

- 添加多层泛型嵌套的解析支持。gitee #I3T6UV .
- 添加将接口排序order推送到torna中。
- 优化字典码推送，空字典码不会像torna发起推送请求。

### Fixed

- 修复inlineEnum为false时枚举展示在参数中的问题。
- 修复父类是泛型时父类中LocalDateTime类型字段生成json样例错误。
- 修复类上的@ignore tag不生效bug.

### Other

- 返回Spring文件下载对象支持自动识别为文件下载，减少手动标记@download tag。
- smart-doc使用的css cdn更换，默认使用国内cdn，提升国内的加载速度，切换英文环境使用google的cdn.

## [2.1.8](https://github.com/TongchengOpenSource/smart-doc/compare/2.1.7...2.1.8) - 2021-05-22

Maven coordinates: `com.github.shalousun:smart-doc:2.1.8`

### Added

- 新增将业务错误码和定义字典推送到torna。

### Changed

- 支持父类上加@RestController注解的子类能够被识别和扫描
- 添加@JsonProperty支持,支持JsonProperty.Access控制字段。

### Fixed

- 修复推送接口到torna丢失部分mock值的问题。
- 修复在参数注释中配置类替换时将非类名解析成类名的bug 。
- 修复maven插件torna-rest和torna-rpc两个task未加编译前缀的问题。
- 修复生成json用例中数组类型json错误的问题。
- 修复customRequestFields中设置字段value在用例中不生效的bug。

## [2.1.7](https://github.com/TongchengOpenSource/smart-doc/compare/2.1.6...2.1.7) - 2021-05-12

Maven coordinates: `com.github.shalousun:smart-doc:2.1.7`

### Changed

- 添加推送接口作者信息到torna数据错误的bug。

### Fixed

- 修复空参数curl命令多余？号问题，github 。

## [2.1.6](https://github.com/TongchengOpenSource/smart-doc/compare/2.1.5...2.1.6) - 2021-05-10

Maven coordinates: `com.github.shalousun:smart-doc:2.1.6`

### Changed

- 添加推送接口作者信息到torna,通过配置author设置推送人，不配置默认为计算机用户名。
- 添加推送queryParams参数到torna(需要使用torna 1.6.0+)

### Fixed

- 修复不允许List中放文件上传对象错误的bug。

## [2.1.5](https://github.com/TongchengOpenSource/smart-doc/compare/2.1.4...2.1.5) - 2021-05-05

Maven coordinates: `com.github.shalousun:smart-doc:2.1.5`

### Changed

- 添加dubbo文档到torna的推送。

### Fixed

- 修复requestBodyAdvice请求样例丢之。

## [2.1.4](https://github.com/TongchengOpenSource/smart-doc/compare/2.1.3...2.1.4) - 2021-04-24

Maven coordinates: `com.github.shalousun:smart-doc:2.1.4`

### Added

- 新增requestBodyAdvice支持，可以实现请求参数包装。

### Fixed

- 修复Controller继承时，父类的Mapping未继承的问题。
- 修复配置responseBodyAdvice后，controller中void方法返回显示错误。
- 修复往torna推送漏掉pathParams的问题。
- 修复非json请求集合中绑定枚举强制检查错误的问题。
- 修复泛型为List数据时，类型为object问题。
- 修复customFiled为继承参数时配置失效问题。

## [2.1.3](https://github.com/TongchengOpenSource/smart-doc/compare/2.1.2...2.1.3) - 2021-04-11

Maven coordinates: `com.github.shalousun:smart-doc:2.1.3`

### Changed

- 增加customRequestFields配置项，[#97](https://github.com/TongchengOpenSource/smart-doc/issues/97)

### Fixed

- 修复往torna推送漏掉pathParams的问题。

### Other

- 增强对文件上传的支持。
- 修改debug测试页面，支持post表单请求
- 修改表单请求对象中枚举字段默认值错误的bug

## [2.1.2](https://github.com/TongchengOpenSource/smart-doc/compare/2.1.1...2.1.2) - 2021-03-29

Maven coordinates: `com.github.shalousun:smart-doc:2.1.2`

### Fixed

- 修复Map嵌套在某些结构体中栈溢出问题，gitee #I3CCLY。
- 修复Torna数据推送问题。

## [2.1.1](https://github.com/TongchengOpenSource/smart-doc/compare/2.1.0...2.1.1) - 2021-03-24

Maven coordinates: `com.github.shalousun:smart-doc:2.1.1`

### Fixed

- 修复Map嵌套在某些结构体中栈溢出问题，gitee #I3CCLY。
- 修复Torna数据推送问题。

## [2.1.0](https://github.com/TongchengOpenSource/smart-doc/compare/2.0.9...2.1.0) - 2021-03-21

Maven coordinates: `com.github.shalousun:smart-doc:2.1.0`

### Changed

- 增加@ignoreParams自定义tag来过滤掉不想显示在文档中参数。
- 增加了自动生成版本记录的功能。
- 支持旧的SpringMVC项目的url后缀，新项目不建议加什么破玩意后缀。

### Other

- 导出的postman的url资源下添加缺失的protocol。
- 修改torna推送的bug。

## [2.0.9](https://github.com/TongchengOpenSource/smart-doc/compare/2.0.8...2.0.9) - 2021-03-12

Maven coordinates: `com.github.shalousun:smart-doc:2.0.9`

### Changed

- 支持UUID和ZonedDateTime字段类型，[#89](https://github.com/TongchengOpenSource/smart-doc/issues/89)

### Other

- 对map参数增加开关来兼容旧项目，还是不建议使用map参数。
- 完成和Torna的对接。

## [2.0.8](https://github.com/TongchengOpenSource/smart-doc/compare/2.0.7...2.0.8) - 2021-02-26

Maven coordinates: `com.github.shalousun:smart-doc:2.0.8`

### Added

- 修复2.0.7新增忽略接口方法后解析父类字段缺失注释bug。

### Fixed

- 修复文件上传的参数丢失的注释。

### Other

- 修改byte类型的转换，将过去的string转为int8。

## [2.0.7](https://github.com/TongchengOpenSource/smart-doc/compare/2.0.6...2.0.7) - 2021-01-30

Maven coordinates: `com.github.shalousun:smart-doc:2.0.7`

### Changed

- 添加对默认接口实现中get方法重写忽略的能力解析。

### Fixed

- 修复postman的url中不附加的context-path的问题。
- 修复带正则的path路径参数解析出现截取越界的问题。
- 修复对mapping中headers的处理。

### Other

- 修改数组、map等字段类型的自定义mock值显示错误问题。

## [2.0.6](https://github.com/TongchengOpenSource/smart-doc/compare/2.0.5...2.0.6) - 2021-01-15

Maven coordinates: `com.github.shalousun:smart-doc:2.0.6`

### Fixed

- 修复带正则的path路径参数在postman中用例问题。

### Other

- 增强对祖传不良代码的分析兼容。

## [2.0.5](https://github.com/TongchengOpenSource/smart-doc/compare/2.0.4...2.0.5) - 2021-01-09

Maven coordinates: `com.github.shalousun:smart-doc:2.0.5`

### Fixed

- 修复集合类无泛型参数作为入参出参时的数组越界。
- 修复新开tab访问的url拼接问题。

## [2.0.3-2.0.4](https://github.com/TongchengOpenSource/smart-doc/compare/2.0.2...2.0.3) - 2021-01-01

Maven coordinates: `com.github.shalousun:smart-doc:2.0.3`

### Other

- 修改页面的错误列表标题显示。
- 修改debug页面curl header语法错误。
- 修改debug页面json参数输入框，允许粘贴小段文本。
- 解决使用dubbo 2.7+，在provider中生成文档出错问题[#77](https://github.com/TongchengOpenSource/smart-doc/issues/77)

## [2.0.2](https://github.com/TongchengOpenSource/smart-doc/compare/2.0.1...2.0.2) - 2020-12-27

Maven coordinates: `com.github.shalousun:smart-doc:2.0.2`

### Added

- 调试页面新增Highlight支持。

### Changed

- 优化debug页面中的文件下载测试。
- 优化enum入参mock错误的bug。
- 优化set等集合类在文档中的类型显示。
- 添加对集合字段中枚举的处理。

### Other

- 修改创建openapi时的空指针异常。
- 修改debug页面时未使用mock值的问题。
- debug页面可以根据请求动态更新curl命令。
- mock页面支持使用新窗口打开后端渲染的页面。
- 修改生成一些字段值生成错误的bug。
- 修改类中使用集合字段未指定泛型可能出错的bug。
- 枚举序列化支持优化。

## [2.0.1](https://github.com/TongchengOpenSource/smart-doc/compare/2.0.0...2.0.1) - 2020-12-20

Maven coordinates: `com.github.shalousun:smart-doc:2.0.1`

### Other

- debug调试页面支持文件上传。
- 修改简单请求参数mock值和类型不匹配问题。
- debug页面完全支持文件下载测试。
- 所有html的文档支持接口目录搜索。
- 剔除flexmark依赖，旧的非allInOne模板删除，统一h5文档样式。

## [2.0.0](https://github.com/TongchengOpenSource/smart-doc/compare/1.9.9.1...2.0.0) - 2020-12-13

Maven coordinates: `com.github.shalousun:smart-doc:2.0.0`

### Changed

- 优化了文档的显示，将query和path单独提出来做了展示
- 优化openapi 3.0文档规范的支持，可集成swagger ui等ui使用。
- 优化postman collection 2.0的支持。
- 添加分组支持group。

### Other

- 修改mock的一些bug和增强使用
- 支出创建debug页面

## [1.9.9.2](https://github.com/TongchengOpenSource/smart-doc/compare/1.9.9.1...1.9.9.2) - 2020-12-06

Maven coordinates: `com.github.shalousun:smart-doc:1.9.9.2`

### Other

- Fix error while use SpringMVC ResponseBodyAdvice.

## [1.9.9.1](https://github.com/TongchengOpenSource/smart-doc/compare/1.9.9...1.9.9.1) - 2020-11-23

Maven coordinates: `com.github.shalousun:smart-doc:1.9.9.1`

### Other

- 这是一个紧急修改版本。
- 解决1.9.9版本controller中存在非路径映射方法时的错误。

## [1.9.9](https://github.com/TongchengOpenSource/smart-doc/compare/1.9.8...1.9.9) - 2020-11-23

Maven coordinates: `com.github.shalousun:smart-doc:1.9.9`

### Changed

- 增加对@RequestMapping注解的path属性的解析支持

### Fixed

- 修复ResponseEntity中的数组解析错误。
- 修复controller方法标注ignore后文档序号错误。
- 修复postman中formdata表单不显示描述信息的问题

### Other

- 修改1.9.8启用严格检查注释模式下的bug。
- 修改使用泛型数组参数时解析错误。
- html5 allInOne模板支持代码高亮。

## [1.9.8](https://github.com/TongchengOpenSource/smart-doc/compare/1.9.7...1.9.8) - 2020-11-10

Maven coordinates: `com.github.shalousun:smart-doc:1.9.8`

### Changed

- 增加对抽象Controller方法的解析。
- 支持ResponseBodyAdvice通用接口响应包装设置。

### Fixed

- 修复类同时继承和基类和实现接口中可能出现字段重复的bug。

### Other

- 忽略Class对象的解析。
- 修改阿里版本dubbo注解名称解析错误 。
- 修改模拟值生成错误。

## [1.9.7](https://github.com/TongchengOpenSource/smart-doc/compare/1.9.6...1.9.7) - 2020-10-24

Maven coordinates: `com.github.shalousun:smart-doc:1.9.7`

### Changed

- 优化rpc html非all in one的问题。

### Fixed

- 修复restful接口泛型中使用?时的解析错误。

### Other

- 对rest query参数自动添加描述，增加可读性。
- support ali dubbo,#I22CF7 .
- support @RequestMapping headers.

## [1.9.6](https://github.com/TongchengOpenSource/smart-doc/compare/1.9.5...1.9.6) - 2020-10-09

Maven coordinates: `com.github.shalousun:smart-doc:1.9.6`

### Changed

- 增加泛型实际类型的显示开关控制。
- 优化smart-doc maven插件，提升用户在多模块下的使用体验。

### Fixed

- 修复RequestParam 解析错误。
- 修复泛型中使用?时的解析错误。
- 修复类继承一个泛型类时的解析错误。

### Other

- 修改服务url的地址为空字符串，不再提供默认http前缀

## [1.9.5](https://github.com/TongchengOpenSource/smart-doc/compare/1.9.4...1.9.5) - 2020-09-19

Maven coordinates: `com.github.shalousun:smart-doc:1.9.5`

### Other

- 接口参数无注解时将required设置为false。
- 修改html自适应。

## [1.9.4](https://github.com/TongchengOpenSource/smart-doc/compare/1.9.3...1.9.4) - 2020-09-06

Maven coordinates: `com.github.shalousun:smart-doc:1.9.4`

### Changed

- 添加order tag支持对api做排序。
- 优化一些重复的代码。
- 添加生成yapi文件的功能。

### Other

- 修改基础url中使用常量出现空格的问题。

## [1.9.3](https://github.com/TongchengOpenSource/smart-doc/compare/1.9.2.1...1.9.3) - 2020-08-30

Maven coordinates: `com.github.shalousun:smart-doc:1.9.3`

### Changed

- 优化MultipartFile文件上传参数处理，不对该参数进行展开分析。

### Fixed

- 修复Get请求用例参数值被去空格问题。
- 修复非allInOne模板使用渲染错误。
- 修复一些泛型例子解析错误bug。

### Other

- 修改复杂参数表树型数据转化的错误。

## [1.9.2.1](https://github.com/TongchengOpenSource/smart-doc/compare/1.9.2...1.9.2.1) - 2020-08-23

Maven coordinates: `com.github.shalousun:smart-doc:1.9.2.1`

### Other

- Emergency fix version for 1.9.2

## [1.9.2](https://github.com/TongchengOpenSource/smart-doc/compare/1.9.1...1.9.2) - 2020-08-23

Maven coordinates: `com.github.shalousun:smart-doc:1.9.2`

### Added

- 新增忽略请求参数对象的配置gitee #I1RBJO。
- 新增ApiDataBuilder中获取树形格式参数数据的接口[#40](https://github.com/smart-doc-group/smart-doc/issues/40)
- 新增对Open Api 3.0的支持。

### Changed

- 优化curl用例，增加请求头。

### Other

- 修改前面版本修改引发的普通jsr 303验证解析错误问题。
- 修改smart-doc的beetl配置避免和用户的业务中beetl配置冲突。
- 修改字典表空时内部发生空指针的问题。

## [1.9.1](https://github.com/TongchengOpenSource/smart-doc/compare/1.9.0...1.9.1) - 2020-08-02

Maven coordinates: `com.github.shalousun:smart-doc:1.9.1`

### Other

- 修改进去版本更新导致的泛型解析问题。
- 修改1.8.9版本修改后带来的dubbo接口文档显示问题
- 修改smart-doc-maven-plugin生成dubbo文档时缺乏配置文件错误问题。
- 修改gradle插件的对多模块的支持。

## [1.9.0](https://github.com/TongchengOpenSource/smart-doc/compare/1.8.9...1.9.0) - 2020-07-19

Maven coordinates: `com.github.shalousun:smart-doc:1.9.0`

### Added

- 新增自定义输出文件名称的配置。

### Changed

- 添加请求和响应示例的开关配置项。

### Other

- 修改dubbo html依赖部分错乱问题。
- 修改使用JSR303参数校验时，默认分组验证被忽略问题。
- 修改jackson JsonIgnore注解在参数对象中不生效的问题。

## [1.8.9](https://github.com/TongchengOpenSource/smart-doc/compare/1.8.8...1.8.9) - 2020-07-05

Maven coordinates: `com.github.shalousun:smart-doc:1.8.9`

### Changed

- 优化支持gitee #I1IQKY常量解析需求

### Other

- 修改[#38](https://github.com/smart-doc-group/smart-doc/issues/38)
- 修改gitee #I1LBKO。
- 修改fix #39多泛型解析顺序问题。

## [1.8.8](https://github.com/TongchengOpenSource/smart-doc/compare/1.8.7...1.8.8) - 2020-06-21

Maven coordinates: `com.github.shalousun:smart-doc:1.8.8`

### Changed

- 优化接口方法字段不能获取docletTag的问题。
- 优化枚举参数展示，支持自定义控制显示。
- 添加Feign的支持。
- 优化递归执行，对外提供递归次数限制。

### Other

- 修改忽略对LinkedHashMap的解析，gitee #I1JI5W。
- 修改接口或和实现类合并分析是字段重复问题，gitee #I1JHMW。

## [1.8.7](https://github.com/TongchengOpenSource/smart-doc/compare/1.8.6...1.8.7) - 2020-06-01

Maven coordinates: `com.github.shalousun:smart-doc:1.8.7`

### Changed

- 增加对java接口的分析，例如Jpa的分页Page类。
- 增加dubbo rpc文档生成支持。
- 增加将驼峰字段格式转化为下划线格式。
- 增加文档接口根据接口标题排序功能。

### Other

- 增强对使用@RequestBody绑定参数方法的解析。
- maven插件和gradle插件提供includes支持，方便自行配置加载第三方库。
- fix https://github.com/smart-doc-group/smart-doc/issues/32.

## [1.8.6](https://github.com/TongchengOpenSource/smart-doc/compare/1.8.5...1.8.6) - 2020-05-09

Maven coordinates: `com.github.shalousun:smart-doc:1.8.6`

### Changed

- 增加localTime支持[gitee #I1F7CW](https://gitee.com/sunyurepository/smart-doc/issues/I1F7CW)。
- 优化smart-doc导入Postman collection时的header问题[gitee #I1EX42](https://gitee.com/sunyurepository/smart-doc/issues/I1EX42)
- 优化smart-doc-maven-plugin加载source的过滤，支持使用通配符来过滤。

### Fixed

- 修复通用泛型解析出错[git #28](https://github.com/smart-doc-group/smart-doc/issues/28)。

### Other

- 首次发布gradle插件，发布smart-doc-gradle-plugin插件，

## [1.8.5](https://github.com/TongchengOpenSource/smart-doc/compare/1.8.4...1.8.5) - 2020-04-19

Maven coordinates: `com.github.shalousun:smart-doc:1.8.5`

### Changed

- 增加@PatchMapping支持[gitee #I1EDRF](https://gitee.com/sunyurepository/smart-doc/issues/I1EDRF)
- 优化smart-doc-maven-plugin对多级maven项目的加载。
- 支持请求参数对象替换成另外的对象来渲染文档

### Fixed

- 修复分组验证空指针问题，不对返回对象做分组验证处理。

### Other

- maven插件错误码列表导出bug[git #I1EHXA](https://gitee.com/sunyurepository/smart-doc/issues/I1EHXA)
- 解决javadoc包含重复tag生成文档报错[gitee #I1ENNM](https://gitee.com/sunyurepository/smart-doc/issues/I1ENNM)
- 修改当请求参数为泛型时数据解析错误问题。

## [1.8.4](https://github.com/TongchengOpenSource/smart-doc/compare/1.8.3...1.8.4) - 2020-03-30

Maven coordinates: `com.github.shalousun:smart-doc:1.8.4`

### Added

- Controller新增时候@ignore tag,可适应该tag忽略不需要生成文档的controller[git #24](https://github.com/smart-doc-group/smart-doc/issues/24)

### Other

- 参数中包含 HttpSession时smart-doc卡主，[gitee #I1CA9M](https://gitee.com/sunyurepository/smart-doc/issues/I1CA9M)
- 解决一些复杂分组场景smart-doc报错的问题[gitee #I1CPSM](https://gitee.com/sunyurepository/smart-doc/issues/I1CPSM)
- 解决smart-doc-maven-plugin插件读取配置乱码问题。

## [1.8.3](https://github.com/TongchengOpenSource/smart-doc/compare/1.8.2...1.8.3) - 2020-03-21

Maven coordinates: `com.github.shalousun:smart-doc:1.8.3`

### Changed

- 增加从接口方法getter或者setter方法中读取注释。
- 增加对代码中@author tag的支持，支持多作者。

### Other

- 修改smart-doc默认编码为utf-8，解决生成文档乱码问题。

## [1.8.2](https://github.com/TongchengOpenSource/smart-doc/compare/1.8.1...1.8.2) - 2020-03-13

Maven coordinates: `com.github.shalousun:smart-doc:1.8.2`

### Changed

- 支持@Validated 分组

### Other

- 修改gitee #I19IYW 。
- 修改文档模板中的title设置错误。
- 修改gitee #I191EO

## [1.8.1](https://github.com/TongchengOpenSource/smart-doc/compare/1.8.0...1.8.1) - 2020-01-22

Maven coordinates: `com.github.shalousun:smart-doc:1.8.1`

### Added

- 新增@Deprecated标注接口的支持(使用line through完成样式标记)

### Changed

- 增加对接口get方法的分析。
- 增加对第三方jar中list泛型数据的解析。

### Other

- 删除原来冗长的SourceBuilder代码。
- 修改AdocDocBuilder、HtmlApiDocBuilder、ApiDocBuilder的方法名规范化，单元测试的升级需要做小部分变更。
- 修改1.8.0重构后的请求示例将header放入普通参数的bug。
- 修改参数加上@Validated注解后，文档里没有该参数信息的bug。

## [1.8.0](https://github.com/TongchengOpenSource/smart-doc/compare/1.7.9...1.8.0) - 2020-01-01

Maven coordinates: `com.github.shalousun:smart-doc:1.8.0`

### Added

- 新增ApiDataBuilder用于获取smart-doc生成的文档数据，包含header、字典、错误码等。
- 合并fork分支的github book html5模板，新增搜索和锚点。
- 新增自定义@mock tag用于指定生成文档的字段值，@param 的参数注释增加mock值的功能(@param name 姓名|张三)

### Changed

- 优化对实体类中枚举字段的支持。
- 增加对实体中静态常量常量字段的过滤。

### Other

- 修改参数上多个验证注解不支持的问题。
- 修改支持上传文件参数不列举到文档的问题。
- 重点：smart-doc的maven插件smart-doc-maven-plugin增强对maven标准项目的支持。
- 全面支持spring的表单参数绑定解析。
- postman json生成支持所有参数自动回填。再也不用自己建参数了。

## [1.7.9](https://github.com/TongchengOpenSource/smart-doc/compare/1.7.8...1.7.9) - 2019-12-16

Maven coordinates: `com.github.shalousun:smart-doc:1.7.9`

### Added

- 新增ApiDataBuilder用于获取smart-doc生成的文档数据，包含header、字典、错误码等。
- 新增接口的@author展示，方法从文档中查到找到接口负责人，生成文档可以选择关闭显示。

### Other

- 修改request请求参数中嵌套对象不能解析的bug，参考gitee #I16AN2.
- controller参数是数组时添加@PathVariable注解会报空指针,参考gitee #I16F6G
- 修改github https://github.com/smart-doc-group/smart-doc/issues/9 文档错误bug.
- 重点：smart-doc的maven插件smart-doc-maven-plugin 1.0.0版本发布。

## [1.7.8](https://github.com/TongchengOpenSource/smart-doc/compare/1.7.7...1.7.8) - 2019-12-02

Maven coordinates: `com.github.shalousun:smart-doc:1.7.8`

### Changed

- 添加对transient修饰字段文档输出开关，默认不输出。
- 添加导出PostMan json数据

### Other

- 修改Spring Controller使用非Spring Web注解时生成的响应示例出错的bug。
- 修改使用mybatis-plus实体继承Model对象时将log字段输出到文档的问题。
- html文档添加项目名称显示
- 修改[#github 4](https://github.com/smart-doc-group/smart-doc/issues/4) 泛型中Void类型解析死循环
- 修改[#github 5](https://github.com/smart-doc-group/smart-doc/issues/5) 简单枚举参数解析空指针异常

## [1.7.7](https://github.com/TongchengOpenSource/smart-doc/compare/1.7.6...1.7.7) - 2019-11-18

Maven coordinates: `com.github.shalousun:smart-doc:1.7.7`

### Changed

- 优化对java.util.concurrent.ConcurrentMap的解析。

### Other

- 修改timestamp类型字段创建json示例错误bug。
- fix #I1545A 单接口多路径bug。
- 修改部分url生成部署空格问题。

## [1.7.6](https://github.com/TongchengOpenSource/smart-doc/compare/1.7.5...1.7.6) - 2019-11-13

Maven coordinates: `com.github.shalousun:smart-doc:1.7.6`

### Changed

- 增加请求参数枚举字段解析(试用功能)

### Other

- fix #I14PT5 header重复渲染到文档
- fix #I14MV7 不设置dataDictionaries出现空指针错误

## [1.7.5](https://github.com/TongchengOpenSource/smart-doc/compare/1.7.4...1.7.5) - 2019-11-06

Maven coordinates: `com.github.shalousun:smart-doc:1.7.5`

### Added

- 新增枚举字典码导出到文档的功能。

### Changed

- 优化文档中错误列表的标题，可根据语言环境变化显示中文或因为。
- 支持环形依赖分析。只要你敢写！
- 支持设置项目作为markdown的一级标题。
- 添加生成AllInOne的覆盖配置项，默认自动加版本号不覆盖。

### Other

- 解决项目外jar中内部类生成文档错误的bug。
- 修改使用SpringMvc或者SpringBoot上传时接口的Content-Type显示错误。
- 修改方法注释相同引起的html链接跳转错误。

## [1.7.4](https://github.com/TongchengOpenSource/smart-doc/compare/1.7.3...1.7.4) - 2019-10-29

Maven coordinates: `com.github.shalousun:smart-doc:1.7.4`

### Changed

- 优化AllInOne的markdown展示，生成时带上自动产生的序号。

### Other

- 修改gitee上bug #I1426C。
- 修改gitee上bug #I13ZAL,1.7.0~1.7.3 结构优化后产生的bug，建议用户升级。
- 修改gitee上bug #I13U4C。
- 修改设置中文语言环境(默认中文)下错误码列表title显示英文的问题。

## [1.7.3](https://github.com/TongchengOpenSource/smart-doc/compare/1.7.2...1.7.3) - 2019-10-24

Maven coordinates: `com.github.shalousun:smart-doc:1.7.3`

### Changed

- 优化html5模板左侧文档目录展示，能够展开和收缩。

### Other

- 修改gitee上bug #I13R3K。
- 修改gitee上bug #I13NR1。
- 开放的文档数据获取接口添加返回方法的唯一id和方法名称，方便一些企业自己做对接。

## [1.7.2](https://github.com/TongchengOpenSource/smart-doc/compare/1.7.1...1.7.2) - 2019-10-19

Maven coordinates: `com.github.shalousun:smart-doc:1.7.2`

### Added

- 新增对Spring ResponseEntity的解析。

### Changed

- 优化注释换行\n\r问题，依赖common-util 1.8.7。
- 添加@requestHeader注解的支持，文档自定将参数绑定到请求头列表中。
- 增加javadoc apiNote tag的支持。
- 添加支持@RequestParam注解重写参数名和设置默认值的文档解析。
- 支持使用@PostMapping和@PutMapping请求自定义注解接收单个json参数场景下生成json请求实例。
- 增加内部类返回结构解析。

### Other

- 修改gitee上bug #I135PG、#I13NR1。
- 解决扫描分析controller中private方法的问题。
- 修改文档中显示的字段类型，float、double等由原来的number直接变成具体类型。

## [1.7.1](https://github.com/TongchengOpenSource/smart-doc/compare/1.7.0...1.7.1) (deprecated)

Maven coordinates: `com.github.shalousun:smart-doc:1.7.1`

### Changed

- 优化注释换行\n\r问题。
- 添加requestHeader功能

### Other

- 修改bug #I135PG

## [1.7.0](https://github.com/TongchengOpenSource/smart-doc/compare/1.6.3...1.7.0) - 2019-09-30

Maven coordinates: `com.github.shalousun:smart-doc:1.7.0`

### Changed

- 优化代码。
- 添加生成HTML5和Asciidoctor文档的功能。
- 增加开放API数据接口功能。
- 支持Callable,Future,CompletableFuture等异步接口返回的推导。
- 支持Spring Boot Web Flux(Controller方式书写)。

## 1.6.4 - 2019-09-23

Maven coordinates: `com.github.shalousun:smart-doc:1.6.4`

### Changed

- 优化代码
- 增加对普通的get请求参数拼装示例的生成
- 增加spring mvc占位符restful url请求示例生成

## [1.6.2](https://github.com/TongchengOpenSource/smart-doc/commits/1.6.2) - 2019-09-13

Maven coordinates: `com.github.shalousun:smart-doc:1.6.2`

### Changed

- 升级基础库依赖版本

### Other

- 修改字段注释多行显示错误bug
- 字段描述文档增加@Since tag的支持
- 解析代码忽略WebRequest类防止生产过多信息

## 1.3 - 2018-09-15

Maven coordinates: `com.github.shalousun:smart-doc:1.3`

### Changed

- 增加PutMapping和DeleteMapping支持
- 添加字符串date和Date类型时间的模拟值生成

## 1.2 - 2018-09-04

Maven coordinates: `com.github.shalousun:smart-doc:1.2`

### Other

- 根据用户反馈增加controller报名过滤功能，该功能为可选项

## 1.1 - 2018-08-30

Maven coordinates: `com.github.shalousun:smart-doc:1.1`

### Other

- 修改PostMapping和GetMapping value为空报错的bug
- 增强时间字段的mock数据创建
- 修改smart-doc解析自引用对象出错的bug

## 1.0 - 2018-08-25

Maven coordinates: `com.github.shalousun:smart-doc:1.0`

### Other

- smart-doc增加将所有文档导出归档到一个markdown中件的功能
- 参考阿里开发手册将直接提升到1.0，之前的版本主要是个人内部测试

## 0.5 - 2018-08-23

Maven coordinates: `com.github.shalousun:smart-doc:0.5`

### Other

- 将api-doc重命名为smart-doc并发布到中央仓库

## 0.4 - 2018-07-11

Maven coordinates: `com.github.shalousun:smart-doc:0.4`

### Other

- 修改api-doc对类继承属性的支持。

## 0.3 - 2018-07-10

Maven coordinates: `com.github.shalousun:smart-doc:0.3`

### Other

- api-doc增加对jackson和fastjson注解的支持，可根据注解定义来生成返回信息。

## 0.2 - 2018-07-07

Maven coordinates: `com.github.shalousun:smart-doc:0.2`

### Other

- 修改api-doc泛型推导的bug.

## 0.1 - 2018-06-25

Maven coordinates: `com.github.shalousun:smart-doc:0.1`

### Other

- 手册将api-doc发布到中央仓库
