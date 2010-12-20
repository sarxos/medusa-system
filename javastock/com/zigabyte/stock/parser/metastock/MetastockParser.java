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

import com.zigabyte.stock.data.*;
import com.zigabyte.stock.parser.*;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

/** MetastockParser parses stock data in Metastock format.
    Creates a {@link DefaultStockMarketHistory} in memory for all stocks
    below a given directory.  Optionally restricts data to a given date
    range.

    <p>Assumes data is preadjusted for splits and dividends, and
    contains no split or dividend dates.
    
    <p>Written using documentation on the Metastock data format from
    http://prdownloads.sourceforge.net/mstockfl/MetaStock.pdf and
    http://www.jfb-city.co.uk/code/Metastock_Reader.zip
    (which was released to public domain in
    http://www.turtletradingsoftware.com/forum/viewtopic.php?t=742).

    @author Written for Zigabyte by SakuraJ via HotDispatch<br>
    <a href="http://www.HotDispatch.com/officefronts/SakuraJ"
             >http://www.HotDispatch.com/officefronts/SakuraJ</a>
**/
public class MetastockParser extends AbstractStockFilesParser {
  // Fields
  /** If startOn is non-null, data before the startOn date is omitted
      from the parsing result. **/
  private final Date startOn;
  /** If non-null, data on or after the endBefore date is omitted from
      the parsing result. **/
  private final Date endBefore;
  /** If true, skip files if index dates show it is entirely out of range. **/
  private final boolean useIndexDates;
  // CONSTRUCTORS
  /** Construct a MetastockParser that creates {@link DefaultStockDataPoint}s.
   **/
  public MetastockParser() {
    this(false);
  }
  /** Construct a MetastockParser that may create 
      {@link ShortStockDataPoint} or {@link DefaultStockDataPoint}s.
      @param useShortRep if true, tries to save space by creating data points 
      using {@link ShortStockDataPoint}, which <em>assumes prices are to
      the nearest cent</em> (so may not be suitable for fractions that
      cannot be expressed in cents).
      If a price is over 32767 cents, backs out for that data point and uses
      {@link DefaultStockDataPoint}.  If useShortRep is false, always
      creates {@link DefaultStockDataPoint}.
      @see AbstractStockFilesParser#createStockDataPoint
   **/
  public MetastockParser(boolean useShortRep) {
    this(useShortRep, null, null, false);
  }
  /** Construct a MetastockParser that may create 
      {@link ShortStockDataPoint} or {@link DefaultStockDataPoint}s.
      @param useShortRep if true, tries to save space by creating data points 
      using {@link ShortStockDataPoint}, which <em>assumes prices are to
      the nearest cent</em> (so may not be suitable for fractions that
      cannot be expressed in cents).
      If a price is over 32767 cents, backs out for that data point and uses
      {@link DefaultStockDataPoint}.  If useShortRep is false, always
      creates {@link DefaultStockDataPoint}.
      @param startOn If not null, omit data from dates before the
      <code>startOn</code> date.
      @param endBefore If not null, omit data from dates on or after the
      <code>endBefore</code> date.
      @param useIndexDates If true, skip parsing stock files if the
      index indicates the file has a positive date range that does not
      overlap the desired range (startOn--endBefore).  If false,
      assume index dates may be incorrect and scan file anyway.
      
      @see AbstractStockFilesParser#createStockDataPoint
   **/
  public MetastockParser(boolean useShortRep, Date startOn, Date endBefore,
			 boolean useIndexDates) {
    super(useShortRep);
    this.startOn = startOn;
    this.endBefore = endBefore;
    this.useIndexDates =
      (useIndexDates && (startOn != null || endBefore != null));
  }

  /** Add to stockHistories all histories in the directory, recursing
      into subdirectories.  Loads all files listed in each directory
      "emaster" index (or "master" index if no "emaster" index), and
      all files in each directory "xmaster" index, if present.
      @param dir root directory containing Metastock data.
      @param stockHistories where to add stockHistories.
  **/
  public void loadHistory(File dir, 
			  DefaultStockMarketHistory stockHistories)
  throws IOException {
    if (!dir.isDirectory())
      throw new FileNotFoundException("Not a directory: "+dir);
    boolean emasterFilesLoaded = loadEMasterHistory(dir, stockHistories);
    // load master only if emaster does not exist, as it indexes same files
    boolean masterFilesLoaded =
      (emasterFilesLoaded? false : loadMasterHistory(dir, stockHistories));
    boolean xmasterFilesLoaded = loadXMasterHistory(dir, stockHistories);
    File[] subdirectories = dir.listFiles(DIRECTORY_FILTER);
    for (int i = 0; i < subdirectories.length; i++)
      loadHistory(subdirectories[i], stockHistories);
  }
  /** File filter that accepts only directories. **/
  private static FileFilter DIRECTORY_FILTER =
    new FileFilter() {
      public boolean accept(File file) {
	return file.isDirectory();
      }
    };

