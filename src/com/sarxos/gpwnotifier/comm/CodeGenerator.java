package com.sarxos.gpwnotifier.comm;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;


/**
 * Message code generator.
 * 
 * @TODO Move codes generation to the database 
 * @author Bartosz Firyn (SarXos)
 */
public class CodeGenerator {

	/**
	 * Default code length.
	 */
	public static final int CODE_LENGTH = 6;
	
	/**
	 * List of all already generated codes.
	 */
	private List<String> list = new LinkedList<String>(); 
	
	/**
	 * Static instance of code generator.
	 */
	private static CodeGenerator instance = new CodeGenerator();

	/**
	 * Pseudo random number generator.
	 */
	private Random generator = new Random(9);


	/**
	 * Singleton.
	 */
	private CodeGenerator() {
	}
	
	/**
	 * @return Return singleton instance of this code generator.
	 */
	public static CodeGenerator getInstance() {
		return instance;
	}

	/**
	 * @return Return new unique code.
	 */
	public String generate() {

		StringBuffer sb = new StringBuffer();
		
		int i = 0;
		int r = 0;
		
		String code = null;
		
		do {
			for (i = 0; i < CODE_LENGTH; i++) {
				r = generator.nextInt(9);
				sb.append(r);
			}
			code = sb.toString();
			sb.delete(0, sb.length() - 1);
		} while (list.contains(code));
		
		return code;
	}
}
