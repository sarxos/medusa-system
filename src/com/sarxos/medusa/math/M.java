package com.sarxos.medusa.math;

/**
 * Mathematical basics core.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class M {

	public static double sum(double[] v) {
		double s = 0;
		for (int i = 0; i < v.length; i++) {
			s += v[i];
		}
		return s;
	}

	/**
	 * Return double vector derivative value.
	 * 
	 * @param v - input double vector
	 * @return Derivative
	 */
	public static double[] diff(double[] v) {
		double[] d = new double[v.length - 1];
		for (int i = 0; i < v.length - 1; i++) {
			d[i] = v[i + 1] - v[i];
		}
		return d;
	}

	/**
	 * Return minimum value for two double input arguments.
	 * 
	 * @param a - first argument to compare
	 * @param b - second argument to compare
	 * @return Will return a if and only if a &lt;= b, in other case return b
	 */
	public static double min(double a, double b) {
		return a <= b ? a : b;
	}

	/**
	 * Return maximum value for two double input arguments.
	 * 
	 * @param a - first argument to compare
	 * @param b - second argument to compare
	 * @return Will return a if and only if a &gt;= b, in other case return b
	 */
	public static double max(double a, double b) {
		return a >= b ? a : b;
	}

	/**
	 * Perform multiplication of vector and scalar value.
	 * 
	 * @param v - vector
	 * @param a - scalar value
	 * @return Return new double array
	 */
	public static double[] mul(double[] v, double a) {
		double[] r = new double[v.length];
		for (int i = 0; i < v.length; i++) {
			r[i] = v[i] * a;
		}
		return r;
	}

	/**
	 * Perform scalar multiplication of two vectors.
	 * 
	 * @param v - first vector
	 * @param p - second vector
	 * @return Return new double array
	 */
	public static double[] mul(double[] v, double[] p) {
		double[] r = new double[v.length];
		for (int i = 0; i < v.length; i++) {
			r[i] = v[i] * p[i];
		}
		return r;
	}
}
