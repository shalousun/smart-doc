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
import io.github.smartdoc.constants.DocTags;
import io.github.smartdoc.constants.FrameworkEnum;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.ApiSchema;
import io.github.smartdoc.model.JavadocJavaMethod;
import io.github.smartdoc.model.WebSocketDoc;
import io.github.smartdoc.model.annotation.FrameworkAnnotations;
import io.github.smartdoc.model.javadoc.JavadocApiDoc;
import io.github.smartdoc.utils.DocUtil;
import io.github.smartdoc.utils.JavaClassUtil;
import com.power.common.util.StringUtil;
import com.power.common.util.ValidateUtil;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaType;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * javadoc doc build template.
 *
 * @author chenchuxin
 * @since 3.0.5
 */
public class JavadocDocBuildTemplate implements IDocBuildTemplate<JavadocApiDoc>,
		IWebSocketDocBuildTemplate<WebSocketDoc>, IJavadocDocTemplate<JavadocJavaMethod> {

	/**
	 * api index
	 */
	private final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(1);

	@Override
	public boolean supportsFramework(String framework) {
		return FrameworkEnum.JAVADOC.getFramework().equalsIgnoreCase(framework);
	}

	@Override
	public boolean addMethodModifiers() {
		return true;
	}

	@Override
	public JavadocJavaMethod createEmptyJavadocJavaMethod() {
		return new JavadocJavaMethod();
	}

	@Override
	public ApiSchema<JavadocApiDoc> renderApi(ProjectDocConfigBuilder projectBuilder,
			Collection<JavaClass> candidateClasses) {
		ApiConfig apiConfig = projectBuilder.getApiConfig();
		List<JavadocApiDoc> apiDocList = new ArrayList<>();
		int maxOrder = 0;
		boolean setCustomOrder = false;
		for (JavaClass cls : candidateClasses) {
			if (skipClass(apiConfig, cls, null)) {
				continue;
			}
			String strOrder = JavaClassUtil.getClassTagsValue(cls, DocTags.ORDER, Boolean.TRUE);
			int order = 0;
			if (ValidateUtil.isNonNegativeInteger(strOrder)) {
				order = Integer.parseInt(strOrder);
				maxOrder = Math.max(maxOrder, order);
				setCustomOrder = true;
			}
			List<JavadocJavaMethod> apiMethodDocs = this.buildServiceMethod(cls, apiConfig, projectBuilder);
			this.handleJavaApiDoc(cls, apiDocList, apiMethodDocs, order, projectBuilder);
		}
		ApiSchema<JavadocApiDoc> apiSchema = new ApiSchema<>();
		if (apiConfig.isSortByTitle()) {
			// sort by title
			Collections.sort(apiDocList);
			apiSchema.setApiDatas(apiDocList);
			return apiSchema;
		}
		else if (setCustomOrder) {
			ATOMIC_INTEGER.getAndAdd(maxOrder);
			// while set custom oder
			final List<JavadocApiDoc> tempList = new ArrayList<>(apiDocList);
			tempList.forEach(p -> {
				if (p.getOrder() == 0) {
					p.setOrder(ATOMIC_INTEGER.getAndAdd(1));
				}
			});
			apiSchema.setApiDatas(tempList.stream()
				.sorted(Comparator.comparing(JavadocApiDoc::getOrder))
				.collect(Collectors.toList()));
		}
		else {
			apiDocList.forEach(p -> p.setOrder(ATOMIC_INTEGER.getAndAdd(1)));
			apiSchema.setApiDatas(apiDocList);
		}
		return apiSchema;
	}

	@Override
	public List<WebSocketDoc> renderWebSocketApi(ProjectDocConfigBuilder projectBuilder,
			Collection<JavaClass> candidateClasses) {
		return null;
	}

	@Override
	public boolean ignoreReturnObject(String typeName, List<String> ignoreParams) {
		return false;
	}

	@Override
	public boolean isEntryPoint(JavaClass cls, FrameworkAnnotations frameworkAnnotations) {
		List<DocletTag> docletTags = cls.getTags();
		for (DocletTag docletTag : docletTags) {
			String value = docletTag.getName();
			if (DocTags.JAVA_DOC.equals(value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public FrameworkAnnotations registeredAnnotations() {
		return null;
	}

	/**
	 * Handles the generation of Java API documentation. This method is responsible for
	 * creating an API documentation object based on the provided Java class information
	 * and populating its properties.
	 * @param cls The Java class from which to extract documentation information.
	 * @param apiDocList A list where the generated API documentation objects will be
	 * added.
	 * @param apiMethodDocs A list containing documentation for methods within the class.
	 * @param order The order in which the API documentation should be listed.
	 * @param builder A builder used to retrieve class information and configurations.
	 */
	private void handleJavaApiDoc(JavaClass cls, List<JavadocApiDoc> apiDocList, List<JavadocJavaMethod> apiMethodDocs,
			int order, ProjectDocConfigBuilder builder) {
		String className = cls.getCanonicalName();
		String comment = cls.getComment();
		String shortName = cls.getName();
		List<JavaType> javaTypes = cls.getImplements();
		if (!javaTypes.isEmpty() && !cls.isInterface()) {
			JavaType javaType = javaTypes.get(0);
			className = javaType.getCanonicalName();
			shortName = className;
			JavaClass javaClass = builder.getClassByName(className);
			if (StringUtil.isEmpty(comment) && Objects.nonNull(javaClass)) {
				comment = javaClass.getComment();
			}
		}
		JavadocApiDoc apiDoc = new JavadocApiDoc();
		apiDoc.setOrder(order);
		apiDoc.setName(className);
		apiDoc.setShortName(shortName);
		apiDoc.setAlias(className);
		if (builder.getApiConfig().isMd5EncryptedHtmlName()) {
			String name = DocUtil.generateId(apiDoc.getName());
			apiDoc.setAlias(name);
		}
		apiDoc.setDesc(DocUtil.getEscapeAndCleanComment(comment));
		apiDoc.setList(apiMethodDocs);

		List<DocletTag> docletTags = cls.getTags();
		List<String> authorList = new ArrayList<>();
		for (DocletTag docletTag : docletTags) {
			String name = docletTag.getName();
			if (DocTags.VERSION.equals(name)) {
				apiDoc.setVersion(docletTag.getValue());
			}
			if (DocTags.AUTHOR.equals(name)) {
				authorList.add(docletTag.getValue());
			}
		}
		apiDoc.setAuthor(String.join(", ", authorList));
		apiDocList.add(apiDoc);
	}

}
