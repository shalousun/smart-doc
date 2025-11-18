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
package io.github.smartdoc.template;

import io.github.smartdoc.builder.ProjectDocConfigBuilder;
import io.github.smartdoc.helper.DocBuildHelper;
import io.github.smartdoc.model.DocMapping;
import io.github.smartdoc.model.WebSocketDoc;
import com.thoughtworks.qdox.model.JavaClass;

import java.util.Collection;
import java.util.List;

/**
 * websocket doc build template
 *
 * @author linwumingshi
 * @since 3.0.3
 */
public interface IWebSocketDocBuildTemplate<T extends WebSocketDoc> extends IDocBuildBaseTemplate {

	/**
	 * get websocket data by ProjectDocConfigBuilder
	 * @param projectBuilder ProjectDocConfigBuilder
	 * @return websocket data
	 */
	default List<T> getWebSocketData(ProjectDocConfigBuilder projectBuilder) {
		// For DocMapping initialization, when building multiple modules together, it is
		// necessary to initialize and clear the cache
		DocMapping.init();
		DocBuildHelper docBuildHelper = DocBuildHelper.create(projectBuilder);

		this.preRender(docBuildHelper);

		Collection<JavaClass> candidateClasses = this.getCandidateClasses(projectBuilder, docBuildHelper);

		return this.renderWebSocketApi(projectBuilder, candidateClasses);
	}

	/**
	 * render websocket api
	 * @param projectBuilder ProjectDocConfigBuilder
	 * @param candidateClasses candidate classes
	 * @return websocket data
	 */
	List<T> renderWebSocketApi(ProjectDocConfigBuilder projectBuilder, Collection<JavaClass> candidateClasses);

}