  /** Look for an "emaster" index in dir, and if present, create a
      StockHistory for each indexed file and add the StockHistory to
      stockHistories.
      @return true if "emaster" index file was found. **/
  protected boolean loadEMasterHistory(File dir, 
				       DefaultStockMarketHistory stockHistories)
  throws IOException {
    File emasterFile = new File(dir, "EMASTER");
    if (!emasterFile.exists())
      emasterFile = new File(dir, "emaster");
    if (emasterFile.exists() && !emasterFile.isDirectory()) {
      MetastockDataInputStream in =
	new MetastockDataInputStream(new FileInputStream(emasterFile));
      IndexHeaderRecord header = parseEMasterHeaderRecord(in);
      int recordCount = header.getRecordCount();
      // for each file record, load data file
      for (int recordNumber = 0; recordNumber < recordCount; recordNumber++) {
	IndexItemRecord fileRecord = parseEMasterItemRecord(in);
	StockHistory history = loadDataFile(dir, fileRecord);
	if (history != null)
	  stockHistories.add(history);
      }
      in.close();
      return true;
    } else {
      return false;
    }
  }
  /** Parse a header record of an "emaster" file. 
      This creates a {@link EMasterHeaderRecord} **/
  protected IndexHeaderRecord parseEMasterHeaderRecord(MetastockDataInputStream in)
  throws IOException {
    return new EMasterHeaderRecord(in);
  }
  /** Parse an item record of an "emaster" file.
      This creates a {@link EMasterItemRecord} **/
  protected IndexItemRecord parseEMasterItemRecord(MetastockDataInputStream in)
  throws IOException {
    return new EMasterItemRecord(in);
  }
    
  /** Look for a "master" index in dir, and if present, create a
      StockHistory for each indexed file and add the StockHistory to
      stockHistories.
      @return true if "master" index file was found. **/
  protected boolean loadMasterHistory(File dir, 
				      DefaultStockMarketHistory stockHistories)
  throws IOException {
    File masterFile = new File(dir, "MASTER");
    if (!masterFile.exists())
      masterFile = new File(dir, "master");
    if (masterFile.exists() && !masterFile.isDirectory()) {
      MetastockDataInputStream in =
	new MetastockDataInputStream(new FileInputStream(masterFile));
      IndexHeaderRecord header = parseMasterHeaderRecord(in);
      int recordCount = header.getRecordCount();
      // for each file record, load data file
      for (int recordNumber = 0; recordNumber < recordCount; recordNumber++) {
	IndexItemRecord fileRecord = parseMasterItemRecord(in);
	StockHistory history = loadDataFile(dir, fileRecord);
	if (history != null)
	  stockHistories.add(history);
      }
      in.close();
      return true;
    } else {
      return false;
    }
  }
  /** Parse a header record of a "master" file.
      This creates a {@link MasterHeaderRecord} **/
  protected IndexHeaderRecord parseMasterHeaderRecord(MetastockDataInputStream in)
  throws IOException {
    return new MasterHeaderRecord(in);
  }
  /** Parse an item record of a "master" file.
      This creates a {@link MasterItemRecord} **/
  protected IndexItemRecord parseMasterItemRecord(MetastockDataInputStream in)
  throws IOException {
    return new MasterItemRecord(in);
  }


