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

package io.github.smartdoc.model;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Doc tag mapping
 *
 * @author CKM Relational Mapping 2023/03/20 10:13:00
 */
public class DocMapping {

	/**
	 * key:tag value:ApiDoc
	 */
	public static Map<String, TagDoc> TAG_DOC = new ConcurrentHashMap<>(64);

	public static void tagDocPut(String tag, ApiDoc apiDoc, ApiMethodDoc methodDoc) {
		if (StringUtils.isBlank(tag)) {
			return;
		}
		TagDoc tagDoc = TAG_DOC.computeIfAbsent(tag, TagDoc::new);
		if (Objects.nonNull(apiDoc)) {
			apiDoc.getTagRefs().add(tagDoc);
			tagDoc.getClazzDocs().add(apiDoc);
		}
		if (Objects.nonNull(methodDoc)) {
			methodDoc.getTagRefs().add(tagDoc);
			tagDoc.getMethodDocs().add(methodDoc);
		}
	}

	public static void init() {
		TAG_DOC = new ConcurrentHashMap<>(64);
	}

}
