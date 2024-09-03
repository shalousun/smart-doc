package com.ly.doc.helper;

import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.constants.*;
import com.ly.doc.model.*;
import com.ly.doc.utils.*;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.model.*;

import java.util.*;

import static com.ly.doc.constants.DocTags.IGNORE_RESPONSE_BODY_ADVICE;

public class JsonHelper extends BaseHelper {

	private static final String MAP_KEY = "\"mapKey\":";

	private static final String ANY_OBJECT = "\"any object\"";

	private static final String OBJECT_ANY_OBJECT = "{\"object\":\" any object\"}";

	public static String buildReturnJson(DocJavaMethod docJavaMethod, ProjectDocConfigBuilder builder) {
		JavaMethod method = docJavaMethod.getJavaMethod();
		String responseBodyAdvice = null;
		if (Objects.nonNull(builder.getApiConfig().getResponseBodyAdvice())) {
			responseBodyAdvice = builder.getApiConfig().getResponseBodyAdvice().getClassName();
		}

		if (isVoidReturnType(method, responseBodyAdvice)) {
			return "Return void.";
		}

		if (method.getTagByName(DocTags.DOWNLOAD) != null) {
			return "File download.";
		}

		if (isEnumReturnType(method, responseBodyAdvice)) {
			return StringUtil.removeQuotes(JavaClassUtil.getEnumValue(method.getReturns(), false).toString());
		}

		if (isPrimitiveReturnType(method, responseBodyAdvice)) {
			return StringUtil.removeQuotes(DocUtil.jsonValueByType(method.getReturnType().getCanonicalName()));
		}

		if (isStringReturnType(method, responseBodyAdvice)) {
			return "string";
		}

		String returnType = getReturnType(method, responseBodyAdvice);

		ApiReturn apiReturn = DocClassUtil.processReturnType(returnType);
		String typeName = apiReturn.getSimpleName();

		if (JavaClassValidateUtil.isFileDownloadResource(typeName)) {
			docJavaMethod.setDownload(true);
			return "File download.";
		}

		return buildJsonResponse(docJavaMethod, builder, returnType, apiReturn);
	}

	private static boolean isVoidReturnType(JavaMethod method, String responseBodyAdvice) {
		return method.getReturns().isVoid() && responseBodyAdvice == null;
	}

	private static boolean isEnumReturnType(JavaMethod method, String responseBodyAdvice) {
		return method.getReturns().isEnum() && responseBodyAdvice == null;
	}

	private static boolean isPrimitiveReturnType(JavaMethod method, String responseBodyAdvice) {
		return method.getReturns().isPrimitive() && responseBodyAdvice == null;
	}

	private static boolean isStringReturnType(JavaMethod method, String responseBodyAdvice) {
		return JavaTypeConstants.JAVA_STRING_FULLY.equals(method.getReturnType().getGenericCanonicalName())
				&& responseBodyAdvice == null;
	}

	private static String getReturnType(JavaMethod method, String responseBodyAdvice) {
		String returnType = method.getReturnType().getGenericCanonicalName();
		if (responseBodyAdvice != null && method.getTagByName(IGNORE_RESPONSE_BODY_ADVICE) == null) {
			if (!returnType.startsWith(responseBodyAdvice)) {
				return responseBodyAdvice + "<" + returnType + ">";
			}
		}
		return returnType;
	}

	private static String buildJsonResponse(DocJavaMethod docJavaMethod, ProjectDocConfigBuilder builder,
			String returnType, ApiReturn apiReturn) {
		Map<String, JavaType> actualTypesMap = docJavaMethod.getActualTypesMap();
		String typeName = apiReturn.getSimpleName();
		if (actualTypesMap != null) {
			typeName = JavaClassUtil.getGenericsNameByActualTypesMap(typeName, actualTypesMap);
			returnType = JavaClassUtil.getGenericsNameByActualTypesMap(returnType, actualTypesMap);
		}
		if (JavaClassValidateUtil.isPrimitive(typeName)) {
			return JavaTypeConstants.JAVA_STRING_FULLY.equals(typeName) ? "string"
					: StringUtil.removeQuotes(DocUtil.jsonValueByType(typeName));
		}
		return JsonUtil.toPrettyFormat(buildJson(typeName, returnType, true, 0, new HashMap<>(16),
				Collections.emptySet(), docJavaMethod.getJsonViewClasses(), builder));
	}

