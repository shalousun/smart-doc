package io.github.smartdoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

	private final static Set<String> PREFIX_LIST = new HashSet<>();

	static {
		PREFIX_LIST.add("maven");
		PREFIX_LIST.add("asm");
		PREFIX_LIST.add("tomcat");
		PREFIX_LIST.add("jboss");
		PREFIX_LIST.add("undertow");
		PREFIX_LIST.add("jackson");
		PREFIX_LIST.add("micrometer");
		PREFIX_LIST.add("spring-boot-actuator");
		PREFIX_LIST.add("sharding");
		PREFIX_LIST.add("mybatis-spring-boot-starter");
		PREFIX_LIST.add("flexmark");
	}

	public static void main(String[] args) {
		Long start = System.currentTimeMillis();
		// Result list
		List<String> list = new ArrayList<>();
		List<String> list2 = new ArrayList<>();
		// Fixed size 10 thread pool
		ExecutorService exs = Executors.newFixedThreadPool(10);
		final List<Integer> taskList = Arrays.asList(2, 1, 3, 4, 5, 6, 7, 8, 9, 10);
		try {
			CompletableFuture[] cfs = taskList.stream()
				.map(object -> CompletableFuture.supplyAsync(() -> calc(object), exs)
					.thenApply(h -> Integer.toString(h))
					// If you need to get the order of task completion, this code is
					// available
					.whenComplete((v, e) -> {
						System.out
							.println("Task " + v + " completed! result=" + v + "，exception e=" + e + "," + new Date());
						list2.add(v);
					}))
				.toArray(CompletableFuture[]::new);
			CompletableFuture.allOf(cfs).join();
			System.out.println("The order of task completion, result list2=" + list2
					+ "；The order of task submission, result list=" + list + ", time taken="
					+ (System.currentTimeMillis() - start));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			exs.shutdown();
		}
	}

	public static Integer calc(Integer i) {
		try {
			if (i == 1) {
				// Task 1 takes 3 seconds
				Thread.sleep(3000);
			}
			else if (i == 5) {
				// Task 5 takes 5 seconds
				Thread.sleep(5000);
			}
			else {
				// Other tasks take 1 second
				Thread.sleep(1000);
			}
			System.out.println(
					"task thread：" + Thread.currentThread().getName() + " task i=" + i + ", completed！+" + new Date());
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		return i;
	}

	public static boolean ignoreArtifactById(String artifactId) {
		if (PREFIX_LIST.stream().anyMatch(artifactId::startsWith)) {
			return true;
		}
		return false;
	}

}
