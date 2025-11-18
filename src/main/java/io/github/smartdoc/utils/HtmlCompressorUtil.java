package io.github.smartdoc.utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * html compressor util, quote from
 * <a href="https://code.google.com/archive/p/htmlcompressor/">google html compressor</a>
 *
 * @author Jason Kung
 */
@SuppressWarnings("all")
public class HtmlCompressorUtil {

	/**
	 * Predefined list of tags that are very likely to be block-level
	 */
	public static final String BLOCK_TAGS_MIN = "html,head,body,br,p";

	/**
	 * Predefined list of tags that are block-level by default
	 */
	public static final String BLOCK_TAGS_MAX = BLOCK_TAGS_MIN
			+ ",h1,h2,h3,h4,h5,h6,blockquote,center,dl,fieldset,form,frame,frameset,hr,no frames,ol,table,tbody,tr,td,th,tfoot,thead,ul";

	/**
	 * Flag indicating whether HTML compression is enabled
	 */
	private static final boolean ENABLED = true;

	/**
	 * Flag indicating whether to remove HTML comments
	 */
	private static final boolean REMOVE_COMMENTS = true;

	/**
	 * Flag indicating whether to remove multiple spaces
	 */
	private static final boolean REMOVE_MULTI_SPACES = true;

	/**
	 * Flag indicating whether to remove spaces between tags
	 */
	private static final boolean REMOVE_INTER_TAG_SPACES = false;

	/**
	 * Flag indicating whether to remove quotes from attributes
	 */
	private static final boolean REMOVE_QUOTES = false;

	/**
	 * Flag indicating whether to simplify doctype
	 */
	private static final boolean SIMPLE_DOCTYPE = false;

	/**
	 * Flag indicating whether to remove type and language attributes from script tags
	 */
	private static final boolean REMOVE_SCRIPT_ATTRIBUTES = false;

	/**
	 * Flag indicating whether to remove type attributes from style tags
	 */
	private static final boolean REMOVE_STYLE_ATTRIBUTES = false;

	/**
	 * Flag indicating whether to remove link attributes
	 */
	private static final boolean REMOVE_LINK_ATTRIBUTES = false;

	/**
	 * Flag indicating whether to remove form attributes
	 */
	private static final boolean REMOVE_FORM_ATTRIBUTES = false;

	/**
	 * Flag indicating whether to remove input attributes
	 */
	private static final boolean REMOVE_INPUT_ATTRIBUTES = false;

	/**
	 * Flag indicating whether to simplify boolean attributes
	 */
	private static final boolean SIMPLE_BOOLEAN_ATTRIBUTES = false;

	/**
	 * Flag indicating whether to remove http from attributes
	 */
	private static final boolean REMOVE_HTTP_PROTOCOL = false;

	/**
	 * Flag indicating whether to remove https from attributes
	 */
	private static final boolean REMOVE_HTTPS_PROTOCOL = false;

	/**
	 * Flag indicating whether to preserve line breaks
	 */
	private static final boolean PRESERVE_LINE_BREAKS = false;

	/**
	 * Temporary replacement pattern for preserved conditional comment blocks
	 */
	private static final String TEMP_COND_COMMENT_BLOCK = "%%%~COMPRESS~COND~{0,number,#}~%%%";

	/**
	 * Temporary replacement pattern for preserved PRE blocks
	 */
	private static final String TEMP_PRE_BLOCK = "%%%~COMPRESS~PRE~{0,number,#}~%%%";

	/**
	 * Temporary replacement pattern for preserved TEXTAREA blocks
	 */
	private static final String TEMP_TEXT_AREA_BLOCK = "%%%~COMPRESS~TEXTAREA~{0,number,#}~%%%";

	/**
	 * Temporary replacement pattern for preserved SCRIPT blocks
	 */
	private static final String TEMP_SCRIPT_BLOCK = "%%%~COMPRESS~SCRIPT~{0,number,#}~%%%";

	/**
	 * Temporary replacement pattern for preserved STYLE blocks
	 */
	private static final String TEMP_STYLE_BLOCK = "%%%~COMPRESS~STYLE~{0,number,#}~%%%";

	/**
	 * Temporary replacement pattern for preserved EVENT blocks
	 */
	private static final String TEMP_EVENT_BLOCK = "%%%~COMPRESS~EVENT~{0,number,#}~%%%";

