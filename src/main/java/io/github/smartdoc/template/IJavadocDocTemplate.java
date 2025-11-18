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
import io.github.smartdoc.constants.JavaTypeConstants;
import io.github.smartdoc.constants.ParamTypeConstants;
import io.github.smartdoc.helper.ParamsBuildHelper;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.ApiParam;
import io.github.smartdoc.model.DocJavaMethod;
import io.github.smartdoc.model.JavadocJavaMethod;
import io.github.smartdoc.utils.ApiParamTreeUtil;
import io.github.smartdoc.utils.DocClassUtil;
import io.github.smartdoc.utils.DocUtil;
import io.github.smartdoc.utils.JavaClassUtil;
import io.github.smartdoc.utils.JavaClassValidateUtil;
import io.github.smartdoc.utils.JavaFieldUtil;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.smartdoc.constants.DocTags.DEPRECATED;
import static io.github.smartdoc.constants.DocTags.IGNORE;

/**
 * java doc template
 *
 * @param <T> extends JavadocJavaMethod
 * @author shalousun
 * @since 3.0.5
 */
public interface IJavadocDocTemplate<T extends JavadocJavaMethod> extends IBaseDocBuildTemplate {

	/**
	 * Add method modifiers
	 * @return boolean
	 */
	boolean addMethodModifiers();

	/**
	 * Create empty JavadocJavaMethod.
	 * @return empty JavadocJavaMethod
	 */
	T createEmptyJavadocJavaMethod();

	/**
	 * Convert JavaMethod to JavadocJavaMethod
	 * @param apiConfig ApiConfig
	 * @param method JavaMethod
	 * @param actualTypesMap Map
	 * @return JavadocJavaMethod
	 */
	default T convertToJavadocJavaMethod(ApiConfig apiConfig, JavaMethod method, Map<String, JavaType> actualTypesMap) {
		JavaClass cls = method.getDeclaringClass();
		T javadocJavaMethod = this.createEmptyJavadocJavaMethod();
		javadocJavaMethod.setJavaMethod(method);
		javadocJavaMethod.setName(method.getName());
		javadocJavaMethod.setActualTypesMap(actualTypesMap);
		String methodDefine = this.methodDefinition(method, actualTypesMap);
		String scapeMethod = methodDefine.replaceAll("<", "&lt;");
		scapeMethod = scapeMethod.replaceAll(">", "&gt;");

		javadocJavaMethod.setMethodDefinition(methodDefine);
		javadocJavaMethod.setEscapeMethodDefinition(scapeMethod);
		javadocJavaMethod.setDesc(DocUtil.getEscapeAndCleanComment(method.getComment()));
		// set detail
		String apiNoteValue = DocUtil.getNormalTagComments(method, DocTags.API_NOTE, cls.getName());
		if (StringUtil.isEmpty(apiNoteValue)) {
			apiNoteValue = method.getComment();
		}
		String version = DocUtil.getNormalTagComments(method, DocTags.SINCE, cls.getName());
		javadocJavaMethod.setVersion(version);
		javadocJavaMethod.setDetail(apiNoteValue != null ? apiNoteValue : "");
		// set author
		String authorValue = DocUtil.getNormalTagComments(method, DocTags.AUTHOR, cls.getName());
		if (apiConfig.isShowAuthor() && StringUtil.isNotEmpty(authorValue)) {
			javadocJavaMethod.setAuthor(authorValue);
		}

		// Deprecated
		List<JavaAnnotation> annotations = method.getAnnotations();
		for (JavaAnnotation annotation : annotations) {
			String annotationName = annotation.getType().getName();
			if (DocAnnotationConstants.DEPRECATED.equals(annotationName)) {
				javadocJavaMethod.setDeprecated(true);
			}
		}
		if (Objects.nonNull(method.getTagByName(DEPRECATED))) {
			javadocJavaMethod.setDeprecated(true);
		}
		return javadocJavaMethod;
	}

