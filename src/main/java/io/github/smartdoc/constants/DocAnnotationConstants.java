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
package io.github.smartdoc.constants;

/**
 * java annotations
 *
 * @author yu 2019/9/19.
 */
public interface DocAnnotationConstants {

	/**
	 * jackson JsonIgnore annotation {@code com.fasterxml.jackson.annotation.JsonIgnore}
	 */
	String SHORT_JSON_IGNORE = "JsonIgnore";

	/**
	 * jackson JsonIgnoreProperties annotation
	 * {@code com.fasterxml.jackson.annotation.JsonIgnoreProperties}
	 */
	String SHORT_JSON_IGNORE_PROPERTIES = "JsonIgnoreProperties";

	/**
	 * jackson JsonView annotation {@code com.fasterxml.jackson.annotation.JsonView}
	 */
	String SHORT_JSON_VIEW = "JsonView";

	/**
	 * jackson JsonProperty annotation
	 * {@code com.fasterxml.jackson.annotation.JsonProperty}
	 */
	String SHORT_JSON_PROPERTY = "JsonProperty";

	/**
	 * jackson JsonSerialize annotation
	 * {@code com.fasterxml.jackson.databind.annotation.JsonSerialize}
	 */
	String SHORT_JSON_SERIALIZE = "JsonSerialize";

	/**
	 * the value of `@JsonSerialize(using = ToStringSerializer.class)`
	 */
	String TO_STRING_SERIALIZER_USING = "ToStringSerializer.class";

	/**
	 * issue #484 gson alias annotation
	 */
	String GSON_ALIAS_NAME = "SerializedName";

	/**
	 * fastjson annotation {@code com.alibaba.fastjson.annotation.JSONField}
	 */
	String SHORT_JSON_FIELD = "JSONField";

	/**
	 * required
	 */
	String REQUIRED_PROP = "required";

	/**
	 * com.alibaba.fastjson.annotation.JSONField serialize prop
	 * {@code com.alibaba.fastjson2.annotation.JSONField#serialize}
	 */
	String SERIALIZE_PROP = "serialize";

	/**
	 * com.alibaba.fastjson.annotation.JSONField deserialize prop
	 * {@code com.alibaba.fastjson2.annotation.JSONField#deserialize}
	 */
	String DESERIALIZE_PROP = "deserialize";

	/**
	 * annotation name prop
	 */
	String NAME_PROP = "name";

	/**
	 * annotation value prop
	 */
	String VALUE_PROP = "value";

	/**
	 * annotation using prop
	 */
	String USING_PROP = "using";

	/**
	 * annotation path prop
	 */
	String PATH_PROP = "path";

	/**
	 * annotation groups prop
	 */
	String GROUP_PROP = "groups";

	/**
	 * annotation defaultValue prop
	 */
	String DEFAULT_VALUE_PROP = "defaultValue";

	/**
	 * Deprecated annotation
	 */
	String DEPRECATED = "Deprecated";

	/**
	 * Jackson JsonValue annotation {@code com.fasterxml.jackson.annotation.JsonValue}
	 */
	String JSON_VALUE = "JsonValue";

	/**
	 * Jackson JsonCreator annotation {@code com.fasterxml.jackson.annotation.JsonCreator}
	 */
	String JSON_CREATOR = "JsonCreator";

	/**
	 * Jackson JsonProperty annotation
	 * {@code com.fasterxml.jackson.annotation.JsonProperty}
	 */
	String JSON_PROPERTY = "JsonProperty";

	/**
	 * Jackson JsonNaming annotation
	 * {@code com.fasterxml.jackson.databind.annotation.JsonNaming}
	 */
	String JSON_NAMING = "JsonNaming";

	/**
	 * Jackson JsonFormat annotation {@code com.fasterxml.jackson.annotation.JsonFormat}
	 */
	String JSON_FORMAT = "JsonFormat";

	/**
	 * Jackson JsonFormat annotation pattern prop
	 * {@code com.fasterxml.jackson.annotation.JsonFormat#pattern}
	 */
	String JSON_FORMAT_PATTERN_PROP = "pattern";

