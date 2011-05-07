package com.sarxos.medusa.market;

public enum OrderValidity {

	/**
	 * Up to particular date.
	 */
	TO_DATE,

	/**
	 * Zlecenie wa¿ne bezterminowo, a wiêc do momentu odwo³ania przez Inwestora
	 * lub anulowania przez gie³dê.
	 */
	DOM,

	/**
	 * Wykonaj lub anuluj - zlecenie jest realizowane natychmiast po wys³aniu,
	 * wy³¹cznie w ca³oœci. Zlecenie nie mo¿e byæ sk³adane w fazie interwencji.
	 * Je¿eli w arkuszu zleceñ na gie³dzie brak zleceñ przeciwstawnych z limitem
	 * ceny umo¿liwiaj¹cym zawarcie transakcji, zlecenie traci wa¿noœæ. W
	 * przypadku tego typu wa¿noœci nie ma mo¿liwoœci modyfikacji dat wa¿noœci
	 * zlecenia.
	 */
	WuA,

	/**
	 * Wykonaj i anuluj - zlecenie traci wa¿noœæ po zawarciu pierwszej
	 * transakcji (lub pierwszych transakcji, je¿eli zlecenie zostanie
	 * zrealizowane jednoczeœnie w kilku transakcjach). Zlecenie jest
	 * realizowane natychmiast po wys³aniu a w przypadku zrealizowania
	 * czêœciowego, niezrealizowana czêœæ traci wa¿noœæ. Zlecenie nie mo¿e byæ
	 * sk³adane w fazie interwencji. Je¿eli w arkuszu zleceñ na gie³dzie brak
	 * zleceñ przeciwstawnych z limitem ceny umo¿liwiaj¹cym zawarcie transakcji,
	 * zlecenie traci wa¿noœæ. W przypadku tego typu wa¿noœci nie ma mo¿liwoœci
	 * modyfikacji dat wa¿noœci zlecenia.
	 */
	WiN;

}
