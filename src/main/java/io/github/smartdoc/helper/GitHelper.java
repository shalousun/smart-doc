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
package io.github.smartdoc.helper;

import com.power.common.util.StringUtil;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A git util build on JGit
 *
 * @author Fio
 */
public class GitHelper {

	/**
	 * Repository
	 */
	private Repository repository;

	/**
	 * Private constructor
	 */
	private GitHelper() {
	}

	/**
	 * Create a new instance
	 * @return GitHelper
	 */
	public static GitHelper create() {
		GitHelper helper = new GitHelper();
		helper.repository = helper.findRepo();
		return helper;
	}

	/**
	 * Get diff between current commit and the commit with commitId
	 * @param commitId commitId
	 * @return {@code List<DiffEntry>}
	 */
	public List<DiffEntry> getDiff(String commitId) {
		if (StringUtil.isEmpty(commitId) || notGitRepo()) {
			return Collections.emptyList();
		}

		try (Git git = new Git(repository)) {
			ObjectId commitObjectId = repository.resolve(commitId);
			RevCommit commit = new RevWalk(repository).parseCommit(commitObjectId);

			ObjectId treeId = commit.getTree().getId();
			CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
			try (ObjectReader reader = repository.newObjectReader()) {
				oldTreeIter.reset(reader, treeId);
			}

			ObjectId currentTreeId = repository.resolve("HEAD^{tree}");
			CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
			try (ObjectReader reader = repository.newObjectReader()) {
				newTreeIter.reset(reader, currentTreeId);
			}

			return git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
		}
		catch (IOException | GitAPIException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get uncommitted changes
	 * @return {@code Set<String> }
	 */
	public Set<String> getUncommitted() {
		if (notGitRepo()) {
			return Collections.emptySet();
		}

		try (Git git = new Git(repository)) {
			return git.status().call().getUncommittedChanges();
		}
		catch (GitAPIException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get untracked files
	 * @return {@code Set<String> }
	 */
	public Set<String> getUntracked() {
		if (notGitRepo()) {
			return Collections.emptySet();
		}

		try (Git git = new Git(repository)) {
			return git.status().call().getUntracked();
		}
		catch (GitAPIException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get latest commit id
	 * @return latest commit id
	 */
	public String getLatestCommitId() {
		if (notGitRepo()) {
			return "";
		}

		try {
			ObjectId objectId = repository.resolve("HEAD");
			// if not exist (the repository is init), return empty string
			if (null == objectId) {
				return "";
			}
			return objectId.getName();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Check git repository, if not exist or io exception, return null
	 * @return Repository
	 */
	private Repository findRepo() {
		try {
			return new FileRepositoryBuilder().readEnvironment().findGitDir().build();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		catch (Exception e) {
			System.err.println("WARN: When detecting git repository, got exception:" + e.getMessage()
					+ " Ignore it if this is not a git repository");
			return null;
		}
	}

	/**
	 * Check if not git repository
	 * @return boolean
	 */
	public boolean notGitRepo() {
		return repository == null;
	}

	/**
	 * Check if git repository
	 * @return boolean
	 */
	public boolean isGitRepo() {
		return !notGitRepo();
	}

	/**
	 * Get work directory
	 * @return work directory
	 */
	public String getWorkDir() {
		return repository.getWorkTree().getAbsolutePath();
	}

}
