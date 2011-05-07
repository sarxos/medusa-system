package com.sarxos.medusa.market;

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
	 * Current paper quantity (how much items of given paper I have).
	 */
	private int quantity = 0;

	/**
	 * Desired paper quantity (how much items of this paper I would like to
	 * have)
	 */
	private int desiredQuantity = 0;

	/**
	 * Securities group.
	 */
	private SecuritiesGroup group = null;

	// TODO read from file
	private transient String[] indexes = new String[] { "FW20", "FW40" };
	private transient String[] quotes = new String[] { "FACP", "FKGH", "FPEO", "FPGE", "FPGN", "FPKN", "FPKO", "FPZU", "FTPS" };
	private transient String[] currency = new String[] { "FACP", "FEUR", "FUSD" };
	private transient String[] options = new String[] { "OW20" };
	private transient String[] miniwig = new String[] { "MW20" };
	private transient String[] etf = new String[] { "ETFW" };
	private transient String[] treasury = new String[] { "DS20", "DZ08" }; // and
																			// more

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
		this.initGroup(symbol);
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

	protected void initGroup(Symbol symbol) {

		String sm = symbol.toString();

		if (inGroup(sm, indexes)) {
			group = SecuritiesGroup.FUTURES_INDEXES;
		} else if (inGroup(sm, quotes)) {
			group = SecuritiesGroup.FUTURES_QUOTES;
		} else if (inGroup(sm, currency)) {
			group = SecuritiesGroup.FUTURES_CURRENCY;
		} else if (inGroup(sm, options)) {
			group = SecuritiesGroup.OPTIONS_INDEXES;
		} else if (inGroup(sm, miniwig)) {
			group = SecuritiesGroup.OPTIONS_MINIWIG;
		} else if (inGroup(sm, etf)) {
			group = SecuritiesGroup.ETF_CERTS;
		} else if (inGroup(sm, treasury)) {
			group = SecuritiesGroup.TREASURY_BONDS;
		} else {
			// TODO change this to correct group
			group = SecuritiesGroup.WIG20_GROUP_1;
		}
	}

	/**
	 * @param sm - symbol string to check
	 * @param arr - array of prefixes
	 * @return true / false
	 */
	protected boolean inGroup(String sm, String[] arr) {
		boolean ok = false;
		for (int i = 0; i < arr.length; i++) {
			ok = sm.startsWith(arr[i]);
			if (ok) {
				break;
			}
		}
		return ok;
	}

	/**
	 * @return Securities group.
	 */
	public SecuritiesGroup getGroup() {
		return group;
	}

	@Override
	public String toString() {
		return "Paper[" + getSymbol() + ":" + getQuantity() + "/" + getDesiredQuantity() + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Paper) {
			Paper p = (Paper) obj;
			boolean equals = true;
			equals = equals && p.getDesiredQuantity() == getDesiredQuantity();
			equals = equals && p.getQuantity() == getQuantity();
			equals = equals && p.getSymbol() == getSymbol();
			return equals;
		}
		return false;
	}
}
