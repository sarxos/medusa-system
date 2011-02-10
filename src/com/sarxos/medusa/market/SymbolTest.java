package com.sarxos.medusa.market;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;

public class SymbolTest {

	public static void main(String[] args) {
		
		JavaClass jc = null;
		try {
			jc = Repository.lookupClass(Symbol.class);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		System.out.println(jc);
		
		ClassGen cg = new ClassGen(jc);
		cg.addField(new Field());
		
	}
	
}
