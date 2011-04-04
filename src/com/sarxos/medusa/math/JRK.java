package com.sarxos.medusa.math;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class JRK {

	@SuppressWarnings("unused")
	private static double[] close = new double[] {
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		68.75, 68.25, 68.75, 68.25, 68.75, 68.25, 68.75, 68.25,
		68.75, 68.25, 68.75, 68.25, 68.75, 68.25, 68.75, 68.25,
		68.75, 68.25, 68.75, 68.25, 68.75, 68.25, 68.75, 68.25,
		68.75, 68.25, 68.75, 68.25, 68.75, 68.25, 68.75, 68.25,
		67.75, 67.75, 72.75, 74.75, 72.25, 71.25, 71.75, 72.75,
		77.75, 76.00, 76.00, 76.00, 74.75, 75.50, 74.75, 73.75,
		74.00, 74.75, 72.25, 72.50, 72.25, 74.50, 74.75, 75.75,
		75.75, 75.75, 74.25, 73.75, 74.75, 72.00, 71.75, 72.50,
		72.25, 71.00, 72.00, 71.75, 71.75, 73.25, 72.50, 73.75,
		74.00, 76.75, 75.75, 75.00, 75.75, 74.50, 74.25, 73.50,
		71.75, 70.50, 69.00, 70.50, 70.00, 68.75, 67.25, 68.50,
		70.75, 70.00, 70.50, 68.25, 68.25, 68.25, 63.75, 64.25
	};

	@SuppressWarnings("unused")
	private static double[] ref = new double[] {
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		68.75, 69.11126386, 68.28856883, 67.88047045, 70.61221899,
		73.37735131, 73.16751764, 72.5016599, 72.10679156, 72.09353157,
		73.81775589, 74.94089624, 75.60649969, 75.96759618, 75.9599046,
		75.85557397, 75.64181903, 75.25160642, 74.84644659, 74.61728297,
		74.17868676, 73.67238488, 73.1842394, 73.10785283, 73.34958901,
		73.85707168, 74.42911902, 74.93038851, 75.09287507, 74.94649321,
		74.80499937, 74.33318217, 73.66893412, 73.12071155, 72.71498835,
		72.26423085, 71.9702028, 71.79260915, 71.69675123, 71.86943138,
		72.07979919, 72.43023876, 72.85788116, 73.73196015, 74.51914619,
		75.00208707, 75.33621901, 75.38718804, 75.22566212, 74.87443544,
		74.12468013, 72.85729881, 71.22401395, 70.37858241, 69.96256092,
		69.53514922, 68.68810853, 68.25445186, 68.63035835, 69.0349436,
		69.44967111, 69.46200796, 69.24863466, 68.96050991, 66.39400988,
		65.00557185
	};

	public static void main(String[] args) {

		List<Double> list = new LinkedList<Double>();
		for (int u = 0; u < close.length; u++) {
			list.add(close[u]);
		}
		Collections.reverse(list);
		for (int u = 0; u < list.size(); u++) {
			close[u] = list.get(u);
		}

		double[] jma = SX.reverse(jrk(close, 3, 5));

		for (int i = 70; i < jma.length; i++) {
			System.out.println(Double.toString(jma[i]).replaceAll("\\.", ",") + ";");
		}
	}

	/**
	 * Calculate MA values similar to Jurik's JMA smoother. I admit that I have
	 * no idea how this algorithm works, but output values are very close to
	 * original Jurik's JMA.
	 * 
	 * @param values - input double array
	 * @param K - moving average degree
	 * @param P - phase
	 * @return Will return double array
	 */
	public static double[] jrk(double[] values, double K, double P) {

		int count = values.length - 2;

		// output
		double[] jrk = new double[values.length];
		double[] fatVals = new double[values.length];

		// ----
		double avv = 0;
		double av1 = 0;
		double av2 = 0;
		double av3 = 0;
		double av4 = 0;
		double as8 = 0;
		double as10 = 0;
		double as18 = 0;
		double as20 = 0;

		int av5 = 0;
		int av6 = 0;
		int as28 = 0;
		int as30 = 0;
		int as38 = 0;
		int as40 = 0;
		int as48 = 0;
		int as50 = 0;
		int as58 = 0;
		int as60 = 0;

		double as68 = 0;
		double as70 = 0;
		double af8 = 0;
		double af10 = 0;
		double af18 = 0;
		double af20 = 0;
		double af28 = 0;
		double af30 = 0;
		double af38 = 0;
		double af40 = 0;
		double af48 = 0;
		double af50 = 0;
		double af58 = 0;
		double af60 = 0;
		double af68 = 0;
		double af70 = 0;
		double af78 = 0;
		double af80 = 0;
		double af88 = 0;
		double af90 = 0;
		double af98 = 0;
		double afA0 = 0;
		double afA8 = 0;
		double afB0 = 0;
		double afB8 = 0;
		double s = 0;
		double afC8 = 0;
		double afD0 = 0;
		double af0 = 0;
		double afD8 = 0;
		double afE0 = 0;
		double afE8 = 0;
		int num = 0;
		double r = 0;
		int avalue2 = 0;

		int ii = 0;
		int jj = 0;
		int shift = 0;
		double series = 0;
		double[] alist = new double[128];
		double[] aring1 = new double[128];
		double[] aring2 = new double[11];
		double[] abuffer = new double[62];
		double jma = 0;
		double fvalue1 = 0;

		for (shift = count - 38 - 1; shift >= 0; shift--) {

			fvalue1 =
				0.4360409450 * values[shift + 0]
				+ 0.3658689069 * values[shift + 1]
				+ 0.2460452079 * values[shift + 2]
				+ 0.1104506886 * values[shift + 3]
				- 0.0054034585 * values[shift + 4]
				- 0.0760367731 * values[shift + 5]
				- 0.0933058722 * values[shift + 6]
				- 0.0670110374 * values[shift + 7]
				- 0.0190795053 * values[shift + 8]
				+ 0.0259609206 * values[shift + 9]
				+ 0.0502044896 * values[shift + 10]
				+ 0.0477818607 * values[shift + 11]
				+ 0.0249252327 * values[shift + 12]
				- 0.0047706151 * values[shift + 13]
				- 0.0272432537 * values[shift + 14]
				- 0.0338917071 * values[shift + 15]
				- 0.0244141482 * values[shift + 16]
				- 0.0055774838 * values[shift + 17]
				+ 0.0128149838 * values[shift + 18]
				+ 0.0226522218 * values[shift + 19]
				+ 0.0208778257 * values[shift + 20]
				+ 0.0100299086 * values[shift + 21]
				- 0.0036771622 * values[shift + 22]
				- 0.0136744850 * values[shift + 23]
				- 0.0160483392 * values[shift + 24]
				- 0.0108597376 * values[shift + 25]
				- 0.0016060704 * values[shift + 26]
				+ 0.0069480557 * values[shift + 27]
				+ 0.0110573605 * values[shift + 28]
				+ 0.0095711419 * values[shift + 29]
				+ 0.0040444064 * values[shift + 30]
				- 0.0023824623 * values[shift + 31]
				- 0.0067093714 * values[shift + 32]
				- 0.0072003400 * values[shift + 33]
				- 0.0047717710 * values[shift + 34]
				+ 0.0005541115 * values[shift + 35]
				+ 0.0007860160 * values[shift + 36]
				+ 0.0130129076 * values[shift + 37]
				+ 0.0040364019 * values[shift + 38];

			fatVals[shift] = fvalue1;

			as28 = 63;
			as30 = 64;
			for (ii = 1; ii <= as28; ii++) {
				alist[ii] = -1000000;
			}
			for (ii = as30; ii <= 127; ii++) {
				alist[ii] = 1000000;
			}
			af0 = 1;

			series = values[shift];
			if (num < 61) {
				num = num + 1;
				abuffer[num] = series;
			}

			if (num > 30) {

				if (K < 1.0000000002) {
					af80 = 0.0000000001;
				} else {
					af80 = (K - 1) / 2;
				}

				if (P < -100) {
					af10 = 0.5;
				} else if (P > 100) {
					af10 = 2.5;
				} else {
					af10 = P / 100 + 1.5;
				}

				av1 = Math.log(Math.sqrt(af80));
				av2 = av1;
				if (av1 / Math.log(2) + 2 < 0) {
					av3 = 0;
				} else {
					av3 = av2 / Math.log(2) + 2;
				}
				af98 = av3;

				if (0.5 <= af98 - 2) {
					af88 = af98 - 2;
				} else {
					af88 = 0.5;
				}
				af78 = Math.sqrt(af80) * af98;
				af90 = af78 / (af78 + 1);
				af80 = af80 * 0.9;
				af50 = af80 / (af80 + 2);

				if (af0 != 0) {
					af0 = 0;
					av5 = 0;
					for (ii = 1; ii <= 29; ii++) {
						if (abuffer[ii + 1] != abuffer[ii]) {
							av5 = 1;
						}
					}

					afD8 = av5 * 30;
					if (afD8 == 0) {
						af38 = series;
					} else {
						af38 = abuffer[1];
					}
					af18 = af38;
					if (afD8 > 29) {
						afD8 = 29;
					}
				} else {
					afD8 = 0;
				}

				for (ii = (int) afD8; ii >= 0; ii--) {
					avalue2 = 31 - ii;
					if (ii == 0) {
						af8 = series;
					} else {
						af8 = abuffer[avalue2];
					}
					af28 = af8 - af18;
					af48 = af8 - af38;
					if (Math.abs(af28) > Math.abs(af48)) {
						av2 = Math.abs(af28);
					} else {
						av2 = Math.abs(af48);
					}
					afA0 = av2;
					avv = afA0 + 0.0000000001; // {1.0e-10}

					if (as48 <= 1) {
						as48 = 126;
					} else {
						as48 = as48 - 2;
					}
					if (as50 < 1) {
						as50 = 9;
					} else {
						as50 = as50 - 1;
					}
					if (as70 < 128) {
						as70 = as70 + 1;
					}
					as8 = as8 + avv - aring2[as50];
					aring2[as50] = avv;
					if (as70 > 10) {
						as20 = as8 / 10;
					} else {
						as20 = as8 / as70;
					}

					if (as70 > 127) {
						as10 = aring1[as48];
						aring1[as48] = as20;
						as68 = 64;
						as58 = (int) as68;
						while (as68 > 1) {
							if (alist[as58] < as10) {
								as68 = as68 * 0.5;
								as58 = (int) (as58 + as68);
							} else {
								if (alist[as58] <= as10) {
									as68 = 1;

								} else {
									as68 = as68 * 0.5;
									as58 = (int) (as58 - as68);
								}
							}
						}
					} else {
						aring1[as48] = as20;
						if (as28 + as30 > 127) {
							as30 = as30 - 1;
							as58 = as30;

						} else {
							as28 = as28 + 1;
							as58 = as28;
						}
						if (as28 > 96) {
							as38 = 96;
						} else {
							as38 = as28;
						}
						if (as30 < 32) {
							as40 = 32;
						} else {
							as40 = as30;
						}
					}

					as68 = 64;
					as60 = (int) as68;
					while (as68 > 1) {
						if (alist[as60] >= as20) {
							if (alist[as60 - 1] <= as20) {
								as68 = 1;
							} else {
								as68 = as68 * 0.5;
								as60 = (int) (as60 - as68);
							}
						} else {
							as68 = as68 * 0.5;
							as60 = (int) (as60 + as68);
						}
						if ((as60 == 127) && (as20 > alist[127])) {
							as60 = 128;
						}
					}

					if (as70 > 127) {
						if (as58 >= as60) {
							if ((as38 + 1 > as60) && (as40 - 1 < as60)) {
								as18 = as18 + as20;
							} else {
								if ((as40 > as60) && (as40 - 1 < as58)) {
									as18 = as18 + alist[as40 - 1];
								} else {
									if (as40 >= as60) {
										if ((as38 + 1 < as60) && (as38 + 1 > as58)) {
											as18 = as18 + alist[as38 + 1];
										} else {
											if (as38 + 2 > as60) {
												as18 = as18 + as20;
											} else {
												if ((as38 + 1 < as60) && (as38 + 1 > as58)) {
													as18 = as18 + alist[as38 + 1];
												}
												if (as58 > as60) {
													if ((as40 - 1 < as58) && (as38 + 1 > as58)) {
														as18 = as18 - alist[as58];
													} else {
														if ((as38 < as58) && (as38 + 1 > as60)) {
															as18 = as18 - alist[as38];
														} else {
															if ((as38 + 1 > as58) && (as40 - 1 < as58)) {
																as18 = as18 - alist[as58];
															} else {
																if ((as40 > as58) && (as40 < as60)) {
																	as18 = as18 - alist[as40];
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
					if (as58 <= as60) {
						if (as58 >= as60) {
							alist[as60] = as20;
						} else {
							for (jj = as58 + 1; jj <= as60 - 1; jj++) {
								alist[jj - 1] = alist[jj];
							}
							alist[as60 - 1] = as20;
						}
					} else {
						for (jj = as58 - 1; jj >= as60; jj--) {
							alist[jj + 1] = alist[jj];
						}
						alist[as60] = as20;
					}

					if (as70 <= 127) {
						as18 = 0;
						for (jj = as40; jj <= as38; jj++) {
							as18 = as18 + alist[jj];
						}
					}
					af60 = as18 / (as38 - as40 + 1);
					if (r + 1 > 31) {
						r = 31;
					} else {
						r = r + 1;
					}

					if (r <= 30) {
						if (af28 > 0) {
							af18 = af8;
						} else {
							af18 = af8 - af28 * af90;
						}
						if (af48 < 0) {
							af38 = af8;
						} else {
							af38 = af8 - af48 * af90;
						}
						afB8 = series;

						if (r != 30) {
							continue;
						}
						if (r == 30) {
							s = series;
							if ((af78 + 0.5) >= 1) {
								av4 = (af78 + 0.5);
							} else {
								av4 = 1;
							}
							afE8 = (av4 + 0.5);
							if ((af78 - 0.5) >= 1) {
								av2 = (af78 - 0.5);
							} else {
								av2 = 1;
							}
							afE0 = (av2 + 0.5);
							if (afE8 == afE0) {
								af68 = 1;
							} else {
								av4 = afE8 - afE0;
								af68 = (af78 - afE0) / av4;
							}
							if (afE0 <= 29) {
								av5 = (int) afE0;
							} else {
								av5 = 29;
							}
							if (afE8 <= 29) {
								av6 = (int) afE8;
							} else {
								av6 = 29;
							}
							afA8 = (series - abuffer[num - av5]) * (1 - af68) / afE0 + (series - abuffer[num - av6]) * af68 / afE8;
						}
					} else {
						if (af98 >= Math.pow((afA0 / af60), af88)) {
							av1 = Math.pow((afA0 / af60), af88);
						} else {
							av1 = af98;
						}
						if (av1 < 1) {
							av2 = 1;
						} else {
							if (af98 >= Math.pow((afA0 / af60), af88)) {
								av3 = Math.pow((afA0 / af60), af88);
							} else {
								av3 = af98;
							}
							av2 = av3;
						}
						af58 = av2;
						af70 = Math.pow(af90, Math.sqrt(af58));
						if (af28 > 0) {
							af18 = af8;
						} else {
							af18 = af8 - af28 * af70;
						}
						if (af48 < 0) {
							af38 = af8;
						} else {
							af38 = af8 - af48 * af70;
						}
					}
				}

				if (r > 30) {
					af30 = Math.pow(af50, af58);
					s = (1 - af30) * series + af30 * s;
					afC8 = (series - s) * (1 - af50) + af50 * afC8;
					afD0 = af10 * afC8 + s;
					af20 = af30 * (-2);
					af40 = af30 * af30;
					afB0 = af20 + af40 + 1;
					afA8 = (afD0 - afB8) * afB0 + af40 * afA8;
					afB8 = afB8 + afA8;
				}

				jma = afB8;
			}

			if (num <= 30) {
				jma = 0;
			}

			jrk[shift] = jma;
		}

		return jrk;
	}

}
