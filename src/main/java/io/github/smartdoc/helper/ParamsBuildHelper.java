/*
 * smart-doc https://github.com/smart-doc-group/smart-doc
 *
 * Copyright (C) 2018-2025 smart-doc
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.github.smartdoc.helper;

import io.github.smartdoc.builder.ProjectDocConfigBuilder;
import io.github.smartdoc.constants.DocGlobalConstants;
import io.github.smartdoc.constants.DocTags;
import io.github.smartdoc.constants.JavaTypeConstants;
import io.github.smartdoc.constants.ParamTypeConstants;
import io.github.smartdoc.extension.json.PropertyNameHelper;
import io.github.smartdoc.extension.json.PropertyNamingStrategies;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.ApiDataDictionary;
import io.github.smartdoc.model.ApiParam;
import io.github.smartdoc.model.CustomFieldInfo;
import io.github.smartdoc.model.DocJavaField;
import io.github.smartdoc.model.FieldJsonAnnotationInfo;
import io.github.smartdoc.model.torna.EnumInfoAndValues;
import io.github.smartdoc.utils.DocClassUtil;
import io.github.smartdoc.utils.DocUtil;
import io.github.smartdoc.utils.JavaClassUtil;
import io.github.smartdoc.utils.JavaClassValidateUtil;
import io.github.smartdoc.utils.JavaFieldUtil;
import io.github.smartdoc.utils.ParamUtil;
import com.power.common.model.EnumDictionary;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * ApiParam Builder {@link ApiParam }
 *
 * @author yu 2019/12/21.
 */
public class ParamsBuildHelper extends BaseHelper {

	/**
	 * Builds a parameter list based on field information.
	 * @param className The name of the generic type.
	 * @param pre A prefix builder for nested fields.
	 * @param level The next level of nesting.
	 * @param isRequired Indicates whether the parameter is required.
	 * @param isResp Indicates whether the parameter is a response parameter.
	 * @param registryClasses A collection of registered classes.
	 * @param projectBuilder A project builder instance.
	 * @param groupClasses A collection of JSR303 grouped classes.
	 * @param methodJsonViewClasses A set of valid `@JsonView` classes on controller
	 * method.
	 * @param pid The parent ID of the field.
	 * @param jsonRequest The JSON request object.
	 * @param atomicInteger An AtomicInteger for ID generation.
	 * <p>
	 * This method handles various types of fields and their values, including handling
	 * self-referential loops, maps, arrays, objects, and primitive types. It adds
	 * parameters to the paramList based on the type and structure of the given field,
	 * recursively calling itself for nested or complex types.
	 * @return A List of ApiParam instances representing the built parameters.
	 */
	public static List<ApiParam> buildParams(String className, String pre, int level, String isRequired, boolean isResp,
			Map<String, String> registryClasses, ProjectDocConfigBuilder projectBuilder, Set<String> groupClasses,
			Set<String> methodJsonViewClasses, int pid, boolean jsonRequest, AtomicInteger atomicInteger) {

		if (StringUtil.isEmpty(className)) {
			throw new RuntimeException("Class name can't be null or empty.");
		}

		// Recursion limit check cached for efficiency
		ApiConfig apiConfig = projectBuilder.getApiConfig();
		int recursionLimit = apiConfig.getRecursionLimit();
		if (level > recursionLimit) {
			return Collections.emptyList();
		}

		int nextLevel = level + 1;
		// Check circular reference
		if (registryClasses.containsKey(className) && level > registryClasses.size()) {
			return Collections.emptyList();
		}

		// Registry class
		registryClasses.put(className, className);
		String simpleName = DocClassUtil.getSimpleName(className);
		String[] globGicName = DocClassUtil.getSimpleGicName(className);

		if (Objects.isNull(globGicName) || globGicName.length < 1) {
			JavaClass cls = projectBuilder.getClassByName(simpleName);
			// obtain generics from parent class
			JavaClass superJavaClass = Objects.nonNull(cls) ? cls.getSuperJavaClass() : null;
			if (Objects.nonNull(superJavaClass)
					&& !JavaTypeConstants.OBJECT_SIMPLE_NAME.equals(superJavaClass.getSimpleName())) {
				globGicName = DocClassUtil.getSimpleGicName(superJavaClass.getGenericFullyQualifiedName());
			}
		}

		boolean isShowJavaType = apiConfig.getShowJavaType();

		// if is primitive
		if (JavaClassValidateUtil.isPrimitive(simpleName)) {
			return handlePrimitiveType(simpleName, isShowJavaType, atomicInteger, pid);
		}

		// Handle collection types
		if (JavaClassValidateUtil.isCollection(simpleName) || JavaClassValidateUtil.isArray(simpleName)) {
			return handleCollectionOrArrayType(globGicName, pre, level, isRequired, isResp, registryClasses,
					projectBuilder, groupClasses, methodJsonViewClasses, pid, jsonRequest, atomicInteger, simpleName);
		}

		// Handle map types
		if (JavaClassValidateUtil.isMap(simpleName)) {
			return buildMapParam(globGicName, pre, level, isRequired, isResp, registryClasses, projectBuilder,
					groupClasses, methodJsonViewClasses, pid, jsonRequest, nextLevel, atomicInteger);
		}

		// Handle generic object types
		if (JavaTypeConstants.JAVA_OBJECT_FULLY.equals(className)) {
			return buildGenericObjectParam(className, pre, isRequired, atomicInteger, pid);
		}

		// Handle Reactor types
		if (JavaClassValidateUtil.isReactor(simpleName)) {
			if (globGicName.length > 0) {
				return buildParams(globGicName[0], pre, nextLevel, isRequired, isResp, registryClasses, projectBuilder,
						groupClasses, methodJsonViewClasses, pid, jsonRequest, atomicInteger);
			}
			return Collections.emptyList();
		}

		// handle Class Field
		return processFields(className, pre, level, isRequired, isResp, registryClasses, projectBuilder, groupClasses,
				methodJsonViewClasses, pid, jsonRequest, atomicInteger);
	}

