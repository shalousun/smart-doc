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

package io.github.smartdoc.model;

import io.github.smartdoc.constants.ComponentTypeEnum;
import io.github.smartdoc.constants.DocLanguage;
import io.github.smartdoc.constants.OpenApiTagNameTypeEnum;
import io.github.smartdoc.handler.ICustomJavaMethodHandler;
import io.github.smartdoc.model.jmeter.JMeter;
import io.github.smartdoc.model.rpc.RpcApiDependency;
import com.power.common.util.CollectionUtil;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description: api config info
 *
 * @author yu 2018/06/18.
 */
public class ApiConfig {

	private static ApiConfig instance;

	/**
	 * Web server base url
	 */
	private String serverUrl;

	/**
	 * Web server base url for postman
	 */
	private String serverEnv;

	/**
	 * Path Prefix, eg: Servlet ContextPath
	 */
	private String pathPrefix = "";

	/**
	 * Set comments check mode
	 */
	private boolean isStrict;

	/**
	 * Merge all api doc into one document
	 */
	private boolean allInOne;

	/**
	 * output path
	 */
	private String outPath;

	/**
	 * source path
	 */
	private List<SourceCodePath> sourceCodePaths;

	/**
	 * third source jar path
	 */
	private List<SourceCodePath> jarSourcePaths;

	/**
	 * list of Request headers
	 */
	private List<ApiReqParam> requestHeaders;

	/**
	 * @since 2.2.2 list of Request params
	 */
	private List<ApiReqParam> requestParams;

	/**
	 * @since 1.7.5 cover old all in one markdown
	 */
	private boolean coverOld;

	/**
	 * list of custom response filed
	 */
	private List<CustomField> customResponseFields;

	/**
	 * list of custom request field
	 */
	private List<CustomField> customRequestFields;

	/**
	 * List of error code
	 * @return
	 */

	private List<ApiErrorCode> errorCodes;

	/**
	 * controller package filters
	 */
	private String packageFilters;

	/**
	 * controller package exclude filters
	 */
	private String packageExcludeFilters;

	/**
	 * List of change log
	 */
	private List<RevisionLog> revisionLogs;

	/**
	 * @since 1.7+
	 */
	private boolean md5EncryptedHtmlName;

	/**
	 * language support
	 *
	 * @since 1.7+
	 */
	private DocLanguage language;

	/**
	 * adoc flag
	 */
	private boolean adoc;

	/**
	 * default /src/main/java
	 */
	private String codePath;

	/**
	 * api data dictionary
	 */
	private List<ApiDataDictionary> dataDictionaries;

	private transient ClassLoader classLoader;

	/**
	 * @since 1.7.9 api error code dictionary
	 */
	private List<ApiErrorCodeDictionary> errorCodeDictionaries;

	/**
	 * list of custom response filed
	 */
	private List<ApiObjectReplacement> apiObjectReplacements;

	/**
	 * list of rpc api dependencies
	 */
	private List<RpcApiDependency> rpcApiDependencies;

	/**
	 * list of api constant
	 */
	private List<ApiConstant> apiConstants;

	/**
	 * @since 2.0.7 project group
	 */
	private String group;

	/**
	 * @since 1.7.5 project name
	 */
	private String projectName;

	/**
	 * @since 2.0.7 project cn name
	 */
	private String projectCName;

	/**
	 * serialize request transients;default false
	 */
	private boolean serializeRequestTransients = false;

	/**
	 * serialize response transients;default false
	 */
	private boolean serializeResponseTransients = false;

	/**
	 * @since 1.7.10 default show author
	 */
	private boolean showAuthor = true;

	/**
	 * convert request field to underline
	 *
	 * @since 1.8.7
	 */
	private boolean requestFieldToUnderline;

	/**
	 * convert response field to underline
	 *
	 * @since 1.8.7
	 */
	private boolean responseFieldToUnderline;

