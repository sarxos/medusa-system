package com.sarxos.medusa.market;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


public enum Symbol {

	/**
	 * Synthetic paper for WIG20 futures.
	 */
	@Synthetic
	FW20("FW20"),
	/**
	 * Synthetic symbol for test futures.
	 */
	@Synthetic
	FQQQ("FQQQ"),
	FQQQH11("FQQQ"),
	FQQQM11("FQQQ"),
	FQQQU11("FQQQ"),
	FQQQZ11("FQQQ"),

	/**
	 * WIG20 Future, June 2011
	 */
	FW20M11("FW20M11"),
	FW20H11("FW20H11"),

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
	 * Astarta
	 */
	AST("ASTARTA"),
	IPE("IPOPEMA"),
	GTN("GETIN"),
	EUR("EUROCASH"),
	PEO("PEKAO"),
	/**
	 * AmRest Holdings SE
	 */
	EAT("AMREST"),
	/**
	 * Automotive Components Europe SA
	 */
	ACE("ACE"),
	/**
	 * Gielda Papierow Wartosciowych SA
	 */
	GPW("GPW"),
	/**
	 * Kogeneracja SA
	 */
	KGN("KOGENERA"),
	/**
	 * PEP SA
	 */
	PEP("PEP"),
	/**
	 * Polimex-Mostostal SA
	 */
	PXM("POLIMEXMS"),
	/**
	 * AB SA
	 */
	ABE("ABPL"),
	/**
	 * Agroton SA
	 */
	AGT("AGROTON"),
	/**
	 * Erbud SA
	 */
	ERB("ERBUD"),
	/**
	 * Pol-Aqua SA
	 */
	PQA("POLAQUA"),
	/**
	 * Wawel SA
	 */
	WWL("WAWEL"),
	/**
	 * ABM Solid SA
	 */
	ABM("ABMSOLID"),
	/**
	 * Getin Noble Bank SA
	 */
	GNB("GETINOBLE"),
	/**
	 * Indykpol SA
	 */
	IND("INDYKPOL"),
	/**
	 * Pozbud T&R SA
	 */
	POZ("POZBUD"),
	/**
	 * Petrolinvest SA
	 */
	OIL("PETROLINV"),
	/**
	 * Impel SA
	 */
	IPL("IMPEL"),
	/**
	 * CEZ a.s.
	 */
	CEZ("CEZ"),
	/**
	 * Police SA
	 */
	PCE("POLICE"),
	/**
	 * LW Bogdanka SA
	 */
	LWB("BOGDANKA"),
	/**
	 * Sadovaya Group SA
	 */
	SGR("SADOVAYA"),
	/**
	 * Kruszwica SA
	 */
	KSW("KRUSZWICA"),
	/**
	 * Stomil Sanok SA
	 */
	SNK("SANOK"),
	/**
	 * Belvedere SA
	 */
	BVD("SOBIESKI"),
	/**
	 * Mondi �wiecie SA
	 */
	MSC("SWIECIE"),
	/**
	 * Sygnity SA
	 */
	SGN("SYGNITY"),
	/**
	 * Grupa Lotos SA
	 */
	LTS("LOTOS"),
	/**
	 * Synthos SA
	 */
	SNS("SYNTHOS");

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