	/**
	 * Processes fields of a given class and populates a list of API parameters based on
	 * the field types and properties.
	 * @param className The name of the class containing the fields.
	 * @param pre A prefix to prepend to field names.
	 * @param level The current level of nested fields.
	 * @param isRequired Indicates if the field is required.
	 * @param isResp Indicates if the field is part of a response.
	 * @param registryClasses A map of registry classes used for type resolution.
	 * @param projectBuilder The project builder used to access project-specific details.
	 * @param groupClasses A set of group classes relevant to the field processing.
	 * @param methodJsonViewClasses A set of JSON view classes applicable to methods.
	 * @param pid The parent ID of the current field.
	 * @param jsonRequest Indicates if the field is part of a JSON request.
	 * @param atomicInteger An AtomicInteger used for generating unique IDs.
	 * @return A list of API parameters representing the processed fields.
	 */
	private static List<ApiParam> processFields(String className, String pre, int level, String isRequired,
			boolean isResp, Map<String, String> registryClasses, ProjectDocConfigBuilder projectBuilder,
			Set<String> groupClasses, Set<String> methodJsonViewClasses, int pid, boolean jsonRequest,
			AtomicInteger atomicInteger) {

		if (StringUtil.isEmpty(className)) {
			throw new RuntimeException("Class name can't be null or empty.");
		}

		// Check for recursion limit to avoid infinite loops
		int recursionLimit = projectBuilder.getApiConfig().getRecursionLimit();

		// Early exit when recursion limit is hit
		if (level > recursionLimit) {
			return Collections.emptyList();
		}

		// Avoid processing the same class multiple times
		if (registryClasses.containsKey(className) && level > registryClasses.size()) {
			return Collections.emptyList();
		}

		List<ApiParam> paramList = new ArrayList<>();
		String simpleName = DocClassUtil.getSimpleName(className);

		JavaClass cls = projectBuilder.getClassByName(simpleName);
		boolean isShowJavaType = projectBuilder.getApiConfig().getShowJavaType();
		boolean requestFieldToUnderline = projectBuilder.getApiConfig().isRequestFieldToUnderline();
		boolean responseFieldToUnderline = projectBuilder.getApiConfig().isResponseFieldToUnderline();
		boolean displayActualType = projectBuilder.getApiConfig().isDisplayActualType();
		ClassLoader classLoader = projectBuilder.getApiConfig().getClassLoader();

		PropertyNamingStrategies.NamingBase fieldNameConvert = null;
		// ignore
		if (Objects.nonNull(cls)) {
			List<JavaAnnotation> clsAnnotation = cls.getAnnotations();
			fieldNameConvert = PropertyNameHelper.translate(projectBuilder, clsAnnotation);
		}

		String[] globGicName = DocClassUtil.getSimpleGicName(className);
		Map<String, String> genericMap = new HashMap<>(globGicName.length);
		JavaClassUtil.genericParamMap(genericMap, cls, globGicName);

		Map<String, String> ignoreFields = JavaClassUtil.getClassJsonIgnoreFields(cls);
		List<DocJavaField> fields = JavaClassUtil.getFields(cls, 0, new LinkedHashMap<>(), classLoader);
		for (DocJavaField docField : fields) {
			JavaField field = docField.getJavaField();
			// ignore transient field
			if (isTransientField(field, projectBuilder, isResp)) {
				continue;
			}

			String maxLength = JavaFieldUtil.getParamMaxLength(classLoader, field.getAnnotations());
			StringBuilder comment = new StringBuilder();
			comment.append(docField.getComment());

			String fieldName = docField.getFieldName();
			if (Objects.nonNull(fieldNameConvert)) {
				fieldName = fieldNameConvert.translate(fieldName);
			}
			if (ignoreFields.containsKey(fieldName)) {
				continue;
			}

			String subTypeName = docField.getTypeFullyQualifiedName();
			boolean needToUnderline = (responseFieldToUnderline && isResp) || (requestFieldToUnderline && !isResp);
			if (needToUnderline) {
				fieldName = StringUtil.camelToUnderline(fieldName);
			}
			String typeSimpleName = field.getType().getSimpleName();
			String fieldGicName = docField.getTypeGenericCanonicalName();
			List<JavaAnnotation> javaAnnotations = docField.getAnnotations();

			Map<String, String> tagsMap = DocUtil.getFieldTagsValue(field, docField);
			// since tag value
			String since = DocGlobalConstants.DEFAULT_VERSION;

			if (tagsMap.containsKey(DocTags.SINCE)) {
				since = tagsMap.get(DocTags.SINCE);
			}
			// handle extension
			Map<String, String> extensions = DocUtil.getCommentsByTag(field.getTagsByName(DocTags.EXTENSION),
					DocTags.EXTENSION);
			Map<String, Object> extensionParams = new HashMap<>(extensions.size());
			if (!extensions.isEmpty()) {
				extensions.forEach((k, v) -> extensionParams.put(k, DocUtil.detectTagValue(v)));
			}

			boolean strRequired = false;

			CustomFieldInfo customFieldInfo = getCustomFieldInfo(projectBuilder, docField, isResp, typeSimpleName,
					fieldName);
			// ignore custom field
			if (Boolean.TRUE.equals(customFieldInfo.getIgnore())) {
				continue;
			}

			// field json annotation
			FieldJsonAnnotationInfo annotationInfo = getFieldJsonAnnotationInfo(projectBuilder, docField, isResp,
					groupClasses, methodJsonViewClasses);
			if (Boolean.TRUE.equals(annotationInfo.getIgnore())) {
				continue;
			}
			// the param type from @JsonFormat
			String fieldJsonFormatType = annotationInfo.getFieldJsonFormatType();
			// the param value from @JsonFormat
			String fieldJsonFormatValue = annotationInfo.getFieldJsonFormatValue();
			// has Annotation @JsonSerialize And using ToStringSerializer
			boolean toStringSerializer = Boolean.TRUE.equals(annotationInfo.getToStringSerializer());
			if (StringUtil.isNotEmpty(annotationInfo.getFieldName())) {
				fieldName = annotationInfo.getFieldName();
			}
			if (Objects.nonNull(annotationInfo.getStrRequired())) {
				strRequired = annotationInfo.getStrRequired();
			}

			comment.append(JavaFieldUtil.getJsrComment(projectBuilder.getApiConfig().isShowValidation(), classLoader,
					javaAnnotations));
			// fix mock post form curl example error
			String fieldValue = getFieldValueFromMock(tagsMap);

			if (StringUtil.isNotEmpty(customFieldInfo.getFieldValue())) {
				fieldValue = customFieldInfo.getFieldValue();
			}
			if (StringUtil.isNotEmpty(customFieldInfo.getFieldName())) {
				fieldName = customFieldInfo.getFieldName();
			}
			if (StringUtil.isNotEmpty(customFieldInfo.getComment())) {
				comment = new StringBuilder(customFieldInfo.getComment());
			}
			if (Objects.nonNull(customFieldInfo.getStrRequired())) {
				strRequired = customFieldInfo.getStrRequired();
			}

			fieldName = fieldName.trim();

			int nextLevel = level + 1;
			// Analyzing File Type Field
			if (JavaClassValidateUtil.isFile(fieldGicName)) {
				ApiParam param = ApiParam.of()
					.setField(pre + fieldName)
					.setType(ParamTypeConstants.PARAM_TYPE_FILE)
					.setClassName(className)
					.setPid(pid)
					.setId(atomicOrDefault(atomicInteger, paramList.size() + pid + 1))
					.setMaxLength(maxLength)
					.setDesc(comment.toString())
					.setRequired(strRequired)
					.setVersion(since)
					.setExtensions(extensionParams);
				if (fieldGicName.contains("[]") || fieldGicName.endsWith(">")) {
					param.setType(ParamTypeConstants.PARAM_TYPE_FILE);
					param.setDesc(comment.append("(array of file)").toString());
					param.setHasItems(true);
				}
				paramList.add(param);
				continue;
			}
			// Analyzing Map Type Field
			if (JavaClassValidateUtil.isMap(subTypeName)) {
				ApiParam param = ApiParam.of()
					.setField(pre + fieldName)
					.setType(ParamTypeConstants.PARAM_TYPE_OBJECT)
					.setClassName(className)
					.setPid(pid)
					.setId(atomicOrDefault(atomicInteger, paramList.size() + pid + 1))
					.setMaxLength(maxLength)
					.setDesc(comment.toString())
					.setRequired(strRequired)
					.setVersion(since)
					.setExtensions(extensionParams);
				paramList.add(param);

				List<ApiParam> apiParams = buildMapParam(DocClassUtil.getSimpleGicName(fieldGicName),
						DocUtil.getIndentByLevel(level), level + 1, isRequired, isResp, registryClasses, projectBuilder,
						groupClasses, methodJsonViewClasses, param.getId(), jsonRequest, nextLevel, atomicInteger);
				paramList.addAll(apiParams);
				continue;
			}
			// Analyzing Primitive Type Field
			if (JavaClassValidateUtil.isPrimitive(subTypeName)) {
				if (StringUtil.isEmpty(fieldValue)) {
					fieldValue = StringUtil.isNotEmpty(fieldJsonFormatValue) ? fieldJsonFormatValue : StringUtil
						.removeQuotes(DocUtil.getValByTypeAndFieldName(typeSimpleName, field.getName()));
				}

				ApiParam param = ApiParam.of()
					.setClassName(className)
					.setField(pre + fieldName)
					.setPid(pid)
					.setMaxLength(maxLength)
					.setValue(fieldValue);
				param.setId(atomicOrDefault(atomicInteger, paramList.size() + param.getPid() + 1));
				String processedType = (isResp && toStringSerializer) ? "string"
						: StringUtil.isNotEmpty(fieldJsonFormatType) ? fieldJsonFormatType
								: processFieldTypeName(isShowJavaType, subTypeName);
				param.setType(processedType);
				param.setExtensions(extensionParams);
				// handle param
				processApiParam(paramList, param, isRequired, comment.toString(), since, strRequired);

				JavaClass enumClass = ParamUtil.handleSeeEnum(param, field, projectBuilder, isResp || jsonRequest,
						tagsMap, fieldJsonFormatValue);
				if (Objects.nonNull(enumClass)) {
					String enumClassComment = DocGlobalConstants.EMPTY;
					if (StringUtil.isNotEmpty(enumClass.getComment())) {
						enumClassComment = enumClass.getComment();
					}
					comment = new StringBuilder(
							StringUtils.isEmpty(comment.toString()) ? enumClassComment : comment.toString());
					String enumComment = handleEnumComment(enumClass, projectBuilder);
					param.setDesc(comment + enumComment);
				}
			}
			else {
				String appendComment = "";
				if (displayActualType) {
					if (globGicName.length > 0) {
						String gicName = genericMap.get(subTypeName) != null ? genericMap.get(subTypeName)
								: globGicName[0];
						if (!simpleName.equals(gicName)) {
							appendComment = " (ActualType: " + JavaClassUtil.getClassSimpleName(gicName) + ")";
						}
					}
					if (Objects.nonNull(docField.getActualJavaType())) {
						appendComment = " (ActualType: "
								+ JavaClassUtil.getClassSimpleName(docField.getActualJavaType()) + ")";
					}
				}

				StringBuilder preBuilder = DocUtil.getStringBuilderByLevel(level);
				int fieldPid;
				ApiParam param = ApiParam.of()
					.setField(pre + fieldName)
					.setClassName(className)
					.setPid(pid)
					.setMaxLength(maxLength);
				param.setId(atomicOrDefault(atomicInteger, paramList.size() + param.getPid() + 1));
				param.setExtensions(extensionParams);
				String processedType;
				if (fieldGicName.length() == 1) {
					String gicName = JavaTypeConstants.JAVA_OBJECT_FULLY;
					if (Objects.nonNull(genericMap.get(typeSimpleName))) {
						gicName = genericMap.get(subTypeName);
					}
					else {
						if (globGicName.length > 0) {
							gicName = globGicName[0];
						}
					}
					if (JavaClassValidateUtil.isPrimitive(gicName)) {
						processedType = DocClassUtil.processTypeNameForParams(gicName);
					}
					else {
						processedType = DocClassUtil.processTypeNameForParams(typeSimpleName.toLowerCase());
					}
				}
				else {
					processedType = StringUtil.isNotEmpty(fieldJsonFormatType) ? fieldJsonFormatType
							: processFieldTypeName(isShowJavaType, subTypeName);
				}
				param.setType(processedType);
				JavaClass javaClass = field.getType();
				if (javaClass.isEnum()) {
					comment.append(handleEnumComment(javaClass, projectBuilder));
					ParamUtil.handleSeeEnum(param, field, projectBuilder, isResp || jsonRequest, tagsMap,
							fieldJsonFormatValue);
					// hand Param
					processApiParam(paramList, param, isRequired, comment.toString(), since, strRequired);
				}
				else if (JavaClassValidateUtil.isCollection(subTypeName)
						|| JavaClassValidateUtil.isArray(subTypeName)) {
					if (isShowJavaType) {
						// rpc
						param.setType(
								JavaFieldUtil.convertToSimpleTypeName(docField.getTypeGenericFullyQualifiedName()));
					}
					else {
						param.setType(ParamTypeConstants.PARAM_TYPE_ARRAY);
					}
					if (tagsMap.containsKey(DocTags.MOCK) && StringUtil.isNotEmpty(tagsMap.get(DocTags.MOCK))) {
						param.setValue(fieldValue);
					}
					if (globGicName.length > 0 && JavaTypeConstants.JAVA_LIST_FULLY.equals(fieldGicName)) {
						// no generic, just object
						fieldGicName = fieldGicName + "<T>";
					}
					if (JavaClassValidateUtil.isArray(subTypeName)) {
						fieldGicName = fieldGicName.substring(0, fieldGicName.lastIndexOf("["));
						fieldGicName = "java.util.List<" + fieldGicName + ">";
					}
					String[] gNameArr = DocClassUtil.getSimpleGicName(fieldGicName);
					if (gNameArr.length == 0) {
						continue;
					}
					String gName = gNameArr[0];
					if (JavaClassValidateUtil.isPrimitive(gName)) {
						String builder = DocUtil.jsonValueByType(gName) + "," + DocUtil.jsonValueByType(gName);

						if (StringUtil.isEmpty(fieldValue)) {
							param.setValue(DocUtil.handleJsonStr(builder));
						}
						else {
							param.setValue(fieldValue);
						}
						processApiParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
					}
					else {
						processApiParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
						fieldPid = Optional.ofNullable(atomicInteger).isPresent() ? param.getId()
								: paramList.size() + pid;
						if (!simpleName.equals(gName)) {
							JavaClass arraySubClass = projectBuilder.getJavaProjectBuilder().getClassByName(gName);
							if (arraySubClass.isEnum()) {
								comment.append(handleEnumComment(arraySubClass, projectBuilder));
								param.setDesc(comment.toString());
								param.setType(ParamTypeConstants.PARAM_TYPE_ARRAY);

								EnumInfoAndValues enumInfoAndValue = JavaClassUtil.getEnumInfoAndValue(arraySubClass,
										projectBuilder, jsonRequest || isResp);
								if (Objects.nonNull(enumInfoAndValue)) {
									param.setValue("[" + enumInfoAndValue.getValue() + "]")
										.setEnumInfoAndValues(enumInfoAndValue);
								}
							}
							else if (gName.length() == 1) {
								// handle generic
								int len = globGicName.length;
								if (len < 1) {
									continue;
								}
								String gicName = genericMap.get(gName) != null ? genericMap.get(gName) : globGicName[0];

								if (!JavaClassValidateUtil.isPrimitive(gicName) && !simpleName.equals(gicName)) {
									paramList.addAll(buildParams(gicName, preBuilder.toString(), nextLevel, isRequired,
											isResp, registryClasses, projectBuilder, groupClasses,
											methodJsonViewClasses, fieldPid, jsonRequest, atomicInteger));
								}
							}
							else {
								paramList.addAll(buildParams(gName, preBuilder.toString(), nextLevel, isRequired,
										isResp, registryClasses, projectBuilder, groupClasses, methodJsonViewClasses,
										fieldPid, jsonRequest, atomicInteger));
							}
						}
						else {
							// Check configuration for self-reference in collections
							if (projectBuilder.getApiConfig().isAllowSelfReference()) {
								// Allow limited recursion when enabled
								if (level < 2) {
									paramList.addAll(buildParams(gName, preBuilder.toString(), nextLevel, isRequired,
											isResp, registryClasses, projectBuilder, groupClasses,
											methodJsonViewClasses, fieldPid, jsonRequest, atomicInteger));
								}
							}
							else {
								// Original behavior: set self reference loop
								param.setSelfReferenceLoop(true);
							}
						}
					}

				}
				else if (JavaClassValidateUtil.isMap(subTypeName)) {
					if (tagsMap.containsKey(DocTags.MOCK) && StringUtil.isNotEmpty(tagsMap.get(DocTags.MOCK))) {
						param.setType(ParamTypeConstants.PARAM_TYPE_MAP);
						param.setValue(fieldValue);
					}
					processApiParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
					fieldPid = Optional.ofNullable(atomicInteger).isPresent() ? param.getId() : paramList.size() + pid;
					String valType = DocClassUtil.getMapKeyValueType(fieldGicName).length == 0 ? fieldGicName
							: DocClassUtil.getMapKeyValueType(fieldGicName)[1];
					if (JavaClassValidateUtil.isMap(fieldGicName)
							|| JavaTypeConstants.JAVA_OBJECT_FULLY.equals(valType)) {
						ApiParam param1 = ApiParam.of()
							.setField(preBuilder + "any object")
							.setId(atomicOrDefault(atomicInteger, fieldPid + 1))
							.setPid(fieldPid)
							.setClassName(className)
							.setMaxLength(maxLength)
							.setType(ParamTypeConstants.PARAM_TYPE_OBJECT)
							.setDesc(DocGlobalConstants.ANY_OBJECT_MSG)
							.setVersion(DocGlobalConstants.DEFAULT_VERSION)
							.setExtensions(extensionParams);
						paramList.add(param1);
						continue;
					}
					if (!JavaClassValidateUtil.isPrimitive(valType)) {
						if (valType.length() == 1) {
							String gicName = genericMap.get(valType);
							if (!JavaClassValidateUtil.isPrimitive(gicName) && !simpleName.equals(gicName)) {
								paramList.addAll(buildParams(gicName, preBuilder.toString(), nextLevel, isRequired,
										isResp, registryClasses, projectBuilder, groupClasses, methodJsonViewClasses,
										fieldPid, jsonRequest, atomicInteger));
							}
						}
						else {
							paramList.addAll(buildParams(valType, preBuilder.toString(), nextLevel, isRequired, isResp,
									registryClasses, projectBuilder, groupClasses, methodJsonViewClasses, fieldPid,
									jsonRequest, atomicInteger));
						}
					}
				}
				else if (JavaTypeConstants.JAVA_OBJECT_FULLY.equals(fieldGicName)) {
					if (StringUtil.isEmpty(param.getDesc())) {
						param.setDesc(DocGlobalConstants.ANY_OBJECT_MSG);
					}
					processApiParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
				}
				else if (fieldGicName.length() == 1) {
					processApiParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
					fieldPid = Optional.ofNullable(atomicInteger).isPresent() ? param.getId() : paramList.size() + pid;
					// handle java generic or object
					if (!simpleName.equals(className)) {
						if (globGicName.length > 0) {
							String gicName = genericMap.get(subTypeName) != null ? genericMap.get(subTypeName)
									: globGicName[0];
							String simple = DocClassUtil.getSimpleName(gicName);
							// set type array
							if (JavaClassValidateUtil.isArray(gicName)) {
								param.setType(ParamTypeConstants.PARAM_TYPE_ARRAY);
							}
							if (JavaClassValidateUtil.isPrimitive(simple)) {
								// do nothing
							}
							else if (gicName.contains("<")) {
								if (JavaClassValidateUtil.isCollection(simple)) {
									param.setType(ParamTypeConstants.PARAM_TYPE_ARRAY);
									String gName = DocClassUtil.getSimpleGicName(gicName)[0];
									if (!JavaClassValidateUtil.isPrimitive(gName)) {
										paramList.addAll(buildParams(gName, preBuilder.toString(), nextLevel,
												isRequired, isResp, registryClasses, projectBuilder, groupClasses,
												methodJsonViewClasses, fieldPid, jsonRequest, atomicInteger));
									}
								}
								else {
									paramList.addAll(buildParams(gicName, preBuilder.toString(), nextLevel, isRequired,
											isResp, registryClasses, projectBuilder, groupClasses,
											methodJsonViewClasses, fieldPid, jsonRequest, atomicInteger));
								}
							}
							else {
								paramList.addAll(buildParams(gicName, preBuilder.toString(), nextLevel, isRequired,
										isResp, registryClasses, projectBuilder, groupClasses, methodJsonViewClasses,
										fieldPid, jsonRequest, atomicInteger));
							}
						}
						else {
							paramList.addAll(buildParams(subTypeName, preBuilder.toString(), nextLevel, isRequired,
									isResp, registryClasses, projectBuilder, groupClasses, methodJsonViewClasses,
									fieldPid, jsonRequest, atomicInteger));
						}
					}
				}
				else if (simpleName.equals(subTypeName)) {
					// reference self - check configuration
					if (projectBuilder.getApiConfig().isAllowSelfReference()) {
						// Allow limited recursion when enabled
						processApiParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
						fieldPid = Optional.ofNullable(atomicInteger).isPresent() ? param.getId()
								: paramList.size() + pid;
						if (level < 2) {
							paramList.addAll(buildParams(fieldGicName, preBuilder.toString(), nextLevel, isRequired,
									isResp, registryClasses, projectBuilder, groupClasses, methodJsonViewClasses,
									fieldPid, jsonRequest, atomicInteger));
						}
					}
					else {
						// Original behavior: create reference parameter
						ApiParam param1 = ApiParam.of()
							.setField(pre + fieldName)
							.setPid(pid)
							.setId(atomicOrDefault(atomicInteger, paramList.size() + pid + 1))
							.setClassName(subTypeName)
							.setMaxLength(maxLength)
							.setType(ParamTypeConstants.PARAM_TYPE_OBJECT)
							.setDesc(comment.append(" $ref... self").toString())
							.setVersion(DocGlobalConstants.DEFAULT_VERSION)
							.setExtensions(extensionParams);
						paramList.add(param1);
					}
				}
				else {
					processApiParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
					fieldGicName = DocUtil.formatFieldTypeGicName(genericMap, fieldGicName);
					fieldPid = Optional.ofNullable(atomicInteger).isPresent() ? param.getId() : paramList.size() + pid;
					paramList.addAll(buildParams(fieldGicName, preBuilder.toString(), nextLevel, isRequired, isResp,
							registryClasses, projectBuilder, groupClasses, methodJsonViewClasses, fieldPid, jsonRequest,
							atomicInteger));

				}
			}

		}
		return paramList;
		// end field
	}

