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
package com.ly.doc.builder;

import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.constants.DocLanguage;
import com.ly.doc.constants.FrameworkEnum;
import com.ly.doc.constants.TemplateVariable;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.ApiDocDict;
import com.ly.doc.model.ApiErrorCode;
import com.ly.doc.model.RevisionLog;
import com.ly.doc.model.rpc.RpcApiDoc;
import com.ly.doc.utils.BeetlTemplateUtil;
import com.ly.doc.utils.DocUtil;
import com.power.common.util.CollectionUtil;
import com.power.common.util.DateTimeUtil;
import com.power.common.util.FileUtil;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.apache.commons.lang3.StringUtils;
import org.beetl.core.Resource;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author yu 2020/5/16.
 */
public class BaseDocBuilderTemplate {

    public static long NOW = System.currentTimeMillis();

    /**
     * check condition and init
     *
     * @param config       Api config
     * @param checkOutPath check out path
     */
    public void checkAndInit(ApiConfig config, boolean checkOutPath) {
        this.checkAndInitForGetApiData(config);
        if (StringUtil.isEmpty(config.getOutPath()) && checkOutPath) {
            throw new RuntimeException("doc output path can't be null or empty");
        }
        ApiConfig.setInstance(config);
    }

    /**
     * check condition and init for get Data
     *
     * @param config Api config
     */
    public void checkAndInitForGetApiData(ApiConfig config) {
        if (Objects.isNull(config)) {
            throw new NullPointerException("ApiConfig can't be null");
        }
        System.setProperty(DocGlobalConstants.RANDOM_MOCK, String.valueOf(config.isRandomMock()));
        if (Objects.nonNull(config.getLanguage())) {
            System.setProperty(DocGlobalConstants.DOC_LANGUAGE, config.getLanguage().getCode());
        } else {
            // default is chinese
            config.setLanguage(DocLanguage.CHINESE);
            System.setProperty(DocGlobalConstants.DOC_LANGUAGE, DocLanguage.CHINESE.getCode());
        }
        if (Objects.isNull(config.getRevisionLogs())) {
            String strTime = DateTimeUtil.long2Str(NOW, DateTimeUtil.DATE_FORMAT_SECOND);
            config.setRevisionLogs(
                    RevisionLog.builder()
                            .setRevisionTime(strTime)
                            .setAuthor("@" + System.getProperty("user.name"))
                            .setVersion("v" + strTime)
                            .setRemarks("Created by smart-doc")
                            .setStatus("auto")
            );
        }
        if (StringUtil.isEmpty(config.getFramework())) {
            config.setFramework(FrameworkEnum.SPRING.getFramework());
        }
        if (StringUtil.isEmpty(config.getAuthor())) {
            config.setAuthor(System.getProperty("user.name"));
        }
        if (Objects.isNull(config.getReplace())) {
            config.setReplace(Boolean.TRUE);
        }
    }

    public Map<String, String> setDirectoryLanguageVariable(ApiConfig config, Template mapper) {
        Map<String, String> titleMap = new HashMap<>(16);
        if (Objects.nonNull(config.getLanguage())) {
            if (DocLanguage.CHINESE.code.equals(config.getLanguage().getCode())) {
                mapper.binding(TemplateVariable.ERROR_LIST_TITLE.getVariable(), DocGlobalConstants.ERROR_CODE_LIST_CN_TITLE);
                mapper.binding(TemplateVariable.DICT_LIST_TITLE.getVariable(), DocGlobalConstants.DICT_CN_TITLE);
                titleMap.put(TemplateVariable.ERROR_LIST_TITLE.getVariable(), DocGlobalConstants.ERROR_CODE_LIST_CN_TITLE);
                titleMap.put(TemplateVariable.DICT_LIST_TITLE.getVariable(), DocGlobalConstants.DICT_CN_TITLE);
            } else {
                mapper.binding(TemplateVariable.ERROR_LIST_TITLE.getVariable(), DocGlobalConstants.ERROR_CODE_LIST_EN_TITLE);
                mapper.binding(TemplateVariable.DICT_LIST_TITLE.getVariable(), DocGlobalConstants.DICT_EN_TITLE);
                titleMap.put(TemplateVariable.ERROR_LIST_TITLE.getVariable(), DocGlobalConstants.ERROR_CODE_LIST_EN_TITLE);
                titleMap.put(TemplateVariable.DICT_LIST_TITLE.getVariable(), DocGlobalConstants.DICT_EN_TITLE);
            }
        } else {
            mapper.binding(TemplateVariable.ERROR_LIST_TITLE.getVariable(), DocGlobalConstants.ERROR_CODE_LIST_CN_TITLE);
            mapper.binding(TemplateVariable.DICT_LIST_TITLE.getVariable(), DocGlobalConstants.DICT_CN_TITLE);
            titleMap.put(TemplateVariable.ERROR_LIST_TITLE.getVariable(), DocGlobalConstants.ERROR_CODE_LIST_CN_TITLE);
            titleMap.put(TemplateVariable.DICT_LIST_TITLE.getVariable(), DocGlobalConstants.DICT_CN_TITLE);
        }
        return titleMap;
    }

