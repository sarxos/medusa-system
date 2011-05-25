package com.sarxos.medusa.market;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation is used to point which future symbol from the {@link Symbol}
 * enumeration is synthetic.
 * 
 * @author Bartosz Firyn (SarXos)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Synthetic {

}