  /** Look for an "xmaster" index in dir, and if present, create a
      StockHistory for each indexed file and add the StockHistory to
      stockHistories.
      @return true if "xmaster" index file was found. **/
  protected boolean loadXMasterHistory(File dir, 
				       DefaultStockMarketHistory stockHistories)
  throws IOException {
    File xmasterFile = new File(dir, "XMASTER");
    if (!xmasterFile.exists())
      xmasterFile = new File(dir, "xmaster");
    if (xmasterFile.exists() && !xmasterFile.isDirectory()) {
      MetastockDataInputStream in =
	new MetastockDataInputStream(new FileInputStream(xmasterFile));
      IndexHeaderRecord header = parseXMasterHeaderRecord(in);
      int recordCount = header.getRecordCount();
      // for each file record, load data file
      for (int recordNumber = 0; recordNumber < recordCount; recordNumber++) {
	IndexItemRecord fileRecord = parseXMasterItemRecord(in);
	StockHistory history = loadDataFile(dir, fileRecord);
	if (history != null)
	  stockHistories.add(history);
      }
      in.close();
      return true;
    } else {
      return false;
    }
  }
  /** Parse a header record of an "xmaster" file.
      This creates an {@link XMasterHeaderRecord} **/
  protected IndexHeaderRecord parseXMasterHeaderRecord(MetastockDataInputStream in)
  throws IOException {
    return new XMasterHeaderRecord(in);
  }
  /** Parse an item record of an "xmaster" file.
      This creates an {@link XMasterItemRecord} **/
  protected IndexItemRecord parseXMasterItemRecord(MetastockDataInputStream in)
  throws IOException {
    return new XMasterItemRecord(in);
  }


  /** From dir, load the data file for the stock described by fileRecord.
      Create a StockHistory holding all the dated data points in the file.
      Return null if startOn--endBefore does not overlap range of data.
      @param dir directory holding data files
      @param fileRecord specifies stock name and data file in dir **/
  protected StockHistory loadDataFile(File dir, 
				      IndexItemRecord fileRecord)
  throws IOException {
    long msecLoadStart = (this.startOn   == null? 0 : startOn.getTime());
    long msecLoadEnd   = (this.endBefore == null? 0 : endBefore.getTime());
    long msecFileStart = fileRecord.getBeginDate().getTime();
    long msecFileEnd   = fileRecord.getEndDate().getTime();
    // if file record has valid date range
    if (this.useIndexDates && msecFileStart < msecFileEnd) { 
      // if does not overlap desired date range, skip
      if ((this.startOn   != null && msecLoadStart > msecFileEnd) || 
	  (this.endBefore != null || msecLoadEnd <= msecFileStart)) {
	System.err.println("Skip "+fileRecord.getSymbol()+" indexed as "+
			   DATE_FORMAT.format(fileRecord.getBeginDate())+"-"+
			   DATE_FORMAT.format(fileRecord.getEndDate()));
	return null;
      }
    }

    DefaultStockHistory stockHistory = createStockHistory(dir, fileRecord);
    File dataFile = fileRecord.getFile(dir);
    MetastockDataInputStream dataIn =
      new MetastockDataInputStream(new FileInputStream(dataFile));

    DataHeaderRecord headerRecord = new DataHeaderRecord(dataIn);
    int headerRecordCount = headerRecord.getRecordCount();
    int readRecordCount = 1; // include headerRecord
    while(--headerRecordCount > 0) { 
      DataItemRecord itemRecord = new DataItemRecord(dataIn);
      StockDataPoint datum = createStockDataPoint(itemRecord);
      long msecDate = datum.getDate().getTime();
      if ((this.startOn   == null || msecLoadStart <= msecDate) &&
	  (this.endBefore == null || msecDate < msecLoadEnd))
	stockHistory.add(datum);
    }
    dataIn.close();
    return (stockHistory.size() == 0 ? null : stockHistory);
  }

  /** Calls createStockHistory with symbol and name from file record **/
  protected DefaultStockHistory createStockHistory(File dir,
						   IndexItemRecord fileRecord){
    return createStockHistory(fileRecord.getSymbol(), fileRecord.getName());
  }

  protected StockDataPoint createStockDataPoint(DataItemRecord itemRecord) {
    return createStockDataPoint(itemRecord.getDate(),
				itemRecord.getOpen(),
				itemRecord.getHigh(),
				itemRecord.getLow(),
				itemRecord.getClose(),
				itemRecord.getVolume());
  }
  /** The initial header record of a Metastock index file contains
      the number of records in the file. **/
  protected static interface IndexHeaderRecord {
    /** Number of records in file.   Record count includes 
	header record, so it is always at least 1. **/
    public int getRecordCount();
  }

