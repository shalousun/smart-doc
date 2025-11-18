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

import io.github.smartdoc.builder.ProjectDocConfigBuilder;
import io.github.smartdoc.constants.DefaultClassConstants;
import io.github.smartdoc.constants.DocAnnotationConstants;
import io.github.smartdoc.constants.DocGlobalConstants;
import io.github.smartdoc.constants.DocTags;
import io.github.smartdoc.constants.DocValidatorAnnotationEnum;
import io.github.smartdoc.constants.JSRAnnotationConstants;
import io.github.smartdoc.constants.JavaTypeConstants;
import io.github.smartdoc.constants.ParamTypeConstants;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.ApiDataDictionary;
import io.github.smartdoc.model.DocJavaField;
import io.github.smartdoc.model.torna.EnumInfo;
import io.github.smartdoc.model.torna.EnumInfoAndValues;
import io.github.smartdoc.model.torna.Item;
import com.power.common.model.EnumDictionary;
import com.power.common.util.CollectionUtil;
import com.power.common.util.EnumUtil;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaGenericDeclaration;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameterizedType;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.JavaTypeVariable;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import com.thoughtworks.qdox.model.expression.AnnotationValueList;
import com.thoughtworks.qdox.model.expression.Constant;
import com.thoughtworks.qdox.model.expression.TypeRef;
import com.thoughtworks.qdox.model.impl.DefaultJavaField;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameterizedType;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Handle JavaClass
 *
 * @author yu 2019/12/21.
 */
public class JavaClassUtil {

	/**
	 * logger
	 */
	private static final Logger logger = Logger.getLogger(JavaClassUtil.class.getName());

	/**
	 * dot
	 */
	private static final String DOT = ".";

	/**
	 * private constructor
	 */
	private JavaClassUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Get fields
	 * @param cls1 The JavaClass object
	 * @param counter Recursive counter
	 * @param addedFields added fields,Field deduplication
	 * @param classLoader classLoader
	 * @return list of JavaField
	 */
	public static List<DocJavaField> getFields(JavaClass cls1, int counter, Map<String, DocJavaField> addedFields,
			ClassLoader classLoader) {
		Map<String, JavaType> actualJavaTypes = new HashMap<>(10);
		List<DocJavaField> fields = getFields(cls1, counter, addedFields, actualJavaTypes, classLoader);

		for (DocJavaField field : fields) {
			String genericCanonicalName = field.getTypeGenericCanonicalName();
			if (Objects.isNull(genericCanonicalName)) {
				continue;
			}
			JavaType actualJavaType = actualJavaTypes.get(genericCanonicalName);
			if (Objects.isNull(actualJavaType)) {
				continue;
			}
			field.setTypeGenericCanonicalName(
					genericCanonicalName.replace(genericCanonicalName, actualJavaType.getGenericCanonicalName()));
			field.setTypeFullyQualifiedName(field.getTypeFullyQualifiedName()
				.replace(genericCanonicalName, actualJavaType.getFullyQualifiedName()));
			field.setActualJavaType(actualJavaType.getFullyQualifiedName());
		}
		return fields;
	}

	/**
	 * Get fields
	 * @param cls1 The JavaClass object
	 * @param counter Recursive counter
	 * @param addedFields added fields,Field deduplication
	 * @param actualJavaTypes collected actualJavaTypes
	 * @return list of JavaField
	 */
	private static List<DocJavaField> getFields(JavaClass cls1, int counter, Map<String, DocJavaField> addedFields,
			Map<String, JavaType> actualJavaTypes, ClassLoader classLoader) {
		List<DocJavaField> fieldList = new ArrayList<>();
		if (Objects.isNull(cls1)) {
			return fieldList;
		}
		// ignore enum class
		if (cls1.isEnum()) {
			return fieldList;
		}
		// ignore class in jdk
		String className = cls1.getFullyQualifiedName();
		if (JavaClassValidateUtil.isJdkClass(className)) {
			return fieldList;
		}
		if (cls1.isInterface()) {
			List<JavaMethod> methods = cls1.getMethods();
			for (JavaMethod javaMethod : methods) {
				String methodName = javaMethod.getName();
				int paramSize = javaMethod.getParameters().size();
				boolean enable = false;
				if (methodName.startsWith("get") && !"get".equals(methodName) && paramSize == 0) {
					methodName = StringUtil.firstToLowerCase(methodName.substring(3));
					enable = true;
				}
				else if (methodName.startsWith("is") && !"is".equals(methodName) && paramSize == 0) {
					methodName = StringUtil.firstToLowerCase(methodName.substring(2));
					enable = true;
				}
				if (!enable || addedFields.containsKey(methodName)) {
					continue;
				}
				String comment = javaMethod.getComment();
				if (StringUtil.isEmpty(comment)) {
					comment = DocGlobalConstants.NO_COMMENTS_FOUND;
				}
				JavaField javaField = new DefaultJavaField(javaMethod.getReturns(), methodName);
				DocJavaField docJavaField = DocJavaField.builder()
					.setDeclaringClassName(className)
					.setFieldName(methodName)
					.setJavaField(javaField)
					.setComment(comment)
					.setDocletTags(javaMethod.getTags())
					.setAnnotations(javaMethod.getAnnotations())
					.setTypeFullyQualifiedName(javaField.getType().getFullyQualifiedName())
					.setTypeGenericCanonicalName(getReturnGenericType(javaMethod, classLoader))
					.setTypeGenericFullyQualifiedName(javaField.getType().getGenericFullyQualifiedName())
					.setTypeSimpleName(javaField.getType().getSimpleName());
				addedFields.put(methodName, docJavaField);
			}
		}

		JavaClass parentClass = cls1.getSuperJavaClass();
		if (Objects.nonNull(parentClass)) {
			getFields(parentClass, counter, addedFields, actualJavaTypes, classLoader);
		}

		List<JavaType> implClasses = cls1.getImplements();
		for (JavaType type : implClasses) {
			JavaClass javaClass = (JavaClass) type;
			getFields(javaClass, counter, addedFields, actualJavaTypes, classLoader);
		}

		actualJavaTypes.putAll(getActualTypesMap(cls1));
		List<JavaMethod> javaMethods = cls1.getMethods();
		for (JavaMethod method : javaMethods) {
			String methodName = method.getName();
			if (method.getAnnotations().isEmpty()) {
				continue;
			}
			int paramSize = method.getParameters().size();
			if (methodName.startsWith("get") && !"get".equals(methodName) && paramSize == 0) {
				methodName = StringUtil.firstToLowerCase(methodName.substring(3));
			}
			else if (methodName.startsWith("is") && !"is".equals(methodName) && paramSize == 0) {
				methodName = StringUtil.firstToLowerCase(methodName.substring(2));
			}
			if (addedFields.containsKey(methodName)) {
				String comment = method.getComment();
				if (Objects.isNull(comment)) {
					comment = addedFields.get(methodName).getComment();
				}
				if (StringUtil.isEmpty(comment)) {
					comment = DocGlobalConstants.NO_COMMENTS_FOUND;
				}
				DocJavaField docJavaField = addedFields.get(methodName);
				docJavaField.setAnnotations(method.getAnnotations());
				docJavaField.setComment(comment);
				docJavaField.setFieldName(methodName);
				docJavaField.setDeclaringClassName(className);
				addedFields.put(methodName, docJavaField);
			}
		}
		if (!cls1.isInterface()) {
			Map<String, String> recordComments = new HashMap<>(0);
			if (cls1.isRecord()) {
				recordComments = DocUtil.getRecordCommentsByTag(cls1, DocTags.PARAM);
			}
			for (JavaField javaField : cls1.getFields()) {
				String fieldName = javaField.getName();
				String subTypeName = javaField.getType().getFullyQualifiedName();

				if (javaField.isStatic() || "this$0".equals(fieldName)
						|| JavaClassValidateUtil.isIgnoreFieldTypes(subTypeName)) {
					continue;
				}
				if (fieldName.startsWith("is") && ("boolean".equals(subTypeName))) {
					fieldName = StringUtil.firstToLowerCase(fieldName.substring(2));
				}
				long count = javaField.getAnnotations()
					.stream()
					.filter(annotation -> DocAnnotationConstants.SHORT_JSON_IGNORE
						.equals(annotation.getType().getSimpleName()))
					.count();
				if (count > 0) {
					addedFields.remove(fieldName);
					continue;
				}

				DocJavaField docJavaField = DocJavaField.builder();
				boolean typeChecked = false;
				JavaType fieldType = javaField.getType();
				String gicName = fieldType.getGenericCanonicalName();

				String actualType = null;
				if (JavaClassValidateUtil.isCollection(subTypeName) && !JavaClassValidateUtil.isCollection(gicName)) {
					String[] gNameArr = DocClassUtil.getSimpleGicName(gicName);
					actualType = JavaClassUtil.getClassSimpleName(gNameArr[0]);
					docJavaField.setArray(true);
					typeChecked = true;
				}
				if (JavaClassValidateUtil.isPrimitive(subTypeName) && !typeChecked) {
					docJavaField.setPrimitive(true);
					typeChecked = true;
				}
				if (JavaClassValidateUtil.isFile(subTypeName) && !typeChecked) {
					docJavaField.setFile(true);
					typeChecked = true;
				}
				if (javaField.getType().isEnum() && !typeChecked) {
					docJavaField.setEnum(true);
				}
				String comment = javaField.getComment();
				if (cls1.isRecord()) {
					comment = recordComments.get(fieldName);
				}
				if (Objects.isNull(comment)) {
					comment = DocGlobalConstants.NO_COMMENTS_FOUND;
				}
				// Getting the Original Defined Type of Field
				if (!docJavaField.isFile() || !docJavaField.isEnum() || !docJavaField.isPrimitive()
						|| JavaTypeConstants.JAVA_OBJECT_FULLY.equals(gicName)) {
					String genericFieldTypeName = getFieldGenericType(javaField, classLoader);
					if (StringUtil.isNotEmpty(genericFieldTypeName)) {
						gicName = genericFieldTypeName;
					}
				}
				docJavaField.setComment(comment)
					.setJavaField(javaField)
					.setTypeFullyQualifiedName(subTypeName)
					.setTypeGenericCanonicalName(gicName)
					.setTypeGenericFullyQualifiedName(fieldType.getGenericFullyQualifiedName())
					.setActualJavaType(actualType)
					.setAnnotations(javaField.getAnnotations())
					.setFieldName(fieldName)
					.setDeclaringClassName(className)
					.setTypeSimpleName(javaField.getType().getSimpleName());
				if (addedFields.containsKey(fieldName)) {
					addedFields.remove(fieldName);
					addedFields.put(fieldName, docJavaField);
					continue;
				}
				addedFields.put(fieldName, docJavaField);
			}
		}
		List<DocJavaField> parentFieldList = addedFields.values()
			.stream()
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
		fieldList.addAll(parentFieldList);

		return fieldList;
	}

