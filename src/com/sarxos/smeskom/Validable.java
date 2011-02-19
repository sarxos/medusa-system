package com.sarxos.smeskom;

/**
 * Existence of this interface means that object which implements it will be
 * validated before JAXB marshalling against data correctness.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public interface Validable {

	/**
	 * Validate object against data correctness.
	 * 
	 * @param ctx - validation context
	 * @return True in case of successful validation, false otherwise.
	 */
	public boolean validate(ValidationContext ctx);

}
