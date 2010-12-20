package com.mac.verec.datafeed.metastock ;

/**
 * The guts of Metastock file format.
 */
public interface MasterFileDescriptor {

	int		MASTER_RECORD_LENGTH	=	53 ;	// size of a MASTER record

	//	record #0
	int		NUM_FILES_0		=	0 ;		// number of file contained in MASTER
	int		NUM_FILES_1		=	1 ;		
	int		NEXT_FILE_0		=	2 ;		// number of next file (highest F# used)
	int		NEXT_FILE_1		=	3 ;
	int		ZEROES_49		=	4 ;

	// record #1 .. #255				// description of F#xxx.DAT
	int		FILE_NUM		=	0 ;		// file # ie F#
	int		FILE_TYPE_0		=	1 ;		// CT file type = 0'e' (5 or 7 fields)
	int		FILE_TYPE_1		=	2 ;
	int		RECORD_LENGTH	=	3 ;		// record length in bytes
	int		RECORD_COUNT	=	4 ;		// number of 4 bytes fields per record
	int		RESERVED1_0		=	5 ;
	int		RESERVED1_1		=	6 ;
	int		ISSUE_NAME_0	=	7 ;		// stock name: 16 bytes
	int		ISSUE_NAME_LEN	=	16 ;
	int		RESERVED2		=	23 ;	// 6+16
	int		CT_V2_8_FLAG	=	24 ;	// 'Y" if CT ver 2.8, anything else otherwise
	int		FIRST_DATE_0	=	25 ;	// yymmdd
	int		FIRST_DATE_1	=	26 ;	// yymmdd
	int		FIRST_DATE_2	=	27 ;	// yymmdd
	int		FIRST_DATE_3	=	28 ;	// yymmdd
	int		LAST_DATE_0		=	29 ;	// yymmdd
	int		LAST_DATE_1		=	30 ;	// yymmdd
	int		LAST_DATE_2		=	31 ;	// yymmdd
	int		LAST_DATE_3		=	32 ;	// yymmdd
	int		TIME_FRAME		=	33 ;	// data format: 'I', 'W', 'Q', 'D', 'M' or 'Y'
	int		IDA_TIME_0		=	34 ;	// IDA=intraday time base
	int		IDA_TIME_1		=	35 ;
	int		SYMBOL_0		=	36 ;	// stock symbol
	int		SYMBOL_LEN		=	14 ;
	int		RESERVED3		=	50 ;	// 36+14
	int		FLAG			=	51 ;
	int		RESERVED4		=	52 ;
}
