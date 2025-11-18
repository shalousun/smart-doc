/*
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
import io.github.smartdoc.model.ApiReturn;
import io.github.smartdoc.model.CustomField;
import io.github.smartdoc.model.CustomFieldInfo;
import io.github.smartdoc.model.DocJavaField;
import io.github.smartdoc.model.DocJavaMethod;
import io.github.smartdoc.model.FieldJsonAnnotationInfo;
import io.github.smartdoc.utils.DocClassUtil;
import io.github.smartdoc.utils.DocUtil;
import io.github.smartdoc.utils.JavaClassUtil;
import io.github.smartdoc.utils.JavaClassValidateUtil;
import io.github.smartdoc.utils.JavaFieldUtil;
import io.github.smartdoc.utils.JsonUtil;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaType;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static io.github.smartdoc.constants.DocTags.IGNORE_RESPONSE_BODY_ADVICE;

/**
 * Json Builder
 *
 * @author yu 2019/12/21.
 */
public class JsonBuildHelper extends BaseHelper {

	/**
	 * build return json
	 * @param docJavaMethod The JavaMethod object
	 * @param builder ProjectDocConfigBuilder builder
	 * @return String
	 */
	public static String buildReturnJson(DocJavaMethod docJavaMethod, ProjectDocConfigBuilder builder) {
		JavaMethod method = docJavaMethod.getJavaMethod();
		String responseBodyAdvice = null;
		if (Objects.nonNull(builder.getApiConfig().getResponseBodyAdvice())) {
			responseBodyAdvice = builder.getApiConfig().getResponseBodyAdvice().getClassName();
		}
		if (method.getReturns().isVoid() && Objects.isNull(responseBodyAdvice)) {
			return "Return void.";
		}
		DocletTag downloadTag = method.getTagByName(DocTags.DOWNLOAD);
		if (Objects.nonNull(downloadTag)) {
			return "File download.";
		}
		if (method.getReturns().isEnum() && Objects.isNull(responseBodyAdvice)) {
			return StringUtil
				.removeQuotes(String.valueOf(JavaClassUtil.getEnumValue(method.getReturns(), builder, Boolean.TRUE)));
		}
		if (method.getReturns().isPrimitive() && Objects.isNull(responseBodyAdvice)) {
			String typeName = method.getReturnType().getCanonicalName();
			return StringUtil.removeQuotes(DocUtil.jsonValueByType(typeName));
		}
		if (JavaTypeConstants.JAVA_STRING_FULLY.equals(method.getReturnType().getGenericCanonicalName())
				&& Objects.isNull(responseBodyAdvice)) {
			return "string";
		}
		String returnTypeGenericCanonicalName = method.getReturnType().getGenericCanonicalName();
		if (Objects.nonNull(responseBodyAdvice) && Objects.isNull(method.getTagByName(IGNORE_RESPONSE_BODY_ADVICE))) {
			if (!returnTypeGenericCanonicalName.startsWith(responseBodyAdvice)) {
				returnTypeGenericCanonicalName = responseBodyAdvice + "<" + returnTypeGenericCanonicalName + ">";
			}
		}
		ApiReturn apiReturn = DocClassUtil.processReturnType(returnTypeGenericCanonicalName);
		String typeName = apiReturn.getSimpleName();
		if (JavaClassValidateUtil.isFileDownloadResource(typeName)) {
			docJavaMethod.setDownload(true);
			return "File download.";
		}
		Map<String, JavaType> actualTypesMap = docJavaMethod.getActualTypesMap();
		String returnType = apiReturn.getGenericCanonicalName();
		if (Objects.nonNull(actualTypesMap)) {
			typeName = JavaClassUtil.getGenericsNameByActualTypesMap(typeName, actualTypesMap);
			returnType = JavaClassUtil.getGenericsNameByActualTypesMap(returnType, actualTypesMap);
		}
		if (JavaClassValidateUtil.isPrimitive(typeName)) {
			if (JavaTypeConstants.JAVA_STRING_FULLY.equals(typeName)) {
				return "string";
			}
			return StringUtil.removeQuotes(DocUtil.jsonValueByType(typeName));
		}

		return JsonUtil.toPrettyFormat(buildJson(typeName, returnType, Boolean.TRUE, 0, new HashMap<>(16),
				Collections.emptySet(), docJavaMethod.getJsonViewClasses(), builder));
	}