	/**
	 * sort by title
	 *
	 * @since 1.8.7
	 */
	private boolean sortByTitle;

	/**
	 * is rest api doc
	 *
	 * @since 1.8.7
	 */
	private Boolean showJavaType = Boolean.FALSE;

	/**
	 * is inline enum field comment
	 *
	 * @since 1.8.8
	 */
	private Boolean inlineEnum = Boolean.FALSE;

	/**
	 * rpc consumer config example
	 *
	 * @since 1.8.7
	 */
	private String rpcConsumerConfig;

	/**
	 * recursion limit
	 *
	 * @since 1.8.8
	 */
	private int recursionLimit = 7;

	/**
	 * request example
	 *
	 * @since 1.9.0
	 */
	private boolean requestExample = Boolean.TRUE;

	/**
	 * response example
	 *
	 * @since 1.9.0
	 */
	private boolean responseExample = Boolean.TRUE;

	/**
	 * custom setting api document name
	 *
	 * @since 1.9.0
	 */
	private String allInOneDocFileName;

	/**
	 * convert param data to tree
	 */
	private boolean paramsDataToTree;

	/**
	 * request ignore param
	 * @return
	 * @since 1.9.2
	 */
	private List<String> ignoreRequestParams;

	/**
	 * display actual type of generic
	 *
	 * @since 1.9.6
	 */
	private boolean displayActualType;

	/**
	 * Support Spring MVC ResponseBodyAdvice
	 *
	 * @since 1.9.8
	 */
	private BodyAdvice responseBodyAdvice;

	/**
	 * @since 2.1.4
	 */
	private BodyAdvice requestBodyAdvice;

	private String style;

	private String highlightStyleLink;

	/**
	 * create debug page
	 */
	private boolean createDebugPage;

	/**
	 * Spring MVC url suffix
	 *
	 * @since 2.1.0
	 */
	private String urlSuffix;

	/**
	 * Torna appKey
	 */
	private String appKey;

	/**
	 * Torna Secret
	 */
	private String secret;

	/**
	 * Torna appToken
	 */
	private String appToken;

	/**
	 * Torna openUrl
	 */
	private String openUrl;

	/**
	 * public static final String APP_KEY = "20201216788835306945118208"; public static
	 * final String SECRET = "W.ZyGMOB9Q0UqujVxnfi@.I#V&tUUYZR"; public static final
	 * String APP_TOKEN = "2f9a7d3858a147b7845ebb48785d4dc7"; public static final String
	 * OPEN_URL = "http://torna.opensphere.cn/api/";
	 * @return
	 */
	/**
	 * Debugging environment name
	 */
	private String debugEnvName;

	/**
	 * Url of the debugging environment
	 */
	private String debugEnvUrl;

	/**
	 * Show log when pushing document to torna
	 */
	private boolean tornaDebug = true;

	/**
	 * The operator who pushes the document to Torna
	 */
	private String author;

	/**
	 * smart-doc supported framework, if not set default is spring,
	 */
	private String framework;

	private List<ApiGroup> groups;

	/**
	 * replace old document while push to torna
	 *
	 * @since 2.2.4
	 */
	private Boolean replace;

	/**
	 * @since 2.2.5
	 */
	private boolean requestParamsTable = Boolean.TRUE;

	/**
	 * @since 2.2.5
	 */
	private boolean responseParamsTable = Boolean.TRUE;

	/**
	 * @since 2.6.8
	 */
	private ICustomJavaMethodHandler customJavaMethodHandler;

	/**
	 * @since 2.6.9
	 */
	private boolean randomMock;

	/**
	 * build random component for openApi
	 *
	 * @see ComponentTypeEnum
	 */
	private ComponentTypeEnum componentType = ComponentTypeEnum.RANDOM;

