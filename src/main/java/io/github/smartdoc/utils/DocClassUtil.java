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
package io.github.smartdoc.utils;

import io.github.smartdoc.filter.ReturnTypeProcessor;
import io.github.smartdoc.model.ApiReturn;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Description: Doc class handle util
 *
 * @author yu 2018//14.
 */
public class DocClassUtil {

	/**
	 * private constructor
	 */
	private DocClassUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * get class names by generic class name
	 * @param typeName generic class name
	 * @return array of string
	 */
	public static String[] getSimpleGicName(String typeName) {
		if (JavaClassValidateUtil.isCollection(typeName)) {
			typeName = typeName + "<T>";
		}
		else if (JavaClassValidateUtil.isArray(typeName)) {
			typeName = typeName.substring(0, typeName.lastIndexOf("["));
			typeName = "java.util.List<" + typeName + ">";
		}
		else if (JavaClassValidateUtil.isMap(typeName)) {
			typeName = typeName + "<String,T>";
		}
		if (typeName.contains("<")) {
			String pre = typeName.substring(0, typeName.indexOf("<"));
			if (JavaClassValidateUtil.isMap(pre)) {
				return getMapKeyValueType(typeName);
			}
			if (JavaClassValidateUtil.isCollection(pre)) {
				String type = typeName.substring(typeName.indexOf("<") + 1, typeName.lastIndexOf(">"));
				return type.split(" ");
			}

			String[] arr = Arrays.stream(getGicName(typeName))
				.map(str -> str.split(","))
				.map(Arrays::asList)
				.flatMap(Collection::stream)
				.toArray(String[]::new);

			return classNameFix(arr);
		}
		else {
			return new String[0];
		}
	}

	/**
	 * get class names by generic class name.<br>
	 * "controller.R<T,A>$Data<T,A>" =====> ["T,A", "T,A"]
	 * @param typeName generic class name
	 * @return array of string
	 */
	static String[] getGicName(String typeName) {
		StringBuilder builder = new StringBuilder(typeName.length());
		List<String> ginNameList = new ArrayList<>();
		int ltLen = 0;
		for (char c : typeName.toCharArray()) {
			if (c == '<' || c == '>') {
				ltLen += (c == '<') ? 1 : -1;
				// Skip the outermost symbols <
				if (c == '<' && ltLen == 1) {
					continue;
				}
			}
			if (ltLen > 0) {
				builder.append(c);
			}
			else if (ltLen == 0 && c == '>') {
				ginNameList.add(builder.toString());
				builder.setLength(0);
			}
		}
		return ginNameList.toArray(new String[0]);
	}

	/**
	 * Get a simple type name from a generic class name
	 * @param gicName Generic class name
	 * @return String
	 */
	public static String getSimpleName(String gicName) {
		// remove strings contained in '< >' and < >. e.g. controller.R<T,A>$Data<T,A>
		// =====> controller.R$Data
		StringBuilder builder = new StringBuilder(gicName.length());
		int ltLen = 0;
		for (char c : gicName.toCharArray()) {
			if (c == '<' || c == '>') {
				ltLen += (c == '<') ? 1 : -1;
			}
			else if (ltLen <= 0) {
				builder.append(c);
			}
		}
		return builder.toString();
	}

	/**
	 * Automatic repair of generic split class names
	 * @param arr arr of class name
	 * @return array of String
	 */
	private static String[] classNameFix(String[] arr) {
		List<String> classes = new ArrayList<>();
		List<Integer> indexList = new ArrayList<>();
		int globIndex = 0;
		int length = arr.length;
		for (int i = 0; i < length; i++) {
			if (!classes.isEmpty()) {
				int index = classes.size() - 1;
				if (!isClassName(classes.get(index))) {
					globIndex = globIndex + 1;
					if (globIndex < length) {
						indexList.add(globIndex);
						String className = classes.get(index) + "," + arr[globIndex];
						classes.set(index, className);
					}
				}
				else {
					globIndex = globIndex + 1;
					if (globIndex < length) {
						if (isClassName(arr[globIndex])) {
							indexList.add(globIndex);
							classes.add(arr[globIndex]);
						}
						else {
							if (!indexList.contains(globIndex) && !indexList.contains(globIndex + 1)) {
								indexList.add(globIndex);
								classes.add(arr[globIndex] + "," + arr[globIndex + 1]);
								globIndex = globIndex + 1;
								indexList.add(globIndex);
							}
						}
					}
				}
			}
			else {
				if (isClassName(arr[i])) {
					indexList.add(i);
					classes.add(arr[i]);
				}
				else {
					if (!indexList.contains(i) && !indexList.contains(i + 1)) {
						globIndex = i + 1;
						classes.add(arr[i] + "," + arr[globIndex]);
						indexList.add(i);
						indexList.add(i + 1);
					}
				}
			}
		}
		return classes.toArray(new String[0]);
	}