	/**
	 * Builds a JSON string representation of a given type.
	 * @param typeName The name of the type.
	 * @param genericCanonicalName The canonical name of the generic type.
	 * @param isResp Flag indicating if this is a response.
	 * @param counter The recursion counter.
	 * @param registryClasses A map to keep track of processed classes.
	 * @param groupClasses A set of valid group classes.
	 * @param methodJsonViewClasses A set of valid `@JsonView` classes on controller
	 * method.
	 * @param projectBuilder The project config projectBuilder.
	 * @return The JSON string representation of the type.
	 */
	public static String buildJson(String typeName, String genericCanonicalName, boolean isResp, int counter,
			Map<String, String> registryClasses, Set<String> groupClasses, Set<String> methodJsonViewClasses,
			ProjectDocConfigBuilder projectBuilder) {

		if (StringUtil.isEmpty(typeName)) {
			throw new RuntimeException("Class name can't be null or empty.");
		}

		// Check for recursion limit to avoid infinite loops
		int recursionLimit = projectBuilder.getApiConfig().getRecursionLimit();

		// Early exit when recursion limit is hit
		if (counter > recursionLimit) {
			return "{\"$ref\":\"...\"}";
		}

		int nextLevel = counter + 1;
		// Avoid processing the same class multiple times
		if (registryClasses.containsKey(typeName) && counter > registryClasses.size()) {
			return "{\"$ref\":\"...\"}";
		}

		// Registry class
		registryClasses.put(typeName, typeName);

		Map<String, String> genericMap = new HashMap<>(10);
		JavaClass javaClass = projectBuilder.getJavaProjectBuilder().getClassByName(typeName);

		// Check if the class should be ignored based on MVC parameters
		if (JavaClassValidateUtil.isMvcIgnoreParams(typeName, projectBuilder.getApiConfig().getIgnoreRequestParams())) {
			if (DocGlobalConstants.MODE_AND_VIEW_FULLY.equals(typeName)) {
				return "Forward or redirect to a page view.";
			}
			else {
				return "Error restful return.";
			}
		}

		// Handle primitive types
		if (JavaClassValidateUtil.isPrimitive(typeName)) {
			return StringUtil.removeQuotes(DocUtil.jsonValueByType(typeName));
		}

		// Handle enum types
		if (javaClass.isEnum()) {
			return StringUtil
				.removeQuotes(String.valueOf(JavaClassUtil.getEnumValue(javaClass, projectBuilder, Boolean.TRUE)));
		}

		StringBuilder result = new StringBuilder();
		JavaClass cls = projectBuilder.getClassByName(typeName);

		result.append("{");
		String[] globGicName = DocClassUtil.getSimpleGicName(genericCanonicalName);

		// Obtain generics from parent class if not found
		if (Objects.isNull(globGicName) || globGicName.length < 1) {
			// obtain generics from parent class
			JavaClass superJavaClass = cls != null ? cls.getSuperJavaClass() : null;
			if (Objects.nonNull(superJavaClass)
					&& !JavaTypeConstants.OBJECT_SIMPLE_NAME.equals(superJavaClass.getSimpleName())) {
				globGicName = DocClassUtil.getSimpleGicName(superJavaClass.getGenericFullyQualifiedName());
			}
		}
		JavaClassUtil.genericParamMap(genericMap, cls, globGicName);
		StringBuilder data = new StringBuilder();

		// Handle collection types
		if (JavaClassValidateUtil.isCollection(typeName) || JavaClassValidateUtil.isArray(typeName)) {
			data.append("[");
			if (globGicName.length == 0) {
				data.append("{\"object\":\"any object\"}");
				data.append("]");
				return data.toString();
			}
			String gNameTemp = globGicName[0];
			String gName = JavaClassValidateUtil.isArray(gNameTemp) ? gNameTemp.substring(0, gNameTemp.indexOf("["))
					: globGicName[0];
			if (JavaTypeConstants.JAVA_OBJECT_FULLY.equals(gName)) {
				data.append(DocGlobalConstants.GENERIC_LIST_WARNING);
			}
			else if (JavaClassValidateUtil.isPrimitive(gName)) {
				data.append(DocUtil.jsonValueByType(gName)).append(",");
				data.append(DocUtil.jsonValueByType(gName));
			}
			else if (gName.contains("<")) {
				String simple = DocClassUtil.getSimpleName(gName);
				String json = buildJson(simple, gName, isResp, nextLevel, registryClasses, groupClasses,
						methodJsonViewClasses, projectBuilder);
				data.append(json);
			}
			else if (JavaClassValidateUtil.isCollection(gName)) {
				data.append("\"any object\"");
			}
			else {
				String json = buildJson(gName, gName, isResp, nextLevel, registryClasses, groupClasses,
						methodJsonViewClasses, projectBuilder);
				data.append(json);
			}
			data.append("]");
			return data.toString();
		}
		// Handle map types
		if (JavaClassValidateUtil.isMap(typeName)) {
			buildMapJson(genericCanonicalName, isResp, counter, registryClasses, groupClasses, methodJsonViewClasses,
					projectBuilder, data, nextLevel);
			return data.toString();
		}
		// Handle generic object types
		if (JavaTypeConstants.JAVA_OBJECT_FULLY.equals(typeName)) {
			data.append("{\"object\":\" any object\"},");
			// throw new RuntimeException("Please do not return java.lang.Object directly
			// in api interface.");
		}
		// Handle Reactor types
		else if (JavaClassValidateUtil.isReactor(typeName)) {
			data.append(buildJson(globGicName[0], typeName, isResp, nextLevel, registryClasses, groupClasses,
					methodJsonViewClasses, projectBuilder));
			return data.toString();
		}
		// Process fields of the class
		else {
			boolean requestFieldToUnderline = projectBuilder.getApiConfig().isRequestFieldToUnderline();
			boolean responseFieldToUnderline = projectBuilder.getApiConfig().isResponseFieldToUnderline();
			List<DocJavaField> fields = JavaClassUtil.getFields(cls, 0, new LinkedHashMap<>(),
					projectBuilder.getApiConfig().getClassLoader());

			// get ignore fields from class
			Map<String, String> ignoreFields = JavaClassUtil.getClassJsonIgnoreFields(cls);

			// Process each field of the class
			for (DocJavaField docField : fields) {
				JavaField field = docField.getJavaField();
				// ignore transient field
				if (isTransientField(field, projectBuilder, isResp)) {
					continue;
				}

				String fieldName = docField.getFieldName();

				// if ignore fields contains the field name, then skip this field
				if (ignoreFields.containsKey(fieldName)) {
					continue;
				}
				String subTypeName = docField.getTypeFullyQualifiedName();

				// if the field name is underlined, then convert it to camel case
				if ((responseFieldToUnderline && isResp) || (requestFieldToUnderline && !isResp)) {
					fieldName = StringUtil.camelToUnderline(fieldName);
				}

				// get tags value from the field
				Map<String, String> tagsMap = DocUtil.getFieldTagsValue(field, docField);

				// field json annotation
				FieldJsonAnnotationInfo annotationInfo = getFieldJsonAnnotationInfo(projectBuilder, docField, isResp,
						groupClasses, methodJsonViewClasses);

				if (Boolean.TRUE.equals(annotationInfo.getIgnore())) {
					continue;
				}
				// the param value from @JsonFormat
				String fieldJsonFormatValue = annotationInfo.getFieldJsonFormatValue();
				// has Annotation @JsonSerialize And using ToStringSerializer
				boolean toStringSerializer = Boolean.TRUE.equals(annotationInfo.getToStringSerializer());
				if (StringUtil.isNotEmpty(annotationInfo.getFieldName())) {
					fieldName = annotationInfo.getFieldName();
				}

				String typeSimpleName = docField.getTypeSimpleName();
				String fieldGicName = docField.getTypeGenericCanonicalName();

				CustomFieldInfo customFieldInfo = getCustomFieldInfo(projectBuilder, docField, isResp, typeSimpleName,
						fieldName);
				// ignore custom field
				if (Boolean.TRUE.equals(customFieldInfo.getIgnore())) {
					continue;
				}
				if (StringUtil.isNotEmpty(customFieldInfo.getFieldName())) {
					fieldName = customFieldInfo.getFieldName();
				}

				fieldName = fieldName.trim();
				result.append("\"").append(fieldName).append("\":");
				// get mock value from tag @mock
				String fieldValue = getFieldValueFromMockForJson(subTypeName, tagsMap, typeSimpleName);
				// if the field is primitive type, then get the default value
				if (JavaClassValidateUtil.isPrimitive(subTypeName)) {
					int data0Length = result.length();
					if (StringUtil.isEmpty(fieldValue)) {
						String valueByTypeAndFieldName = DocUtil.getValByTypeAndFieldName(typeSimpleName,
								field.getName());
						if (toStringSerializer && isResp) {
							fieldValue = valueByTypeAndFieldName.startsWith("\"")
									&& valueByTypeAndFieldName.endsWith("\"") ? valueByTypeAndFieldName
											: DocUtil.handleJsonStr(valueByTypeAndFieldName);
						}
						else {
							fieldValue = StringUtil.isNotEmpty(fieldJsonFormatValue) ? fieldJsonFormatValue
									: valueByTypeAndFieldName;
						}
					}

					CustomField customResponseField = customFieldInfo.getCustomResponseField();
					CustomField customRequestField = customFieldInfo.getCustomRequestField();
					if (Objects.nonNull(customRequestField) && !isResp
							&& typeName.equals(customRequestField.getOwnerClassName())) {
						JavaFieldUtil.buildCustomField(result, typeSimpleName, customRequestField);
					}
					if (Objects.nonNull(customResponseField) && isResp
							&& typeName.equals(customResponseField.getOwnerClassName())) {
						JavaFieldUtil.buildCustomField(result, typeSimpleName, customResponseField);
					}
					if (result.length() == data0Length) {
						result.append(fieldValue).append(",");
					}
				}
				else {
					// collection or array
					if (JavaClassValidateUtil.isCollection(subTypeName) || JavaClassValidateUtil.isArray(subTypeName)) {
						if (StringUtil.isNotEmpty(fieldValue)) {
							result.append(fieldValue).append(",");
							continue;
						}
						if (globGicName.length > 0 && JavaTypeConstants.JAVA_LIST_FULLY.equals(fieldGicName)) {
							fieldGicName = fieldGicName + "<T>";
						}
						if (JavaClassValidateUtil.isArray(subTypeName)) {
							fieldGicName = fieldGicName.substring(0, fieldGicName.lastIndexOf("["));
							fieldGicName = "java.util.List<" + fieldGicName + ">";
						}
						String[] gicNameArray = DocClassUtil.getSimpleGicName(fieldGicName);
						String gicName = gicNameArray[0];
						if (JavaTypeConstants.JAVA_STRING_FULLY.equals(gicName)) {
							result.append("[").append(DocUtil.jsonValueByType(gicName)).append("]").append(",");
						}
						else if (JavaTypeConstants.JAVA_LIST_FULLY.equals(gicName)) {
							result.append("[{\"object\":\"any object\"}],");
						}
						else if (gicName.length() == 1) {
							if (globGicName.length == 0) {
								result.append("[{\"object\":\"any object\"}],");
								continue;
							}
							String gicName1 = genericMap.get(gicName) == null ? globGicName[0]
									: genericMap.get(gicName);
							if (JavaTypeConstants.JAVA_STRING_FULLY.equals(gicName1)) {
								result.append("[").append(DocUtil.jsonValueByType(gicName1)).append("]").append(",");
							}
							else {
								if (!typeName.equals(gicName1)) {
									result.append("[")
										.append(buildJson(DocClassUtil.getSimpleName(gicName1), gicName1, isResp,
												nextLevel, registryClasses, groupClasses, methodJsonViewClasses,
												projectBuilder))
										.append("]")
										.append(",");
								}
								else {
									result.append("[{\"$ref\":\"..\"}]").append(",");
								}
							}
						}
						else {
							if (!typeName.equals(gicName)) {
								if (JavaClassValidateUtil.isMap(gicName)) {
									result.append("[{\"mapKey\":{}}],");
									continue;
								}
								JavaClass arraySubClass = projectBuilder.getJavaProjectBuilder()
									.getClassByName(gicName);
								if (arraySubClass.isEnum()) {
									Object value = JavaClassUtil.getEnumValue(arraySubClass, projectBuilder,
											Boolean.TRUE);
									result.append("[").append(value).append("],");
									continue;
								}
								gicName = DocClassUtil.getSimpleName(gicName);
								fieldGicName = DocUtil.formatFieldTypeGicName(genericMap, fieldGicName);
								result.append("[")
									.append(buildJson(gicName, fieldGicName, isResp, nextLevel, registryClasses,
											groupClasses, methodJsonViewClasses, projectBuilder))
									.append("]")
									.append(",");
							}
							else {
								result.append("[{\"$ref\":\"..\"}]").append(",");
							}
						}
					}
					// when the field is map
					else if (JavaClassValidateUtil.isMap(subTypeName)) {
						if (StringUtil.isNotEmpty(fieldValue)) {
							result.append(fieldValue).append(",");
							continue;
						}
						if (JavaClassValidateUtil.isMap(fieldGicName)) {
							result.append("{").append("\"mapKey\":{}},");
							continue;
						}
						buildMapJson(fieldGicName, isResp, nextLevel, registryClasses, groupClasses,
								methodJsonViewClasses, projectBuilder, result, nextLevel);
					}
					else if (fieldGicName.length() == 1) {
						if (!typeName.equals(genericCanonicalName)) {
							String gicName = genericMap.get(subTypeName) == null ? globGicName[0]
									: genericMap.get(subTypeName);
							if (JavaClassValidateUtil.isPrimitive(gicName)) {
								result.append(DocUtil.jsonValueByType(gicName)).append(",");
							}
							else {
								String simple = DocClassUtil.getSimpleName(gicName);
								result
									.append(buildJson(simple, gicName, isResp, nextLevel, registryClasses, groupClasses,
											methodJsonViewClasses, projectBuilder))
									.append(",");
							}
						}
						else {
							result.append("{},");
						}
					}
					// Object
					else if (JavaTypeConstants.JAVA_OBJECT_FULLY.equals(fieldGicName)) {
						if (StringUtil.isNotEmpty(field.getComment())) {
							// from source code
							result.append("{\"object\":\"any object\"},");
						}
						else {
							result.append("{},");
						}
					}
					else if (typeName.equals(fieldGicName)) {
						result.append("{\"$ref\":\"...\"}").append(",");
					}
					else {
						javaClass = field.getType();
						// if enum
						if (javaClass.isEnum()) {
							// Override old value
							if (tagsMap.containsKey(DocTags.MOCK) && StringUtil.isNotEmpty(tagsMap.get(DocTags.MOCK))) {
								result.append(tagsMap.get(DocTags.MOCK)).append(",");
							}
							// if has Annotation @JsonSerialize And using
							// ToStringSerializer && isResp
							else if (toStringSerializer && isResp) {
								Object value = JavaClassUtil.getEnumValue(javaClass, projectBuilder, Boolean.TRUE);
								result.append(value).append(",");
							}
							// if has @JsonFormat
							else if (StringUtil.isNotEmpty(fieldJsonFormatValue)) {
								result.append(fieldJsonFormatValue).append(",");
							}
							else {
								Object value = JavaClassUtil.getEnumValue(javaClass, projectBuilder, Boolean.TRUE);
								result.append(value).append(",");
							}
						}
						else {
							// if has Annotation @JsonSerialize And using
							// ToStringSerializer && isResp
							if (toStringSerializer && isResp) {
								result.append(" ").append(",");
							}
							else if (StringUtil.isNotEmpty(fieldJsonFormatValue)) {
								result.append(fieldJsonFormatValue).append(",");
							}
							else {
								fieldGicName = DocUtil.formatFieldTypeGicName(genericMap, fieldGicName);
								result
									.append(buildJson(subTypeName, fieldGicName, isResp, nextLevel, registryClasses,
											groupClasses, methodJsonViewClasses, projectBuilder))
									.append(",");
							}
						}
					}
				}
			}
		}
		// Remove the trailing comma
		if (result.charAt(result.length() - 1) == ',') {
			result.deleteCharAt(result.length() - 1);
		}
		result.append("}");
		return result.toString();
	}

