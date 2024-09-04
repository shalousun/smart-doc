/*
 * Copyright (C) 2018-2024 smart-doc
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
package com.ly.doc.helper;

import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.constants.*;
import com.ly.doc.model.*;
import com.ly.doc.utils.*;
import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.model.*;
import com.thoughtworks.qdox.model.expression.AnnotationValue;

import java.util.*;

import static com.ly.doc.constants.DocTags.IGNORE_RESPONSE_BODY_ADVICE;

/**
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
				.removeQuotes(String.valueOf(JavaClassUtil.getEnumValue(method.getReturns(), builder, Boolean.FALSE)));
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
				StringBuilder sb = new StringBuilder();
				sb.append(responseBodyAdvice).append("<").append(returnTypeGenericCanonicalName).append(">");
				returnTypeGenericCanonicalName = sb.toString();
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
	 * @param builder The project config builder.
	 * @return The JSON string representation of the type.
	 */
	public static String buildJson(String typeName, String genericCanonicalName, boolean isResp, int counter,
			Map<String, String> registryClasses, Set<String> groupClasses, Set<String> methodJsonViewClasses,
			ProjectDocConfigBuilder builder) {

		Map<String, String> genericMap = new HashMap<>(10);
		JavaClass javaClass = builder.getJavaProjectBuilder().getClassByName(typeName);
		ApiConfig apiConfig = builder.getApiConfig();
		// Check for recursion limit to avoid infinite loops
		if (counter > apiConfig.getRecursionLimit()) {
			return "{\"$ref\":\"...\"}";
		}

		// Avoid processing the same class multiple times
		if (registryClasses.containsKey(typeName) && counter > registryClasses.size()) {
			return "{\"$ref\":\"...\"}";
		}

		int nextLevel = counter + 1;
		registryClasses.put(typeName, typeName);

		// Check if the class should be ignored based on MVC parameters
		if (JavaClassValidateUtil.isMvcIgnoreParams(typeName, builder.getApiConfig().getIgnoreRequestParams())) {
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
				.removeQuotes(String.valueOf(JavaClassUtil.getEnumValue(javaClass, builder, Boolean.FALSE)));
		}

		StringBuilder data0 = new StringBuilder();
		JavaClass cls = builder.getClassByName(typeName);

		data0.append("{");
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
				data.append("{\"waring\":\"You may use java.util.Object instead of display generics in the List\"}");
			}
			else if (JavaClassValidateUtil.isPrimitive(gName)) {
				data.append(DocUtil.jsonValueByType(gName)).append(",");
				data.append(DocUtil.jsonValueByType(gName));
			}
			else if (gName.contains("<")) {
				String simple = DocClassUtil.getSimpleName(gName);
				String json = buildJson(simple, gName, isResp, nextLevel, registryClasses, groupClasses,
						methodJsonViewClasses, builder);
				data.append(json);
			}
			else if (JavaClassValidateUtil.isCollection(gName)) {
				data.append("\"any object\"");
			}
			else {
				String json = buildJson(gName, gName, isResp, nextLevel, registryClasses, groupClasses,
						methodJsonViewClasses, builder);
				data.append(json);
			}
			data.append("]");
			return data.toString();
		}
		// Handle map types
		else if (JavaClassValidateUtil.isMap(typeName)) {
			String[] getKeyValType = DocClassUtil.getMapKeyValueType(genericCanonicalName);
			if (getKeyValType.length == 0) {
				data.append("{\"mapKey\":{}}");
				return data.toString();
			}
			JavaClass mapKeyClass = builder.getJavaProjectBuilder().getClassByName(getKeyValType[0]);
			boolean mapKeyIsEnum = mapKeyClass.isEnum();
			if ((!JavaTypeConstants.JAVA_STRING_FULLY.equals(getKeyValType[0]) || !mapKeyIsEnum
					|| mapKeyClass.getEnumConstants().isEmpty()) && apiConfig.isStrict()) {
				throw new RuntimeException(
						"Map's key can only use String or Enum for json,but you use " + getKeyValType[0]);
			}
			String gicName = genericCanonicalName.substring(genericCanonicalName.indexOf(",") + 1,
					genericCanonicalName.lastIndexOf(">"));
			if (JavaTypeConstants.JAVA_OBJECT_FULLY.equals(gicName)) {
				data.append("{")
					.append("\"mapKey\":")
					.append("{\"waring\":\"You may use java.util.Object for Map value; smart-doc can't be handle.\"}")
					.append("}");
			}
			else if (JavaClassValidateUtil.isPrimitive(gicName)) {
				data.append("{").append("\"mapKey1\":").append(DocUtil.jsonValueByType(gicName)).append(",");
				data.append("\"mapKey2\":").append(DocUtil.jsonValueByType(gicName)).append("}");
			}
			else if (gicName.contains("<")) {
				String simple = DocClassUtil.getSimpleName(gicName);
				String json = buildJson(simple, gicName, isResp, nextLevel, registryClasses, groupClasses,
						methodJsonViewClasses, builder);
				data.append("{").append("\"mapKey\":").append(json).append("}");
			}
			else {
				if (mapKeyIsEnum) {
					data.append(buildJsonForEnumMapKey(mapKeyClass, gicName, genericCanonicalName, isResp, counter + 1,
							registryClasses, groupClasses, methodJsonViewClasses, builder));
				}
				else {
					data.append("{")
						.append("\"mapKey\":")
						.append(buildJson(gicName, genericCanonicalName, isResp, counter + 1, registryClasses,
								groupClasses, methodJsonViewClasses, builder))
						.append("}");
				}
			}
			return data.toString();
		}
		// Handle generic object types
		else if (JavaTypeConstants.JAVA_OBJECT_FULLY.equals(typeName)) {
			data.append("{\"object\":\" any object\"},");
			// throw new RuntimeException("Please do not return java.lang.Object directly
			// in api interface.");
		}
		// Handle Reactor types
		else if (JavaClassValidateUtil.isReactor(typeName)) {
			data.append(buildJson(globGicName[0], typeName, isResp, nextLevel, registryClasses, groupClasses,
					methodJsonViewClasses, builder));
			return data.toString();
		}
		// Process fields of the class
		else {
			boolean requestFieldToUnderline = builder.getApiConfig().isRequestFieldToUnderline();
			boolean responseFieldToUnderline = builder.getApiConfig().isResponseFieldToUnderline();
			List<DocJavaField> fields = JavaClassUtil.getFields(cls, 0, new LinkedHashMap<>(),
					builder.getApiConfig().getClassLoader());

			// get ignore fields from class
			Map<String, String> ignoreFields = JavaClassUtil.getClassJsonIgnoreFields(cls);

			// Process each field of the class
			out: for (DocJavaField docField : fields) {
				JavaField field = docField.getJavaField();
				if (field.isTransient()) {
					boolean passBuild = (apiConfig.isSerializeRequestTransients() && !isResp)
							|| (apiConfig.isSerializeResponseTransients() && isResp);
					if (passBuild) {
						continue;
					}
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
				// get annotations on the field
				List<JavaAnnotation> annotations = docField.getAnnotations();

				String jsonFormatString = null;
				// has Annotation @JsonSerialize And using ToStringSerializer
				boolean toStringSerializer = false;
				// Handle @JsonView; if the field is not annotated with @JsonView, skip
				// it.
				if (!methodJsonViewClasses.isEmpty() && isResp && annotations.isEmpty()) {
					continue;
				}
				// Handle annotations on the field
				for (JavaAnnotation annotation : annotations) {
					// Handle @JsonView
					if (JavaClassUtil.shouldExcludeFieldFromJsonView(annotation, methodJsonViewClasses, isResp,
							builder)) {
						continue out;
					}

					String annotationName = annotation.getType().getValue();
					// if the field is annotated with @JsonSerialize
					if (DocAnnotationConstants.SHORT_JSON_SERIALIZE.equals(annotationName)
							&& DocAnnotationConstants.TO_STRING_SERIALIZER_USING
								.equals(annotation.getNamedParameter("using"))) {
						toStringSerializer = true;
						continue;
					}
					// if the field is annotated with @Null And isResp is false
					if (JSRAnnotationConstants.NULL.equals(annotationName) && !isResp) {
						if (CollectionUtil.isEmpty(groupClasses)) {
							continue out;
						}
						Set<String> groupClassList = JavaClassUtil.getParamGroupJavaClass(annotation);
						for (String groupClass : groupClassList) {
							if (groupClasses.contains(groupClass)) {
								continue out;
							}
						}
					}

					// if the field is annotated with @JsonIgnore || @JsonProperty, then
					// check if it belongs to the groupClasses
					if (JavaClassValidateUtil.isIgnoreFieldJson(annotation, isResp)) {
						continue out;
					}

					// Handle @JSONField
					if (DocAnnotationConstants.SHORT_JSON_FIELD.equals(annotationName)) {
						if (null != annotation.getProperty(DocAnnotationConstants.NAME_PROP)) {
							fieldName = StringUtil
								.removeQuotes(annotation.getProperty(DocAnnotationConstants.NAME_PROP).toString());
						}
					}

					// Handle @JsonProperty
					else if (DocAnnotationConstants.SHORT_JSON_PROPERTY.equals(annotationName)
							|| DocAnnotationConstants.GSON_ALIAS_NAME.equals(annotationName)) {
						AnnotationValue annotationValue = annotation.getProperty(DocAnnotationConstants.VALUE_PROP);
						if (null != annotationValue) {
							fieldName = StringUtil.removeQuotes(annotationValue.toString());
						}
					}
					// Handle @JsonFormat
					else if (DocAnnotationConstants.JSON_FORMAT.equals(annotationName)) {
						jsonFormatString = DocUtil.getJsonFormatString(field, annotation);
					}
				}

				String typeSimpleName = docField.getTypeSimpleName();
				String fieldGicName = docField.getTypeGenericCanonicalName();
				CustomField.Key key = CustomField.Key.create(docField.getDeclaringClassName(), fieldName);

				CustomField customResponseField = CustomField.nameEquals(key, builder.getCustomRespFieldMap());
				CustomField customRequestField = CustomField.nameEquals(key, builder.getCustomReqFieldMap());
				if (Objects.nonNull(customRequestField)) {
					if (JavaClassUtil.isTargetChildClass(docField.getDeclaringClassName(),
							customRequestField.getOwnerClassName()) && (customRequestField.isIgnore()) && !isResp) {
						continue;
					}
					else {
						fieldName = StringUtil.isEmpty(customRequestField.getReplaceName()) ? fieldName
								: customRequestField.getReplaceName();
					}
				}
				if (Objects.nonNull(customResponseField)) {
					if (JavaClassUtil.isTargetChildClass(docField.getDeclaringClassName(),
							customResponseField.getOwnerClassName()) && (customResponseField.isIgnore()) && isResp) {
						continue;
					}
					else {
						fieldName = StringUtil.isEmpty(customResponseField.getReplaceName()) ? fieldName
								: customResponseField.getReplaceName();
					}
				}
				fieldName = fieldName.trim();
				data0.append("\"").append(fieldName).append("\":");
				// get mock value from tag @mock
				String fieldValue = getFieldValueFromMockForJson(subTypeName, tagsMap, typeSimpleName);
				// if the field is primitive type, then get the default value
				if (JavaClassValidateUtil.isPrimitive(subTypeName)) {
					int data0Length = data0.length();
					if (StringUtil.isEmpty(fieldValue)) {
						String valueByTypeAndFieldName = DocUtil.getValByTypeAndFieldName(typeSimpleName,
								field.getName());
						if (toStringSerializer && isResp) {
							fieldValue = valueByTypeAndFieldName.startsWith("\"")
									&& valueByTypeAndFieldName.endsWith("\"") ? valueByTypeAndFieldName
											: DocUtil.handleJsonStr(valueByTypeAndFieldName);
						}
						else {
							fieldValue = StringUtil.isNotEmpty(jsonFormatString) ? jsonFormatString
									: valueByTypeAndFieldName;
						}
					}
					if (Objects.nonNull(customRequestField) && !isResp
							&& typeName.equals(customRequestField.getOwnerClassName())) {
						JavaFieldUtil.buildCustomField(data0, typeSimpleName, customRequestField);
					}
					if (Objects.nonNull(customResponseField) && isResp
							&& typeName.equals(customResponseField.getOwnerClassName())) {
						JavaFieldUtil.buildCustomField(data0, typeSimpleName, customResponseField);
					}
					if (data0.length() == data0Length) {
						data0.append(fieldValue).append(",");
					}
				}
				else {
					// collection or array
					if (JavaClassValidateUtil.isCollection(subTypeName) || JavaClassValidateUtil.isArray(subTypeName)) {
						if (StringUtil.isNotEmpty(fieldValue)) {
							data0.append(fieldValue).append(",");
							continue;
						}
						if (globGicName.length > 0 && "java.util.List".equals(fieldGicName)) {
							fieldGicName = fieldGicName + "<T>";
						}
						if (JavaClassValidateUtil.isArray(subTypeName)) {
							fieldGicName = fieldGicName.substring(0, fieldGicName.lastIndexOf("["));
							fieldGicName = "java.util.List<" + fieldGicName + ">";
						}
						String[] gicNameArray = DocClassUtil.getSimpleGicName(fieldGicName);
						String gicName = gicNameArray[0];
						if (JavaTypeConstants.JAVA_STRING_FULLY.equals(gicName)) {
							data0.append("[").append(DocUtil.jsonValueByType(gicName)).append("]").append(",");
						}
						else if (JavaTypeConstants.JAVA_LIST_FULLY.equals(gicName)) {
							data0.append("[{\"object\":\"any object\"}],");
						}
						else if (gicName.length() == 1) {
							if (globGicName.length == 0) {
								data0.append("[{\"object\":\"any object\"}],");
								continue;
							}
							String gicName1 = genericMap.get(gicName) == null ? globGicName[0]
									: genericMap.get(gicName);
							if (JavaTypeConstants.JAVA_STRING_FULLY.equals(gicName1)) {
								data0.append("[").append(DocUtil.jsonValueByType(gicName1)).append("]").append(",");
							}
							else {
								if (!typeName.equals(gicName1)) {
									data0.append("[")
										.append(buildJson(DocClassUtil.getSimpleName(gicName1), gicName1, isResp,
												nextLevel, registryClasses, groupClasses, methodJsonViewClasses,
												builder))
										.append("]")
										.append(",");
								}
								else {
									data0.append("[{\"$ref\":\"..\"}]").append(",");
								}
							}
						}
						else {
							if (!typeName.equals(gicName)) {
								if (JavaClassValidateUtil.isMap(gicName)) {
									data0.append("[{\"mapKey\":{}}],");
									continue;
								}
								JavaClass arraySubClass = builder.getJavaProjectBuilder().getClassByName(gicName);
								if (arraySubClass.isEnum()) {
									Object value = JavaClassUtil.getEnumValue(arraySubClass, builder, Boolean.FALSE);
									data0.append("[").append(value).append("],");
									continue;
								}
								gicName = DocClassUtil.getSimpleName(gicName);
								fieldGicName = DocUtil.formatFieldTypeGicName(genericMap, fieldGicName);
								data0.append("[")
									.append(buildJson(gicName, fieldGicName, isResp, nextLevel, registryClasses,
											groupClasses, methodJsonViewClasses, builder))
									.append("]")
									.append(",");
							}
							else {
								data0.append("[{\"$ref\":\"..\"}]").append(",");
							}
						}
					}
					// map
					else if (JavaClassValidateUtil.isMap(subTypeName)) {
						if (StringUtil.isNotEmpty(fieldValue)) {
							data0.append(fieldValue).append(",");
							continue;
						}
						if (JavaClassValidateUtil.isMap(fieldGicName)) {
							data0.append("{").append("\"mapKey\":{}},");
							continue;
						}
						String gicName = fieldGicName.substring(fieldGicName.indexOf(",") + 1,
								fieldGicName.indexOf(">"));

						if (gicName.length() == 1) {
							String gicName1 = "";
							if (Objects.nonNull(genericMap.get(gicName))) {
								gicName1 = genericMap.get(gicName);
							}
							else {
								if (Objects.nonNull(globGicName) && globGicName.length > 0) {
									gicName1 = globGicName[0];
								}
							}
							if (JavaTypeConstants.JAVA_STRING_FULLY.equals(gicName1)) {
								data0.append("{")
									.append("\"mapKey\":")
									.append(DocUtil.jsonValueByType(gicName1))
									.append("},");
							}
							else {
								if (!typeName.equals(gicName1)) {
									data0.append("{")
										.append("\"mapKey\":")
										.append(buildJson(DocClassUtil.getSimpleName(gicName1), gicName1, isResp,
												nextLevel, registryClasses, groupClasses, methodJsonViewClasses,
												builder))
										.append("},");
								}
								else {
									data0.append("{\"mapKey\":{}},");
								}
							}
						}
						else {
							String[] mapGicName = DocClassUtil.getSimpleGicName(fieldGicName);
							// mock map key param
							String mapKeySimpleName = mapGicName[0];
							// get map key class
							JavaClass mapKeyClass = builder.getJavaProjectBuilder().getClassByName(mapKeySimpleName);
							if (mapKeyClass.isEnum()) {
								data0.append(buildJsonForEnumMapKey(mapKeyClass, gicName, genericCanonicalName, isResp,
										counter, registryClasses, groupClasses, methodJsonViewClasses, builder));
							}
							else {
								data0.append("{")
									.append("\"mapKey\":")
									.append(buildJson(gicName, fieldGicName, isResp, nextLevel, registryClasses,
											groupClasses, methodJsonViewClasses, builder))
									.append("},");
							}
						}
					}
					else if (fieldGicName.length() == 1) {
						if (!typeName.equals(genericCanonicalName)) {
							String gicName = genericMap.get(subTypeName) == null ? globGicName[0]
									: genericMap.get(subTypeName);
							if (JavaClassValidateUtil.isPrimitive(gicName)) {
								data0.append(DocUtil.jsonValueByType(gicName)).append(",");
							}
							else {
								String simple = DocClassUtil.getSimpleName(gicName);
								data0
									.append(buildJson(simple, gicName, isResp, nextLevel, registryClasses, groupClasses,
											methodJsonViewClasses, builder))
									.append(",");
							}
						}
						else {
							data0.append("{},");
						}
					}
					// Object
					else if (JavaTypeConstants.JAVA_OBJECT_FULLY.equals(fieldGicName)) {
						if (StringUtil.isNotEmpty(field.getComment())) {
							// from source code
							data0.append("{\"object\":\"any object\"},");
						}
						else {
							data0.append("{},");
						}
					}
					else if (typeName.equals(fieldGicName)) {
						data0.append("{\"$ref\":\"...\"}").append(",");
					}
					else {
						javaClass = field.getType();
						// if enum
						if (javaClass.isEnum()) {
							// Override old value
							if (tagsMap.containsKey(DocTags.MOCK) && StringUtil.isNotEmpty(tagsMap.get(DocTags.MOCK))) {
								data0.append(tagsMap.get(DocTags.MOCK)).append(",");
							}
							// if has Annotation @JsonSerialize And using
							// ToStringSerializer && isResp
							else if (toStringSerializer && isResp) {
								Object value = JavaClassUtil.getEnumValue(javaClass, builder, Boolean.FALSE);
								data0.append(value).append(",");
							}
							// if has @JsonFormat
							else if (StringUtil.isNotEmpty(jsonFormatString)) {
								data0.append(jsonFormatString).append(",");
							}
							else {
								Object value = JavaClassUtil.getEnumValue(javaClass, builder, Boolean.FALSE);
								data0.append(value).append(",");
							}
						}
						else {
							// if has Annotation @JsonSerialize And using
							// ToStringSerializer && isResp
							if (toStringSerializer && isResp) {
								data0.append(" ").append(",");
							}
							else if (StringUtil.isNotEmpty(jsonFormatString)) {
								data0.append(jsonFormatString).append(",");
							}
							else {
								fieldGicName = DocUtil.formatFieldTypeGicName(genericMap, fieldGicName);
								data0
									.append(buildJson(subTypeName, fieldGicName, isResp, nextLevel, registryClasses,
											groupClasses, methodJsonViewClasses, builder))
									.append(",");
							}
						}
					}
				}
			}
		}
		if (data0.toString().contains(",")) {
			data0.deleteCharAt(data0.lastIndexOf(","));
		}
		data0.append("}");
		return data0.toString();
	}

	private static String buildJsonForEnumMapKey(JavaClass mapKeyClass, String gicName, String genericCanonicalName,
			boolean isResp, int counter, Map<String, String> registryClasses, Set<String> groupClasses,
			Set<String> methodJsonViewClasses, ProjectDocConfigBuilder builder) {

		StringBuilder data = new StringBuilder("{");
		List<JavaField> mapKeyClassFields = mapKeyClass.getFields();
		int size = mapKeyClassFields.size();
		for (int i = 0; i < size; i++) {
			JavaField mapKeyField = mapKeyClassFields.get(i);
			data.append("\"")
				.append(mapKeyField.getName())
				.append("\":")
				.append(buildJson(gicName, genericCanonicalName, isResp, counter + 1, registryClasses, groupClasses,
						methodJsonViewClasses, builder));

			if (i < size - 1) {
				data.append(",");
			}
		}
		data.append("},");
		return data.toString();
	}

}
