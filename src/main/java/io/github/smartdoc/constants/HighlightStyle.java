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
package io.github.smartdoc.constants;

import com.power.common.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Highlight style
 *
 * @author jitmit 2020/11/16
 */
public class HighlightStyle {

	/**
	 * Default style
	 */
	public static final String DEFAULT_STYLE = "github";

	/**
	 * Dark style
	 */
	private static final List<String> DARK_STYLE;

	/**
	 * Light style
	 */
	private static final List<String> LIGHT_STYLE;

	/**
	 * key is style,value is color
	 */
	private static final Map<String, String> BACKGROUND = new HashMap<>(76);

	static {
		LIGHT_STYLE = Stream
			.of(DEFAULT_STYLE, "a11y-light", "arduino-light", "ascetic", "atelier-cave-light", "atelier-dune-light",
					"atelier-estuary-light", "atelier-forest-light", "atelier-heath-light", "atelier-lakeside-light",
					"atelier-plateau-light", "atelier-savanna-light", "atelier-seaside-light",
					"atelier-sulphurpool-light", "atom-one-light", "color-brewer", "docco", "github-gist", "googlecode",
					"grayscale", "gruvbox-light", "idea", "isbl-editor-light", "kimbie.light", "lightfair", "magula",
					"mono-blue", "nnfx", "paraiso-light", "purebasic", "qtcreator_light", "routeros", "school-book",
					"solarized-light", "tomorrow", "vs", "xcode")
			.collect(Collectors.toList());

	}

	static {
		DARK_STYLE = Stream
			.of("a11y-dark", "agate", "an-old-hope", "androidstudio", "arta", "atelier-cave-dark", "atelier-dune-dark",
					"atelier-estuary-dark", "atelier-forest-dark", "atelier-heath-dark", "atelier-lakeside-dark",
					"atelier-plateau-dark", "atelier-savanna-dark", "atelier-seaside-dark", "atelier-sulphurpool-dark",
					"atom-one-dark-reasonable", "atom-one-dark", "brown-paper", "codepen-embed", "darcula", "dark",
					"default", "dracula", "far", "foundation", "gml", "gradient-dark", "gruvbox-dark", "hopscotch",
					"hybrid", "ir-black", "isbl-editor-dark", "kimbie.dark", "lioshi", "monokai", "monokai-sublime",
					"night-owl", "nnfx-dark", "nord", "obsidian", "ocean", "paraiso-dark", "pojoaque", "qtcreator_dark",
					"railscasts", "rainbow", "shades-of-purple", "solarized-dark", "srcery", "sunburst",
					"tomorrow-night", "tomorrow-night-blue", "tomorrow-night-bright", "tomorrow-night-eighties",
					"vs2015", "xt256", "zenburn")
			.collect(Collectors.toList());
	}

