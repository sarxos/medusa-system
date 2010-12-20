/*
 *  javastock - Java MetaStock parser and Stock Portfolio Simulator
 *  Copyright (C) 2005 Zigabyte Corporation. ALL RIGHTS RESERVED.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.zigabyte.stock.parser.metastock;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.io.*;

/** Reads numbers and strings from a Metastock file stream and
    converts them to Java numbers and strings.
    Differs from a Java {@link DataInputStream} in several ways:
    <ul>
    <li>byte order (LSB, Least Significant Byte first)
    <li>Floats: reads a non-IEEE bit format (see {@link #readFloat}).
    <li>Strings: can reads zero-terminated strings from fixed length fields.
    <li>Dates: can read float or integer of form YYMMDD or YYYYMMDD into a Date.
    </ul>
    Written using documentation from
    http://prdownloads.sourceforge.net/mstockfl/MetaStock.pdf and
    http://www.jfb-city.co.uk/code/Metastock_Reader.zip
    (which was released to public domain in
    http://www.turtletradingsoftware.com/forum/viewtopic.php?t=742).

    @author Written for Zigabyte by SakuraJ via HotDispatch<br>
    <a href="http://www.HotDispatch.com/officefronts/SakuraJ"
            >http://www.HotDispatch.com/officefronts/SakuraJ</a>
**/
public class MetastockDataInputStream extends InputStream { 
  /** source stream **/
  private DataInputStream in;
  /** Create a MetastockDataInputStream that reads from source. **/ 
  public MetastockDataInputStream(InputStream source) {
    this.in = new DataInputStream(source);
  }
  /** Returned a signed byte value, -128..127. **/
  public byte readByte() throws IOException {
    return in.readByte();
  }
  /** Returned an unsigned byte value, 0..255. **/
  public int readUnsignedByte() throws IOException {
    return in.readUnsignedByte();
  }
  /** Read a signed two byte short, -32768..32767,
      least significant byte first **/ 
  public short readShort() throws IOException {
    return (short)(in.readUnsignedByte() | in.readByte() << 8);
  }
  /** Read an unsigned two byte short, 0..65535,
      least significant byte first **/
  public short readUnsignedShort() throws IOException {
    return (short)(in.readUnsignedByte() | in.readUnsignedByte() << 8);
  }
  /** Read a signed four byte int, least significant byte first **/
  public int readInt() throws IOException {
    return (in.readUnsignedByte() | in.readUnsignedByte() << 8 |
	    in.readUnsignedByte() << 16 | in.readByte() << 24);
  }
  /** Read a four-byte Microsoft QBasic format float,
   *  convert to IEEE standard float for Java.
   * <pre>
   * Microsoft 4 byte float
   *         31     24 23 22                       0
   *         .-------------------------------------.
   *         | 8 bits |s|msb  23 bit mantissa   lsb|
   *         `-------------------------------------'
   *              |    |              `----------------  mantissa
   *              |    `----------------------------  sign bit
   *              `------------------------------  biased exponent (81h) 
   * 	
   * b0        b1        b2        b3		
   * Microsoft Basic LE
   * mmmm|mmmm mmmm|mmmm smmm|mmmm eeee|eeee
   * BE
   * eeee|eeee smmm|mmmm mmmm|mmmm mmmm|mmmm
   * 
   * IEEE 4 byte float
   *         31 30    23 22                        0
   *         .-------------------------------------.
   *         |s| 8 bits |msb   23 bit mantissa  lsb|
   *         `-------------------------------------'
   *          |      |                `----------------  mantissa
   *          |      `--------------------------------  biased exponent (7fh)
   *          `-------------------------------------  sign bit
   * 
   * IEEE LE
   * mmmm|mmmm mmmm|mmmm emmm|mmmm seee|eeee
   * BE
   * seee|eeee emmm|mmmm mmmm|mmmm mmmm|mmmm
   * </pre>
   * [diagram by verec in http://www.jfb-city.co.uk/code/Metastock_Reader.zip
   *  released to public domain in
   *  http://www.turtletradingsoftware.com/forum/viewtopic.php?t=742]
   **/
  public float readFloat() throws IOException {
    int b0 = readUnsignedByte();
    int b1 = readUnsignedByte();
    int b2 = readUnsignedByte();
    int b3 = readUnsignedByte();
    int mantissa = (b2 << 16 | b1 << 8 | b0) & 0x7fffff;
    int sign = b2 & 0x80;
    int exponent = b3 - 2;
    int ieeeFloatBits = sign << 24 | exponent << 23 | mantissa;
    return Float.intBitsToFloat(ieeeFloatBits);
  }
  /** Read date formatted as a float YYMMDD or YYYMMDD or YYYYMMDD.
      1900 is added to years less than 1000, so 1040101 is read as 20040101. **/
  public Date readFloatDate() throws IOException {
    float f = readFloat();
    return toDate((int) f);
  }
  /** Read date formatted as an integer YYMMDD or YYYMMDD or YYYYMMDD.
      1900 is added to years less than 1000, so 1040101 is read as 20040101. **/
  public Date readIntegerDate() throws IOException {
    int i = readInt();
    return toDate(i);
  }
  /** Calendar reused by {@link #toDate}. **/
  private Calendar calendar = new GregorianCalendar();
  /** Convert an integer YYMMDD or YYYMMDD or YYYYMMDD to java.util.Date.
      1900 is added to years less than 1000, so 1040101 is read as 20040101. **/
  private Date toDate(int i) {
    int dateOfMonth = i % 100;
    i /= 100;
    int month = i % 100;
    i /= 100;
    int year = i;
    if (year < 1000)
      year += 1900;
    calendar.clear(); 
    calendar.set(year, month - 1, dateOfMonth);
    int parsedYear = calendar.get(Calendar.YEAR);
    return calendar.getTime();
  }
  /** Read zero-terminated string within field of byteCount bytes. **/ 
  public String readASCIIString(int byteCount) throws IOException {
    StringBuffer buf = new StringBuffer();
    while (byteCount-- > 0) { // read into buf until 0 byte or byteCount
      char c = (char) readUnsignedByte();
      if (c == 0)
	break;
      else
	buf.append(c);
    }
    // consume any remaining bytes after 0.
    if (byteCount > 0)
      skip(byteCount);
//     while (byteCount-- > 0) 
//       readUnsignedByte();
    return buf.toString();
  }

  /** Forwards call to source stream **/
  public int read() throws IOException {
    return in.read();
  }
  /** Forwards call to source stream **/
  public int read(byte[] buffer, int offset, int length) throws IOException {
    return in.read(buffer, offset, length);
  }
  /** Forwards call to source stream **/
  public int available() throws IOException {
    return in.available();
  }
  /** Forwards call to source stream **/
  public void close() throws IOException {
    in.close();
  }
  /** Forwards call to source stream **/
  public void skip(int n) throws IOException {
    in.skip(n);
  }
}
