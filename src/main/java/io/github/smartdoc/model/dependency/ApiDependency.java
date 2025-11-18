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

import java.util.List;
import java.util.Objects;

/**
 * @author Fio
 */
public class ApiDependency {

	/**
	 * The endpoint class's full qualified name
	 */
	private String clazz;

	/**
	 * Derived class full qualified name if the entry point class is interface
	 */
	private List<String> derivedClazz;

	/**
	 * Api methods in the entry point class
	 */
	private List<ApiInfo> apis;

	public ApiDependency() {
	}

	public ApiDependency(String clazz, List<String> derivedClazz, List<ApiInfo> apis) {
		this.clazz = clazz;
		this.derivedClazz = derivedClazz;
		this.apis = apis;
	}

	/**
	 * Api method simple info
	 */
	public static class ApiInfo {

		/**
		 * Api method name
		 */
		private String method;

		/**
		 * Api method args
		 */
		private List<String> args;

		/**
		 * Api method return，include the generics
		 */
		private List<String> returns;

		public ApiInfo() {
		}

		public ApiInfo(String method, List<String> args, List<String> returns) {
			this.method = method;
			this.args = args;
			this.returns = returns;
		}

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		public List<String> getArgs() {
			return args;
		}

		public void setArgs(List<String> args) {
			this.args = args;
		}

		public List<String> getReturns() {
			return returns;
		}

		public void setReturns(List<String> returns) {
			this.returns = returns;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof ApiInfo)) {
				return false;
			}
			ApiInfo apiInfo = (ApiInfo) o;
			return Objects.equals(method, apiInfo.method) && Objects.equals(args, apiInfo.args)
					&& Objects.equals(returns, apiInfo.returns);
		}

		@Override
		public int hashCode() {
			return Objects.hash(method, args, returns);
		}

	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public List<String> getDerivedClazz() {
		return derivedClazz;
	}

	public void setDerivedClazz(List<String> derivedClazz) {
		this.derivedClazz = derivedClazz;
	}

	public List<ApiInfo> getApis() {
		return apis;
	}

	public void setApis(List<ApiInfo> apis) {
		this.apis = apis;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ApiDependency)) {
			return false;
		}
		ApiDependency that = (ApiDependency) o;
		return Objects.equals(clazz, that.clazz) && Objects.equals(derivedClazz, that.derivedClazz)
				&& Objects.equals(apis, that.apis);
	}

	@Override
	public int hashCode() {
		return Objects.hash(clazz, derivedClazz, apis);
	}

}
