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
import io.github.smartdoc.constants.DocAnnotationConstants;
import io.github.smartdoc.constants.DocTags;
import io.github.smartdoc.constants.JSRAnnotationConstants;
import io.github.smartdoc.model.CustomField;
import io.github.smartdoc.model.CustomFieldInfo;
import io.github.smartdoc.model.DocJavaField;
import io.github.smartdoc.model.FieldJsonAnnotationInfo;
import io.github.smartdoc.utils.DocUtil;
import io.github.smartdoc.utils.JavaClassUtil;
import io.github.smartdoc.utils.JavaClassValidateUtil;
import com.power.common.util.CollectionUtil;
import com.power.common.util.StringEscapeUtil;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.expression.AnnotationValue;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Abstract Base helper
 *
 * @author yu3.sun on 2022/10/14
 */
public abstract class BaseHelper {

	/**
	 * get field value from mock tag
	 * @param subTypeName subType name
	 * @param tagsMap tags map
	 * @param typeSimpleName type simple name
	 * @return field value
	 */
	protected static String getFieldValueFromMockForJson(String subTypeName, Map<String, String> tagsMap,
			String typeSimpleName) {
		String fieldValue = "";
		if (tagsMap.containsKey(DocTags.MOCK) && StringUtil.isNotEmpty(tagsMap.get(DocTags.MOCK))) {
			fieldValue = tagsMap.get(DocTags.MOCK);
			fieldValue = StringEscapeUtil.unescapeJava(fieldValue);
			if (!DocUtil.javaPrimaryType(typeSimpleName) && !JavaClassValidateUtil.isCollection(subTypeName)
					&& !JavaClassValidateUtil.isMap(subTypeName) && !JavaClassValidateUtil.isArray(subTypeName)) {
				fieldValue = StringEscapeUtil.escapeJava(fieldValue, true);
				fieldValue = DocUtil.handleJsonStr(fieldValue);
			}
		}
		return fieldValue;
	}

	/**
	 * get field value from mock tag
	 * @param tagsMap tags map
	 * @return field value
	 */
	protected static String getFieldValueFromMock(Map<String, String> tagsMap) {
		String fieldValue = "";
		if (tagsMap.containsKey(DocTags.MOCK) && StringUtil.isNotEmpty(tagsMap.get(DocTags.MOCK))) {
			fieldValue = StringEscapeUtil.unescapeJava(tagsMap.get(DocTags.MOCK));
		}
		return fieldValue;
	}

	/**
	 * check custom field is ignored
	 * @param docField doc field
	 * @param isResp is resp
	 * @param customRequestField custom request field
	 * @param customResponseField custom response field
	 * @return boolean
	 */
	protected static boolean isIgnoreCustomField(DocJavaField docField, boolean isResp, CustomField customRequestField,
			CustomField customResponseField) {
		if (Objects.nonNull(customRequestField) && JavaClassUtil.isTargetChildClass(docField.getDeclaringClassName(),
				customRequestField.getOwnerClassName()) && (customRequestField.isIgnore()) && !isResp) {
			return true;
		}
		return Objects.nonNull(customResponseField) && JavaClassUtil
			.isTargetChildClass(docField.getDeclaringClassName(), customResponseField.getOwnerClassName())
				&& (customResponseField.isIgnore()) && isResp;
	}

	/**
	 * check field is transient
	 * @param field field
	 * @param projectBuilder project builder
	 * @param isResp is resp
	 * @return boolean
	 */
	protected static boolean isTransientField(JavaField field, ProjectDocConfigBuilder projectBuilder, boolean isResp) {
		if (field.isTransient()) {
			return (projectBuilder.getApiConfig().isSerializeRequestTransients() && !isResp)
					|| (projectBuilder.getApiConfig().isSerializeResponseTransients() && isResp);
		}
		return false;
	}

