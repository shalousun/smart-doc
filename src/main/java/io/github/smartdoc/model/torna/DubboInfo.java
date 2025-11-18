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

package io.github.smartdoc.model.torna;

import java.io.Serializable;

/**
 * Torna Dubbo Info
 *
 * @author xingzi 2021/4/28 12:54
 * @since 2.1.5
 **/
public class DubboInfo implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1556292473952962536L;

	/**
	 * dubbo interface name
	 */
	private String interfaceName;

	/**
	 * author
	 */
	private String author;

	/**
	 * dubbo interface version
	 */
	private String version;

	/**
	 * dubbo protocol
	 */
	private String protocol;

	/**
	 * dubbo maven dependency
	 */
	private String dependency;

	public DubboInfo builder() {
		return new DubboInfo();
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public DubboInfo setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
		return this;
	}

	public String getAuthor() {
		return author;
	}

	public DubboInfo setAuthor(String author) {
		this.author = author;
		return this;
	}

	public String getVersion() {
		return version;
	}

	public DubboInfo setVersion(String version) {
		this.version = version;
		return this;
	}

	public String getProtocol() {
		return protocol;
	}

	public DubboInfo setProtocol(String protocol) {
		this.protocol = protocol;
		return this;
	}

	public String getDependency() {
		return dependency;
	}

	public DubboInfo setDependency(String dependency) {
		this.dependency = dependency;
		return this;
	}

	@Override
	public String toString() {
		return "DubboInfo{" + "interfaceName='" + interfaceName + '\'' + ", author='" + author + '\'' + ", version='"
				+ version + '\'' + ", protocol='" + protocol + '\'' + ", dependency='" + dependency + '\'' + '}';
	}

}
