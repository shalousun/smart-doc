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
package io.github.smartdoc.constants;

/**
 * JSR303 Annotation Constants
 *
 * @author yu 2024/6/7
 */
public interface JSRAnnotationConstants {

	// begin from javax.validation:validation-api
	/**
	 * Spring validator annotations `@AssertFalse`
	 */
	String ASSERT_FALSE = "AssertFalse";

	/**
	 * Spring validator annotations `@AssertTrue`
	 */
	String ASSERT_TRUE = "AssertTrue";

	/**
	 * Spring validator annotations `@DecimalMax`
	 */
	String DECIMAL_MAX = "DecimalMax";

	/**
	 * Spring validator annotations `@DecimalMin`
	 */
	String DECIMAL_MIN = "DecimalMin";

	/**
	 * Spring validator annotations `@Digits`
	 */
	String DIGITS = "Digits";

	/**
	 * Spring validator annotations `@Email`
	 */
	String EMAIL = "Email";

	/**
	 * Spring validator annotations `@Future`
	 */
	String FUTURE = "Future";

	/**
	 * Spring validator annotations `@FutureOrPresent`
	 */
	String FUTURE_OR_PRESENT = "FutureOrPresent";

	/**
	 * Spring validator annotations `@Max`
	 */
	String MAX = "Max";

	/**
	 * Spring validator annotations `@Min`
	 */
	String MIN = "Min";

	/**
	 * Spring validator annotations `@Negative`
	 */
	String NEGATIVE = "Negative";

	/**
	 * Spring validator annotations `@NegativeOrZero`
	 */
	String NEGATIVE_OR_ZERO = "NegativeOrZero";

	/**
	 * Spring validator annotations `@NotBlank`
	 */
	String NOT_BLANK = "NotBlank";

	/**
	 * Spring validator annotations `@NotEmpty`
	 */
	String NOT_EMPTY = "NotEmpty";

	/**
	 * Spring validator annotations `@NotNull`
	 */
	String NOT_NULL = "NotNull";

	/**
	 * Spring validator annotations `@Null`
	 */
	String NULL = "Null";

	/**
	 * Spring validator annotations `@Past`
	 */
	String PAST = "Past";

	/**
	 * Spring validator annotations `@PastOrPresent`
	 */
	String PAST_OR_PRESENT = "PastOrPresent";

	/**
	 * Spring validator annotations `@Pattern`
	 */
	String PATTERN = "Pattern";

	/**
	 * Spring validator annotations `@Positive`
	 */
	String POSITIVE = "Positive";

	/**
	 * Spring validator annotations `@PositiveOrZero`
	 */
	String POSITIVE_OR_ZERO = "PositiveOrZero";

	/**
	 * Spring validator annotations `@Size`
	 */
	String SIZE = "Size";

	// end from javax.validation:validation-api

	// begin from org.hibernate.validator:hibernate-validator
	/**
	 * Spring validator annotations `@Length`
	 */
	String LENGTH = "Length";

	/**
	 * Spring validator annotations `@Range`
	 */
	String RANGE = "Range";

	// end from org.hibernate.validator:hibernate-validator

	/**
	 * java annotations `@Valid` jakarta.validation.Valid javax.validation.Valid
	 */
	String VALID = "Valid";

	/**
	 * Spring validator annotations `@Validated`
	 */
	String VALIDATED = "Validated";

}
