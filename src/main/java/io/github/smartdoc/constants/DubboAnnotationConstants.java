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
 * dubbo annotation
 *
 * @author yu 2020/1/29.
 */
public interface DubboAnnotationConstants {

	/**
	 * dubbo service annotation {@code org.apache.dubbo.config.annotation.Service}
	 */
	String SERVICE = "org.apache.dubbo.config.annotation.Service";

	/**
	 * dubbo service {@code org.apache.dubbo.config.annotation.DubboService}
	 * @since dubbo 2.7.7
	 */
	String DUBBO_SERVICE = "org.apache.dubbo.config.annotation.DubboService";

	/**
	 * support ali dubbo {@code com.alibaba.dubbo.config.annotation.Service}
	 */
	String ALI_DUBBO_SERVICE = "com.alibaba.dubbo.config.annotation.Service";

	/**
	 * dubbo swagger.
	 * {@code org.apache.dubbo.rpc.protocol.rest.integration.swagger.DubboSwaggerApiListingResource}
	 */
	String DUBBO_SWAGGER = "org.apache.dubbo.rpc.protocol.rest.integration.swagger.DubboSwaggerApiListingResource";

}
