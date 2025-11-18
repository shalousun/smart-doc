package io.github.smartdoc.function;

import org.beetl.core.Context;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WordXmlEscapeTest {

	private static final String UNICODE_NON_BREAK_SPACE = "\u00A0";

	private static Context context;

	@ParameterizedTest
	@MethodSource("provideNormalTestCases")
	void testWordXmlEscape(String input, String expected) {
		WordXmlEscape wordXmlEscape = new WordXmlEscape();

		String result = wordXmlEscape.call(new Object[] { input }, context);

		assertEquals(expected, result);
	}

	@ParameterizedTest
	@NullAndEmptySource
	void testWordXmlEscapeNullAndEmptyString(Object[] input) {
		WordXmlEscape wordXmlEscape = new WordXmlEscape();

		String result = wordXmlEscape.call(input, context);

		assertEquals("", result);
	}

	/**
	 * Provides test cases for normal inputs.
	 * @return a stream of arguments for testing
	 */
	static Stream<Arguments> provideNormalTestCases() {
		return Stream.of(Arguments.of(" ", UNICODE_NON_BREAK_SPACE),
				Arguments.of("&nbsp;&nbsp;", UNICODE_NON_BREAK_SPACE), Arguments.of("&nbsp;", ""),
				// Combination of doubled non-breaking spaces and empty spaces
				Arguments.of("&nbsp;&nbsp; abc&nbsp;",
						String.format("%s%sabc", UNICODE_NON_BREAK_SPACE, UNICODE_NON_BREAK_SPACE)),
				Arguments.of("<br/>", ""), Arguments.of("&", "&amp;"), Arguments.of("<", "&lt;"),
				Arguments.of("Hello & <br/>",
						String.format("Hello%s&amp;%s", UNICODE_NON_BREAK_SPACE, UNICODE_NON_BREAK_SPACE)),
				Arguments.of("", ""), Arguments.of(null, ""));
	}

}