	public static String buildJson(String typeName, String genericCanonicalName, boolean isResp, int counter,
			Map<String, String> registryClasses, Set<String> groupClasses, Set<String> methodJsonViewClasses,
			ProjectDocConfigBuilder builder) {

		if (isRecursionLimitReached(counter, builder) || isAlreadyProcessed(typeName, counter, registryClasses)) {
			return "{\"$ref\":\"...\"}";
		}

		registryClasses.put(typeName, typeName);

		if (shouldIgnoreType(typeName, builder)) {
			return handleMvcIgnoreParams(typeName);
		}

		if (JavaClassValidateUtil.isPrimitive(typeName)) {
			return StringUtil.removeQuotes(DocUtil.jsonValueByType(typeName));
		}

		JavaClass javaClass = builder.getJavaProjectBuilder().getClassByName(typeName);
		if (javaClass.isEnum()) {
			return StringUtil.removeQuotes(JavaClassUtil.getEnumValue(javaClass, false).toString());
		}

		return buildJsonForType(typeName, genericCanonicalName, isResp, counter, registryClasses, groupClasses,
				methodJsonViewClasses, builder, javaClass);
	}

	private static boolean isRecursionLimitReached(int counter, ProjectDocConfigBuilder builder) {
		return counter > builder.getApiConfig().getRecursionLimit();
	}

	private static boolean isAlreadyProcessed(String typeName, int counter, Map<String, String> registryClasses) {
		return registryClasses.containsKey(typeName) && counter > registryClasses.size();
	}

	private static boolean shouldIgnoreType(String typeName, ProjectDocConfigBuilder builder) {
		return JavaClassValidateUtil.isMvcIgnoreParams(typeName, builder.getApiConfig().getIgnoreRequestParams());
	}

	private static String handleMvcIgnoreParams(String typeName) {
		if (DocGlobalConstants.MODE_AND_VIEW_FULLY.equals(typeName)) {
			return "Forward or redirect to a page view.";
		}
		return "Error restful return.";
	}

	private static String buildJsonForType(String typeName, String genericCanonicalName, boolean isResp, int counter,
			Map<String, String> registryClasses, Set<String> groupClasses, Set<String> methodJsonViewClasses,
			ProjectDocConfigBuilder builder, JavaClass javaClass) {

		Map<String, String> genericMap = new HashMap<>(10);
		String[] globGicName = DocClassUtil.getSimpleGicName(genericCanonicalName);

		JavaClass cls = builder.getClassByName(typeName);

		if (globGicName == null || globGicName.length < 1) {
			globGicName = getSuperClassGenerics(cls);
		}
		JavaClassUtil.genericParamMap(genericMap, cls, globGicName);

		StringBuilder data = new StringBuilder();

		if (JavaClassValidateUtil.isCollection(typeName) || JavaClassValidateUtil.isArray(typeName)) {
			return handleCollectionType(typeName, globGicName, isResp, counter, registryClasses, groupClasses,
					methodJsonViewClasses, builder, data);
		}
		else if (JavaClassValidateUtil.isMap(typeName)) {
			return handleMapType(genericCanonicalName, isResp, counter, registryClasses, groupClasses,
					methodJsonViewClasses, builder, data);
		}
		else if (JavaTypeConstants.JAVA_OBJECT_FULLY.equals(typeName)) {
			return OBJECT_ANY_OBJECT;
		}
		else if (JavaClassValidateUtil.isReactor(typeName)) {
			return buildJson(globGicName[0], typeName, isResp, counter + 1, registryClasses, groupClasses,
					methodJsonViewClasses, builder);
		}
		else {
			return buildJsonForObjectType(typeName, genericCanonicalName, isResp, counter, registryClasses,
					groupClasses, methodJsonViewClasses, builder, cls, data);
		}
	}

