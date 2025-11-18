/*
 * smart-doc
 *
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

import io.github.smartdoc.builder.ProjectDocConfigBuilder;
import io.github.smartdoc.constants.DocTags;
import io.github.smartdoc.constants.ParamTypeConstants;
import io.github.smartdoc.model.ApiParam;
import io.github.smartdoc.model.torna.EnumInfoAndValues;
import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * ParamUtil
 *
 * @author <a href="mailto:cqmike0315@gmail.com">chenqi</a>
 * @version 1.0
 */
public class ParamUtil {

	/**
	 * private constructor
	 */
	private ParamUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Handles enum types in API parameters.
	 * <p>
	 * This method is primarily used to process enum types, setting the corresponding
	 * parameter type, value, and enumeration information in the ApiParam object. If the
	 * enum has a @JsonFormat annotation with a shape attribute value of NUMBER, the value
	 * is processed accordingly. Additionally, it supports setting mock values for
	 * parameters through tag mappings.
	 * @param param The ApiParam object, used to store parameter information.
	 * @param javaField The JavaField object, representing the field of a class, used to
	 * obtain the type and other information of the field.
	 * @param builder The ProjectDocConfigBuilder object, used to obtain configuration
	 * information for generating documentation.
	 * @param jsonRequest A boolean value indicating whether the request is in JSON
	 * format, used to determine how to obtain the enum value.
	 * @param tagsMap A map containing tag names and their descriptions, used to override
	 * parameter values with mock data.
	 * @param jsonFormatValue The value of the @JsonFormat annotation's shape attribute,
	 * used to handle special cases of numeric enums.
	 * @return Returns the processed JavaClass object representing the enum, or null if
	 * the input field is not an enum.
	 */
	public static JavaClass handleSeeEnum(ApiParam param, JavaField javaField, ProjectDocConfigBuilder builder,
			boolean jsonRequest, Map<String, String> tagsMap, String jsonFormatValue) {
		JavaClass seeEnum = JavaClassUtil.getSeeEnum(javaField, builder);
		if (Objects.isNull(seeEnum)) {
			return null;
		}
		// when enum is same class, set type to enum
		if (Objects.equals(seeEnum.getGenericFullyQualifiedName(),
				javaField.getType().getGenericFullyQualifiedName())) {
			param.setType(ParamTypeConstants.PARAM_TYPE_ENUM);
		}
		EnumInfoAndValues enumInfoAndValue = JavaClassUtil.getEnumInfoAndValue(seeEnum, builder, jsonRequest);
		if (Objects.nonNull(enumInfoAndValue)) {
			param.setValue(StringUtil.removeDoubleQuotes(String.valueOf(enumInfoAndValue.getValue())))
				.setEnumInfoAndValues(enumInfoAndValue)
				.setType(enumInfoAndValue.getType());
		}
		// If the @JsonFormat annotation's shape attribute value is specified,
		// use it as the parameter value
		if (StringUtil.isNotEmpty(jsonFormatValue)) {
			param.setValue(jsonFormatValue);
			param.setEnumValues(IntStream.rangeClosed(0, param.getEnumValues().size() - 1)
				.mapToObj(Integer::toString)
				.collect(Collectors.toList()));
		}
		// If the tagsMap contains a mock tag and the value is not empty
		// use the value of the mock tag as the parameter value
		// Override old value
		if (tagsMap.containsKey(DocTags.MOCK) && StringUtil.isNotEmpty(tagsMap.get(DocTags.MOCK))) {
			param.setValue(tagsMap.get(DocTags.MOCK));
		}
		return seeEnum;
	}

	/**
	 * Format mock value
	 * @param mock mock value
	 * @return formatted mock value
	 */
	public static String formatMockValue(String mock) {
		if (StringUtil.isEmpty(mock)) {
			return mock;
		}
		return mock.replaceAll("\\\\", "");
	}

	/**
	 * Extract qualified name from param list
	 * @param paramList param list
	 * @return qualified name list
	 */
	public static List<String> extractQualifiedName(List<ApiParam> paramList) {
		if (CollectionUtil.isEmpty(paramList)) {
			return Collections.emptyList();
		}

		Set<String> set = new HashSet<>();
		for (ApiParam param : paramList) {
			String className = param.getClassName();

			if (StringUtil.isEmpty(className)) {
				continue;
			}

			int index = className.indexOf("<");
			if (index > -1) {
				className = className.substring(0, index);
			}

			set.add(className);
		}

		return new ArrayList<>(set);
	}

}
