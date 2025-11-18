/*
 * Copyright (C) 2018-2025 smart-doc
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
package io.github.smartdoc.template;

import com.google.gson.Gson;
import io.github.smartdoc.builder.ProjectDocConfigBuilder;
import io.github.smartdoc.constants.DocGlobalConstants;
import io.github.smartdoc.constants.FrameworkEnum;
import io.github.smartdoc.constants.GrpcMethodTypeEnum;
import io.github.smartdoc.constants.ParamTypeConstants;
import io.github.smartdoc.model.ApiConfig;
import io.github.smartdoc.model.ApiParam;
import io.github.smartdoc.model.ApiSchema;
import io.github.smartdoc.model.SourceCodePath;
import io.github.smartdoc.model.annotation.FrameworkAnnotations;
import io.github.smartdoc.model.grpc.GrpcApiDoc;
import io.github.smartdoc.model.grpc.GrpcJavaMethod;
import io.github.smartdoc.model.grpc.ProtoInfo;
import io.github.smartdoc.model.grpc.proto.EnumDefinition;
import io.github.smartdoc.model.grpc.proto.EnumValue;
import io.github.smartdoc.model.grpc.proto.Message;
import io.github.smartdoc.model.grpc.proto.MessageField;
import io.github.smartdoc.model.grpc.proto.ProtoJson;
import io.github.smartdoc.model.grpc.proto.Service;
import io.github.smartdoc.model.grpc.proto.ServiceMethod;
import io.github.smartdoc.utils.DocUtil;
import com.power.common.util.FileUtil;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.model.JavaClass;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * gRPC Doc build template.
 *
 * @author linwumingshi
 * @since 3.0.7
 */
public class GRpcDocBuildTemplate implements IDocBuildTemplate<GrpcApiDoc>, IJavadocDocTemplate<GrpcJavaMethod> {

	/**
	 * Logger for the class.
	 */
	private final static Logger log = Logger.getLogger(GRpcDocBuildTemplate.class.getName());

	/**
	 * api index
	 */
	private final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(1);

	/**
	 * message map
	 */
	private Map<String, Message> messageMap;

	/**
	 * enum value map
	 */
	private Map<String, List<EnumValue>> enumValueMap;

	@Override
	public boolean supportsFramework(String framework) {
		return FrameworkEnum.GRPC.getFramework().equalsIgnoreCase(framework);
	}

	@Override
	public boolean addMethodModifiers() {
		return false;
	}

	@Override
	public GrpcJavaMethod createEmptyJavadocJavaMethod() {
		return new GrpcJavaMethod();
	}

	@Override
	public boolean ignoreReturnObject(String typeName, List<String> ignoreParams) {
		return false;
	}

	@Override
	public ApiSchema<GrpcApiDoc> renderApi(ProjectDocConfigBuilder projectBuilder,
			Collection<JavaClass> candidateClasses) {
		ApiConfig apiConfig = projectBuilder.getApiConfig();
		List<GrpcApiDoc> apiDocList = new ArrayList<>();

		// proto to json
		ProtoInfo protoInfo = this.protoToJson(apiConfig);

		if (Objects.isNull(protoInfo) || Objects.isNull(protoInfo.getTargetJsonFilePath())) {
			return new ApiSchema<>();
		}

		// json to javaClass
		String fileContent = FileUtil.getFileContent(protoInfo.getTargetJsonFilePath());
		ProtoJson protoJson = new Gson().fromJson(fileContent, ProtoJson.class);

		// get services
		List<Service> services = protoJson.getFiles()
			.stream()
			.filter(file -> file.isHasServices() && !file.getServices().isEmpty())
			.flatMap(file -> file.getServices().stream())
			.collect(Collectors.toList());
		// get message
		this.messageMap = protoJson.getFiles()
			.stream()
			.flatMap(file -> file.getMessages().stream())
			.collect(Collectors.toMap(Message::getFullName, Function.identity(), (v1, v2) -> v2));
		// get enums
		this.enumValueMap = protoJson.getFiles()
			.stream()
			.filter(i -> i.isHasEnums() && !i.getEnums().isEmpty())
			.flatMap(file -> file.getEnums().stream())
			.collect(Collectors.toMap(EnumDefinition::getName, EnumDefinition::getValues, (v1, v2) -> v2));
		for (Service service : services) {
			GrpcApiDoc gRpcApiDoc = this.getRpcApiDocByService(service);
			apiDocList.add(gRpcApiDoc);
		}

		ApiSchema<GrpcApiDoc> apiSchema = new ApiSchema<>();
		apiSchema.setApiDatas(apiDocList);
		return apiSchema;
	}

