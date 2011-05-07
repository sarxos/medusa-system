package com.sarxos.medusa.trader;

import java.util.EventObject;

import com.sarxos.medusa.market.Position;


/**
 * Position change vent.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class PositionEvent extends EventObject {

	private static final long serialVersionUID = -1895535057212550708L;

	/**
	 * Current position.
	 */
	private Position newPosition = null;

	/**
	 * Previous position.
	 */
	private Position oldPosition = null;

	/**
	 * Create position event.
	 * 
	 * @param dm - decision maker (source object)
	 * @param oldPos - old position
	 * @param newPos - new position
	 */
	public PositionEvent(DecisionMaker dm, Position oldPos, Position newPos) {
		super(dm);
		if (newPos == null) {
			throw new IllegalArgumentException("New position cannot be null");
		}
		this.oldPosition = oldPos;
		this.newPosition = newPos;
	}

	/**
	 * @return Current (new) position
	 */
	public Position getNewPosition() {
		return newPosition;
	}

	/**
	 * @return Previous (old) position (can be null in some cases, e.g. init)
	 */
	public Position getOldPosition() {
		return oldPosition;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(getClass().getSimpleName());
		sb.append("[ ");
		sb.append(getOldPosition());
		sb.append(" -> ");
		sb.append(getNewPosition());
		sb.append(" ]");
		return sb.toString();
	}
}
