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

package io.github.smartdoc.constants;

import java.util.Collections;
import java.util.Map;

/**
 * Provides default attribute values for validation annotations.
 * <p>
 * Implementations of this interface should return immutable maps containing the default
 * values for validation annotation attributes. These defaults are used for placeholder
 * resolution in validation messages (e.g., replacing {min} with the actual default value
 * from the annotation).
 */
public interface ValidationAnnotationDefaultsProvider {

	/**
	 * Gets the default attribute values for the associated validation annotation.
	 * <p>
	 * The returned map should be immutable and contain only primitive/wrapper values
	 * converted to their String representations. Implementations should return an empty
	 * map if no defaults are available.
	 * @return Unmodifiable map of default attribute values, or empty map if none exist
	 */
	default Map<String, String> getDefaultProperties() {
		return Collections.emptyMap();
	}

	/**
	 * Checks if the annotation has any default property values defined.
	 * @return true if default properties are available, false otherwise
	 */
	default boolean hasDefaults() {
		return !this.getDefaultProperties().isEmpty();
	}

}