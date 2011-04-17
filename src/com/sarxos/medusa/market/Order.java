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
}