	/**
	 * Builds a list of {@link ApiParam} objects for a map parameter.
	 * @param globGicName the global generic name array
	 * @param pre the prefix string
	 * @param level the level of the parameter
	 * @param isRequired the requirement status of the parameter
	 * @param isResp the response flag
	 * @param registryClasses the map of registry classes
	 * @param projectBuilder the project configuration builder
	 * @param groupClasses the set of group classes
	 * @param jsonViewClasses A set of valid `@JsonView` classes.
	 * @param pid the parent ID
	 * @param jsonRequest the JSON request flag
	 * @param nextLevel the next level of the parameter
	 * @param atomicInteger the atomic integer for generating unique IDs
	 * @return a list of {@link ApiParam} objects
	 */
	public static List<ApiParam> buildMapParam(String[] globGicName, String pre, int level, String isRequired,
			boolean isResp, Map<String, String> registryClasses, ProjectDocConfigBuilder projectBuilder,
			Set<String> groupClasses, Set<String> jsonViewClasses, int pid, boolean jsonRequest, int nextLevel,
			AtomicInteger atomicInteger) {
		if (globGicName.length != 2) {
			return Collections.emptyList();
		}

		// mock map key param
		String mapKeySimpleName = DocClassUtil.getSimpleName(globGicName[0]);
		String valueSimpleName = DocClassUtil.getSimpleName(globGicName[1]);
		// get map key class
		JavaClass mapKeyClass = projectBuilder.getClassByName(mapKeySimpleName);

		boolean isShowJavaType = projectBuilder.getApiConfig().getShowJavaType();
		String valueSimpleNameType = processFieldTypeName(isShowJavaType, valueSimpleName);
		List<ApiParam> paramList = new ArrayList<>();
		// map key is enum
		if (Objects.nonNull(mapKeyClass) && mapKeyClass.isEnum() && !mapKeyClass.getEnumConstants().isEmpty()) {
			Integer keyParentId = null;
			for (JavaField enumConstant : mapKeyClass.getEnumConstants()) {
				ApiParam apiParam = ApiParam.of()
					.setField(pre + enumConstant.getName())
					.setType(valueSimpleNameType)
					.setClassName(valueSimpleName)
					.setDesc(StringUtil.isEmpty(enumConstant.getComment()) ? enumConstant.getName()
							: enumConstant.getComment() + " "
									+ Optional.ofNullable(projectBuilder.getClassByName(valueSimpleName))
										.map(JavaClass::getComment)
										.orElse(DocGlobalConstants.DEFAULT_MAP_KEY_DESC))
					.setVersion(DocGlobalConstants.DEFAULT_VERSION)
					.setPid(null == keyParentId ? pid : keyParentId);

				Object enumValueWithJsonValue = JavaClassUtil.getEnumValueWithJsonValue(mapKeyClass, projectBuilder,
						enumConstant);
				if ((isResp || jsonRequest) && enumValueWithJsonValue != null) {
					apiParam.setField(pre + enumValueWithJsonValue);
				}
				apiParam.setId(apiParam.getPid() + paramList.size() + 1);
				if (null == keyParentId) {
					keyParentId = apiParam.getPid();
				}
				paramList.add(apiParam);
				// in foreach, need remove enum class in registry
				registryClasses.remove(valueSimpleName);
				List<ApiParam> apiParams = addValueParams(valueSimpleName, globGicName, level, isRequired, isResp,
						registryClasses, projectBuilder, groupClasses, jsonViewClasses, apiParam.getId(), jsonRequest,
						nextLevel, atomicInteger);
				paramList.addAll(apiParams);
			}
			return paramList;
		}
		// map key is primitive
		if (JavaClassValidateUtil.isPrimitive(mapKeySimpleName)) {
			ApiParam apiParam = ApiParam.of()
				.setField(pre + "mapKey")
				.setType(valueSimpleNameType)
				.setClassName(valueSimpleName)
				.setDesc(Optional.ofNullable(projectBuilder.getClassByName(valueSimpleName))
					.map(JavaClass::getComment)
					.orElse(DocGlobalConstants.DEFAULT_MAP_KEY_DESC))
				.setVersion(DocGlobalConstants.DEFAULT_VERSION)
				.setPid(pid)
				.setId(atomicOrDefault(atomicInteger, ++pid));
			paramList.add(apiParam);
		}

		paramList.addAll(addValueParams(valueSimpleName, globGicName, level, isRequired, isResp, registryClasses,
				projectBuilder, groupClasses, jsonViewClasses, pid, jsonRequest, nextLevel, atomicInteger));
		return paramList;
	}

