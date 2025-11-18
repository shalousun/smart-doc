/*
 * Copyright (C) 2018-2025 smart-doc
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
import io.github.smartdoc.constants.DocAnnotationConstants;
import io.github.smartdoc.constants.DocGlobalConstants;
import io.github.smartdoc.constants.DocTags;
import io.github.smartdoc.constants.FrameworkEnum;
import io.github.smartdoc.constants.Methods;
import io.github.smartdoc.constants.SpringMvcAnnotations;
import io.github.smartdoc.constants.SpringMvcRequestAnnotationsEnum;
import io.github.smartdoc.handler.SpringMVCRequestHeaderHandler;
import io.github.smartdoc.handler.SpringMVCRequestMappingHandler;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.ApiDoc;
import io.github.smartdoc.model.ApiExceptionStatus;
import io.github.smartdoc.model.ApiParam;
import io.github.smartdoc.model.ApiReqParam;
import io.github.smartdoc.model.ApiSchema;
import io.github.smartdoc.model.ExceptionAdviceMethod;
import io.github.smartdoc.model.WebSocketDoc;
import io.github.smartdoc.model.annotation.EntryAnnotation;
import io.github.smartdoc.model.annotation.ExceptionAdviceAnnotation;
import io.github.smartdoc.model.annotation.FrameworkAnnotations;
import io.github.smartdoc.model.annotation.HeaderAnnotation;
import io.github.smartdoc.model.annotation.MappingAnnotation;
import io.github.smartdoc.model.annotation.PathVariableAnnotation;
import io.github.smartdoc.model.annotation.RequestBodyAnnotation;
import io.github.smartdoc.model.annotation.RequestParamAnnotation;
import io.github.smartdoc.model.annotation.RequestPartAnnotation;
import io.github.smartdoc.model.annotation.ServerEndpointAnnotation;
import io.github.smartdoc.model.request.RequestMapping;
import io.github.smartdoc.utils.JavaClassValidateUtil;
import com.power.common.util.DateTimeUtil;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * spring boot doc build template.
 *
 * @author yu 2019/12/21.
 */
