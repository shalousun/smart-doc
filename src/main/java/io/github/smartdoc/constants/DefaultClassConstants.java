package io.github.smartdoc.constants;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * default class constants
 *
 * @author linwumingshi
 * @since 3.0.6
 */
public interface DefaultClassConstants {

	/**
	 * jakarta.validation.groups.Default
	 */
	String JAKARTA_VALIDATION_GROUPS_DEFAULT = "jakarta.validation.groups.Default";

	/**
	 * javax.validation.groups.Default
	 */
	String JAVAX_VALIDATION_GROUPS_DEFAULT = "javax.validation.groups.Default";

	/**
	 * default class set
	 */
	Set<String> DEFAULT_CLASSES = Stream.of(JAKARTA_VALIDATION_GROUPS_DEFAULT, JAVAX_VALIDATION_GROUPS_DEFAULT)
		.collect(Collectors.toSet());

}
