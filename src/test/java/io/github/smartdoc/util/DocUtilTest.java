package io.github.smartdoc.util;

import io.github.smartdoc.constants.DocGlobalConstants;
import io.github.smartdoc.constants.DocLanguage;
import io.github.smartdoc.constants.DocTags;
import io.github.smartdoc.enums.IEnum;
import io.github.smartdoc.enums.OrderEnum;
import io.github.smartdoc.utils.DocUtil;
import com.thoughtworks.qdox.model.DocletTag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author yu 2018/12/10.
 */
public class DocUtilTest {

	/**
	 * Assuming DocTags.PARAM is "param"
	 */
	private static final String TAG_NAME_PARAM = DocTags.PARAM;

	/**
	 * Assuming DocTags.EXTENSION is "extension"
	 */
	private static final String TAG_NAME_EXTENSION = DocTags.EXTENSION;

	/**
	 * Assuming DocGlobalConstants.NO_COMMENTS_FOUND
	 */
	private static final String NO_COMMENTS_FOUND = DocGlobalConstants.NO_COMMENTS_FOUND;

	@Test
	public void test() {
		System.setProperty(DocGlobalConstants.DOC_LANGUAGE, DocLanguage.CHINESE.getCode());
		String str = DocUtil.getValByTypeAndFieldName("string", "name");
		System.out.println(str);
	}

	@Test
	public void testFormatAndRemove() {
		System.setProperty(DocGlobalConstants.DOC_LANGUAGE, DocLanguage.CHINESE.getCode());
		Map<String, String> params = new HashMap<>();
		params.put("name", "dd");
		params.put("age", "0");

		String url2 = "${server.error.path:${error.path:/error}}/test/{name:[a-zA-Z0-9]{3}}/{bb}/add";
		System.out.println(DocUtil.formatAndRemove(url2, params));

		params.put("name", "dd");
		params.put("age", "0");
		String url3 = "http://localhost:8080/detail/{id:[a-zA-Z0-9]{3}}/{name:[a-zA-Z0-9]{3}}";
		System.out.println(DocUtil.formatAndRemove(url3, params));
	}

	@Test
	public void testGetInterfacesEnum() throws ClassNotFoundException {
		System.out.println(IEnum.class.isAssignableFrom(OrderEnum.class));
	}

	@Test
	public void testIsMatch() {
		System.setProperty(DocGlobalConstants.DOC_LANGUAGE, DocLanguage.CHINESE.getCode());
		String pattern = "com.aaa.*.controller";
		String controllerName = "com.aaa.cc.controlle";

		System.out.println(DocUtil.isMatch(pattern, controllerName));
	}

	@Test
	public void testFormatPathUrl() {
		System.setProperty(DocGlobalConstants.DOC_LANGUAGE, DocLanguage.CHINESE.getCode());
		String url = "http://localhost:8080/detail/{id:[a-zA-Z0-9]{3}}/{name:[a-zA-Z0-9]{3}}";
		System.out.println(DocUtil.formatPathUrl(url));
	}

	@Test
	public void testSplitPathBySlash() {
		String str = "${server.error.path:${error.path:/error}}/test/{name:[a-zA-Z0-9]{3}}/{bb}/add";
		List<String> paths = DocUtil.splitPathBySlash(str);
		for (String s : paths) {
			if (s != null) {
				System.out.println(s);
			}
		}
	}

	@Test
	public void testReplaceGenericParameter() {
		String base = "java.util.List<com.smartdoc.example.model.TreeNode<T>>";
		String originalGeneric = "T";
		String replacement = "User";
		String result = DocUtil.replaceGenericParameter(base, originalGeneric, replacement);
		System.out.println(result); // Output: com.Test<List<Use
	}

	// Helper method to create a mock DocletTag
	private DocletTag createMockTag(String value) {
		DocletTag mockTag = Mockito.mock(DocletTag.class);
		when(mockTag.getValue()).thenReturn(value);
		return mockTag;
	}

