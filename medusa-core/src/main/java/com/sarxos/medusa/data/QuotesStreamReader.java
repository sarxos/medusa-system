package com.sarxos.medusa.data;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sarxos.medusa.market.Quote;
import com.sarxos.medusa.market.Symbol;


public class QuotesStreamReader implements Closeable {

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

	public static enum Format {
		UNKNOWN,
		MST,
		CGL_INTRA,
		CGL_DAY,
		OBL;
	}

	/**
	 * Underlying buffered reader.
	 */
	private BufferedReader br = null;

	private Format format = null;

	/**
	 * Create PRN reader from input stream.
	 * 
	 * @param is - input stream
	 */
	public QuotesStreamReader(InputStream is) {
		this(new InputStreamReader(is));
	}

	/**
	 * Create PRN reader from character stream reader.
	 * 
	 * @param r - characters reader
	 */
	public QuotesStreamReader(Reader r) {
		if (r == null) {
			throw new IllegalArgumentException("Reader cannot be null");
		}

		this.br = new BufferedReader(r);
	}

	@Override
	public void close() throws IOException {
		br.close();
	}

	private void recognize() throws IOException {
		if (br.ready()) {
			br.mark(1024);
			String fline = br.readLine();
			if (fline.startsWith("\"")) {
				format = Format.OBL;
			} else if (fline.startsWith("<")) {
				format = Format.MST;
			} else {
				if (fline.split(",").length == 10) {
					format = Format.CGL_INTRA;
				} else {
					format = Format.CGL_DAY;
				}
			}
			br.reset();
		}
		if (format == null) {
			format = Format.UNKNOWN;
		}
	}

	public int read(Quote[] quotes) throws IOException, ParseException {

		if (quotes == null) {
			throw new IllegalArgumentException("Quotes array canot be null");
		}

		if (!br.ready()) {
			return 0;
		}

		if (format == null) {
			recognize();
		}
		if (format == Format.UNKNOWN) {
			throw new ParseException("Unknown file format", 0);
		}

		int i = 0;

		for (i = 0; i < quotes.length; i++) {
			if (br.ready()) {
				quotes[i] = read();
			} else {
				break;
			}
		}

		return i;
	}

	public Quote read() throws IOException, ParseException {

		if (!br.ready()) {
			return null;
		}

		if (format == null) {
			recognize();
		}
		if (format == Format.UNKNOWN) {
			throw new ParseException("Unknown file format", 0);
		}

		Quote q = null;
		String line = br.readLine();

		switch (format) {
			case CGL_INTRA:
				q = fromCGLIntra(line);
				break;

			case CGL_DAY:
				// read
				break;

			case OBL:
			case MST:
				if (line.startsWith("<") || line.startsWith("\"")) {
					// omit headers
					line = br.readLine();
				}
				// read
				break;
		}

		return q;
	}

	/**
	 * Tells whether this stream is ready to be read. Stream is ready if the
	 * buffer is not empty, or if the underlying buffered reader is ready.
	 * 
	 * @exception IOException If an I/O error occurs
	 */
	public boolean ready() throws IOException {
		return br.ready();
	}

	private Quote fromCGLIntra(String str) throws ParseException {

		String[] parts = str.split(",");

		// 0 ticker
		// 1 dunno
		// 2 date yyyyMMdd,
		// 3 time hhmmdd,
		// 4 open
		// 5 high
		// 6 low
		// 7 close
		// 8 volume
		// 9 open interests

		Symbol symbol = Symbol.valueOfName(parts[0]);
		Date date = DATE_FORMAT.parse(parts[2] + parts[3]);
		double open = Double.parseDouble(parts[4]);
		double high = Double.parseDouble(parts[5]);
		double low = Double.parseDouble(parts[6]);
		double close = Double.parseDouble(parts[7]);
		long volume = Long.parseLong(parts[8]);

		return new Quote(symbol, date, open, high, low, close, volume);
	}

	/**
	 * @return the format
	 */
	protected Format getFormat() {
		return format;
	}
}
