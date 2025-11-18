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

package io.github.smartdoc.constants;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Enumeration of Spring validator annotations with metadata for documentation generation.
 * <p>
 * This enum maps validation annotations to their default attribute values, enabling
 * automatic replacement of placeholders in validation messages (e.g., {min}, {max}).
 *
 * @author yu 2019/9/19.
 */
public enum DocValidatorAnnotationEnum implements ValidationAnnotationDefaultsProvider {

	/**
	 * Spring validator annotations `@NotEmpty`
	 */
	NOT_EMPTY(JSRAnnotationConstants.NOT_EMPTY),

	/**
	 * Spring validator annotations `@NotBlank`
	 */
	NOT_BLANK(JSRAnnotationConstants.NOT_BLANK),

	/**
	 * Spring validator annotations `@NotNull`
	 */
	NOT_NULL(JSRAnnotationConstants.NOT_NULL),

	/**
	 * Spring validator annotations `@Null`
	 */
	NULL(JSRAnnotationConstants.NULL),

	/**
	 * Spring validator annotations `@AssertTrue`
	 */
	ASSERT_TRUE(JSRAnnotationConstants.ASSERT_TRUE),

	/**
	 * Spring validator annotations `@AssertFalse`
	 */
	ASSERT_FALSE(JSRAnnotationConstants.ASSERT_FALSE),

	/**
	 * Spring validator annotations `@Min`
	 */
	MIN(JSRAnnotationConstants.MIN),

	/**
	 * Spring validator annotations `@Max`
	 */
	MAX(JSRAnnotationConstants.MAX),

	/**
	 * Spring validator annotations `@DecimalMin`
	 */
	DECIMAL_MIN(JSRAnnotationConstants.DECIMAL_MIN),

	/**
	 * Spring validator annotations `@DecimalMax`
	 */
	DECIMAL_MAX(JSRAnnotationConstants.DECIMAL_MAX),

	/**
	 * Spring validator annotations `@Size`
	 */
	SIZE(JSRAnnotationConstants.SIZE) {
		public Map<String, String> getDefaultProperties() {
			// @Size default values (min=0, max=Integer.MAX_VALUE)
			Map<String, String> sizeDefaults = new HashMap<>(2);
			sizeDefaults.put("min", "0");
			sizeDefaults.put("max", Integer.toString(Integer.MAX_VALUE));
			return sizeDefaults;
		}
	},

	/**
	 * Spring validator annotations `@Digits`
	 */
	DIGITS(JSRAnnotationConstants.DIGITS),

	/**
	 * Spring validator annotations `@Past`
	 */
	PAST(JSRAnnotationConstants.PAST),

	/**
	 * Spring validator annotations `@PastOrPresent`
	 */
	PAST_OR_PRESENT(JSRAnnotationConstants.PAST_OR_PRESENT),

	/**
	 * Spring validator annotations `@Future`
	 */
	FUTURE(JSRAnnotationConstants.FUTURE),

	/**
	 * Spring validator annotations `@FutureOrPresent`
	 */
	FUTURE_OR_PRESENT(JSRAnnotationConstants.FUTURE_OR_PRESENT),

	/**
	 * Spring validator annotations `@Pattern`
	 */
	PATTERN(JSRAnnotationConstants.PATTERN),

	/**
	 * Spring validator annotations `@Positive`
	 */
	POSITIVE(JSRAnnotationConstants.POSITIVE),

	/**
	 * Spring validator annotations `@PositiveOrZero`
	 */
	POSITIVE_OR_ZERO(JSRAnnotationConstants.POSITIVE_OR_ZERO),

	/**
	 * Spring validator annotations `@Negative`
	 */
	NEGATIVE(JSRAnnotationConstants.NEGATIVE),

	/**
	 * Spring validator annotations `@NegativeOrZero`
	 */
	NEGATIVE_OR_ZERO(JSRAnnotationConstants.NEGATIVE_OR_ZERO),

	/**
	 * Spring validator annotations `@Email`
	 */
	EMAIL(JSRAnnotationConstants.EMAIL),

	/**
	 * Spring validator annotations `@Length`
	 */
	LENGTH(JSRAnnotationConstants.LENGTH) {
		public Map<String, String> getDefaultProperties() {
			// @Length default values (min=0, max=Integer.MAX_VALUE)
			Map<String, String> lengthDefaults = new HashMap<>(2);
			lengthDefaults.put("min", "0");
			lengthDefaults.put("max", Integer.toString(Integer.MAX_VALUE));
			return lengthDefaults;
		}
	},

	/**
	 * Spring validator annotations `@Range`
	 */
	RANGE(JSRAnnotationConstants.RANGE) {
		@Override
		public Map<String, String> getDefaultProperties() {
			// @Range default values (min=0, max=Long.MAX_VALUE)
			Map<String, String> rangeDefaults = new HashMap<>(2);
			rangeDefaults.put("min", "0");
			rangeDefaults.put("max", Long.toString(Long.MAX_VALUE));
			return rangeDefaults;
		}
	},

	/**
	 * Spring validator annotations `@Validated`
	 */
	VALIDATED(JSRAnnotationConstants.VALIDATED),;

	/**
	 * annotation value
	 */
	private final String value;

	DocValidatorAnnotationEnum(String value) {
		this.value = value;
	}

	/**
	 * validator annotations
	 */
	public static final Set<String> VALIDATOR_ANNOTATIONS = new HashSet<>();

	/**
	 * excluded annotations
	 */
	public static final Set<String> EXCLUDED_ANNOTATIONS = new HashSet<>();

	static {
		for (DocValidatorAnnotationEnum annotation : DocValidatorAnnotationEnum.values()) {
			VALIDATOR_ANNOTATIONS.add(annotation.value);
		}

		EXCLUDED_ANNOTATIONS.add(NOT_BLANK.value);
		EXCLUDED_ANNOTATIONS.add(NOT_EMPTY.value);
		EXCLUDED_ANNOTATIONS.add(NOT_NULL.value);
		EXCLUDED_ANNOTATIONS.add(NULL.value);
		EXCLUDED_ANNOTATIONS.add(VALIDATED.value);
	}

	/**
	 * Looks up and returns the default attribute values for a validation annotation.
	 * <p>
	 * This method iterates through all {@link DocValidatorAnnotationEnum} entries to find
	 * the matching annotation definition. If found, returns the associated default values
	 * from {@link ValidationAnnotationDefaultsProvider#getDefaultProperties()}.
	 * @param annotationName Fully qualified class name of the annotation (e.g.,
	 * "javax.validation.constraints.Min")
	 * @return An unmodifiable map of default attribute name-value pairs. Returns an empty
	 * map if:
	 * <ul>
	 * <li>No matching annotation is found</li>
	 * <li>The annotation does not define default values</li>
	 * </ul>
	 * @see DocValidatorAnnotationEnum
	 * @see ValidationAnnotationDefaultsProvider
	 */
	public static Map<String, String> getDefaults(String annotationName) {
		for (DocValidatorAnnotationEnum entry : values()) {
			if (entry.value.equals(annotationName)) {
				return entry.getDefaultProperties();
			}
		}
		return Collections.emptyMap();
	}

}
