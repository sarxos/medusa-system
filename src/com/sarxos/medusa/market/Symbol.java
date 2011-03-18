package com.sarxos.medusa.market;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


public enum Symbol {

	/**
	 * For tests.
	 */
	QQQ("QQQINC"),

	// /**
	// * PL
	// */
	WIG20("WIG20"),
	//
	// /**
	// * UK
	// */
	// FTSE100,
	//
	// /**
	// * DE
	// */
	// DAX,
	//
	// /**
	// * Wegry
	// */
	// BUX,
	//
	// /**
	// * US
	// */
	// NASDAQ,
	// DJI,
	// SP,
	//
	// /**
	// * FR
	// */
	// CAC,
	//
	// /**
	// * JP
	// */
	// NIKKEI,
	//
	// // quotes

	/**
	 * KGHM
	 */
	KGH("KGHM"),
	BRE("BRE"),
	BZW("BZWBK"),
	CPS("CYFRPLSAT"),
	KER("KERNEL"),
	MCI("MCI"),
	PBG("PBG"),
	/**
	 * Astarta.
	 */
	AST("ASTARTA"),
	IPE("IPOPEMA"),
	GTN("GETIN"),
	EUR("EUROCASH"),
	PEO("PEKAO"),
	/**
	 * AmRest Holdings SE.
	 */
	EAT("AMREST");

	/**
	 * Symbol second name (e.g. KGH == KGHM, BZW == BZWBK)
	 */
	private String name = null;

	/**
	 * Static instance for symbols enum set.
	 */
	private static AtomicReference<EnumSet<Symbol>> set = new AtomicReference<EnumSet<Symbol>>();

	/**
	 * Name-Symbol mapping.
	 */
	private static Map<String, Symbol> mapping = new HashMap<String, Symbol>();

	private Symbol(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * This method will find Symbol enum constant on the base of given Symbol
	 * name.
	 * 
	 * @param name - Symbol name to search
	 * @return Symbol or null if symbol for given name has not been found
	 */
	public static final Symbol valueOfName(String name) {

		if (name == null) {
			throw new IllegalArgumentException("Symbol name cannot be null");
		}

		if (set.get() == null) {
			set.compareAndSet(null, EnumSet.allOf(Symbol.class));
		}

		EnumSet<Symbol> enums = set.get();
		Symbol sym = null;

		if (enums.size() != mapping.size()) {
			for (Symbol s : enums) {
				if (name.equals(s.getName())) {
					sym = s;
				}
				mapping.put(s.getName(), s);
			}
		} else {
			sym = mapping.get(name);
		}

		if (sym != null) {
			return sym;
		}

		return null;
	}
}