    public void setCssCDN(ApiConfig config, Template template) {
        if (DocLanguage.CHINESE.equals(config.getLanguage())) {
            template.binding(TemplateVariable.CSS_CND.getVariable(), DocGlobalConstants.CSS_CDN_CH);
        } else {
            template.binding(TemplateVariable.CSS_CND.getVariable(), DocGlobalConstants.CSS_CDN);
        }
    }

    /**
     * Binds common variables to the specified template in preparation for document generation.
     * This method populates the template with essential configuration data, timestamps, and
     * various lists derived from the provided configurations and project structure.
     *
     * @param config The API configuration object containing project name, version details, etc.
     * @param javaProjectBuilder A builder for the Java project used to parse project configurations.
     * @param template The template object to which variables will be bound.
     * @param apiDocListEmpty A flag indicating whether the API documentation list is empty.
     */
    public void bindingCommonVariable( ApiConfig config, JavaProjectBuilder javaProjectBuilder,Template template, boolean apiDocListEmpty) {
        String strTime = DateTimeUtil.long2Str(NOW, DateTimeUtil.DATE_FORMAT_SECOND);
        List<ApiErrorCode> errorCodeList = DocUtil.errorCodeDictToList(config, javaProjectBuilder);
        template.binding(TemplateVariable.ERROR_CODE_LIST.getVariable(), errorCodeList);
        template.binding(TemplateVariable.VERSION_LIST.getVariable(), config.getRevisionLogs());
        template.binding(TemplateVariable.DEPENDENCY_LIST.getVariable(), config.getRpcApiDependencies());
        template.binding(TemplateVariable.VERSION.getVariable(), NOW);
        template.binding(TemplateVariable.CREATE_TIME.getVariable(), strTime);
        template.binding(TemplateVariable.PROJECT_NAME.getVariable(), config.getProjectName());
        List<ApiDocDict> apiDocDictList = DocUtil.buildDictionary(config, javaProjectBuilder);
        template.binding(TemplateVariable.DICT_LIST.getVariable(), apiDocDictList);
        int codeIndex = apiDocListEmpty ? 1 : apiDocDictList.size();
        if (CollectionUtil.isNotEmpty(errorCodeList)) {
            template.binding(TemplateVariable.ERROR_CODE_ORDER.getVariable(), ++codeIndex);
        }
        if (CollectionUtil.isNotEmpty(apiDocDictList)) {
            template.binding(TemplateVariable.DICT_ORDER.getVariable(), ++codeIndex);
        }
        setDirectoryLanguageVariable(config, template);
        setCssCDN(config, template);
    }


    public String allInOneDocName(ApiConfig apiConfig, String fileName, String suffix) {
        String allInOneName = apiConfig.getAllInOneDocFileName();
        if (StringUtils.isNotEmpty(apiConfig.getAllInOneDocFileName())) {
            if (allInOneName.endsWith(suffix)) {
                return allInOneName;
            } else {
                return allInOneName + suffix;
            }
        } else if (StringUtil.isNotEmpty(fileName) && fileName.endsWith(suffix)) {
            return fileName;
        } else {
            return fileName + suffix;
        }
    }

    /**
     * Copies jQuery and CSS files to the output directory.
     * This method is utilized during documentation generation to transfer essential CSS and JavaScript files from the resource directory to the output directory.
     * It encompasses the all-in-one stylesheet, jQuery library, highlight.js, and ensures the documentation pages have the necessary styling and functionality.
     *
     * @param config An instance of ApiConfig carrying configuration details including the output path.
     */
    public void copyJQueryAndCss(ApiConfig config) {
        Template indexCssTemplate = BeetlTemplateUtil.getByName(DocGlobalConstants.ALL_IN_ONE_CSS);
        FileUtil.nioWriteFile(indexCssTemplate.render(), config.getOutPath() + DocGlobalConstants.FILE_SEPARATOR + DocGlobalConstants.ALL_IN_ONE_CSS_OUT);
        BaseDocBuilderTemplate.copyJarFile("css/" + DocGlobalConstants.FONT_STYLE, config.getOutPath() + DocGlobalConstants.FILE_SEPARATOR + DocGlobalConstants.FONT_STYLE);
        BaseDocBuilderTemplate.copyJarFile("js/" + DocGlobalConstants.JQUERY, config.getOutPath() + DocGlobalConstants.FILE_SEPARATOR + DocGlobalConstants.JQUERY);
        BaseDocBuilderTemplate.copyJarFile("js/" + DocGlobalConstants.HIGH_LIGHT_JS, config.getOutPath() + DocGlobalConstants.FILE_SEPARATOR + DocGlobalConstants.HIGH_LIGHT_JS);
        BaseDocBuilderTemplate.copyJarFile("css/" + DocGlobalConstants.FONT_STYLE, config.getOutPath() + DocGlobalConstants.FILE_SEPARATOR + DocGlobalConstants.FONT_STYLE);
    }

    public static void copyJarFile(String source, String target) {
        ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader("/template/");
        Resource<?> resource = resourceLoader.getResource(source);
        try (FileWriter fileWriter = new FileWriter(target, false);
             Reader reader = resource.openReader()) {
            char[] c = new char[1024 * 1024];
            int temp;
            int len = 0;
            while ((temp = reader.read()) != -1) {
                c[len] = (char) temp;
                len++;
            }
            reader.close();
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(c, 0, len);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
