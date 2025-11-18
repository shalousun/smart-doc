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
package io.github.smartdoc.builder;

import io.github.smartdoc.constants.DocGlobalConstants;
import io.github.smartdoc.constants.HighLightJsConstants;
import io.github.smartdoc.constants.HighlightStyle;
import io.github.smartdoc.helper.JavaProjectBuilderHelper;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.ApiConstant;
import io.github.smartdoc.model.ApiDataDictionary;
import io.github.smartdoc.model.ApiErrorCodeDictionary;
import io.github.smartdoc.model.ApiObjectReplacement;
import io.github.smartdoc.model.BodyAdvice;
import io.github.smartdoc.model.CustomField;
import io.github.smartdoc.model.DocJavaField;
import io.github.smartdoc.model.SourceCodePath;
import io.github.smartdoc.utils.JavaClassUtil;
import com.power.common.constants.Charset;
import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.directorywalker.DirectoryScanner;
import com.thoughtworks.qdox.directorywalker.SuffixFilter;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

/**
 * ProjectDocConfigBuilder
 *
 * @author yu 2019/12/21.
 * @since 1.8.0
 */
public class ProjectDocConfigBuilder {

	/**
	 * Logger
	 */
	private static final Logger log = Logger.getLogger(ProjectDocConfigBuilder.class.getName());

	/**
	 * JavaProjectBuilder
	 */
	private final JavaProjectBuilder javaProjectBuilder;

	/**
	 * classFilesMap
	 */
	private final Map<String, JavaClass> classFilesMap = new ConcurrentHashMap<>();

	/**
	 * enumClassMap
	 */
	private final Map<String, Class<? extends Enum<?>>> enumClassMap = new ConcurrentHashMap<>();

	/**
	 * customRespFieldMap
	 */
	private final Map<CustomField.Key, CustomField> customRespFieldMap = new ConcurrentHashMap<>();

	/**
	 * customReqFieldMap
	 */
	private final Map<CustomField.Key, CustomField> customReqFieldMap = new ConcurrentHashMap<>();

	/**
	 * replaceClassMap
	 */
	private final Map<String, String> replaceClassMap = new ConcurrentHashMap<>();

	/**
	 * constantsMap
	 */
	private final Map<String, String> constantsMap = new ConcurrentHashMap<>();

	/**
	 * serverUrl
	 */
	private final String serverUrl;

	/**
	 * ApiConfig
	 */
	private final ApiConfig apiConfig;

	/**
	 * Constructor
	 * @param apiConfig ApiConfig
	 * @param javaProjectBuilder JavaProjectBuilder
	 */
	public ProjectDocConfigBuilder(ApiConfig apiConfig, JavaProjectBuilder javaProjectBuilder) {
		if (null == apiConfig) {
			throw new NullPointerException("ApiConfig can't be null.");
		}
		this.apiConfig = apiConfig;
		if (Objects.isNull(javaProjectBuilder)) {
			javaProjectBuilder = JavaProjectBuilderHelper.create();
		}

		if (StringUtil.isEmpty(apiConfig.getServerUrl())) {
			this.serverUrl = DocGlobalConstants.DEFAULT_SERVER_URL;
		}
		else {
			this.serverUrl = apiConfig.getServerUrl();
		}
		this.setHighlightStyle();
		javaProjectBuilder.setEncoding(Charset.DEFAULT_CHARSET);
		this.javaProjectBuilder = javaProjectBuilder;
		try {
			this.loadJavaSource(apiConfig, this.javaProjectBuilder);
		}
		catch (Exception e) {
			log.warning(e.getMessage());
		}
		this.initClassFilesMap();
		this.initCustomResponseFieldsMap(apiConfig);
		this.initCustomRequestFieldsMap(apiConfig);
		this.initReplaceClassMap(apiConfig);
		this.initConstants(apiConfig);
		this.initDict(apiConfig);
		this.checkBodyAdvice(apiConfig.getRequestBodyAdvice());
		this.checkBodyAdvice(apiConfig.getResponseBodyAdvice());
	}

