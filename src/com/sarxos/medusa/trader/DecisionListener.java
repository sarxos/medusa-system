package com.sarxos.medusa.trader;

import javax.xml.bind.annotation.XmlTransient;


/**
 * Decision listener interface.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public interface DecisionListener {

	/**
	 * Notified after decision change.
	 * 
	 * @param event - decision event
	 */
	@XmlTransient
	public void decisionChange(DecisionEvent event);
	
}