	@Override
	public FrameworkAnnotations registeredAnnotations() {
		return null;
	}

	@Override
	public boolean isEntryPoint(JavaClass javaClass, FrameworkAnnotations frameworkAnnotations) {
		return false;
	}

	/**
	 * get rpc api doc by service.
	 * @param service service
	 * @return rpc api doc
	 */
	private GrpcApiDoc getRpcApiDocByService(Service service) {
		GrpcApiDoc gRpcApiDoc = new GrpcApiDoc();
		gRpcApiDoc.setOrder(ATOMIC_INTEGER.getAndIncrement());
		gRpcApiDoc.setTitle(service.getDescription());
		gRpcApiDoc.setName(service.getFullName());
		gRpcApiDoc.setShortName(service.getName());
		gRpcApiDoc.setDesc(service.getDescription());
		gRpcApiDoc.setProtocol(FrameworkEnum.GRPC.getFramework());
		gRpcApiDoc.setUri("gRpc://" + service.getFullName());
		gRpcApiDoc.setVersion(DocGlobalConstants.DEFAULT_VERSION);

		List<GrpcJavaMethod> rpcJavaMethods = this.getRpcJavaMethods(service);
		gRpcApiDoc.setList(rpcJavaMethods);
		return gRpcApiDoc;
	}

	/**
	 * get rpc java methods.
	 * @param service service
	 * @return rpc java methods
	 */
	private List<GrpcJavaMethod> getRpcJavaMethods(Service service) {
		List<GrpcJavaMethod> gRpcJavaMethods = new ArrayList<>();
		int methodOrder = 1;
		for (ServiceMethod method : service.getMethods()) {
			GrpcJavaMethod rpcJavaMethod = new GrpcJavaMethod();
			// Determine the gRPC style based on requestStreaming and responseStreaming
			rpcJavaMethod.setMethodType(
					GrpcMethodTypeEnum.fromStreaming(method.isRequestStreaming(), method.isResponseStreaming()));
			rpcJavaMethod.setOrder(methodOrder++);
			rpcJavaMethod.setName(method.getName());
			rpcJavaMethod.setDesc(method.getDescription());
			rpcJavaMethod.setDetail(method.getDescription());
			rpcJavaMethod.setReturnClassInfo(method.getResponseFullType());
			rpcJavaMethod.setEscapeMethodDefinition(
					method.getResponseType() + "  " + method.getName() + "(" + method.getRequestType() + ")");
			// Process request parameters
			Message requestMessage = this.messageMap.get(method.getRequestFullType());
			if (requestMessage != null) {
				List<ApiParam> requestParams = this.processMessage(requestMessage, 0);
				rpcJavaMethod.setRequestParams(requestParams);
			}

			// Process response parameters
			Message responseMessage = this.messageMap.get(method.getResponseFullType());
			if (responseMessage != null) {
				List<ApiParam> responseParams = this.processMessage(responseMessage, 0);
				rpcJavaMethod.setResponseParams(responseParams);
			}

			gRpcJavaMethods.add(rpcJavaMethod);
		}
		return gRpcJavaMethods;
	}

	/**
	 * proto to json.
	 * @param apiConfig api config
	 */
	private ProtoInfo protoToJson(ApiConfig apiConfig) {
		Set<String> protoFiles = this.findProtoFiles(apiConfig.getSourceCodePaths());
		if (protoFiles.isEmpty()) {
			return null;
		}

		ProtoInfo protoInfo = ProtoInfo.build();

		this.createDirectories(apiConfig.getOutPath(), protoInfo.getTargetJsonDirectoryPath());
		this.copyResourceFiles(protoInfo);
		this.executeProtocCommands(protoFiles, protoInfo);

		return protoInfo;
	}

	/**
	 * Copy the resource files to the target directory.
	 * @param protoInfo The proto info.
	 */
	private void copyResourceFiles(ProtoInfo protoInfo) {
		this.copyResourceFile(protoInfo.getSourceProtocPath(), protoInfo.getProtocPath());
		this.copyResourceFile(protoInfo.getSourceProtocGenDocPath(), protoInfo.getProtocGenDocPath());
	}

	/**
	 * Finds the .proto files in the specified source code paths.
	 * @param sourceCodePaths The list of source code paths.
	 * @return A set of .proto file paths.
	 */
	private Set<String> findProtoFiles(List<SourceCodePath> sourceCodePaths) {
		return sourceCodePaths.stream()
			.map(SourceCodePath::getPath)
			.flatMap(path -> this.findProtoFilesRecursively(path).stream())
			.collect(Collectors.toSet());
	}

