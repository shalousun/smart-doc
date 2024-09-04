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
package com.ly.doc.template;

import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.constants.*;
import com.ly.doc.handler.IHeaderHandler;
import com.ly.doc.handler.IRequestMappingHandler;
import com.ly.doc.helper.FormDataBuildHelper;
import com.ly.doc.helper.JsonBuildHelper;
import com.ly.doc.helper.ParamsBuildHelper;
import com.ly.doc.model.*;
import com.ly.doc.model.annotation.EntryAnnotation;
import com.ly.doc.model.annotation.ExceptionAdviceAnnotation;
import com.ly.doc.model.annotation.FrameworkAnnotations;
import com.ly.doc.model.annotation.MappingAnnotation;
import com.ly.doc.model.request.ApiRequestExample;
import com.ly.doc.model.request.CurlRequest;
import com.ly.doc.model.request.RequestMapping;
import com.ly.doc.utils.*;
import com.power.common.util.*;
import com.thoughtworks.qdox.model.*;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ly.doc.constants.DocTags.IGNORE;

/**
 * Rest api doc template
 *
 * @author shalousun
 */
public interface IRestDocTemplate extends IBaseDocBuildTemplate {

	/**
	 * Logger for the class.
	 */
	Logger log = Logger.getLogger(IRestDocTemplate.class.getName());

	/**
	 * Atomic integer.
	 */
	AtomicInteger ATOMIC_INTEGER = new AtomicInteger(1);

	/**
	 * Processes API data to generate documentation schemas.
	 * @param projectBuilder Configuration builder for the project, used to obtain project
	 * configuration information.
	 * @param frameworkAnnotations Framework annotation processor, used to handle
	 * framework-specific annotations.
	 * @param configApiReqParams Configuration list of request parameters, used to process
	 * request parameters.
	 * @param baseMappingHandler Request mapping handler, used to process request mapping
	 * information.
	 * @param headerHandler Header handler, used to process header information.
	 * @param javaClasses Collection of Java classes, used to scan and process classes
	 * that need to generate documentation.
	 * @return Returns the generated API documentation schema.
	 */
	default ApiSchema<ApiDoc> processApiData(ProjectDocConfigBuilder projectBuilder,
			FrameworkAnnotations frameworkAnnotations, List<ApiReqParam> configApiReqParams,
			IRequestMappingHandler baseMappingHandler, IHeaderHandler headerHandler,
			Collection<JavaClass> javaClasses) {
		ApiConfig apiConfig = projectBuilder.getApiConfig();
		List<ApiDoc> apiDocList = new ArrayList<>();
		boolean setCustomOrder = false;
		int maxOrder = 0;
		ApiSchema<ApiDoc> apiSchema = new ApiSchema<>();
		// exclude class is ignore
		for (JavaClass cls : javaClasses) {
			if (StringUtil.isNotEmpty(apiConfig.getPackageFilters())) {
				// from smart config
				if (!DocUtil.isMatch(apiConfig.getPackageFilters(), cls)) {
					continue;
				}
			}
			if (StringUtil.isNotEmpty(apiConfig.getPackageExcludeFilters())) {
				if (DocUtil.isMatch(apiConfig.getPackageExcludeFilters(), cls)) {
					continue;
				}
			}
			// from tag
			DocletTag ignoreTag = cls.getTagByName(DocTags.IGNORE);
			if (!isEntryPoint(cls, frameworkAnnotations) || Objects.nonNull(ignoreTag)) {
				continue;
			}
			int order = 0;
			String strOrder = JavaClassUtil.getClassTagsValue(cls, DocTags.ORDER, Boolean.TRUE);
			if (ValidateUtil.isNonNegativeInteger(strOrder)) {
				setCustomOrder = true;
				order = Integer.parseInt(strOrder);
				maxOrder = Math.max(maxOrder, order);
			}

			List<ApiMethodDoc> apiMethodDocs = this.buildEntryPointMethod(cls, apiConfig, projectBuilder,
					frameworkAnnotations, configApiReqParams, baseMappingHandler, headerHandler);
			if (CollectionUtil.isEmpty(apiMethodDocs)) {
				continue;
			}
			this.handleApiDoc(cls, apiDocList, apiMethodDocs, order, apiConfig.isMd5EncryptedHtmlName());
		}
		apiDocList = this.handleTagsApiDoc(apiDocList);

		apiSchema.setApiExceptionStatuses(this.buildExceptionStatus(projectBuilder, javaClasses, frameworkAnnotations));

		if (apiConfig.isSortByTitle()) {
			// sort by title
			Collections.sort(apiDocList);
			apiSchema.setApiDatas(apiDocList);
			return apiSchema;
		}
		else if (setCustomOrder) {
			ATOMIC_INTEGER.getAndAdd(maxOrder);
			// while set custom oder
			final List<ApiDoc> tempList = new ArrayList<>(apiDocList);
			tempList.forEach(p -> {
				if (p.getOrder() == 0) {
					p.setOrder(ATOMIC_INTEGER.getAndAdd(1));
				}
			});
			apiSchema.setApiDatas(
					tempList.stream().sorted(Comparator.comparing(ApiDoc::getOrder)).collect(Collectors.toList()));
		}
		else {
			apiDocList.forEach(p -> p.setOrder(ATOMIC_INTEGER.getAndAdd(1)));
			apiSchema.setApiDatas(apiDocList);
		}
		return apiSchema;
	}

	/**
	 * Generates a string for document header rendering based on the list of request
	 * parameters. If the header list is empty, it initializes it to an empty list to
	 * avoid null pointer issues. This method is primarily used for creating API
	 * documentation table headers, with columns including parameter name, type, whether
	 * it's required, description, and since version.
	 * @param headers A list of {@link ApiReqParam} objects representing the request
	 * headers. Each object contains details like name, type, requirements, etc.
	 * @param isAdoc A flag indicating whether the output is formatted for AsciiDoc
	 * (.adoc) format. Currently, this parameter does not affect the output and is
	 * reserved for future use.
	 * @return A string representing the formatted header information for documentation.
	 * If the input list is empty, an empty string is returned.
	 */
	default String createDocRenderHeaders(List<ApiReqParam> headers, boolean isAdoc) {
		StringBuilder builder = new StringBuilder();
		if (CollectionUtil.isEmpty(headers)) {
			headers = new ArrayList<>(0);
		}
		for (ApiReqParam header : headers) {
			builder.append("|")
				.append(header.getName())
				.append("|")
				.append(header.getType())
				.append("|")
				.append(header.isRequired())
				.append("|")
				.append(header.getDesc())
				.append("|")
				.append(header.getSince())
				.append("|\n");
		}
		return builder.toString();
	}

	/**
	 * Handles the generation of API documentation. This default method creates API
	 * documentation for the provided Java class, including both class-level and
	 * method-level details. It constructs API documentation objects and populates them
	 * based on the comments associated with the class and its methods.
	 * @param cls The Java class to process documentation for.
	 * @param apiDocList A list accumulating all API documentation instances.
	 * @param apiMethodDocs A list of method documentation objects corresponding to the
	 * methods within the class.
	 * @param order The sorting order for the generated API documentation entry.
	 * @param isUseMD5 Flag indicating whether to use MD5 hashing to generate a unique
	 * alias for the documented class.
	 */
	default void handleApiDoc(JavaClass cls, List<ApiDoc> apiDocList, List<ApiMethodDoc> apiMethodDocs, int order,
			boolean isUseMD5) {
		String controllerName = cls.getName();
		ApiDoc apiDoc = new ApiDoc();
		String classAuthor = JavaClassUtil.getClassTagsValue(cls, DocTags.AUTHOR, Boolean.TRUE);
		apiDoc.setOrder(order);
		apiDoc.setName(controllerName);
		apiDoc.setAuthor(classAuthor);
		apiDoc.setAlias(controllerName);
		apiDoc.setFolder(true);
		apiDoc.setPackageName(cls.getPackage().getName());
		// apiDoc.setAuthor();

		// handle class tags
		List<DocletTag> classTags = cls.getTagsByName(DocTags.TAG);
		Set<String> tagSet = classTags.stream()
			.map(DocletTag::getValue)
			.map(StringUtils::trim)
			.collect(Collectors.toCollection(LinkedHashSet::new));
		String[] tags = tagSet.toArray(new String[] {});
		apiDoc.setTags(tags);

		if (isUseMD5) {
			String name = DocUtil.generateId(apiDoc.getName());
			apiDoc.setAlias(name);
		}
		String desc = DocUtil.getEscapeAndCleanComment(cls.getComment());
		apiDoc.setDesc(desc);
		apiDoc.setList(apiMethodDocs);
		apiDocList.add(apiDoc);

		tagSet.add(StringUtils.trim(apiDoc.getName()));
		for (String tag : tagSet) {
			DocMapping.tagDocPut(tag, apiDoc, null);
			for (ApiMethodDoc methodDoc : apiMethodDocs) {
				DocMapping.tagDocPut(tag, null, methodDoc);
			}
		}
		for (ApiMethodDoc methodDoc : apiMethodDocs) {
			String[] docTags = methodDoc.getTags();
			methodDoc.setClazzDoc(apiDoc);
			if (ArrayUtils.isEmpty(docTags)) {
				continue;
			}
			for (String tag : docTags) {
				DocMapping.tagDocPut(tag, null, methodDoc);
			}
		}
	}