	/**
	 * Get Common for methods with the same signature from interfaces
	 * @param cls cls
	 * @param method method
	 * @return common
	 */
	public static String getSameSignatureMethodCommonFromInterface(JavaClass cls, JavaMethod method) {

		List<JavaMethod> methodsBySignature = cls.getMethodsBySignature(method.getName(), method.getParameterTypes(),
				true, method.isVarArgs());

		for (JavaMethod sameSignatureMethod : methodsBySignature) {
			if (sameSignatureMethod == method || sameSignatureMethod.getDeclaringClass() == null
					|| !sameSignatureMethod.getDeclaringClass().isInterface()) {
				continue;
			}
			if (sameSignatureMethod.getComment() != null) {
				return sameSignatureMethod.getComment();
			}
		}
		return null;
	}

	/**
	 * Get the value of an enum
	 * <p>
	 * This method retrieves the value of an enum based on its fields or methods. It
	 * supports loading the enum class via reflection and determines the enum value based
	 * on the presence of specific annotations such as {@code JsonValue}
	 * @param javaClass The JavaClass object representing the enum class
	 * @param builder A ProjectDocConfigBuilder object used to retrieve API configuration
	 * and the class loader
	 * @param jsonEnum Enum is json or not
	 * @return Object The enum value, whose type depends on the specific enum definition
	 * @throws RuntimeException If the enum constants do not exist
	 */
	public static Object getEnumValue(JavaClass javaClass, ProjectDocConfigBuilder builder, boolean jsonEnum) {
		EnumInfoAndValues enumInfoAndValue = getEnumInfoAndValue(javaClass, builder, jsonEnum);
		if (enumInfoAndValue == null) {
			return null;
		}

		// Default handling for enum values
		return enumInfoAndValue.getValue();
	}

	/**
	 * Get the value of an enumConstant
	 * <p>
	 * First look for it using the @JsonValue annotation, and if you can't find it, look
	 * for the default value of the enumeration field
	 * @param javaClass The JavaClass object representing the enum class
	 * @param builder A ProjectDocConfigBuilder object used to retrieve API configuration
	 * and the class loader
	 * @param enumConstant The JavaField object representing the enum constant
	 * @return Object The enum value, whose type depends on the specific enum definition
	 * @throws RuntimeException If the enum constants do not exist
	 */
	public static Object getDefaultEnumValue(JavaClass javaClass, ProjectDocConfigBuilder builder,
			JavaField enumConstant) {
		// Try getting value from method with JsonValue annotation
		Object enumValue = getEnumValueWithJsonValue(javaClass, builder, enumConstant);
		if (enumValue != null) {
			return enumValue;
		}

		// Default handling for enum values
		return processDefaultEnumFields(enumConstant, builder);
	}

	/**
	 * Get the value with @JsonValue annotation
	 * <p>
	 * This method retrieves the value of an enum based on its fields or methods. It
	 * supports loading the enum class via reflection and determines the enum value based
	 * on the presence of specific annotations such as {@code JsonValue}
	 * @param javaClass The JavaClass object representing the enum class
	 * @param builder A ProjectDocConfigBuilder object used to retrieve API configuration
	 * and the class loader
	 * @param enumConstant The JavaField object representing the enum constant
	 * @return Object The enum value, whose type depends on the specific enum definition
	 * @throws RuntimeException If the enum constants do not exist
	 */
	public static Object getEnumValueWithJsonValue(JavaClass javaClass, ProjectDocConfigBuilder builder,
			JavaField enumConstant) {
		String methodName = findMethodWithJsonValue(javaClass);
		if (Objects.nonNull(methodName)) {
			Class<?> enumClass = loadEnumClass(javaClass, builder);
			if (Objects.nonNull(enumClass)) {
				return EnumUtil.getFieldValueByMethod(enumClass, methodName, enumConstant.getName());
			}
			return null;
		}

		// Try getting value from field with JsonValue annotation
		Optional<JavaField> fieldWithJsonValue = findFieldWithJsonValue(javaClass);
		if (fieldWithJsonValue.isPresent()) {
			Class<?> enumClass = loadEnumClass(javaClass, builder);
			if (Objects.nonNull(enumClass)) {
				return EnumUtil.getFieldValue(enumClass, fieldWithJsonValue.get().getName(), enumConstant.getName());
			}
			return null;
		}

		return null;
	}

	/**
	 * Loads the enum class using the specified class loader from the builder.
	 * @param javaClass The JavaClass representing the enum
	 * @param builder The configuration builder
	 * @return The loaded Class object for the enum
	 */
	private static Class<?> loadEnumClass(JavaClass javaClass, ProjectDocConfigBuilder builder) {
		ApiConfig apiConfig = builder.getApiConfig();
		ClassLoader classLoader = apiConfig.getClassLoader();
		try {
			if (Objects.nonNull(classLoader)) {
				return classLoader.loadClass(javaClass.getBinaryName());
			}
			else {
				return Class.forName(javaClass.getBinaryName());
			}
		}
		catch (ClassNotFoundException e) {
			logger.warning(e.getMessage());
			return null;
		}
	}

