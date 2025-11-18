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
 * Java EE has been renamed Jakarta EE, this class is an upgraded replacement for
 * {@link JAXRSAnnotations}
 * <p>
 * JAX-RS Annotations
 *
 * @author youngledo
 */
public final class JakartaJaxrsAnnotations {

	/**
	 * JAX-RS @DefaultValue
	 */
	public static final String JAX_DEFAULT_VALUE_FULLY = "jakarta.ws.rs.DefaultValue";

	/**
	 * JAX-RS @DefaultValue
	 */
	public static final String JAX_DEFAULT_VALUE = "DefaultValue";

	/**
	 * JAX-RS @HeaderParam
	 */
	public static final String JAX_HEADER_PARAM_FULLY = "jakarta.ws.rs.HeaderParam";

	/**
	 * JAX-RS @HeaderParam
	 */
	public static final String JAX_HEADER_PARAM = "HeaderParam";

	/**
	 * JAX-RS @PathParam
	 */
	public static final String JAX_PATH_PARAM_FULLY = "jakarta.ws.rs.PathParam";

	/**
	 * JAX-RS @PathParam
	 */
	public static final String JAX_PATH_PARAM = "PathParam";

	/**
	 * JAX-RS @Path
	 */
	public static final String JAX_PATH_FULLY = "jakarta.ws.rs.Path";

	/**
	 * JAX-RS @Path
	 */
	public static final String JAX_PATH = "Path";

	/**
	 * JAX-RS @Produces
	 */
	public static final String JAX_PRODUCES_FULLY = "jakarta.ws.rs.Produces";

	/**
	 * JAX-RS @Produces
	 */
	public static final String JAX_PRODUCES = "Produces";

	/**
	 * JAX-RS @Consumes
	 */
	public static final String JAX_CONSUMES_FULLY = "jakarta.ws.rs.Consumes";

	/**
	 * JAX-RS @Consumes
	 */
	public static final String JAX_CONSUMES = "Consumes";

	/**
	 * JAX-RS@GET
	 */
	public static final String GET = "GET";

	/**
	 * JAX-RS@POST
	 */
	public static final String POST = "POST";

	/**
	 * JAX-RS@PUT
	 */
	public static final String PUT = "PUT";

	/**
	 * JAX-RS@DELETE
	 */
	public static final String DELETE = "DELETE";

	/**
	 * JAX-RS@GET
	 */
	public static final String JAX_GET_FULLY = "jakarta.ws.rs.GET";

	/**
	 * JAX-RS@POST
	 */
	public static final String JAX_POST_FULLY = "jakarta.ws.rs.POST";

	/**
	 * JAX-RS@PUT
	 */
	public static final String JAX_PUT_FULLY = "jakarta.ws.rs.PUT";

	/**
	 * JAX-RS@DELETE
	 */
	public static final String JAXB_DELETE_FULLY = "jakarta.ws.rs.DELETE";

	/**
	 * JAX-RS@RestPath
	 */
	public static final String JAXB_REST_PATH_FULLY = "org.jboss.resteasy.reactive.RestPath";

	/**
	 * JAX-RS@PATCH
	 */
	public static final String JAX_PATCH_FULLY = "jakarta.ws.rs.PATCH";

	/**
	 * JAX-RS@HEAD
	 */
	public static final String JAX_HEAD_FULLY = "jakarta.ws.rs.HEAD";

	/**
	 * private constructor
	 */
	private JakartaJaxrsAnnotations() {
		throw new IllegalStateException("Utility class");
	}

}