  /** An index item record in a Metastock index file contains symbol and name
      for a stock and points to the data file for the stock. **/
  protected static interface IndexItemRecord {
    /** Return the stock file in directory described by this item record.**/
    public File getFile(File directory);
    /** Return the stock symbol for this stock. **/
    public String getSymbol();
    /** Return the company name for this stock. **/
    public String getName();
    /** Return the begin date for the stock history in the file. **/
    public Date getBeginDate();
    /** Return the end date for the stock history in the file. **/
    public Date getEndDate();
  }

  /** Header record parsed from "master" index file in Metastock directories.**/
  protected static class MasterHeaderRecord implements IndexHeaderRecord {
    final int recordCount;
    /** Record count is first byte, unsigned, of 53 byte record. **/
    public MasterHeaderRecord(MetastockDataInputStream in) throws IOException {
      this.recordCount = in.readUnsignedByte();	//  0-0  (1)
      in.skip(52);				//  1-52(52)
    }
    public int getRecordCount() { return recordCount; } 
  }

  /** Item records parsed from "master" index file in Metastock directories.**/
  protected static class MasterItemRecord implements IndexItemRecord{
    final int fileNumber;
    final String symbol;
    final String name;
    final Date beginDate;
    final Date endDate;
    /** Parse 53-byte item record from in stream.<br>
     *  <pre>
     *   0-0   (1) File number (unsigned byte) for Fn.DAT file
     *   7-22 (16) Company name (zero-terminated ASCII string)
     *  25-28  (4) Begin date (proprietary float)
     *  29-32  (4) End date (proprietary float)
     *  36-49 (14) Stock Symbol (zero-terminated ASCII string)
     *  </pre> **/
    public MasterItemRecord(MetastockDataInputStream in) throws IOException {
      this.fileNumber = in.readUnsignedByte();	//  0-0   (1)
      in.skip(6);				//  1-6   (6)
      this.name = in.readASCIIString(16);	//  7-22 (16)
      in.skip(2);				// 23-24  (2)
      this.beginDate = in.readFloatDate();	// 25-28  (4)
      this.endDate = in.readFloatDate();	// 29-32  (4)
      in.skip(3);				// 33-35  (3)
      this.symbol = in.readASCIIString(14);	// 36-49 (14)
      in.skip(3);				// 50-52  (3)
    }
    /** Returns directory/Fn.DAT, where n is the unsigned file number. **/
    public File getFile(File directory) {
      String fileName = "F"+this.fileNumber+".DAT";
      return new File(directory, fileName);
    }
    public String getSymbol() { return this.symbol; }
    public String getName() { return this.name; }
    public Date getBeginDate() { return this.beginDate; }
    public Date getEndDate() { return this.endDate; } 
  }

  /** Header record of "emaster" index file in Metastock directories. **/
  protected static class EMasterHeaderRecord implements IndexHeaderRecord {
    final int recordCount;
    /** Record count is first byte, unsigned, of 192-byte record. **/
    public EMasterHeaderRecord(MetastockDataInputStream in) throws IOException {
      this.recordCount = in.readUnsignedByte();	//  0-0    (1)
      in.skip(191);				//  1-191(191)
    }
    public int getRecordCount() { return recordCount; } 
  }
  /** Item records parsed from "emaster" index file in Metastock directories.*/
  protected static class EMasterItemRecord implements IndexItemRecord {
    static int MAGIC_NUMBER = 0x3134;
    final int fileNumber;
    final String symbol;
    final String name;
    final Date beginDate;
    final Date endDate;
    final float lastDividend;
    final float lastDividendAdjRate;
    /** Parse 192-byte item record from in stream.<br>
     *  <pre>
     *   0-1   (2) Skip
     *   2-2   (1) File number (unsigned byte) for Fn.DAT file
     *  11-24 (14) Stock symbol (zero terminated ASCII string)
     *  32-47 (16) Company name (zero terminated ASCII string)
     *  72-75  (4) End date (proprietary float holding YYMMDD)
     * 126-129 (4) Begin date (proprietary float holding YYYYMMDD)
     * 131-134 (4) Last dividend (proprietary float)
     * 135-138 (4) Last dividend adjustment rate (proprietary float)
     * </pre> **/
    public EMasterItemRecord(MetastockDataInputStream in) throws IOException {
      //in = printRecord(in, 192);
      in.skip(2);				//  0-1   (2)
      this.fileNumber = in.readUnsignedByte();	//  2-2   (1)
      in.skip(8);				//  3-10  (8)
      this.symbol = in.readASCIIString(14);	// 11-24 (14)
      in.skip(7);				// 25-31  (7)
      this.name = in.readASCIIString(16);	// 32-47 (16)
      in.skip(16);				// 48-63 (16)
      float beginDateShort = in.readFloat();	// 64-67  (4)
      in.skip(4);				// 68-71  (4)
      this.endDate = in.readFloatDate();	// 72-75  (4)
      in.skip(50);				// 76-125(50)
      this.beginDate = in.readFloatDate();  	//126-129 (4)
      in.skip(1);				//130-130 (1)
      this.lastDividend = in.readFloat();	//131-134 (4)
      this.lastDividendAdjRate = in.readFloat();//135-138 (4)
      in.skip(53);				//139-191(53)
    }
    /** Returns directory/Fn.DAT, where n is the unsigned file number. **/
    public File getFile(File directory) {
      String fileName = "F"+this.fileNumber+".DAT";
      return new File(directory, fileName);
    }
    public String getSymbol() { return this.symbol; }
    public String getName() { return this.name; }
    public Date getBeginDate() { return this.beginDate; }
    public Date getEndDate() { return this.endDate; } 
  }
  