	/**
	 * Finds the method in the class that has {@code JsonValue} annotation.
	 * @param javaClass The JavaClass to search in
	 * @return The method name if found, null otherwise
	 */
	private static String findMethodWithJsonValue(JavaClass javaClass) {
		for (JavaMethod method : javaClass.getMethods()) {
			for (JavaAnnotation annotation : method.getAnnotations()) {
				String annotationName = annotation.getType().getValue();
				// Handle JsonValue annotation
				if (DocAnnotationConstants.JSON_VALUE.equals(annotationName)) {
					AnnotationValue property = annotation.getProperty(DocAnnotationConstants.VALUE_PROP);
					// If property is null or its value is true, return the method name
					if (property == null || Objects.equals(property.getParameterValue(), true)) {
						return method.getName();
					}
				}
			}
		}
		return null;
	}

	/**
	 * Finds the field in the class that has {@code JsonValue} annotation.
	 * @param javaClass The JavaClass to search in
	 * @return An Optional containing the field if found
	 */
	private static Optional<JavaField> findFieldWithJsonValue(JavaClass javaClass) {
		return javaClass.getFields()
			.stream()
			.filter(field -> field.getAnnotations().stream().anyMatch(javaAnnotation -> {
				if (DocAnnotationConstants.JSON_VALUE.equals(javaAnnotation.getType().getValue())) {
					// Check if the property is null, if so, consider it as "true"
					AnnotationValue property = javaAnnotation.getProperty(DocAnnotationConstants.VALUE_PROP);
					return property == null || Objects.equals(property.getParameterValue(), true);
				}
				return false;
			}))
			.findFirst();
	}

	/**
	 * Handles the default logic for processing enum fields.
	 * @param javaField The JavaField object representing the enum constant
	 * @return The value based on the enum field processing logic
	 */
	private static Object processDefaultEnumFields(JavaField javaField, ProjectDocConfigBuilder builder) {
		ApiConfig apiConfig = builder.getApiConfig();
		if (!apiConfig.isEnumConvertor()) {
			return javaField.getName();
		}

		String initializationExpression = javaField.getInitializationExpression();

		if (StringUtils.isBlank(initializationExpression)) {
			return javaField.getName();
		}

		String[] result = initializationExpression.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
		if (result.length == 0) {
			return javaField.getName();
		}

		String str = result[0];
		return StringUtils.isNumeric(str) ? Integer.parseInt(str) : str;
	}