	/**
	 * Temporary replacement pattern for preserved LINE BREAK blocks
	 */
	private static final String TEMP_LINE_BREAK_BLOCK = "%%%~COMPRESS~LT~{0,number,#}~%%%";

	/**
	 * Temporary replacement pattern for preserved SKIP blocks
	 */
	private static final String TEMP_SKIP_BLOCK = "%%%~COMPRESS~SKIP~{0,number,#}~%%%";

	/**
	 * Pattern to match skip blocks in HTML
	 */
	private static final Pattern SKIP_PATTERN = Pattern.compile("<!--\\s*\\{\\{\\{\\s*-->(.*?)<!--\\s*\\}\\}\\}\\s*-->",
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match conditional comments
	 */
	private static final Pattern COND_COMMENT_PATTERN = Pattern
		.compile("(<!(?:--)?\\[[^\\]]+?]>)(.*?)(<!\\[[^\\]]+]-->)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match HTML comments
	 */
	private static final Pattern COMMENT_PATTERN = Pattern.compile("<!---->|<!--[^\\[].*?-->",
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match spaces between HTML tags
	 */
	private static final Pattern INTER_TAG_PATTERN_TAG_TAG = Pattern.compile(">\\s+<",
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match spaces between tags and custom markers
	 */
	private static final Pattern INTER_TAG_PATTERN_TAG_CUSTOM = Pattern.compile(">\\s+%%%~",
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match spaces between custom markers and tags
	 */
	private static final Pattern INTER_TAG_PATTERN_CUSTOM_TAG = Pattern.compile("~%%%\\s+<",
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match spaces between custom markers
	 */
	private static final Pattern INTER_TAG_PATTERN_CUSTOM_CUSTOM = Pattern.compile("~%%%\\s+%%%~",
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match multiple spaces
	 */
	private static final Pattern MULTI_SPACE_PATTERN = Pattern.compile("\\s+",
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match ending spaces in tags
	 */
	private static final Pattern TAG_END_SPACE_PATTERN = Pattern.compile("(<(?:[^>]+?))(?:\\s+?)(/?>)",
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match quoted attributes in tags
	 */
	private static final Pattern TAG_QUOTE_PATTERN = Pattern.compile("\\s*=\\s*([\"'])([a-z0-9-_]+?)\\1(/?)(?=[^<]*?>)",
			Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match PRE tags
	 */
	private static final Pattern PRE_PATTERN = Pattern.compile("(<pre[^>]*?>)(.*?)(</pre>)",
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match TEXTAREA tags
	 */
	private static final Pattern TA_PATTERN = Pattern.compile("(<textarea[^>]*?>)(.*?)(</textarea>)",
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match SCRIPT tags
	 */
	private static final Pattern SCRIPT_PATTERN = Pattern.compile("(<script[^>]*?>)(.*?)(</script>)",
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match STYLE tags
	 */
	private static final Pattern STYLE_PATTERN = Pattern.compile("(<style[^>]*?>)(.*?)(</style>)",
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match tag properties
	 */
	private static final Pattern TAG_PROPERTY_PATTERN = Pattern.compile("(\\s\\w+)\\s*=\\s*(?=[^<]*?>)",
			Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match DOCTYPE declarations
	 */
	private static final Pattern DOCTYPE_PATTERN = Pattern.compile("<!DOCTYPE[^>]*>",
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match JavaScript type attributes
	 */
	private static final Pattern JS_TYPE_ATTR_PATTERN = Pattern.compile(
			"(<script[^>]*)type\\s*=\\s*([\"']*)(?:text|application)/javascript\\2([^>]*>)",
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match JavaScript language attributes
	 */
	private static final Pattern JS_LANG_ATTR_PATTERN = Pattern.compile(
			"(<script[^>]*)language\\s*=\\s*([\"']*)javascript\\2([^>]*>)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match jQuery template type attributes
	 */
	private static final Pattern JS_JQUERY_TMPL_TYPE_PATTERN = Pattern.compile(
			"<script[^>]*type\\s*=\\s*([\"']*)text/x-jquery-tmpl\\1[^>]*>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match style type attributes
	 */
	private static final Pattern STYLE_TYPE_ATTR_PATTERN = Pattern
		.compile("(<style[^>]*)type\\s*=\\s*([\"']*)text/style\\2([^>]*>)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match link type attributes
	 */
	private static final Pattern LINK_TYPE_ATTR_PATTERN = Pattern.compile(
			"(<link[^>]*)type\\s*=\\s*([\"']*)text/(?:css|plain)\\2([^>]*>)",
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match link rel attributes
	 */
	private static final Pattern LINK_REL_ATTR_PATTERN = Pattern.compile(
			"<link(?:[^>]*)rel\\s*=\\s*([\"']*)(?:alternate\\s+)?stylesheet\\1(?:[^>]*)>",
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match form method attributes
	 */
	private static final Pattern FORM_METHOD_ATTR_PATTERN = Pattern
		.compile("(<form[^>]*)method\\s*=\\s*([\"']*)get\\2([^>]*>)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match input type attributes
	 */
	private static final Pattern INPUT_TYPE_ATTR_PATTERN = Pattern
		.compile("(<input[^>]*)type\\s*=\\s*([\"']*)text\\2([^>]*>)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match boolean attributes
	 */
	private static final Pattern BOOLEAN_ATTR_PATTERN = Pattern.compile(
			"(<\\w+[^>]*)(checked|selected|disabled|readonly)\\s*=\\s*([\"']*)\\w*\\3([^>]*>)",
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match HTTP protocol in attributes
	 */
	private static final Pattern HTTP_PROTOCOL_PATTERN = Pattern.compile(
			"(<[^>]+?(?:href|src|cite|action)\\s*=\\s*['\"])http:(//[^>]+?>)",
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match HTTPS protocol in attributes
	 */
	private static final Pattern HTTPS_PROTOCOL_PATTERN = Pattern.compile(
			"(<[^>]+?(?:href|src|cite|action)\\s*=\\s*['\"])https:(//[^>]+?>)",
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match external rel attributes
	 */
	private static final Pattern REL_EXTERNAL_PATTERN = Pattern.compile(
			"<(?:[^>]*)rel\\s*=\\s*([\"']*)(?:alternate\\s+)?external\\1(?:[^>]*)>",
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match event attributes with double quotes
	 */
	private static final Pattern EVENT_PATTERN1 = Pattern
		.compile("(\\son[a-z]+\\s*=\\s*\")([^\"\\\\\\r\\n]*(?:\\\\.[^\"\\\\\\r\\n]*)*)(\")", Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match event attributes with single quotes
	 */
	private static final Pattern EVENT_PATTERN2 = Pattern
		.compile("(\\son[a-z]+\\s*=\\s*')([^'\\\\\\r\\n]*(?:\\\\.[^'\\\\\\r\\n]*)*)(')", Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern to match line breaks
	 */
	private static final Pattern LINE_BREAK_PATTERN = Pattern.compile("(?:\\p{Blank}*(\\r?\\n)\\p{Blank}*)+");

	/**
	 * Pattern to match temporary conditional comment blocks
	 */
	private static final Pattern TEMP_COND_COMMENT_PATTERN = Pattern.compile("%%%~COMPRESS~COND~(\\d+?)~%%%");

	/**
	 * Pattern to match temporary PRE blocks
	 */
	private static final Pattern TEMP_PRE_PATTERN = Pattern.compile("%%%~COMPRESS~PRE~(\\d+?)~%%%");

	/**
	 * Pattern to match temporary TEXTAREA blocks
	 */
	private static final Pattern TEMP_TEXT_AREA_PATTERN = Pattern.compile("%%%~COMPRESS~TEXTAREA~(\\d+?)~%%%");

	/**
	 * Pattern to match temporary SCRIPT blocks
	 */
	private static final Pattern TEMP_SCRIPT_PATTERN = Pattern.compile("%%%~COMPRESS~SCRIPT~(\\d+?)~%%%");

	/**
	 * Pattern to match temporary STYLE blocks
	 */
	private static final Pattern TEMP_STYLE_PATTERN = Pattern.compile("%%%~COMPRESS~STYLE~(\\d+?)~%%%");

	/**
	 * Pattern to match temporary EVENT blocks
	 */
	private static final Pattern TEMP_EVENT_PATTERN = Pattern.compile("%%%~COMPRESS~EVENT~(\\d+?)~%%%");

	/**
	 * Pattern to match temporary SKIP blocks
	 */
	private static final Pattern TEMP_SKIP_PATTERN = Pattern.compile("%%%~COMPRESS~SKIP~(\\d+?)~%%%");

	/**
	 * Pattern to match temporary LINE BREAK blocks
	 */
	private static final Pattern TEMP_LINE_BREAK_PATTERN = Pattern.compile("%%%~COMPRESS~LT~(\\d+?)~%%%");

	/**
	 * Compresses the given HTML content by removing unnecessary whitespace, comments and
	 * other elements while preserving the content structure.
	 * @param html The HTML content to compress
	 * @return The compressed HTML content
	 */
	public static String compress(String html) {
		if (!ENABLED || html == null || html.length() == 0) {
			return html;
		}

		// preserved block containers
		List<String> condCommentBlocks = new ArrayList<>();
		List<String> preBlocks = new ArrayList<>();
		List<String> taBlocks = new ArrayList<>();
		List<String> scriptBlocks = new ArrayList<>();
		List<String> styleBlocks = new ArrayList<>();
		List<String> eventBlocks = new ArrayList<>();
		List<String> skipBlocks = new ArrayList<>();
		List<String> lineBreakBlocks = new ArrayList<>();
		List<List<String>> userBlocks = new ArrayList<>();

		// preserve blocks
		html = preserveBlocks(html, preBlocks, taBlocks, scriptBlocks, styleBlocks, eventBlocks, condCommentBlocks,
				skipBlocks, lineBreakBlocks, userBlocks);

		// process pure html
		html = processHtml(html);

		// put preserved blocks back
		html = returnBlocks(html, preBlocks, taBlocks, scriptBlocks, styleBlocks, eventBlocks, condCommentBlocks,
				skipBlocks, lineBreakBlocks, userBlocks);

		return html;
	}

	/**
	 * Preserves specific blocks of HTML content from compression by replacing them with
	 * temporary markers. This includes PRE tags, TEXTAREA tags, SCRIPT tags, STYLE tags,
	 * comments and custom preserved blocks.
	 * @param html The HTML content to process
	 * @param preBlocks List to store preserved PRE tag contents
	 * @param taBlocks List to store preserved TEXTAREA tag contents
	 * @param scriptBlocks List to store preserved SCRIPT tag contents
	 * @param styleBlocks List to store preserved STYLE tag contents
	 * @param eventBlocks List to store preserved event attributes
	 * @param condCommentBlocks List to store preserved conditional comments
	 * @param skipBlocks List to store preserved skip blocks
	 * @param lineBreakBlocks List to store preserved line breaks
	 * @param userBlocks List to store preserved user-defined blocks
	 * @return The HTML content with preserved blocks replaced by markers
	 */
	private static String preserveBlocks(String html, List<String> preBlocks, List<String> taBlocks,
			List<String> scriptBlocks, List<String> styleBlocks, List<String> eventBlocks,
			List<String> condCommentBlocks, List<String> skipBlocks, List<String> lineBreakBlocks,
			List<List<String>> userBlocks) {

		// preserve <!-- {{{ ---><!-- }}} ---> skip blocks
		Matcher matcher = SKIP_PATTERN.matcher(html);
		int index = 0;
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			if (matcher.group(1).trim().length() > 0) {
				skipBlocks.add(matcher.group(1));
				matcher.appendReplacement(sb, MessageFormat.format(TEMP_SKIP_BLOCK, index++));
			}
		}
		matcher.appendTail(sb);
		html = sb.toString();

		// preserve conditional comments
		matcher = COND_COMMENT_PATTERN.matcher(html);
		index = 0;
		sb = new StringBuffer();
		while (matcher.find()) {
			if (matcher.group(2).trim().length() > 0) {
				condCommentBlocks
					.add(matcher.group(1) + HtmlCompressorUtil.compress(matcher.group(2)) + matcher.group(3));
				matcher.appendReplacement(sb, MessageFormat.format(TEMP_COND_COMMENT_BLOCK, index++));
			}
		}
		matcher.appendTail(sb);
		html = sb.toString();

		// preserve inline events
		matcher = EVENT_PATTERN1.matcher(html);
		index = 0;
		sb = new StringBuffer();
		while (matcher.find()) {
			if (matcher.group(2).trim().length() > 0) {
				eventBlocks.add(matcher.group(2));
				matcher.appendReplacement(sb, "$1" + MessageFormat.format(TEMP_EVENT_BLOCK, index++) + "$3");
			}
		}
		matcher.appendTail(sb);
		html = sb.toString();

		matcher = EVENT_PATTERN2.matcher(html);
		sb = new StringBuffer();
		while (matcher.find()) {
			if (matcher.group(2).trim().length() > 0) {
				eventBlocks.add(matcher.group(2));
				matcher.appendReplacement(sb, "$1" + MessageFormat.format(TEMP_EVENT_BLOCK, index++) + "$3");
			}
		}
		matcher.appendTail(sb);
		html = sb.toString();

		// preserve PRE tags
		matcher = PRE_PATTERN.matcher(html);
		index = 0;
		sb = new StringBuffer();
		while (matcher.find()) {
			if (matcher.group(2).trim().length() > 0) {
				preBlocks.add(matcher.group(2));
				matcher.appendReplacement(sb, "$1" + MessageFormat.format(TEMP_PRE_BLOCK, index++) + "$3");
			}
		}
		matcher.appendTail(sb);
		html = sb.toString();

		// preserve SCRIPT tags
		matcher = SCRIPT_PATTERN.matcher(html);
		index = 0;
		sb = new StringBuffer();
		while (matcher.find()) {
			// ignore empty scripts
			if (matcher.group(2).trim().length() > 0) {
				// ignore jquery templates
				if (!JS_JQUERY_TMPL_TYPE_PATTERN.matcher(matcher.group(1)).matches()) {
					scriptBlocks.add(matcher.group(2));
					matcher.appendReplacement(sb, "$1" + MessageFormat.format(TEMP_SCRIPT_BLOCK, index++) + "$3");
				}
			}
		}
		matcher.appendTail(sb);
		html = sb.toString();

		// preserve STYLE tags
		matcher = STYLE_PATTERN.matcher(html);
		index = 0;
		sb = new StringBuffer();
		while (matcher.find()) {
			if (matcher.group(2).trim().length() > 0) {
				styleBlocks.add(matcher.group(2));
				matcher.appendReplacement(sb, "$1" + MessageFormat.format(TEMP_STYLE_BLOCK, index++) + "$3");
			}
		}
		matcher.appendTail(sb);
		html = sb.toString();

		// preserve TEXTAREA tags
		matcher = TA_PATTERN.matcher(html);
		index = 0;
		sb = new StringBuffer();
		while (matcher.find()) {
			if (matcher.group(2).trim().length() > 0) {
				taBlocks.add(matcher.group(2));
				matcher.appendReplacement(sb, "$1" + MessageFormat.format(TEMP_TEXT_AREA_BLOCK, index++) + "$3");
			}
		}
		matcher.appendTail(sb);
		html = sb.toString();

		// preserve line breaks
		if (PRESERVE_LINE_BREAKS) {
			matcher = LINE_BREAK_PATTERN.matcher(html);
			index = 0;
			sb = new StringBuffer();
			while (matcher.find()) {
				lineBreakBlocks.add(matcher.group(1));
				matcher.appendReplacement(sb, MessageFormat.format(TEMP_LINE_BREAK_BLOCK, index++));
			}
			matcher.appendTail(sb);
			html = sb.toString();
		}

		return html;
	}

	/**
	 * Restores previously preserved blocks back into the compressed HTML content.
	 * @param html The compressed HTML content with temporary markers
	 * @param preBlocks List of preserved PRE tag contents
	 * @param taBlocks List of preserved TEXTAREA tag contents
	 * @param scriptBlocks List of preserved SCRIPT tag contents
	 * @param styleBlocks List of preserved STYLE tag contents
	 * @param eventBlocks List of preserved event attributes
	 * @param condCommentBlocks List of preserved conditional comments
	 * @param skipBlocks List of preserved skip blocks
	 * @param lineBreakBlocks List of preserved line breaks
	 * @param userBlocks List of preserved user-defined blocks
	 * @return The final HTML content with all preserved blocks restored
	 */
	private static String returnBlocks(String html, List<String> preBlocks, List<String> taBlocks,
			List<String> scriptBlocks, List<String> styleBlocks, List<String> eventBlocks,
			List<String> condCommentBlocks, List<String> skipBlocks, List<String> lineBreakBlocks,
			List<List<String>> userBlocks) {

		// put line breaks back
		if (PRESERVE_LINE_BREAKS) {
			Matcher matcher = TEMP_LINE_BREAK_PATTERN.matcher(html);
			StringBuffer sb = new StringBuffer();
			while (matcher.find()) {
				int i = Integer.parseInt(matcher.group(1));
				if (lineBreakBlocks.size() > i) {
					matcher.appendReplacement(sb, lineBreakBlocks.get(i));
				}
			}
			matcher.appendTail(sb);
			html = sb.toString();
		}

		// put TEXTAREA blocks back
		Matcher matcher = TEMP_TEXT_AREA_PATTERN.matcher(html);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			int i = Integer.parseInt(matcher.group(1));
			if (taBlocks.size() > i) {
				matcher.appendReplacement(sb, Matcher.quoteReplacement(taBlocks.get(i)));
			}
		}
		matcher.appendTail(sb);
		html = sb.toString();

		// put STYLE blocks back
		matcher = TEMP_STYLE_PATTERN.matcher(html);
		sb = new StringBuffer();
		while (matcher.find()) {
			int i = Integer.parseInt(matcher.group(1));
			if (styleBlocks.size() > i) {
				matcher.appendReplacement(sb, Matcher.quoteReplacement(styleBlocks.get(i)));
			}
		}
		matcher.appendTail(sb);
		html = sb.toString();

		// put SCRIPT blocks back
		matcher = TEMP_SCRIPT_PATTERN.matcher(html);
		sb = new StringBuffer();
		while (matcher.find()) {
			int i = Integer.parseInt(matcher.group(1));
			if (scriptBlocks.size() > i) {
				matcher.appendReplacement(sb, Matcher.quoteReplacement(scriptBlocks.get(i)));
			}
		}
		matcher.appendTail(sb);
		html = sb.toString();

		// put PRE blocks back
		matcher = TEMP_PRE_PATTERN.matcher(html);
		sb = new StringBuffer();
		while (matcher.find()) {
			int i = Integer.parseInt(matcher.group(1));
			if (preBlocks.size() > i) {
				matcher.appendReplacement(sb, Matcher.quoteReplacement(preBlocks.get(i)));
			}
		}
		matcher.appendTail(sb);
		html = sb.toString();

		// put event blocks back
		matcher = TEMP_EVENT_PATTERN.matcher(html);
		sb = new StringBuffer();
		while (matcher.find()) {
			int i = Integer.parseInt(matcher.group(1));
			if (eventBlocks.size() > i) {
				matcher.appendReplacement(sb, Matcher.quoteReplacement(eventBlocks.get(i)));
			}
		}
		matcher.appendTail(sb);
		html = sb.toString();

		// put conditional comments back
		matcher = TEMP_COND_COMMENT_PATTERN.matcher(html);
		sb = new StringBuffer();
		while (matcher.find()) {
			int i = Integer.parseInt(matcher.group(1));
			if (condCommentBlocks.size() > i) {
				matcher.appendReplacement(sb, Matcher.quoteReplacement(condCommentBlocks.get(i)));
			}
		}
		matcher.appendTail(sb);
		html = sb.toString();

		// put skip blocks back
		matcher = TEMP_SKIP_PATTERN.matcher(html);
		sb = new StringBuffer();
		while (matcher.find()) {
			int i = Integer.parseInt(matcher.group(1));
			if (skipBlocks.size() > i) {
				matcher.appendReplacement(sb, Matcher.quoteReplacement(skipBlocks.get(i)));
			}
		}
		matcher.appendTail(sb);
		html = sb.toString();

		return html;
	}

	/**
	 * Processes the HTML content by applying various compression techniques.
	 * @param html The HTML content to process
	 * @return The processed HTML content
	 */
	private static String processHtml(String html) {

		// remove comments
		html = removeComments(html);

		// simplify doctype
		html = simpleDoctype(html);

		// remove script attributes
		html = removeScriptAttributes(html);

		// remove style attributes
		html = removeStyleAttributes(html);

		// remove link attributes
		html = removeLinkAttributes(html);

		// remove form attributes
		html = removeFormAttributes(html);

		// remove input attributes
		html = removeInputAttributes(html);

		// simplify boolean attributes
		html = simpleBooleanAttributes(html);

		// remove http from attributes
		html = removeHttpProtocol(html);

		// remove https from attributes
		html = removeHttpsProtocol(html);

		// remove inter-tag spaces
		html = removeInterTagSpaces(html);

		// remove multi whitespace characters
		html = removeMultiSpaces(html);

		// remove spaces around equals sign and ending spaces
		html = removeSpacesInsideTags(html);

		// remove quotes from tag attributes
		html = removeQuotesInsideTags(html);

		return html.trim();
	}

	/**
	 * Removes quotes from HTML tag attributes where possible.
	 * @param html The HTML content to process
	 * @return The HTML content with unnecessary quotes removed
	 */
	private static String removeQuotesInsideTags(String html) {
		// remove quotes from tag attributes
		if (REMOVE_QUOTES) {
			Matcher matcher = TAG_QUOTE_PATTERN.matcher(html);
			StringBuffer sb = new StringBuffer();
			while (matcher.find()) {
				// if quoted attribute is followed by "/" add extra space
				if (matcher.group(3).trim().length() == 0) {
					matcher.appendReplacement(sb, "=$2");
				}
				else {
					matcher.appendReplacement(sb, "=$2 $3");
				}
			}
			matcher.appendTail(sb);
			html = sb.toString();

		}
		return html;
	}

	/**
	 * Removes unnecessary spaces inside HTML tags.
	 * @param html The HTML content to process
	 * @return The HTML content with unnecessary spaces removed from inside tags
	 */
	private static String removeSpacesInsideTags(String html) {
		// remove spaces around equals sign inside tags
		html = TAG_PROPERTY_PATTERN.matcher(html).replaceAll("$1=");

		// remove ending spaces inside tags
		html = TAG_END_SPACE_PATTERN.matcher(html).replaceAll("$1$2");
		return html;
	}

	/**
	 * Removes multiple consecutive spaces from HTML content.
	 * @param html The HTML content to process
	 * @return The HTML content with multiple spaces collapsed to single spaces
	 */
	private static String removeMultiSpaces(String html) {
		// collapse multiple spaces
		if (REMOVE_MULTI_SPACES) {
			html = MULTI_SPACE_PATTERN.matcher(html).replaceAll(" ");
		}
		return html;
	}

	/**
	 * Removes spaces between HTML tags.
	 * @param html The HTML content to process
	 * @return The HTML content with spaces between tags removed
	 */
	private static String removeInterTagSpaces(String html) {
		// remove inter-tag spaces
		if (REMOVE_INTER_TAG_SPACES) {
			html = INTER_TAG_PATTERN_TAG_TAG.matcher(html).replaceAll("><");
			html = INTER_TAG_PATTERN_TAG_CUSTOM.matcher(html).replaceAll(">%%%~");
			html = INTER_TAG_PATTERN_CUSTOM_TAG.matcher(html).replaceAll("~%%%<");
			html = INTER_TAG_PATTERN_CUSTOM_CUSTOM.matcher(html).replaceAll("~%%%%%%~");
		}
		return html;
	}

	/**
	 * Removes HTML comments from the content.
	 * @param html The HTML content to process
	 * @return The HTML content with comments removed
	 */
	private static String removeComments(String html) {
		// remove comments
		if (REMOVE_COMMENTS) {
			html = COMMENT_PATTERN.matcher(html).replaceAll("");
		}
		return html;
	}

	/**
	 * Simplifies the DOCTYPE declaration to HTML5 format.
	 * @param html The HTML content to process
	 * @return The HTML content with simplified DOCTYPE
	 */
	private static String simpleDoctype(String html) {
		// simplify doctype
		if (SIMPLE_DOCTYPE) {
			html = DOCTYPE_PATTERN.matcher(html).replaceAll("<!DOCTYPE html>");
		}
		return html;
	}

	/**
	 * Removes unnecessary attributes from script tags.
	 * @param html The HTML content to process
	 * @return The HTML content with unnecessary script attributes removed
	 */
	private static String removeScriptAttributes(String html) {

		if (REMOVE_SCRIPT_ATTRIBUTES) {
			// remove type from script tags
			html = JS_TYPE_ATTR_PATTERN.matcher(html).replaceAll("$1$3");

			// remove language from script tags
			html = JS_LANG_ATTR_PATTERN.matcher(html).replaceAll("$1$3");
		}
		return html;
	}

	/**
	 * Removes unnecessary attributes from style tags.
	 * @param html The HTML content to process
	 * @return The HTML content with unnecessary style attributes removed
	 */
	private static String removeStyleAttributes(String html) {
		// remove type from style tags
		if (REMOVE_STYLE_ATTRIBUTES) {
			html = STYLE_TYPE_ATTR_PATTERN.matcher(html).replaceAll("$1$3");
		}
		return html;
	}

	/**
	 * Removes unnecessary attributes from link tags.
	 * @param html The HTML content to process
	 * @return The HTML content with unnecessary link attributes removed
	 */
	private static String removeLinkAttributes(String html) {
		// remove type from link tags with rel=stylesheet
		if (REMOVE_LINK_ATTRIBUTES) {
			Matcher matcher = LINK_TYPE_ATTR_PATTERN.matcher(html);
			StringBuffer sb = new StringBuffer();
			while (matcher.find()) {
				// if rel=stylesheet
				if (LINK_REL_ATTR_PATTERN.matcher(matcher.group(0)).matches()) {
					matcher.appendReplacement(sb, "$1$3");
				}
				else {
					matcher.appendReplacement(sb, "$0");
				}
			}
			matcher.appendTail(sb);
			html = sb.toString();
		}
		return html;
	}

	/**
	 * Removes unnecessary attributes from form tags.
	 * @param html The HTML content to process
	 * @return The HTML content with unnecessary form attributes removed
	 */
	private static String removeFormAttributes(String html) {
		// remove method from form tags
		if (REMOVE_FORM_ATTRIBUTES) {
			html = FORM_METHOD_ATTR_PATTERN.matcher(html).replaceAll("$1$3");
		}
		return html;
	}

	/**
	 * Removes unnecessary attributes from input tags.
	 * @param html The HTML content to process
	 * @return The HTML content with unnecessary input attributes removed
	 */
	private static String removeInputAttributes(String html) {
		// remove type from input tags
		if (REMOVE_INPUT_ATTRIBUTES) {
			html = INPUT_TYPE_ATTR_PATTERN.matcher(html).replaceAll("$1$3");
		}
		return html;
	}

	/**
	 * Simplifies boolean attributes in HTML tags.
	 * @param html The HTML content to process
	 * @return The HTML content with simplified boolean attributes
	 */
	private static String simpleBooleanAttributes(String html) {
		// simplify boolean attributes
		if (SIMPLE_BOOLEAN_ATTRIBUTES) {
			html = BOOLEAN_ATTR_PATTERN.matcher(html).replaceAll("$1$2$4");
		}
		return html;
	}

	/**
	 * Removes HTTP protocol from URLs where possible.
	 * @param html The HTML content to process
	 * @return The HTML content with unnecessary HTTP protocols removed
	 */
	private static String removeHttpProtocol(String html) {
		// remove http protocol from tag attributes
		if (REMOVE_HTTP_PROTOCOL) {
			Matcher matcher = HTTP_PROTOCOL_PATTERN.matcher(html);
			StringBuffer sb = new StringBuffer();
			while (matcher.find()) {
				// if rel!=external
				if (!REL_EXTERNAL_PATTERN.matcher(matcher.group(0)).matches()) {
					matcher.appendReplacement(sb, "$1$2");
				}
				else {
					matcher.appendReplacement(sb, "$0");
				}
			}
			matcher.appendTail(sb);
			html = sb.toString();
		}
		return html;
	}

	/**
	 * Removes HTTPS protocol from URLs where possible.
	 * @param html The HTML content to process
	 * @return The HTML content with unnecessary HTTPS protocols removed
	 */
	private static String removeHttpsProtocol(String html) {
		// remove https protocol from tag attributes
		if (REMOVE_HTTPS_PROTOCOL) {
			Matcher matcher = HTTPS_PROTOCOL_PATTERN.matcher(html);
			StringBuffer sb = new StringBuffer();
			while (matcher.find()) {
				// if rel!=external
				if (!REL_EXTERNAL_PATTERN.matcher(matcher.group(0)).matches()) {
					matcher.appendReplacement(sb, "$1$2");
				}
				else {
					matcher.appendReplacement(sb, "$0");
				}
			}
			matcher.appendTail(sb);
			html = sb.toString();
		}
		return html;
	}

}