	/**
	 * Adds parameters for the map value to the parameter list.
	 * @param valueSimpleName the simple name of the value type
	 * @param globGicName the global generic name array
	 * @param level the level of the parameter
	 * @param isRequired the requirement status of the parameter
	 * @param isResp the response flag
	 * @param registryClasses the map of registry classes
	 * @param projectBuilder the project configuration builder
	 * @param groupClasses the set of group classes
	 * @param jsonViewClasses A set of valid `@JsonView` classes.
	 * @param pid the parent ID
	 * @param jsonRequest the JSON request flag
	 * @param nextLevel the next level of the parameter
	 * @param atomicInteger the atomic integer for generating unique IDs
	 * @return the list of {@link ApiParam} objects
	 */
	private static List<ApiParam> addValueParams(String valueSimpleName, String[] globGicName, int level,
			String isRequired, boolean isResp, Map<String, String> registryClasses,
			ProjectDocConfigBuilder projectBuilder, Set<String> groupClasses, Set<String> jsonViewClasses, int pid,
			boolean jsonRequest, int nextLevel, AtomicInteger atomicInteger) {
		// build param when map value is not primitive
		if (JavaClassValidateUtil.isPrimitive(valueSimpleName)) {
			return Collections.emptyList();
		}
		return buildParams(globGicName[1], DocUtil.getIndentByLevel(level), nextLevel, isRequired, isResp,
				registryClasses, projectBuilder, groupClasses, jsonViewClasses, pid, jsonRequest, atomicInteger);
	}

