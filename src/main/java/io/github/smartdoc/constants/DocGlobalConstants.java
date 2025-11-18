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

import java.nio.file.FileSystems;

/**
 * doc global constants.
 *
 * @author yu 2018/12/15.
 */
public interface DocGlobalConstants {

	/**
	 * api order.
	 */
	int API_ORDER = 0;

	/**
	 * File separator.
	 */
	String FILE_SEPARATOR = FileSystems.getDefault().getSeparator();

	/**
	 * html api doc out path.
	 */
	String HTML_DOC_OUT_PATH = "src/main/resources/static/doc";

	/**
	 * asciidoc api doc out path.
	 */
	String ADOC_OUT_PATH = "src/docs/asciidoc";

	/**
	 * project code path.
	 */
	String PROJECT_CODE_PATH = "src" + FILE_SEPARATOR + "main" + FILE_SEPARATOR + "java";

	/**
	 * Absolute path of project code.
	 */
	String ABSOLUTE_CODE_PATH = System.getProperty("user.dir") + FILE_SEPARATOR + PROJECT_CODE_PATH;

	/**
	 * Absolute path of target class.
	 */
	String ABSOLUTE_TARGET_CLASS_PATH = System.getProperty("user.dir") + "/target/classes";

	/**
	 * Language.
	 */
	String DOC_LANGUAGE = "smart-doc_language";

	/**
	 * random mock.
	 */
	String RANDOM_MOCK = "randomMock";

	/**
	 * api doc template.
	 */
	String API_DOC_MD_TPL = "ApiDoc.md";

	/**
	 * markdown api file extension.
	 */
	String MARKDOWN_API_FILE_EXTENSION = "Api.md";

	/**
	 * jmeter template.
	 */
	String JMETER_TPL = "JMeter.jmx";

	/**
	 * api doc asciidoc template.
	 */
	String API_DOC_ADOC_TPL = "ApiDoc.adoc";

	/**
	 * all in one api doc markdown template.
	 */
	String ALL_IN_ONE_MD_TPL = "AllInOne.md";

	/**
	 * all in one api doc ascii template.
	 */
	String ALL_IN_ONE_ADOC_TPL = "AllInOne.adoc";

	/**
	 * all in one api doc html template.
	 */
	String ALL_IN_ONE_HTML_TPL = "AllInOne.html";

	/**
	 * all in one api doc word template.
	 */
	String ALL_IN_ONE_WORD_XML_TPL = "word/AllInOneWordTemplate.xml";

	/**
	 * word template xml file.
	 */
	String WORD_XML_TPL = "word/index.xml";

	/**
	 * word error template xml file.
	 */
	String WORD_ERROR_XML_TPL = "word/error.xml";

	/**
	 * word dictionary template xml file.
	 */
	String WORD_DICT_XML_TPL = "word/dict.xml";

	/**
	 * html api doc template.
	 */
	String HTML_API_DOC_TPL = "HtmlApiDoc.html";

	/**
	 * error code list api doc template.
	 */
	String ERROR_CODE_LIST_MD_TPL = "ErrorCodeList.md";

	/**
	 * error code list api doc ascii template.
	 */
	String ERROR_CODE_LIST_ADOC_TPL = "ErrorCodeList.adoc";

	/**
	 * error code list api doc markdown template.
	 */
	String ERROR_CODE_LIST_MD = "ErrorCodeList.md";

	/**
	 * error code list api doc ascii template.
	 */
	String ERROR_CODE_LIST_ADOC = "ErrorCodeList.adoc";

	/**
	 * dictionary api doc template.
	 */
	String DICT_LIST_MD = "Dictionary.md";

	/**
	 * dictionary api doc template.
	 */
	String DICT_LIST_MD_TPL = "Dictionary.md";

	/**
	 * dictionary api doc ascii template.
	 */
	String DICT_LIST_ADOC = "Dictionary.adoc";

	/**
	 * dictionary api doc template.
	 */
	String DICT_LIST_ADOC_TPL = "Dictionary.md";