	static {
		BACKGROUND.put("a11y-dark", "#2b2b2b");
		BACKGROUND.put("agate", "#333");
		BACKGROUND.put("androidstudio", "#282b2e");
		BACKGROUND.put("atom-one-light", "#fafafa");
		BACKGROUND.put("an-old-hope", "#1c1d21");
		BACKGROUND.put("arta", "#222");
		BACKGROUND.put("atelier-cave-dark", "#19171c");
		BACKGROUND.put("atelier-cave-light", "#efecf4");
		BACKGROUND.put("atelier-dune-dark", "#20201d");
		BACKGROUND.put("atelier-dune-light", "#fefbec");
		BACKGROUND.put("atelier-estuary-dark", "#22221b");
		BACKGROUND.put("atelier-estuary-light", "#f4f3ec");
		BACKGROUND.put("atelier-forest-dark", "#1b1918");
		BACKGROUND.put("atelier-forest-light", "#f1efee");
		BACKGROUND.put("atelier-heath-dark", "#1b181b");
		BACKGROUND.put("atelier-heath-light", "#f7f3f7");
		BACKGROUND.put("atelier-lakeside-dark", "#161b1d");
		BACKGROUND.put("atelier-lakeside-light", "#ebf8ff");
		BACKGROUND.put("atelier-plateau-dark", "#1b1818");
		BACKGROUND.put("atelier-plateau-light", "#f4ecec");
		BACKGROUND.put("atelier-savanna-dark", "#171c19");
		BACKGROUND.put("atelier-savanna-light", "#ecf4ee");
		BACKGROUND.put("atelier-seaside-dark", "#131513");
		BACKGROUND.put("atelier-seaside-light", "#f4fbf4");
		BACKGROUND.put("atelier-sulphurpool-dark", "#202746");
		BACKGROUND.put("atelier-sulphurpool-light", "#f5f7ff");
		BACKGROUND.put("atom-one-dark", "#282c34");
		BACKGROUND.put("atom-one-dark-reasonable", "#282c34");
		BACKGROUND.put("codepen-embed", "#222");
		BACKGROUND.put("darcula", "#2b2b2b");
		BACKGROUND.put("dark", "#444");
		BACKGROUND.put("default", "#F0F0F0");
		BACKGROUND.put("docco", "#f8f8ff");
		BACKGROUND.put("dracula", "#282a36");
		BACKGROUND.put("far", "#000080");
		BACKGROUND.put("foundation", "#eee");
		BACKGROUND.put("github", "#f8f8f8");
		BACKGROUND.put("gml", "#222222");
		BACKGROUND.put("gradient-dark", "linear-gradient(166deg, rgba(80,31,122,1) 0%, rgba(40,32,179,1) 80%)");
		BACKGROUND.put("gruvbox-dark", "#282828");
		BACKGROUND.put("gruvbox-light", "#fbf1c7");
		BACKGROUND.put("hopscotch", "#322931");
		BACKGROUND.put("hybrid", "#1d1f21");
		BACKGROUND.put("ir-black", "#000");
		BACKGROUND.put("isbl-editor-dark", "#404040");
		BACKGROUND.put("kimbie.dark", "#221a0f");
		BACKGROUND.put("kimbie.light", "#fbebd4");
		BACKGROUND.put("lioshi", "#303030");
		BACKGROUND.put("magula", "#f4f4f4");
		BACKGROUND.put("mono-blue", "#eaeef3");
		BACKGROUND.put("monokai", "#272822");
		BACKGROUND.put("monokai-sublime", "#23241f");
		BACKGROUND.put("night-owl", "#011627");
		BACKGROUND.put("nnfx-dark", "#333");
		BACKGROUND.put("nord", "#2E3440");
		BACKGROUND.put("obsidian", "#282b2e");
		BACKGROUND.put("ocean", "#2b303b");
		BACKGROUND.put("paraiso-dark", "#2f1e2e");
		BACKGROUND.put("paraiso-light", "#e7e9db");
		BACKGROUND.put("purebasic", "#FFFFDF");
		BACKGROUND.put("qtcreator_dark", "#000000");
		BACKGROUND.put("railscasts", "#232323");
		BACKGROUND.put("rainbow", "#474949");
		BACKGROUND.put("routeros", "#f0f0f0");

		BACKGROUND.put("shades-of-purple", "#2d2b57");
		BACKGROUND.put("solarized-dark", "#002b36");
		BACKGROUND.put("solarized-light", "#fdf6e3");
		BACKGROUND.put("srcery", "#1C1B19");
		BACKGROUND.put("sunburst", "#000");
		BACKGROUND.put("tomorrow-night", "#1d1f21");
		BACKGROUND.put("tomorrow-night-blue", "#002451");
		BACKGROUND.put("tomorrow-night-bright", "black");
		BACKGROUND.put("tomorrow-night-eighties", "#2d2d2d");
		BACKGROUND.put("xt256", "#000");
		BACKGROUND.put("vs2015", "#1E1E1E");
		BACKGROUND.put("zenburn", "#3f3f3f");
	}

	/**
	 * Randomly select a light style
	 * @param random Random
	 * @return String of random
	 */
	public static String randomLight(Random random) {
		return LIGHT_STYLE.get(random.nextInt(LIGHT_STYLE.size()));
	}

	/**
	 * Randomly select a dark style
	 * @param random Random
	 * @return String of random
	 */
	public static String randomDark(Random random) {
		return DARK_STYLE.get(random.nextInt(DARK_STYLE.size()));
	}

	/**
	 * Randomly select a style
	 * @param random Random
	 * @return String of random
	 */
	public static String randomAll(Random random) {
		if (random.nextBoolean()) {
			return randomLight(random);
		}
		else {
			return randomDark(random);
		}
	}

	/**
	 * Get background color
	 * @param style Highlight style
	 * @return String of background color
	 */
	public static String getBackgroundColor(String style) {
		String color = BACKGROUND.get(style);
		if (StringUtil.isNotEmpty(color)) {
			return color;
		}
		return "#f7f7f8";
	}

	/**
	 * Does the highlight style exist?
	 * @param style Highlight style
	 * @return boolean
	 */
	public static boolean containsStyle(String style) {
		return BACKGROUND.containsKey(style);
	}

}