  /** Header record parsed from "xmaster" index file in Metastock directories.*/
  protected static class XMasterHeaderRecord implements IndexHeaderRecord {
    final int recordCount;
    /** Record count is after bytes 0-9: unsigned short at bytes 10-11
	of 150 byte record. **/
    public XMasterHeaderRecord(MetastockDataInputStream in) throws IOException {
      in.skip(10);					//  0-9   (10)
      this.recordCount = in.readUnsignedShort();	// 10-11   (2)
      in.skip(138);					// 12-149(138)
    }
    public int getRecordCount() { return recordCount; } 
  }

  /** Item records parsed from "xmaster" index file in Metastock directories.*/
  protected static class XMasterItemRecord implements IndexItemRecord {
    static final char TYPE = 'D';
    final int fileNumber;
    final String symbol;
    final String name;
    final Date beginDate;
    final Date endDate;
    /** Parse 150-byte item record from in stream.<br>
     *  <pre>
     *   0-0   (1) Skip
     *   1-15 (15) Stock symbol (zero terminated ASCII string)
     *  16-61 (46) Company name (zero terminated ASCII string)
     *  65-66 (16) File number (unsigned short) for Fn.MWD file
     * 104-107 (4) Begin date (int holding YYYYMMDD)
     * 116-119 (4) End date (int holding YYYYMMDD)
     * </pre> **/
    public XMasterItemRecord(MetastockDataInputStream in) throws IOException {
      //in = printRecord(in, 150);
      in.skip(1);				//  0-0   (1)
      this.symbol = in.readASCIIString(15);	//  1-15 (15)
      this.name = in.readASCIIString(46);	// 16-61 (46)
      char type = (char) in.readUnsignedByte(); // 62-62  (1)
      in.skip(2);				// 63-64  (2)
      this.fileNumber = in.readUnsignedShort();	// 65-66  (2)
      in.skip(13);				// 67-79 (13)
      Date endDate2 = in.readIntegerDate();	// 80-83  (4)
      in.skip(20);				// 84-103(20)
      this.beginDate = in.readIntegerDate();	//104-107 (4)
      Date beginDate2 = in.readIntegerDate();	//108-111 (4)
      in.skip(4);				//112-115 (4)
      this.endDate = in.readIntegerDate();	//116-119 (4)
      in.skip(30);				//120-149(30)

      //if (type != TYPE)
      //System.err.println("XMaster Warning ("+this.symbol+
      //		   "): Expected "+TYPE+", found "+type);
      /// is one of the dates UTC instead of local time?
      //if (beginDate2.getTime() != this.beginDate.getTime())
      //System.err.println("XMaster Warning ("+this.symbol+
      //		   "): Expected beginDate "+this.beginDate+
      //		   " to be same as beginDate2 "+beginDate2);
      //if (endDate2.getTime() != this.endDate.getTime())
      //System.err.println("XMaster Warning ("+this.symbol+
      //		   "): Expected endDate "+this.endDate+
      //		   " to be same as endDate2 "+endDate2);
    }
    /** Return directory/Fn.MWD where n is file number. **/
    public File getFile(File directory) {
      String fileName = "F"+this.fileNumber+".MWD";
      return new File(directory, fileName);
    }
    public String getSymbol() { return this.symbol; }
    public String getName() { return this.name; }
    public Date getBeginDate() { return this.beginDate; }
    public Date getEndDate() { return this.endDate; } 
  }