	/**
	 * Gets the enum parameters for the given JavaClass.
	 * @param javaClass The JavaClass representing the enum
	 * @return A String containing the enum parameters
	 */
	public static String getEnumParams(JavaClass javaClass) {
		List<JavaField> javaFields = javaClass.getEnumConstants();
		StringBuilder stringBuilder = new StringBuilder();
		for (JavaField javaField : javaFields) {
			// string comment
			String exception = javaField.getInitializationExpression();
			// add a separator to Enum values for display better.
			if (stringBuilder.length() > 0) {
				stringBuilder.append(", ");
			}
			stringBuilder.append(javaField.getName());
			if (StringUtil.isNotEmpty(exception)) {
				stringBuilder.append("(").append(exception).append(")").append("<br/>");
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * Gets the list of enum values for the given JavaClass.
	 * @param javaClass The JavaClass representing the enum
	 * @return A List of Strings containing the enum values
	 */
	public static List<String> getEnumValues(JavaClass javaClass) {
		List<JavaField> javaFields = javaClass.getEnumConstants();
		List<String> enums = new ArrayList<>();
		for (JavaField javaField : javaFields) {
			enums.add(javaField.getName());
		}
		return enums;
	}

	/**
	 * Retrieves the associated enum class for a given Java field.
	 * <p>
	 * This method aims to obtain the enum type (JavaClass) associated with the provided
	 * Java field object (JavaField). If the field does not associate with an enum type or
	 * if there is no appropriate @see tag providing enum information, it returns null.
	 * The splitting logic is implemented to handle the case where the @see tag might
	 * contain additional description information after the class name (e.g., `@see
	 * GenderEnum descriptionGender`).
	 * @param javaField The Java field object to inspect
	 * @param builder The builder used to retrieve project documentation configuration
	 * @return The enum class object associated with the field, or null if not found
	 */
	public static JavaClass getSeeEnum(JavaField javaField, ProjectDocConfigBuilder builder) {
		if (Objects.isNull(javaField)) {
			return null;
		}

		// If the field type is already an enum, return it directly
		JavaClass javaClass = javaField.getType();
		if (javaClass.isEnum()) {
			return javaClass;
		}

		// Process the @see tag if present
		DocletTag see = javaField.getTagByName(DocTags.SEE);
		if (Objects.isNull(see)) {
			return null;
		}

		// Extract the enum class name from @see tag
		String value = extractEnumClassName(see.getValue());
		if (value == null) {
			return null;
		}

		// Resolve the class name
		// (handles imports, nested classes, and fully qualified names)
		value = resolveClassName(value, javaField.getDeclaringClass());

		// Check if the value corresponds to a valid class name
		if (!JavaClassValidateUtil.isClassName(value)) {
			// Fixed #995: If the value is not a valid class name,
			// attempt to resolve it by adding the current package name prefix.
			value = javaField.getDeclaringClass().getPackageName() + DOT + value;
			// Check again
			if (!JavaClassValidateUtil.isClassName(value)) {
				return null;
			}
		}

		// Retrieve the JavaClass by name and check if it is an enum
		JavaClass enumClass = builder.getClassByName(value);
		if (Objects.isNull(enumClass)) {
			// Fixed #995: If the class cannot be found, attempt to resolve the class name
			// by adding the package prefix of the declaring class. This approach is used
			// when the enum is defined in the same package as the declaring class.
			enumClass = builder.getClassByName(javaField.getDeclaringClass().getPackageName() + DOT + value);
		}
		if (Objects.nonNull(enumClass) && enumClass.isEnum()) {
			return enumClass;
		}
		return null;
	}

	/**
	 * Extracts the enum class name from the @see tag value. Handles cases where the @see
	 * tag contains additional description info after the class name.<br>
	 * e.g. {@code @see TestEnum test}
	 * @param seeValue The value of the @see tag
	 * @return The extracted enum class name or null if not found
	 */
	private static String extractEnumClassName(String seeValue) {
		if (seeValue == null || seeValue.trim().isEmpty()) {
			return null;
		}
		// Split the value to extract the class name (first part before any whitespace)
		return seeValue.trim().split("\\s+")[0];
	}

	/**
	 * Resolves the class name by checking imports and nested classes. Handles both fully
	 * qualified class names and short class names (e.g., class names without package
	 * information).
	 * <p>
	 * This method first checks if the given class name is fully qualified (i.e., contains
	 * a dot). If it's not fully qualified, it will attempt to resolve it using imports
	 * and nested classes. If the class name is fully qualified, it will check if it can
	 * be resolved using imports or nested classes. The method handles cases where the
	 * class name is not directly available or when it's a nested class.
	 * @param value The class name to resolve. This can either be a fully qualified class
	 * name (e.g., "com.example.MyClass") or a short class name (e.g., "MyClass").
	 * @param declaringClass The declaring class that may contain nested classes or
	 * imports that could be used to resolve the class name.
	 * @return The resolved class name, which may be a fully qualified name or the
	 * original value if it cannot be resolved. If a match is found in imports or nested
	 * classes, the resolved class name will be returned; otherwise, the original value is
	 * returned.
	 */
	private static String resolveClassName(String value, JavaClass declaringClass) {
		List<String> imports = declaringClass.getSource().getImports();

		// If it's not a fully qualified class name
		// try resolving from imports or nested classes
		if (!StringUtils.contains(value, DOT)) {
			value = resolveFromImports(value, imports, declaringClass);
		}
		// Handle fully qualified names (with a dot) or inner classes
		else {
			value = resolveFullyQualifiedClass(value, declaringClass, imports);
		}
		return value;
	}

	/**
	 * Resolves the class name from imports or nested classes for short class names.
	 * <p>
	 * This method looks for the class name in the list of imports of the declaring class.
	 * If the class name is not found in the imports, it checks if the class is a nested
	 * class of the declaring class. If a match is found, the full class name is returned;
	 * otherwise, the original short class name is returned.
	 * @param value The short class name (e.g., "MyClass") to resolve.
	 * @param imports A list of import statements in the declaring class that may contain
	 * the class name.
	 * @param declaringClass The declaring class that may contain nested classes and
	 * imports that could be used to resolve the class name.
	 * @return The fully qualified class name if found in imports or as a nested class,
	 * otherwise the original short class name.
	 */
	private static String resolveFromImports(String value, List<String> imports, JavaClass declaringClass) {
		Optional<String> importClass = imports.stream().filter(i -> i.endsWith(value)).findFirst();

		if (importClass.isPresent()) {
			return importClass.get();
		}

		// Check for nested class if not found in imports
		for (JavaClass nestedClass : declaringClass.getNestedClasses()) {
			if (nestedClass.getFullyQualifiedName().endsWith(DOT + value)) {
				return nestedClass.getFullyQualifiedName();
			}
		}

		// Return original if no match found
		return value;
	}

	/**
	 * Resolves the class name for fully qualified class names or nested classes.
	 * <p>
	 * This method processes fully qualified class names (i.e., names with package
	 * information) by checking if the class is present in the imports list of the
	 * declaring class. If it is not found in the imports, it checks if the class is a
	 * nested class of the declaring class. The method can also handle cases where the
	 * class name contains inner class references (e.g., "OuterClass$InnerClass").
	 * @param value The fully qualified class name to resolve (e.g., "com.example.MyClass"
	 * or "OuterClass$InnerClass").
	 * @param declaringClass The declaring class that may contain nested classes and
	 * imports that could be used to resolve the class name.
	 * @param imports A list of import statements in the declaring class that may contain
	 * the class name.
	 * @return The fully qualified class name if found in imports or as a nested class,
	 * otherwise the original class name is returned.
	 */
	private static String resolveFullyQualifiedClass(String value, JavaClass declaringClass, List<String> imports) {
		String[] parts = value.split("\\.", 2);
		String classNamePart = parts[0];
		String restPart = (parts.length > 1) ? parts[1] : "";

		// Try to resolve the class from imports
		Optional<String> importClass = imports.stream().filter(i -> i.endsWith(DOT + classNamePart)).findFirst();

		if (importClass.isPresent()) {
			return importClass.get() + (restPart.isEmpty() ? "" : DOT + restPart);
		}

		// If not found in imports, check if it's a nested class
		for (JavaClass nestedClass : declaringClass.getNestedClasses()) {
			if (nestedClass.getName().equals(classNamePart)) {
				return declaringClass.getFullyQualifiedName() + DOT + value;
			}
		}

		// Return original if no match found
		return value;
	}

	/**
	 * get enum info by java class
	 * @param javaClass the java class info
	 * @param builder builder
	 * @return EnumInfo
	 * @author chen qi
	 * @since 1.0.0
	 */
	public static EnumInfo getEnumInfo(JavaClass javaClass, ProjectDocConfigBuilder builder) {
		if (Objects.isNull(javaClass) || !javaClass.isEnum()) {
			return null;
		}
		if (Objects.nonNull(javaClass.getTagByName(DocTags.IGNORE))) {
			return null;
		}

		EnumInfo enumInfo = new EnumInfo();
		String comment = javaClass.getComment();
		DocletTag apiNoteTag = javaClass.getTagByName(DocTags.API_NOTE);
		enumInfo.setName(comment);
		enumInfo.setDescription(DocUtil.getEscapeAndCleanComment(
				Optional.ofNullable(apiNoteTag).map(DocletTag::getValue).orElse(StringUtil.EMPTY)));
		ApiConfig apiConfig = builder.getApiConfig();
		ApiDataDictionary dataDictionary = apiConfig.getDataDictionary(javaClass.getBinaryName());

		// Default Enum Processing (values, types, descriptions)
		enumInfo.setItems(getEnumItemList(javaClass, dataDictionary, builder));
		// value can use invoke method to get value, desc too
		if (Objects.nonNull(dataDictionary)) {
			if (StringUtils.isNotEmpty(dataDictionary.getTitle())) {
				enumInfo.setName(dataDictionary.getTitle());
			}
			return enumInfo;
		}
		return enumInfo;
	}

	/**
	 * Get annotation simpleName
	 * @param annotationName annotationName
	 * @return String
	 */
	public static String getAnnotationSimpleName(String annotationName) {
		return getClassSimpleName(annotationName);
	}

	/**
	 * Get className
	 * @param className className
	 * @return String
	 */
	public static String getClassSimpleName(String className) {
		if (className.contains(DOT)) {
			if (className.contains("<")) {
				className = className.substring(0, className.indexOf("<"));
			}
			int index = className.lastIndexOf(DOT);
			className = className.substring(index + 1);
		}
		if (className.contains("[")) {
			int index = className.indexOf("[");
			className = className.substring(0, index);
		}
		return className;
	}

	/**
	 * get Actual type
	 * @param javaClass JavaClass
	 * @return JavaClass
	 */
	public static JavaType getActualType(JavaClass javaClass) {
		return getActualTypes(javaClass).get(0);
	}

	/**
	 * get Actual type list
	 * @param javaType JavaClass
	 * @return JavaClass
	 */
	public static List<JavaType> getActualTypes(JavaType javaType) {
		if (Objects.isNull(javaType)) {
			return new ArrayList<>(0);
		}
		String typeName = javaType.getGenericFullyQualifiedName();
		if (typeName.contains("<")) {
			return ((JavaParameterizedType) javaType).getActualTypeArguments();
		}
		return new ArrayList<>(0);

	}

	/**
	 * get Actual type map
	 * @param javaClass JavaClass
	 * @return Map
	 */
	public static Map<String, JavaType> getActualTypesMap(JavaClass javaClass) {
		Map<String, JavaType> genericMap = new HashMap<>(10);
		List<JavaTypeVariable<JavaGenericDeclaration>> variables = javaClass.getTypeParameters();
		if (variables.isEmpty()) {
			return genericMap;
		}
		List<JavaType> javaTypes = getActualTypes(javaClass);
		for (int i = 0; i < variables.size(); i++) {
			if (!javaTypes.isEmpty()) {
				genericMap.put(variables.get(i).getName(), javaTypes.get(i));
			}
		}
		return genericMap;
	}

	/**
	 * Obtain the validation group classes from controller method parameter annotations.
	 * <p>
	 * This method processes a list of annotations associated with a controller method
	 * parameter to identify validation groups. It checks if any of the annotations are
	 * validation-related and retrieves their group classes. If the @Validated annotation
	 * is present and no group classes are specified, the default group class is added.
	 * The @Valid annotation is treated as equivalent to the default group since it does
	 * not have group parameters.
	 * @param annotations the list of annotations on the controller method parameter.
	 * @param builder the JavaProjectBuilder instance used to resolve annotation values.
	 * @return a set of group class names identified from the annotations, or an empty set
	 * if none are found.
	 */
	public static Set<String> getParamGroupJavaClass(List<JavaAnnotation> annotations, JavaProjectBuilder builder) {
		if (CollectionUtil.isEmpty(annotations)) {
			return new HashSet<>(0);
		}
		Set<String> javaClassList = new HashSet<>();
		for (JavaAnnotation javaAnnotation : annotations) {
			List<AnnotationValue> annotationValueList = getValidatedAnnotationValues(javaAnnotation);
			addGroupClass(annotationValueList, javaClassList, builder);
			// When using @Validated and group class is empty, add the Default group
			// class;
			// Note: @Valid does not have group parameters and is equivalent to the
			// default group.
			String simpleAnnotationName = javaAnnotation.getType().getValue();
			if (javaClassList.isEmpty() && (JSRAnnotationConstants.VALIDATED.equals(simpleAnnotationName)
					|| JSRAnnotationConstants.VALID.equals(simpleAnnotationName))) {
				javaClassList.addAll(DefaultClassConstants.DEFAULT_CLASSES);
			}
		}
		return javaClassList;
	}

	/**
	 * Obtain Validate Group classes
	 * @param javaAnnotation the annotation of controller method param
	 * @return the group annotation value
	 */
	public static Set<String> getParamGroupJavaClass(JavaAnnotation javaAnnotation) {
		if (Objects.isNull(javaAnnotation)) {
			return new HashSet<>(0);
		}
		Set<String> javaClassList = new HashSet<>();
		List<AnnotationValue> annotationValueList = getValidatedAnnotationValues(javaAnnotation);
		addGroupClass(annotationValueList, javaClassList);
		String simpleAnnotationName = javaAnnotation.getType().getValue();
		// add default group
		if (javaClassList.isEmpty() && JavaClassValidateUtil.isJSR303Required(simpleAnnotationName)) {
			// fix bug #819 https://github.com/smart-doc-group/smart-doc/issues/819
			javaClassList.addAll(DefaultClassConstants.DEFAULT_CLASSES);
		}
		return javaClassList;
	}

	/**
	 * Retrieves the Javadoc tag values for a specified tag name in a given Java class.
	 * @param cls The Java class to inspect.
	 * @param tagName The name of the tag to search for.
	 * @param checkComments Indicates whether to validate the presence of comments for
	 * empty tag values.
	 * @return A comma-separated string of all values found for the specified tag, or an
	 * empty string if the tag name is empty.
	 * @throws RuntimeException If the tag is used without comments and checkComments is
	 * true.
	 */
	public static String getClassTagsValue(final JavaClass cls, final String tagName, boolean checkComments) {
		if (StringUtil.isNotEmpty(tagName)) {
			StringBuilder result = new StringBuilder();
			List<DocletTag> tags = cls.getTags();
			for (DocletTag tag : tags) {
				if (!tagName.equals(tag.getName())) {
					continue;
				}
				String value = tag.getValue();
				if (StringUtil.isEmpty(value) && checkComments) {
					throw new RuntimeException("ERROR: #" + cls.getName() + "() - bad @" + tagName
							+ " Javadoc tag usage from " + cls.getName() + ", must be add comment if you use it.");
				}
				if (tagName.equals(tag.getName())) {
					if (result.length() > 0) {
						result.append(",");
					}
					result.append(value);
				}
			}
			return result.toString();
		}
		return "";
	}

	/**
	 * Get Map of final field and value
	 * @param clazz Java class
	 * @return Map
	 * @throws IllegalAccessException IllegalAccessException
	 */
	public static Map<String, String> getFinalFieldValue(Class<?> clazz) throws IllegalAccessException {
		String className = getClassSimpleName(clazz.getName());
		Field[] fields = clazz.getDeclaredFields();
		Map<String, String> constants = new HashMap<>(16);
		for (Field field : fields) {
			if (Modifier.isPrivate(field.getModifiers())) {
				continue;
			}
			if (Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
				String name = field.getName();
				constants.put(className + DOT + name, String.valueOf(field.get(null)));
			}
		}
		return constants;
	}

	/**
	 * Add group class
	 * @param annotationValueList annotation value list
	 * @param javaClassList java class list
	 */
	private static void addGroupClass(List<AnnotationValue> annotationValueList, Set<String> javaClassList) {
		if (CollectionUtil.isEmpty(annotationValueList)) {
			return;
		}
		for (AnnotationValue annotationValue : annotationValueList) {
			TypeRef typeRef = (TypeRef) annotationValue;
			DefaultJavaParameterizedType annotationValueType = (DefaultJavaParameterizedType) typeRef.getType();
			javaClassList.add(annotationValueType.getGenericFullyQualifiedName());
		}
	}

	/**
	 * Add group class
	 * @param annotationValueList annotation value list
	 * @param javaClassList java class list
	 * @param builder JavaProjectBuilder
	 */
	private static void addGroupClass(List<AnnotationValue> annotationValueList, Set<String> javaClassList,
			JavaProjectBuilder builder) {
		if (CollectionUtil.isEmpty(annotationValueList)) {
			return;
		}
		for (AnnotationValue annotationValue : annotationValueList) {
			TypeRef typeRef = (TypeRef) annotationValue;
			DefaultJavaParameterizedType annotationValueType = (DefaultJavaParameterizedType) typeRef.getType();
			String genericCanonicalName = annotationValueType.getGenericFullyQualifiedName();
			JavaClass classByName = builder.getClassByName(genericCanonicalName);
			recursionGetAllValidInterface(classByName, javaClassList, builder);
			javaClassList.add(genericCanonicalName);
		}
	}

	/**
	 * Recursively adds all valid interfaces to the provided set.
	 * @param classByName The Java class to start the recursion from.
	 * @param javaClassSet The set to which valid interfaces will be added.
	 * @param builder The JavaProjectBuilder instance used for class lookup.
	 */
	private static void recursionGetAllValidInterface(JavaClass classByName, Set<String> javaClassSet,
			JavaProjectBuilder builder) {
		List<JavaType> anImplements = classByName.getImplements();
		if (CollectionUtil.isEmpty(anImplements)) {
			return;
		}
		for (JavaType javaType : anImplements) {
			String genericFullyQualifiedName = javaType.getGenericFullyQualifiedName();
			javaClassSet.add(genericFullyQualifiedName);
			// skip default group
			if (DefaultClassConstants.DEFAULT_CLASSES.contains(genericFullyQualifiedName)) {
				continue;
			}
			JavaClass implementJavaClass = builder.getClassByName(genericFullyQualifiedName);
			recursionGetAllValidInterface(implementJavaClass, javaClassSet, builder);
		}
	}

	/**
	 * Retrieves a list of validated annotation values.
	 * <p>
	 * This method processes and extracts validation-related information from a given
	 * annotation object, based on the list of validation annotation names provided. It
	 * first determines the annotation type, then identifies the property name to extract
	 * based on the annotation type, and finally retrieves the corresponding annotation
	 * values according to that property name.
	 * </p>
	 * @param validates A list containing the names of annotations that need validation,
	 * used to identify valid annotations.
	 * @param javaAnnotation A JavaAnnotation object representing the annotation being
	 * processed.
	 * @return Returns a list of AnnotationValue objects containing valid validation
	 * annotation values. If the property name cannot be determined or the annotation does
	 * not contain valid validation information, an empty list is returned.
	 */
	private static List<AnnotationValue> getValidatedAnnotationValues(Set<String> validates,
			JavaAnnotation javaAnnotation) {
		String simpleName = javaAnnotation.getType().getValue();

		// Determine the property name based on the annotation type
		String propertyName = null;
		if (JSRAnnotationConstants.VALIDATED.equalsIgnoreCase(simpleName)) {
			propertyName = DocAnnotationConstants.VALUE_PROP;
		}
		else if (validates.contains(simpleName)) {
			propertyName = DocAnnotationConstants.GROUP_PROP;
		}

		// If propertyName is determined, extract the annotation values
		if (propertyName != null) {
			return getAnnotationValues(javaAnnotation, propertyName);
		}

		return Collections.emptyList();
	}

	/**
	 * Retrieves a list of validated annotation values.
	 * <p>
	 * This method processes and extracts validation-related information from a given
	 * annotation object, based on the list of validation annotation names provided. It
	 * first determines the annotation type, then identifies the property name to extract
	 * based on the annotation type, and finally retrieves the corresponding annotation
	 * values according to that property name.
	 * </p>
	 * @param javaAnnotation A JavaAnnotation object representing the annotation being
	 * processed.
	 * @return Returns a list of AnnotationValue objects containing valid validation
	 * annotation values. If the property name cannot be determined or the annotation does
	 * not contain valid validation information, an empty list is returned.
	 */
	private static List<AnnotationValue> getValidatedAnnotationValues(JavaAnnotation javaAnnotation) {
		return getValidatedAnnotationValues(DocValidatorAnnotationEnum.VALIDATOR_ANNOTATIONS, javaAnnotation);
	}

	/**
	 * Generic parameter map.
	 * @param genericMap generic map
	 * @param cls Java class
	 * @param globGicName generic name array
	 */
	public static void genericParamMap(Map<String, String> genericMap, JavaClass cls, String[] globGicName) {
		if (Objects.isNull(cls) || Objects.isNull(cls.getTypeParameters())) {
			return;
		}
		List<JavaTypeVariable<JavaGenericDeclaration>> variables = cls.getTypeParameters();
		if (!variables.isEmpty()) {
			for (int i = 0; i < cls.getTypeParameters().size() && i < globGicName.length; i++) {
				genericMap.put(variables.get(i).getName(), globGicName[i]);
			}
			return;
		}
		try {
			Class<?> c = Class.forName(cls.getCanonicalName());
			TypeVariable<?>[] tValue = c.getTypeParameters();
			for (int i = 0; i < tValue.length && i < globGicName.length; i++) {
				genericMap.put(tValue[i].getName(), globGicName[i]);
			}
		}
		catch (ClassNotFoundException e) {
			// skip
		}
	}

	/**
	 * Formats a Java type string. This method processes and formats the return type
	 * string, removing generic uncertainty indicators and spaces.
	 * @param returnType The original return type string, which may contain generic
	 * uncertainty indicators.
	 * @return The formatted return type string, with '?', ' ', and 'extends' characters
	 * removed.
	 */
	public static String javaTypeFormat(String returnType) {
		// Check if the return type string contains '?'. If it does, format the string.
		if (returnType.contains("?")) {
			// Use regex to remove '?', spaces, and 'extends' from the string and return
			// the formatted string.
			return returnType.replaceAll("[?\\s]", "").replaceAll("extends", "");
		}
		// If the return type string does not contain '?', return the original string.
		return returnType;
	}

	/**
	 * Determines if one class is a child of another class.
	 * <p>
	 * This method checks whether the class represented by `sourceClass` is a subclass of
	 * the class represented by `targetClass`. It returns true if `sourceClass` is a
	 * subclass or identical to `targetClass`, and false otherwise. If either
	 * `sourceClass` or `targetClass` cannot be found, it returns false and logs a warning
	 * message.
	 * </p>
	 * @param sourceClass The name of the class to check.
	 * @param targetClass The name of the target class to determine if `sourceClass` is a
	 * subclass of.
	 * @return true if `sourceClass` is a subclass or identical to `targetClass`; false
	 * otherwise.
	 */
	public static boolean isTargetChildClass(String sourceClass, String targetClass) {
		try {
			// If the sourceClass is the same as the targetClass, return true.
			if (sourceClass.equals(targetClass)) {
				return true;
			}
			// Obtain the Class object for the sourceClass.
			Class<?> c = Class.forName(sourceClass);
			// Loop through the inheritance hierarchy until a match is found or the top of
			// the hierarchy is reached.
			while (c != null) {
				// If the current class matches the targetClass, return true.
				if (c.getName().equals(targetClass)) {
					return true;
				}
				// Get the superclass of the current class.
				c = c.getSuperclass();
			}
		}
		// Catch the exception when the class cannot be found.
		catch (ClassNotFoundException e) {
			// Log a warning message.
			logger.warning("JavaClass.isTargetChildClass() Unable to find class " + sourceClass);
			return false;
		}
		// Return false if no match was found or an exception occurred.
		return false;
	}

	/**
	 * Retrieves all fields marked for JSON serialization ignoring from a given class.
	 * This method inspects the annotations of the class to find field names that are
	 * marked as ignored for JSON serialization.
	 * @param cls The JavaClass object representing the class to inspect its annotations.
	 * @return A map containing field names and their ignore properties. Returns an empty
	 * map if not found or if input is null.
	 */
	public static Map<String, String> getClassJsonIgnoreFields(JavaClass cls) {
		if (Objects.isNull(cls)) {
			return Collections.emptyMap();
		}
		List<JavaAnnotation> classAnnotation = cls.getAnnotations();
		Map<String, String> ignoreFields = new HashMap<>(16);

		for (JavaAnnotation annotation : classAnnotation) {
			String simpleAnnotationName = annotation.getType().getValue();
			if (DocAnnotationConstants.SHORT_JSON_IGNORE_PROPERTIES.equalsIgnoreCase(simpleAnnotationName)) {
				return JavaClassUtil.getJsonIgnoresProp(annotation, DocAnnotationConstants.VALUE_PROP);
			}
			if (DocAnnotationConstants.SHORT_JSON_TYPE.equals(simpleAnnotationName)) {
				return JavaClassUtil.getJsonIgnoresProp(annotation, DocAnnotationConstants.IGNORE_PROP);
			}
		}
		return ignoreFields;
	}

	/**
	 * Retrieves specified ignored properties from a Java annotation in JSON format. This
	 * method parses a specific parameter from an annotation and converts it into a map
	 * format for further processing. It handles three scenarios: the parameter does not
	 * exist, is a single value, or is multiple values.
	 * @param annotation The input Java annotation.
	 * @param propName The name of the parameter in the annotation that indicates which
	 * properties should be ignored.
	 * @return A Map where keys are property names to be ignored and values are null.
	 */
	@SuppressWarnings({ "unchecked" })
	public static Map<String, String> getJsonIgnoresProp(JavaAnnotation annotation, String propName) {
		Map<String, String> ignoreFields = new HashMap<>(16);
		Object ignoresObject = annotation.getNamedParameter(propName);
		if (Objects.isNull(ignoresObject)) {
			return ignoreFields;
		}
		if (ignoresObject instanceof String) {
			String prop = StringUtil.removeQuotes(ignoresObject.toString());
			ignoreFields.put(prop, null);
			return ignoreFields;
		}
		LinkedList<String> ignorePropList = (LinkedList<String>) ignoresObject;
		for (String str : ignorePropList) {
			String prop = StringUtil.removeQuotes(str);
			ignoreFields.put(prop, null);
		}
		return ignoreFields;
	}

	/**
	 * getFieldGenericType by ClassLoader
	 * @param javaField JavaField
	 * @param classLoader ClassLoader
	 * @return fieldGenericType
	 */
	private static String getFieldGenericType(JavaField javaField, ClassLoader classLoader) {
		if (JavaClassValidateUtil.isPrimitive(javaField.getType().getGenericCanonicalName())
				|| (javaField.isFinal() && javaField.isPrivate())) {
			return null;
		}
		String name = javaField.getName();
		try {
			Class<?> c;
			if (Objects.nonNull(classLoader)) {
				c = classLoader.loadClass(javaField.getDeclaringClass().getBinaryName());
			}
			else {
				c = Class.forName(javaField.getDeclaringClass().getBinaryName());
			}
			Field f = c.getDeclaredField(name);
			f.setAccessible(true);
			Type t = f.getGenericType();
			return StringUtil.trim(t.getTypeName());
		}
		catch (NoSuchFieldException | ClassNotFoundException | NoClassDefFoundError e) {
			return null;
		}
	}

	/**
	 * Retrieves the generic return type name of a Java method.
	 * @param javaMethod The Java method object from which to extract the return type
	 * information.
	 * @param classLoader The class loader used to load the class. If null, uses
	 * Class.forName to load the class.
	 * @return The string representation of the generic return type name, or null if the
	 * generic type cannot be determined.
	 */
	private static String getReturnGenericType(JavaMethod javaMethod, ClassLoader classLoader) {
		String methodName = javaMethod.getName();
		// `BinaryName` is the correct name for inner classes required by
		// `ClassLoader.loadClass`
		// and `Class.forName`, as inner class paths use `$` instead of `.`.
		String binaryName = javaMethod.getDeclaringClass().getBinaryName();
		try {
			Class<?> c;
			if (Objects.nonNull(classLoader)) {
				c = classLoader.loadClass(binaryName);
			}
			else {
				c = Class.forName(binaryName);
			}

			Method m = c.getDeclaredMethod(methodName);
			Type t = m.getGenericReturnType();
			return StringUtil.trim(t.getTypeName());
		}
		catch (ClassNotFoundException | NoSuchMethodException e) {
			return null;
		}
	}

	/**
	 * Replaces generic type parameters in a given type name with their corresponding
	 * actual types, based on the provided mapping.
	 * @param originalName the original type name containing generic type parameters
	 * @param actualTypesMap a mapping of generic type parameter names to their
	 * corresponding actual types
	 * @return the type name with generic type parameters replaced by their actual types
	 */
	public static String getGenericsNameByActualTypesMap(String originalName, Map<String, JavaType> actualTypesMap) {
		// Find the index of the last left angle bracket '<' and the first right angle
		// bracket '>'
		int typeNameLastLeftIndex = originalName.lastIndexOf('<');
		int typeNameFirstRightIndex = originalName.indexOf('>', typeNameLastLeftIndex);

		// If both angle brackets are found
		if (typeNameLastLeftIndex > 0 && typeNameFirstRightIndex > 0) {
			// Extract the substring containing the generics
			String genericsString = originalName.substring(typeNameLastLeftIndex + 1, typeNameFirstRightIndex);
			String[] generics = genericsString.split(",");

			// StringBuilder to build the replaced string
			StringBuilder resultString = new StringBuilder();
			// Append the portion of originalName before the generics, including the '<'
			resultString.append(originalName, 0, typeNameLastLeftIndex + 1);

			// Replace each generic type
			for (String generic : generics) {
				// Trim the generic type to remove leading/trailing whitespaces
				String trimmedGeneric = generic.trim();
				// Look up the mapped type in the actualTypesMap
				JavaType mappedType = actualTypesMap.get(trimmedGeneric);
				// If a mapping is found, append the mapped type; otherwise, keep the
				// original generic type
				resultString.append(mappedType != null ? mappedType.getCanonicalName() : trimmedGeneric);
				// Append a comma after each replaced generic type
				resultString.append(",");
			}
			// Remove the trailing comma
			resultString.setLength(resultString.length() - 1);
			// Append the portion of originalName after the generics, including the '>'
			resultString.append(originalName, typeNameFirstRightIndex, originalName.length());

			return resultString.toString();
		}
		// Return originalName unchanged if no generics are found
		return originalName;
	}

	/**
	 * Extracts `@JsonView` value classes from a collection of annotations.
	 * @param annotations the collection of Java annotations to process
	 * @param builder the project documentation configuration builder, used to access
	 * configuration details
	 * @return a set containing all the JSON view classes extracted from the annotations.
	 * If the input annotation collection is empty, an empty set is returned.
	 */
	public static Set<String> getParamJsonViewClasses(Collection<JavaAnnotation> annotations,
			ProjectDocConfigBuilder builder) {
		if (CollectionUtil.isEmpty(annotations)) {
			return Collections.emptySet();
		}
		Set<String> result = new HashSet<>();
		for (JavaAnnotation annotation : annotations) {
			return getJsonViewClasses(annotation, builder, true);
		}
		return result;
	}

	/**
	 * Retrieves a set of fully qualified class names associated with the JsonView.
	 * @param javaAnnotation The Java annotation containing the JsonView information,
	 * which
	 * @param builder The project configuration builder used to retrieve class information
	 * by name.
	 * @param isParam if isParam,just return the type;if isParam is false, return the
	 * super class and interface
	 * @return A set of fully qualified class names related to the JsonView.
	 */
	public static Set<String> getJsonViewClasses(JavaAnnotation javaAnnotation, ProjectDocConfigBuilder builder,
			boolean isParam) {

		String annotationName = javaAnnotation.getType().getValue();
		if (!DocAnnotationConstants.SHORT_JSON_VIEW.equals(annotationName)) {
			return Collections.emptySet();
		}

		// Retrieve fully qualified class names from the annotation property
		List<String> classNames = getAnnotationValueClassNames(javaAnnotation, DocAnnotationConstants.VALUE_PROP);

		// If class names are present, process them to get class and its super
		// classes/interfaces
		Set<String> result = new HashSet<>();
		if (CollectionUtil.isNotEmpty(classNames)) {
			if (isParam) {
				result.addAll(classNames);
			}
			else {
				classNames.forEach(typeName -> {
					JavaClass clazz = builder.getClassByName(typeName);
					if (clazz != null) {
						result.addAll(getSuperJavaClassAndInterface(clazz));
					}
				});
			}
		}

		return result;

	}

	/**
	 * Determines whether a field should be excluded from the JSON output based on the
	 * {@code @JsonView} annotation present on the method.
	 * <p>
	 * This method uses the {@code shouldIncludeFieldInJsonView} method to determine if
	 * the field should be included and negates the result to determine if it should be
	 * excluded.
	 * @param annotation the annotation present on the field, typically {@code @JsonView}.
	 * @param methodJsonViewClasses the set of {@code JsonView} classes specified on the
	 * method.
	 * @param isResp a boolean indicating whether the current context is a response.
	 * @param projectBuilder the project configuration builder.
	 * @return {@code true} if the field should be excluded from the JSON output,
	 * otherwise {@code false}.
	 */
	public static boolean shouldExcludeFieldFromJsonView(JavaAnnotation annotation, Set<String> methodJsonViewClasses,
			boolean isResp, ProjectDocConfigBuilder projectBuilder) {
		return !shouldIncludeFieldInJsonView(annotation, methodJsonViewClasses, isResp, projectBuilder);
	}

	/**
	 * Determines if a field should be included in the JSON response based on the presence
	 * of {@code @JsonView} annotations on the method and field. This method checks if the
	 * field's annotation matches any of the method's JsonView classes when the context is
	 * a response.
	 * @param annotation The annotation on the field being checked.
	 * @param methodJsonViewClasses The set of JsonView classes associated with the
	 * method.
	 * @param isResponse Whether the current context is for a response (true) or a request
	 * (false).
	 * @param projectBuilder The project configuration builder used to resolve classes.
	 * @return {@code true} if the field should be included in the JSON view;
	 * {@code false} otherwise.
	 */
	public static boolean shouldIncludeFieldInJsonView(JavaAnnotation annotation, Set<String> methodJsonViewClasses,
			boolean isResponse, ProjectDocConfigBuilder projectBuilder) {
		String simpleAnnotationName = annotation.getType().getValue();

		// If context is not a response or no JsonView classes are defined for the method,
		// include the field
		if (!isResponse || methodJsonViewClasses.isEmpty()) {
			return true;
		}

		// If the annotation is not JsonView, exclude the field
		if (!DocAnnotationConstants.SHORT_JSON_VIEW.equals(simpleAnnotationName)) {
			return false;
		}

		// Check if the field's JsonView classes match any of the method's JsonView
		// classes
		Set<String> paramJsonViewClasses = getJsonViewClasses(annotation, projectBuilder, true);
		return !Collections.disjoint(methodJsonViewClasses, paramJsonViewClasses);
	}

	/**
	 * Recursively retrieves the fully qualified names of superclasses and interfaces of a
	 * given Java class.
	 * @param clazz The Java class to process.
	 * @return A set of fully qualified names of superclasses and interfaces.
	 */
	public static Set<String> getSuperJavaClassAndInterface(JavaClass clazz) {
		Set<String> superClassesAndInterfaces = new HashSet<>();
		superClassesAndInterfaces.add(clazz.getFullyQualifiedName());
		// Get and add the superclass, if it is not Object
		JavaClass parentClass = clazz.getSuperJavaClass();
		if (Objects.nonNull(parentClass) && !JavaTypeConstants.OBJECT_SIMPLE_NAME.equals(parentClass.getSimpleName())) {
			superClassesAndInterfaces.add(parentClass.getFullyQualifiedName());

			// Recursively get superclasses and interfaces for the parent class
			superClassesAndInterfaces.addAll(getSuperJavaClassAndInterface(parentClass));
		}

		// Add all implemented interfaces
		for (JavaClass anInterface : clazz.getInterfaces()) {
			superClassesAndInterfaces.add(anInterface.getFullyQualifiedName());

			// Recursively get interfaces for the interface itself
			superClassesAndInterfaces.addAll(getSuperJavaClassAndInterface(anInterface));
		}

		return superClassesAndInterfaces;
	}

	/**
	 * Retrieves a list of fully qualified class names from a specified annotation
	 * property.
	 * <p>
	 * This method extracts the fully qualified names of classes referenced by a specific
	 * property within a Java annotation. It handles both single class references and
	 * lists of class references. If the property is not found or has no valid class
	 * references, an empty list is returned.
	 * </p>
	 * @param javaAnnotation the annotation containing the property
	 * @param propertyName the name of the property to retrieve
	 * @return a list of fully qualified class names or an empty list if not present
	 */
	public static List<String> getAnnotationValueClassNames(JavaAnnotation javaAnnotation, String propertyName) {
		AnnotationValue propertyValue = javaAnnotation.getProperty(propertyName);
		if (propertyValue != null) {
			if (propertyValue instanceof AnnotationValueList) {
				return ((AnnotationValueList) propertyValue).getValueList()
					.stream()
					.filter(v -> v instanceof TypeRef)
					.map(v -> ((TypeRef) v).getType().getFullyQualifiedName())
					.collect(Collectors.toList());
			}
			if (propertyValue instanceof TypeRef) {
				return Collections.singletonList(((TypeRef) propertyValue).getType().getFullyQualifiedName());
			}
		}
		return Collections.emptyList();
	}

	/**
	 * Retrieves a list of string values from a specified annotation property.
	 * <p>
	 * This method extracts string values referenced by a specific property within a Java
	 * annotation. It handles both single string values and lists of string values. If the
	 * property is not found or has no valid string values, an empty list is returned.
	 * </p>
	 * @param projectBuilder the project configuration builder
	 * @param javaAnnotation the annotation containing the property
	 * @param propertyName the name of the property to retrieve
	 * @return a list of string values or an empty list if not present
	 */
	public static List<String> getAnnotationValueStrings(ProjectDocConfigBuilder projectBuilder,
			JavaAnnotation javaAnnotation, String propertyName) {
		AnnotationValue propertyValue = javaAnnotation.getProperty(propertyName);
		if (propertyValue != null) {
			if (propertyValue instanceof AnnotationValueList) {
				return ((AnnotationValueList) propertyValue).getValueList()
					.stream()
					.filter(temp -> Objects.nonNull(temp) && temp instanceof Constant)
					.map(temp -> ((Constant) temp).getValue().toString())
					.filter(StringUtil::isNotEmpty)
					.collect(Collectors.toList());
			}
			String value = DocUtil.resolveAnnotationValue(projectBuilder.getApiConfig().getClassLoader(),
					propertyValue);
			return Collections.singletonList(value);
		}
		return Collections.emptyList();
	}

	/**
	 * Retrieves a list of AnnotationValue objects from a specified annotation property.
	 * <p>
	 * This method extracts the AnnotationValue objects referenced by a specific property
	 * within a Java annotation. It handles both single value references and lists of
	 * value references.
	 * </p>
	 * @param javaAnnotation the annotation containing the property
	 * @param propertyName the name of the property to retrieve
	 * @return a list of AnnotationValue objects if present, otherwise an empty list
	 */
	public static List<AnnotationValue> getAnnotationValues(JavaAnnotation javaAnnotation, String propertyName) {
		AnnotationValue annotationValue = javaAnnotation.getProperty(propertyName);
		if (Objects.isNull(annotationValue)) {
			return Collections.emptyList();
		}
		if (annotationValue instanceof AnnotationValueList) {
			return ((AnnotationValueList) annotationValue).getValueList();
		}
		if (annotationValue instanceof TypeRef) {
			return Collections.singletonList(annotationValue);
		}
		return Collections.emptyList();
	}

	/**
	 * Retrieves enum information and values for a given Java class.
	 * <p>
	 * This method first gathers general information about the enum, then collects all
	 * enum values and their descriptions. If the enum is used in a form, it only collects
	 * names and sets the type as string.
	 * @param javaClass The Java class object representing the enum.
	 * @param builder The project documentation configuration builder, used to access
	 * project-specific documentation settings.
	 * @param jsonEnum Whether it is an enum in JSON
	 * @return An EnumInfoAndValues object containing both the enum information and its
	 * values.
	 */
	public static EnumInfoAndValues getEnumInfoAndValue(JavaClass javaClass, ProjectDocConfigBuilder builder,
			boolean jsonEnum) {
		// Step 1: Retrieve EnumInfo (general enum info like name, description, etc.)
		EnumInfo enumInfo = getEnumInfo(javaClass, builder);

		// Return if the enumInfo is null
		if (enumInfo == null) {
			return null;
		}

		return generateEnumInfoAndValues(enumInfo, javaClass, builder, jsonEnum);
	}

	/**
	 * Gets the enum item list, either through the data dictionary or @JsonValue or the
	 * enumerated field defaults to the first parameter
	 * @param enumClass the enum class info
	 * @param dataDictionary data dictionary
	 * @param builder builder
	 * @return List<Item>
	 * @author JasonKung22
	 */
	private static List<Item> getEnumItemList(JavaClass enumClass, ApiDataDictionary dataDictionary,
			ProjectDocConfigBuilder builder) {
		ApiConfig apiConfig = builder.getApiConfig();
		if (Objects.nonNull(dataDictionary)) {
			Class<?> dataDictionaryEnumClass = dataDictionary.getEnumClass();
			if (dataDictionaryEnumClass.isInterface()) {
				try {
					ClassLoader classLoader = apiConfig.getClassLoader();
					dataDictionaryEnumClass = classLoader.loadClass(enumClass.getBinaryName());
				}
				catch (ClassNotFoundException e) {
					return null;
				}
			}
			List<EnumDictionary> enumInformation = EnumUtil.getEnumInformation(dataDictionaryEnumClass,
					dataDictionary.getCodeField(), dataDictionary.getDescField());
			return enumInformation.stream()
				.map(i -> new Item(i.getName(), i.getType(), i.getValue(), i.getDesc()))
				.collect(Collectors.toList());
		}

		List<JavaField> enumConstants = enumClass.getEnumConstants();
		return enumConstants.stream().map(cons -> {
			Item item = new Item();
			String name = cons.getName();
			String enumComment = cons.getComment();
			item.setName(name);
			item.setValue(name);
			item.setType(ParamTypeConstants.PARAM_TYPE_ENUM);
			item.setDescription(enumComment);

			Object defaultEnumValue = getDefaultEnumValue(enumClass, builder, cons);
			if (defaultEnumValue == null) {
				item.setValue(StringUtil.removeDoubleQuotes(name));
				return item;
			}
			String stringValue = StringUtil.removeQuotes(String.valueOf(defaultEnumValue));

			if (!StringUtils.equals(name, stringValue)) {
				item.setValue(stringValue);
				item.setType(DocClassUtil.processTypeNameForParams(defaultEnumValue.getClass().getCanonicalName()));
			}
			return item;
		}).collect(Collectors.toList());
	}

	/**
	 * Generate EnumInfoAndValues, and determine the enumNameExample configuration in
	 * apiConfig
	 * @param enumInfo the enum info
	 * @param javaClass The Java class object representing the enum.
	 * @param builder builder
	 * @param jsonEnum Whether it is an enum in JSON
	 * @return List<Item>
	 * @author JasonKung22
	 */
	private static EnumInfoAndValues generateEnumInfoAndValues(EnumInfo enumInfo, JavaClass javaClass,
			ProjectDocConfigBuilder builder, boolean jsonEnum) {
		ApiConfig apiConfig = builder.getApiConfig();
		List<Item> items = enumInfo.getItems();
		String enumValue = null;
		List<String> enumValues = null;
		String type = null;

		if (jsonEnum || apiConfig.isEnumConvertor()) {
			List<JavaField> enumConstants = javaClass.getEnumConstants();
			List<Object> enumValueList = enumConstants.stream()
				.map(cons -> getEnumValueWithJsonValue(javaClass, builder, cons))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

			if (CollectionUtil.isNotEmpty(enumValueList)) {
				Object enumValueWithJsonValue = enumValueList.get(0);
				type = DocClassUtil.processTypeNameForParams(enumValueWithJsonValue.getClass().getCanonicalName());
				enumValue = String.valueOf(enumValueWithJsonValue);
				enumValues = enumValueList.stream().map(String::valueOf).collect(Collectors.toList());
			}
		}

		if (enumValue == null) {
			enumValues = items.stream().map(Item::getName).collect(Collectors.toList());
			if (CollectionUtil.isNotEmpty(enumValues)) {
				enumValue = enumValues.get(0);
			}
			type = ParamTypeConstants.PARAM_TYPE_ENUM;
		}

		return EnumInfoAndValues.builder()
			.setEnumInfo(enumInfo)
			.setEnumValues(enumValues)
			.setType(type)
			.setValue(enumValue);
	}

}