	public static String dictionaryListComment(List<EnumDictionary> enumDataDict) {
		return enumDataDict.stream()
			.map(apiDataDictionary -> apiDataDictionary.getName() + "-(\"" + apiDataDictionary.getValue() + "\",\""
					+ apiDataDictionary.getDesc() + "\")")
			.collect(Collectors.joining(","));
	}

	public static List<ApiParam> primitiveReturnRespComment(String typeName, AtomicInteger atomicInteger, int pid) {
		String comments = "Return " + typeName + ".";
		ApiParam apiParam = ApiParam.of()
			.setClassName(typeName)
			.setId(atomicOrDefault(atomicInteger, pid + 1))
			.setField("-")
			.setPid(pid)
			.setType(typeName)
			.setDesc(comments)
			.setVersion(DocGlobalConstants.DEFAULT_VERSION);

		List<ApiParam> paramList = new ArrayList<>();
		paramList.add(apiParam);
		return paramList;
	}

	/**
	 * Processes and sets properties for an API parameter and adds it to the provided
	 * list.
	 * @param paramList The list of API parameters to which the processed parameter will
	 * be added.
	 * @param param The API parameter to process and set properties for.
	 * @param isRequired A string indicating if the parameter is required (can be empty).
	 * @param comment The description or comment for the parameter.
	 * @param since The version information for the parameter.
	 * @param strRequired A boolean indicating whether the parameter is required.
	 */
	private static void processApiParam(List<ApiParam> paramList, ApiParam param, String isRequired, String comment,
			String since, boolean strRequired) {
		if (StringUtil.isEmpty(isRequired)) {
			param.setDesc(comment).setVersion(since);
		}
		else {
			param.setDesc(comment).setVersion(since).setRequired(strRequired);
		}
		paramList.add(param);
	}