	private static String[] getSuperClassGenerics(JavaClass cls) {
		JavaClass superJavaClass = cls.getSuperJavaClass();
		if (superJavaClass != null && !JavaTypeConstants.OBJECT_SIMPLE_NAME.equals(superJavaClass.getSimpleName())) {
			return DocClassUtil.getSimpleGicName(superJavaClass.getGenericFullyQualifiedName());
		}
		return new String[0];
	}

	private static String handleCollectionType(String typeName, String[] globGicName, boolean isResp, int counter,
			Map<String, String> registryClasses, Set<String> groupClasses, Set<String> methodJsonViewClasses,
			ProjectDocConfigBuilder builder, StringBuilder data) {

		data.append("[");
		if (globGicName.length == 0) {
			data.append("{").append(ANY_OBJECT).append("}");
			data.append("]");
			return data.toString();
		}

		String gName = globGicName[0];
		if (JavaClassValidateUtil.isArray(gName)) {
			gName = gName.substring(0, gName.indexOf("["));
		}

		if (JavaTypeConstants.JAVA_OBJECT_FULLY.equals(gName)) {
			data.append("{\"warning\":\"You may use java.util.Object instead of display generics in the List\"}");
		}
		else if (JavaClassValidateUtil.isPrimitive(gName)) {
			data.append(DocUtil.jsonValueByType(gName)).append(",").append(DocUtil.jsonValueByType(gName));
		}
		else {
			data.append(buildJsonForComplexType(gName, isResp, counter, registryClasses, groupClasses,
					methodJsonViewClasses, builder));
		}
		data.append("]");
		return data.toString();
	}

	private static String handleMapType(String genericCanonicalName, boolean isResp, int counter,
			Map<String, String> registryClasses, Set<String> groupClasses, Set<String> methodJsonViewClasses,
			ProjectDocConfigBuilder builder, StringBuilder data) {

		String[] getKeyValType = DocClassUtil.getMapKeyValueType(genericCanonicalName);
		if (getKeyValType.length == 0) {
			data.append("{").append(MAP_KEY).append("{}}");
			return data.toString();
		}

		String gicName = genericCanonicalName.substring(genericCanonicalName.indexOf(",") + 1,
				genericCanonicalName.lastIndexOf(">"));
		if (JavaTypeConstants.JAVA_OBJECT_FULLY.equals(gicName)) {
			data.append("{")
				.append(MAP_KEY)
				.append("{\"warning\":\"You may use java.util.Object for Map value; smart-doc can't handle it.\"}}");
		}
		else if (JavaClassValidateUtil.isPrimitive(gicName)) {
			data.append("{")
				.append("\"mapKey1\":")
				.append(DocUtil.jsonValueByType(gicName))
				.append(",")
				.append("\"mapKey2\":")
				.append(DocUtil.jsonValueByType(gicName))
				.append("}");
		}
		else {
			data.append("{")
				.append(MAP_KEY)
				.append(buildJsonForComplexType(gicName, isResp, counter, registryClasses, groupClasses,
						methodJsonViewClasses, builder))
				.append("}");
		}
		return data.toString();
	}