public class SpringBootDocBuildTemplate implements IDocBuildTemplate<ApiDoc>, IWebSocketDocBuildTemplate<WebSocketDoc>,
		IRestDocTemplate, IWebSocketTemplate {

	@Override
	public boolean supportsFramework(String framework) {
		return FrameworkEnum.SPRING.getFramework().equalsIgnoreCase(framework);
	}

	@Override
	public ApiSchema<ApiDoc> renderApi(ProjectDocConfigBuilder projectBuilder, Collection<JavaClass> candidateClasses) {
		ApiConfig apiConfig = projectBuilder.getApiConfig();
		List<ApiReqParam> configApiReqParams = Stream.of(apiConfig.getRequestHeaders(), apiConfig.getRequestParams())
			.filter(Objects::nonNull)
			.flatMap(Collection::stream)
			.collect(Collectors.toList());
		FrameworkAnnotations frameworkAnnotations = this.registeredAnnotations();
		return this.processApiData(projectBuilder, frameworkAnnotations, configApiReqParams,
				new SpringMVCRequestMappingHandler(), new SpringMVCRequestHeaderHandler(), candidateClasses);
	}

	@Override
	public List<WebSocketDoc> renderWebSocketApi(ProjectDocConfigBuilder projectBuilder,
			Collection<JavaClass> candidateClasses) {
		FrameworkAnnotations frameworkAnnotations = this.registeredAnnotations();
		return this.processWebSocketData(projectBuilder, frameworkAnnotations, new SpringMVCRequestMappingHandler(),
				candidateClasses);
	}

	@Override
	public boolean ignoreReturnObject(String typeName, List<String> ignoreParams) {
		return JavaClassValidateUtil.isMvcIgnoreParams(typeName, ignoreParams);
	}

	@Override
	public FrameworkAnnotations registeredAnnotations() {
		FrameworkAnnotations annotations = FrameworkAnnotations.builder();

		// Header annotation
		HeaderAnnotation headerAnnotation = HeaderAnnotation.builder()
			.setAnnotationName(SpringMvcAnnotations.REQUEST_HEADER)
			.setValueProp(DocAnnotationConstants.VALUE_PROP)
			.setDefaultValueProp(DocAnnotationConstants.DEFAULT_VALUE_PROP)
			.setRequiredProp(DocAnnotationConstants.REQUIRED_PROP);
		// add header annotation
		annotations.setHeaderAnnotation(headerAnnotation);

		// Entry annotations (Controller, RestController)
		Map<String, EntryAnnotation> entryAnnotations = new HashMap<>(16);
		EntryAnnotation controllerAnnotation = EntryAnnotation.builder()
			.setAnnotationName(SpringMvcAnnotations.CONTROLLER)
			.setAnnotationFullyName(SpringMvcAnnotations.CONTROLLER);
		entryAnnotations.put(controllerAnnotation.getAnnotationName(), controllerAnnotation);

		EntryAnnotation restController = EntryAnnotation.builder()
			.setAnnotationName(SpringMvcAnnotations.REST_CONTROLLER);
		entryAnnotations.put(restController.getAnnotationName(), restController);
		annotations.setEntryAnnotations(entryAnnotations);

		// RequestBody annotation
		RequestBodyAnnotation bodyAnnotation = RequestBodyAnnotation.builder()
			.setAnnotationName(SpringMvcAnnotations.REQUEST_BODY)
			.setAnnotationFullyName(SpringMvcAnnotations.REQUEST_BODY_FULLY);
		annotations.setRequestBodyAnnotation(bodyAnnotation);

		// RequestParam annotation
		RequestParamAnnotation requestParamAnnotation = RequestParamAnnotation.builder()
			.setAnnotationName(SpringMvcAnnotations.REQUEST_PARAM)
			.setDefaultValueProp(DocAnnotationConstants.DEFAULT_VALUE_PROP)
			.setRequiredProp(DocAnnotationConstants.REQUIRED_PROP);
		annotations.setRequestParamAnnotation(requestParamAnnotation);

		// RequestPart annotation
		RequestPartAnnotation requestPartAnnotation = RequestPartAnnotation.builder()
			.setAnnotationName(SpringMvcAnnotations.REQUEST_PART)
			.setDefaultValueProp(DocAnnotationConstants.DEFAULT_VALUE_PROP)
			.setRequiredProp(DocAnnotationConstants.REQUIRED_PROP);
		annotations.setRequestPartAnnotation(requestPartAnnotation);

		// PathVariable annotation
		PathVariableAnnotation pathVariableAnnotation = PathVariableAnnotation.builder()
			.setAnnotationName(SpringMvcAnnotations.PATH_VARIABLE)
			.setDefaultValueProp(DocAnnotationConstants.DEFAULT_VALUE_PROP)
			.setRequiredProp(DocAnnotationConstants.REQUIRED_PROP);
		annotations.setPathVariableAnnotation(pathVariableAnnotation);

		// ServerEndpoint annotation (WebSocket)
		ServerEndpointAnnotation serverEndpointAnnotation = ServerEndpointAnnotation.builder()
			.setAnnotationName(SpringMvcAnnotations.SERVER_ENDPOINT);
		annotations.setServerEndpointAnnotation(serverEndpointAnnotation);

		// add mapping annotations
		Map<String, MappingAnnotation> mappingAnnotations = this.buildSpringMappingAnnotations();
		annotations.setMappingAnnotations(mappingAnnotations);

		// Exception advice annotations
		Map<String, ExceptionAdviceAnnotation> exceptionAdviceAnnotations = new HashMap<>(16);

		ExceptionAdviceAnnotation controllerAdviceAnnotation = ExceptionAdviceAnnotation.builder()
			.setAnnotationName(SpringMvcAnnotations.CONTROLLER_ADVICE);
		exceptionAdviceAnnotations.put(controllerAdviceAnnotation.getAnnotationName(), controllerAdviceAnnotation);

		ExceptionAdviceAnnotation restControllerAdviceAnnotation = ExceptionAdviceAnnotation.builder()
			.setAnnotationName(SpringMvcAnnotations.REST_CONTROLLER_ADVICE);
		exceptionAdviceAnnotations.put(restControllerAdviceAnnotation.getAnnotationName(),
				restControllerAdviceAnnotation);

		annotations.setExceptionAdviceAnnotations(exceptionAdviceAnnotations);

		return annotations;
	}

	@Override
	public boolean isEntryPoint(JavaClass javaClass, FrameworkAnnotations frameworkAnnotations) {
		boolean isDefaultEntryPoint = this.defaultEntryPoint(javaClass, frameworkAnnotations);
		if (isDefaultEntryPoint) {
			return true;
		}

		if (javaClass.isAnnotation() || javaClass.isEnum()) {
			return false;
		}
		// use custom doc tag to support Feign.
		List<DocletTag> docletTags = javaClass.getTags();
		for (DocletTag docletTag : docletTags) {
			String value = docletTag.getName();
			if (DocTags.REST_API.equals(value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> listMvcRequestAnnotations() {
		return SpringMvcRequestAnnotationsEnum.listSpringMvcRequestAnnotations();
	}

	@Override
	public void requestMappingPostProcess(JavaClass javaClass, JavaMethod method, RequestMapping requestMapping) {
		// do nothing
	}

	@Override
	public boolean ignoreMvcParamWithAnnotation(String annotation) {
		return JavaClassValidateUtil.ignoreSpringMvcParamWithAnnotation(annotation);
	}

	@Override
	public boolean isExceptionAdviceEntryPoint(JavaClass javaClass, FrameworkAnnotations frameworkAnnotations) {
		return this.defaultExceptionAdviceEntryPoint(javaClass, frameworkAnnotations);
	}

	@Override
	public ExceptionAdviceMethod processExceptionAdviceMethod(JavaMethod method) {
		List<JavaAnnotation> annotations = method.getAnnotations();
		boolean isExceptionHandlerMethod = false;
		String status = null;
		for (JavaAnnotation annotation : annotations) {
			String annotationName = annotation.getType().getValue();
			if (SpringMvcAnnotations.EXCEPTION_HANDLER.equals(annotationName)) {
				isExceptionHandlerMethod = true;
			}
			if (SpringMvcAnnotations.RESPONSE_STATUS.equals(annotationName)) {
				Object consumes = annotation.getNamedParameter(DocAnnotationConstants.VALUE_PROP);
				if (Objects.nonNull(consumes)) {
					status = consumes.toString();
				}
			}
		}
		return ExceptionAdviceMethod.builder().setExceptionHandlerMethod(isExceptionHandlerMethod).setStatus(status);
	}

	@Override
	public List<ApiExceptionStatus> defaultHttpErrorStatuses() {
		ZonedDateTime now = ZonedDateTime.now();
		String strDateTime = DateTimeUtil.zonedDateTimeToStr(now, DateTimeUtil.DATE_FORMAT_ZONED_DATE_TIME);

		ApiParam errorParam = ApiParam.of()
			.setClassName("HttpErrorStatusResponse")
			.setField("error")
			.setType("string")
			.setValue("error")
			.setDesc("error message");
		ApiParam pathParam = ApiParam.of()
			.setClassName("HttpErrorStatusResponse")
			.setField("path")
			.setType("string")
			.setValue("")
			.setDesc("request path");

		ApiParam timestampParam = ApiParam.of()
			.setClassName("HttpErrorStatusResponse")
			.setField("timestamp")
			.setType("string")
			.setValue("")
			.setDesc("timestamp");

		ApiParam status500Param = ApiParam.of()
			.setClassName("HttpErrorStatusResponse")
			.setField("status")
			.setType("int")
			.setValue("500")
			.setDesc("Internal Server Error")
			.setRequired(true);
		ApiParam status400Param = ApiParam.of()
			.setClassName("HttpErrorStatusResponse")
			.setField("status")
			.setType("int")
			.setValue("400")
			.setDesc("Bad Request")
			.setRequired(true);

		ApiParam status404Param = ApiParam.of()
			.setClassName("HttpErrorStatusResponse")
			.setField("status")
			.setType("int")
			.setValue("404")
			.setDesc("Not Found")
			.setRequired(true);
		ApiParam status401Param = ApiParam.of()
			.setClassName("HttpErrorStatusResponse")
			.setField("status")
			.setType("int")
			.setValue("401")
			.setDesc("Unauthorized")
			.setRequired(true);
		ApiParam status403Param = ApiParam.of()
			.setClassName("HttpErrorStatusResponse")
			.setField("status")
			.setType("int")
			.setValue("403")
			.setDesc("Forbidden")
			.setRequired(true);
		ApiParam status405Param = ApiParam.of()
			.setClassName("HttpErrorStatusResponse")
			.setField("status")
			.setType("int")
			.setValue("405")
			.setDesc("Method Not Allowed")
			.setRequired(true);
		ApiParam status415Param = ApiParam.of()
			.setClassName("HttpErrorStatusResponse")
			.setField("status")
			.setType("int")
			.setValue("415")
			.setDesc("Unsupported Media Type")
			.setRequired(true);

		List<ApiExceptionStatus> exceptionStatusList = new ArrayList<>();
		exceptionStatusList.add(ApiExceptionStatus.of()
			.setStatus("500")
			.setDesc("Internal Server Error")
			.setResponseUsage("{\n" + "  \"timestamp\": \"" + strDateTime + "\",\n" + "  \"status\": 500,\n"
					+ "  \"error\": \"Internal Server Error\",\n" + "  \"path\": \"/api/v1/xx\"\n" + "}")
			.setExceptionResponseParams(Arrays.asList(errorParam, pathParam, timestampParam, status500Param)));
		exceptionStatusList.add(ApiExceptionStatus.of()
			.setStatus("400")
			.setDesc("Bad Request")
			.setResponseUsage("{\n" + "  \"timestamp\": \"" + strDateTime + "\",\n" + "  \"status\": 400,\n"
					+ "  \"error\": \"Bad Request\",\n" + "  \"path\": \"/api/v1/xx\"\n" + "}")
			.setExceptionResponseParams(Arrays.asList(errorParam, pathParam, timestampParam, status400Param)));

		exceptionStatusList.add(ApiExceptionStatus.of()
			.setStatus("404")
			.setDesc("Not Found")
			.setResponseUsage("{\n" + "  \"timestamp\": \"" + strDateTime + "\",\n" + "  \"status\": 404,\n"
					+ "  \"error\": \"Not Found\",\n" + "  \"path\": \"/api/v1/xx\"\n" + "}")
			.setExceptionResponseParams(Arrays.asList(errorParam, pathParam, timestampParam, status404Param)));

		exceptionStatusList.add(ApiExceptionStatus.of()
			.setStatus("401")
			.setDesc("Unauthorized")
			.setResponseUsage("{\n" + "  \"timestamp\": \"" + strDateTime + "\",\n" + "  \"status\": 401,\n"
					+ "  \"error\": \"Unauthorized\",\n" + "  \"path\": \"/api/v1/xx\"\n" + "} ")
			.setExceptionResponseParams(Arrays.asList(errorParam, pathParam, timestampParam, status401Param)));
		exceptionStatusList.add(ApiExceptionStatus.of()
			.setStatus("403")
			.setDesc("Forbidden")
			.setResponseUsage("{\n" + "  \"timestamp\": \"" + strDateTime + "\",\n" + "  \"status\": 403,\n"
					+ "  \"error\": \"Forbidden\",\n" + "  \"path\": \"/api/v1/xx\"\n" + "} ")
			.setExceptionResponseParams(Arrays.asList(errorParam, pathParam, timestampParam, status403Param)));

		exceptionStatusList.add(ApiExceptionStatus.of()
			.setStatus("405")
			.setDesc("Method Not Allowed")
			.setResponseUsage("{\n" + "  \"timestamp\": \"" + strDateTime + "\",\n" + "  \"status\": 405,\n"
					+ "  \"error\": \"Method Not Allowed\",\n" + "  \"path\": \"/api/v1/xx\"\n" + "} ")
			.setExceptionResponseParams(Arrays.asList(errorParam, pathParam, timestampParam, status405Param)));
		exceptionStatusList.add(ApiExceptionStatus.of()
			.setStatus("415")
			.setDesc("Unsupported Media Type")
			.setResponseUsage("{\n" + "  \"timestamp\": \"" + strDateTime + "\",\n" + "  \"status\": 415,\n"
					+ "  \"error\": \"Unsupported Media Type\",\n" + "  \"path\": \"/api/v1/xx\"\n" + "} ")
			.setExceptionResponseParams(Arrays.asList(errorParam, pathParam, timestampParam, status415Param)));
		return exceptionStatusList;
	}

	/**
	 * Builds and returns all Spring MVC request mapping annotations
	 * including @RequestMapping, @GetMapping, @PostMapping, etc., with consistent
	 * attribute configurations.
	 * @return a map of annotation name to {@link MappingAnnotation}
	 */
	private Map<String, MappingAnnotation> buildSpringMappingAnnotations() {
		Map<String, MappingAnnotation> mappingAnnotations = new HashMap<>(16);

		// Common properties
		String consumes = DocAnnotationConstants.CONSUMES;
		String produces = DocAnnotationConstants.PRODUCES;
		String method = DocAnnotationConstants.METHOD;
		String params = DocAnnotationConstants.PARAMS;
		String[] pathProps = DocAnnotationConstants.PATH_MAPPING_PROPS;

		// @RequestMapping
		MappingAnnotation requestMapping = MappingAnnotation.builder()
			.setAnnotationName(SpringMvcAnnotations.REQUEST_MAPPING)
			.setConsumesProp(consumes)
			.setProducesProp(produces)
			.setMethodProp(method)
			.setParamsProp(params)
			.setScope("class", "method")
			.setPathProps(pathProps);
		mappingAnnotations.put(requestMapping.getAnnotationName(), requestMapping);

		// @PostMapping
		MappingAnnotation postMapping = this.createMapping(SpringMvcAnnotations.POST_MAPPING, Methods.POST.getValue(),
				pathProps, consumes, produces, method, params);
		mappingAnnotations.put(postMapping.getAnnotationName(), postMapping);

		// @GetMapping
		MappingAnnotation getMapping = this.createMapping(SpringMvcAnnotations.GET_MAPPING, Methods.GET.getValue(),
				pathProps, consumes, produces, method, params);
		mappingAnnotations.put(getMapping.getAnnotationName(), getMapping);

		// @PutMapping
		MappingAnnotation putMapping = this.createMapping(SpringMvcAnnotations.PUT_MAPPING, Methods.PUT.getValue(),
				pathProps, consumes, produces, method, params);
		mappingAnnotations.put(putMapping.getAnnotationName(), putMapping);

		// @PatchMapping
		MappingAnnotation patchMapping = this.createMapping(SpringMvcAnnotations.PATCH_MAPPING,
				Methods.PATCH.getValue(), pathProps, consumes, produces, method, params);
		mappingAnnotations.put(patchMapping.getAnnotationName(), patchMapping);

		// @DeleteMapping
		MappingAnnotation deleteMapping = this.createMapping(SpringMvcAnnotations.DELETE_MAPPING,
				Methods.DELETE.getValue(), pathProps, consumes, produces, method, params);
		mappingAnnotations.put(deleteMapping.getAnnotationName(), deleteMapping);

		// @FeignClient
		MappingAnnotation feignClient = MappingAnnotation.builder()
			.setAnnotationName(DocGlobalConstants.FEIGN_CLIENT)
			.setAnnotationFullyName(DocGlobalConstants.FEIGN_CLIENT_FULLY)
			.setPathProps(DocAnnotationConstants.PATH_PROP);
		mappingAnnotations.put(feignClient.getAnnotationName(), feignClient);

		return mappingAnnotations;
	}

	/**
	 * Helper method to create common HTTP method-based mappings
	 * (e.g., @GetMapping, @PostMapping).
	 * @param annotationName the annotation name
	 * @param methodType the method type
	 * @param pathProps the path properties
	 * @param consumes the consumes property
	 * @param produces the produces property
	 * @param method the HTTP method
	 * @param params the params property
	 */
	private MappingAnnotation createMapping(String annotationName, String methodType, String[] pathProps,
			String consumes, String produces, String method, String params) {
		return MappingAnnotation.builder()
			.setAnnotationName(annotationName)
			.setConsumesProp(consumes)
			.setProducesProp(produces)
			.setMethodProp(method)
			.setParamsProp(params)
			.setMethodType(methodType)
			.setPathProps(pathProps);
	}

}
