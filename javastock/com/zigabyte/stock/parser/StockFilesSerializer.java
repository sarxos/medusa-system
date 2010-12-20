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
package com.zigabyte.stock.parser;

import com.zigabyte.stock.parser.metastock.MetastockParser;
import com.zigabyte.stock.data.*;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.zip.GZIPOutputStream;

/** Parses files in <em>DIR</em> using {@link MetastockParser}
    then using {@link ObjectOutputStream} writes to
    <em>DIR</em><code>.ser.gz</code> the serialized gzipped
    {@link StockMarketHistory}, or to <em>DIR</em><code>.ser</code>
    just serialized.

    @see #main
**/
public class StockFilesSerializer {
  /** Parses files in DIR using the parser then writes out the serialized
      StockMarketHistory to DIR.ser.gz using {@link ObjectOutputStream}.
      The parsed StockMarketHistory must be {@link Serializable}.

      @param parameters
      <pre>
         -Metastock <em>INFILE</em>
	 <em>[-OUTTYPE OUTFILE]</em>
         <em>[</em>-start-on <em>ddMMMyyyy]</em>
	 <em>[</em>-end-on <em>ddMMMyyyy]</em>
	 <em>[</em>-end-before <em>ddMMMyyyy]</em>
         <em>[</em>-use-metastock-index-dates<em>]</em>
      </pre>
      where <em>-OUTTYPE OUTFILE</em> is one of
      <pre>
         -serialize-to   <em>OUTFILE.ser</em>
         -serializegz-to <em>OUTFILE.ser.gz</em>
      </pre>
      If <code>-start-on</code> date provided,
      then omits data before date.<br>
      If <code>-end-on</code> date provided,
      then omits data after date.<br>
      If <code>-end-before</code> date provided,
      then omits data on or after date.<br>
      If <code>-use-metastock-index-dates</code> is present, skip
      parsing an individual stock file if the Metastock index
      indicates the file has a positive date range that does not
      overlap the desired range (startOn upto endBefore).  Otherwise,
      assume index may be incorrect and parse each stock file
      regardless of index dates.<p>

      If <code>-serialize-to</code>, then output is not compressed in
      gzip format, and output is written to
      <em>OUTFILE</em><code>.ser</code><br>
      If <code>-serializegz-to</code>, then output is compressed
      in gzip format, and output is written to
      <em>OUTFILE</em><code>.ser.gz</code><p>
      The <em>OUTFILE</em> is optional and defaults to
      <em>INFILE</em><code>.ser</code> or
      <em>INFILE</em><code>.ser.gz</code><br>
      
      (Concatenates path parameters with spaces, so ok to type path with
      single spaces in name on a command line shell that divides
      parameters at spaces).
  **/
  public static void main(String... parameters) throws IOException {
    int paramIndex = 0;
    parseParameters:
    try { 
      boolean useGZip = true;
      String outFileName = null;
      File inFile = null;
      Date start = null, end = null; // end is exclusive 
      boolean useMetastockIndexDates = false;
      try { 
	while (paramIndex < parameters.length) { 
	  String param = parameters[paramIndex].toLowerCase();
	  if ("-start-on".equals(param) || "-end-before".equals(param) ||
	      "-end-on".equals(param)) {
	    ++paramIndex;
	    Date date = DATE_FORMAT.parse(parameters[paramIndex].trim());
	    ++paramIndex; // increment if parse successful
	    if ("-start-on".equals(param))
	      start = date;
	    else if ("-end-before".equals(param))
	      end = date;
	    else if ("-end-on".equals(param)) {
	      Calendar calendar = new GregorianCalendar();
	      calendar.setTime(date);
	      calendar.add(Calendar.DAY_OF_MONTH, 1);
	      end = calendar.getTime();
	    }
	  } else if ("-serialize-to".equals(param) ||
		     "-serializegz-to".equals(param)) {
	    useGZip = "-serializegz-to".equals(param);
	    ++paramIndex;
	    StringBuffer buf = new StringBuffer();
	    int nextIndex = addUpToDash(buf, parameters, paramIndex);
	    if (buf.length() > 0) { 
	      outFileName = buf.toString();
	      File outDir = new File(outFileName).getParentFile();
	      if (outDir == null || outDir.exists()) // null means current dir
		paramIndex = nextIndex;
	      else throw new FileNotFoundException(outDir.getPath());
	    } // else if there is one input file use it to generate output file.
	  } else if ("-metastock".equals(param)) {
	    ++paramIndex;
	    StringBuffer buf = new StringBuffer();
	    int nextIndex = addUpToDash(buf, parameters, paramIndex);
	    if (buf.length() > 0) { 
	      inFile = new File(buf.toString());
	      if (inFile.exists())
		paramIndex = nextIndex;
	      else throw new FileNotFoundException(inFile.getPath());
	    } else throw new IllegalArgumentException("Expected parameter");
	  } else if ("-use-metastock-index-dates".equals(param)) {
	    ++paramIndex;
	    useMetastockIndexDates = true;
	  } else if ("-help".equals(param)) {
	    break parseParameters;
	  } else throw new IllegalArgumentException("Unrecognized option");
	}
	if (inFile == null)
	  throw new IllegalArgumentException("Missing infile");
	paramIndex++; // done parsing parameters
      } catch (ArrayIndexOutOfBoundsException e) {
	System.err.println("Expected parameter");
      } catch (IllegalArgumentException e) {
	System.err.println(e.getMessage());
      } catch (ParseException e) {
	System.err.println(e.getMessage());
      }
      if (paramIndex <= parameters.length) // did not reach 'done'
	parameterExit(parameters, paramIndex);

      StockMarketHistoryFactory parser =
	new MetastockParser(true, start, end, useMetastockIndexDates);

      // default output file name if null 
      String outSuffix = ".ser"+(useGZip? ".gz" : "");
      if (outFileName == null)
	outFileName = inFile.getPath()+outSuffix;
      else if (!outFileName.endsWith(outSuffix))
	outFileName += outSuffix;
      File outFile = new File(outFileName);

      System.out.println("Reading "+inFile);

      // Load the data, reporting how much memory is used
      long memoryBeforeLoad = occupiedMemory(); //gc's
      long timeBeforeLoad = System.currentTimeMillis();

      StockMarketHistory stockMarketHistory =
	parser.loadHistory(inFile);

      long loadTime = System.currentTimeMillis() - timeBeforeLoad;
      long dataMemory = occupiedMemory() - memoryBeforeLoad; // gc's
      System.out.println
	("Data occupies about "+NumberFormat.getInstance().format(dataMemory)+
	 " bytes in memory, took "+formatDuration(loadTime)+" to load.");

      // Store the data, reporting how much memory is used
      long timeBeforeStore = System.currentTimeMillis();

      ObjectOutputStream outStr =
	new ObjectOutputStream
	  (!useGZip ? new FileOutputStream(outFile) : 
	   new GZIPOutputStream(new FileOutputStream(outFile)));
      outStr.writeObject(stockMarketHistory);
      outStr.close();

      long storeTime = System.currentTimeMillis() - timeBeforeStore;
      System.out.println
	("wrote "+NumberFormat.getInstance().format(outFile.length())+
	 " bytes in "+formatDuration(storeTime)+"\nto "+outFile);
      return;
    } catch (FileNotFoundException e) {
      System.err.println(e);
    } catch (Throwable t) {
      t.printStackTrace();
    }
    parameterExit(parameters, paramIndex);
  }
  /** paramIndex must be < parameters.length.  Returns paramIndex
      at next parameter with dash, or at parameters.length. **/
  private static int addUpToDash(StringBuffer buf, 
				 String[] parameters, int paramIndex) {
    int initialIndex = paramIndex;
    for (; paramIndex < parameters.length &&
	   !parameters[paramIndex].startsWith("-"); paramIndex++) {
      if (paramIndex > initialIndex)
	buf.append(' ');
      buf.append(parameters[paramIndex]);
    }
    return paramIndex;
  }
  private static void parameterExit(String[] parameters, int paramIndex) {
    boolean isError = (paramIndex <= parameters.length && parameters.length > 0
		       && !parameters[0].equalsIgnoreCase("-help"));
    // parseParameters error: point to parameter where stopped
    if (isError) { 
      // echo parameters and mark parameter where parsed stopped
      int indent = 0;
      for (int i = 0; i < parameters.length; i++) {
	String param = parameters[i];
	System.err.print(param);
	System.err.print(' ');
	if (i < paramIndex)
	  indent += param.length() + 1;
      }
      System.err.println();
      for (int i = 0; i < indent; i++)
	System.err.print('_');
      System.err.println('^');
    }
    // show parameter help info
    System.err.println
      ("parameters:\n"+
       "  -metastock INFILE\n"+
       "  [-OUTTYPE [OUTFILE]]\n"+
       "  [-start-on ddMMMyyyy]    example: -start-on   1Feb2000\n"+
       "  [-end-on ddMMMyyyy]      example: -end-on    29Feb2000\n"+
       "  [-end-before ddMMMyyyy]  example: -end-before 1Mar2000\n"+
       "  [-use-metastock-index-dates]\n"+
       " where -OUTTYPE OUTFILE is one of\n"+
       "  -serialize-to   OUTFILE.ser\n"+
       "  -serializegz-to OUTFILE.ser.gz\n"+
       " The OUTFILE is optional and defaults\n"+
       " to INFILE + \".ser\" or INFILE + \".ser.gz\"\n"+
       " -end-on 29Feb2000 is same as -end-before 1Mar2000.");
    System.exit(isError? -1 : 0);
  }
  private static DateFormat DATE_FORMAT = new SimpleDateFormat("ddMMMyyyy");
  private static DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
  private static String formatDuration(long duration) {
    // effective for 0 <= duration < 24 hours.
    Calendar calendar = new GregorianCalendar();
    calendar.clear();
    calendar.add(Calendar.MILLISECOND, (int)duration + 500); // round to second
    return TIME_FORMAT.format(calendar.getTime());
  }
  /** Runs garbage collector,
      then calculate the amount of occupied memory (total - free). **/
  private static long occupiedMemory() {
    System.gc();
    Runtime runtime = Runtime.getRuntime();
    long totalMemory = runtime.totalMemory();
    long freeMemory = runtime.freeMemory();
    long occupiedMemory = totalMemory - freeMemory;
    return occupiedMemory;
  }
}