	/**
	 * Jackson JsonFormat annotation shape prop
	 * {@code com.fasterxml.jackson.annotation.JsonFormat#shape}
	 */
	String JSON_FORMAT_SHAPE_PROP = "shape";

	/**
	 * Jackson JsonFormat annotation timezone prop
	 * {@code com.fasterxml.jackson.annotation.JsonFormat#timezone}
	 */
	String JSON_FORMAT_TIMEZONE_PROP = "timezone";

	/**
	 * Jackson JsonFormat annotation locale prop
	 * {@code com.fasterxml.jackson.annotation.JsonFormat#locale}
	 */
	String JSON_FORMAT_LOCALE_PROP = "locale";

	/**
	 * Jackson JsonFormat annotation shape prop number
	 * {@code com.fasterxml.jackson.annotation.JsonFormat.Shape#NUMBER}
	 */
	String JSON_FORMAT_SHAPE_NUMBER = "JsonFormat.Shape.NUMBER";

	/**
	 * Jackson JsonFormat annotation shape prop string
	 * {@code com.fasterxml.jackson.annotation.JsonFormat.Shape#STRING}
	 */
	String JSON_FORMAT_SHAPE_STRING = "JsonFormat.Shape.STRING";

	/**
	 * Fastjson JSONType annotation {@code com.alibaba.fastjson2.annotation.JSONType}
	 */
	String SHORT_JSON_TYPE = "JSONType";

	/**
	 * Fastjson JSONType annotation ignores prop
	 * {@code com.alibaba.fastjson2.annotation.JSONType#ignores}
	 */
	String IGNORE_PROP = "ignores";

	/**
	 * `@javax.websocket.OnOpen` or `@jakarta.websocket.OnOpen` annotation name
	 * {@code javax.websocket.OnOpen} {@code jakarta.websocket.OnOpen}
	 */
	String ON_OPEN = "OnOpen";

	/**
	 * `@javax.websocket.OnMessage` or `@jakarta.websocket.OnMessage` annotation name
	 * {@code javax.websocket.OnMessage} {@code jakarta.websocket.OnMessage}
	 */
	String ON_MESSAGE = "OnMessage";

	/**
	 * `@javax.websocket.server.PathParam` or `@jakarta.websocket.PathParam` annotation
	 * name {@code javax.websocket.server.PathParam}
	 * {@code jakarta.websocket.server.PathParam}
	 */
	String PATH_PARAM = "PathParam";

	/**
	 * The name of the 'consumes' attribute in Spring MVC request mapping annotations,
	 * used to specify the media types that the annotated handler method can consume.
	 * <p>
	 * Corresponds to
	 * {@code org.springframework.web.bind.annotation.RequestMapping#consumes()}
	 * </p>
	 */
	String CONSUMES = "consumes";

	/**
	 * The name of the 'produces' attribute in Spring MVC request mapping annotations,
	 * used to specify the media types that the annotated handler method can produce.
	 * <p>
	 * Corresponds to
	 * {@code org.springframework.web.bind.annotation.RequestMapping#produces()}
	 * </p>
	 */
	String PRODUCES = "produces";

	/**
	 * The name of the 'method' attribute in Spring MVC request mapping annotations, used
	 * to specify the HTTP request methods that the annotated handler method supports.
	 * <p>
	 * Corresponds to
	 * {@code org.springframework.web.bind.annotation.RequestMapping#method()}
	 * </p>
	 */
	String METHOD = "method";

	/**
	 * The name of the 'params' attribute in Spring MVC request mapping annotations, used
	 * to specify HTTP request parameters that must be present for the mapping to be
	 * matched.
	 * <p>
	 * Corresponds to
	 * {@code org.springframework.web.bind.annotation.RequestMapping#params()}
	 * </p>
	 */
	String PARAMS = "params";

	/**
	 * Common property names used to define request mapping paths. Typically used in
	 * annotations like {@code @RequestMapping}, {@code @GetMapping}, etc. Corresponds to
	 * 'value' and 'path' attributes which are synonyms in Spring MVC.
	 *
	 * <p>
	 * Used to configure URL mapping paths for handler methods.
	 * </p>
	 */
	String[] PATH_MAPPING_PROPS = { VALUE_PROP, PATH_PROP };

}