	/**
	 * Maps the given parameter to an API parameter object. This method processes a string
	 * parameter, converting it into an entry in the API parameter list, and
	 * simultaneously updates the parameter mapping. It detects if the parameter includes
	 * a value assignment (e.g., "name=value"), extracts the name and value accordingly,
	 * or treats the entire string as the parameter name if no assignment is found. The
	 * parameter's type is determined (either "int32" for positive integers or "string"),
	 * and a new `ApiParam` instance is created with details extracted or inferred from
	 * the input. The newly created `ApiParam` is then added to the provided list, and its
	 * name is recorded in the mappingParams map with a null value.
	 * @param str The raw string containing the parameter information.
	 * @param paramList The list of ApiParam objects to which the processed parameter will
	 * be added.
	 * @param mappingParams A map holding parameter names as keys, used for tracking
	 * mapped parameters.
	 */
	default void mappingParamToApiParam(String str, List<ApiParam> paramList, Map<String, String> mappingParams) {
		String param = StringUtil.removeQuotes(str);
		String paramName;
		String paramValue;
		String description = "Parameter condition.";
		if (param.contains("=")) {
			int index = param.indexOf("=");
			paramName = param.substring(0, index);
			paramValue = param.substring(index + 1);
			description = description + " [" + paramName + "=" + paramValue + "]";
		}
		else {
			paramName = param;
			paramValue = DocUtil.getValByTypeAndFieldName("string", paramName, Boolean.TRUE);
		}
		String type = ValidateUtil.isPositiveInteger(paramValue) ? "int32" : "string";
		ApiParam apiParam = ApiParam.of()
			.setField(paramName)
			.setId(paramList.size() + 1)
			.setQueryParam(true)
			.setValue(paramValue)
			.setType(type)
			.setDesc(description)
			.setRequired(true)
			.setVersion(DocGlobalConstants.DEFAULT_VERSION);
		paramList.add(apiParam);
		mappingParams.put(paramName, null);
	}

	/**
	 * Processes a parameter string, extracting or inferring its name and value, then adds
	 * it to a path parameters map. This method handles a raw string that represents a
	 * parameter. If the string includes an equals sign, indicating a value assignment, it
	 * separates the parameter name and value accordingly. Otherwise, it assumes the
	 * entire string is the parameter name and infers a default value based on the type
	 * "string". The resulting parameter name-value pair is then stored in the provided
	 * pathParamsMap.
	 * @param str The raw string containing the parameter information potentially with a
	 * value assignment.
	 * @param pathParamsMap A map to store path parameter names paired with their
	 * respective values.
	 */
	default void mappingParamProcess(String str, Map<String, String> pathParamsMap) {
		String param = StringUtil.removeQuotes(str);
		String paramName;
		String paramValue;
		// If the parameter contains an equals sign, split into name and value; otherwise,
		// treat the whole string as the name.
		if (param.contains("=")) {
			int index = param.indexOf("=");
			paramName = param.substring(0, index);
			paramValue = param.substring(index + 1);
			pathParamsMap.put(paramName, paramValue);
		}
		else {
			paramName = param;
			// Infer a default value for the parameter based on its type and add it to the
			// map.
			pathParamsMap.put(paramName, DocUtil.getValByTypeAndFieldName("string", paramName, Boolean.TRUE));
		}
	}

	/**
	 * Retrieves the parameter name from an annotation, considering both 'value' and
	 * 'name' properties, and removes any surrounding quotes from the resolved name.
	 * @param classLoader The ClassLoader used to load classes for annotation resolution.
	 * @param paramName The default parameter name.
	 * @param annotation The annotation instance containing the parameter name
	 * information.
	 * @return The resolved and cleaned parameter name, or the default if not specified in
	 * the annotation.
	 */
	default String getParamName(ClassLoader classLoader, String paramName, JavaAnnotation annotation) {
		// First, attempt to resolve the parameter name using the 'value' property of the
		// annotation
		String resolvedParamName = DocUtil.resolveAnnotationValue(classLoader,
				annotation.getProperty(DocAnnotationConstants.VALUE_PROP));

		// If the 'value' property did not yield a usable name, try resolving it with the
		// 'name' property
		if (StringUtils.isBlank(resolvedParamName)) {
			resolvedParamName = DocUtil.resolveAnnotationValue(classLoader,
					annotation.getProperty(DocAnnotationConstants.NAME_PROP));
		}

		// If a name was successfully resolved from the annotation, replace the default
		// paramName with it,
		// after removing any surrounding quotes
		if (!StringUtils.isBlank(resolvedParamName)) {
			paramName = StringUtil.removeQuotes(resolvedParamName);
		}
		// Ensure the final parameter name has no surrounding quotes and return it
		return StringUtil.removeQuotes(paramName);
	}

	/**
	 * Processes the API documentation tags, consolidating API documents with identical
	 * tags.
	 * @param apiDocList The original list of API documentation.
	 * @return A processed list of API documentation, sorted and merged by tags.
	 */
	default List<ApiDoc> handleTagsApiDoc(List<ApiDoc> apiDocList) {
		if (CollectionUtil.isEmpty(apiDocList)) {
			return Collections.emptyList();
		}

		// all class tag copy
		Map<String, ApiDoc> copyMap = new HashMap<>(16);
		apiDocList.forEach(doc -> {
			String[] tags = doc.getTags();
			if (ArrayUtils.isEmpty(tags)) {
				tags = new String[] { doc.getPackageName() + "." + doc.getName() };
			}

			for (String tag : tags) {
				tag = StringUtil.trim(tag);
				copyMap.computeIfPresent(tag, (k, v) -> {
					List<ApiMethodDoc> list = CollectionUtil.isEmpty(v.getList()) ? new ArrayList<>() : v.getList();
					list.addAll(doc.getList());
					v.setList(list);
					return v;
				});
				copyMap.putIfAbsent(tag, doc);
			}
		});

		// handle method tag
		Map<String, ApiDoc> allMap = new HashMap<>(copyMap);
		allMap.forEach((k, v) -> {
			List<ApiMethodDoc> methodDocList = v.getList();
			methodDocList.forEach(method -> {
				String[] tags = method.getTags();
				if (ArrayUtils.isEmpty(tags)) {
					return;
				}
				for (String tag : tags) {
					tag = StringUtil.trim(tag);
					copyMap.computeIfPresent(tag, (k1, v2) -> {
						method.setOrder(v2.getList().size() + 1);
						v2.getList().add(method);
						return v2;
					});
					copyMap.putIfAbsent(tag, ApiDoc.buildTagApiDoc(v, tag, method));
				}
			});
		});

		List<ApiDoc> apiDocs = new ArrayList<>(copyMap.values());
		int index = apiDocs.size() - 1;
		for (ApiDoc apiDoc : apiDocs) {
			if (apiDoc.getOrder() == null) {
				apiDoc.setOrder(index++);
			}
		}
		apiDocs.sort(Comparator.comparing(ApiDoc::getOrder));
		return apiDocs;
	}

	/**
	 * Retrieves the annotations of the specified class, including those inherited from
	 * its superclasses and interfaces, but only considering mapping annotations for
	 * inheritance.
	 *
	 * <p>
	 * This method first checks if the specified class has both entry and mapping
	 * annotations. If it does, it returns the annotations of the class directly. If not,
	 * it recursively retrieves the mapping annotations from its superclasses and
	 * interfaces.
	 * </p>
	 * @param cls the class whose annotations are to be retrieved
	 * @param frameworkAnnotations the framework annotations to be used for filtering
	 * @return a list of annotations for the specified class, including inherited mapping
	 * annotations
	 */
	default List<JavaAnnotation> getClassAnnotations(JavaClass cls, FrameworkAnnotations frameworkAnnotations) {
		// Retrieve the annotations of the specified class
		List<JavaAnnotation> annotationsList = new ArrayList<>(cls.getAnnotations());

		// Get entry annotations from framework annotations
		Map<String, EntryAnnotation> entryAnnotationMap = Objects.isNull(frameworkAnnotations.getEntryAnnotations())
				? Collections.emptyMap() : frameworkAnnotations.getEntryAnnotations();

		// Get mapping annotations from framework annotations
		Map<String, MappingAnnotation> mappingAnnotationMap = Objects
			.isNull(frameworkAnnotations.getMappingAnnotations()) ? Collections.emptyMap()
					: frameworkAnnotations.getMappingAnnotations();

		// Check if the class has both entry and mapping annotations
		boolean hasEntryAndMappingAnnotation = annotationsList.stream().anyMatch(item -> {
			String annotationName = item.getType().getValue();
			String fullyName = item.getType().getFullyQualifiedName();
			return (entryAnnotationMap.containsKey(annotationName) || entryAnnotationMap.containsKey(fullyName))
					&& (mappingAnnotationMap.containsKey(annotationName)
							|| mappingAnnotationMap.containsKey(fullyName));
		});

		// If the class has both entry and mapping annotations, return its annotations
		// directly
		if (hasEntryAndMappingAnnotation) {
			return annotationsList;
		}

		// Inherit mapping annotations from superclass, if any
		JavaClass superJavaClass = cls.getSuperJavaClass();
		if (Objects.nonNull(superJavaClass)
				&& !JavaTypeConstants.OBJECT_SIMPLE_NAME.equals(superJavaClass.getSimpleName())) {
			List<JavaAnnotation> superAnnotations = this.getClassAnnotations(superJavaClass, frameworkAnnotations);
			annotationsList.addAll(superAnnotations);
		}

		// Inherit mapping annotations from interfaces, if any
		List<JavaClass> interfaceList = cls.getInterfaces();
		if (CollectionUtil.isNotEmpty(interfaceList)) {
			for (JavaClass javaInterface : interfaceList) {
				List<JavaAnnotation> interfaceAnnotations = this.getClassAnnotations(javaInterface,
						frameworkAnnotations);
				annotationsList.addAll(interfaceAnnotations);
			}
		}
		return annotationsList;
	}