	/**
	 * search js template.
	 */
	String SEARCH_ALL_JS_TPL = "js/search_all.js.btl";

	/**
	 * search js template.
	 */
	String SEARCH_JS_TPL = "js/search.js.btl";

	/**
	 * search js.
	 */
	String SEARCH_JS_OUT = "search.js";

	/**
	 * debug js template.
	 */
	String DEBUG_JS_TPL = "js/debug.js";

	/**
	 * debug js.
	 */
	String DEBUG_JS_OUT = "debug.js";

	/**
	 * debug page template.
	 */
	String DEBUG_PAGE_TPL = "mock.html";

	/**
	 * debug page template.
	 */
	String DEBUG_PAGE_ALL_TPL = "debug-all.html";

	/**
	 * debug page template.
	 */
	String DEBUG_PAGE_SINGLE_TPL = "html/debug.html";

	/**
	 * index page template.
	 */
	String SINGLE_INDEX_HTML_TPL = "html/index.html";

	/**
	 * error page template.
	 */
	String SINGLE_ERROR_HTML_TPL = "html/error.html";

	/**
	 * dict page template.
	 */
	String SINGLE_DICT_HTML_TPL = "html/dict.html";

	/**
	 * index page template.
	 */
	String ALL_IN_ONE_CSS = "css/AllInOne.css";

	/**
	 * index page.
	 */
	String ALL_IN_ONE_CSS_OUT = "AllInOne.css";

	/**
	 * font style.
	 */
	String FONT_STYLE = "font.css";

	/**
	 * highlight js.
	 */
	String HIGH_LIGHT_JS = "highlight.min.js";

	/**
	 * jquery.
	 */
	String JQUERY = "jquery.min.js";

	/**
	 * highlight style.
	 */
	String HIGH_LIGHT_STYLE = "xt256.min.css";

	/**
	 * rpc out dir.
	 */
	String RPC_OUT_DIR = "rpc";

	/**
	 * javadoc out dir.
	 */
	String JAVADOC_OUT_DIR = "javadoc";

	/**
	 * javadoc all in one html template.
	 */
	String JAVADOC_ALL_IN_ONE_HTML_TPL = "javadoc/JavadocAllInOne.html";

	/**
	 * javadoc all in one search template.
	 */
	String JAVADOC_ALL_IN_ONE_SEARCH_TPL = "javadoc/JavadocSearch.btl";

	/**
	 * javadoc all in one markdown template.
	 */
	String JAVADOC_ALL_IN_ONE_MD_TPL = "javadoc/JavadocAllInOne.md";

	/**
	 * javadoc api doc template.
	 */
	String JAVADOC_API_DOC_MD_TPL = "javadoc/Javadoc.md";

	/**
	 * javadoc all in one ascii template.
	 */
	String JAVADOC_ALL_IN_ONE_ADOC_TPL = "javadoc/JavadocAllInOne.adoc";

	/**
	 * javadoc api doc ascii template.
	 */
	String JAVADOC_API_DOC_ADOC_TPL = "javadoc/Javadoc.adoc";

	/**
	 * rpc api doc asciidoc template.
	 */
	String RPC_API_DOC_ADOC_TPL = "dubbo/Dubbo.adoc";

	/**
	 * rpc all in one ascii template.
	 */
	String RPC_ALL_IN_ONE_ADOC_TPL = "dubbo/DubboAllInOne.adoc";

	/**
	 * rpc all in one word template.
	 */
	String RPC_ALL_IN_ONE_WORD_TPL = "dubbo/DubboAllInOneWordTemplate.xml";

	/**
	 * rpc all in one html template.
	 */
	String RPC_ALL_IN_ONE_HTML_TPL = "dubbo/DubboAllInOne.html";

	/**
	 * rpc all in one search template.
	 */
	String RPC_ALL_IN_ONE_SEARCH_TPL = "dubbo/DubboSearch.btl";

	/**
	 * rpc api dependency template.
	 */
	String RPC_DEPENDENCY_MD_TPL = "dubbo/DubboApiDependency.md";