	/**
	 * Get method definition
	 * @param method JavaMethod
	 * @param actualTypesMap Map
	 * @return String
	 */
	default String methodDefinition(JavaMethod method, Map<String, JavaType> actualTypesMap) {
		StringBuilder methodBuilder = new StringBuilder();
		if (this.addMethodModifiers()) {
			// append method modifiers
			method.getModifiers().forEach(item -> methodBuilder.append(item).append(" "));
		}
		String returnType = this.getMethodReturnType(method, actualTypesMap);
		// append method return type
		methodBuilder.append(returnType).append(" ");
		List<String> params = new ArrayList<>();
		List<JavaParameter> parameters = method.getParameters();
		for (JavaParameter parameter : parameters) {
			String typeName = this.replaceTypeName(parameter.getType().getGenericValue(), actualTypesMap, Boolean.TRUE);
			params.add(typeName + " " + parameter.getName());
		}
		methodBuilder.append(method.getName()).append("(").append(String.join(", ", params)).append(")");
		return methodBuilder.toString();
	}

	/**
	 * Processes methods of a given class and its parent or interfaces.
	 * @param cls JavaClass
	 * @param methodProcessor Function to process methods from a class
	 * @return List of documented Java methods
	 */
	default List<T> processClassHierarchy(JavaClass cls, Function<JavaClass, List<T>> methodProcessor) {
		List<T> docJavaMethods = new ArrayList<>();
		Set<JavaClass> classesToProcess = new LinkedHashSet<>();
		classesToProcess.add(cls);

		while (!classesToProcess.isEmpty()) {
			JavaClass currentClass = classesToProcess.iterator().next();
			classesToProcess.remove(currentClass);

			// Process methods
			docJavaMethods.addAll(methodProcessor.apply(currentClass));

			// Add parent class if not Object
			JavaClass parentClass = currentClass.getSuperJavaClass();
			if (Objects.nonNull(parentClass)
					&& !JavaTypeConstants.OBJECT_SIMPLE_NAME.equals(parentClass.getSimpleName())) {
				classesToProcess.add(parentClass);
			}

			// Add interfaces
			classesToProcess.addAll(currentClass.getInterfaces());
		}

		return docJavaMethods;
	}

	/**
	 * Get parent class and interface methods
	 * @param apiConfig ApiConfig
	 * @param cls JavaClass
	 * @return List
	 */
	default List<T> getParentsClassAndInterfaceMethods(ApiConfig apiConfig, JavaClass cls) {
		return this.processClassHierarchy(cls, currentClass -> {
			List<T> docJavaMethods = new ArrayList<>();
			Map<String, JavaType> actualTypesMap = JavaClassUtil.getActualTypesMap(currentClass);
			List<JavaMethod> methodList = currentClass.getMethods();
			for (JavaMethod method : methodList) {
				docJavaMethods.add(this.convertToJavadocJavaMethod(apiConfig, method, actualTypesMap));
			}
			return docJavaMethods;
		});

	}