	/**
	 * Retrieves the annotations of the specified class, including those inherited from
	 * its superclasses and interfaces, but only considering mapping annotations for
	 * inheritance.
	 *
	 * <p>
	 * This method retrieves the annotations of the specified class and recursively
	 * collects mapping annotations from its superclasses and interfaces.
	 * </p>
	 * @param cls the class whose annotations are to be retrieved
	 * @param mappingAnnotationMap the map of mapping annotations used to filter and
	 * inherit annotations
	 * @return a list of annotations for the specified class, including inherited mapping
	 * annotations
	 */
	default List<JavaAnnotation> getClassAnnotations(JavaClass cls,
			Map<String, MappingAnnotation> mappingAnnotationMap) {
		// Retrieve the annotations of the specified class
		List<JavaAnnotation> annotationsList = new ArrayList<>(cls.getAnnotations());

		// Check if the class has both mapping annotations
		boolean hasMappingAnnotation = annotationsList.stream().anyMatch(item -> {
			String annotationName = item.getType().getValue();
			String fullyName = item.getType().getFullyQualifiedName();
			return (mappingAnnotationMap.containsKey(annotationName) || mappingAnnotationMap.containsKey(fullyName));
		});

		// If the class has both mapping annotations, return its annotations directly
		if (hasMappingAnnotation) {
			return annotationsList;
		}

		// Inherit mapping annotations from superclass, if any
		JavaClass superJavaClass = cls.getSuperJavaClass();
		if (Objects.nonNull(superJavaClass)
				&& !JavaTypeConstants.OBJECT_SIMPLE_NAME.equals(superJavaClass.getSimpleName())) {
			annotationsList.addAll(this.getClassAnnotations(superJavaClass, mappingAnnotationMap)
				.stream()
				.filter(annotation -> mappingAnnotationMap.containsKey(annotation.getType().getValue())
						|| mappingAnnotationMap.containsKey(annotation.getType().getFullyQualifiedName()))
				.collect(Collectors.toList()));
		}

		// Inherit mapping annotations from interfaces, if any
		List<JavaClass> interfaceList = cls.getInterfaces();
		if (CollectionUtil.isNotEmpty(interfaceList)) {
			for (JavaClass javaInterface : interfaceList) {
				annotationsList.addAll(this.getClassAnnotations(javaInterface, mappingAnnotationMap)
					.stream()
					.filter(annotation -> mappingAnnotationMap.containsKey(annotation.getType().getValue())
							|| mappingAnnotationMap.containsKey(annotation.getType().getFullyQualifiedName()))
					.collect(Collectors.toList()));
			}
		}

		return annotationsList;
	}

	/**
	 * Constructs a list of exception statuses based on the provided project configuration
	 * builder, collection of Java classes, and framework annotations. This method
	 * primarily parses exception handling methods within Java classes and generates
	 * corresponding exception status configurations from these method details.
	 * @param projectBuilder The project configuration builder to retrieve project-related
	 * configuration information.
	 * @param javaClasses A collection of Java classes from which exception handling
	 * methods are parsed.
	 * @param frameworkAnnotations A collection of framework annotations used to determine
	 * if certain classes or methods should be ignored.
	 * @return A list of ApiExceptionStatus objects representing all parsed exception
	 * handling methods and their associated status information.
	 */
	default List<ApiExceptionStatus> buildExceptionStatus(ProjectDocConfigBuilder projectBuilder,
			Collection<JavaClass> javaClasses, FrameworkAnnotations frameworkAnnotations) {
		ApiConfig apiConfig = projectBuilder.getApiConfig();
		Set<String> statusSet = new HashSet<>(8);
		List<ApiExceptionStatus> exceptionStatusList = new ArrayList<>(8);
		for (JavaClass cls : javaClasses) {
			// from tag
			DocletTag ignoreTag = cls.getTagByName(DocTags.IGNORE);
			if (!isExceptionAdviceEntryPoint(cls, frameworkAnnotations) || Objects.nonNull(ignoreTag)) {
				continue;
			}
			boolean paramsDataToTree = projectBuilder.getApiConfig().isParamsDataToTree();

			List<JavaMethod> methods = cls.getMethods();
			List<DocJavaMethod> docJavaMethods = new ArrayList<>(methods.size());
			for (JavaMethod method : methods) {
				if (method.isPrivate()) {
					continue;
				}
				if (Objects.nonNull(method.getTagByName(IGNORE))) {
					continue;
				}
				docJavaMethods.add(convertToDocJavaMethod(apiConfig, projectBuilder, method, null));
			}
			// add parent class methods
			docJavaMethods.addAll(getParentsClassMethods(apiConfig, projectBuilder, cls));

			for (DocJavaMethod docJavaMethod : docJavaMethods) {
				JavaMethod method = docJavaMethod.getJavaMethod();
				ExceptionAdviceMethod adviceMethod = this.processExceptionAdviceMethod(method);
				if (Objects.isNull(adviceMethod) || !adviceMethod.isExceptionHandlerMethod()
						|| Objects.isNull(adviceMethod.getStatus())) {
					continue;
				}
				String statusCode = HttpStatusUtil.getStatusCode(adviceMethod.getStatus());
				if (statusSet.contains(statusCode)) {
					continue;
				}
				statusSet.add(statusCode);
				ApiExceptionStatus apiExceptionStatus = new ApiExceptionStatus();
				apiExceptionStatus.setStatus(statusCode);
				apiExceptionStatus.setDesc(HttpStatusUtil.getStatusDescription(statusCode));
				apiExceptionStatus.setAuthor(docJavaMethod.getAuthor());
				apiExceptionStatus.setDetail(docJavaMethod.getDetail());

				// build response params
				List<ApiParam> responseParams = buildReturnApiParams(docJavaMethod, projectBuilder);
				if (paramsDataToTree) {
					responseParams = ApiParamTreeUtil.apiParamToTree(responseParams);
				}
				apiExceptionStatus.setExceptionResponseParams(responseParams);

				// build response usage
				String responseValue = DocUtil.getNormalTagComments(method, DocTags.API_RESPONSE, cls.getName());
				if (StringUtil.isNotEmpty(responseValue)) {
					responseValue = responseValue.replaceAll("<br>", "");
					apiExceptionStatus.setResponseUsage(JsonUtil.toPrettyFormat(responseValue));
				}
				else {
					apiExceptionStatus.setResponseUsage(JsonBuildHelper.buildReturnJson(docJavaMethod, projectBuilder));
				}
				exceptionStatusList.add(apiExceptionStatus);
			}
		}
		if (apiConfig.isAddDefaultHttpStatuses() && exceptionStatusList.isEmpty()) {
			exceptionStatusList.addAll(defaultHttpErrorStatuses());
		}
		Collections.sort(exceptionStatusList);
		return exceptionStatusList;
	}