	/**
	 * Init data dictionary.
	 * @param apiConfig apiConfig
	 */
	private void initDict(ApiConfig apiConfig) {
		if (enumClassMap.isEmpty()) {
			return;
		}
		List<ApiDataDictionary> dataDictionaries = apiConfig.getDataDictionaries();
		if (Objects.isNull(dataDictionaries)) {
			dataDictionaries = new ArrayList<>();
		}

		for (ApiDataDictionary dataDictionary : dataDictionaries) {
			dataDictionary.setEnumImplementSet(getEnumImplementsByInterface(dataDictionary.getEnumClass()));
		}

		List<ApiErrorCodeDictionary> errorCodeDictionaries = apiConfig.getErrorCodeDictionaries();
		if (Objects.isNull(errorCodeDictionaries)) {
			errorCodeDictionaries = new ArrayList<>();
		}

		for (ApiErrorCodeDictionary errorCodeDictionary : errorCodeDictionaries) {
			errorCodeDictionary
				.setEnumImplementSet(this.getEnumImplementsByInterface(errorCodeDictionary.getEnumClass()));
		}
	}

	/**
	 * Get enum implements by interface.
	 * @param enumClass enumClass
	 * @return enum implements
	 */
	private Set<Class<? extends Enum<?>>> getEnumImplementsByInterface(Class<?> enumClass) {
		if (!enumClass.isInterface()) {
			return Collections.emptySet();
		}
		Set<Class<? extends Enum<?>>> set = new HashSet<>();
		enumClassMap.forEach((k, v) -> {
			if (enumClass.isAssignableFrom(v)) {
				set.add(v);
			}
		});
		return set;
	}

	/**
	 * Get class by name.
	 * @param simpleName simpleName
	 * @return JavaClass
	 */
	public JavaClass getClassByName(String simpleName) {
		JavaClass cls = javaProjectBuilder.getClassByName(simpleName);

		if (!cls.isEnum()) {
			List<DocJavaField> fieldList = JavaClassUtil.getFields(cls, 0, new LinkedHashMap<>(), null);
			// handle inner class
			if (Objects.isNull(cls.getFields()) || fieldList.isEmpty()) {
				cls = classFilesMap.get(simpleName);
				return cls;
			}
		}

		List<JavaClass> classList = cls.getNestedClasses();
		for (JavaClass javaClass : classList) {
			classFilesMap.put(javaClass.getFullyQualifiedName(), javaClass);
		}
		return cls;
	}

	/**
	 * Load java source.
	 * @param config ApiConfig
	 * @param builder JavaProjectBuilder
	 */
	private void loadJavaSource(ApiConfig config, JavaProjectBuilder builder) {
		if (CollectionUtil.isNotEmpty(config.getJarSourcePaths())) {
			for (SourceCodePath path : config.getJarSourcePaths()) {
				loadJarJavaSource(path.getPath(), builder);
			}
		}
		if (CollectionUtil.isEmpty(config.getSourceCodePaths())) {
			builder.addSourceTree(new File(DocGlobalConstants.PROJECT_CODE_PATH));
		}
		else {
			for (SourceCodePath path : config.getSourceCodePaths()) {
				if (null == path) {
					continue;
				}
				String strPath = path.getPath();
				if (StringUtil.isNotEmpty(strPath)) {
					strPath = strPath.replace("\\", DocGlobalConstants.PATH_DELIMITER);
					loadJavaSource(strPath, builder);
				}
			}
		}
	}

	/**
	 * Load jar java source.
	 * @param strPath path
	 * @param builder builder
	 */
	private void loadJavaSource(String strPath, JavaProjectBuilder builder) {
		DirectoryScanner scanner = new DirectoryScanner(new File(strPath));
		scanner.addFilter(new SuffixFilter(".java"));
		scanner.scan(currentFile -> {
			try {
				builder.addSource(currentFile);
			}
			catch (ParseException | IOException e) {
				log.warning(e.getMessage());
			}
		});
	}