	private static String buildJsonForObjectType(String typeName, String genericCanonicalName, boolean isResp,
			int counter, Map<String, String> registryClasses, Set<String> groupClasses,
			Set<String> methodJsonViewClasses, ProjectDocConfigBuilder builder, JavaClass cls, StringBuilder data) {

		boolean requestFieldToUnderline = builder.getApiConfig().isRequestFieldToUnderline();
		boolean responseFieldToUnderline = builder.getApiConfig().isResponseFieldToUnderline();

		List<DocJavaField> fields = JavaClassUtil.getFields(cls, 0, new LinkedHashMap<>(),
				builder.getApiConfig().getClassLoader());

		Map<String, String> ignoreFields = JavaClassUtil.getClassJsonIgnoreFields(cls);

		for (DocJavaField docField : fields) {
			JavaField field = docField.getJavaField();
			if (field.isTransient() && shouldSkipTransientField(isResp, builder)) {
				continue;
			}

			String fieldName = getFieldName(docField, isResp, responseFieldToUnderline, requestFieldToUnderline,
					ignoreFields);

			if (fieldName == null)
				continue;

			String subTypeName = docField.getTypeFullyQualifiedName();
			String fieldValue = getFieldValueFromMockForJson(subTypeName, DocUtil.getFieldTagsValue(field, docField),
					docField.getTypeSimpleName());

			data.append("\"").append(fieldName).append("\":");

			if (JavaClassValidateUtil.isPrimitive(subTypeName)) {
				data.append(getPrimitiveFieldValue(docField, isResp, builder, fieldValue)).append(",");
			}
			else {
				data.append(getComplexFieldValue(typeName, subTypeName, docField, isResp, counter, registryClasses,
						groupClasses, methodJsonViewClasses, builder, fieldValue))
					.append(",");
			}
		}

		removeTrailingComma(data);
		data.append("}");
		return data.toString();
	}

	private static boolean shouldSkipTransientField(boolean isResp, ProjectDocConfigBuilder builder) {
		return (builder.getApiConfig().isSerializeRequestTransients() && !isResp)
				|| (builder.getApiConfig().isSerializeResponseTransients() && isResp);
	}

	private static String getFieldName(DocJavaField docField, boolean isResp, boolean responseFieldToUnderline,
			boolean requestFieldToUnderline, Map<String, String> ignoreFields) {
		String fieldName = docField.getFieldName();
		if (ignoreFields.containsKey(fieldName)) {
			return null;
		}
		if ((responseFieldToUnderline && isResp) || (requestFieldToUnderline && !isResp)) {
			fieldName = StringUtil.camelToUnderline(fieldName);
		}
		return fieldName;
	}

	private static String getPrimitiveFieldValue(DocJavaField docField, boolean isResp, ProjectDocConfigBuilder builder,
			String fieldValue) {
		if (StringUtil.isEmpty(fieldValue)) {
			String valueByTypeAndFieldName = DocUtil.getValByTypeAndFieldName(docField.getTypeSimpleName(),
					docField.getFieldName());
			fieldValue = builder.getApiConfig().isSerializeResponseTransients() && isResp
					? DocUtil.handleJsonStr(valueByTypeAndFieldName) : valueByTypeAndFieldName;
		}
		return fieldValue;
	}

	private static String getComplexFieldValue(String typeName, String subTypeName, DocJavaField docField,
			boolean isResp, int counter, Map<String, String> registryClasses, Set<String> groupClasses,
			Set<String> methodJsonViewClasses, ProjectDocConfigBuilder builder, String fieldValue) {

		if (StringUtil.isNotEmpty(fieldValue)) {
			return fieldValue;
		}

		String fieldGicName = docField.getTypeGenericCanonicalName();
		if (JavaClassValidateUtil.isCollection(subTypeName) || JavaClassValidateUtil.isArray(subTypeName)) {
			return handleCollectionField(typeName, subTypeName, fieldGicName, isResp, counter, registryClasses,
					groupClasses, methodJsonViewClasses, builder);
		}
		else if (JavaClassValidateUtil.isMap(subTypeName)) {
			return handleMapField(fieldGicName, isResp, counter, registryClasses, groupClasses, methodJsonViewClasses,
					builder);
		}
		else if (fieldGicName.length() == 1) {
			return handleGenericField(typeName, subTypeName, fieldGicName, isResp, counter, registryClasses,
					groupClasses, methodJsonViewClasses, builder);
		}
		else {
			return buildJson(subTypeName, fieldGicName, isResp, counter + 1, registryClasses, groupClasses,
					methodJsonViewClasses, builder);
		}
	}

