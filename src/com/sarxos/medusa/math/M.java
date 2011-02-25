package com.sarxos.medusa.math;

public class M {

	public static double sum(double[] v) {
		double s = 0;
		for (int i = 0; i < v.length; i++) {
			s += v[i];
		}
		return s;
	}

	public static double[] diff(double[] v) {
		double[] d = new double[v.length - 1];
		for (int i = 0; i < v.length - 1; i++) {
			d[i] = v[i + 1] - v[i];
		}
		return d;
	}
}