  /** Header record in an Fn.DAT or Fn.MWD data file contains
      the record count.**/
  protected static class DataHeaderRecord {
    private final int recordCount;
    public DataHeaderRecord(MetastockDataInputStream in) throws IOException {
      // in = printRecord(in, 28);
      in.skip(2);				// 0-1  (2)
      this.recordCount = in.readUnsignedShort();// 2-3  (2)
      in.skip(24);				// 4-27(24)
    }
    /** Number of records in file.   Record count includes 
	header record, so it is always at least 1. **/
    public int getRecordCount() { return recordCount; }
  }

  /** Data records in Fn.DAT and Fn.MWD data files contain data for the stock
      on one date: open, high, low, and close prices, and trading volume,
      and optional 'openInterest'. **/
  protected static class DataItemRecord {
    final Date date;
    final float open, high, low, close, volume, openInterest;
    /** Parse 28-byte data record from in stream.<br>
     *  <pre>
     *   0-3  (4) date (proprietary float holding YYMMDD)
     *   4-7  (4) open price (proprietary float)
     *   8-11 (4) high price (proprietary float)
     *  12-15 (4) low  price (proprietary float)
     *  16-19 (4) close price (proprietary float)
     *  20-23 (4) volume shares traded (proprietary float)
     *  24-27 (4) open interest (proprietary float)
     *  </pre>**/
    public DataItemRecord(MetastockDataInputStream in) throws IOException {
      // in = printRecord(in, 28);
      this.date         = in.readFloatDate();   //  0-3  (4);
      this.open		= in.readFloat();	//  4-7  (4);
      this.high 	= in.readFloat();	//  8-11 (4);
      this.low  	= in.readFloat();	// 12-15 (4);
      this.close	= in.readFloat();	// 16-19 (4);
      this.volume	= in.readFloat();	// 20-23 (4);
      this.openInterest = in.readFloat();	// 24-27 (4);
    }
    /** Date for which these data were recorded. **/
    public Date getDate()       { return this.date; } 
    /** First traded price for stock on this date. **/
    public float getOpen()	{ return this.open;  }
    /** Highest traded price for stock on this date. **/
    public float getHigh()	{ return this.high;  }
    /** Lowest traded price for stock on this date. **/
    public float getLow() 	{ return this.low;   }
    /** Last traded price for stock on this date. **/
    public float getClose() 	{ return this.close; }
    /** Number of shares traded on this date. **/
    public float getVolume()	{ return this.volume;}
    /** optional field **/
    public float getOpenInterest() { return this.openInterest; }
  }
  private static final SimpleDateFormat DATE_FORMAT =
    new SimpleDateFormat("ddMMMyyyy");

  /** Return one of the following formats, depending on whether
      startOn and endBefore are null.
      <pre>
      MetastockParser()
      MetastockParser(1Jan2004<=dates)
      MetastockParser(dates<1Mar2004)
      MetastockParser(1Jan2004<=dates<1Mar2004)
      </pre>
   **/
  public String toString() {
    String name = this.getClass().getName();
    name = name.substring(name.lastIndexOf('.') + 1);
    StringBuffer buf = new StringBuffer(name);
    buf.append("(");
    if (this.startOn != null || this.endBefore != null) { 
      if (this.startOn != null)
	buf.append(DATE_FORMAT.format(this.startOn)).append("<=");
      buf.append("dates");
      if (this.endBefore != null)
	buf.append("<").append(DATE_FORMAT.format(this.endBefore));
    }	
    buf.append(")");
    return buf.toString();
  }

  /*
  // for debugging: in = printRecord(in, length);
  private static MetastockDataInputStream printRecord(MetastockDataInputStream
						      in, int length)
  throws IOException {
    byte[] data = new byte[length];
    int count = in.read(data, 0, length);
    System.err.print("data (len="+count+"): ");
    for (int i = 0; i < count; i++) {
      if (i % 20 > 0) System.err.print(" ");
      else System.err.print("\n"+i+": ");
      System.err.print(Integer.toString(data[i] & 0xff, 16));
    }
    System.err.println();
    return new MetastockDataInputStream(new ByteArrayInputStream(data));
  }
  */
}