	private static String handleCollectionField(String typeName, String subTypeName, String fieldGicName,
			boolean isResp, int counter, Map<String, String> registryClasses, Set<String> groupClasses,
			Set<String> methodJsonViewClasses, ProjectDocConfigBuilder builder) {

		String[] gicNameArray = DocClassUtil.getSimpleGicName(fieldGicName);
		String gicName = gicNameArray[0];

		if (JavaTypeConstants.JAVA_STRING_FULLY.equals(gicName)) {
			return "[" + DocUtil.jsonValueByType(gicName) + "]";
		}
		else if (JavaTypeConstants.JAVA_LIST_FULLY.equals(gicName)) {
			return "[{" + ANY_OBJECT + "}]";
		}
		else if (gicName.length() == 1) {
			return handleGenericCollectionField(typeName, gicName, gicNameArray, isResp, counter, registryClasses,
					groupClasses, methodJsonViewClasses, builder);
		}
		else {
			return handleNonGenericCollectionField(typeName, gicName, fieldGicName, isResp, counter, registryClasses,
					groupClasses, methodJsonViewClasses, builder);
		}
	}

	private static String handleGenericCollectionField(String typeName, String gicName, String[] gicNameArray,
			boolean isResp, int counter, Map<String, String> registryClasses, Set<String> groupClasses,
			Set<String> methodJsonViewClasses, ProjectDocConfigBuilder builder) {

		String gicName1 = builder.getJavaProjectBuilder().getClassByName(typeName).getGenericFullyQualifiedName();
		if (JavaTypeConstants.JAVA_STRING_FULLY.equals(gicName1)) {
			return "[" + DocUtil.jsonValueByType(gicName1) + "]";
		}
		else {
			return "[" + buildJson(DocClassUtil.getSimpleName(gicName1), gicName1, isResp, counter + 1, registryClasses,
					groupClasses, methodJsonViewClasses, builder) + "]";
		}
	}

	private static String handleNonGenericCollectionField(String typeName, String gicName, String fieldGicName,
			boolean isResp, int counter, Map<String, String> registryClasses, Set<String> groupClasses,
			Set<String> methodJsonViewClasses, ProjectDocConfigBuilder builder) {

		if (!typeName.equals(gicName)) {
			if (JavaClassValidateUtil.isMap(gicName)) {
				return "[{\"mapKey\":{}}]";
			}
			JavaClass arraySubClass = builder.getJavaProjectBuilder().getClassByName(gicName);
			if (arraySubClass.isEnum()) {
				return "[" + JavaClassUtil.getEnumValue(arraySubClass, false) + "]";
			}
			gicName = DocClassUtil.getSimpleName(gicName);
			fieldGicName = DocUtil.formatFieldTypeGicName(new HashMap<>(), fieldGicName);
			return "[" + buildJson(gicName, fieldGicName, isResp, counter + 1, registryClasses, groupClasses,
					methodJsonViewClasses, builder) + "]";
		}
		return "[{\"$ref\":\"..\"}]";
	}

