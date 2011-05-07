package com.sarxos.medusa.market;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;


/**
 * Brokerage account class.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public abstract class Account {

	/**
	 * Currency number format
	 */
	public static final NumberFormat NUMBER_FORMAT = new DecimalFormat("#.##");;
	static {
		NUMBER_FORMAT.setMinimumFractionDigits(2);
		NUMBER_FORMAT.setMaximumFractionDigits(2);
	}

	/**
	 * Weight used to check account number validity
	 */
	private static int[] weights = new int[] {
			1, 10, 3, 30, 9, 90, 27, 76, 81, 34, 49,
			5, 50, 15, 53, 45, 62, 38, 89, 17, 73,
			51, 25, 56, 75, 71, 31, 19, 93, 57
	};
	
	/**
	 * Account number
	 */
	private String number = null;

	/**
	 * Required constructor.
	 * 
	 * @param number - account number
	 */
	public Account(String number) {
		if (!isValidNumber(number)) {
			throw new IllegalArgumentException("Account number " + number + " is incorrect!");
		}
		this.number = number;
	}

	/**
	 * Check account number validity
	 * 
	 * @param nrb - account number
	 * @return true if number is correct, false otherwise
	 * @see http://szewo.com/php/nrb.phtml?nrb=34343
	 */
	public static boolean isValidNumber(String nrb) {

		if (nrb == null) {
			throw new IllegalArgumentException("Account number cannot be null");
		}

		if (nrb.length() != 26) {
			return false;
		}

		nrb += "2521";
		nrb = nrb.substring(2) + nrb.substring(0, 2);

		String s = null;
		int z = 0;
		for (int i = 0; i < 30; i++) {
			s = Character.toString(nrb.charAt(29 - i));
			z += Integer.parseInt(s) * weights[i];
		}

		if (z % 97 == 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		if (!isValidNumber(number)) {
			throw new IllegalArgumentException("Account number " + number + " is incorrect!");
		}
		this.number = number;
	}

	/**
	 * @return Return papers list
	 */
	public abstract List<Paper> getPapers();
	
	public abstract List<Order> getOrders();
	
	public abstract boolean updateOrder(Order order);
	
	public abstract boolean placeOrder(Order order);
	
	public abstract boolean cancelOrder(Order order);	
}