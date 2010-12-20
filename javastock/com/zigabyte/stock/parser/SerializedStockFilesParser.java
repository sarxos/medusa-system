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

import com.zigabyte.stock.data.*;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

/** Loads a serialized {@link StockMarketHistory},
    optionally decompressing from GZip format first. **/
public class SerializedStockFilesParser implements StockMarketHistoryFactory {
  private final boolean useGZip;
  /** Create loader for (uncompressed) serialization files. **/
  public SerializedStockFilesParser() {
    this(false);
  }
  /** Create loader for serialization files.
      @param useGZip If true, uncompress files from GZip format
      before deserializing. **/
  public SerializedStockFilesParser(boolean useGZip) {
    this.useGZip = useGZip;
  }

  public StockMarketHistory loadHistory(File serializedFile) throws IOException{
    return loadHistory(new FileInputStream(serializedFile));
  }
  public StockMarketHistory loadHistory(InputStream in) throws IOException {
    ObjectInputStream ois = new ObjectInputStream(!useGZip ? in
						  : new GZIPInputStream(in));
    try {
      return (StockMarketHistory) ois.readObject();
    } catch (ClassNotFoundException e) {
      IOException e2 = new IOException(e.getMessage());
      e2.initCause(e);
      throw e2;
    } finally {
      ois.close();
    }
  }
}
