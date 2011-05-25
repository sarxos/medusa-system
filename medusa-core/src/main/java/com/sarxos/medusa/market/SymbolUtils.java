package com.sarxos.medusa.market;

/**
 * Symbol utilities.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class SymbolUtils {

	/**
	 * Tell whether or not given future symbol is synthetic.
	 * 
	 * @param symbol - symbol to check
	 * @return true i f symbol is synthetic, false otherwise
	 */
	public static boolean isSynthetic(Symbol symbol) {
		if (symbol == null) {
			throw new IllegalArgumentException("Symobl to check cannot be null");
		}
		Synthetic synth = null;
		try {
			synth = symbol.getClass().getField(symbol.toString()).getAnnotation(Synthetic.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return synth != null;
	}
}
