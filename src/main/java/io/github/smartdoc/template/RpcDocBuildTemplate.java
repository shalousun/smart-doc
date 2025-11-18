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
import io.github.smartdoc.constants.DocGlobalConstants;
import io.github.smartdoc.constants.DocTags;
import io.github.smartdoc.constants.DubboAnnotationConstants;
import io.github.smartdoc.constants.FrameworkEnum;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.ApiSchema;
import io.github.smartdoc.model.RpcJavaMethod;
import io.github.smartdoc.model.WebSocketDoc;
import io.github.smartdoc.model.annotation.FrameworkAnnotations;
import io.github.smartdoc.model.rpc.RpcApiDoc;
import io.github.smartdoc.utils.DocUtil;
import io.github.smartdoc.utils.JavaClassUtil;
import com.power.common.util.StringUtil;
import com.power.common.util.ValidateUtil;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.expression.AnnotationValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * (Apache Dubbo) rpc doc build template.
 *
 * @author yu 2020/1/29.
 */
public class RpcDocBuildTemplate implements IDocBuildTemplate<RpcApiDoc>, IWebSocketDocBuildTemplate<WebSocketDoc>,
		IJavadocDocTemplate<RpcJavaMethod> {

	/**
	 * api index
	 */
	private final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(1);

	@Override
	public boolean supportsFramework(String framework) {
		return FrameworkEnum.DUBBO.getFramework().equalsIgnoreCase(framework);
	}

	@Override
	public boolean addMethodModifiers() {
		return false;
	}

	@Override
	public RpcJavaMethod createEmptyJavadocJavaMethod() {
		return new RpcJavaMethod();
	}

	@Override
	public ApiSchema<RpcApiDoc> renderApi(ProjectDocConfigBuilder projectBuilder,
			Collection<JavaClass> candidateClasses) {
		ApiConfig apiConfig = projectBuilder.getApiConfig();
		List<RpcApiDoc> apiDocList = new ArrayList<>();

		boolean setCustomOrder = false;
		int maxOrder = 0;
		for (JavaClass cls : candidateClasses) {
			if (skipClass(apiConfig, cls, null)) {
				continue;
			}
			String strOrder = JavaClassUtil.getClassTagsValue(cls, DocTags.ORDER, Boolean.TRUE);
			int order = 0;
			if (ValidateUtil.isNonNegativeInteger(strOrder)) {
				order = Integer.parseInt(strOrder);
				setCustomOrder = true;
				maxOrder = Math.max(maxOrder, order);
			}
			List<RpcJavaMethod> apiMethodDocs = this.buildServiceMethod(cls, apiConfig, projectBuilder);
			this.handleJavaApiDoc(cls, apiDocList, apiMethodDocs, order, projectBuilder);
		}
		ApiSchema<RpcApiDoc> apiSchema = new ApiSchema<>();
		if (apiConfig.isSortByTitle()) {
			// sort by title
			Collections.sort(apiDocList);
			apiSchema.setApiDatas(apiDocList);
			return apiSchema;
		}
		else if (setCustomOrder) {
			ATOMIC_INTEGER.getAndAdd(maxOrder);
			// while set custom oder
			final List<RpcApiDoc> tempList = new ArrayList<>(apiDocList);
			tempList.forEach(p -> {
				if (p.getOrder() == 0) {
					p.setOrder(ATOMIC_INTEGER.getAndAdd(1));
				}
			});
			apiSchema.setApiDatas(
					tempList.stream().sorted(Comparator.comparing(RpcApiDoc::getOrder)).collect(Collectors.toList()));
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
		// Exclude DubboSwaggerService from dubbo 2.7.x
		if (DubboAnnotationConstants.DUBBO_SWAGGER.equals(cls.getCanonicalName())) {
			return false;
		}
		List<JavaAnnotation> classAnnotations = cls.getAnnotations();
		for (JavaAnnotation annotation : classAnnotations) {
			String name = annotation.getType().getCanonicalName();
			if (DubboAnnotationConstants.SERVICE.equals(name) || DubboAnnotationConstants.DUBBO_SERVICE.equals(name)
					|| DubboAnnotationConstants.ALI_DUBBO_SERVICE.equals(name)) {
				return true;
			}
		}
		List<DocletTag> docletTags = cls.getTags();
		for (DocletTag docletTag : docletTags) {
			String value = docletTag.getName();
			if (DocTags.DUBBO.equals(value)) {
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
	 * processing a Java class to generate API documentation details, including setting up
	 * the API documentation list and method details.
	 * @param cls The JavaClass object representing the class being documented.
	 * @param apiDocList The list to store generated RpcApiDoc objects.
	 * @param apiMethodDocs The list containing documentation for methods within the
	 * class.
	 * @param order The order or priority of the API documentation.
	 * @param builder The ProjectDocConfigBuilder used to configure and retrieve class
	 * information.
	 */
	private void handleJavaApiDoc(JavaClass cls, List<RpcApiDoc> apiDocList, List<RpcJavaMethod> apiMethodDocs,
			int order, ProjectDocConfigBuilder builder) {
		String className = cls.getCanonicalName();
		String shortName = cls.getName();
		String comment = cls.getComment();
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
		RpcApiDoc apiDoc = new RpcApiDoc();
		apiDoc.setOrder(order);
		apiDoc.setName(className);
		apiDoc.setShortName(shortName);
		apiDoc.setAlias(className);
		apiDoc.setUri(builder.getServerUrl() + DocGlobalConstants.PATH_DELIMITER + className);
		apiDoc.setProtocol(FrameworkEnum.DUBBO.getFramework());
		if (builder.getApiConfig().isMd5EncryptedHtmlName()) {
			String name = DocUtil.generateId(apiDoc.getName());
			apiDoc.setAlias(name);
		}
		apiDoc.setDesc(DocUtil.getEscapeAndCleanComment(comment));
		apiDoc.setList(apiMethodDocs);

		List<JavaAnnotation> annotations = cls.getAnnotations();
		for (JavaAnnotation annotation : annotations) {
			String name = annotation.getType().getCanonicalName();
			if (!DubboAnnotationConstants.DUBBO_SERVICE.equals(name)) {
				continue;
			}
			AnnotationValue versionValue = annotation.getProperty("version");
			if (Objects.nonNull(versionValue)) {
				apiDoc.setVersion(StringUtil.removeDoubleQuotes(versionValue.getParameterValue().toString()));
			}
			AnnotationValue protocolValue = annotation.getProperty("protocol");
			if (Objects.nonNull(protocolValue)) {
				apiDoc.setProtocol(StringUtil.removeDoubleQuotes(protocolValue.getParameterValue().toString()));
			}
			AnnotationValue interfaceNameValue = annotation.getProperty("interfaceName");
			if (Objects.nonNull(interfaceNameValue)) {
				apiDoc.setName(StringUtil.removeDoubleQuotes(interfaceNameValue.getParameterValue().toString()));
			}
		}
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
			// set rpc protocol
			if (DocTags.PROTOCOL.equals(name)) {
				apiDoc.setProtocol(docletTag.getValue());
			}
			// set rpc service name
			if (DocTags.SERVICE.equals(name)) {
				apiDoc.setName(docletTag.getValue());
			}
		}
		apiDoc.setAuthor(String.join(", ", authorList));
		apiDocList.add(apiDoc);
	}

}
