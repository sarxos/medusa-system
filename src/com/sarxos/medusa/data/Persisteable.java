package com.sarxos.medusa.data;

/**
 * Allows implementing object to be persistent.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public interface Persisteable {

	/**
	 * Make implementing object persistent.
	 */
	public void persist();

}
