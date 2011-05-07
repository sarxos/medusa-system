package com.sarxos.medusa.data.persistence;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation will make given field persistent.
 * 
 * @author Bartosz Firyn (SarXos)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE, ElementType.CONSTRUCTOR })
@Documented
public @interface Persistent {

	/**
	 * @return Persistent field name.
	 */
	String value() default "#name";

}
