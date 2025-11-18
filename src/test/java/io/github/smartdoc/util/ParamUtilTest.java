package io.github.smartdoc.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.smartdoc.model.ApiParam;
import io.github.smartdoc.utils.ParamUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ParamUtilTest {

	@Test
	public void testFormatMockValue() {
		System.out.printf(ParamUtil.formatMockValue("*\\/5 * * *"));
	}

	@Test
	public void testExtractQualifiedName() {
		String paramJson = "[\n" + "  {\n" + "    \"className\": \"org.example.springboot.dto.PetDTO\",\n"
				+ "    \"id\": 1,\n" + "    \"field\": \"name\",\n" + "    \"type\": \"string\",\n"
				+ "    \"desc\": \"name\",\n" + "    \"required\": false,\n" + "    \"version\": \"-\",\n"
				+ "    \"pid\": 0,\n" + "    \"pathParam\": false,\n" + "    \"queryParam\": false,\n"
				+ "    \"value\": \"\",\n" + "    \"hasItems\": false,\n" + "    \"maxLength\": \"\",\n"
				+ "    \"configParam\": false,\n" + "    \"selfReferenceLoop\": false\n" + "  },\n" + "  {\n"
				+ "    \"className\": \"org.example.springboot.dto.PetDTO\",\n" + "    \"id\": 2,\n"
				+ "    \"field\": \"age\",\n" + "    \"type\": \"int32\",\n" + "    \"desc\": \"age\",\n"
				+ "    \"required\": false,\n" + "    \"version\": \"-\",\n" + "    \"pid\": 0,\n"
				+ "    \"pathParam\": false,\n" + "    \"queryParam\": false,\n" + "    \"value\": \"0\",\n"
				+ "    \"hasItems\": false,\n" + "    \"maxLength\": \"\",\n" + "    \"configParam\": false,\n"
				+ "    \"selfReferenceLoop\": false\n" + "  },\n" + "  {\n"
				+ "    \"className\": \"org.example.springboot.dto.PetDTO\",\n" + "    \"id\": 3,\n"
				+ "    \"field\": \"master\",\n" + "    \"type\": \"object\",\n" + "    \"desc\": \"master\",\n"
				+ "    \"required\": false,\n" + "    \"version\": \"-\",\n" + "    \"pid\": 0,\n"
				+ "    \"pathParam\": false,\n" + "    \"queryParam\": false,\n" + "    \"value\": \"\",\n"
				+ "    \"hasItems\": false,\n" + "    \"maxLength\": \"\",\n" + "    \"configParam\": false,\n"
				+ "    \"selfReferenceLoop\": false\n" + "  },\n" + "  {\n"
				+ "    \"className\": \"org.example.springboot.dto.UserDTO\",\n" + "    \"id\": 4,\n"
				+ "    \"field\": \"└─username\",\n" + "    \"type\": \"string\",\n"
				+ "    \"desc\": \"No comments found.\",\n" + "    \"required\": false,\n" + "    \"version\": \"-\",\n"
				+ "    \"pid\": 3,\n" + "    \"pathParam\": false,\n" + "    \"queryParam\": false,\n"
				+ "    \"value\": \"\",\n" + "    \"hasItems\": false,\n" + "    \"maxLength\": \"\",\n"
				+ "    \"configParam\": false,\n" + "    \"selfReferenceLoop\": false\n" + "  }\n" + "]";
		List<ApiParam> paramList = new Gson().fromJson(paramJson, new TypeToken<List<ApiParam>>() {
		});
		List<String> qualifiedList = ParamUtil.extractQualifiedName(paramList);
		qualifiedList.forEach(System.out::println);
	}

}
