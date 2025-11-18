package io.github.smartdoc.model.annotation;

/**
 * request part annotation info
 *
 * @author shalousun
 */
public class RequestPartAnnotation {

	/**
	 * annotation name
	 */
	private String annotationName;

	/**
	 * annotation fully name
	 */
	private String annotationFullyName;

	/**
	 * annotation defaultValueProp
	 */
	private String defaultValueProp;

	/**
	 * annotation requiredProp
	 */
	private String requiredProp;

	public static RequestPartAnnotation builder() {
		return new RequestPartAnnotation();
	}

	public String getAnnotationName() {
		return annotationName;
	}

	public RequestPartAnnotation setAnnotationName(String annotationName) {
		this.annotationName = annotationName;
		return this;
	}

	public String getAnnotationFullyName() {
		return annotationFullyName;
	}

	public RequestPartAnnotation setAnnotationFullyName(String annotationFullyName) {
		this.annotationFullyName = annotationFullyName;
		return this;
	}

	public String getDefaultValueProp() {
		return defaultValueProp;
	}

	public RequestPartAnnotation setDefaultValueProp(String defaultValueProp) {
		this.defaultValueProp = defaultValueProp;
		return this;
	}

	public String getRequiredProp() {
		return requiredProp;
	}

	public RequestPartAnnotation setRequiredProp(String requiredProp) {
		this.requiredProp = requiredProp;
		return this;
	}

}