	/**
	 * Get a field JSON annotation information for a given field.
	 * @param projectBuilder the project builder
	 * @param docField the doc of java field
	 * @param isResp the response flag for the parameter
	 * @param groupClasses the group classes
	 * @param methodJsonViewClasses the method JSON view classes
	 * @return the field JSON annotation information {@link FieldJsonAnnotationInfo}
	 *
	 */
	protected static FieldJsonAnnotationInfo getFieldJsonAnnotationInfo(ProjectDocConfigBuilder projectBuilder,
			DocJavaField docField, boolean isResp, Set<String> groupClasses, Set<String> methodJsonViewClasses) {
		FieldJsonAnnotationInfo fieldJsonAnnotationInfo = new FieldJsonAnnotationInfo();
		// Handle @JsonView; if the field is not annotated with @JsonView, skip
		if (!methodJsonViewClasses.isEmpty() && isResp && docField.getAnnotations().isEmpty()) {
			return fieldJsonAnnotationInfo;
		}

		for (JavaAnnotation annotation : docField.getAnnotations()) {
			// if the field is annotated with @JsonIgnore || @JsonProperty, then
			// check if it belongs to the groupClasses
			if (JavaClassValidateUtil.isIgnoreFieldJson(annotation, isResp)) {
				fieldJsonAnnotationInfo.setIgnore(true);
				continue;
			}
			// Handle @JsonView
			if (JavaClassUtil.shouldExcludeFieldFromJsonView(annotation, methodJsonViewClasses, isResp,
					projectBuilder)) {
				fieldJsonAnnotationInfo.setIgnore(true);
				return fieldJsonAnnotationInfo;
			}

			String annotationName = annotation.getType().getValue();
			// if the field is annotated with @JsonSerialize
			if (DocAnnotationConstants.SHORT_JSON_SERIALIZE.equals(annotationName)
					&& DocAnnotationConstants.TO_STRING_SERIALIZER_USING
						.equals(annotation.getNamedParameter(DocAnnotationConstants.USING_PROP))) {
				fieldJsonAnnotationInfo.setToStringSerializer(true);
				continue;
			}
			// if the field is annotated with @Null And isResp is false
			if (JSRAnnotationConstants.NULL.equals(annotationName) && !isResp) {
				if (CollectionUtil.isEmpty(groupClasses)) {
					fieldJsonAnnotationInfo.setIgnore(true);
					return fieldJsonAnnotationInfo;
				}
				Set<String> groupClassList = JavaClassUtil.getParamGroupJavaClass(annotation);
				for (String javaClass : groupClassList) {
					if (groupClasses.contains(javaClass)) {
						fieldJsonAnnotationInfo.setIgnore(true);
						return fieldJsonAnnotationInfo;
					}
				}
			}

			// Handle @JSONField
			if (DocAnnotationConstants.SHORT_JSON_FIELD.equals(annotationName)) {
				if (null != annotation.getProperty(DocAnnotationConstants.NAME_PROP)) {
					AnnotationValue annotationValue = annotation.getProperty(DocAnnotationConstants.NAME_PROP);
					String fieldName = DocUtil.resolveAnnotationValue(projectBuilder.getApiConfig().getClassLoader(),
							annotationValue);
					fieldJsonAnnotationInfo.setFieldName(fieldName);
				}
			}

			// Handle @JsonProperty
			else if (DocAnnotationConstants.SHORT_JSON_PROPERTY.equals(annotationName)
					|| DocAnnotationConstants.GSON_ALIAS_NAME.equals(annotationName)) {
				AnnotationValue annotationValue = annotation.getProperty(DocAnnotationConstants.VALUE_PROP);
				String fieldName = DocUtil.resolveAnnotationValue(projectBuilder.getApiConfig().getClassLoader(),
						annotationValue);
				fieldJsonAnnotationInfo.setFieldName(fieldName);
			}
			// Handle JSR303 required
			if (JavaClassValidateUtil.isJSR303Required(annotationName) && !isResp) {
				Set<String> groupClassList = JavaClassUtil.getParamGroupJavaClass(annotation);
				// Check if groupClasses contains any element from
				// groupClassList
				boolean hasGroup = groupClassList.stream().anyMatch(groupClasses::contains);

				if (hasGroup) {
					fieldJsonAnnotationInfo.setStrRequired(true);
				}
				else if (CollectionUtil.isEmpty(groupClasses)) {
					// If the annotation is @Valid or @Validated, the Default
					// group is added by default and groupClasses will not be
					// empty;
					// In other cases, if groupClasses is still empty, then
					// strRequired is false.
					fieldJsonAnnotationInfo.setStrRequired(false);
				}
			}
			// Handle @JsonFormat
			if (DocAnnotationConstants.JSON_FORMAT.equals(annotationName)) {
				fieldJsonAnnotationInfo.setFieldJsonFormatType(
						DocUtil.processFieldTypeNameByJsonFormat(projectBuilder.getApiConfig().getShowJavaType(),
								docField.getTypeFullyQualifiedName(), annotation));
				fieldJsonAnnotationInfo
					.setFieldJsonFormatValue(DocUtil.getJsonFormatString(docField.getJavaField(), annotation));
			}

		}
		return fieldJsonAnnotationInfo;
	}

