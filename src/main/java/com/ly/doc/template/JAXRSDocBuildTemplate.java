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
import com.ly.doc.handler.*;
import com.ly.doc.helper.FormDataBuildHelper;
import com.ly.doc.helper.JsonBuildHelper;
import com.ly.doc.helper.ParamsBuildHelper;
import com.ly.doc.model.*;
import com.ly.doc.model.annotation.EntryAnnotation;
import com.ly.doc.model.annotation.FrameworkAnnotations;
import com.ly.doc.model.annotation.HeaderAnnotation;
import com.ly.doc.model.request.ApiRequestExample;
import com.ly.doc.model.request.CurlRequest;
import com.ly.doc.model.request.JaxrsPathMapping;
import com.ly.doc.model.request.RequestMapping;
import com.ly.doc.utils.*;
import com.power.common.util.CollectionUtil;
import com.power.common.util.RandomUtil;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.model.*;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Build documents for JAX RS.
 *
 * @author Zxq
 * @since 2021/7/15
 */
public class JAXRSDocBuildTemplate implements IDocBuildTemplate<ApiDoc>, IWebSocketDocBuildTemplate<WebSocketDoc>,
		IRestDocTemplate, IWebSocketTemplate {

	private static final Logger log = Logger.getLogger(JAXRSDocBuildTemplate.class.getName());

	/**
	 * headers
	 */
	private List<ApiReqParam> headers;

	@Override
	public boolean supportsFramework(String framework) {
		return FrameworkEnum.JAX_RS.getFramework().equalsIgnoreCase(framework);
	}

	@Override
	public ApiSchema<ApiDoc> renderApi(ProjectDocConfigBuilder projectBuilder, Collection<JavaClass> candidateClasses) {
		ApiConfig apiConfig = projectBuilder.getApiConfig();
		this.headers = apiConfig.getRequestHeaders();
		List<ApiReqParam> configApiReqParams = Stream.of(apiConfig.getRequestHeaders(), apiConfig.getRequestParams())
			.filter(Objects::nonNull)
			.flatMap(Collection::stream)
			.collect(Collectors.toList());
		FrameworkAnnotations frameworkAnnotations = this.registeredAnnotations();
		return this.processApiData(projectBuilder, frameworkAnnotations, configApiReqParams,
				new SpringMVCRequestMappingHandler(), new SpringMVCRequestHeaderHandler(), candidateClasses);
	}

	@Override
	public List<ApiMethodDoc> buildEntryPointMethod(JavaClass cls, ApiConfig apiConfig,
			ProjectDocConfigBuilder projectBuilder, FrameworkAnnotations frameworkAnnotations,
			List<ApiReqParam> configApiReqParams, IRequestMappingHandler baseMappingHandler,
			IHeaderHandler headerHandler) {
		return buildControllerMethod(cls, apiConfig, projectBuilder, frameworkAnnotations);
	}

	@Override
	public List<WebSocketDoc> renderWebSocketApi(ProjectDocConfigBuilder projectBuilder,
			Collection<JavaClass> candidateClasses) {
		FrameworkAnnotations frameworkAnnotations = registeredAnnotations();
		return processWebSocketData(projectBuilder, frameworkAnnotations, DefaultWebSocketRequestHandler.getInstance(),
				candidateClasses);
	}

	/**
	 * Analyze resource method
	 * @param cls cls
	 * @param apiConfig apiConfig
	 * @param projectBuilder projectBuilder
	 * @return List<ApiMethodDoc>
	 */
	@SuppressWarnings("deprecation")
	private List<ApiMethodDoc> buildControllerMethod(final JavaClass cls, ApiConfig apiConfig,
			ProjectDocConfigBuilder projectBuilder, FrameworkAnnotations frameworkAnnotations) {
		String clzName = cls.getCanonicalName();
		boolean paramsDataToTree = projectBuilder.getApiConfig().isParamsDataToTree();
		String group = JavaClassUtil.getClassTagsValue(cls, DocTags.GROUP, Boolean.TRUE);
		String baseUrl = "";
		String mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE;
		List<JavaAnnotation> classAnnotations = this.getClassAnnotations(cls, frameworkAnnotations);
		for (JavaAnnotation annotation : classAnnotations) {
			String annotationName = annotation.getType().getFullyQualifiedName();
			if (JakartaJaxrsAnnotations.JAX_PATH_FULLY.equals(annotationName)
					|| JAXRSAnnotations.JAX_PATH_FULLY.equals(annotationName)) {
				ClassLoader classLoader = projectBuilder.getApiConfig().getClassLoader();
				baseUrl = StringUtil.removeQuotes(DocUtil.getRequestHeaderValue(classLoader, annotation));
			}
			// use first annotation's value
			if (annotationName.equals(JakartaJaxrsAnnotations.JAX_CONSUMES_FULLY)
					|| annotationName.equals(JAXRSAnnotations.JAX_CONSUMES_FULLY)) {
				Object value = annotation.getNamedParameter("value");
				if (Objects.nonNull(value)) {
					mediaType = MediaType.valueOf(value.toString());
				}
			}
		}

		Set<String> filterMethods = DocUtil.findFilterMethods(clzName);
		boolean needAllMethods = filterMethods.contains(DocGlobalConstants.DEFAULT_FILTER_METHOD);

		List<JavaMethod> methods = cls.getMethods();
		List<DocJavaMethod> docJavaMethods = new ArrayList<>(methods.size());
		// filter private method
		for (JavaMethod method : methods) {
			if (method.isPrivate()) {
				continue;
			}
			if (needAllMethods || filterMethods.contains(method.getName())) {
				docJavaMethods.add(convertToDocJavaMethod(apiConfig, projectBuilder, method, null));
			}
		}
		// add parent class methods
		docJavaMethods.addAll(getParentsClassMethods(apiConfig, projectBuilder, cls));
		List<ApiMethodDoc> methodDocList = new ArrayList<>(methods.size());
		int methodOrder = 0;
		for (DocJavaMethod docJavaMethod : docJavaMethods) {
			JavaMethod method = docJavaMethod.getJavaMethod();
			if (checkCondition(method)) {
				continue;
			}
			// new api doc
			// handle request mapping
			JaxrsPathMapping jaxPathMapping = new JaxrsPathHandler().handle(projectBuilder, baseUrl, method, mediaType);
			if (Objects.isNull(jaxPathMapping)) {
				continue;
			}
			ApiMethodDoc apiMethodDoc = new ApiMethodDoc();
			apiMethodDoc.setDownload(docJavaMethod.isDownload());
			apiMethodDoc.setPage(docJavaMethod.getPage());
			apiMethodDoc.setGroup(group);
			if (Objects.nonNull(docJavaMethod.getGroup())) {
				apiMethodDoc.setGroup(docJavaMethod.getGroup());
			}

			methodOrder++;
			apiMethodDoc.setName(method.getName());
			apiMethodDoc.setOrder(methodOrder);
			apiMethodDoc.setDesc(docJavaMethod.getDesc());
			String methodUid = DocUtil.generateId(clzName + method.getName() + methodOrder);
			apiMethodDoc.setMethodId(methodUid);
			apiMethodDoc.setAuthor(docJavaMethod.getAuthor());
			apiMethodDoc.setDetail(docJavaMethod.getDetail());
			List<ApiReqParam> apiReqParams = new JaxrsHeaderHandler().handle(method, projectBuilder);
			apiMethodDoc.setType(jaxPathMapping.getMethodType());
			apiMethodDoc.setUrl(jaxPathMapping.getUrl());
			apiMethodDoc.setServerUrl(projectBuilder.getServerUrl());
			apiMethodDoc.setPath(jaxPathMapping.getShortUrl());
			apiMethodDoc.setDeprecated(jaxPathMapping.isDeprecated());
			apiMethodDoc.setContentType(jaxPathMapping.getMediaType());

			// build request params
			ApiMethodReqParam apiMethodReqParam = requestParams(docJavaMethod, projectBuilder);
			apiMethodDoc.setPathParams(apiMethodReqParam.getPathParams());
			apiMethodDoc.setQueryParams(apiMethodReqParam.getQueryParams());
			apiMethodDoc.setRequestParams(apiMethodReqParam.getRequestParams());
			if (paramsDataToTree) {
				// convert to tree
				this.convertParamsDataToTree(apiMethodDoc);
			}
			List<ApiReqParam> allApiReqParams;
			allApiReqParams = apiReqParams;
			if (Objects.nonNull(this.headers)) {
				allApiReqParams = Stream.of(this.headers, apiReqParams)
					.flatMap(Collection::stream)
					.distinct()
					.collect(Collectors.toList());
			}
			allApiReqParams.removeIf(apiReqParam -> {
				if (StringUtil.isEmpty(apiReqParam.getPathPatterns())
						&& StringUtil.isEmpty(apiReqParam.getExcludePathPatterns())) {
					return false;
				}
				else {
					boolean flag = DocPathUtil.matches(jaxPathMapping.getShortUrl(), apiReqParam.getPathPatterns(),
							apiReqParam.getExcludePathPatterns());
					return !flag;
				}
			});
			// reduce create in template
			apiMethodDoc.setHeaders(this.createDocRenderHeaders(allApiReqParams, apiConfig.isAdoc()));
			apiMethodDoc.setRequestHeaders(allApiReqParams);

			// build request json
			ApiRequestExample requestExample = buildReqJson(docJavaMethod, apiMethodDoc, projectBuilder);
			String requestJson = requestExample.getExampleBody();
			// set request example detail
			apiMethodDoc.setRequestExample(requestExample);
			apiMethodDoc.setRequestUsage(requestJson == null ? requestExample.getUrl() : requestJson);
			// build response usage
			String responseValue = DocUtil.getNormalTagComments(method, DocTags.API_RESPONSE, cls.getName());
			if (StringUtil.isNotEmpty(responseValue)) {
				apiMethodDoc.setResponseUsage(responseValue);
			}
			else {
				apiMethodDoc.setResponseUsage(JsonBuildHelper.buildReturnJson(docJavaMethod, projectBuilder));
			}
			// build response params
			List<ApiParam> responseParams = buildReturnApiParams(docJavaMethod, projectBuilder);
			if (paramsDataToTree) {
				responseParams = ApiParamTreeUtil.apiParamToTree(responseParams);
			}
			apiMethodDoc.setReturnSchema(docJavaMethod.getReturnSchema());
			apiMethodDoc.setRequestSchema(docJavaMethod.getRequestSchema());
			apiMethodDoc.setResponseParams(responseParams);
			methodDocList.add(apiMethodDoc);
			TornaUtil.setTornaArrayTags(docJavaMethod.getJavaMethod(), apiMethodDoc, apiConfig);
		}
		return methodDocList;
	}

	@Override
	@SuppressWarnings("deprecation")
	public FrameworkAnnotations registeredAnnotations() {
		FrameworkAnnotations annotations = FrameworkAnnotations.builder();
		HeaderAnnotation headerAnnotation = HeaderAnnotation.builder()
			.setAnnotationName(JAXRSAnnotations.JAX_HEADER_PARAM_FULLY)
			.setValueProp(DocAnnotationConstants.VALUE_PROP)
			.setDefaultValueProp(DocAnnotationConstants.DEFAULT_VALUE_PROP)
			.setRequiredProp(DocAnnotationConstants.REQUIRED_PROP);
		// add header annotation
		annotations.setHeaderAnnotation(headerAnnotation);

		// add entry annotation
		Map<String, EntryAnnotation> entryAnnotations = new HashMap<>(16);
		EntryAnnotation jakartaPathAnnotation = EntryAnnotation.builder()
			.setAnnotationName(JakartaJaxrsAnnotations.JAX_PATH_FULLY)
			.setAnnotationFullyName(JakartaJaxrsAnnotations.JAX_PATH_FULLY);
		entryAnnotations.put(jakartaPathAnnotation.getAnnotationName(), jakartaPathAnnotation);

		EntryAnnotation jaxPathAnnotation = EntryAnnotation.builder()
			.setAnnotationName(JAXRSAnnotations.JAX_PATH_FULLY)
			.setAnnotationFullyName(JAXRSAnnotations.JAX_PATH_FULLY);
		entryAnnotations.put(jaxPathAnnotation.getAnnotationName(), jaxPathAnnotation);
		annotations.setEntryAnnotations(entryAnnotations);
		return annotations;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isEntryPoint(JavaClass cls, FrameworkAnnotations frameworkAnnotations) {
		boolean isDefaultEntryPoint = defaultEntryPoint(cls, frameworkAnnotations);
		if (isDefaultEntryPoint) {
			return true;
		}

		if (cls.isAnnotation() || cls.isEnum()) {
			return false;
		}
		List<JavaAnnotation> classAnnotations = DocClassUtil.getAnnotations(cls);
		for (JavaAnnotation annotation : classAnnotations) {
			String annotationName = annotation.getType().getFullyQualifiedName();
			if (JakartaJaxrsAnnotations.JAX_PATH_FULLY.equals(annotationName)
					|| JAXRSAnnotations.JAX_PATH_FULLY.equals(annotationName)) {
				return true;
			}
		}
		// use custom doc tag to support Feign.
		List<DocletTag> docletTags = cls.getTags();
		for (DocletTag docletTag : docletTags) {
			String value = docletTag.getName();
			if (DocTags.DUBBO_REST.equals(value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> listMvcRequestAnnotations() {
		return null;
	}

	/**
	 * build request params
	 * @param docJavaMethod docJavaMethod
	 * @param builder builder
	 * @return ApiMethodReqParam
	 */
	@SuppressWarnings("deprecation")
	private ApiMethodReqParam requestParams(final DocJavaMethod docJavaMethod, ProjectDocConfigBuilder builder) {
		List<ApiParam> paramList = new ArrayList<>();
		List<DocJavaParameter> parameterList = getJavaParameterList(builder, docJavaMethod, null);
		if (parameterList.isEmpty()) {
			return ApiMethodReqParam.builder()
				.setPathParams(new ArrayList<>(0))
				.setQueryParams(paramList)
				.setRequestParams(new ArrayList<>(0));
		}
		boolean isStrict = builder.getApiConfig().isStrict();
		boolean isShowValidation = builder.getApiConfig().isShowValidation();
		ClassLoader classLoader = builder.getApiConfig().getClassLoader();
		JavaMethod javaMethod = docJavaMethod.getJavaMethod();
		String className = javaMethod.getDeclaringClass().getCanonicalName();
		Map<String, String> paramTagMap = docJavaMethod.getParamTagMap();
		Map<String, String> paramsComments = docJavaMethod.getParamsComments();
		Map<String, String> constantsMap = builder.getConstantsMap();
		boolean requestFieldToUnderline = builder.getApiConfig().isRequestFieldToUnderline();
		Set<String> ignoreSets = ignoreParamsSets(javaMethod);
		out: for (DocJavaParameter apiParameter : parameterList) {
			JavaParameter parameter = apiParameter.getJavaParameter();
			String paramName = parameter.getName();
			if (ignoreSets.contains(paramName)) {
				continue;
			}

			String typeName = apiParameter.getGenericCanonicalName();
			String simpleName = apiParameter.getTypeValue().toLowerCase();
			String fullyQualifiedName = apiParameter.getFullyQualifiedName();
			String genericFullyQualifiedName = apiParameter.getGenericFullyQualifiedName();
			String simpleTypeName = apiParameter.getTypeValue();
			if (!paramTagMap.containsKey(paramName) && JavaClassValidateUtil.isPrimitive(genericFullyQualifiedName)
					&& isStrict) {
				throw new RuntimeException("ERROR: Unable to find javadoc @QueryParam for actual param \"" + paramName
						+ "\" in method " + javaMethod.getName() + " from " + className);
			}

			if (requestFieldToUnderline) {
				paramName = StringUtil.camelToUnderline(paramName);
			}
			String mockValue = JavaFieldUtil.createMockValue(paramsComments, paramName, typeName, simpleTypeName);
			JavaClass javaClass = builder.getJavaProjectBuilder().getClassByName(genericFullyQualifiedName);
			List<JavaAnnotation> annotations = parameter.getAnnotations();
			Set<String> groupClasses = JavaClassUtil.getParamGroupJavaClass(annotations,
					builder.getJavaProjectBuilder());
			Set<String> jsonViewClasses = JavaClassUtil.getParamJsonViewClasses(annotations, builder);

			StringBuilder comment = new StringBuilder(this.paramCommentResolve(paramTagMap.get(paramName)));
			boolean isPathVariable = false;
			boolean isRequestBody = false;
			String strRequired = "false";
			if (CollectionUtil.isNotEmpty(annotations)) {
				for (JavaAnnotation annotation : annotations) {
					String annotationName = annotation.getType().getFullyQualifiedName();
					if (JakartaJaxrsAnnotations.JAX_HEADER_PARAM_FULLY.equals(annotationName)
							|| JAXRSAnnotations.JAX_HEADER_PARAM_FULLY.equals(annotationName)) {
						continue out;
					}
					// default value
					if (JakartaJaxrsAnnotations.JAX_DEFAULT_VALUE_FULLY.equals(annotationName)
							|| JAXRSAnnotations.JAX_DEFAULT_VALUE_FULLY.equals(annotationName)) {
						mockValue = StringUtil.removeQuotes(DocUtil.getRequestHeaderValue(classLoader, annotation));
						mockValue = DocUtil.handleConstants(constantsMap, mockValue);
					}
					// path param
					if (JakartaJaxrsAnnotations.JAX_PATH_PARAM_FULLY.equals(annotationName)
							|| JakartaJaxrsAnnotations.JAXB_REST_PATH_FULLY.equals(annotationName)
							|| JAXRSAnnotations.JAX_PATH_PARAM_FULLY.equals(annotationName)) {
						isPathVariable = true;
						strRequired = "true";
					}
					if (JavaClassValidateUtil.isJSR303Required(annotation.getType().getValue())) {
						strRequired = "true";
					}
				}
				comment.append(JavaFieldUtil.getJsrComment(isShowValidation, classLoader, annotations));
			}
			else {
				isRequestBody = true;
			}
			boolean required = Boolean.parseBoolean(strRequired);
			boolean queryParam = !isRequestBody && !isPathVariable;
			if (JavaClassValidateUtil.isCollection(fullyQualifiedName)
					|| JavaClassValidateUtil.isArray(fullyQualifiedName)) {
				String[] gicNameArr = DocClassUtil.getSimpleGicName(typeName);
				String gicName = gicNameArr[0];
				if (JavaClassValidateUtil.isArray(gicName)) {
					gicName = gicName.substring(0, gicName.indexOf("["));
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
						.setType(ParamTypeConstants.PARAM_TYPE_ARRAY)
						.setValue(String.valueOf(value));
					paramList.add(param);
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
						.setValue(DocUtil.getValByTypeAndFieldName(gicName, paramName));
					paramList.add(param);
				}
				else {
					int id = paramList.size() + 1;
					ApiParam param = ApiParam.of()
						.setField(paramName)
						.setDesc(comment + ",[array of object]")
						.setRequired(required)
						.setPathParam(isPathVariable)
						.setQueryParam(queryParam)
						.setId(id)
						.setType(ParamTypeConstants.PARAM_TYPE_ARRAY);
					paramList.add(param);
					paramList.addAll(ParamsBuildHelper.buildParams(typeName, DocGlobalConstants.PARAM_PREFIX, 1, "true",
							Boolean.FALSE, new HashMap<>(16), builder, groupClasses, jsonViewClasses, id, Boolean.FALSE,
							null));
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
			}
			else if (JavaClassValidateUtil.isMap(fullyQualifiedName)) {
				log.warning("When using smart-doc, it is not recommended to use Map to receive parameters, Check it in "
						+ javaMethod.getDeclaringClass().getCanonicalName() + "#" + javaMethod.getName());
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
					continue;
				}
				String[] gicNameArr = DocClassUtil.getSimpleGicName(typeName);
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
				}
				else {
					paramList.addAll(ParamsBuildHelper.buildParams(gicNameArr[1], DocGlobalConstants.EMPTY, 0, "true",
							Boolean.FALSE, new HashMap<>(16), builder, groupClasses, jsonViewClasses, 0, Boolean.FALSE,
							null));
				}

			}
			else if (JavaClassValidateUtil.isFile(typeName)) {
				// file upload
				ApiParam param = ApiParam.of()
					.setField(paramName)
					.setType(ParamTypeConstants.PARAM_TYPE_FILE)
					.setId(paramList.size() + 1)
					.setQueryParam(true)
					.setRequired(required)
					.setVersion(DocGlobalConstants.DEFAULT_VERSION)
					.setDesc(comment.toString());
				if (typeName.contains("[]") || typeName.endsWith(">")) {
					comment.append("(array of file)");
					param.setDesc(comment.toString());
					param.setHasItems(true);
				}
				paramList.add(param);
			}
			// param is enum
			else if (javaClass.isEnum()) {
				String o = JavaClassUtil.getEnumParams(javaClass);
				Object value = JavaClassUtil.getEnumValue(javaClass, builder, true);
				ApiParam param = ApiParam.of()
					.setField(paramName)
					.setId(paramList.size() + 1)
					.setPathParam(isPathVariable)
					.setQueryParam(queryParam)
					.setValue(String.valueOf(value))
					.setType(ParamTypeConstants.PARAM_TYPE_ENUM)
					.setDesc(StringUtil.removeQuotes(o))
					.setRequired(required)
					.setVersion(DocGlobalConstants.DEFAULT_VERSION)
					.setEnumValues(JavaClassUtil.getEnumValues(javaClass));
				paramList.add(param);
			}
			else {
				paramList
					.addAll(ParamsBuildHelper.buildParams(typeName, DocGlobalConstants.EMPTY, 0, "true", Boolean.FALSE,
							new HashMap<>(16), builder, groupClasses, jsonViewClasses, 0, Boolean.FALSE, null));
			}
		}
		List<ApiParam> pathParams = new ArrayList<>();
		List<ApiParam> queryParams = new ArrayList<>();
		List<ApiParam> bodyParams = new ArrayList<>();
		for (ApiParam param : paramList) {
			param.setValue(StringUtil.removeDoubleQuotes(param.getValue()));
			if (param.isPathParam()) {
				param.setId(pathParams.size() + 1);
				pathParams.add(param);
			}
			else if (param.isQueryParam()) {
				param.setId(queryParams.size() + 1);
				queryParams.add(param);
			}
			else {
				param.setId(bodyParams.size() + 1);
				bodyParams.add(param);
			}
		}
		return ApiMethodReqParam.builder()
			.setRequestParams(bodyParams)
			.setPathParams(pathParams)
			.setQueryParams(queryParams);
	}

	/**
	 * Constructs an API request example in JSON format based on the provided Java method
	 * and API method documentation.
	 * @param javaMethod The object representing the Java method, encapsulating method
	 * details.
	 * @param apiMethodDoc Documentation for the API method, detailing its configuration
	 * such as type, headers, URL, etc.
	 * @param configBuilder The configuration builder for the project, used to access
	 * project-wide settings like field naming conventions and class information.
	 * @return An instance of ApiRequestExample configured with the appropriate URL,
	 * example body, and form data if applicable.
	 */
	@SuppressWarnings("deprecation")
	private ApiRequestExample buildReqJson(DocJavaMethod javaMethod, ApiMethodDoc apiMethodDoc,
			ProjectDocConfigBuilder configBuilder) {
		String methodType = apiMethodDoc.getType();
		JavaMethod method = javaMethod.getJavaMethod();
		Map<String, String> pathParamsMap = new LinkedHashMap<>();
		List<DocJavaParameter> parameterList = getJavaParameterList(configBuilder, javaMethod, null);
		List<ApiReqParam> reqHeaderList = apiMethodDoc.getRequestHeaders();
		if (parameterList.isEmpty()) {
			CurlRequest curlRequest = CurlRequest.builder()
				.setContentType(apiMethodDoc.getContentType())
				.setType(methodType)
				.setReqHeaders(reqHeaderList)
				.setUrl(apiMethodDoc.getUrl());
			String format = CurlUtil.toCurl(curlRequest);
			return ApiRequestExample.builder().setUrl(apiMethodDoc.getUrl()).setExampleBody(format);
		}
		boolean requestFieldToUnderline = configBuilder.getApiConfig().isRequestFieldToUnderline();
		Map<String, String> paramsComments = javaMethod.getParamsComments();
		List<FormData> formDataList = new ArrayList<>();
		ApiRequestExample requestExample = ApiRequestExample.builder();
		for (DocJavaParameter apiParameter : parameterList) {
			JavaParameter parameter = apiParameter.getJavaParameter();
			String paramName = parameter.getName();
			String typeName = apiParameter.getGenericFullyQualifiedName();
			String fullyQualifiedName = apiParameter.getFullyQualifiedName();
			String gicTypeName = apiParameter.getGenericCanonicalName();
			String simpleTypeName = apiParameter.getTypeValue();
			gicTypeName = DocClassUtil.rewriteRequestParam(gicTypeName);
			JavaClass javaClass = configBuilder.getJavaProjectBuilder().getClassByName(typeName);
			String[] globGicName = DocClassUtil.getSimpleGicName(gicTypeName);
			String comment = this.paramCommentResolve(paramsComments.get(paramName));
			String mockValue = JavaFieldUtil.createMockValue(paramsComments, paramName, gicTypeName, simpleTypeName);
			if (requestFieldToUnderline) {
				paramName = StringUtil.camelToUnderline(paramName);
			}
			List<JavaAnnotation> annotations = parameter.getAnnotations();
			Set<String> groupClasses = JavaClassUtil.getParamGroupJavaClass(annotations,
					configBuilder.getJavaProjectBuilder());
			Set<String> jsonViewClasses = JavaClassUtil.getParamJsonViewClasses(annotations, configBuilder);
			boolean paramAdded = false;
			if (CollectionUtil.isNotEmpty(annotations)) {
				for (JavaAnnotation annotation : annotations) {
					String annotationName = annotation.getType().getFullyQualifiedName();
					if (JakartaJaxrsAnnotations.JAX_PATH_PARAM_FULLY.equals(annotationName)
							|| JakartaJaxrsAnnotations.JAXB_REST_PATH_FULLY.equals(annotationName)
							|| JAXRSAnnotations.JAX_PATH_PARAM_FULLY.equals(annotationName)) {
						if (javaClass.isEnum()) {
							Object value = JavaClassUtil.getEnumValue(javaClass, configBuilder, Boolean.TRUE);
							mockValue = StringUtil.removeQuotes(String.valueOf(value));
						}
						pathParamsMap.put(paramName, mockValue);
						paramAdded = true;
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
						formData.setDescription(comment);
						formData.setValue(mockValue);
						formDataList.add(formData);
					}
					else if (JavaClassValidateUtil.isPrimitive(fullyQualifiedName)) {
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
								&& !configBuilder.getJavaProjectBuilder().getClassByName(gicName).isEnum()) {
							throw new RuntimeException(
									"Jaxrs rest can't support binding Collection on method " + method.getName()
											+ "Check it in " + method.getDeclaringClass().getCanonicalName());
						}
						FormData formData = new FormData();
						formData.setKey(paramName);
						if (!paramName.contains("[]")) {
							formData.setKey(paramName + "[]");
						}
						formData.setDescription(comment);
						formData.setType(ParamTypeConstants.PARAM_TYPE_TEXT);
						formData.setValue(RandomUtil.randomValueByType(gicName));
						formDataList.add(formData);
					}
					else if (javaClass.isEnum()) {
						// do nothing
						Object value = JavaClassUtil.getEnumValue(javaClass, configBuilder, Boolean.TRUE);
						String strVal = StringUtil.removeQuotes(String.valueOf(value));
						FormData formData = new FormData();
						formData.setDescription(comment);
						formData.setKey(paramName);
						formData.setType(ParamTypeConstants.PARAM_TYPE_TEXT);
						formData.setValue(strVal);
						formDataList.add(formData);
					}
					else {
						formDataList.addAll(FormDataBuildHelper.getFormData(gicTypeName, new HashMap<>(16), 0,
								configBuilder, DocGlobalConstants.EMPTY, groupClasses));
					}
				}
			}
			else {
				if (JavaClassValidateUtil.isPrimitive(simpleTypeName)) {
					requestExample.setJsonBody(mockValue).setJson(true);
				}
				else {
					String json = JsonBuildHelper.buildJson(fullyQualifiedName, gicTypeName, Boolean.FALSE, 0,
							new HashMap<>(16), groupClasses, jsonViewClasses, configBuilder);
					requestExample.setJsonBody(JsonUtil.toPrettyFormat(json)).setJson(true);
				}
			}
		}
		requestExample.setFormDataList(formDataList);
		RequestExampleUtil.setExampleBody(apiMethodDoc, requestExample, formDataList, pathParamsMap, pathParamsMap);
		return requestExample;
	}

	/**
	 * @param method method
	 * @return boolean
	 */
	private boolean checkCondition(JavaMethod method) {
		return method.isPrivate() || Objects.nonNull(method.getTagByName(DocTags.IGNORE));
	}

	@Override
	public void requestMappingPostProcess(JavaClass javaClass, JavaMethod method, RequestMapping requestMapping) {

	}

	@Override
	public boolean ignoreMvcParamWithAnnotation(String annotation) {
		return false;
	}

	@Override
	public boolean ignoreReturnObject(String typeName, List<String> ignoreParams) {
		return false;
	}

	@Override
	public boolean isExceptionAdviceEntryPoint(JavaClass javaClass, FrameworkAnnotations frameworkAnnotations) {
		return false;
	}

	@Override
	public ExceptionAdviceMethod processExceptionAdviceMethod(JavaMethod method) {
		return null;
	}

	@Override
	public List<ApiExceptionStatus> defaultHttpErrorStatuses() {
		return null;
	}

}