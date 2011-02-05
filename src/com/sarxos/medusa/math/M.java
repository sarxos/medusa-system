package com.sarxos.medusa.math;


public class M {

	public static double sum(double[] v) {
		double s = 0;
		for (int i = 0; i < v.length; i++) {
			s += v[i];
		}
		return s;
	}
	
}