	/**
	 * Load jar java source.
	 * @param path path
	 * @param builder builder
	 */
	public void loadJarJavaSource(String path, JavaProjectBuilder builder) {
		OutputStream out;
		if (!path.endsWith(".jar")) {
			return;
		}
		try (JarFile jarFile = new JarFile(path)) {
			builder.setEncoding(Charset.DEFAULT_CHARSET);
			Enumeration<JarEntry> entryEnumeration = jarFile.entries();
			while (entryEnumeration.hasMoreElements()) {
				JarEntry entry = entryEnumeration.nextElement();
				if (entry.getName().endsWith(".java")) {
					InputStream is = jarFile.getInputStream(entry);
					File file = new File(DocGlobalConstants.JAR_TEMP + entry.getName());
					if (!file.exists()) {
						file.getParentFile().mkdirs();
					}
					out = Files.newOutputStream(file.toPath());
					int len;
					while ((len = is.read()) != -1) {
						out.write(len);
					}
					is.close();
					out.close();
				}
			}
			File file = new File(DocGlobalConstants.JAR_TEMP);
			builder.addSourceTree(file);
			deleteDir(file);
		}
		catch (IOException e) {
			log.info("jar" + path + " load  error ,e :" + e);
		}
	}

	/**
	 * Delete dir.
	 * @param file file
	 */
	public static void deleteDir(File file) {
		File[] files = file.listFiles();
		if (file.isFile() || Objects.isNull(files) || files.length == 0) {
			file.delete();
		}
		else {
			for (File f : files) {
				deleteDir(f);
			}
		}
		file.delete();
	}

	/**
	 * Init class files map.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initClassFilesMap() {
		Collection<JavaClass> javaClasses = javaProjectBuilder.getClasses();
		for (JavaClass cls : javaClasses) {
			if (cls.isEnum()) {
				Class enumClass;
				ClassLoader classLoader = apiConfig.getClassLoader();
				try {
					if (Objects.isNull(classLoader)) {
						enumClass = Class.forName(cls.getBinaryName());
					}
					else {
						enumClass = classLoader.loadClass(cls.getBinaryName());
					}
					enumClassMap.put(cls.getFullyQualifiedName(), enumClass);
				}
				catch (ClassNotFoundException | NoClassDefFoundError e) {
					continue;
				}
			}
			classFilesMap.put(cls.getFullyQualifiedName(), cls);
		}
	}

	/**
	 * Init custom response fields map.
	 * @param config config
	 */
	private void initCustomResponseFieldsMap(ApiConfig config) {
		if (CollectionUtil.isNotEmpty(config.getCustomResponseFields())) {
			for (CustomField field : config.getCustomResponseFields()) {
				CustomField.Key key = CustomField.Key.create(field.getOwnerClassName(), field.getName());
				customRespFieldMap.put(key, field);
			}
		}
	}

	/**
	 * Init custom request fields map.
	 * @param config config
	 */
	private void initCustomRequestFieldsMap(ApiConfig config) {
		if (CollectionUtil.isNotEmpty(config.getCustomRequestFields())) {
			for (CustomField field : config.getCustomRequestFields()) {
				CustomField.Key key = CustomField.Key.create(field.getOwnerClassName(), field.getName());
				customReqFieldMap.put(key, field);
			}
		}
	}

	/**
	 * Init replace class map.
	 * @param config config
	 */
	private void initReplaceClassMap(ApiConfig config) {
		if (CollectionUtil.isNotEmpty(config.getApiObjectReplacements())) {
			for (ApiObjectReplacement replace : config.getApiObjectReplacements()) {
				replaceClassMap.put(replace.getClassName(), replace.getReplacementClassName());
			}
		}
	}