	/**
	 * Builds entry point method documentation. Generates API method documentation for the
	 * specified class, including all its methods and inherited methods that meet the
	 * documentation generation criteria.
	 * @param cls The class to generate documentation for.
	 * @param apiConfig The API configuration information.
	 * @param projectBuilder Configuration builder for the project, used to obtain
	 * project-related configuration.
	 * @param frameworkAnnotations Framework annotation information, used to handle
	 * mapping annotations.
	 * @param configApiReqParams Configured request parameters, used to generate API
	 * request parameter documentation.
	 * @param baseMappingHandler Handles request mapping annotations, used to process URL
	 * and HTTP method information.
	 * @param headerHandler Handles request headers, used to generate request header
	 * documentation.
	 * @return A list of API method documentation.
	 */
	default List<ApiMethodDoc> buildEntryPointMethod(final JavaClass cls, ApiConfig apiConfig,
			ProjectDocConfigBuilder projectBuilder, FrameworkAnnotations frameworkAnnotations,
			List<ApiReqParam> configApiReqParams, IRequestMappingHandler baseMappingHandler,
			IHeaderHandler headerHandler) {
		String clazName = cls.getCanonicalName();
		boolean paramsDataToTree = projectBuilder.getApiConfig().isParamsDataToTree();
		ClassLoader classLoader = projectBuilder.getApiConfig().getClassLoader();
		String group = JavaClassUtil.getClassTagsValue(cls, DocTags.GROUP, Boolean.TRUE);
		// Get mapping annotations
		Map<String, MappingAnnotation> mappingAnnotations = Objects.isNull(frameworkAnnotations.getMappingAnnotations())
				? Collections.emptyMap() : frameworkAnnotations.getMappingAnnotations();
		// Get class mappingAnnotations from class and its parent class or interface
		List<JavaAnnotation> classAnnotations = this.getClassAnnotations(cls, mappingAnnotations);

		String baseUrl = "";
		// The requestMapping annotation's consumes value on class
		String classMediaType = null;
		Map<String, MappingAnnotation> mappingAnnotationMap = frameworkAnnotations.getMappingAnnotations();
		for (JavaAnnotation annotation : classAnnotations) {
			String annotationName = annotation.getType().getValue();
			MappingAnnotation mappingAnnotation = mappingAnnotationMap.get(annotationName);
			if (Objects.isNull(mappingAnnotation)) {
				continue;
			}
			if (CollectionUtil.isNotEmpty(mappingAnnotation.getPathProps())) {
				baseUrl = StringUtil.removeQuotes(DocUtil.getPathUrl(classLoader, annotation,
						mappingAnnotation.getPathProps().toArray(new String[0])));
			}
			// use first annotation's value
			if (classMediaType == null) {
				Object consumes = annotation.getNamedParameter(mappingAnnotation.getConsumesProp());
				if (consumes != null) {
					classMediaType = consumes.toString();
				}
			}
		}

		Set<String> filterMethods = DocUtil.findFilterMethods(clazName);
		boolean needAllMethods = filterMethods.contains(DocGlobalConstants.DEFAULT_FILTER_METHOD);

		List<JavaMethod> methods = cls.getMethods();
		List<DocJavaMethod> docJavaMethods = new ArrayList<>(methods.size());
		for (JavaMethod method : methods) {
			if (method.isPrivate()
					|| DocUtil.isMatch(apiConfig.getPackageExcludeFilters(), clazName + "." + method.getName())) {
				continue;
			}
			if (Objects.nonNull(method.getTagByName(IGNORE))) {
				continue;
			}
			if (needAllMethods || filterMethods.contains(method.getName())) {
				docJavaMethods.add(this.convertToDocJavaMethod(apiConfig, projectBuilder, method, null));
			}
		}
		// add parent class methods
		docJavaMethods.addAll(getParentsClassMethods(apiConfig, projectBuilder, cls));
		List<JavaType> implClasses = cls.getImplements();
		for (JavaType type : implClasses) {
			JavaClass javaClass = (JavaClass) type;
			Map<String, JavaType> actualTypesMap = JavaClassUtil.getActualTypesMap(javaClass);
			for (JavaMethod method : javaClass.getMethods()) {
				if (method.isDefault()) {
					docJavaMethods.add(this.convertToDocJavaMethod(apiConfig, projectBuilder, method, actualTypesMap));
				}
			}
		}
		// call ICustomJavaMethodHandler
		if (apiConfig.getCustomJavaMethodHandler() != null) {
			docJavaMethods = apiConfig.getCustomJavaMethodHandler().apply(cls, docJavaMethods);
		}
		List<ApiMethodDoc> methodDocList = new ArrayList<>(methods.size());
		int methodOrder = 0;
		for (DocJavaMethod docJavaMethod : docJavaMethods) {
			JavaMethod method = docJavaMethod.getJavaMethod();

			// handle request mapping
			RequestMapping requestMapping = baseMappingHandler.handle(projectBuilder, baseUrl, method,
					frameworkAnnotations,
					(javaClass, mapping) -> this.requestMappingPostProcess(javaClass, method, mapping));
			if (Objects.isNull(requestMapping)) {
				continue;
			}
			if (Objects.isNull(requestMapping.getShortUrl())) {
				continue;
			}
			docJavaMethod.setMethodType(requestMapping.getMethodType());
			ApiMethodDoc apiMethodDoc = new ApiMethodDoc();
			// fill contentType by annotation's consumes parameter
			String mediaType = requestMapping.getMediaType();
			if (Objects.nonNull(mediaType)) {
				apiMethodDoc.setContentType(MediaType.valueOf(mediaType));
			}
			else if (Objects.nonNull(classMediaType)) {
				// if method does not contain consumes parameter, then use the value of
				// class
				apiMethodDoc.setContentType(MediaType.valueOf(classMediaType));
			}
			apiMethodDoc.setDownload(docJavaMethod.isDownload());
			apiMethodDoc.setPage(docJavaMethod.getPage());
			apiMethodDoc.setGroup(group);
			apiMethodDoc.setVersion(docJavaMethod.getVersion());
			if (Objects.nonNull(docJavaMethod.getGroup())) {
				apiMethodDoc.setGroup(docJavaMethod.getGroup());
			}

			// handle tags
			List<DocletTag> tags = method.getTagsByName(DocTags.TAG);
			apiMethodDoc.setTags(tags.stream().map(DocletTag::getValue).toArray(String[]::new));

			methodOrder++;
			apiMethodDoc.setOrder(methodOrder);
			apiMethodDoc.setName(method.getName());
			String common = method.getComment();
			if (StringUtil.isEmpty(common)) {
				common = JavaClassUtil.getSameSignatureMethodCommonFromInterface(cls, method);
			}
			apiMethodDoc.setDesc(common);
			apiMethodDoc.setAuthor(docJavaMethod.getAuthor());
			apiMethodDoc.setDetail(docJavaMethod.getDetail());
			String methodUid = DocUtil.generateId(clazName + method.getName() + methodOrder);
			apiMethodDoc.setMethodId(methodUid);
			// handle headers
			List<ApiReqParam> apiReqHeaders = headerHandler.handle(method, projectBuilder);
			apiReqHeaders = apiReqHeaders.stream()
				.filter(param -> DocUtil.filterPath(requestMapping, param))
				.collect(Collectors.toList());

			apiMethodDoc.setType(requestMapping.getMethodType());
			apiMethodDoc.setUrl(requestMapping.getUrl());
			apiMethodDoc.setServerUrl(projectBuilder.getServerUrl());
			apiMethodDoc.setPath(requestMapping.getShortUrl());
			apiMethodDoc.setDeprecated(requestMapping.isDeprecated());

			final List<ApiReqParam> apiReqParamList = configApiReqParams.stream()
				.filter(param -> DocUtil.filterPath(requestMapping, param))
				.collect(Collectors.toList());

			// build request params
			ApiMethodReqParam apiMethodReqParam = this.requestParams(docJavaMethod, projectBuilder, apiReqParamList,
					frameworkAnnotations);
			apiMethodDoc.setPathParams(apiMethodReqParam.getPathParams());
			apiMethodDoc.setQueryParams(apiMethodReqParam.getQueryParams());
			apiMethodDoc.setRequestParams(apiMethodReqParam.getRequestParams());

			if (paramsDataToTree) {
				// convert to tree
				this.convertParamsDataToTree(apiMethodDoc);
			}
			List<ApiReqParam> allApiReqHeaders;
			final Map<String, List<ApiReqParam>> reqParamMap = configApiReqParams.stream()
				.collect(Collectors.groupingBy(ApiReqParam::getParamIn));
			final List<ApiReqParam> headerParamList = reqParamMap.getOrDefault(ApiReqParamInTypeEnum.HEADER.getValue(),
					Collections.emptyList());
			allApiReqHeaders = Stream.of(headerParamList, apiReqHeaders)
				.filter(Objects::nonNull)
				.flatMap(Collection::stream)
				.distinct()
				.filter(param -> DocUtil.filterPath(requestMapping, param))
				.collect(Collectors.toList());

			// reduce create in template
			apiMethodDoc.setHeaders(this.createDocRenderHeaders(allApiReqHeaders, apiConfig.isAdoc()));
			apiMethodDoc.setRequestHeaders(allApiReqHeaders);
			String path = apiMethodDoc.getPath().split(";")[0];
			String pathUrl = DocUtil.formatPathUrl(path);
			List<ApiParam> pathParams = apiMethodDoc.getPathParams();
			Iterator<ApiParam> pathIterator = pathParams.iterator();
			while (pathIterator.hasNext()) {
				ApiParam next = pathIterator.next();
				String pathKey = "{" + next.getField() + "}";
				if (!pathUrl.contains(pathKey)) {
					pathIterator.remove();
				}
			}

			// build request json
			ApiRequestExample requestExample = this.buildReqJson(docJavaMethod, apiMethodDoc, projectBuilder,
					frameworkAnnotations);
			String requestJson = requestExample.getExampleBody();
			// set request example detail
			apiMethodDoc.setRequestExample(requestExample);
			apiMethodDoc.setRequestUsage(requestJson == null ? requestExample.getUrl() : requestJson);
			// build response usage
			String responseValue = DocUtil.getNormalTagComments(method, DocTags.API_RESPONSE, cls.getName());
			if (StringUtil.isNotEmpty(responseValue)) {
				responseValue = responseValue.replaceAll("<br>", "");
				apiMethodDoc.setResponseUsage(JsonUtil.toPrettyFormat(responseValue));
			}
			else {
				apiMethodDoc.setResponseUsage(JsonBuildHelper.buildReturnJson(docJavaMethod, projectBuilder));
			}
			// build response params
			List<ApiParam> responseParams = this.buildReturnApiParams(docJavaMethod, projectBuilder);
			if (paramsDataToTree) {
				responseParams = ApiParamTreeUtil.apiParamToTree(responseParams);
			}
			apiMethodDoc.setReturnSchema(docJavaMethod.getReturnSchema());
			apiMethodDoc.setRequestSchema(docJavaMethod.getRequestSchema());
			apiMethodDoc.setResponseParams(responseParams);

			// handle extension
			Map<String, String> extensions = DocUtil.getCommentsByTag(method, DocTags.EXTENSION, null);
			if (!extensions.isEmpty()) {
				Map<String, Object> extensionParams = apiMethodDoc.getExtensions() != null
						? apiMethodDoc.getExtensions() : new HashMap<>();
				extensions.forEach((key, value) -> extensionParams.put(key, DocUtil.detectTagValue(value)));
				apiMethodDoc.setExtensions(extensionParams);
			}

			TornaUtil.setTornaArrayTags(docJavaMethod.getJavaMethod(), apiMethodDoc, apiConfig);
			methodDocList.add(apiMethodDoc);
		}

		return methodDocList;
	}

