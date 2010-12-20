package com.mac.verec.utils ;

import java.awt.* ;
import java.lang.* ;
import java.util.* ;
import javax.swing.* ;

public class HostSystem {

private static	Boolean				onMac ;
private static	Boolean				onAqua ;

	public static boolean
	onMac() {
		if (onMac == null) {
			onMac = new Boolean(System.getProperty("mrj.version") != null) ;
		}
		return onMac.booleanValue() ;
	}

	public static boolean
	onAqua() {
		if (onAqua == null) {
			onAqua = new Boolean(onMac() && (
							UIManager.getSystemLookAndFeelClassName().equals(
								UIManager.getLookAndFeel().getClass().getName()))) ; // <- I love Lisp!
		}

		return onAqua.booleanValue() ;
	}

    private static final String[] kludgyMonthString = {
    	"Jan", "Feb", "Mar"
    ,	"Apr", "May", "Jun"
    ,	"Jul", "Aug", "Sep"
    ,	"Oct", "Nov", "Dec"
    } ;

    public static String
    dateToString(Date d) {
    	StringBuffer sb = new StringBuffer() ;
    	Calendar c = Calendar.getInstance() ;
		c.clear() ;
    	c.setTime(d) ;
    	sb.append(Integer.toString(c.get(Calendar.DATE))) ;
			sb.append("-") ;
    	sb.append(kludgyMonthString[c.get(Calendar.MONTH)]) ;
	    	sb.append("-") ;
	//	sb.append(Integer.toString(c.get(Calendar.YEAR)+1900)) ;
		sb.append(Integer.toString(c.get(Calendar.YEAR))) ;
		return sb.toString() ;
    }
    
    public static Point
    getScrollPosition(JScrollPane pane) {

		JViewport	port= pane.getViewport() ;
		
		return port.getViewPosition() ;
    }
    
    public static void
    setScrollPosition(JScrollPane pane, Point newPosition) {

		JViewport	port= pane.getViewport() ;
		
		Point		pos = new Point() ;
		Dimension	size= port.getViewSize() ;
		Dimension	ext = port.getExtentSize() ;

		pos.x = size.width - ext.width ;
		pos.y = size.height - ext.height ;

		if (newPosition.x < 0) newPosition.x = 0 ;
		if (newPosition.y < 0) newPosition.y = 0 ;
		if (newPosition.x > pos.x) newPosition.x = pos.x ;
		if (newPosition.x > pos.y) newPosition.y = pos.y ;

		port.setViewPosition(newPosition) ;
    }
    
    public static void
    scrollToBottom(JScrollPane pane) {
		JViewport	port= pane.getViewport() ;

		Point		pos = port.getViewPosition() ;
		Dimension	size= port.getViewSize() ;
		Dimension	ext = port.getExtentSize() ;

		pos.y = size.height - ext.height ;
		port.setViewPosition(pos) ;
    }
    
    public static void
    scrollToRight(JScrollPane pane) {
		JViewport	port= pane.getViewport() ;

		Point		pos = port.getViewPosition() ;
		Dimension	size= port.getViewSize() ;
		Dimension	ext = port.getExtentSize() ;

		pos.x = size.width - ext.width ;
		port.setViewPosition(pos) ;
    }

	public static class ViewWindow {
		public	int	xMin ;
		public	int	xMax ;
		public	int	yMin ;
		public	int	yMax ;
		
		public
		ViewWindow(
			int		xMin
		,	int		xMax
		,	int		yMin
		,	int		yMax) {
		
			this.xMin = xMin ;
			this.xMax = xMax ;
			this.yMin = yMin ;
			this.yMax = yMax ;
		}
	}

    public static ViewWindow
    getViewWindow(JScrollPane pane) {
		JViewport	port= pane.getViewport() ;

		Point		pos = port.getViewPosition() ;
		Dimension	ext = port.getExtentSize() ;
		
		return new ViewWindow(	pos.x  
							,	pos.x + ext.width
							,	pos.y
							,	pos.y + ext.height) ;
    }
}