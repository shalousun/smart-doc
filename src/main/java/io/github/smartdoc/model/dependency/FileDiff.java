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

import java.util.Objects;

/**
 * Mark the file change type
 *
 * @author Fio
 */
public class FileDiff {

	/**
	 * file change type
	 */
	private ChangeType changeType;

	/**
	 * old absolute path
	 */
	private String oldQualifiedName;

	/**
	 * new absolute path
	 */
	private String newQualifiedName;

	/**
	 * whether the class is entry point before build
	 */
	private boolean isEntryPoint = false;

	public enum ChangeType {

		/** Add a new file to the project */
		ADD,

		/** Modify an existing file in the project (content and/or mode) */
		MODIFY,

		/** Delete an existing file from the project */
		DELETE,

		/** Rename an existing file to a new location */
		RENAME,

		/** Copy an existing file to a new location, keeping the original */
		COPY,

		/**
		 * File uncommitted, only with newPackagePath
		 */
		UNCOMMITTED,

		/**
		 * File untracked, only with newPackagePath
		 */
		UNTRACKED,

		/**
		 * The class related, only with newPackagePath
		 */
		RELATED;

	}

	public ChangeType getChangeType() {
		return changeType;
	}

	public void setChangeType(ChangeType changeType) {
		this.changeType = changeType;
	}

	public String getOldQualifiedName() {
		return oldQualifiedName;
	}

	public void setOldQualifiedName(String oldQualifiedName) {
		this.oldQualifiedName = oldQualifiedName;
	}

	public String getNewQualifiedName() {
		return newQualifiedName;
	}

	public void setNewQualifiedName(String newQualifiedName) {
		this.newQualifiedName = newQualifiedName;
	}

	public boolean isEntryPoint() {
		return isEntryPoint;
	}

	public void setEntryPoint(boolean entryPoint) {
		isEntryPoint = entryPoint;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof FileDiff)) {
			return false;
		}
		FileDiff fileDiff = (FileDiff) o;
		return isEntryPoint == fileDiff.isEntryPoint && changeType == fileDiff.changeType
				&& Objects.equals(oldQualifiedName, fileDiff.oldQualifiedName)
				&& Objects.equals(newQualifiedName, fileDiff.newQualifiedName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(changeType, oldQualifiedName, newQualifiedName, isEntryPoint);
	}

}