	/**
	 * Handles the generation of comments for enum types in a Java class. If the class is
	 * an enum, it generates the corresponding enum comment based on the project
	 * configuration; otherwise, it returns an empty comment string.
	 * @param javaClass The JavaClass object containing class information.
	 * @param projectBuilder The ProjectDocConfigBuilder object containing project
	 * configuration information.
	 * @return The generated enum comment string.
	 */
	public static String handleEnumComment(JavaClass javaClass, ProjectDocConfigBuilder projectBuilder) {
		String comment = "";
		if (!javaClass.isEnum()) {
			return comment;
		}
		String enumComments = javaClass.getComment();
		if (Boolean.TRUE.equals(projectBuilder.getApiConfig().getInlineEnum())) {
			ApiDataDictionary dataDictionary = projectBuilder.getApiConfig()
				.getDataDictionary(javaClass.getBinaryName());
			if (Objects.isNull(dataDictionary)) {
				// the output format should be unified ( as same as the "else" output)
				comment = comment + "<br/>[Enum: " + JavaClassUtil.getEnumParams(javaClass) + "]";
			}
			else {
				Class<?> enumClass = dataDictionary.getEnumClass();
				if (enumClass.isInterface()) {
					ClassLoader classLoader = projectBuilder.getApiConfig().getClassLoader();
					try {
						enumClass = classLoader.loadClass(javaClass.getBinaryName());
					}
					catch (ClassNotFoundException e) {
						return comment;
					}
				}
				comment = comment + "<br/>[Enum: " + dictionaryListComment(dataDictionary.getEnumDataDict(enumClass))
						+ "]";
			}
		}
		else {
			if (StringUtil.isNotEmpty(enumComments)) {
				comment = comment + "<br/>(See: " + enumComments + ")";
			}
			comment = StringUtil.removeQuotes(comment);
		}

		if (Boolean.TRUE.equals(projectBuilder.getApiConfig().isDisplayActualType())) {
			comment = comment + " (ActualType: " + javaClass.getSimpleName() + ")";
		}

		return comment;
	}