	/**
	 * get map key and value type name populate into array.
	 * @param gName generic class name
	 * @return array of string
	 */
	public static String[] getMapKeyValueType(String gName) {
		if (StringUtil.isEmpty(gName)) {
			return new String[0];
		}
		// Find the positions of the outermost '<' and '>' characters
		int leftAngleBracket = gName.indexOf('<');
		// If no matching angle brackets are found, return new String[0]
		if (leftAngleBracket == -1) {
			return new String[0];
		}
		int rightAngleBracket = findMatchingAngleBracket(gName, leftAngleBracket);

		// If no matching angle brackets are found, return new String[0]
		if (rightAngleBracket == -1) {
			return new String[0];
		}
		// Extract the content inside "Map<...>"
		String insideMap = gName.substring(leftAngleBracket + 1, rightAngleBracket);

		// Split the content into the key and value parts
		return splitKeyValue(insideMap);
	}

	/**
	 * Splits the content inside a generic Map declaration into key and value parts. This
	 * method identifies the position of the comma that separates the key and value types,
	 * ensuring that it is outside any nested angle brackets.
	 * @param insideMap The string inside the angle brackets of the Map declaration, e.g.,
	 * "A, Map<B, C>".
	 * @return An array of two elements: the key type and the value type, or {@code null}
	 * if the format is invalid.
	 */
	private static String[] splitKeyValue(String insideMap) {
		int commaIndex = findCommaOutsideBrackets(insideMap);
		if (commaIndex == -1) {
			return new String[0];
		}

		String key = insideMap.substring(0, commaIndex).trim();
		String value = insideMap.substring(commaIndex + 1).trim();
		return new String[] { key, value };
	}