	/**
	 * build request params
	 * @param docJavaMethod docJavaMethod
	 * @param builder projectBuilder
	 * @param configApiReqParams configApiReqParams
	 * @param frameworkAnnotations frameworkAnnotations
	 * @return ApiMethodReqParam
	 */
	default ApiMethodReqParam requestParams(final DocJavaMethod docJavaMethod, ProjectDocConfigBuilder builder,
			List<ApiReqParam> configApiReqParams, FrameworkAnnotations frameworkAnnotations) {
		JavaMethod javaMethod = docJavaMethod.getJavaMethod();
		boolean isStrict = builder.getApiConfig().isStrict();
		boolean isShowValidation = builder.getApiConfig().isShowValidation();
		ClassLoader classLoader = builder.getApiConfig().getClassLoader();
		String className = javaMethod.getDeclaringClass().getCanonicalName();
		Map<String, String> paramTagMap = docJavaMethod.getParamTagMap();
		Map<String, String> paramsComments = docJavaMethod.getParamsComments();
		List<ApiParam> paramList = new ArrayList<>();
		Map<String, String> mappingParams = new HashMap<>(16);
		List<JavaAnnotation> methodAnnotations = javaMethod.getAnnotations();
		Map<String, MappingAnnotation> mappingAnnotationMap = frameworkAnnotations.getMappingAnnotations();
		for (JavaAnnotation annotation : methodAnnotations) {
			String annotationName = annotation.getType().getName();
			MappingAnnotation mappingAnnotation = mappingAnnotationMap.get(annotationName);
			if (Objects.nonNull(mappingAnnotation) && StringUtil.isNotEmpty(mappingAnnotation.getParamsProp())) {
				Object paramsObjects = annotation.getNamedParameter(mappingAnnotation.getParamsProp());
				if (Objects.isNull(paramsObjects)) {
					continue;
				}
				String params = StringUtil.removeQuotes(paramsObjects.toString());
				if (!params.startsWith("[")) {
					mappingParamToApiParam(paramsObjects.toString(), paramList, mappingParams);
					continue;
				}
				@SuppressWarnings("unchecked")
				List<String> headers = (LinkedList<String>) paramsObjects;
				for (String str : headers) {
					mappingParamToApiParam(str, paramList, mappingParams);
				}
			}
		}
		final Map<String, Map<String, ApiReqParam>> collect = configApiReqParams.stream()
			.collect(Collectors.groupingBy(ApiReqParam::getParamIn,
					Collectors.toMap(ApiReqParam::getName, m -> m, (k1, k2) -> k1)));
		final Map<String, ApiReqParam> pathReqParamMap = collect.getOrDefault(ApiReqParamInTypeEnum.PATH.getValue(),
				Collections.emptyMap());
		final Map<String, ApiReqParam> queryReqParamMap = collect.getOrDefault(ApiReqParamInTypeEnum.QUERY.getValue(),
				Collections.emptyMap());
		List<DocJavaParameter> parameterList = getJavaParameterList(builder, docJavaMethod, frameworkAnnotations);
		if (parameterList.isEmpty()) {
			AtomicInteger querySize = new AtomicInteger(paramList.size() + 1);
			paramList.addAll(queryReqParamMap.values()
				.stream()
				.map(p -> ApiReqParam.convertToApiParam(p).setQueryParam(true).setId(querySize.getAndIncrement()))
				.collect(Collectors.toList()));
			AtomicInteger pathSize = new AtomicInteger(1);
			return ApiMethodReqParam.builder()
				.setPathParams(new ArrayList<>(pathReqParamMap.values()
					.stream()
					.map(p -> ApiReqParam.convertToApiParam(p).setPathParam(true).setId(pathSize.getAndIncrement()))
					.collect(Collectors.toList())))
				.setQueryParams(paramList)
				.setRequestParams(new ArrayList<>(0));
		}
		boolean requestFieldToUnderline = builder.getApiConfig().isRequestFieldToUnderline();
		int requestBodyCounter = 0;
		out: for (DocJavaParameter apiParameter : parameterList) {
			JavaParameter parameter = apiParameter.getJavaParameter();
			String paramName = parameter.getName();
			if (mappingParams.containsKey(paramName)) {
				continue;
			}
			String typeName = apiParameter.getGenericCanonicalName();
			String simpleTypeName = apiParameter.getTypeValue();
			String simpleName = simpleTypeName.toLowerCase();
			String fullyQualifiedName = apiParameter.getFullyQualifiedName();
			String genericFullyQualifiedName = apiParameter.getGenericFullyQualifiedName();
			if (!paramTagMap.containsKey(paramName) && JavaClassValidateUtil.isPrimitive(fullyQualifiedName)
					&& isStrict) {
				throw new RuntimeException("ERROR: Unable to find javadoc @param for actual param \"" + paramName
						+ "\" in method " + javaMethod.getName() + " from " + className);
			}
			StringBuilder comment = new StringBuilder(this.paramCommentResolve(paramTagMap.get(paramName)));

			JavaClass javaClass = builder.getJavaProjectBuilder().getClassByName(genericFullyQualifiedName);
			String mockValue = JavaFieldUtil.createMockValue(paramsComments, paramName, typeName, simpleTypeName);
			List<JavaAnnotation> annotations = parameter.getAnnotations();
			Set<String> groupClasses = JavaClassUtil.getParamGroupJavaClass(annotations,
					builder.getJavaProjectBuilder());
			String strRequired = "false";
			boolean isPathVariable = false;
			boolean isRequestBody = false;
			boolean required = false;
			boolean isRequestParam = false;
			boolean isRequestPart = false;
			if (annotations.isEmpty() && (Methods.GET.getValue().equals(docJavaMethod.getMethodType())
					|| Methods.DELETE.getValue().equals(docJavaMethod.getMethodType()))) {
				isRequestParam = true;
			}
			for (JavaAnnotation annotation : annotations) {
				String annotationName = annotation.getType().getValue();
				if (ignoreMvcParamWithAnnotation(annotationName)) {
					continue out;
				}
				if (frameworkAnnotations.getRequestParamAnnotation().getAnnotationName().equals(annotationName)
						|| frameworkAnnotations.getPathVariableAnnotation().getAnnotationName().equals(annotationName)
						|| frameworkAnnotations.getRequestPartAnnotation().getAnnotationName().equals(annotationName)) {
					String defaultValueProp = DocAnnotationConstants.DEFAULT_VALUE_PROP;
					String requiredProp = DocAnnotationConstants.REQUIRED_PROP;
					if (frameworkAnnotations.getRequestParamAnnotation().getAnnotationName().equals(annotationName)) {
						defaultValueProp = frameworkAnnotations.getRequestParamAnnotation().getDefaultValueProp();
						requiredProp = frameworkAnnotations.getRequestParamAnnotation().getRequiredProp();
						isRequestParam = true;
					}
					if (frameworkAnnotations.getPathVariableAnnotation().getAnnotationName().equals(annotationName)) {
						defaultValueProp = frameworkAnnotations.getPathVariableAnnotation().getDefaultValueProp();
						requiredProp = frameworkAnnotations.getPathVariableAnnotation().getRequiredProp();
						isPathVariable = true;
					}
					if (frameworkAnnotations.getRequestPartAnnotation().getAnnotationName().equals(annotationName)) {
						requiredProp = frameworkAnnotations.getRequestPartAnnotation().getRequiredProp();
						isRequestPart = true;
						mockValue = JsonBuildHelper.buildJson(fullyQualifiedName, typeName, Boolean.FALSE, 0,
								new HashMap<>(16), groupClasses, docJavaMethod.getJsonViewClasses(), builder);
						requestBodyCounter++;
					}
					AnnotationValue annotationDefaultVal = annotation.getProperty(defaultValueProp);
					if (Objects.nonNull(annotationDefaultVal)) {
						mockValue = DocUtil.resolveAnnotationValue(classLoader, annotationDefaultVal);
					}
					paramName = getParamName(classLoader, paramName, annotation);
					AnnotationValue annotationRequired = annotation.getProperty(requiredProp);
					if (Objects.nonNull(annotationRequired)) {
						strRequired = annotationRequired.toString();
					}
					else {
						strRequired = "true";
					}
				}
				if (JavaClassValidateUtil.isJSR303Required(annotationName)) {
					strRequired = "true";
				}
				if (frameworkAnnotations.getRequestBodyAnnotation().getAnnotationName().equals(annotationName)) {
					// if (requestBodyCounter > 0) {
					// throw new RuntimeException("You have use @RequestBody Passing
					// multiple variables for method "
					// + javaMethod.getName() + " in " + className + ",@RequestBody
					// annotation could only bind one variables.");
					// }
					mockValue = JsonBuildHelper.buildJson(fullyQualifiedName, typeName, Boolean.FALSE, 0,
							new HashMap<>(16), groupClasses, docJavaMethod.getJsonViewClasses(), builder);
					requestBodyCounter++;
					isRequestBody = true;
				}
				required = Boolean.parseBoolean(strRequired);
			}
			comment.append(JavaFieldUtil.getJsrComment(isShowValidation, classLoader, annotations));
			if (requestFieldToUnderline && !isPathVariable) {
				paramName = StringUtil.camelToUnderline(paramName);
			}
			// file upload
			if (JavaClassValidateUtil.isFile(typeName)) {
				ApiParam param = ApiParam.of()
					.setField(paramName)
					.setType(ParamTypeConstants.PARAM_TYPE_FILE)
					.setId(paramList.size() + 1)
					.setQueryParam(false)
					.setRequired(required)
					.setVersion(DocGlobalConstants.DEFAULT_VERSION)
					.setDesc(comment.toString());
				if (typeName.contains("[]") || typeName.endsWith(">")) {
					comment.append("(array of file)");
					param.setType(ParamTypeConstants.PARAM_TYPE_FILE);
					param.setDesc(comment.toString());
					param.setHasItems(true);
				}
				paramList.add(param);
				continue;
			}

			boolean queryParam = Methods.GET.getValue().equals(docJavaMethod.getMethodType())
					&& Methods.DELETE.getValue().equals(docJavaMethod.getMethodType()) && !isRequestBody
					&& !isPathVariable;
			if (JavaClassValidateUtil.isCollection(fullyQualifiedName)
					|| JavaClassValidateUtil.isArray(fullyQualifiedName)) {
				String[] gicNameArr = DocClassUtil.getSimpleGicName(typeName);
				String gicName = gicNameArr[0];
				if (JavaClassValidateUtil.isArray(gicName)) {
					gicName = gicName.substring(0, gicName.indexOf("["));
				}
				// handle array and list mock value
				mockValue = JavaFieldUtil.createMockValue(paramsComments, paramName, gicName, gicName);
				if (StringUtil.isNotEmpty(mockValue) && !mockValue.contains(",")) {
					mockValue = StringUtils.join(mockValue, ",",
							JavaFieldUtil.createMockValue(paramsComments, paramName, gicName, gicName));
				}
				JavaClass gicJavaClass = builder.getJavaProjectBuilder().getClassByName(gicName);
				if (gicJavaClass.isEnum()) {
					Object value = JavaClassUtil.getEnumValue(gicJavaClass, builder, Boolean.TRUE);
					ApiParam param = ApiParam.of()
						.setField(paramName)
						.setDesc(comment + ",[array of enum]")
						.setRequired(required)
						.setPathParam(isPathVariable)
						.setQueryParam(queryParam)
						.setId(paramList.size() + 1)
						.setEnumValues(JavaClassUtil.getEnumValues(gicJavaClass))
						.setEnumInfo(JavaClassUtil.getEnumInfo(gicJavaClass, builder))
						.setType(ParamTypeConstants.PARAM_TYPE_ARRAY)
						.setValue(String.valueOf(value));
					paramList.add(param);
					if (requestBodyCounter > 0) {
						Map<String, Object> map = OpenApiSchemaUtil.arrayTypeSchema(gicName);
						docJavaMethod.setRequestSchema(map);
					}
				}
				else if (JavaClassValidateUtil.isPrimitive(gicName)) {
					String shortSimple = DocClassUtil.processTypeNameForParams(gicName);
					ApiParam param = ApiParam.of()
						.setField(paramName)
						.setDesc(comment + ",[array of " + shortSimple + "]")
						.setRequired(required)
						.setPathParam(isPathVariable)
						.setQueryParam(queryParam)
						.setId(paramList.size() + 1)
						.setType(ParamTypeConstants.PARAM_TYPE_ARRAY)
						.setVersion(DocGlobalConstants.DEFAULT_VERSION)
						.setValue(mockValue);
					paramList.add(param);
					if (requestBodyCounter > 0) {
						Map<String, Object> map = OpenApiSchemaUtil.arrayTypeSchema(gicName);
						docJavaMethod.setRequestSchema(map);
					}
				}
				else if (JavaClassValidateUtil.isFile(gicName)) {
					// file upload
					ApiParam param = ApiParam.of()
						.setField(paramName)
						.setType(ParamTypeConstants.PARAM_TYPE_FILE)
						.setId(paramList.size() + 1)
						.setQueryParam(false)
						.setRequired(required)
						.setVersion(DocGlobalConstants.DEFAULT_VERSION)
						.setHasItems(true)
						.setDesc(comment + "(array of file)");
					paramList.add(param);
				}
				else {
					if (requestBodyCounter > 0 || !isRequestParam) {
						// for json
						paramList.addAll(ParamsBuildHelper.buildParams(gicNameArr[0], DocGlobalConstants.EMPTY, 0,
								String.valueOf(required), Boolean.FALSE, new HashMap<>(16), builder, groupClasses,
								docJavaMethod.getJsonViewClasses(), 0, Boolean.TRUE, null));
					}
				}
			}
			else if (JavaClassValidateUtil.isPrimitive(fullyQualifiedName)) {
				ApiParam param = ApiParam.of()
					.setField(paramName)
					.setType(DocClassUtil.processTypeNameForParams(simpleName))
					.setId(paramList.size() + 1)
					.setPathParam(isPathVariable)
					.setQueryParam(queryParam)
					.setValue(mockValue)
					.setDesc(comment.toString())
					.setRequired(required)
					.setVersion(DocGlobalConstants.DEFAULT_VERSION);
				paramList.add(param);
				if (requestBodyCounter > 0) {
					Map<String, Object> map = OpenApiSchemaUtil.primaryTypeSchema(simpleName);
					docJavaMethod.setRequestSchema(map);
				}
			}
			else if (JavaClassValidateUtil.isMap(fullyQualifiedName)) {
				log.warning("When using smart-doc, it is not recommended to use Map to receive parameters, Check it in "
						+ javaMethod.getDeclaringClass().getCanonicalName() + "#" + javaMethod.getName());
				// is map without Gic
				if (JavaClassValidateUtil.isMap(typeName)) {
					ApiParam apiParam = ApiParam.of()
						.setField(paramName)
						.setType(ParamTypeConstants.PARAM_TYPE_MAP)
						.setId(paramList.size() + 1)
						.setPathParam(isPathVariable)
						.setQueryParam(queryParam)
						.setDesc(comment.toString())
						.setRequired(required)
						.setVersion(DocGlobalConstants.DEFAULT_VERSION);
					paramList.add(apiParam);
					if (requestBodyCounter > 0) {
						Map<String, Object> map = OpenApiSchemaUtil.mapTypeSchema("object");
						docJavaMethod.setRequestSchema(map);
					}
					continue;
				}
				String[] gicNameArr = DocClassUtil.getSimpleGicName(typeName);

				// get map key class
				JavaClass mapKeyClass = builder.getClassByName(gicNameArr[0]);

				if (JavaClassValidateUtil.isPrimitive(gicNameArr[1])) {
					ApiParam apiParam = ApiParam.of()
						.setField(paramName)
						.setType(ParamTypeConstants.PARAM_TYPE_MAP)
						.setId(paramList.size() + 1)
						.setPathParam(isPathVariable)
						.setQueryParam(queryParam)
						.setDesc(comment.toString())
						.setRequired(required)
						.setVersion(DocGlobalConstants.DEFAULT_VERSION);
					paramList.add(apiParam);
					if (requestBodyCounter > 0) {
						Map<String, Object> map = OpenApiSchemaUtil.mapTypeSchema(gicNameArr[1]);
						docJavaMethod.setRequestSchema(map);
					}
				}
				else if (Objects.nonNull(mapKeyClass) && mapKeyClass.isEnum()
						&& !mapKeyClass.getEnumConstants().isEmpty()) {
					for (JavaField enumConstant : mapKeyClass.getEnumConstants()) {
						ApiParam apiParam = ApiParam.of()
							.setField(enumConstant.getName())
							.setType(ParamTypeConstants.PARAM_TYPE_OBJECT)
							.setClassName(gicNameArr[1])
							.setDesc(Optional.ofNullable(builder.getClassByName(gicNameArr[1]))
								.map(JavaClass::getComment)
								.orElse("A map key."))
							.setVersion(DocGlobalConstants.DEFAULT_VERSION)
							.setId(paramList.size() + 1);
						paramList.add(apiParam);
						paramList.addAll(ParamsBuildHelper.buildParams(gicNameArr[1], DocGlobalConstants.PARAM_PREFIX,
								1, String.valueOf(required), Boolean.FALSE, new HashMap<>(16), builder, groupClasses,
								docJavaMethod.getJsonViewClasses(), apiParam.getId(), Boolean.FALSE, null));
					}
				}
				else {
					paramList.addAll(ParamsBuildHelper.buildParams(gicNameArr[1], DocGlobalConstants.EMPTY, 0,
							String.valueOf(required), Boolean.FALSE, new HashMap<>(16), builder, groupClasses,
							docJavaMethod.getJsonViewClasses(), 0, Boolean.FALSE, null));
				}

			}
			// param is enum
			else if (javaClass.isEnum()) {
				String enumName = JavaClassUtil.getEnumParams(javaClass);
				Object value = JavaClassUtil.getEnumValue(javaClass, builder, isPathVariable || queryParam);
				if (Boolean.TRUE.equals(builder.getApiConfig().getInlineEnum())) {
					comment.append("<br/>[Enum: ").append(StringUtil.removeQuotes(enumName)).append("]");
				}
				ApiParam param = ApiParam.of()
					.setField(paramName)
					.setId(paramList.size() + 1)
					.setPathParam(isPathVariable)
					.setQueryParam(queryParam)
					.setValue(StringUtil.removeDoubleQuotes(String.valueOf(value)))
					.setType(ParamTypeConstants.PARAM_TYPE_ENUM)
					.setDesc(comment.toString())
					.setRequired(required)
					.setVersion(DocGlobalConstants.DEFAULT_VERSION)
					.setEnumInfo(JavaClassUtil.getEnumInfo(javaClass, builder))
					.setEnumValues(JavaClassUtil.getEnumValues(javaClass));
				paramList.add(param);
			}
			// if it has annotation @RequestPart
			else if (isRequestPart) {
				ApiParam param = ApiParam.of()
					.setClassName(fullyQualifiedName)
					.setField(paramName)
					.setId(paramList.size() + 1)
					.setPathParam(isPathVariable)
					.setQueryParam(queryParam)
					.setValue(mockValue)
					.setType(ParamTypeConstants.PARAM_TYPE_OBJECT)
					.setDesc(comment.toString())
					.setRequired(required)
					.setVersion(DocGlobalConstants.DEFAULT_VERSION);
				paramList.add(param);
				paramList.addAll(ParamsBuildHelper.buildParams(typeName, DocGlobalConstants.PARAM_PREFIX, 1,
						String.valueOf(required), Boolean.FALSE, new HashMap<>(16), builder, groupClasses,
						docJavaMethod.getJsonViewClasses(), 1, Boolean.FALSE, null));
			}
			else {
				paramList.addAll(ParamsBuildHelper.buildParams(typeName, DocGlobalConstants.EMPTY, 0,
						String.valueOf(required), Boolean.FALSE, new HashMap<>(16), builder, groupClasses,
						docJavaMethod.getJsonViewClasses(), 0, Boolean.FALSE, null));
			}
		}
		return ApiParamTreeUtil.buildMethodReqParam(paramList, queryReqParamMap, pathReqParamMap,
				docJavaMethod.getMethodType());
	}