	/**
	 * Returns the next value of the specified atomic integer or the default value if the
	 * atomic integer is null.
	 * @param atomicInteger the atomic integer
	 * @param defaultVal the default value
	 * @return the next value of the atomic integer or the default value
	 */
	private static int atomicOrDefault(AtomicInteger atomicInteger, int defaultVal) {
		if (null != atomicInteger) {
			return atomicInteger.incrementAndGet();
		}
		return defaultVal;
	}

	/**
	 * Processes the field type name based on the specified flag.
	 * @param isShowJavaType a flag indicating whether to show the Java type or not
	 * @param fieldTypeName the field type name to be processed
	 * @return the processed field type name
	 */
	private static String processFieldTypeName(boolean isShowJavaType, String fieldTypeName) {
		if (isShowJavaType) {
			return JavaFieldUtil.convertToSimpleTypeName(fieldTypeName);
		}
		else {
			return DocClassUtil.processTypeNameForParams(fieldTypeName.toLowerCase());
		}
	}

	/**
	 * Handles primitive types and returns a list of {@link ApiParam} objects.
	 * @param simpleName the simple name of the primitive type
	 * @param isShowJavaType a flag indicating whether to show the Java type or not
	 * @param atomicInteger the atomic integer for generating unique IDs
	 * @param pid the parent ID
	 * @return the list of {@link ApiParam} objects
	 */
	private static List<ApiParam> handlePrimitiveType(String simpleName, boolean isShowJavaType,
			AtomicInteger atomicInteger, int pid) {
		String processedType = processFieldTypeName(isShowJavaType, simpleName);
		return primitiveReturnRespComment(processedType, atomicInteger, pid);
	}

