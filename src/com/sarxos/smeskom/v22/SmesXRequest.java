package com.sarxos.smeskom.v22;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


/**
 * SmesX request.
 * 
 * @author Bartosz Firyn (SarXos)
 * @see http://www.smeskom.pl/documents/SmesX_v2.2.pdf
 */
@XmlRootElement(name = "request")
public class SmesXRequest extends SmesXEntity {

	/**
	 * SmesX user's name.
	 */
	@XmlAttribute(name = "user")
	private String user = null;

	/**
	 * SmesX user's password.
	 */
	@XmlAttribute(name = "password")
	private String password = null;

	/**
	 * Various SmesX operations.
	 */
	@XmlElementRef
	private SmesXOperation operation = null;

	/**
	 * Create SmesX request.
	 */
	public SmesXRequest() {
	}

	/**
	 * @return Return SmesX user's name.
	 */
	@XmlTransient
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@XmlTransient
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@XmlTransient
	public SmesXOperation getOperation() {
		return operation;
	}

	public void setOperation(SmesXOperation operation) {
		this.operation = operation;
	}
}