	/**
	 * Finds the position of the closing '>' that matches the opening '<' at the given
	 * position. This method scans the string starting from the position of the first '<'
	 * and tracks the depth of nested angle brackets to find the correct closing '>'.
	 * @param str The string to search in.
	 * @param start The position of the first '<'.
	 * @return The index of the matching '>', or -1 if no matching bracket is found.
	 */
	private static int findMatchingAngleBracket(String str, int start) {
		int depth = 0;
		for (int i = start; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch == '<') {
				depth++;
			}
			else if (ch == '>') {
				depth--;
				if (depth == 0) {
					return i;
				}
			}
		}
		// No matching closing angle bracket found
		return -1;
	}

	/**
	 * Finds the position of the comma that separates the key and value types in a generic
	 * declaration. This method ensures that the comma is not inside any nested angle
	 * brackets.
	 * @param str The string to search in, e.g., "A, Map<B, C>".
	 * @return The index of the comma, or -1 if no suitable comma is found.
	 */
	private static int findCommaOutsideBrackets(String str) {
		int depth = 0;
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch == '<') {
				depth++;
			}
			else if (ch == '>') {
				depth--;
			}
			else if (ch == ',' && depth == 0) {
				return i;
			}
		}
		// No comma found outside nested angle brackets
		return -1;
	}

	/**
	 * Convert the parameter types exported to the api document
	 * @param javaTypeName java simple typeName
	 * @return String
	 */
	public static String processTypeNameForParams(String javaTypeName) {
		if (StringUtil.isEmpty(javaTypeName)) {
			return "object";
		}
		if (javaTypeName.length() == 1) {
			return "object";
		}
		if (javaTypeName.contains("[]")) {
			return "array";
		}
		javaTypeName = javaTypeName.toLowerCase();
		switch (javaTypeName) {
			case "java.lang.string":
			case "string":
			case "char":
			case "java.util.date":
			case "java.util.calendar":
			case "java.time.zoneoffset":
			case "java.time.offsettime":
			case "date":
			case "java.util.uuid":
			case "uuid":
			case "localdatetime":
			case "java.time.instant":
			case "java.time.localdatetime":
			case "java.time.localdate":
			case "java.time.localtime":
			case "java.time.year":
			case "java.time.yearmonth":
			case "java.time.monthday":
			case "java.time.period":
			case "localdate":
			case "offsetdatetime":
			case "localtime":
			case "timestamp":
			case "zoneddatetime":
			case "period":
			case "java.time.zoneddatetime":
			case "java.time.offsetdatetime":
			case "java.sql.timestamp":
			case "java.lang.character":
			case "character":
			case "org.bson.types.objectid":
				return "string";
			case "java.util.list":
			case "list":
			case "java.util.set":
			case "set":
			case "java.util.linkedlist":
			case "linkedlist":
			case "java.util.arraylist":
			case "arraylist":
			case "java.util.treeset":
			case "treeset":
			case "enumset":
				return "array";
			case "java.lang.byte":
			case "byte":
				return "int8";
			case "java.lang.integer":
			case "integer":
			case "int":
				return "int32";
			case "short":
			case "java.lang.short":
				return "int16";
			case "double":
			case "java.lang.double":
				return "double";
			case "java.lang.long":
			case "long":
				return "int64";
			case "java.lang.float":
			case "float":
				return "float";
			case "java.math.bigdecimal":
			case "bigdecimal":
			case "biginteger":
				return "number";
			case "java.lang.boolean":
			case "boolean":
				return "boolean";
			case "map":
				return "map";
			case "multipartfile":
				return "file";
			default:
				return "object";
		}

	}

	/**
	 * process return type
	 * @param fullyName fully name
	 * @return ApiReturn
	 */
	public static ApiReturn processReturnType(String fullyName) {
		ReturnTypeProcessor processor = new ReturnTypeProcessor();
		processor.setTypeName(fullyName);
		return processor.process();
	}

	/**
	 * rewrite request param
	 * @param typeName param type name
	 * @return String
	 */
	public static String rewriteRequestParam(String typeName) {
		if (typeName.equals("org.springframework.data.domain.Pageable")) {
			return "io.github.smartdoc.model.framework.PageableAsQueryParam";
		}
		return typeName;
	}

	/**
	 * Check if the given string is a valid class name.
	 * @param className The string to check.
	 * @return True if the string is a valid class name, false otherwise.
	 */
	private static boolean isClassName(String className) {
		className = className.replaceAll("[^<>]", "");
		Stack<Character> stack = new Stack<>();
		for (char c : className.toCharArray()) {
			if (c == '<') {
				stack.push('>');
			}
			else if (stack.isEmpty() || c != stack.pop()) {
				return false;
			}
		}
		return stack.isEmpty();
	}

	/**
	 * Get class annotations
	 * @param cls JavaClass
	 * @return List of JavaAnnotation
	 */
	public static List<JavaAnnotation> getAnnotations(JavaClass cls) {
		JavaClass superClass = cls.getSuperJavaClass();
		List<JavaAnnotation> classAnnotations = new ArrayList<>();
		try {
			if (Objects.nonNull(superClass)) {
				classAnnotations.addAll(superClass.getAnnotations());
			}
		}
		catch (Throwable e) {
			throw new RuntimeException(
					"Could not obtain annotations for class: " + cls.getFullyQualifiedName() + "\n" + e);
		}
		classAnnotations.addAll(cls.getAnnotations());
		return classAnnotations;
	}

	/**
	 * Create an instance of the given class using its default constructor.
	 * @param <T> The type of the class to create an instance of.
	 * @param classWithNoArgsConstructor The class to create an instance of.
	 * @return An instance of the given class.
	 */
	public static <T> T newInstance(Class<T> classWithNoArgsConstructor) {
		try {
			return classWithNoArgsConstructor.getConstructor().newInstance();
		}
		catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
			throw new IllegalArgumentException("Class must have the NoArgsConstructor");
		}
	}

}