	/**
	 * Builds a list of {@link ApiParam} objects for a map type.
	 * @param globGicName the generic canonical name of the map type
	 * @param pre the prefix for the field name
	 * @param level the level of the parameter
	 * @param isRequired the required flag for the parameter
	 * @param isResp the response flag for the parameter
	 * @param registryClasses the registry classes
	 * @param projectBuilder the project builder
	 * @param groupClasses the group classes
	 * @param methodJsonViewClasses the method JSON view classes
	 * @param pid the parent ID
	 * @param jsonRequest the request flag for the parameter
	 * @param atomicInteger the atomic integer for generating unique IDs
	 * @param simpleName the simple name of the collection type
	 *
	 */
	private static List<ApiParam> handleCollectionOrArrayType(String[] globGicName, String pre, int level,
			String isRequired, boolean isResp, Map<String, String> registryClasses,
			ProjectDocConfigBuilder projectBuilder, Set<String> groupClasses, Set<String> methodJsonViewClasses,
			int pid, boolean jsonRequest, AtomicInteger atomicInteger, String simpleName) {
		List<ApiParam> paramList = new ArrayList<>();
		if (!JavaClassValidateUtil.isCollection(globGicName[0])) {
			String gNameTemp = globGicName[0];
			String gName = JavaClassValidateUtil.isArray(gNameTemp) ? gNameTemp.substring(0, gNameTemp.indexOf("["))
					: globGicName[0];
			if (JavaClassValidateUtil.isPrimitive(gName)) {
				String processedType = projectBuilder.getApiConfig().getShowJavaType()
						? JavaFieldUtil.convertToSimpleTypeName(simpleName)
						: DocClassUtil.processTypeNameForParams(gName);
				ApiParam param = ApiParam.of()
					.setId(atomicOrDefault(atomicInteger, pid + 1))
					.setField(pre + " -")
					.setType("array[" + processedType + "]")
					.setPid(pid)
					.setDesc("array of " + processedType)
					.setVersion(DocGlobalConstants.DEFAULT_VERSION)
					.setRequired(Boolean.parseBoolean(isRequired));
				paramList.add(param);
			}
			else {
				if (JavaClassValidateUtil.isArray(gNameTemp)) {
					gNameTemp = gNameTemp.substring(0, gNameTemp.indexOf("["));
				}
				paramList.addAll(buildParams(gNameTemp, pre, level + 1, isRequired, isResp, registryClasses,
						projectBuilder, groupClasses, methodJsonViewClasses, pid, jsonRequest, atomicInteger));
			}
		}
		return paramList;
	}

	/**
	 * Builds a list of {@link ApiParam} objects for a generic object type.
	 * @param className the canonical name of the generic object type
	 * @param pre the prefix for the field name
	 * @param isRequired the required flag for the parameter
	 * @param atomicInteger the atomic integer for generating unique IDs
	 * @param pid the parent ID
	 * @return the list of {@link ApiParam} objects
	 */
	private static List<ApiParam> buildGenericObjectParam(String className, String pre, String isRequired,
			AtomicInteger atomicInteger, int pid) {
		List<ApiParam> paramList = new ArrayList<>();
		ApiParam apiParam = ApiParam.of()
			.setClassName(className)
			.setId(atomicOrDefault(atomicInteger, pid + 1))
			.setField(pre + "any object")
			.setType(ParamTypeConstants.PARAM_TYPE_OBJECT)
			.setPid(pid)
			.setDesc(DocGlobalConstants.ANY_OBJECT_MSG)
			.setVersion(DocGlobalConstants.DEFAULT_VERSION)
			.setRequired(Boolean.parseBoolean(isRequired));
		paramList.add(apiParam);
		return paramList;
	}

}
