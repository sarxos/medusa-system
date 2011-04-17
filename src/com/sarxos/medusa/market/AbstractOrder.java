package com.sarxos.medusa.market;

/**
 * Abstract order class.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public abstract class AbstractOrder implements Order {

	/**
	 * Paper to buy/sell.
	 */
	private Paper paper = null;

	/**
	 * Order direction - buy/sell.
	 */
	private OrderDirection direction = null;

	/**
	 * Order type - limit by default.
	 */
	private OrderType type = OrderType.LIMIT;

	/**
	 * @param paper - paper to buy/sell
	 * @param direction - order direction
	 */
	public AbstractOrder(Paper paper, OrderDirection direction) {
		super();
		this.paper = paper;
		this.direction = direction;
	}

	@Override
	public Paper getPaper() {
		return paper;
	}

	@Override
	public OrderDirection getDirection() {
		return direction;
	}

	@Override
	public OrderType getType() {
		return type;
	}

	@Override
	public void setPaper(Paper paper) {
		if (paper == null) {
			throw new IllegalArgumentException("Paper to order cannot be null");
		}
		this.paper = paper;
	}

	@Override
	public void setDirection(OrderDirection direction) {
		if (direction == null) {
			throw new IllegalArgumentException("Order direction cannot be null");
		}
		this.direction = direction;
	}

	@Override
	public void setType(OrderType type) {
		if (type == null) {
			throw new IllegalArgumentException("Order type cannot be null");
		}
		this.type = type;
	}
}
