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
package io.github.smartdoc.extension.dict;

import com.power.common.model.EnumDictionary;

import java.util.Collection;
import java.util.Collections;

/**
 * dictionary values resolver
 *
 * @author 吴垚
 * @see <a href="https://github.com/smart-doc-group/smart-doc/issues/338">issues-338</a>
 */
public interface DictionaryValuesResolver {

	/**
	 * resolve the dictionary, if an exception occurs, return empty collection instead of
	 * null default behaviour is the same as {@link #resolve()}
	 * @param clazz dictionary class
	 * @param <T> the type parameter
	 * @return the dictionary
	 */
	default <T extends EnumDictionary> Collection<T> resolve(Class<?> clazz) {
		return resolve();
	}

	/**
	 * resolve the dictionary, for compatibility, do not return null
	 * @param <T> the type parameter
	 * @return the dictionary
	 * @see #resolve(Class)
	 */
	default <T extends EnumDictionary> Collection<T> resolve() {
		return Collections.emptyList();
	}

}
