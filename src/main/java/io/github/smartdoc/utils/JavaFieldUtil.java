/*
 * smart-doc
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
package io.github.smartdoc.utils;

import io.github.smartdoc.constants.DocGlobalConstants;
import io.github.smartdoc.constants.DocValidatorAnnotationEnum;
import io.github.smartdoc.constants.JSRAnnotationConstants;
import io.github.smartdoc.constants.JSRAnnotationPropConstants;
import io.github.smartdoc.model.CustomField;
import io.github.smartdoc.model.DocJavaField;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.expression.AnnotationValue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JavaFieldUtil
 *
 * @author yu 2019/12/21.
 */
public class JavaFieldUtil {

	/**
	 * private constructor
	 */
	private JavaFieldUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Pattern to match placeholders in messages
	 */
	private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{(.+?)\\}");

	/**
	 * public static final
	 */
	private static final int PUBLIC_STATIC_FINAL = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;

	/**
	 * check generics
	 * @param fields list of fields
	 * @return boolean
	 */
	public static boolean checkGenerics(List<DocJavaField> fields) {
		for (DocJavaField field : fields) {
			if (field.getJavaField().getType().getFullyQualifiedName().length() == 1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * build custom field
	 * @param data0 data0
	 * @param typeSimpleName typeName
	 * @param customField config field
	 */
	public static void buildCustomField(StringBuilder data0, String typeSimpleName, CustomField customField) {
		Object val = customField.getValue();
		if (Objects.nonNull(val)) {
			if (DocUtil.javaPrimaryType(typeSimpleName)) {
				data0.append(val).append(",");
			}
			else {
				data0.append(DocUtil.handleJsonStr(String.valueOf(val))).append(",");
			}
		}
	}

	/**
	 * create mock value
	 * @param paramsComments param comments
	 * @param paramName param name
	 * @param typeName param type
	 * @param simpleTypeName simple type name
	 * @return mock value
	 */
	public static String createMockValue(Map<String, String> paramsComments, String paramName, String typeName,
			String simpleTypeName) {
		String mockValue = "";
		if (JavaClassValidateUtil.isPrimitive(typeName)) {
			mockValue = paramsComments.get(paramName);
			if (Objects.nonNull(mockValue) && mockValue.contains("|")) {
				mockValue = mockValue.substring(mockValue.lastIndexOf("|") + 1);
			}
			else {
				mockValue = "";
			}
			if (StringUtil.isEmpty(mockValue)) {
				mockValue = DocUtil.getValByTypeAndFieldName(simpleTypeName, paramName, Boolean.TRUE);
			}
		}
		return ParamUtil.formatMockValue(mockValue);
	}

	/**
	 * get param max length
	 * @param classLoader classLoader
	 * @param annotations annotation
	 * @return max length
	 */
	public static String getParamMaxLength(ClassLoader classLoader, List<JavaAnnotation> annotations) {
		String maxLength = "";
		for (JavaAnnotation annotation : annotations) {
			String simpleAnnotationName = annotation.getType().getValue();
			AnnotationValue annotationValue = null;
			if (JSRAnnotationConstants.SIZE.equalsIgnoreCase(simpleAnnotationName)) {
				annotationValue = annotation.getProperty(JSRAnnotationPropConstants.MAX_PROP);
			}
			if (JSRAnnotationConstants.LENGTH.equalsIgnoreCase(simpleAnnotationName)) {
				annotationValue = annotation.getProperty(JSRAnnotationPropConstants.MAX_PROP);
			}
			if (Objects.nonNull(annotationValue)) {
				maxLength = DocUtil.resolveAnnotationValue(classLoader, annotationValue);
			}
		}
		return maxLength;
	}

	/**
	 * get param max length
	 * @param annotations annotation
	 * @return max length
	 */
	public static String getParamMaxLength(List<JavaAnnotation> annotations) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		return getParamMaxLength(classLoader, annotations);
	}

	/**
	 * Get JSR 303 validation comments.
	 * @param showValidation Whether to show JSR validation information
	 * @param classLoader The ClassLoader used to resolve annotation values
	 * @param annotations List of Java annotations to process
	 * @return A string containing JSR validation comments
	 */
	public static String getJsrComment(boolean showValidation, ClassLoader classLoader,
			List<JavaAnnotation> annotations) {
		if (!showValidation) {
			return DocGlobalConstants.EMPTY;
		}
		StringJoiner validationJoiner = new StringJoiner("; ");
		for (JavaAnnotation annotation : annotations) {
			String annotationName = annotation.getType().getValue();
			// Skip excluded annotations
			if (DocValidatorAnnotationEnum.EXCLUDED_ANNOTATIONS.contains(annotationName)) {
				continue;
			}

			// Skip non-validator annotations
			if (!DocValidatorAnnotationEnum.VALIDATOR_ANNOTATIONS.contains(annotationName)) {
				continue;
			}

			StringJoiner paramJoiner = getParamJoiner(annotationName, classLoader, annotation);

			validationJoiner.add(annotationName + "(" + paramJoiner + ")");

		}
		return validationJoiner.length() == 0 ? DocGlobalConstants.EMPTY : "\nValidation[" + validationJoiner + "]";
	}

	/**
	 * Get the string joiner for the given annotation.
	 * @param annotationName The annotation name
	 * @param classLoader The ClassLoader used to resolve annotation values
	 * @param annotation The Java annotation to process
	 * @return A string joiner containing the resolved annotation properties
	 */
	private static StringJoiner getParamJoiner(String annotationName, ClassLoader classLoader,
			JavaAnnotation annotation) {

		// 1. Initialize with default values
		Map<String, String> effectiveValues = new LinkedHashMap<>(
				DocValidatorAnnotationEnum.getDefaults(annotationName));

		// 2. Override with explicit values
		annotation.getPropertyMap()
			.forEach((key, value) -> effectiveValues.put(key,
					StringUtil.removeDoubleQuotes(DocUtil.resolveAnnotationValue(classLoader, value))));

		// 3. Process message placeholders
		effectiveValues.computeIfPresent("message", (k, v) -> replacePlaceholders(v, effectiveValues));

		// 4. Build parameter string
		StringJoiner paramJoiner = new StringJoiner(", ");
		effectiveValues.forEach((key, val) -> paramJoiner.add(key + "=" + val));

		return paramJoiner;
	}

	/**
	 * Replace placeholders in the message with corresponding annotation property values.
	 * @param message The original message content
	 * @param resolvedValues A map of resolved annotation property values
	 * @return The message with placeholders replaced
	 */
	private static String replacePlaceholders(String message, Map<String, String> resolvedValues) {
		// Early exit if the message is null, empty, or does not contain any placeholders
		if (message == null || !message.contains("{") || !message.contains("}")) {
			return message;
		}

		Matcher matcher = PLACEHOLDER_PATTERN.matcher(message);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String key = matcher.group(1);
			String replacement = resolvedValues.getOrDefault(key, matcher.group());
			matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
		}
		matcher.appendTail(sb);
		return sb.toString();

	}

	/**
	 * convert to simple type name
	 * @param str str
	 * @return simple type name
	 */
	public static String convertToSimpleTypeName(String str) {
		String regex = "\\b\\w+\\.(?=\\w+\\b)";
		return str.replaceAll(regex, "");
	}

	/**
	 * Obtain value of constants field
	 * @param classLoader classLoader
	 * @param javaClass class
	 * @param fieldName field name
	 * @return Obtain value of constants field
	 */
	public static String getConstantsFieldValue(ClassLoader classLoader, JavaClass javaClass, String fieldName) {
		try {
			Class<?> c;
			if (Objects.nonNull(classLoader)) {
				c = classLoader.loadClass(javaClass.getBinaryName());
			}
			else {
				c = Class.forName(javaClass.getBinaryName());
			}
			Field[] fields = c.getDeclaredFields();
			for (Field f : fields) {
				// if not have public static final
				if ((f.getModifiers() & PUBLIC_STATIC_FINAL) != PUBLIC_STATIC_FINAL) {
					continue;
				}
				// if not match field name
				if (!f.getName().equals(fieldName)) {
					continue;
				}
				// get value
				Object constantValue = f.get(null);
				return null == constantValue ? null : String.valueOf(constantValue);
			}
		}
		catch (ClassNotFoundException | IllegalAccessException e) {
			return null;
		}
		return null;
	}

}