	/**
	 * Constructs a list of request parameters.
	 * @param javaMethod The JavaMethod object, used to extract method information.
	 * @param builder The ProjectDocConfigBuilder object, containing project configuration
	 * details.
	 * @param atomicInteger An AtomicInteger, used to generate unique parameter IDs.
	 * @param actualTypesMap A map of actual types, used for type replacement.
	 * @return A List of ApiParam objects representing the request parameters or null if
	 * no parameters exist.
	 */
	default List<ApiParam> requestParams(final JavaMethod javaMethod, ProjectDocConfigBuilder builder,
			AtomicInteger atomicInteger, Map<String, JavaType> actualTypesMap) {
		boolean isStrict = builder.getApiConfig().isStrict();
		boolean isShowJavaType = builder.getApiConfig().getShowJavaType();
		boolean isShowValidation = builder.getApiConfig().isShowValidation();
		String className = javaMethod.getDeclaringClass().getCanonicalName();
		Map<String, String> paramTagMap = DocUtil.getCommentsByTag(javaMethod, DocTags.PARAM, className);
		List<JavaParameter> parameterList = javaMethod.getParameters();
		if (parameterList.isEmpty()) {
			return null;
		}
		ClassLoader classLoader = builder.getApiConfig().getClassLoader();
		List<ApiParam> paramList = new ArrayList<>();
		for (JavaParameter parameter : parameterList) {
			boolean required = false;
			String paramName = parameter.getName();
			String typeName = this.replaceTypeName(parameter.getType().getGenericCanonicalName(), actualTypesMap,
					Boolean.FALSE);
			String simpleName = this.replaceTypeName(parameter.getType().getValue(), actualTypesMap, Boolean.FALSE)
				.toLowerCase();
			String fullTypeName = this.replaceTypeName(parameter.getType().getFullyQualifiedName(), actualTypesMap,
					Boolean.FALSE);
			String paramPre = paramName + ".";
			if (!paramTagMap.containsKey(paramName) && JavaClassValidateUtil.isPrimitive(fullTypeName) && isStrict) {
				throw new RuntimeException("ERROR: Unable to find javadoc @param for actual param \"" + paramName
						+ "\" in method " + javaMethod.getName() + " from " + className);
			}
			StringBuilder comment = new StringBuilder(this.paramCommentResolve(paramTagMap.get(paramName)));
			String mockValue = JavaFieldUtil.createMockValue(paramTagMap, paramName, typeName, typeName);
			JavaClass javaClass = builder.getJavaProjectBuilder().getClassByName(fullTypeName);
			List<JavaAnnotation> annotations = parameter.getAnnotations();
			for (JavaAnnotation a : annotations) {
				if (JavaClassValidateUtil.isJSR303Required(a.getType().getValue())) {
					required = true;
				}
			}
			comment.append(JavaFieldUtil.getJsrComment(isShowValidation, classLoader, annotations));
			Set<String> groupClasses = JavaClassUtil.getParamGroupJavaClass(annotations,
					builder.getJavaProjectBuilder());
			Set<String> paramJsonViewClasses = JavaClassUtil.getParamJsonViewClasses(annotations, builder);
			if (JavaClassValidateUtil.isCollection(fullTypeName) || JavaClassValidateUtil.isArray(fullTypeName)) {
				if (JavaClassValidateUtil.isCollection(typeName)) {
					typeName = typeName + "<T>";
				}
				String[] gicNameArr = DocClassUtil.getSimpleGicName(typeName);
				String gicName = gicNameArr[0];
				if (JavaClassValidateUtil.isArray(gicName)) {
					gicName = gicName.substring(0, gicName.indexOf("["));
				}
				if (JavaClassValidateUtil.isPrimitive(gicName)) {
					String processedType = isShowJavaType ? JavaClassUtil.getClassSimpleName(typeName)
							: DocClassUtil.processTypeNameForParams(simpleName);
					ApiParam param = ApiParam.of()
						.setId(atomicInteger.incrementAndGet())
						.setField(paramName)
						.setDesc(comment + "   (children type : " + gicName + ")")
						.setRequired(required)
						.setType(processedType);
					paramList.add(param);
				}
				else {
					paramList.addAll(ParamsBuildHelper.buildParams(gicNameArr[0], paramPre, 0, "true", Boolean.FALSE,
							new HashMap<>(16), builder, groupClasses, paramJsonViewClasses, 0, Boolean.FALSE,
							atomicInteger));
				}
			}
			else if (JavaClassValidateUtil.isPrimitive(fullTypeName)) {
				ApiParam param = ApiParam.of()
					.setId(atomicInteger.incrementAndGet())
					.setField(paramName)
					.setType(JavaClassUtil.getClassSimpleName(typeName))
					.setDesc(comment.toString())
					.setRequired(required)
					.setMaxLength(JavaFieldUtil.getParamMaxLength(classLoader, parameter.getAnnotations()))
					.setValue(mockValue)
					.setVersion(DocGlobalConstants.DEFAULT_VERSION);
				paramList.add(param);
			}
			else if (JavaClassValidateUtil.isMap(fullTypeName)) {
				if (JavaClassValidateUtil.isMap(typeName)) {
					ApiParam apiParam = ApiParam.of()
						.setId(atomicInteger.incrementAndGet())
						.setField(paramName)
						.setType(typeName)
						.setDesc(comment.toString())
						.setRequired(required)
						.setVersion(DocGlobalConstants.DEFAULT_VERSION);
					paramList.add(apiParam);
					continue;
				}
				String[] gicNameArr = DocClassUtil.getSimpleGicName(typeName);
				paramList.addAll(ParamsBuildHelper.buildParams(gicNameArr[1], paramPre, 0, "true", Boolean.FALSE,
						new HashMap<>(16), builder, groupClasses, paramJsonViewClasses, 0, Boolean.FALSE,
						atomicInteger));
			}
			else if (javaClass.isEnum()) {
				ApiParam param = ApiParam.of()
					.setId(atomicInteger.incrementAndGet())
					.setField(paramName)
					.setType(ParamTypeConstants.PARAM_TYPE_ENUM)
					.setRequired(required)
					.setDesc(comment.toString())
					.setVersion(DocGlobalConstants.DEFAULT_VERSION);
				paramList.add(param);
			}
			else {
				paramList.addAll(
						ParamsBuildHelper.buildParams(typeName, paramPre, 0, "true", Boolean.FALSE, new HashMap<>(16),
								builder, groupClasses, paramJsonViewClasses, 0, Boolean.FALSE, atomicInteger));
			}
		}
		return paramList;
	}