	/**
	 * Strategy for determining the source of OpenAPI tag names.
	 * <p>
	 * This field specifies how the tag names in the generated OpenAPI document are
	 * derived. Supported strategies include:
	 * </p>
	 *
	 * <ul>
	 * <li>{@link OpenApiTagNameTypeEnum#CLASS_NAME}: Use the controller's simple class
	 * name (e.g., "UserController")</li>
	 * <li>{@link OpenApiTagNameTypeEnum#DESCRIPTION}: Use the controller's description
	 * (e.g., from {@link ApiDoc#getDesc()})</li>
	 * <li>{@link OpenApiTagNameTypeEnum#PACKAGE_NAME}: Use the controller's full package
	 * name (e.g., "com.example.controller")</li>
	 * </ul>
	 *
	 * <p>
	 * This setting affects how API operations are grouped in the OpenAPI documentation.
	 * Choose the strategy that best aligns with your API organization needs.
	 * </p>
	 *
	 * @see OpenApiTagNameTypeEnum
	 * @see ApiDoc#getDesc() for description source details
	 * @since 3.1.1
	 */
	private OpenApiTagNameTypeEnum openApiTagNameType = OpenApiTagNameTypeEnum.CLASS_NAME;

	/**
	 * whether to build the doc incrementally
	 *
	 * @since 3.0.0
	 */
	private boolean increment = Boolean.FALSE;

	/**
	 * the doc module absolute path, used for dependency tree file
	 */
	private String baseDir;

	/**
	 * upload api split number
	 *
	 * @since 3.0.2
	 */
	private Integer apiUploadNums;

	/**
	 * Show JSR validation information
	 *
	 * @since 3.0.3
	 */
	private boolean showValidation = Boolean.TRUE;

	/**
	 * JMeter
	 *
	 * @since 3.0.4
	 */
	private JMeter jmeter;

	/**
	 * Flag to include default HTTP status codes This field controls whether a default set
	 * of HTTP status codes should be included in HTTP responses. If set to true, a
	 * standard set of HTTP status codes (e.g., 200 OK, 404 Not Found) will be
	 * automatically added when handling HTTP responses. If set to false, these status
	 * codes will not be automatically added, requiring manual specification of desired
	 * status codes. The default value is false to avoid unnecessary resource usage where
	 * not required.
	 * @since 3.0.5
	 */
	private boolean addDefaultHttpStatuses;

	/**
	 * Whether to enable the enumeration converter, The default value is false. If true,
	 * the enumeration value is parsed as an enumeration example value in the
	 * header/path/query request mode. If false, take the enumeration name as the
	 * enumeration example value
	 *
	 * @since 3.1.0
	 */
	private boolean enumConvertor = Boolean.FALSE;

	/**
	 * allow self reference recursion
	 *
	 * @since 3.1.0
	 */
	private boolean allowSelfReference = Boolean.FALSE;

	public static ApiConfig getInstance() {
		return instance;
	}

	public static void setInstance(ApiConfig instance) {
		ApiConfig.instance = instance;
	}

	public String getCodePath() {
		return codePath;
	}

	public ApiConfig setCodePath(String codePath) {
		this.codePath = codePath;
		return this;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public ApiConfig setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
		return this;
	}

	public String getPackageExcludeFilters() {
		return packageExcludeFilters;
	}

	public ApiConfig setPackageExcludeFilters(String packageExcludeFilters) {
		this.packageExcludeFilters = packageExcludeFilters;
		return this;
	}

	public String getPathPrefix() {
		return pathPrefix;
	}

