package com.sarxos.medusa.market;

/**
 * Paper to be stored inside wallet. It wraps symbol and provide some useful
 * methods.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class Paper implements Cloneable {

	/**
	 * Paper symbol (e.g. KGHM, BRE, etc).
	 */
	private Symbol symbol = null;

	/**
	 * Securities group.
	 */
	private SecuritiesGroup group = null;

	// TODO read from file
	private transient String[] fut_indexes = new String[] { "FW20", "FW40" };
	private transient String[] fut_quotes = new String[] { "FACP", "FKGH", "FPEO", "FPGE", "FPGN", "FPKN", "FPKO", "FPZU", "FTPS", "FQQQ" };
	private transient String[] fut_currency = new String[] { "FACP", "FEUR", "FUSD" };
	private transient String[] options = new String[] { "OW20" };
	private transient String[] miniwig = new String[] { "MW20" };
	private transient String[] etf = new String[] { "ETFW" };
	private transient String[] treasury = new String[] { "DS20", "DZ08" };

	// and more

	/**
	 * Paper to be stored inside wallet. Constructor.
	 * 
	 * @param symbol - paper symbol
	 */
	public Paper(Symbol symbol) {
		super();
		this.setSymbol(symbol);
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
	private void setSymbol(Symbol symbol) {
		if (symbol == null) {
			throw new IllegalArgumentException("Symbol cannot be null");
		}
		this.symbol = symbol;
		this.initGroup(symbol);
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

		if (inGroup(sm, fut_indexes)) {
			group = SecuritiesGroup.FUTURES_INDEXES;
		} else if (inGroup(sm, fut_quotes)) {
			group = SecuritiesGroup.FUTURES_QUOTES;
		} else if (inGroup(sm, fut_currency)) {
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
		return "Paper[" + getSymbol() + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Paper) {
			Paper p = (Paper) obj;
			boolean equals = true;
			equals = equals && p.getSymbol() == getSymbol();
			equals = equals && p.getGroup() == getGroup();
			return equals;
		}
		return false;
	}
}
