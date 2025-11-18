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

import io.github.smartdoc.constants.ComponentTypeEnum;
import io.github.smartdoc.constants.DocGlobalConstants;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.ApiParam;
import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.smartdoc.constants.TornaConstants.GSON;

/**
 * OpenApi Schema Util
 *
 * @author yu 2020/11/29.
 */
public class OpenApiSchemaUtil {

	/**
	 * private constructor
	 */
	private OpenApiSchemaUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * No body param
	 */
	public static final String NO_BODY_PARAM = "NO_BODY_PARAM";

	/**
	 * Pattern
	 */
	static final Pattern PATTERN = Pattern.compile("[A-Z]\\w+.*?|[A-Z]");

	/**
	 * Primary type schema
	 * @param primaryType primary type
	 * @return Map
	 */
	public static Map<String, Object> primaryTypeSchema(String primaryType) {
		Map<String, Object> map = new HashMap<>(16);
		map.put("type", DocUtil.javaTypeToOpenApiTypeConvert(primaryType));
		return map;
	}

	/**
	 * Map type schema
	 * @param primaryType primary type
	 * @return Map
	 */
	public static Map<String, Object> mapTypeSchema(String primaryType) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("type", "object");
		Map<String, Object> items = primaryTypeSchema(primaryType);
		map.put("additionalProperties", items);
		return map;
	}

	/**
	 * Array type schema
	 * @param primaryType primary type
	 * @return Map
	 */
	public static Map<String, Object> arrayTypeSchema(String primaryType) {
		Map<String, Object> map = new HashMap<>(16);
		map.put("type", "array");
		Map<String, Object> items = primaryTypeSchema(primaryType);
		map.put("items", items);
		return map;
	}

	/**
	 * Get className from params
	 * @param apiParams api params
	 * @return className
	 */
	public static String getClassNameFromParams(List<ApiParam> apiParams) {
		ComponentTypeEnum componentTypeEnum = ApiConfig.getInstance().getComponentType();
		// random name
		if (componentTypeEnum.equals(ComponentTypeEnum.RANDOM)) {
			return DigestUtils.md5Hex(GSON.toJson(apiParams));
		}
		// if array[Primitive] or Primitive
		if (CollectionUtil.isNotEmpty(apiParams) && apiParams.size() == 1
				&& StringUtil.isEmpty(apiParams.get(0).getClassName())
				&& CollectionUtil.isEmpty(apiParams.get(0).getChildren())) {
			return DocGlobalConstants.DEFAULT_PRIMITIVE;
		}
		// className
		for (ApiParam a : apiParams) {
			if (StringUtil.isNotEmpty(a.getClassName())) {
				return OpenApiSchemaUtil.delClassName(a.getClassName());
			}
		}
		return NO_BODY_PARAM;
	}

	/**
	 * Delete className
	 * @param className className
	 * @return className
	 */
	public static String delClassName(String className) {
		return String.join("", getPatternResult(PATTERN, className));
	}

	/**
	 * Get pattern result
	 * @param p pattern
	 * @param content content
	 * @return result
	 */
	public static List<String> getPatternResult(Pattern p, String content) {
		List<String> matchers = new ArrayList<>();
		Matcher m = p.matcher(content);
		while (m.find()) {
			matchers.add(m.group());
		}
		return matchers;
	}

	/**
	 * Get pattern result
	 * @param rex regex
	 * @param content content
	 * @return result
	 */
	public static List<String> getPatternResult(String rex, String content) {
		Pattern p = Pattern.compile(rex);
		return getPatternResult(p, content);
	}

}
