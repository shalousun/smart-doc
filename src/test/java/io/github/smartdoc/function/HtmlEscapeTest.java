package io.github.smartdoc.function;

import org.beetl.core.Context;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HtmlEscapeTest {

	/*
	 * Since context is not used in the function, we can set it to null.
	 */
	private static final Context context = null;

	@ParameterizedTest
	@MethodSource("provideNormalTestCases")
	void testHtmlEscape(String input, String expected) {
		HtmlEscape htmlEscape = new HtmlEscape();

		String result = htmlEscape.call(new Object[] { input }, context);

		assertEquals(expected, result);
	}

	@ParameterizedTest
	@NullAndEmptySource
	void testHtmlEscapeNullAndEmptyString(Object[] input) {
		HtmlEscape htmlEscape = new HtmlEscape();

		String result = htmlEscape.call(input, context);

		assertEquals("", result);
	}

	/**
	 * Provides test cases for normal inputs.
	 * @return a stream of arguments for testing
	 */
	static Stream<Arguments> provideNormalTestCases() {
		return Stream.of(Arguments.of("&", "&amp;"), Arguments.of("\"", "&quot;"), Arguments.of("<p>", ""),
				Arguments.of("<p>ab</p>abc", "ab abc"), Arguments.of("</p>", ""),
				Arguments.of("<p>Hello & \"World\"</p>", "Hello &amp; &quot;World&quot; "), Arguments.of("", ""),
				Arguments.of(null, ""));
	}

}