	/**
	 * Recursively searches for .proto files within a given directory.
	 * <p>
	 * This method traverses the specified directory and its subdirectories, looking for
	 * files that end with the .proto extension, and adds their absolute paths to a list.
	 * It is an application of a depth-first search algorithm to recursively search
	 * through the filesystem.
	 * @param directoryPath The directory path to search. This is the starting point for
	 * the recursion.
	 * @return A set of .proto file paths.
	 */
	private Set<String> findProtoFilesRecursively(String directoryPath) {
		try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
			return paths.filter(Files::isRegularFile)
				.map(Path::toString)
				.filter(string -> string.endsWith(DocGlobalConstants.PROTO_FILE_SUFFIX))
				.collect(Collectors.toSet());
		}
		catch (IOException e) {
			log.warning("Error walking directory: " + directoryPath + ", " + e.getMessage());
			return Collections.emptySet();
		}
	}

	/**
	 * Create directories for the specified output path and target JSON path.
	 * @param outPath The output path.
	 * @param targetJsonPath The target JSON path.
	 */
	private void createDirectories(String outPath, String targetJsonPath) {
		FileUtil.mkdirs(outPath);
		FileUtil.mkdirs(targetJsonPath);
	}

	/**
	 * Copy a resource file from the classpath to the specified target path.
	 * @param resourcePath The path of the resource file to be copied.
	 * @param targetPath The target path where the resource file will be copied.
	 */
	private void copyResourceFile(String resourcePath, String targetPath) {
		try (InputStream resourceAsStream = GRpcDocBuildTemplate.class.getResourceAsStream(resourcePath)) {
			if (Objects.isNull(resourceAsStream)) {
				return;
			}
			File targetFile = new File(targetPath);
			FileUtil.mkdirs(targetFile.getParent());
			FileUtil.copyInputStreamToFile(resourceAsStream, new File(targetPath), StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e) {
			log.warning("Error executing command error:" + e.getMessage());
		}
	}

	/**
	 * Execute the protoc command to generate the JSON files.
	 * @param protoFiles The set of .proto files to be processed.
	 * @param protoInfo The proto info.
	 */
	public void executeProtocCommands(Set<String> protoFiles, ProtoInfo protoInfo) {
		List<String> command = this.buildProtocCommand(protoFiles, protoInfo);
		if (command.isEmpty()) {
			return;
		}
		this.executeCommand(command, protoInfo);
	}

	/**
	 * Execute the protoc command to generate the JSON files.
	 * @param command The command to be executed.
	 * @param protoInfo The proto info.
	 */
	private void executeCommand(List<String> command, ProtoInfo protoInfo) {
		try {
			if (!protoInfo.isWinOs()) {
				// Grant execute permissions
				Files.setPosixFilePermissions(Paths.get(protoInfo.getProtocPath()),
						PosixFilePermissions.fromString("rwxr-xr-x"));
				Files.setPosixFilePermissions(Paths.get(protoInfo.getProtocGenDocPath()),
						PosixFilePermissions.fromString("rwxr-xr-x"));
			}
		}
		catch (IOException e) {
			log.warning("Failed to grant execute permission: " + e.getMessage());
			return;
		}

		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.redirectErrorStream(true);

		try {
			Process process = processBuilder.start();

			StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(),
					line -> log.warning("[protoc] " + line));
			Thread outputThread = new Thread(outputGobbler);
			outputThread.start();

			// wait for the process to finish
			int exitCode = process.waitFor();
			// wait for the output thread to finish
			outputThread.join();

			if (exitCode != 0) {
				log.warning("protoc command failed with exit code: " + exitCode);
			}
		}
		catch (IOException | InterruptedException e) {
			log.warning("Error executing command: " + e.getMessage());
		}
	}

	/**
	 * Build the protoc command to generate the JSON files.
	 * @param protoFiles The set of .proto files to be processed.
	 * @param protoInfo The proto info.
	 * @return The list of command arguments.
	 */
	private List<String> buildProtocCommand(Set<String> protoFiles, ProtoInfo protoInfo) {
		List<String> command = new ArrayList<>();

		// Add the protoc executable (using absolute path)
		command.add(Paths.get(protoInfo.getProtocPath()).toAbsolutePath().toString());

		// Find the common root directory for all .proto files
		String commonRootPath = this.findCommonRootDirectory(protoFiles);
		if (StringUtil.isEmpty(commonRootPath)) {
			log.warning("No common root directory found for proto files.");
			return Collections.emptyList();
		}

		// Set --proto_path to the common root directory
		command.add("--proto_path=" + commonRootPath);

		// Set output options
		command.add("--doc_out=" + Paths.get(protoInfo.getTargetJsonDirectoryPath()).toAbsolutePath());
		command.add("--doc_opt=json," + protoInfo.getJsonName());

		// Set the plugin path (using absolute path)
		command.add("--plugin=protoc-gen-doc=" + Paths.get(protoInfo.getProtocGenDocPath()).toAbsolutePath());

		// Add the absolute paths of the .proto files
		for (String filePath : protoFiles) {
			Path file = Paths.get(filePath).toAbsolutePath().normalize();
			command.add(file.toString());
		}

		return command;
	}

	/**
	 * Find the deepest common ancestor directory for the given set of files.
	 * @param files The set of .proto file paths.
	 * @return The common root directory path, or null if the set is empty.
	 */
	private String findCommonRootDirectory(Set<String> files) {
		if (files.isEmpty()) {
			return null;
		}

		// Convert all paths to Path objects
		List<Path> filePaths = files.stream()
			.map(Paths::get)
			.map(Path::toAbsolutePath)
			.map(Path::normalize)
			.collect(Collectors.toList());

		// Use the first path as the initial common root
		Path commonRoot = filePaths.get(0);

		// Compare with all other paths, gradually shorten commonRoot until it is a prefix
		// of all paths
		for (Path path : filePaths) {
			while (!path.startsWith(commonRoot)) {
				commonRoot = commonRoot.getParent();
				if (commonRoot == null) {
					// If we've gone up to null, there's no common parent, return the root
					// of the path
					commonRoot = path.getRoot();
					break;
				}
			}
		}

		return commonRoot.toString();
	}

	/**
	 * Process a message to extract ApiParams recursively.
	 * @param message message to process
	 * @param level level
	 * @return list of ApiParams
	 */
	private List<ApiParam> processMessage(Message message, int level) {
		if (message == null) {
			return Collections.emptyList();
		}

		List<ApiParam> apiParams = new ArrayList<>();

		for (MessageField field : message.getFields()) {
			List<EnumValue> enumValues = this.enumValueMap.get(field.getType());

			String fieldType = field.isMap() ? ParamTypeConstants.PARAM_TYPE_MAP
					: enumValues != null ? ParamTypeConstants.PARAM_TYPE_ENUM + "[" + field.getType() + "]"
							: "repeated".equals(field.getLabel()) ? "array[" + field.getType() + "]" : field.getType();

			String fieldDesc = field.getDescription();
			if (enumValues != null) {
				fieldDesc += "<br/>" + enumValues.stream().map(EnumValue::getName).collect(Collectors.joining("<br/>"));
			}
			// field name; if level is 0, it's the root message; otherwise, it's a nested
			// message
			String fieldName = level <= 0 ? field.getName() : DocUtil.getIndentByLevel(level) + field.getName();
			ApiParam apiParam = ApiParam.of()
				.setField(fieldName)
				.setType(fieldType)
				.setDesc(fieldDesc)
				.setRequired(!"optional".equals(field.getLabel()) || "required".equals(field.getLabel())
						|| "".equals(field.getLabel()))
				.setVersion(DocGlobalConstants.DEFAULT_VERSION)
				.setValue(field.getDefaultValue())
				.setClassName(field.getFullType())
				.setId(level)
				.setPid(level - 1);

			apiParams.add(apiParam);
			// Handle nested messages recursively
			if (field.getFullType().contains(".")) {
				Message nestedMessage = this.messageMap.get(field.getFullType());
				if (nestedMessage != null) {
					List<ApiParam> children = this.processMessage(nestedMessage, level + 1);
					apiParams.addAll(children);
				}
			}
		}
		return apiParams;
	}

	/**
	 * The StreamGobbler class is a private static inner class implementing the Runnable
	 * interface. Its purpose is to consume data from an InputStream and process this data
	 * using a Consumer. This design is typically used for handling stream data in another
	 * thread, such as log output or error messages.
	 *
	 * @author linwumingshi
	 */
	private static class StreamGobbler implements Runnable {

		private final InputStream inputStream;

		private final Consumer<String> consumer;

		public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
			this.inputStream = inputStream;
			this.consumer = consumer;
		}

		@Override
		public void run() {
			new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
		}

	}

}
