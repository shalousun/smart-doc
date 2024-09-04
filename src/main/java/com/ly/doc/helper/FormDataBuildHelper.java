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
package com.ly.doc.helper;

import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.constants.DocTags;
import com.ly.doc.constants.JSRAnnotationConstants;
import com.ly.doc.constants.ParamTypeConstants;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.CustomField;
import com.ly.doc.model.DocJavaField;
import com.ly.doc.model.FormData;
import com.ly.doc.utils.*;
import com.power.common.util.CollectionUtil;
import com.power.common.util.RandomUtil;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;

import java.util.*;

/**
 * @author yu 2019/12/25.
 */
public class FormDataBuildHelper extends BaseHelper {

	/**
	 * build form data
	 * @param className class name
	 * @param registryClasses Class container
	 * @param counter invoked counter
	 * @param builder ProjectDocConfigBuilder
	 * @param pre pre
	 * @param groupClasses group class
	 * @return list of FormData
	 */
	public static List<FormData> getFormData(String className, Map<String, String> registryClasses, int counter,
			ProjectDocConfigBuilder builder, String pre, Set<String> groupClasses) {

		if (StringUtil.isEmpty(className)) {
			throw new RuntimeException("Class name can't be null or empty.");
		}

		ApiConfig apiConfig = builder.getApiConfig();

		ClassLoader classLoader = builder.getApiConfig().getClassLoader();
		List<FormData> formDataList = new ArrayList<>();
		if (counter > apiConfig.getRecursionLimit()) {
			return formDataList;
		}
		// Check circular reference
		if (registryClasses.containsKey(className) && counter > registryClasses.size()) {
			return formDataList;
		}
		// Registry class
		registryClasses.put(className, className);
		counter++;
		boolean requestFieldToUnderline = apiConfig.isRequestFieldToUnderline();
		boolean responseFieldToUnderline = apiConfig.isResponseFieldToUnderline();
		String simpleName = DocClassUtil.getSimpleName(className);
		String[] globGicName = DocClassUtil.getSimpleGicName(className);
		JavaClass cls = builder.getJavaProjectBuilder().getClassByName(simpleName);
		List<DocJavaField> fields = JavaClassUtil.getFields(cls, 0, new LinkedHashMap<>(),
				builder.getApiConfig().getClassLoader());
		if (JavaClassValidateUtil.isPrimitive(simpleName)) {
			FormData formData = new FormData();
			formData.setKey(pre);
			formData.setType(ParamTypeConstants.PARAM_TYPE_TEXT);
			formData.setValue(StringUtil.removeQuotes(RandomUtil.randomValueByType(className)));
			formDataList.add(formData);
			return formDataList;
		}
		if (JavaClassValidateUtil.isCollection(simpleName) || JavaClassValidateUtil.isArray(simpleName)) {
			String gicName = globGicName[0];
			if (JavaClassValidateUtil.isArray(gicName)) {
				gicName = gicName.substring(0, gicName.indexOf("["));
			}
			if (JavaClassValidateUtil.isPrimitive(gicName)) {
				pre = pre.substring(0, pre.lastIndexOf("."));
			}
			formDataList.addAll(getFormData(gicName, registryClasses, counter, builder, pre + "[]", groupClasses));
		}
		int n = 0;
		out: for (DocJavaField docField : fields) {
			JavaField field = docField.getJavaField();
			String fieldName = field.getName();
			String subTypeName = docField.getTypeFullyQualifiedName();
			String fieldGicName = docField.getTypeGenericCanonicalName();
			JavaClass javaClass = field.getType();
			if (field.isStatic() || "this$0".equals(fieldName)
					|| JavaClassValidateUtil.isIgnoreFieldTypes(subTypeName)) {
				continue;
			}
			if (field.isTransient() && apiConfig.isSerializeRequestTransients()) {
				continue;
			}

			List<JavaAnnotation> javaAnnotations = docField.getAnnotations();
			for (JavaAnnotation annotation : javaAnnotations) {
				String simpleAnnotationName = annotation.getType().getValue();
				if (JSRAnnotationConstants.NULL.equals(simpleAnnotationName)) {
					if (CollectionUtil.isEmpty(groupClasses)) {
						continue out;
					}
					Set<String> groupClassList = JavaClassUtil.getParamGroupJavaClass(annotation);
					for (String javaClassName : groupClassList) {
						if (groupClasses.contains(javaClassName)) {
							continue out;
						}
					}
				}
			}

			if (responseFieldToUnderline || requestFieldToUnderline) {
				fieldName = StringUtil.camelToUnderline(fieldName);
			}
			Map<String, String> tagsMap = DocUtil.getFieldTagsValue(field, docField);
			String typeSimpleName = field.getType().getSimpleName();
			if (JavaClassValidateUtil.isMap(subTypeName)) {
				continue;
			}
			String comment = docField.getComment()
					+ JavaFieldUtil.getJsrComment(apiConfig.isShowValidation(), classLoader, javaAnnotations);
			if (JavaClassValidateUtil.isFile(fieldGicName)) {
				FormData formData = new FormData();
				formData.setKey(pre + fieldName);
				formData.setType(ParamTypeConstants.PARAM_TYPE_FILE);
				if (fieldGicName.contains("[]") || fieldGicName.endsWith(">")) {
					comment = comment + "(array of file)";
					formData.setType(ParamTypeConstants.PARAM_TYPE_FILE);
				}
				formData.setDescription(comment);
				formData.setValue("");
				formDataList.add(formData);
			}
			else if (JavaClassValidateUtil.isPrimitive(subTypeName)) {
				String fieldValue = getFieldValueFromMock(tagsMap);
				if (StringUtil.isEmpty(fieldValue)) {
					fieldValue = DocUtil.getValByTypeAndFieldName(typeSimpleName, field.getName());
				}
				CustomField.Key key = CustomField.Key.create(docField.getDeclaringClassName(), fieldName);
				CustomField customRequestField = builder.getCustomReqFieldMap().get(key);
				// cover request value
				if (Objects.nonNull(customRequestField) && Objects.nonNull(customRequestField.getValue())
						&& JavaClassUtil.isTargetChildClass(simpleName, customRequestField.getOwnerClassName())) {
					fieldValue = String.valueOf(customRequestField.getValue());
				}
				FormData formData = new FormData();
				formData.setKey(pre + fieldName);
				formData.setType(ParamTypeConstants.PARAM_TYPE_TEXT);
				formData.setValue(fieldValue);
				formData.setDescription(comment);
				formDataList.add(formData);
			}
			else if (javaClass.isEnum()) {
				Object value = JavaClassUtil.getEnumValue(javaClass, builder, Boolean.TRUE);
				if (tagsMap.containsKey(DocTags.MOCK) && StringUtil.isNotEmpty(tagsMap.get(DocTags.MOCK))) {
					value = ParamUtil.formatMockValue(tagsMap.get(DocTags.MOCK));
				}
				FormData formData = new FormData();
				formData.setKey(pre + fieldName);
				formData.setType(ParamTypeConstants.PARAM_TYPE_TEXT);
				formData.setValue(StringUtil.removeQuotes(String.valueOf(value)));
				formData.setDescription(comment);
				formDataList.add(formData);
			}
			else if (JavaClassValidateUtil.isCollection(subTypeName) || JavaClassValidateUtil.isArray(subTypeName)) {
				String gNameTemp = field.getType().getGenericCanonicalName();
				String[] gNameArr = DocClassUtil.getSimpleGicName(gNameTemp);
				if (gNameArr.length == 0) {
					continue;
				}
				String gName = DocClassUtil.getSimpleGicName(gNameTemp)[0];
				if (JavaClassValidateUtil.isPrimitive(gName)) {
					String fieldValue = getFieldValueFromMock(tagsMap);
					if (StringUtil.isEmpty(fieldValue)) {
						fieldValue = DocUtil.getValByTypeAndFieldName(typeSimpleName, field.getName());
						fieldValue = fieldValue + "," + fieldValue;
					}
					FormData formData = new FormData();
					formData.setKey(pre + fieldName);
					formData.setType(ParamTypeConstants.PARAM_TYPE_TEXT);
					formData.setValue(fieldValue);
					formData.setDescription(comment);
					formDataList.add(formData);
				}
				else {
					if (!simpleName.equals(gName)) {
						if (gName.length() == 1) {
							int len = globGicName.length;
							if (len > 0) {
								String gicName = globGicName[n];
								if (!JavaClassValidateUtil.isPrimitive(gicName) && !simpleName.equals(gicName)) {
									formDataList.addAll(getFormData(gicName, registryClasses, counter, builder,
											pre + fieldName + "[0].", groupClasses));
								}
							}
						}
						else {
							formDataList.addAll(getFormData(gName, registryClasses, counter, builder,
									pre + fieldName + "[0].", groupClasses));
						}
					}
				}
			}
			// else if (subTypeName.length() == 1 ||
			// DocGlobalConstants.JAVA_OBJECT_FULLY.equals(subTypeName)) {
			// For Generics,do nothing, spring mvc not support
			// if (n < globGicName.length) {
			// String gicName = globGicName[n];
			// formDataList.addAll(getFormData(gicName, registryClasses, counter, builder,
			// pre + fieldName + "."));
			// }
			// n++;
			// }
			else {
				formDataList.addAll(getFormData(javaClass.getGenericFullyQualifiedName(), registryClasses, counter,
						builder, pre + fieldName + ".", groupClasses));
			}
		}
		return formDataList;
	}

}