	/**
	 * rpc api dependency template.
	 */
	String RPC_DEPENDENCY_EMPTY_MD_TPL = "dubbo/DubboApiDependencyEmpty.md";

	/**
	 * rpc api doc markdown template.
	 */
	String RPC_API_DOC_MD_TPL = "dubbo/Dubbo.md";

	/**
	 * rpc all in one markdown template.
	 */
	String RPC_ALL_IN_ONE_MD_TPL = "dubbo/DubboAllInOne.md";

	/**
	 * rpc index template.
	 */
	String RPC_INDEX_TPL = "dubbo/DubboIndex.btl";

	/**
	 * postman json.
	 */
	String POSTMAN_JSON = "/postman.json";

	/**
	 * open api json.
	 */
	String OPEN_API_JSON = "/openapi.json";

	/**
	 * spring ModelAndView.
	 */
	String MODE_AND_VIEW_FULLY = "org.springframework.web.servlet.ModelAndView";

	/**
	 * feign client.
	 */
	String FEIGN_CLIENT_FULLY = "org.springframework.cloud.netflix.feign.FeignClient";

	/**
	 * feign client.
	 */
	String FEIGN_CLIENT = "FeignClient";

	/**
	 * default version.
	 */
	String DEFAULT_VERSION = "-";

	/**
	 * error code list chinese title.
	 */
	String ERROR_CODE_LIST_CN_TITLE = "错误码列表";

	/**
	 * error code list english title.
	 */
	String ERROR_CODE_LIST_EN_TITLE = "Error Code List";

	/**
	 * dictionary chinese title.
	 */
	String DICT_CN_TITLE = "数据字典";

	/**
	 * dictionary english title.
	 */
	String DICT_EN_TITLE = "Data Dictionaries";

	/**
	 * field space.
	 */
	String FIELD_SPACE = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";

	/**
	 * any object msg.
	 */
	String ANY_OBJECT_MSG = "any object.";

	/**
	 * no comments found.
	 */
	String NO_COMMENTS_FOUND = "No comments found.";

	/**
	 * spring web annotation package.
	 */
	String SPRING_WEB_ANNOTATION_PACKAGE = "org.springframework.web.bind.annotation";

	/**
	 * postman mode formdata.
	 */
	String POSTMAN_MODE_FORMDATA = "formdata";

	/**
	 * postman mode raw.
	 */
	String POSTMAN_MODE_RAW = "raw";

	/**
	 * short multipart file.
	 */
	String SHORT_MULTIPART_FILE_FULLY = "MultipartFile";

	/**
	 * empty.
	 */
	String EMPTY = "";

	/**
	 * default server url.
	 */
	String DEFAULT_SERVER_URL = EMPTY;

	/**
	 * short request body.
	 */
	String SHORT_REQUEST_BODY = "RequestBody";

	/**
	 * curl request type.
	 */
	String CURL_REQUEST_TYPE = "curl -X %s %s -i %s";

	/**
	 * curl request type data.
	 */
	String CURL_REQUEST_TYPE_DATA = "curl -X %s %s -i %s --data '%s'";

	/**
	 * curl post put json.
	 */
	String CURL_POST_PUT_JSON = "curl -X %s -H 'Content-Type: application/json;charset=UTF-8' %s -i %s --data '%s'";

	/**
	 * css cdn ch.
	 */
	String CSS_CDN_CH = "https://fonts.googleapis.cnpmjs.org";

	/**
	 * css cdn.
	 */
	String CSS_CDN = "https://fonts.googleapis.com";

	/**
	 * path delimiter.
	 */
	String PATH_DELIMITER = "/";

	/**
	 * multi url separator.
	 */
	String MULTI_URL_SEPARATOR = ";\t";

	/**
	 * param prefix.
	 */
	String PARAM_PREFIX = "└─";

	/**
	 * openapi 2.0.0 component key.
	 */
	String OPENAPI_2_COMPONENT_KRY = "#/definitions/";

	/**
	 * openapi 3.0.0 component key.
	 */
	String OPENAPI_3_COMPONENT_KRY = "#/components/schemas/";

