package com.sarxos.smeskom.v22;

import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


@XmlRootElement(name = "entity")
public abstract class SmesXEntity {

	/**
	 * Default used date format.
	 */
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
	
	/**
	 * Protocol version.
	 */
	public static final String VERSION = "2.2";

	/**
	 * Protocol name.
	 */
	public static final String PROTOCOL = "SmesX";
	
	/**
	 * This field is used only by the XML marshaller.
	 */
	@XmlAttribute(name = "version")
	private final String version = VERSION;
	
	/**
	 * This field is used only by the XML marshaller.
	 */
	@XmlAttribute(name = "protocol")
	private final String protocol = PROTOCOL; 

	
	public SmesXEntity() {
	}
	
	@XmlTransient
	public String getVersion() {
		return this.version;
	}

	@XmlTransient
	public String getProtocol() {
		return this.protocol;
	}	
}
