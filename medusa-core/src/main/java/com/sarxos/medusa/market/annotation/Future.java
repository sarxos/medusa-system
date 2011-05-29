package com.sarxos.medusa.market.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation is used to point which symbol describes future contract.
 * 
 * @author Bartosz Firyn (SarXos)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Future {

	/**
	 * @return Return future type
	 */
	public FutureType value() default FutureType.INDEX;

}
