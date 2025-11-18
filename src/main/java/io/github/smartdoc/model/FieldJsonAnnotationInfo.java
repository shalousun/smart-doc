package io.github.smartdoc.model;

import java.io.Serializable;

/**
 * FieldJsonAnnotationInfo
 *
 * @author linwumingshi
 * @since 3.0.9
 */
public class FieldJsonAnnotationInfo implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 9003861567366363140L;

	/**
	 * the param type from @JsonFormat
	 */
	private String fieldJsonFormatType;

	/**
	 * the param value from @JsonFormat
	 */
	private String fieldJsonFormatValue;

	/**
	 * has Annotation @JsonSerialize And using ToStringSerializer
	 */
	private Boolean toStringSerializer;

	/**
	 * is required
	 */
	private Boolean strRequired;

	/**
	 * field name
	 */
	private String fieldName;

	/**
	 * is ignored
	 */
	private Boolean isIgnore;

	public String getFieldJsonFormatType() {
		return fieldJsonFormatType;
	}

	public FieldJsonAnnotationInfo setFieldJsonFormatType(String fieldJsonFormatType) {
		this.fieldJsonFormatType = fieldJsonFormatType;
		return this;
	}

	public String getFieldJsonFormatValue() {
		return fieldJsonFormatValue;
	}

	public FieldJsonAnnotationInfo setFieldJsonFormatValue(String fieldJsonFormatValue) {
		this.fieldJsonFormatValue = fieldJsonFormatValue;
		return this;
	}

	public Boolean getToStringSerializer() {
		return toStringSerializer;
	}

	public FieldJsonAnnotationInfo setToStringSerializer(Boolean toStringSerializer) {
		this.toStringSerializer = toStringSerializer;
		return this;
	}

	public Boolean getStrRequired() {
		return strRequired;
	}

	public FieldJsonAnnotationInfo setStrRequired(Boolean strRequired) {
		this.strRequired = strRequired;
		return this;
	}

	public String getFieldName() {
		return fieldName;
	}

	public FieldJsonAnnotationInfo setFieldName(String fieldName) {
		this.fieldName = fieldName;
		return this;
	}

	public Boolean getIgnore() {
		return isIgnore;
	}

	public FieldJsonAnnotationInfo setIgnore(Boolean ignore) {
		isIgnore = ignore;
		return this;
	}

}