	public void setPathPrefix(String pathPrefix) {
		this.pathPrefix = pathPrefix;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public boolean isTornaDebug() {
		return tornaDebug;
	}

	public void setTornaDebug(boolean tornaDebug) {
		this.tornaDebug = tornaDebug;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getAppToken() {
		return appToken;
	}

	public void setAppToken(String appToken) {
		this.appToken = appToken;
	}

	public String getOpenUrl() {
		return openUrl;
	}

	public void setOpenUrl(String openUrl) {
		this.openUrl = openUrl;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public boolean isStrict() {
		return isStrict;
	}

	public void setStrict(boolean strict) {
		isStrict = strict;
	}

	public String getOutPath() {
		return outPath;
	}

	public void setOutPath(String outPath) {
		this.outPath = outPath;
	}

	public List<ApiReqParam> getRequestHeaders() {
		return requestHeaders;
	}

	public void setRequestHeaders(List<ApiReqParam> requestHeaders) {
		this.requestHeaders = requestHeaders;
	}

	public void setRequestHeaders(ApiReqParam... requestHeaders) {
		this.requestHeaders = CollectionUtil.asList(requestHeaders);
		this.requestHeaders.forEach(header -> header.setDesc(header.getDesc() + "(Global)"));
	}

	public List<ApiGroup> getGroups() {
		return groups;
	}

	public ApiConfig setGroups(List<ApiGroup> groups) {
		this.groups = groups;
		return this;
	}

	public ApiConfig setGroups(ApiGroup... groups) {
		this.groups = CollectionUtil.asList(groups);
		return this;
	}

	public List<ApiReqParam> getRequestParams() {
		return requestParams;
	}

	public ApiConfig setRequestParams(List<ApiReqParam> requestParams) {
		this.requestParams = requestParams;
		return this;
	}

	public void setRequestParams(ApiReqParam... requestParams) {
		this.requestParams = CollectionUtil.asList(requestParams);
		this.requestParams.forEach(param -> param.setDesc(param.getDesc() + "(Global)"));
	}

	public List<CustomField> getCustomResponseFields() {
		return customResponseFields;
	}

	public void setCustomResponseFields(List<CustomField> customResponseFields) {
		this.customResponseFields = customResponseFields;
	}

	public void setCustomResponseFields(CustomField... customResponseFields) {
		this.customResponseFields = CollectionUtil.asList(customResponseFields);
	}

	public List<ApiErrorCode> getErrorCodes() {
		return errorCodes;
	}

	public void setErrorCodes(List<ApiErrorCode> errorCodes) {
		this.errorCodes = errorCodes;
	}

	public List<SourceCodePath> getSourceCodePaths() {
		return sourceCodePaths;
	}

	public void setSourceCodePaths(List<SourceCodePath> sourceCodePaths) {
		this.sourceCodePaths = sourceCodePaths;
	}

	public void setSourceCodePaths(SourceCodePath... sourcePaths) {
		this.sourceCodePaths = CollectionUtil.asList(sourcePaths);
	}

	public boolean isAllInOne() {
		return allInOne;
	}

	public void setAllInOne(boolean allInOne) {
		this.allInOne = allInOne;
	}

	public String getPackageFilters() {
		return packageFilters;
	}

	public void setPackageFilters(String packageFilters) {
		this.packageFilters = packageFilters;
	}

	public List<RevisionLog> getRevisionLogs() {
		return revisionLogs;
	}

	public void setRevisionLogs(List<RevisionLog> revisionLogs) {
		this.revisionLogs = revisionLogs;
	}

	public void setRevisionLogs(RevisionLog... revisionLogs) {
		this.revisionLogs = CollectionUtil.asList(revisionLogs);
	}

	public boolean isMd5EncryptedHtmlName() {
		return md5EncryptedHtmlName;
	}

	public void setMd5EncryptedHtmlName(boolean md5EncryptedHtmlName) {
		this.md5EncryptedHtmlName = md5EncryptedHtmlName;
	}

	public DocLanguage getLanguage() {
		return language;
	}

	public void setLanguage(DocLanguage language) {
		this.language = language;
	}

	public boolean isAdoc() {
		return adoc;
	}

	public void setAdoc(boolean adoc) {
		this.adoc = adoc;
	}

	public List<ApiDataDictionary> getDataDictionaries() {
		return dataDictionaries;
	}

	public void setDataDictionaries(List<ApiDataDictionary> dataDictionaries) {
		this.dataDictionaries = dataDictionaries;
	}

	public void setDataDictionaries(ApiDataDictionary... dataDictConfigs) {
		this.dataDictionaries = CollectionUtil.asList(dataDictConfigs);
	}

	public ApiDataDictionary getDataDictionary(String enumClassName) {
		if (Objects.isNull(this.dataDictionaries)) {
			return null;
		}
		return this.dataDictionaries.stream().filter((apiDataDictionary -> {
			boolean equalsName = enumClassName.equalsIgnoreCase(apiDataDictionary.getEnumClassName());

			Set<Class<? extends Enum<?>>> enumImplementSet = apiDataDictionary.getEnumImplementSet();
			if (CollectionUtil.isEmpty(enumImplementSet)) {
				return equalsName;
			}
			Set<String> collect = enumImplementSet.stream().map(Class::getName).collect(Collectors.toSet());
			return equalsName || collect.contains(enumClassName);
		})).findFirst().orElse(null);
	}

	public List<ApiErrorCodeDictionary> getErrorCodeDictionaries() {
		return errorCodeDictionaries;
	}

	public void setErrorCodeDictionaries(List<ApiErrorCodeDictionary> errorCodeDictionaries) {
		this.errorCodeDictionaries = errorCodeDictionaries;
	}

	public void setErrorCodeDictionaries(ApiErrorCodeDictionary... errorCodeDictConfigs) {
		this.errorCodeDictionaries = CollectionUtil.asList(errorCodeDictConfigs);
	}

	public List<ApiObjectReplacement> getApiObjectReplacements() {
		return apiObjectReplacements;
	}

	public void setApiObjectReplacements(List<ApiObjectReplacement> apiObjectReplacements) {
		this.apiObjectReplacements = apiObjectReplacements;
	}

	public void setApiObjectReplacements(ApiObjectReplacement... apiObjectReplaces) {
		this.apiObjectReplacements = CollectionUtil.asList(apiObjectReplaces);
	}

	public List<RpcApiDependency> getRpcApiDependencies() {
		return rpcApiDependencies;
	}

	public void setRpcApiDependencies(List<RpcApiDependency> rpcApiDependencies) {
		this.rpcApiDependencies = rpcApiDependencies;
	}

	public void setRpcApiDependencies(RpcApiDependency... rpcApiDependencies) {
		this.rpcApiDependencies = CollectionUtil.asList(rpcApiDependencies);
	}

	public List<ApiConstant> getApiConstants() {
		return apiConstants;
	}

	public void setApiConstants(List<ApiConstant> apiConstants) {
		this.apiConstants = apiConstants;
	}

	public void setApiConstants(ApiConstant... apiConstants) {
		this.apiConstants = CollectionUtil.asList(apiConstants);
	}

	public boolean isCoverOld() {
		return coverOld;
	}

	public void setCoverOld(boolean coverOld) {
		this.coverOld = coverOld;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectCName() {
		return projectCName;
	}

	public void setProjectCName(String projectCName) {
		this.projectCName = projectCName;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public boolean isSerializeRequestTransients() {
		return serializeRequestTransients;
	}

	public ApiConfig setSerializeRequestTransients(boolean serializeRequestTransients) {
		this.serializeRequestTransients = serializeRequestTransients;
		return this;
	}

	public boolean isSerializeResponseTransients() {
		return serializeResponseTransients;
	}

	public ApiConfig setSerializeResponseTransients(boolean serializeResponseTransients) {
		this.serializeResponseTransients = serializeResponseTransients;
		return this;
	}

	public boolean isShowAuthor() {
		return showAuthor;
	}

	public void setShowAuthor(boolean showAuthor) {
		this.showAuthor = showAuthor;
	}

	public boolean isRequestFieldToUnderline() {
		return requestFieldToUnderline;
	}

	public void setRequestFieldToUnderline(boolean requestFieldToUnderline) {
		this.requestFieldToUnderline = requestFieldToUnderline;
	}

	public boolean isResponseFieldToUnderline() {
		return responseFieldToUnderline;
	}

	public void setResponseFieldToUnderline(boolean responseFieldToUnderline) {
		this.responseFieldToUnderline = responseFieldToUnderline;
	}

	public boolean isSortByTitle() {
		return sortByTitle;
	}

	public void setSortByTitle(boolean sortByTitle) {
		this.sortByTitle = sortByTitle;
	}

	public Boolean getShowJavaType() {
		return showJavaType;
	}

	public void setShowJavaType(Boolean showJavaType) {
		this.showJavaType = showJavaType;
	}

	public String getRpcConsumerConfig() {
		return rpcConsumerConfig;
	}

	public void setRpcConsumerConfig(String rpcConsumerConfig) {
		this.rpcConsumerConfig = rpcConsumerConfig;
	}

	public Boolean getInlineEnum() {
		return inlineEnum;
	}

	public void setInlineEnum(Boolean inlineEnum) {
		this.inlineEnum = inlineEnum;
	}

	public int getRecursionLimit() {
		return recursionLimit;
	}

	public void setRecursionLimit(int recursionLimit) {
		this.recursionLimit = recursionLimit;
	}

	public boolean isRequestExample() {
		return requestExample;
	}

	public void setRequestExample(boolean requestExample) {
		this.requestExample = requestExample;
	}

	public boolean isResponseExample() {
		return responseExample;
	}

	public void setResponseExample(boolean responseExample) {
		this.responseExample = responseExample;
	}

	public String getAllInOneDocFileName() {
		return allInOneDocFileName;
	}

	public void setAllInOneDocFileName(String allInOneDocFileName) {
		this.allInOneDocFileName = allInOneDocFileName;
	}

	public boolean isParamsDataToTree() {
		return paramsDataToTree;
	}

	public void setParamsDataToTree(boolean paramsDataToTree) {
		this.paramsDataToTree = paramsDataToTree;
	}

	public List<String> getIgnoreRequestParams() {
		return ignoreRequestParams;
	}

	public void setIgnoreRequestParams(List<String> ignoreRequestParams) {
		this.ignoreRequestParams = ignoreRequestParams;
	}

	public boolean isDisplayActualType() {
		return displayActualType;
	}

	public void setDisplayActualType(boolean displayActualType) {
		this.displayActualType = displayActualType;
	}

	public BodyAdvice getResponseBodyAdvice() {
		return responseBodyAdvice;
	}

	public void setResponseBodyAdvice(BodyAdvice responseBodyAdvice) {
		this.responseBodyAdvice = responseBodyAdvice;
	}

	public BodyAdvice getRequestBodyAdvice() {
		return requestBodyAdvice;
	}

	public void setRequestBodyAdvice(BodyAdvice requestBodyAdvice) {
		this.requestBodyAdvice = requestBodyAdvice;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public boolean isCreateDebugPage() {
		return createDebugPage;
	}

	public void setCreateDebugPage(boolean createDebugPage) {
		this.createDebugPage = createDebugPage;
	}

	public String getDebugEnvName() {
		return debugEnvName;
	}

	public void setDebugEnvName(String debugEnvName) {
		this.debugEnvName = debugEnvName;
	}

	public String getDebugEnvUrl() {
		return debugEnvUrl;
	}

	public void setDebugEnvUrl(String debugEnvUrl) {
		this.debugEnvUrl = debugEnvUrl;
	}

	public String getUrlSuffix() {
		return urlSuffix;
	}

	public void setUrlSuffix(String urlSuffix) {
		this.urlSuffix = urlSuffix;
	}

	public List<CustomField> getCustomRequestFields() {
		return customRequestFields;
	}

	public ApiConfig setCustomRequestFields(List<CustomField> customRequestFields) {
		this.customRequestFields = customRequestFields;
		return this;
	}

	public void setCustomRequestFields(CustomField... customRequestFields) {
		this.customRequestFields = CollectionUtil.asList(customRequestFields);
	}

	public String getFramework() {
		return framework;
	}

	public void setFramework(String framework) {
		this.framework = framework;
	}

	public Boolean getReplace() {
		return replace;
	}

	public void setReplace(Boolean replace) {
		this.replace = replace;
	}

	public boolean isRequestParamsTable() {
		return requestParamsTable;
	}

	public void setRequestParamsTable(boolean requestParamsTable) {
		this.requestParamsTable = requestParamsTable;
	}

	public boolean isResponseParamsTable() {
		return responseParamsTable;
	}

	public void setResponseParamsTable(boolean responseParamsTable) {
		this.responseParamsTable = responseParamsTable;
	}

	public String getHighlightStyleLink() {
		return highlightStyleLink;
	}

	public void setHighlightStyleLink(String highlightStyleLink) {
		this.highlightStyleLink = highlightStyleLink;
	}

	public String getServerEnv() {
		return serverEnv;
	}

	public void setServerEnv(String serverEnv) {
		this.serverEnv = serverEnv;
	}

	public ICustomJavaMethodHandler getCustomJavaMethodHandler() {
		return customJavaMethodHandler;
	}

	public void setCustomJavaMethodHandler(ICustomJavaMethodHandler customJavaMethodHandler) {
		this.customJavaMethodHandler = customJavaMethodHandler;
	}

	public boolean isRandomMock() {
		return randomMock;
	}

	public void setRandomMock(boolean randomMock) {
		this.randomMock = randomMock;
	}

	public List<SourceCodePath> getJarSourcePaths() {
		return jarSourcePaths;
	}

	public ApiConfig setJarSourcePaths(List<SourceCodePath> jarSourcePaths) {
		this.jarSourcePaths = jarSourcePaths;
		return this;
	}

	public void setJarSourcePaths(SourceCodePath... jarSourcePaths) {
		this.jarSourcePaths = CollectionUtil.asList(jarSourcePaths);
	}

	public ComponentTypeEnum getComponentType() {
		return componentType;
	}

	public ApiConfig setComponentType(ComponentTypeEnum componentType) {
		this.componentType = componentType;
		return this;
	}

	public boolean isIncrement() {
		return increment;
	}

	public void setIncrement(boolean increment) {
		this.increment = increment;
	}

	public String getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	public Integer getApiUploadNums() {
		return apiUploadNums;
	}

	public void setApiUploadNums(Integer apiUploadNums) {
		this.apiUploadNums = apiUploadNums;
	}

	public boolean isShowValidation() {
		return showValidation;
	}

	public void setShowValidation(boolean showValidation) {
		this.showValidation = showValidation;
	}

	public JMeter getJmeter() {
		return jmeter;
	}

	public void setJmeter(JMeter jmeter) {
		this.jmeter = jmeter;
	}

	public boolean isAddDefaultHttpStatuses() {
		return addDefaultHttpStatuses;
	}

	public void setAddDefaultHttpStatuses(boolean addDefaultHttpStatuses) {
		this.addDefaultHttpStatuses = addDefaultHttpStatuses;
	}

	public boolean isEnumConvertor() {
		return enumConvertor;
	}

	public void setEnumConvertor(boolean enumConvertor) {
		this.enumConvertor = enumConvertor;
	}

	public boolean isAllowSelfReference() {
		return allowSelfReference;
	}

	public void setAllowSelfReference(boolean allowSelfReference) {
		this.allowSelfReference = allowSelfReference;
	}

	public OpenApiTagNameTypeEnum getOpenApiTagNameType() {
		if (Objects.isNull(openApiTagNameType)) {
			return OpenApiTagNameTypeEnum.CLASS_NAME;
		}
		return openApiTagNameType;
	}

	public ApiConfig setOpenApiTagNameType(OpenApiTagNameTypeEnum openApiTagNameType) {
		this.openApiTagNameType = openApiTagNameType;
		return this;
	}

}
