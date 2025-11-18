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
package io.github.smartdoc.model.dependency;

import io.github.smartdoc.utils.JsonUtil;
import com.power.common.util.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Fio
 */
public class DependencyTree {

	private static final String CONFIG_NAME = ".smart-doc-dependency.json";

	/**
	 * whether increment build
	 */
	private transient boolean increment;

	/**
	 * dependency tree file
	 */
	private transient File configFile;

	/**
	 * The schema version for dependency tree format. Maybe you can rewrite the
	 * dependency-tree file when the format is incompatible.
	 */
	private String schema = "v1";

	/**
	 * Git commit id when smart-doc build.
	 */
	private String commitId;

	/**
	 * Api methods dependency trees.
	 */
	private List<ApiDependency> dependencyTree;

	private DependencyTree() {

	}

	/**
	 * Create or Load dependency tree config.
	 * @param baseDir the dependency tree config file base directory
	 * @param isIncrement whether increment build
	 * @return DependencyTree
	 */
	public static DependencyTree detect(String baseDir, boolean isIncrement) {
		DependencyTree dependencyTree;
		if (!isIncrement) {
			dependencyTree = new DependencyTree();
			dependencyTree.setIncrement(false);
			return dependencyTree;
		}

		File configFile = new File(baseDir + File.separator + CONFIG_NAME);
		// the config file no exists
		boolean fileNoExists = !configFile.exists();
		if (fileNoExists) {
			dependencyTree = Support.create(configFile);
		}
		else {
			dependencyTree = Support.load(configFile);
		}
		return dependencyTree;
	}

	public static void write(DependencyTree dependencyTree) {
		// do something when the dependency-tree format is changed dependent on the
		// version
		if (dependencyTree.isIncrement()) {
			Support.writeFile(dependencyTree);
		}
	}

	public String getSchema() {
		return schema;
	}

	public File getConfigFile() {
		return configFile;
	}

	public String getCommitId() {
		return commitId;
	}

	public List<ApiDependency> getDependencyTree() {
		return dependencyTree;
	}

	public void setConfig(String commitId, List<ApiDependency> dependencyTree) {
		this.commitId = commitId;
		this.dependencyTree = dependencyTree;
	}

	public boolean isIncrement() {
		return increment;
	}

	public void setIncrement(boolean increment) {
		this.increment = increment;
	}

	/**
	 * Provide some utility methods
	 */
	private static class Support {

		private static DependencyTree create(File configFile) {
			if (!configFile.exists()) {
				try {
					Files.createFile(configFile.toPath());
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			DependencyTree instance = new DependencyTree();
			instance.configFile = configFile;
			instance.setConfig("", Collections.emptyList());

			writeFile(instance);

			return instance;
		}

		private static DependencyTree load(File configFile) {
			if (configFile == null || !configFile.exists()) {
				return null;
			}

			String content = readFile(configFile);
			DependencyTree instance = JsonUtil.toObject(content, DependencyTree.class);
			instance.configFile = configFile;
			return instance;
		}

		private static void writeFile(DependencyTree instance) {
			List<ApiDependency> distinctDependency = instance.getDependencyTree()
				.stream()
				.distinct()
				.collect(Collectors.toList());
			instance.setConfig(instance.getCommitId(), distinctDependency);
			String content = JsonUtil.toPrettyJson(instance);
			FileUtil.writeFileNotAppend(content, instance.configFile.getAbsolutePath());
		}

		private static String readFile(File configFile) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(configFile));
				StringBuilder builder = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				return builder.toString();
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	}

}