	/**
	 * build map json
	 * @param genericCanonicalName genericCanonicalName
	 * @param isResp isResp
	 * @param counter counter
	 * @param registryClasses registryClasses
	 * @param groupClasses groupClasses
	 * @param methodJsonViewClasses methodJsonViewClasses
	 * @param builder builder
	 * @param data StringBuilder data
	 * @param nextLevel nextLevel
	 */
	public static void buildMapJson(String genericCanonicalName, boolean isResp, int counter,
			Map<String, String> registryClasses, Set<String> groupClasses, Set<String> methodJsonViewClasses,
			ProjectDocConfigBuilder builder, StringBuilder data, int nextLevel) {
		String[] getKeyValType = DocClassUtil.getMapKeyValueType(genericCanonicalName);
		if (getKeyValType.length == 0) {
			data.append("{\"mapKey\":{}}");
			return;
		}
		JavaClass mapKeyClass = builder.getJavaProjectBuilder().getClassByName(getKeyValType[0]);
		boolean mapKeyIsEnum = mapKeyClass.isEnum();
		String keyType = getKeyValType[0];

		if (builder.getApiConfig().isStrict()) {
			boolean isStringKey = JavaTypeConstants.JAVA_STRING_FULLY.equals(keyType);
			boolean isValidEnumKey = mapKeyIsEnum && !mapKeyClass.getEnumConstants().isEmpty();
			if (!isStringKey && !isValidEnumKey) {
				throw new RuntimeException("Map's key can only use String or Enum for JSON, but you used: " + keyType);
			}
		}
		String gicName = getKeyValType[1];
		// when map key is enum
		if (mapKeyIsEnum) {
			String mapValueSimpleName = DocClassUtil.getSimpleName(gicName);
			// Handle primitive types
			data.append("{");
			for (JavaField field : mapKeyClass.getFields()) {
				data.append("\"")
					.append(field.getName())
					.append("\":")
					.append(JavaClassValidateUtil.isPrimitive(mapValueSimpleName)
							? DocUtil.jsonValueByType(mapValueSimpleName)
							: buildJson(mapValueSimpleName, gicName, isResp, counter + 1, registryClasses, groupClasses,
									methodJsonViewClasses, builder))
					.append(",");
			}
			// Remove the trailing comma
			if (data.charAt(data.length() - 1) == ',') {
				data.deleteCharAt(data.length() - 1);
			}
			data.append("}");
			return;
		}

		// when map value is Object
		if (JavaTypeConstants.JAVA_OBJECT_FULLY.equals(gicName)) {
			data.append("{").append("\"mapKey\":").append(DocGlobalConstants.OBJECT_MAP_VALUE_WARNING).append("}");
			return;
		}

		// when map value is primitive
		if (JavaClassValidateUtil.isPrimitive(gicName)) {
			data.append("{").append("\"mapKey1\":").append(DocUtil.jsonValueByType(gicName)).append(",");
			data.append("\"mapKey2\":").append(DocUtil.jsonValueByType(gicName)).append("}");
			return;
		}

		if (gicName.contains("<")) {
			String simple = DocClassUtil.getSimpleName(gicName);
			String json = buildJson(simple, gicName, isResp, nextLevel, registryClasses, groupClasses,
					methodJsonViewClasses, builder);
			data.append("{").append("\"mapKey\":").append(json).append("}");
			return;
		}

		data.append("{")
			.append("\"mapKey\":")
			.append(buildJson(gicName, genericCanonicalName, isResp, counter + 1, registryClasses, groupClasses,
					methodJsonViewClasses, builder))
			.append("}");

	}

}