	/**
	 * Get the custom field information for a given field.
	 * @param projectBuilder the project builder
	 * @param docField the doc of java field
	 * @param isResp the response flag for the parameter
	 * @param simpleName the simple name of the field
	 * @param fieldName the name of the field
	 * @return the custom field information {@link CustomFieldInfo}
	 */
	protected static CustomFieldInfo getCustomFieldInfo(ProjectDocConfigBuilder projectBuilder, DocJavaField docField,
			boolean isResp, String simpleName, String fieldName) {
		CustomField.Key key = CustomField.Key.create(docField.getDeclaringClassName(), fieldName);

		CustomField customResponseField = CustomField.nameEquals(key, projectBuilder.getCustomRespFieldMap());
		CustomField customRequestField = CustomField.nameEquals(key, projectBuilder.getCustomReqFieldMap());

		CustomFieldInfo customFieldInfo = new CustomFieldInfo();

		// ignore custom field, if true return quickly
		if (isIgnoreCustomField(docField, isResp, customRequestField, customResponseField)) {
			customFieldInfo.setIgnore(true);
			return customFieldInfo;
		}

		customFieldInfo.setCustomResponseField(customResponseField).setCustomRequestField(customRequestField);

		// cover response value
		if (Objects.nonNull(customResponseField) && isResp && Objects.nonNull(customResponseField.getValue())
				&& JavaClassUtil.isTargetChildClass(simpleName, customResponseField.getOwnerClassName())) {

			customFieldInfo.setFieldValue(String.valueOf(customResponseField.getValue()));
		}

		// cover request value
		if (Objects.nonNull(customRequestField) && !isResp && Objects.nonNull(customRequestField.getValue())
				&& JavaClassUtil.isTargetChildClass(simpleName, customRequestField.getOwnerClassName())) {

			customFieldInfo.setFieldValue(String.valueOf(customRequestField.getValue()));
		}

		// cover required
		if (Objects.nonNull(customRequestField) && !isResp
				&& JavaClassUtil.isTargetChildClass(simpleName, customRequestField.getOwnerClassName())
				&& customRequestField.isRequire()) {

			customFieldInfo.setStrRequired(true);
		}

		// cover comment
		if (Objects.nonNull(customRequestField) && StringUtil.isNotEmpty(customRequestField.getDesc())
				&& JavaClassUtil.isTargetChildClass(simpleName, customRequestField.getOwnerClassName()) && !isResp) {
			customFieldInfo.setComment(customRequestField.getDesc());
		}
		if (Objects.nonNull(customResponseField) && StringUtil.isNotEmpty(customResponseField.getDesc())
				&& JavaClassUtil.isTargetChildClass(simpleName, customResponseField.getOwnerClassName()) && isResp) {
			customFieldInfo.setComment(customResponseField.getDesc());
		}

		// cover fieldName
		if (Objects.nonNull(customRequestField) && StringUtil.isNotEmpty(customRequestField.getReplaceName())
				&& JavaClassUtil.isTargetChildClass(simpleName, customRequestField.getOwnerClassName()) && !isResp) {
			customFieldInfo.setFieldName(customRequestField.getReplaceName());
		}
		if (Objects.nonNull(customResponseField) && StringUtil.isNotEmpty(customResponseField.getReplaceName())
				&& JavaClassUtil.isTargetChildClass(simpleName, customResponseField.getOwnerClassName()) && isResp) {

			customFieldInfo.setFieldName(customResponseField.getReplaceName());
		}
		return customFieldInfo;
	}

}
