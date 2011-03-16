package com.sarxos.medusa.math;

/**
 * Complex number class. The data type is "immutable" so once you create and
 * initialize a Complex object, you cannot change it.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class Complex {

	/**
	 * The real part.
	 */
	private final double re;
	/**
	 * The imaginary part.
	 */
	private final double im;

	/**
	 * Create a new object with the given real and imaginary parts.
	 * 
	 * @param re - real part
	 * @param im - imaginary part
	 */
	public Complex(double re, double im) {
		this.re = re;
		this.im = im;
	}

	@Override
	public String toString() {
		if (im == 0) {
			return re + "";
		}
		if (re == 0) {
			return im + "i";
		}
		if (im < 0) {
			return re + " - " + (-im) + "i";
		}
		return re + " + " + im + "i";
	}

	/**
	 * @return Return modulus (also abs or magnitude)
	 */
	public double abs() {
		return Math.hypot(re, im); // sqrt(re*re + im*im)
	}

	/**
	 * @return Return phase (also angle or argument)
	 */
	public double arg() {
		return Math.atan2(im, re); // between -pi and pi
	}

	/**
	 * @param b - complex object to add
	 * @return Return a new Complex object whose value is (this + b)
	 */
	public Complex plus(Complex b) {
		return new Complex(this.re + b.re, this.im + b.im);
	}

	/**
	 * @param b - complex object to remove
	 * @return Return a new Complex object whose value is (this - b)
	 */
	public Complex minus(Complex b) {
		return new Complex(this.re - b.re, this.im - b.im);
	}

	/**
	 * @param b - complex object to be multiplied
	 * @return Return a new Complex object whose value is (this * b)
	 */
	public Complex times(Complex b) {
		Complex a = this;
		double real = a.re * b.re - a.im * b.im;
		double imag = a.re * b.im + a.im * b.re;
		return new Complex(real, imag);
	}

	/**
	 * Scalar multiplication.
	 * 
	 * @param a - alpha double to be multiplied
	 * @return Return a new object whose value is (this * alpha)
	 */
	public Complex times(double a) {
		return new Complex(a * re, a * im);
	}

	/**
	 * @return Return a new Complex object whose value is the conjugate of this
	 */
	public Complex conj() {
		return new Complex(re, -im);
	}

	/**
	 * @return Return a new Complex object whose value is the reciprocal of this
	 */
	public Complex reciprocal() {
		double scale = re * re + im * im;
		return new Complex(re / scale, -im / scale);
	}

	/**
	 * @return Return real part
	 */
	public double re() {
		return this.re;
	}

	/**
	 * @return Return imaginary part
	 */
	public double im() {
		return this.im;
	}

	/**
	 * @param b - complex divider
	 * @return return new complex (this / b)
	 */
	public Complex divides(Complex b) {
		return this.times(b.reciprocal());
	}

	/**
	 * @return Return a new Complex object whose value is the complex
	 *         exponential of this
	 */
	public Complex exp() {
		double real = Math.exp(re) * Math.cos(im);
		double imag = Math.exp(re) * Math.sin(im);
		return new Complex(real, imag);
	}

	/**
	 * @return Return a new Complex object whose value is the complex sine of
	 *         this
	 */
	public Complex sin() {
		double real = Math.sin(re) * Math.cosh(im);
		double imag = Math.cos(re) * Math.sinh(im);
		return new Complex(real, imag);
	}

	/**
	 * @return A new Complex object whose value is the complex cosine of this
	 */
	public Complex cos() {
		double real = Math.cos(re) * Math.cosh(im);
		double imag = -Math.sin(re) * Math.sinh(im);
		return new Complex(real, imag);
	}

	/**
	 * @return A new Complex object whose value is the complex tangent of this
	 */
	public Complex tan() {
		return sin().divides(cos());
	}

	/**
	 * Static version of plus
	 * 
	 * @param a
	 * @param b
	 * @return new complex for a + b
	 */
	public static Complex plus(Complex a, Complex b) {
		return new Complex(a.re + b.re, a.im + b.im);
	}

	/**
	 * For test purpose
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Complex a = new Complex(5.0, 6.0);
		Complex b = new Complex(-3.0, 4.0);

		System.out.println("a            = " + a);
		System.out.println("b            = " + b);
		System.out.println("Re(a)        = " + a.re());
		System.out.println("Im(a)        = " + a.im());
		System.out.println("b + a        = " + b.plus(a));
		System.out.println("a - b        = " + a.minus(b));
		System.out.println("a * b        = " + a.times(b));
		System.out.println("b * a        = " + b.times(a));
		System.out.println("a / b        = " + a.divides(b));
		System.out.println("(a / b) * b  = " + a.divides(b).times(b));
		System.out.println("conj(a)      = " + a.conj());
		System.out.println("|a|          = " + a.abs());
		System.out.println("tan(a)       = " + a.tan());
	}

}