package com.sarxos.gpwnotifier.comm.smeskom.v22;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;


/**
 * SmesX request.
 * 
 * @author Bartosz Firyn (SarXos)
 * @see http://www.smeskom.pl/documents/SmesX_v2.2.pdf
 */
@XStreamAlias("request")
public class SmesXRequest {

	/**
	 * Protocol version.
	 */
	@XStreamAlias("version")
	@XStreamAsAttribute
	public static final String VERSION = "2.2";
	
	/**
	 * Protocol name.
	 */
	@XStreamAlias("protocol")
	@XStreamAsAttribute
	public static final String PROTOCOL = "SmesX";
	
	/**
	 * SmesX user's name.
	 */
	@XStreamAlias("user")
	@XStreamAsAttribute
	private String user = null;

	/**
	 * SmesX user's password.
	 */
	@XStreamAlias("password")
	@XStreamAsAttribute
	private String password = null;

	
	/**
	 * Create SmesX request.
	 */
	public SmesXRequest() {
	}
	
	/**
	 * @return Return SmesX user's name.
	 */
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