	/**
	 * default swagger tag.
	 */
	String SWAGGER_FILE_TAG = "formData";

	/**
	 * default openapi tag.
	 */
	String OPENAPI_TAG = "default";

	/**
	 * default filter method.
	 */
	String DEFAULT_FILTER_METHOD = "*";

	/**
	 * jar temp path.
	 */
	String JAR_TEMP = "./smart-temp/";

	/**
	 * default primitive.
	 */
	String DEFAULT_PRIMITIVE = "defaultPrimitive";

	/**
	 * date format.
	 */
	String DATE_FORMAT_YYYY_MM_DD_HH_MM = "yyyyMMddHHmm";

	/**
	 * markdown extension.
	 */
	String MARKDOWN_EXTENSION = ".md";

	/**
	 * websocket all in one markdown template.
	 */
	String WEBSOCKET_ALL_IN_ONE_MD_TPL = "websocket/WebSocketAllOne.md";

	/**
	 * websocket markdown template.
	 */
	String WEBSOCKET_MD_TPL = "websocket/WebSocket.md";

	/**
	 * websocket all in one html template.
	 */
	String WEBSOCKET_ALL_IN_ONE_HTML_TPL = "websocket/WebSocketAllInOne.html";

	/**
	 * websocket all in one search template.
	 */
	String WEBSOCKET_ALL_IN_ONE_SEARCH_TPL = "websocket/WebSocketSearch.btl";

	/**
	 * websocket api doc Asciidoc template.
	 */
	String WEBSOCKET_API_DOC_ADOC_TPL = "websocket/WebSocket.adoc";

	/**
	 * websocket all in one Asciidoc template.
	 */
	String WEBSOCKET_ALL_IN_ONE_ADOC_TPL = "websocket/WebSocketAllInOne.adoc";

	/**
	 * websocket api out dir.
	 */
	String WEBSOCKET_OUT_DIR = "websocket";

	/**
	 * grpc all in one md template.
	 */
	String GRPC_ALL_IN_ONE_MD_TPL = "grpc/GrpcAllInOne.md";

	/**
	 * grpc md template.
	 */
	String GRPC_API_MD_TPL = "grpc/Grpc.md";

	/**
	 * grpc all in one html template.
	 */
	String GRPC_ALL_IN_ONE_HTML_TPL = "grpc/GrpcAllInOne.html";

	/**
	 * grpc all in one search template.
	 */
	String GRPC_ALL_IN_ONE_SEARCH_TPL = "grpc/GrpcSearch.btl";

	/**
	 * grpc api doc Asciidoc template.
	 */
	String GRPC_API_DOC_ADOC_TPL = "grpc/Grpc.adoc";

	/**
	 * grpc all in one Asciidoc template.
	 */
	String GRPC_ALL_IN_ONE_ADOC_TPL = "grpc/GrpcAllInOne.adoc";

	/**
	 * grpc api out dir.
	 */
	String GRPC_OUT_DIR = "grpc";

	/**
	 * proto file suffix.
	 */
	String PROTO_FILE_SUFFIX = ".proto";

	/**
	 * asciidoc extension.
	 */
	String ASCIIDOC_EXTENSION = ".adoc";

	/**
	 * asciidoc extension.
	 */
	String WORD_DOC_EXTENSION = ".docx";

	/**
	 * default map key desc.
	 */
	String DEFAULT_MAP_KEY_DESC = "A map key.";

	/**
	 * object map value warning.
	 */
	String OBJECT_MAP_VALUE_WARNING = "{\"warning\":\"Using java.util.Object as a Map value is not recommended. "
			+ "Smart-doc cannot process it properly. Please use a specific type for better documentation generation.\"}";

	/**
	 * object list warning.
	 */
	String GENERIC_LIST_WARNING = "{\"warning\":\"Using java.util.Object in a List instead of a specific generic type is not recommended. "
			+ "Smart-doc cannot display the correct generics. Please specify the generic type for better documentation generation.\"}";

}