	/**
	 * build request json
	 * @param javaMethod JavaMethod
	 * @param apiMethodDoc ApiMethodDoc
	 * @param configBuilder ProjectDocConfigBuilder
	 * @param frameworkAnnotations FrameworkAnnotations
	 * @return ApiRequestExample
	 */
	default ApiRequestExample buildReqJson(DocJavaMethod javaMethod, ApiMethodDoc apiMethodDoc,
			ProjectDocConfigBuilder configBuilder, FrameworkAnnotations frameworkAnnotations) {
		String methodType = apiMethodDoc.getType();
		JavaMethod method = javaMethod.getJavaMethod();
		Map<String, String> pathParamsMap = new LinkedHashMap<>();
		Map<String, String> queryParamsMap = new LinkedHashMap<>();
		ClassLoader classLoader = configBuilder.getApiConfig().getClassLoader();
		apiMethodDoc.getPathParams()
			.stream()
			.filter(Objects::nonNull)
			.filter(p -> StringUtil.isNotEmpty(p.getValue()) || p.isConfigParam())
			.forEach(param -> pathParamsMap.put(param.getSourceField(), param.getValue()));
		apiMethodDoc.getQueryParams()
			.stream()
			.filter(Objects::nonNull)
			.filter(p -> StringUtil.isNotEmpty(p.getValue()) || p.isConfigParam())
			.forEach(param -> queryParamsMap.put(param.getSourceField(), param.getValue()));
		List<JavaAnnotation> methodAnnotations = method.getAnnotations();
		Map<String, MappingAnnotation> mappingAnnotationMap = frameworkAnnotations.getMappingAnnotations();
		for (JavaAnnotation annotation : methodAnnotations) {
			String annotationName = annotation.getType().getName();
			MappingAnnotation mappingAnnotation = mappingAnnotationMap.get(annotationName);
			if (Objects.nonNull(mappingAnnotation) && StringUtil.isNotEmpty(mappingAnnotation.getParamsProp())) {
				Object paramsObjects = annotation.getNamedParameter(mappingAnnotation.getParamsProp());
				if (Objects.isNull(paramsObjects)) {
					continue;
				}
				String params = StringUtil.removeQuotes(paramsObjects.toString());
				if (!params.startsWith("[")) {
					mappingParamProcess(paramsObjects.toString(), queryParamsMap);
					continue;
				}
				@SuppressWarnings("unchecked")
				List<String> headers = (LinkedList<String>) paramsObjects;
				for (String str : headers) {
					mappingParamProcess(str, queryParamsMap);
				}
			}
		}
		List<DocJavaParameter> parameterList = getJavaParameterList(configBuilder, javaMethod, frameworkAnnotations);
		List<ApiReqParam> reqHeaderList = apiMethodDoc.getRequestHeaders();
		if (parameterList.isEmpty()) {
			String path = apiMethodDoc.getPath().split(";")[0];
			path = DocUtil.formatAndRemove(path, pathParamsMap);
			String url = UrlUtil.urlJoin(path, queryParamsMap);
			url = StringUtil.removeQuotes(url);
			url = apiMethodDoc.getServerUrl() + "/" + url;
			url = UrlUtil.simplifyUrl(url);
			CurlRequest curlRequest = CurlRequest.builder()
				.setContentType(apiMethodDoc.getContentType())
				.setType(methodType)
				.setReqHeaders(reqHeaderList)
				.setUrl(url);
			String format = CurlUtil.toCurl(curlRequest);
			return ApiRequestExample.builder().setUrl(apiMethodDoc.getUrl()).setExampleBody(format);
		}
		boolean requestFieldToUnderline = configBuilder.getApiConfig().isRequestFieldToUnderline();
		Map<String, String> paramsComments = DocUtil.getCommentsByTag(method, DocTags.PARAM, null);
		List<String> mvcRequestAnnotations = this.listMvcRequestAnnotations();
		List<FormData> formDataList = new ArrayList<>();
		ApiRequestExample requestExample = ApiRequestExample.builder();
		out: for (DocJavaParameter apiParameter : parameterList) {
			JavaParameter parameter = apiParameter.getJavaParameter();
			String paramName = parameter.getName();
			String genericFullyQualifiedName = apiParameter.getGenericFullyQualifiedName();
			String fullyQualifiedName = apiParameter.getFullyQualifiedName();
			String gicTypeName = apiParameter.getGenericCanonicalName();
			String simpleTypeName = apiParameter.getTypeValue();
			JavaClass javaClass = configBuilder.getJavaProjectBuilder().getClassByName(genericFullyQualifiedName);
			String[] globGicName = DocClassUtil.getSimpleGicName(gicTypeName);
			String comment = this.paramCommentResolve(paramsComments.get(paramName));
			String mockValue = JavaFieldUtil.createMockValue(paramsComments, paramName, gicTypeName, simpleTypeName);
			if (queryParamsMap.containsKey(paramName)) {
				mockValue = queryParamsMap.get(paramName);
			}
			if (requestFieldToUnderline) {
				paramName = StringUtil.camelToUnderline(paramName);
			}
			List<JavaAnnotation> annotations = parameter.getAnnotations();
			Set<String> groupClasses = JavaClassUtil.getParamGroupJavaClass(annotations,
					configBuilder.getJavaProjectBuilder());
			boolean paramAdded = false;
			boolean requestParam = annotations.isEmpty();
			for (JavaAnnotation annotation : annotations) {
				String annotationName = annotation.getType().getValue();
				String fullName = annotation.getType().getSimpleName();
				if (!mvcRequestAnnotations.contains(fullName) || paramAdded) {
					continue;
				}
				if (ignoreMvcParamWithAnnotation(annotationName)) {
					continue out;
				}

				AnnotationValue annotationDefaultVal = annotation
					.getProperty(DocAnnotationConstants.DEFAULT_VALUE_PROP);

				if (Objects.nonNull(annotationDefaultVal)) {
					mockValue = DocUtil.resolveAnnotationValue(classLoader, annotationDefaultVal);
				}
				paramName = getParamName(classLoader, paramName, annotation);
				if (frameworkAnnotations.getRequestBodyAnnotation().getAnnotationName().equals(annotationName)) {
					// priority use mapping annotation's consumer value
					if (apiMethodDoc.getContentType().equals(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
						apiMethodDoc.setContentType(MediaType.APPLICATION_JSON);
					}
					boolean isArrayOrCollection = false;
					if (JavaClassValidateUtil.isArray(fullyQualifiedName)
							|| JavaClassValidateUtil.isCollection(fullyQualifiedName)) {
						simpleTypeName = globGicName[0];
						isArrayOrCollection = true;
					}

					if (JavaClassValidateUtil.isPrimitive(simpleTypeName)) {
						if (isArrayOrCollection) {
							if (StringUtil.isNotEmpty(mockValue)) {
								mockValue = "[" + mockValue + "]";
							}
							else {
								mockValue = "[" + DocUtil.getValByTypeAndFieldName(simpleTypeName, paramName) + "]";
							}
							mockValue = JsonUtil.toPrettyFormat(mockValue);
						}
						requestExample.setJsonBody(mockValue).setJson(true);
					}
					else {
						String json = JsonBuildHelper.buildJson(fullyQualifiedName, gicTypeName, Boolean.FALSE, 0,
								new HashMap<>(16), groupClasses, Collections.emptySet(), configBuilder);
						requestExample.setJsonBody(JsonUtil.toPrettyFormat(json)).setJson(true);
					}
					queryParamsMap.remove(paramName);
					paramAdded = true;
				}
				else if (frameworkAnnotations.getPathVariableAnnotation()
					.getAnnotationName()
					.contains(annotationName)) {
					if (javaClass.isEnum()) {
						Object value = JavaClassUtil.getEnumValue(javaClass, configBuilder, Boolean.TRUE);
						mockValue = StringUtil.removeQuotes(String.valueOf(value));
					}
					if (pathParamsMap.containsKey(paramName)) {
						mockValue = pathParamsMap.get(paramName);
					}
					pathParamsMap.put(paramName, mockValue);
					paramAdded = true;
				}
				else if (frameworkAnnotations.getRequestParamAnnotation()
					.getAnnotationName()
					.contains(annotationName)) {
					if (javaClass.isEnum()) {
						Object value = JavaClassUtil.getEnumValue(javaClass, configBuilder, Boolean.TRUE);
						mockValue = StringUtil.removeQuotes(String.valueOf(value));
					}
					if (queryParamsMap.containsKey(paramName)) {
						mockValue = queryParamsMap.get(paramName);
					}
					if (JavaClassValidateUtil.isPrimitive(simpleTypeName)) {
						requestExample.addJsonBody(mockValue);
					}
					if (JavaClassValidateUtil.isFile(fullyQualifiedName)) {
						break;
					}
					// array and list
					queryParamsMap.put(paramName, mockValue);
					requestParam = true;
					paramAdded = true;
				}
				else if (frameworkAnnotations.getRequestPartAnnotation().getAnnotationName().contains(annotationName)) {
					if (!JavaClassValidateUtil.isFile(gicTypeName)) {
						apiMethodDoc.setContentType(MediaType.MULTIPART_FORM_DATA);
						FormData formData = new FormData();
						formData.setKey(paramName);
						formData.setDescription(comment);
						formData.setType(ParamTypeConstants.PARAM_TYPE_TEXT);
						mockValue = JsonBuildHelper.buildJson(fullyQualifiedName, gicTypeName, Boolean.FALSE, 0,
								new HashMap<>(16), groupClasses, Collections.emptySet(), configBuilder);
						formData.setValue(mockValue);
						formData.setContentType(FormDataContentTypeEnum.APPLICATION_JSON);
						formDataList.add(formData);
						paramAdded = true;
					}
				}
			}
			if (paramAdded) {
				continue;
			}
			// file upload
			if (JavaClassValidateUtil.isFile(gicTypeName)) {
				apiMethodDoc.setContentType(MediaType.MULTIPART_FORM_DATA);
				FormData formData = new FormData();
				formData.setKey(paramName);
				formData.setType(ParamTypeConstants.PARAM_TYPE_FILE);
				if (fullyQualifiedName.contains("[]") || fullyQualifiedName.endsWith(">")) {
					comment = comment + "(array of file)";
					formData.setType(ParamTypeConstants.PARAM_TYPE_FILE);
					formData.setHasItems(true);
				}
				formData.setDescription(comment);
				formData.setValue(mockValue);
				formData.setSrc(new ArrayList<>(0));
				formDataList.add(formData);
			}
			else if (JavaClassValidateUtil.isPrimitive(fullyQualifiedName) && !requestParam) {
				FormData formData = new FormData();
				formData.setKey(paramName);
				formData.setDescription(comment);
				formData.setType(ParamTypeConstants.PARAM_TYPE_TEXT);
				formData.setValue(mockValue);
				formDataList.add(formData);
			}
			else if (JavaClassValidateUtil.isArray(fullyQualifiedName)
					|| JavaClassValidateUtil.isCollection(fullyQualifiedName)) {
				String gicName = globGicName[0];
				if (JavaClassValidateUtil.isArray(gicName)) {
					gicName = gicName.substring(0, gicName.indexOf("["));
				}
				if (!JavaClassValidateUtil.isPrimitive(gicName)
						&& !configBuilder.getJavaProjectBuilder().getClassByName(gicName).isEnum() && requestParam) {
					throw new RuntimeException("can't support binding Collection on method " + method.getName()
							+ " Check it in " + method.getDeclaringClass().getCanonicalName());
				}
				String value;
				JavaClass javaClass1 = configBuilder.getClassByName(gicName);
				if (Objects.nonNull(javaClass1) && javaClass1.isEnum()) {
					value = String.valueOf(JavaClassUtil.getEnumValue(javaClass1, configBuilder, Boolean.TRUE));
				}
				else {
					value = RandomUtil.randomValueByType(gicName);
				}
				FormData formData = new FormData();
				formData.setKey(paramName);
				if (!paramName.contains("[]")) {
					formData.setKey(paramName + "[]");
				}
				formData.setDescription(comment);
				formData.setType(ParamTypeConstants.PARAM_TYPE_TEXT);
				formData.setValue(value);
				formDataList.add(formData);
			}
			else if (javaClass.isEnum()) {
				// do nothing
				Object value = JavaClassUtil.getEnumValue(javaClass, configBuilder, Boolean.TRUE);
				String strVal = StringUtil.removeQuotes(String.valueOf(value));
				FormData formData = new FormData();
				formData.setKey(paramName);
				formData.setType(ParamTypeConstants.PARAM_TYPE_TEXT);
				formData.setDescription(comment);
				formData.setValue(strVal);
				formDataList.add(formData);
			}
			else {
				formDataList.addAll(FormDataBuildHelper.getFormData(gicTypeName, new HashMap<>(16), 0, configBuilder,
						DocGlobalConstants.EMPTY, groupClasses));
			}
		}

		// set content-type to fromData
		boolean hasFormDataUploadFile = formDataList.stream()
			.anyMatch(form -> Objects.equals(form.getType(), ParamTypeConstants.PARAM_TYPE_FILE)
					|| Objects.nonNull(form.getContentType()));
		if (hasFormDataUploadFile) {
			apiMethodDoc.setContentType(MediaType.MULTIPART_FORM_DATA_VALUE);
		}
		requestExample.setFormDataList(formDataList);
		// curl send file to convert
		final Map<String, String> formDataToMap = DocUtil.formDataToMap(formDataList);
		// formData add to params '--data'
		queryParamsMap.putAll(formDataToMap);
		// set example body
		RequestExampleUtil.setExampleBody(apiMethodDoc, requestExample, formDataList, pathParamsMap, queryParamsMap);
		return requestExample;
	}

	/**
	 * Determines if the given Java class is a default entry point based on its
	 * annotations and the provided framework annotations.
	 * @param cls the Java class to check
	 * @param frameworkAnnotations the framework annotations to use for the check
	 * @return {@code true} if the class is a default entry point, {@code false} otherwise
	 */
	default boolean defaultEntryPoint(JavaClass cls, FrameworkAnnotations frameworkAnnotations) {
		// Check if the class is an annotation or an enum, return false if it is
		if (cls.isAnnotation() || cls.isEnum()) {
			return false;
		}

		// Check if frameworkAnnotations is null, return false if it is
		if (Objects.isNull(frameworkAnnotations)) {
			return false;
		}

		// Get framework entry annotations
		Map<String, EntryAnnotation> entryAnnotationMap = frameworkAnnotations.getEntryAnnotations();

		// Check if entry annotations are null, return false if they are
		if (Objects.isNull(frameworkAnnotations.getEntryAnnotations())) {
			return false;
		}

		// Get class annotations; Note: Spring Entry Annotation is not supported for
		// superclass inheritance
		List<JavaAnnotation> classAnnotations = cls.getAnnotations();

		// Check if any of the class annotations match the entry annotations
		return classAnnotations.stream().anyMatch(annotation -> {
			String name = annotation.getType().getValue();
			return entryAnnotationMap.containsKey(name);
		});
	}

	/**
	 * Determines if the given Java class is an exception advice entry point based on its
	 * annotations and the provided framework annotations.
	 * @param cls the Java class to check
	 * @param frameworkAnnotations the framework annotations to use for the check
	 * @return {@code true} if the class is an exception advice entry point, {@code false}
	 * otherwise
	 */
	default boolean defaultExceptionAdviceEntryPoint(JavaClass cls, FrameworkAnnotations frameworkAnnotations) {
		if (cls.isAnnotation() || cls.isEnum()) {
			return false;
		}
		if (Objects.isNull(frameworkAnnotations)) {
			return false;
		}
		List<JavaAnnotation> classAnnotations = DocClassUtil.getAnnotations(cls);
		if (Objects.isNull(frameworkAnnotations.getExceptionAdviceAnnotations())) {
			return false;
		}
		Map<String, ExceptionAdviceAnnotation> exceptionAdviceAnnotationMap = frameworkAnnotations
			.getExceptionAdviceAnnotations();
		return classAnnotations.stream().anyMatch(annotation -> {
			String name = annotation.getType().getValue();
			return exceptionAdviceAnnotationMap.containsKey(name);
		});
	}

	/**
	 * Get the list of parent class methods for a given Java class.
	 * @param apiConfig the API configuration
	 * @param projectBuilder the project builder
	 * @param cls the Java class
	 * @return the list of parent class methods
	 */
	default List<DocJavaMethod> getParentsClassMethods(ApiConfig apiConfig, ProjectDocConfigBuilder projectBuilder,
			JavaClass cls) {
		List<DocJavaMethod> docJavaMethods = new ArrayList<>();
		JavaClass parentClass = cls.getSuperJavaClass();
		if (Objects.nonNull(parentClass) && !JavaTypeConstants.OBJECT_SIMPLE_NAME.equals(parentClass.getSimpleName())) {
			Map<String, JavaType> actualTypesMap = JavaClassUtil.getActualTypesMap(parentClass);
			List<JavaMethod> parentMethodList = parentClass.getMethods();
			for (JavaMethod method : parentMethodList) {
				docJavaMethods.add(convertToDocJavaMethod(apiConfig, projectBuilder, method, actualTypesMap));
			}
			docJavaMethods.addAll(getParentsClassMethods(apiConfig, projectBuilder, parentClass));
		}
		return docJavaMethods;
	}

	/**
	 * Convert a Java method to a DocJavaMethod object.
	 * @param apiConfig the API configuration
	 * @param projectBuilder the project builder
	 * @param method the Java method to convert
	 * @param actualTypesMap the actual types map
	 * @return the converted DocJavaMethod object
	 */
	default DocJavaMethod convertToDocJavaMethod(ApiConfig apiConfig, ProjectDocConfigBuilder projectBuilder,
			JavaMethod method, Map<String, JavaType> actualTypesMap) {
		JavaClass cls = method.getDeclaringClass();
		String clzName = cls.getCanonicalName();
		if (StringUtil.isEmpty(method.getComment()) && apiConfig.isStrict()) {
			throw new RuntimeException(
					"Unable to find comment for method " + method.getName() + " in " + cls.getCanonicalName());
		}
		String classAuthor = JavaClassUtil.getClassTagsValue(cls, DocTags.AUTHOR, Boolean.TRUE);
		DocJavaMethod docJavaMethod = DocJavaMethod.builder().setJavaMethod(method).setActualTypesMap(actualTypesMap);
		if (Objects.nonNull(method.getTagByName(DocTags.DOWNLOAD))) {
			docJavaMethod.setDownload(true);
		}
		DocletTag pageTag = method.getTagByName(DocTags.PAGE);
		if (Objects.nonNull(method.getTagByName(DocTags.PAGE))) {
			String pageUrl = projectBuilder.getServerUrl() + "/" + pageTag.getValue();
			docJavaMethod.setPage(UrlUtil.simplifyUrl(pageUrl));
		}

		DocletTag docletTag = method.getTagByName(DocTags.GROUP);
		if (Objects.nonNull(docletTag)) {
			docJavaMethod.setGroup(docletTag.getValue());
		}
		docJavaMethod.setParamTagMap(DocUtil.getCommentsByTag(method, DocTags.PARAM, clzName));
		docJavaMethod.setParamsComments(DocUtil.getCommentsByTag(method, DocTags.PARAM, null));

		Map<String, String> authorMap = DocUtil.getCommentsByTag(method, DocTags.AUTHOR, cls.getName());
		String authorValue = String.join(", ", new ArrayList<>(authorMap.keySet()));
		if (apiConfig.isShowAuthor() && StringUtil.isNotEmpty(authorValue)) {
			docJavaMethod.setAuthor(JsonUtil.toPrettyFormat(authorValue));
		}
		if (apiConfig.isShowAuthor() && StringUtil.isEmpty(authorValue)) {
			docJavaMethod.setAuthor(classAuthor);
		}

		String comment = DocUtil.getEscapeAndCleanComment(method.getComment());
		docJavaMethod.setDesc(comment);
		String version = DocUtil.getNormalTagComments(method, DocTags.SINCE, cls.getName());
		docJavaMethod.setVersion(version);

		String apiNoteValue = DocUtil.getNormalTagComments(method, DocTags.API_NOTE, cls.getName());
		if (StringUtil.isEmpty(apiNoteValue)) {
			apiNoteValue = method.getComment();
		}
		docJavaMethod.setDetail(apiNoteValue != null ? apiNoteValue : "");

		// set jsonViewClasses
		method.getAnnotations()
			.stream()
			.filter(annotation -> DocAnnotationConstants.SHORT_JSON_VIEW.equals(annotation.getType().getValue()))
			.findFirst()
			.ifPresent(annotation -> docJavaMethod
				.setJsonViewClasses(JavaClassUtil.getJsonViewClasses(annotation, projectBuilder, false)));

		return docJavaMethod;
	}

	/**
	 * Determines whether the provided Java class is an entry point for RESTful APIs.
	 * @param javaClass The Java class to be evaluated.
	 * @param frameworkAnnotations Annotations provided by the framework, which may
	 * include information about the entry point or other metadata.
	 * @return True if the Java class is an entry point for RESTful APIs, false otherwise.
	 */
	boolean isEntryPoint(JavaClass javaClass, FrameworkAnnotations frameworkAnnotations);

	/**
	 * Unified exception handling entry for RESTful APIs.
	 * @param javaClass The Java class associated with the API endpoint.
	 * @param frameworkAnnotations Annotations provided by the framework, which may
	 * include information about exception handling or other metadata.
	 * @return The processed result, typically a response tailored for the RESTful API
	 * context.
	 */
	boolean isExceptionAdviceEntryPoint(JavaClass javaClass, FrameworkAnnotations frameworkAnnotations);

	/**
	 * List of annotations that indicate the entry point of a RESTful API.
	 * @return A list of annotations that indicate the entry point of a RESTful API.
	 */
	List<String> listMvcRequestAnnotations();

	/**
	 * Post-process the Java method associated with a RESTful API endpoint.
	 * @param javaClass The Java class associated with the API endpoint.
	 * @param method The Java method associated with the API endpoint.
	 * @param requestMapping The RequestMapping annotation associated with the API
	 * endpoint.
	 */
	void requestMappingPostProcess(JavaClass javaClass, JavaMethod method, RequestMapping requestMapping);

	/**
	 * Determine whether a parameter should be ignored in a RESTful API endpoint.
	 * @param annotation The annotation associated with the parameter.
	 * @return True if the parameter should be ignored, false otherwise.
	 */
	boolean ignoreMvcParamWithAnnotation(String annotation);

	/**
	 * Process the Java method associated with an exception handler in a RESTful API.
	 * @param method The Java method associated with the exception handler.
	 * @return The processed result, typically a response tailored for the exception
	 * handler.
	 */
	ExceptionAdviceMethod processExceptionAdviceMethod(JavaMethod method);

	/**
	 * Default HTTP error statuses for RESTful APIs.
	 * @return A list of default HTTP error statuses.
	 */
	List<ApiExceptionStatus> defaultHttpErrorStatuses();

}
