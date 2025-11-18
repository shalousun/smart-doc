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
 * JAX-RS Annotations
 *
 * @author Zxq
 * @see JakartaJaxrsAnnotations
 * @deprecated Java EE has been renamed to Jakarta EE, an upgrade is recommended.
 */
@Deprecated
public final class JAXRSAnnotations {

	/**
	 * JAX-RS@DefaultValue
	 */
	public static final String JAX_DEFAULT_VALUE_FULLY = "javax.ws.rs.DefaultValue";

	/**
	 * JAX-RS@HeaderParam
	 */
	public static final String JAX_HEADER_PARAM_FULLY = "javax.ws.rs.HeaderParam";

	/**
	 * JAX-RS@PathParam
	 */
	public static final String JAX_PATH_PARAM_FULLY = "javax.ws.rs.PathParam";

	/**
	 * JAX-RS@PATH
	 */
	public static final String JAX_PATH_FULLY = "javax.ws.rs.Path";

	/**
	 * JAX-RS@Produces
	 */
	public static final String JAX_PRODUCES_FULLY = "javax.ws.rs.Produces";

	/**
	 * JAX-RS@Consumes
	 */
	public static final String JAX_CONSUMES_FULLY = "javax.ws.rs.Consumes";

	/**
	 * JAX-RS@GET
	 */
	public static final String JAX_GET_FULLY = "javax.ws.rs.GET";

	/**
	 * JAX-RS@POST
	 */
	public static final String JAX_POST_FULLY = "javax.ws.rs.POST";

	/**
	 * JAX-RS@PUT
	 */
	public static final String JAX_PUT_FULLY = "javax.ws.rs.PUT";

	/**
	 * JAX-RS@DELETE
	 */
	public static final String JAXB_DELETE_FULLY = "javax.ws.rs.DELETE";

	/**
	 * JAX-RS@PATCH
	 */
	public static final String JAXB_PATCH_FULLY = "javax.ws.rs.PATCH";

	/**
	 * JAX-RS@HEAD
	 */
	public static final String JAXB_HEAD_FULLY = "javax.ws.rs.HEAD";

	/**
	 * private constructor
	 */
	private JAXRSAnnotations() {
		throw new IllegalStateException("Utility class");
	}

}