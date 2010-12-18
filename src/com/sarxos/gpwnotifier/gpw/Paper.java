package com.sarxos.gpwnotifier.gpw;

import com.sarxos.gpwnotifier.entities.Symbol;


/**
 * Paper to be stored inside wallet.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class Paper implements Cloneable {
	
	/**
	 * Paper symbol (e.g. KGHM, BRE, etc).
	 */
	private Symbol symbol = null;
	
	/**
	 * Current paper quantity (how much items of given paper
	 * I have).
	 */
	private int quantity = 0;
	
	/**
	 * Desired paper quantity (how much items of this paper
	 * I would like to have)
	 */
	private int desiredQuantity = 0;

	
	/**
	 * Paper to be stored inside wallet. Constructor.
	 * 
	 * @param symbol - paper symbol
	 * @param desired - desired quantity
	 */
	public Paper(Symbol symbol, int desired) {
		this(symbol, desired, 0);
	}

	/**
	 * Paper to be stored inside wallet. Constructor.
	 * 
	 * @param symbol - paper symbol
	 * @param desired - desired quantity
	 */
	public Paper(Symbol symbol, int desired, int quantity) {
		super();
		this.setSymbol(symbol);
		this.setDesiredQuantity(desired);
		this.setQuantity(quantity);
	}
	
	/**
	 * @return Paper symbol.
	 */
	public Symbol getSymbol() {
		return symbol;
	}

	/**
	 * Set paper symbol
	 * 
	 * @param symbol - symbol to set
	 */
	public void setSymbol(Symbol symbol) {
		if (symbol == null) {
			throw new IllegalArgumentException("Symbol cannot be null");
		}
		this.symbol = symbol;
	}

	/**
	 * @return Paper quantity.
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * Set paper quantity.
	 * 
	 * @param quantity - new quantity.
	 */
	public void setQuantity(int quantity) {
		if (quantity < 0) {
			throw new IllegalArgumentException("Paper quantity cannot be negative!");
		}
		this.quantity = quantity;
	}

	/** 
	 * @return Desired paper quantity.
	 */
	public int getDesiredQuantity() {
		return desiredQuantity;
	}

	/**
	 * Set desired paper quantity.
	 * 
	 * @param desired
	 */
	public void setDesiredQuantity(int desired) {
		if (desired < 0) {
			throw new IllegalArgumentException("Desired paper quantity must be positive");
		}
		this.desiredQuantity = desired;
	}
	
	@Override
	public Paper clone() {
		try {
			return (Paper) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}
