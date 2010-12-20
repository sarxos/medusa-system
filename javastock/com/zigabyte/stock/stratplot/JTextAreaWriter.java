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
package com.zigabyte.stock.stratplot;

import java.io.Writer;
import java.awt.EventQueue;
import java.awt.Rectangle;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

/** A Writer that appends text to a JTextArea.
    Typically wrapped in a PrintWriter(jtextAreaWriter, true), so
    writes only occur when the PrintWriter flushes.
    Writes are performed in the event dispatch thread so they
    will be synchronized with other user interface updates. **/
class JTextAreaWriter extends Writer  {
  // FIELD
  JTextArea textArea;
  // CONSTRUCTOR
  JTextAreaWriter(JTextArea textArea) {
    if (textArea == null)
      throw new NullPointerException();
    this.textArea = textArea;
  }
  // --- implement Writer ---
  public void write(char buffer[], int offset, int length) {
    append(new String(buffer).substring(offset, length));
  }
  public void write(String string) {
    append(string);
  }
  public void write(String string, int offset, int length) {
    append(string.substring(offset, length));
  }
  private void append(final String string) {
    EventQueue.invokeLater(new Runnable() {
	public void run() { 
	  textArea.append(string);
	}});
  }
  /** scrolls to end on flush **/
  public void flush() { // called by superclass constructor?
    if (this.textArea != null) { // if not closed.
      EventQueue.invokeLater(new Runnable() { 
	  public void run() { 
	    try { 
	      Rectangle lastLineStartRectangle = 
		textArea.modelToView(textArea.getLineStartOffset
				     (textArea.getLineCount()-1));
	      if (lastLineStartRectangle != null) // null during init
		textArea.scrollRectToVisible(lastLineStartRectangle);
	    } catch(BadLocationException err) {/** should not happen **/ }
	  }});
    }
  }
  public void close() { 
    if (this.textArea != null)
      flush();
    this.textArea = null;
  }
}

