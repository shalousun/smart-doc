package com.ly.doc.utils;

import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.constants.DocTags;
import com.ly.doc.constants.ParamTypeConstants;
import com.ly.doc.model.ApiParam;
import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author <a href="mailto:cqmike0315@gmail.com">chenqi</a>
 * @version 1.0
 */
public class ParamUtil {

	/**
	 * Handles enum types in API parameters.
	 * <p>
	 * This method is primarily used to process enum types, setting the corresponding
	 * parameter type, value, and enumeration information in the ApiParam object. If the
	 * enum has a @JsonFormat annotation with a shape attribute value of NUMBER, the value
	 * is processed accordingly. Additionally, it supports setting mock values for
	 * parameters through tag mappings.
	 * @param param The ApiParam object, used to store parameter information.
	 * @param javaField The JavaField object, representing the field of a class, used to
	 * obtain the type and other information of the field.
	 * @param builder The ProjectDocConfigBuilder object, used to obtain configuration
	 * information for generating documentation.
	 * @param jsonRequest A boolean value indicating whether the request is in JSON
	 * format, used to determine how to obtain the enum value.
	 * @param tagsMap A map containing tag names and their descriptions, used to override
	 * parameter values with mock data.
	 * @param jsonFormatValue The value of the @JsonFormat annotation's shape attribute,
	 * used to handle special cases of numeric enums.
	 * @return Returns the processed JavaClass object representing the enum, or null if
	 * the input field is not an enum.
	 */
	public static JavaClass handleSeeEnum(ApiParam param, JavaField javaField, ProjectDocConfigBuilder builder,
			boolean jsonRequest, Map<String, String> tagsMap, String jsonFormatValue) {
		JavaClass seeEnum = JavaClassUtil.getSeeEnum(javaField, builder);
		if (Objects.isNull(seeEnum)) {
			return null;
		}
		// when enum is same class, set type to enum
		if (Objects.equals(seeEnum.getGenericFullyQualifiedName(),
				javaField.getType().getGenericFullyQualifiedName())) {
			param.setType(ParamTypeConstants.PARAM_TYPE_ENUM);
		}
		Object value = JavaClassUtil.getEnumValue(seeEnum, builder, !jsonRequest);
		param.setValue(StringUtil.removeDoubleQuotes(String.valueOf(value)));
		param.setEnumValues(JavaClassUtil.getEnumValues(seeEnum));
		param.setEnumInfo(JavaClassUtil.getEnumInfo(seeEnum, builder));
		// If the @JsonFormat annotation's shape attribute value is specified, use it as
		// the parameter value
		if (StringUtil.isNotEmpty(jsonFormatValue)) {
			param.setValue(jsonFormatValue);
			param.setEnumValues(IntStream.rangeClosed(0, param.getEnumValues().size() - 1)
				.mapToObj(Integer::toString)
				.collect(Collectors.toList()));
		}
		// If the tagsMap contains a mock tag and the value is not empty, use the value of
		// the mock tag as the parameter value
		// Override old value
		if (tagsMap.containsKey(DocTags.MOCK) && StringUtil.isNotEmpty(tagsMap.get(DocTags.MOCK))) {
			param.setValue(tagsMap.get(DocTags.MOCK));
		}
		return seeEnum;
	}

	public static String formatMockValue(String mock) {
		if (StringUtil.isEmpty(mock)) {
			return mock;
		}
		return mock.replaceAll("\\\\", "");
	}

	public static List<String> extractQualifiedName(List<ApiParam> paramList) {
		if (CollectionUtil.isEmpty(paramList)) {
			return Collections.emptyList();
		}

		Set<String> set = new HashSet<>();
		for (ApiParam param : paramList) {
			String className = param.getClassName();

			if (StringUtil.isEmpty(className)) {
				continue;
			}

			int index = className.indexOf("<");
			if (index > -1) {
				className = className.substring(0, index);
			}

			set.add(className);
		}

		return new ArrayList<>(set);
	}

}
