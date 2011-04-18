package com.sarxos.medusa.market;

/**
 * Encapsulation of the order abstraction.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public interface Order {

	/**
	 * @return Return paper for order
	 */
	public Paper getPaper();

	/**
	 * Set new paper to order.
	 * 
	 * @param paper - new paper to order
	 */
	public void setPaper(Paper paper);

	/**
	 * @return Return order direction
	 */
	public OrderDirection getDirection();

	/**
	 * Set new order direction.
	 * 
	 * @param direction - new order direction
	 */
	public void setDirection(OrderDirection direction);

	/**
	 * @return Return order type
	 */
	public OrderType getType();

	/**
	 * Set order type.
	 * 
	 * @param type- new order type to set
	 */
	public void setType(OrderType type);
	
	/**
	 * @return Return activation limit or 0 if there is no activation limit
	 */
	public double getActivationLimit();
	
	/**
	 * Set new activation limit
	 * 
	 * @param activation - new activation limit to set, 0 means no activation
	 */
	public void setActivationLimit(double activation);
	
	/**
	 * @return Return order validity
	 */
	public OrderValidity getValidityType();
	
	/**
	 * Set new order validity type.
	 * 
	 * @param validity - new validity type
	 */
	public void setValidityType(OrderValidity validity);
	
	/**
	 * @return Return price limit for the order.
	 */
	public double getLimit();
	
	/**
	 * Set new price limit for order.
	 * 
	 * @param price - price limit
	 */
	public void setLimit(double price);
	
	/**
	 * @return true if order is already deployed in brokerage, false otherwise
	 */
	public boolean isDeployed();
}
