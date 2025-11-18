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
package io.github.smartdoc.model.grpc.proto;

import java.util.ArrayList;
import java.util.List;

/**
 * proto json.
 *
 * @author linwumingshi
 */
public class ProtoJson {

	/**
	 * files.
	 */
	private List<ProtoFile> files;

	/**
	 * scalarValueTypes.
	 */
	private List<ScalarValueType> scalarValueTypes;

	public static ProtoJson builder() {
		return new ProtoJson();
	}

	public List<ProtoFile> getFiles() {
		if (files == null) {
			files = new ArrayList<>();
		}
		return files;
	}

	public ProtoJson setFiles(List<ProtoFile> files) {
		this.files = files;
		return this;
	}

	public List<ScalarValueType> getScalarValueTypes() {
		return scalarValueTypes;
	}

	public ProtoJson setScalarValueTypes(List<ScalarValueType> scalarValueTypes) {
		this.scalarValueTypes = scalarValueTypes;
		return this;
	}

}