	private static String handleMapField(String fieldGicName, boolean isResp, int counter,
			Map<String, String> registryClasses, Set<String> groupClasses, Set<String> methodJsonViewClasses,
			ProjectDocConfigBuilder builder) {

		String gicName = fieldGicName.substring(fieldGicName.indexOf(",") + 1, fieldGicName.indexOf(">"));

		if (gicName.length() == 1) {
			String gicName1 = builder.getJavaProjectBuilder().getClassByName(gicName).getGenericFullyQualifiedName();
			if (JavaTypeConstants.JAVA_STRING_FULLY.equals(gicName1)) {
				return "{" + MAP_KEY + DocUtil.jsonValueByType(gicName1) + "}";
			}
			else {
				return "{" + MAP_KEY + buildJson(DocClassUtil.getSimpleName(gicName1), gicName1, isResp, counter + 1,
						registryClasses, groupClasses, methodJsonViewClasses, builder) + "}";
			}
		}
		else {
			return handleNonGenericMapField(fieldGicName, gicName, isResp, counter, registryClasses, groupClasses,
					methodJsonViewClasses, builder);
		}
	}

	private static String handleNonGenericMapField(String fieldGicName, String gicName, boolean isResp, int counter,
			Map<String, String> registryClasses, Set<String> groupClasses, Set<String> methodJsonViewClasses,
			ProjectDocConfigBuilder builder) {

		String[] mapGicName = DocClassUtil.getSimpleGicName(fieldGicName);
		String mapKeySimpleName = mapGicName[0];
		JavaClass mapKeyClass = builder.getJavaProjectBuilder().getClassByName(mapKeySimpleName);
		if (mapKeyClass.isEnum()) {
			return buildJsonForEnumMapKey(mapKeyClass, gicName, isResp, counter, registryClasses, groupClasses,
					methodJsonViewClasses, builder);
		}
		return "{" + MAP_KEY + buildJson(gicName, fieldGicName, isResp, counter + 1, registryClasses, groupClasses,
				methodJsonViewClasses, builder) + "}";
	}

	private static String buildJsonForEnumMapKey(JavaClass mapKeyClass, String gicName, boolean isResp, int counter,
			Map<String, String> registryClasses, Set<String> groupClasses, Set<String> methodJsonViewClasses,
			ProjectDocConfigBuilder builder) {

		StringBuilder data = new StringBuilder("{");
		List<JavaField> mapKeyClassFields = mapKeyClass.getFields();
		int size = mapKeyClassFields.size();
		for (int i = 0; i < size; i++) {
			JavaField mapKeyField = mapKeyClassFields.get(i);
			data.append("\"")
				.append(mapKeyField.getName())
				.append("\":")
				.append(buildJson(gicName, gicName, isResp, counter + 1, registryClasses, groupClasses,
						methodJsonViewClasses, builder));

			if (i < size - 1) {
				data.append(",");
			}
		}
		data.append("}");
		return data.toString();
	}

	private static String handleGenericField(String typeName, String subTypeName, String fieldGicName, boolean isResp,
			int counter, Map<String, String> registryClasses, Set<String> groupClasses,
			Set<String> methodJsonViewClasses, ProjectDocConfigBuilder builder) {

		String gicName = builder.getJavaProjectBuilder().getClassByName(subTypeName).getGenericFullyQualifiedName();
		if (JavaClassValidateUtil.isPrimitive(gicName)) {
			return DocUtil.jsonValueByType(gicName);
		}
		else {
			return buildJson(DocClassUtil.getSimpleName(gicName), gicName, isResp, counter + 1, registryClasses,
					groupClasses, methodJsonViewClasses, builder);
		}
	}

	private static String buildJsonForComplexType(String typeName, boolean isResp, int counter,
			Map<String, String> registryClasses, Set<String> groupClasses, Set<String> methodJsonViewClasses,
			ProjectDocConfigBuilder builder) {

		if (JavaTypeConstants.JAVA_OBJECT_FULLY.equals(typeName)) {
			return OBJECT_ANY_OBJECT;
		}
		else {
			return buildJson(DocClassUtil.getSimpleName(typeName), typeName, isResp, counter + 1, registryClasses,
					groupClasses, methodJsonViewClasses, builder);
		}
	}

	private static void removeTrailingComma(StringBuilder data) {
		if (data.toString().contains(",")) {
			data.deleteCharAt(data.lastIndexOf(","));
		}
	}

}