	/**
	 * Builds a list of service methods. This method parses the given Java class, extracts
	 * methods that meet certain criteria, and generates corresponding JavadocJavaMethod
	 * objects for them.
	 * @param cls The Java class to parse.
	 * @param apiConfig The API configuration object, containing rules for documentation
	 * generation.
	 * @param projectBuilder The project documentation configuration builder, used to
	 * construct project-level documentation configurations.
	 * @return A list containing documented methods represented as JavadocJavaMethod
	 * objects.
	 */
	default List<T> buildServiceMethod(final JavaClass cls, ApiConfig apiConfig,
			ProjectDocConfigBuilder projectBuilder) {
		String clsCanonicalName = cls.getCanonicalName();
		List<JavaMethod> methods = cls.getMethods();
		List<T> methodDocList = new ArrayList<>(methods.size());

		Set<String> filterMethods = DocUtil.findFilterMethods(clsCanonicalName);
		boolean needAllMethods = filterMethods.contains(DocGlobalConstants.DEFAULT_FILTER_METHOD);

		for (JavaMethod method : methods) {
			if (method.isPrivate()) {
				continue;
			}
			if (Objects.nonNull(method.getTagByName(IGNORE))) {
				continue;
			}

			// skip method
			boolean skipMethod = this.skipMethod(cls, method, apiConfig, projectBuilder);
			if (skipMethod) {
				continue;
			}

			if (StringUtil.isEmpty(method.getComment()) && apiConfig.isStrict()) {
				throw new RuntimeException(
						"Unable to find comment for method " + method.getName() + " in " + cls.getCanonicalName());
			}
			if (needAllMethods || filterMethods.contains(method.getName())) {
				T apiMethodDoc = this.convertToJavadocJavaMethod(apiConfig, method, null);
				methodDocList.add(apiMethodDoc);
			}

		}
		// Add parent class And interface methods
		methodDocList.addAll(this.getParentsClassAndInterfaceMethods(apiConfig, cls));

		Map<T, List<ApiParam>> methodRequestParams = new HashMap<>(16);
		Map<T, List<ApiParam>> methodResponseParams = new HashMap<>(16);

		// Construct the method map
		Map<String, T> methodMap = methodDocList.stream().collect(Collectors.toMap(method -> {
			// Build request params
			List<ApiParam> requestParams = this.requestParams(method.getJavaMethod(), projectBuilder,
					new AtomicInteger(0), method.getActualTypesMap());
			methodRequestParams.put(method, requestParams);
			// Build response params
			List<ApiParam> responseParams = this.buildReturnApiParams(DocJavaMethod.builder()
				.setJavaMethod(method.getJavaMethod())
				.setActualTypesMap(method.getActualTypesMap()), projectBuilder);
			methodResponseParams.put(method, responseParams);
			String requestParamsStr = Objects.isNull(requestParams) ? "null"
					: requestParams.stream().map(ApiParam::getFullyTypeName).collect(Collectors.joining("|"));

			String responseParamsString = (Objects.isNull(responseParams) ? "null"
					: responseParams.stream().map(ApiParam::getFullyTypeName).collect(Collectors.joining("|")));
			return requestParamsStr + " " + method.getName() + "(" + responseParamsString + ")";
		},

				Function.identity(), this::mergeJavadocMethods, LinkedHashMap::new));

		int methodOrder = 0;
		List<T> javadocJavaMethods = new ArrayList<>(methodMap.size());
		for (T method : methodMap.values()) {
			methodOrder++;
			method.setOrder(methodOrder);
			String methodUid = DocUtil.generateId(clsCanonicalName + method.getName() + methodOrder);
			method.setMethodId(methodUid);
			// build request params
			List<ApiParam> requestParams = methodRequestParams.get(method);
			// build response params
			List<ApiParam> responseParams = methodResponseParams.get(method);
			if (apiConfig.isParamsDataToTree()) {
				method.setRequestParams(ApiParamTreeUtil.apiParamToTree(requestParams));
				method.setResponseParams(ApiParamTreeUtil.apiParamToTree(responseParams));
			}
			else {
				method.setRequestParams(requestParams);
				method.setResponseParams(responseParams);
			}
			javadocJavaMethods.add(method);
		}
		return javadocJavaMethods;
	}