	/**
	 * Init constants.
	 * @param config config
	 */
	private void initConstants(ApiConfig config) {
		List<ApiConstant> apiConstants;
		if (CollectionUtil.isEmpty(config.getApiConstants())) {
			apiConstants = new ArrayList<>();
		}
		else {
			apiConstants = config.getApiConstants();
		}
		try {
			for (ApiConstant apiConstant : apiConstants) {
				Class<?> clzz = apiConstant.getConstantsClass();
				if (Objects.isNull(clzz)) {
					if (StringUtil.isEmpty(apiConstant.getConstantsClassName())) {
						throw new RuntimeException("Enum class name can't be null.");
					}
					clzz = Class.forName(apiConstant.getConstantsClassName());
				}
				constantsMap.putAll(JavaClassUtil.getFinalFieldValue(clzz));
			}
		}
		catch (ClassNotFoundException | IllegalAccessException e) {
			log.warning(e.getMessage());
		}
	}

	/**
	 * Check body advice.
	 * @param bodyAdvice body advice
	 */
	private void checkBodyAdvice(BodyAdvice bodyAdvice) {
		if (Objects.nonNull(bodyAdvice) && StringUtil.isNotEmpty(bodyAdvice.getClassName())) {
			if (Objects.nonNull(bodyAdvice.getWrapperClass())) {
				return;
			}
			try {
				Class.forName(bodyAdvice.getClassName());
			}
			catch (ClassNotFoundException e) {
				throw new RuntimeException(
						"Can't find class " + bodyAdvice.getClassName() + " for ResponseBodyAdvice.");
			}
		}
	}

	/**
	 * Set highlight style.
	 */
	private void setHighlightStyle() {
		String style = apiConfig.getStyle();
		if (HighLightJsConstants.HIGH_LIGHT_DEFAULT_STYLE.equals(style)) {
			// use local css file
			apiConfig.setHighlightStyleLink(HighLightJsConstants.HIGH_LIGHT_CSS_DEFAULT);
			return;
		}
		if (HighlightStyle.containsStyle(style)) {
			apiConfig.setHighlightStyleLink(String.format(HighLightJsConstants.HIGH_LIGHT_CSS_URL_FORMAT, style));
			return;
		}
		Random random = new Random();
		if (HighLightJsConstants.HIGH_LIGHT_CSS_RANDOM_LIGHT.equals(style)) {
			// Eliminate styles that do not match the template
			style = HighlightStyle.randomLight(random);
			if (HighlightStyle.containsStyle(style)) {
				apiConfig.setStyle(style);
				apiConfig.setHighlightStyleLink(String.format(HighLightJsConstants.HIGH_LIGHT_CSS_URL_FORMAT, style));
			}
			else {
				apiConfig.setStyle(null);
			}
		}
		else if (HighLightJsConstants.HIGH_LIGHT_CSS_RANDOM_DARK.equals(style)) {
			style = HighlightStyle.randomDark(random);
			if (HighLightJsConstants.HIGH_LIGHT_DEFAULT_STYLE.equals(style)) {
				apiConfig.setHighlightStyleLink(HighLightJsConstants.HIGH_LIGHT_CSS_DEFAULT);
			}
			else {
				apiConfig.setHighlightStyleLink(String.format(HighLightJsConstants.HIGH_LIGHT_CSS_URL_FORMAT, style));
			}
			apiConfig.setStyle(style);
		}
		else {
			// Eliminate styles that do not match the template
			apiConfig.setStyle(null);

		}
	}

	public JavaProjectBuilder getJavaProjectBuilder() {
		return javaProjectBuilder;
	}

	public Map<String, JavaClass> getClassFilesMap() {
		return classFilesMap;
	}

	public Map<CustomField.Key, CustomField> getCustomRespFieldMap() {
		return customRespFieldMap;
	}

	public Map<CustomField.Key, CustomField> getCustomReqFieldMap() {
		return customReqFieldMap;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public ApiConfig getApiConfig() {
		return apiConfig;
	}

	public Map<String, String> getReplaceClassMap() {
		return replaceClassMap;
	}

	public Map<String, Class<? extends Enum<?>>> getEnumClassMap() {
		return enumClassMap;
	}

	public Map<String, String> getConstantsMap() {
		return constantsMap;
	}

}
