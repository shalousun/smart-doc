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
 * Solon Annotations
 *
 * @author noear 2022/2/19 created
 */
public interface SolonAnnotations {

	String REQUEST_MAPPING = "Mapping";

	String REQUEST_MAPPING_FULLY = "org.noear.solon.annotation.Mapping";

	String GET_MAPPING = "Get";

	String GET_MAPPING_FULLY = "org.noear.solon.annotation.Get";

	String POST_MAPPING = "Post";

	String POST_MAPPING_FULLY = "org.noear.solon.annotation.Post";

	String PUT_MAPPING = "Put";

	String PUT_MAPPING_FULLY = "org.noear.solon.annotation.Put";

	String PATCH_MAPPING = "Patch";

	String PATCH_MAPPING_FULLY = "org.noear.solon.annotation.Patch";

	String DELETE_MAPPING = "Delete";

	String DELETE_MAPPING_FULLY = "org.noear.solon.annotation.Delete";

	String REQUEST_PARAM = "Path";

	String REQUEST_PARAM_FULL = "org.noear.solon.annotation.Path";

	String PATH_VAR = "PathVar";

	String PATH_VAR_FULL = "org.noear.solon.annotation.PathVar";

	String REQUEST_HERDER = "Header";

	String REQUEST_HERDER_FULL = "org.noear.solon.annotation.Header";

	String REQUEST_BODY = "Body";

	String REQUEST_BODY_FULLY = "org.noear.solon.annotation.Body";

	String CONTROLLER = "Controller";

	String CONTROLLER_FULL = "org.noear.solon.annotation.Controller";

	String COMPONENT = "Component";

	String COMPONENT_FULL = "org.noear.solon.annotation.Component";

	String REMOTING = "Remoting";

	String REMOTING_FULL = "org.noear.solon.annotation.Remoting";

	String MODE_AND_VIEW_FULLY = "org.noear.solon.core.handle.ModelAndView";

}
