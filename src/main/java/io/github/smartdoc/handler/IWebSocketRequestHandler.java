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
package io.github.smartdoc.handler;

import io.github.smartdoc.builder.ProjectDocConfigBuilder;
import io.github.smartdoc.constants.DocAnnotationConstants;
import io.github.smartdoc.constants.DocTags;
import io.github.smartdoc.model.request.ServerEndpoint;
import io.github.smartdoc.utils.JavaClassUtil;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * websocket handler
 *
 * @author linwumingshi
 */
public interface IWebSocketRequestHandler {

	/**
	 * handle class annotation `@ServerEndpoint`
	 * @param projectBuilder the project configuration builder
	 * @param javaAnnotation javaAnnotation @ServerEndpoint
	 * @param cls JavaClass
	 * @return ServerEndpoint
	 */
	default ServerEndpoint handleServerEndpoint(ProjectDocConfigBuilder projectBuilder, JavaClass cls,
			JavaAnnotation javaAnnotation) {
		if (Objects.nonNull(cls.getTagByName(DocTags.IGNORE))) {
			return null;
		}
		ServerEndpoint builder = ServerEndpoint.builder();
		// get the value of JavaAnnotation
		Optional.ofNullable(javaAnnotation.getProperty(DocAnnotationConstants.VALUE_PROP))
			.map(Object::toString)
			.map(StringUtil::removeQuotes)
			.ifPresent(builder::setUrl);

		// get subProtocols of annotation
		List<String> subProtocols = JavaClassUtil.getAnnotationValueStrings(projectBuilder, javaAnnotation,
				"subprotocols");
		builder.setSubProtocols(subProtocols);

		// Handle 'decoders' property
		builder.setDecoders(JavaClassUtil.getAnnotationValueClassNames(javaAnnotation, "decoders"));

		// Handle 'encoders' property
		builder.setEncoders(JavaClassUtil.getAnnotationValueClassNames(javaAnnotation, "encoders"));
		return builder;
	}

}