	@Test
	public void testGetCommentsByTag_ParamWithNewlines() {
		// Scenario 1: Normal case - param name and description on same line
		DocletTag tag1 = createMockTag("id user identifier");
		// Scenario 2: Description starts on a new line with indentation
		DocletTag tag2 = createMockTag("name\n            user's full name");
		// Scenario 3: Description has multiple newlines and complex formatting
		DocletTag tag3 = createMockTag(
				"age   user's age in years\n            Default is 25.\n            NOTE: Must be > 0.");
		// Scenario 4: Only parameter name, no description
		DocletTag tag4 = createMockTag("flag");
		// Scenario 5: Parameter name with leading/trailing whitespace in the raw value
		// part that's not the name itself
		DocletTag tag5 = createMockTag("  email  user's email address  ");
		// Scenario 6: Description starts immediately after parameter name WITH A SPACE
		// (Standard Javadoc format)
		DocletTag tag6 = createMockTag("status Active user status flag");
		// Scenario 7: Description contains leading whitespace after the parameter name
		DocletTag tag7 = createMockTag("count  Number of items to retrieve");

		List<DocletTag> tags = Arrays.asList(tag1, tag2, tag3, tag4, tag5, tag6, tag7);

		Map<String, String> result = DocUtil.getCommentsByTag(tags, TAG_NAME_PARAM);

		// Verify results
		assertEquals("user identifier", result.get("id"), "Scenario 1: Description should be 'user identifier'");
		assertEquals("user's full name", result.get("name"),
				"Scenario 2: Description should be 'user's full name', preserving content after newline.");
		assertEquals("user's age in years\n            Default is 25.\n            NOTE: Must be > 0.",
				result.get("age"), "Scenario 3: Description should preserve newlines and formatting.");
		assertEquals(NO_COMMENTS_FOUND, result.get("flag"), "Scenario 4: Should return NO_COMMENTS_FOUND for 'flag'");
		assertEquals("user's email address", result.get("email"),
				"Scenario 5: Name should be 'email', description should be 'user's email address'");
		assertEquals("Active user status flag", result.get("status"),
				"Scenario 6: Description should be 'Active user status flag'");
		assertEquals("Number of items to retrieve", result.get("count"),
				"Scenario 7: Description should be 'Number of items to retrieve'");

	}

	@Test
	public void testGetCommentsByTag_ExtensionWithNewlines() {
		// Similar scenarios for @extension
		DocletTag tag1 = createMockTag("extId extension identifier");
		DocletTag tag2 = createMockTag("extName\n            extension name");
		DocletTag tag3 = createMockTag("extConfig   extension configuration details\n            with multiple lines.");
		DocletTag tag4 = createMockTag("extFlag");

		List<DocletTag> tags = Arrays.asList(tag1, tag2, tag3, tag4);

		Map<String, String> result = DocUtil.getCommentsByTag(tags, TAG_NAME_EXTENSION);

		// Verify results
		assertEquals("extension identifier", result.get("extId"));
		assertEquals("extension name", result.get("extName"));
		assertEquals("extension configuration details\n            with multiple lines.", result.get("extConfig"));
		assertEquals(NO_COMMENTS_FOUND, result.get("extFlag"));
	}

	@Test
	public void testGetCommentsByTag_ParamWithCarriageReturn() {
		// Scenario: Description has Windows-style CRLF (\r\n)
		String valueWithCRLF = "token\r\n            Authentication token,\r\n            required for access.";
		DocletTag tag = createMockTag(valueWithCRLF);

		List<DocletTag> tags = Collections.singletonList(tag);
		Map<String, String> result = DocUtil.getCommentsByTag(tags, TAG_NAME_PARAM);

		assertEquals("Authentication token,\r\n            required for access.", result.get("token"),
				"Should handle CRLF correctly, preserving the newline and content.");
	}

	@Test
	public void testGetCommentsByTag_ParamOnlyName() {
		// Scenario: Only parameter name exists, no space/description
		DocletTag tag = createMockTag("justParamName");
		List<DocletTag> tags = Collections.singletonList(tag);
		Map<String, String> result = DocUtil.getCommentsByTag(tags, TAG_NAME_PARAM);

		assertEquals(NO_COMMENTS_FOUND, result.get("justParamName"),
				"Should return NO_COMMENTS_FOUND for 'justParamName'");
	}

	@Test
	public void testGetCommentsByTag_ParamEmptyValue() {
		// Scenario: Empty value string
		DocletTag tag = createMockTag("");
		List<DocletTag> tags = Collections.singletonList(tag);
		Map<String, String> result = DocUtil.getCommentsByTag(tags, TAG_NAME_PARAM);

		// An empty string after trim results in [""], so parts[0] is "", and parts.length
		// is 1.
		// The logic assigns pName = parts[0] (which is ""), and pValue remains the
		// default NO_COMMENTS_FOUND.
		// The map.put(pName.trim(), pValue) effectively does map.put("",
		// NO_COMMENTS_FOUND).
		assertEquals(NO_COMMENTS_FOUND, result.get(""), // 修正 NO_COMMENTS_FOUND 的值
				"Should handle empty value, key should be empty string.");
		assertTrue(result.containsKey(""), "Map should contain an entry with an empty string key.");
	}

	@Test
	public void testGetCommentsByTag_ParamWhitespaceOnlyValue() {
		// Scenario: Value is only whitespace
		DocletTag tag = createMockTag("  \t \n  ");
		List<DocletTag> tags = Collections.singletonList(tag);
		Map<String, String> result = DocUtil.getCommentsByTag(tags, TAG_NAME_PARAM);

		// Whitespace-only string after trim becomes "", same as empty value scenario.
		// After trim and split, pName should be "" and pValue should be
		// NO_COMMENTS_FOUND.
		assertEquals(NO_COMMENTS_FOUND, result.get(""),
				"Should handle whitespace-only value, key should be empty string.");
		assertTrue(result.containsKey(""), "Map should contain an entry with an empty string key.");
	}

}
