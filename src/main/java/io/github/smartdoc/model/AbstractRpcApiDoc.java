package io.github.smartdoc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Abstract API Doc.
 *
 * @author linwumingshi
 * @param <T> the type of method doc
 */
public abstract class AbstractRpcApiDoc<T extends IMethod>
		implements Serializable, IDoc, Comparable<AbstractRpcApiDoc<T>> {

	private static final long serialVersionUID = -3116322721344529338L;

	/**
	 * Order of controller
	 *
	 * @since 1.7+
	 */
	protected int order;

	/**
	 * interface title
	 */
	protected String title;

	/**
	 * interface name
	 */
	protected String name;

	/**
	 * interface short name
	 */
	protected String shortName;

	/**
	 * controller alias handled by md5
	 *
	 * @since 1.7+
	 */
	protected String alias;

	/**
	 * method description
	 */
	protected String desc;

	/**
	 * interface protocol
	 */
	protected String protocol;

	/**
	 * interface author
	 */
	protected String author;

	/**
	 * interface uri
	 */
	protected String uri;

	/**
	 * interface version
	 */
	protected String version;

	/**
	 * List of method doc
	 */
	protected List<T> list;

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public int compareTo(AbstractRpcApiDoc<T> o) {
		if (Objects.nonNull(o.getDesc())) {
			return desc.compareTo(o.getDesc());
		}
		return name.compareTo(o.getName());
	}

	@Override
	public String getDocClass() {
		return this.name;
	}

	@Override
	public List<IMethod> getMethods() {
		if (Objects.isNull(this.list)) {
			return new ArrayList<>();
		}
		return new ArrayList<>(this.list);
	}

}