	/**
	 * Merges two JavadocJavaMethod objects. If the existing method lacks certain details
	 * (description, detail, author, version) that the replacement method has, those
	 * details are copied from the replacement method to the existing method.
	 * @param existing The existing JavadocJavaMethod object.
	 * @param replacement The replacement JavadocJavaMethod object.
	 * @return The merged JavadocJavaMethod object, with details filled in from the
	 * replacement method if necessary.
	 */
	default T mergeJavadocMethods(T existing, T replacement) {
		// if existing info is empty and replacement info has desc,replace the info
		if (StringUtil.isEmpty(existing.getDesc()) && StringUtil.isNotEmpty(replacement.getDesc())) {
			existing.setDesc(replacement.getDesc());
		}
		if (StringUtil.isEmpty(existing.getDetail()) && StringUtil.isNotEmpty(replacement.getDetail())) {
			existing.setDetail(replacement.getDetail());
		}
		if (StringUtil.isEmpty(existing.getAuthor()) && StringUtil.isNotEmpty(replacement.getAuthor())) {
			existing.setAuthor(replacement.getAuthor());
		}
		if (StringUtil.isEmpty(existing.getVersion()) && StringUtil.isNotEmpty(replacement.getVersion())) {
			existing.setVersion(replacement.getVersion());
		}
		return existing;
	}

	/**
	 * Determines whether the specified method should be skipped during documentation
	 * generation.
	 * <p>
	 * If this method returns {@code true}, the method will be excluded from the generated
	 * documentation. If it returns {@code false}, the method will be included.
	 * </p>
	 * <p>
	 * The default implementation always returns {@code false}, meaning no methods are
	 * skipped by default. Subclasses may override this method to provide custom logic for
	 * excluding certain methods.
	 * </p>
	 * @param cls The Java class containing the method.
	 * @param method The Java method to check.
	 * @param apiConfig The API configuration object, used to control documentation
	 * behavior.
	 * @param projectBuilder The project documentation configuration builder.
	 * @return {@code true} if the method should be skipped (not documented),
	 * {@code false} if it should be included.
	 */
	default boolean skipMethod(final JavaClass cls, final JavaMethod method, ApiConfig apiConfig,
			ProjectDocConfigBuilder projectBuilder) {
		return false;
	}

}
