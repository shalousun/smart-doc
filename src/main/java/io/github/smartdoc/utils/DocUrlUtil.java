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

import io.github.smartdoc.constants.DocGlobalConstants;
import com.power.common.util.StringUtil;
import com.power.common.util.UrlUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Utility class for handling URL operations related to documentation.
 * <p>
 * This class provides methods to construct and format URLs. It is not intended to be
 * instantiated.
 *
 * @author yu 2019/12/22.
 */
public class DocUrlUtil {

	/**
	 * private constructor
	 */
	private DocUrlUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Constructs a list of formatted URLs based on the provided base server, base URL,
	 * and a list of URLs.
	 * @param baseServer The base server URL.
	 * @param baseUrl The base URL segment.
	 * @param urls A list of URL segments to append.
	 * @return A concatenated string of simplified URLs separated by a predefined
	 * separator.
	 */
	public static String getMvcUrls(String baseServer, String baseUrl, List<String> urls) {
		StringBuilder sb = new StringBuilder();
		List<String> baseUrls = DocUtil.split(baseUrl);
		int size = urls.size();
		int baseSize = baseUrls.size();
		for (int j = 0; j < baseSize; j++) {
			String base = baseUrls.get(j);
			String trimBase = Optional.ofNullable(StringUtil.trimBlank(base)).orElse(StringUtil.EMPTY);
			trimBase = trimBase.replace("[", "").replace("]", "");
			for (int i = 0; i < size; i++) {
				String trimUrl = Optional.ofNullable(StringUtil.trimBlank(urls.get(i))).orElse(StringUtil.EMPTY);
				String url = baseServer;
				if (StringUtil.isNotEmpty(trimBase)) {
					url = url + DocGlobalConstants.PATH_DELIMITER + trimBase;
				}
				if (StringUtil.isNotEmpty(trimUrl)) {
					url = url + DocGlobalConstants.PATH_DELIMITER + trimUrl;
				}
				sb.append(UrlUtil.simplifyUrl(url));
				if (i < size - 1) {
					sb.append(DocGlobalConstants.MULTI_URL_SEPARATOR);
				}
			}
			if (j < baseSize - 1) {
				sb.append(DocGlobalConstants.MULTI_URL_SEPARATOR);
			}
		}

		return sb.toString();
	}

	/**
	 * Convenience method to construct a formatted URL based on the provided base server,
	 * base URL, and a short URL.
	 * @param baseServer The base server URL.
	 * @param baseUrl The base URL segment.
	 * @param shortUrl A short URL to split and process.
	 * @return A formatted URL.
	 */
	public static String getMvcUrls(String baseServer, String baseUrl, String shortUrl) {
		List<String> urls = DocUtil.split(shortUrl);
		return getMvcUrls(baseServer, baseUrl, urls);
	}

	/**
	 * Formats a request URL with path parameters and query parameters.
	 * @param pathParamsMap A map of path parameters.
	 * @param queryParamsMap A map of query parameters.
	 * @param serverUrl The server URL.
	 * @param path The path to be formatted.
	 * @return A fully constructed and simplified request URL.
	 */
	public static String formatRequestUrl(Map<String, String> pathParamsMap, Map<String, String> queryParamsMap,
			String serverUrl, String path) {
		path = DocUtil.formatAndRemove(path, pathParamsMap);
		String url = UrlUtil.urlJoin(path, queryParamsMap);
		url = StringUtil.removeQuotes(url);
		url = serverUrl + DocGlobalConstants.PATH_DELIMITER + url;
		url = UrlUtil.simplifyUrl(url);
		return url;
	}

}
