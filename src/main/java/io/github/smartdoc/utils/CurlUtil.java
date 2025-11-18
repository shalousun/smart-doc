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

import io.github.smartdoc.constants.MediaType;
import io.github.smartdoc.model.ApiReqParam;
import io.github.smartdoc.model.FormData;
import io.github.smartdoc.model.request.CurlRequest;
import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;

import java.util.List;
import java.util.Objects;

/**
 * curl util
 *
 * @author yu 2020/12/21.
 */
public class CurlUtil {

	/**
	 * private constructor
	 */
	private CurlUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * convert curl request to curl string
	 * @param request CurlRequest
	 * @return String
	 */
	public static String toCurl(CurlRequest request) {
		if (Objects.isNull(request)) {
			return "";
		}
		String methodType = request.getType();
		if (Objects.nonNull(methodType) && methodType.contains(".")) {
			methodType = methodType.substring(methodType.indexOf(".") + 1);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("curl");
		sb.append(" -X");
		sb.append(" ").append(methodType);
		if (request.getUrl().indexOf("https") == 0) {
			sb.append(" -k");
		}
		if (StringUtil.isNotEmpty(request.getContentType())
				&& !MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(request.getContentType())) {
			sb.append(" -H");
			sb.append(" \"Content-Type: ").append(request.getContentType()).append("\"");
		}
		if (CollectionUtil.isNotEmpty(request.getReqHeaders())) {
			for (ApiReqParam reqHeader : request.getReqHeaders()) {
				sb.append(" -H");
				if (StringUtil.isEmpty(reqHeader.getValue())) {
					sb.append(" \"").append(reqHeader.getName()).append("\"");
				}
				else {
					sb.append(" \"").append(reqHeader.getName()).append(':').append(reqHeader.getValue()).append("\"");
				}
			}
		}

		List<FormData> fileFormDataList = request.getFileFormDataList();
		if (CollectionUtil.isNotEmpty(fileFormDataList)) {
			fileFormDataList.forEach(file -> {
				sb.append(" -F '").append(file.getKey()).append("=");
				// eg: -F 'user="{\"stuName\":\"test\"}";type=application/json'
				if (StringUtil.isNotEmpty(file.getValue())) {
					sb.append(file.getValue());
					if (StringUtil.isNotEmpty(file.getContentType())) {
						sb.append(";type=").append(file.getContentType()).append("'");
					}
					else {
						sb.append("'");
					}
				}
				else {
					sb.append("'");
				}
			});
		}

		sb.append(" -i");
		// append request url
		sb.append(" ").append("'").append(request.getUrl()).append("'");
		if (StringUtil.isNotEmpty(request.getBody())) {
			sb.append(" --data");
			String data = request.getBody().replaceAll("'", "'\\\\''");
			sb.append(" '").append(data).append("'");
		}
		return sb.toString();
	}

}
