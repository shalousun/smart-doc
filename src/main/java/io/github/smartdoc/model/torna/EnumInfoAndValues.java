package io.github.smartdoc.model.torna;

import io.github.smartdoc.model.ApiParam;

import java.io.Serializable;
import java.util.List;

/**
 * Torna Enum Info And values
 *
 * @author linwumingshi
 * @since 3.0.9
 */
public class EnumInfoAndValues implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6164712273272981975L;

	/**
	 * torna enumInfo
	 */
	private EnumInfo enumInfo;

	/**
	 * ApiParam enumValue<br>
	 * Use in openapi api document
	 * @see ApiParam#getEnumValues()
	 */
	private List<String> enumValues;

	/**
	 * ApiParam value
	 * @see ApiParam#getValue()
	 */
	private Object value;

	/**
	 * ApiParam value default enum; when type is not enum type, will set this type to
	 * {@link ApiParam#setType(String)}
	 */
	private String type;

	public static EnumInfoAndValues builder() {
		return new EnumInfoAndValues();
	}

	public EnumInfo getEnumInfo() {
		return enumInfo;
	}

	public EnumInfoAndValues setEnumInfo(EnumInfo enumInfo) {
		this.enumInfo = enumInfo;
		return this;
	}

	public List<String> getEnumValues() {
		return enumValues;
	}

	public EnumInfoAndValues setEnumValues(List<String> enumValues) {
		this.enumValues = enumValues;
		return this;
	}

	public Object getValue() {
		return value;
	}

	public EnumInfoAndValues setValue(Object value) {
		this.value = value;
		return this;
	}

	public String getType() {
		return type;
	}

	public EnumInfoAndValues setType(String type) {
		this.type = type;
		return this;
	}

}
