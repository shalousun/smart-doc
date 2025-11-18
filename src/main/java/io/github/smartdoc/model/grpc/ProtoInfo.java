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
package io.github.smartdoc.model.grpc;

import io.github.smartdoc.constants.DocGlobalConstants;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * proto info.
 *
 * @author linwumingshi
 */
public class ProtoInfo implements Serializable {

	private static final Logger log = Logger.getLogger(ProtoInfo.class.getName());

	private static final long serialVersionUID = 4962891140273167418L;

	/**
	 * is Windows os
	 */
	private boolean winOs;

	/**
	 * source protoc path in resource.
	 */
	private String sourceProtocPath;

	/**
	 * source protoc-gen-doc path in resource.
	 */
	private String sourceProtocGenDocPath;

	/**
	 * protoc path.
	 */
	private String protocPath;

	/**
	 * protoc-gen-doc path.
	 */
	private String protocGenDocPath;

	/**
	 * target json directory path.
	 */
	private String targetJsonDirectoryPath;

	/**
	 * target json file path.
	 */
	private String targetJsonFilePath;

	/**
	 * json name.
	 */
	private String jsonName;

	public static ProtoInfo build() {
		return new ProtoInfo();
	}

	private ProtoInfo() {
		this.winOs = false;
		String targetJsonPath = DocGlobalConstants.ABSOLUTE_TARGET_CLASS_PATH + "/json/";
		String targetShPath = DocGlobalConstants.ABSOLUTE_TARGET_CLASS_PATH + "/sh/";
		String os = System.getProperty("os.name").toLowerCase();
		String arch = System.getProperty("os.arch").toLowerCase();

		log.info("The [os.name] is: " + os + " ; [os.arch] is: " + arch);

		this.setTargetJsonDirectoryPath(targetJsonPath);
		this.setJsonName("combined.json");
		this.setTargetJsonFilePath(targetJsonPath + this.getJsonName());

		if (os.contains("win")) {
			this.winOs = true;
			this.setSourcePaths("/protoc/win/protoc.exe", "/protoc/win/protoc-gen-doc.exe");
		}
		else if (os.contains("mac")) {
			if (arch.contains("arm") || arch.contains("aarch")) {
				this.setSourcePaths("/protoc/mac/arm/protoc", "/protoc/mac/arm/protoc-gen-doc");
			}
			else {
				this.setSourcePaths("/protoc/mac/amd/protoc", "/protoc/mac/amd/protoc-gen-doc");
			}
		}
		else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
			if (arch.contains("arm")) {
				this.setSourcePaths("/protoc/linux/arm/protoc", "/protoc/linux/arm/protoc-gen-doc");
			}
			else {
				this.setSourcePaths("/protoc/linux/amd/protoc", "/protoc/linux/amd/protoc-gen-doc");
			}
		}

		this.setProtocPath(targetShPath + this.getSourceProtocPath());
		this.setProtocGenDocPath(targetShPath + this.getSourceProtocGenDocPath());
	}

	private void setSourcePaths(String protocPath, String protocGenDocPath) {
		this.setSourceProtocPath(protocPath);
		this.setSourceProtocGenDocPath(protocGenDocPath);
	}

	public boolean isWinOs() {
		return winOs;
	}

	public String getSourceProtocPath() {
		return sourceProtocPath;
	}

	public ProtoInfo setSourceProtocPath(String sourceProtocPath) {
		this.sourceProtocPath = sourceProtocPath;
		return this;
	}

	public String getSourceProtocGenDocPath() {
		return sourceProtocGenDocPath;
	}

	public ProtoInfo setSourceProtocGenDocPath(String sourceProtocGenDocPath) {
		this.sourceProtocGenDocPath = sourceProtocGenDocPath;
		return this;
	}

	public String getProtocPath() {
		return protocPath;
	}

	public ProtoInfo setProtocPath(String protocPath) {
		this.protocPath = protocPath;
		return this;
	}

	public String getProtocGenDocPath() {
		return protocGenDocPath;
	}

	public ProtoInfo setProtocGenDocPath(String protocGenDocPath) {
		this.protocGenDocPath = protocGenDocPath;
		return this;
	}

	public String getTargetJsonDirectoryPath() {
		return targetJsonDirectoryPath;
	}

	public ProtoInfo setTargetJsonDirectoryPath(String targetJsonDirectoryPath) {
		this.targetJsonDirectoryPath = targetJsonDirectoryPath;
		return this;
	}

	public String getJsonName() {
		return jsonName;
	}

	public ProtoInfo setJsonName(String jsonName) {
		this.jsonName = jsonName;
		return this;
	}

	public String getTargetJsonFilePath() {
		return targetJsonFilePath;
	}

	public ProtoInfo setTargetJsonFilePath(String targetJsonFilePath) {
		this.targetJsonFilePath = targetJsonFilePath;
		return this;
	}

}