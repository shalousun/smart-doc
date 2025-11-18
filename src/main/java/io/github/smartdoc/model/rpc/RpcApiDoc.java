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
package io.github.smartdoc.model.rpc;

import io.github.smartdoc.model.AbstractRpcApiDoc;
import io.github.smartdoc.model.RpcJavaMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yu 2020/5/16.
 */
public class RpcApiDoc extends AbstractRpcApiDoc<RpcJavaMethod> {

	private static final long serialVersionUID = -3116322721344529338L;

	/**
	 * tags
	 *
	 */
	private String[] tags;

	/**
	 * link
	 */
	private String link;

	/**
	 * group
	 */
	private String group;

	/**
	 * class in package name
	 */
	private String packageName;

	/**
	 * if this is group, then is true
	 */
	private boolean isFolder;

	/**
	 * children
	 */
	private List<RpcApiDoc> childrenApiDocs = new ArrayList<>();

	public String getLink() {
		return desc.replace(" ", "_").toLowerCase();
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public boolean isFolder() {
		return isFolder;
	}

	public void setFolder(boolean folder) {
		isFolder = folder;
	}

	public List<RpcApiDoc> getChildrenApiDocs() {
		return childrenApiDocs;
	}

	public void setChildrenApiDocs(List<RpcApiDoc> childrenApiDocs) {
		this.childrenApiDocs = childrenApiDocs;
	}

	public static RpcApiDoc buildGroupApiDoc(String group) {
		RpcApiDoc apiDoc = new RpcApiDoc();
		apiDoc.setFolder(true);
		apiDoc.setGroup(group);
		apiDoc.setAlias(group);
		apiDoc.setName(group);
		apiDoc.setDesc(group);
		apiDoc.setChildrenApiDocs(new ArrayList<>());
		return apiDoc;
	}

}
