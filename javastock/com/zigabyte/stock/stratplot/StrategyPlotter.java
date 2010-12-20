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

import com.zigabyte.stock.data.*;
import com.zigabyte.stock.dataset.*;
import com.zigabyte.stock.datasetofdata.*;
import com.zigabyte.stock.parser.*;
import com.zigabyte.stock.parser.metastock.*;
import com.zigabyte.stock.trade.*;
import com.zigabyte.stock.tradeobserver.*;
import com.zigabyte.stock.strategy.TradingStrategy;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.*;
import org.jfree.data.general.*;
import org.jfree.data.xy.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.Timer;

import java.lang.reflect.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.List;
import java.util.regex.*;


/** StrategyPlotter runs a trade simulation while showing plots and
    logs of progress.  Parameters may be changed by the user between
    runs: initial account values, account trade fees, start/end dates,
    and strategy parameters.  The trade strategy class is reloaded between
    runs without reloading the stock market data (so it may be
    changed and recompiled outside this program between runs).

    <p>Plots:
    <ul>
    <li>Plot of percentage change in total account value after each
        trading day, compared an index such as S&P500.
    <li>Plot of account cash, account stock value, and account total value
        after each trading day.
    </ul>
    <p>Logs:
    <ul>
    <li>Monthly log of account cash, account stock, and account total value.
    <li>Trade log showing each order executed or cancelled.
    </ul>
    <p>Report:  after simulation ends, reports on
    <ul>
    <li>final value of account cash, account stock, and total account value.,
    <li>stock positions remaining,
    <li>number of winning trades and their average profit,
        number of losing trades and their average loss
    <li>number of winning months and best month, number of losing months and
        worst month
    <li>'beta' against compared index such as S&P500
        (slope of least squares fit line correlating day to day change in
	 account value with day to day change in index)
    </ul>
    Sample report:
    <pre>
      Data:     /Trading Data/Stocks
      Dates:    01Jun2004 - 24Sep2004
      Strategy: SundayPeaks(0.2)

      Initial value:                          $10,000.00
	Cash Remaining:          $502.18
	Stock remaining:       $9,693.99
      Final value:                            $10,196.17
      Total Profit/Loss:                         $196.17

      Winning positions remaining:       5
	Average winning position profit:          $62.25

      Winning trade count:               3
	Average winning trade profit:             $62.39
      Losing trade count:                3
	Average losing trade loss:              -$100.75

      Winning Month Count:               2
	Best Month   (began 01Jun2004):          $607.24
      Losing Month Count:                2
	Worst Month  (began 01Jul2004):         -$340.87

      Beta vs. $SP:                      7.074
    </pre>

    Entry point: {@link #main main}

    @author written for Zigabyte by SakuraJ
    http://www.hotdispatch.com/officefronts/SakuraJ
 **/
public class StrategyPlotter extends JFrame {
  /** 100,000 **/
  public static double DEFAULT_INITIAL_CASH = 100000.00;
  /** 5.00 **/
  public static double DEFAULT_PER_TRADE_FEE = 5.00;
  /** 0.00 **/
  public static double DEFAULT_PER_SHARE_TRADE_COMMISSION = 0.00;
  /** com.zigabyte.stock.strategy.SundayPeaks(0.2) **/
  public static String DEFAULT_STRATEGY =
    "com.zigabyte.stock.strategy.SundayPeaks(0.2)";
  /** Metastock **/
  public static String DEFAULT_DATA_FORMAT = "Metastock";
  /** Current directory **/
  public static String DEFAULT_DATA_PATH = System.getProperty("user.dir");

  /** $SP, SPY.  After data loaded, compare Index symbol will default
      to first of these symbols found in data.  **/
  public static String[] COMPARE_INDEX_SYMBOLS = {"$SP","SPY"};

  /** Starts a StrategyPlotter.
   *  @param parameters
   *  <pre>
   *  Optional Parameters and default values:
   *   -initialCash $10,000.00
   *   -perTradeFee $1.00
   *   -perShareTradeCommission $0.02
   *   -strategy com.zigabyte.stock.strategy.SundayPeaks(0.2,0.08)
   *   -metastock (default path: current directory)
   *   -serialized (default path: current directory)
   *   -serializedgz (default path: current directory)
   *  Values may need to be quoted '$1' or 'pkg.MyStrategy(0.1)'.
   *  Use only one -metastock, -serialized, or -serializedgz to specify data.
   *  </pre>
   **/
  public static void main(String... parameters) {
    double initialCash = DEFAULT_INITIAL_CASH;
    double initialPerTradeFee = DEFAULT_PER_TRADE_FEE;
    double initialPerShareTradeCommission = DEFAULT_PER_SHARE_TRADE_COMMISSION;
    String initialStrategy = DEFAULT_STRATEGY;
    String initialDataFormat = DEFAULT_DATA_FORMAT;
    String initialDataPath = DEFAULT_DATA_PATH;

    // parse parameters
    int parameterIndex = 0;
    try { 
      for (;parameterIndex < parameters.length; parameterIndex++) {
	String parameter = parameters[parameterIndex];
	if ("-initialCash".equalsIgnoreCase(parameter) ||
	    "-perTradeFee".equalsIgnoreCase(parameter) ||
	    "-perShareTradeCommission".equalsIgnoreCase(parameter)) {
	  double value =
	    DOLLAR_FORMAT.parse(parameters[++parameterIndex]).doubleValue();
	  if ("-initialCash".equalsIgnoreCase(parameter)) {
	    initialCash = value;
	  } else if ("-perTradeFee".equalsIgnoreCase(parameter)) {
	    initialPerTradeFee = value;
	  } else if ("-perShareTradeCommission".equalsIgnoreCase(parameter)) {
	    initialPerShareTradeCommission = value;
	  } else assert false;
	} else if ("-strategy".equalsIgnoreCase(parameter) ||
		   "-metastock".equalsIgnoreCase(parameter) || 
		   "-serialized".equalsIgnoreCase(parameter) ||
		   "-serializedgz".equalsIgnoreCase(parameter)) {
	  StringBuffer buf = new StringBuffer();
	  String part;
	  while (++parameterIndex < parameters.length && 
		 !(part = parameters[parameterIndex]).startsWith("-")) {
	    if (buf.length() > 0)
	      buf.append(' ');
	    buf.append(part);
	  }
	  --parameterIndex;
	  String value = buf.toString();
	  if ("-strategy".equalsIgnoreCase(parameter)) {
	    initialStrategy = value;
	  } else if ("-metastock".equalsIgnoreCase(parameter)) {
	    initialDataPath = value;
	    initialDataFormat = "Metastock";
	  } else if ("-serialized".equalsIgnoreCase(parameter)) {
	    initialDataPath = value;
	    initialDataFormat = "Serialized";
	  } else if ("-serializedgz".equalsIgnoreCase(parameter)) {
	    initialDataPath = value;
	    initialDataFormat = "SerializedGZ";
	  } else assert false;
	} else if ("-help".equalsIgnoreCase(parameter)) {
	  parameterExit(0);
	} else throw new IllegalArgumentException(parameter);
      }
    } catch (Exception e) {
      System.err.println(e);
      int indent = 0;
      for (int i = 0; i < parameters.length; i++) {
	String parameter = parameters[i];
	if (i < parameterIndex)
	  indent += parameter.length() + 1;
	System.err.print(parameter);
	System.err.print(' ');
      }
      System.err.println();
      for (int i = 0; i < indent; i++)
	System.err.print('_');
      System.err.println('^');
      parameterExit(-1);
    }

    // set up plotter
    StrategyPlotter plotter =
      new StrategyPlotter(initialCash, initialPerTradeFee,
			  initialPerShareTradeCommission,
			  initialStrategy, initialDataFormat, initialDataPath);
    plotter.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    plotter.setSize(1024, 768);
    plotter.setLocationRelativeTo(null); // center on screen
    plotter.setVisible(true);
  }
  private static void parameterExit(int exitValue) {
    System.err.println
      ("Optional parameters and default values:\n"+
       " -initialCash "+DOLLAR_FORMAT.format(DEFAULT_INITIAL_CASH)+"\n"+
       " -perTradeFee "+DOLLAR_FORMAT.format(DEFAULT_PER_TRADE_FEE)+"\n"+
       " -perShareTradeCommission "+
       DOLLAR_FORMAT.format(DEFAULT_PER_SHARE_TRADE_COMMISSION)+"\n"+
       " -strategy "+DEFAULT_STRATEGY+"\n"+
       "Values may need to be quoted '$1' or 'pkg.MyStrategy(0.1)'.\n"+
       "Only one -Metastock, -Serialized, or -SerializedGZ specifies data.\n"+
       "If none given, default is current directory:\n"+
       " -"+DEFAULT_DATA_FORMAT+" "+DEFAULT_DATA_PATH);
    System.exit(exitValue);
  }

  // FIELDS 
  /** The loaded stock market data. **/
  StockMarketHistory histories = null;
  File loadedHistoriesFile = null;

  // UIFIELDS
  // Load Data File
  private final JButton loadFileButton = new JButton(new AbstractAction("LOAD"){
      public void actionPerformed(ActionEvent e) {
	runButton.setEnabled(false);
	viewDataButton.setEnabled(false);
	loadFileButton.setCursor(WAIT_CURSOR);
	loadFileInThread();
      }});
  final Cursor WAIT_CURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
  private static final String[] FILE_FORMATS = {
    "Metastock","Serialized","SerializedGZ"}; 
  private final JComboBox fileFormatCombo = new JComboBox(FILE_FORMATS); {
    this.fileFormatCombo.setEditable(false);
  }
  private final JTextField fileField = new JTextField(30); {
    fileField.getDocument().addDocumentListener(new DocumentListener() {
	public void insertUpdate(DocumentEvent e) { changedUpdate(e); }
	public void removeUpdate(DocumentEvent e) { changedUpdate(e); }	
	public void changedUpdate(DocumentEvent e) {
	  EventQueue.invokeLater(new Runnable() {
	      public void run() { 
		String name = fileField.getText();
		if (name.endsWith(".ser.gz"))
		  fileFormatCombo.setSelectedItem("SerializedGZ");
		else if (name.endsWith(".ser"))
		  fileFormatCombo.setSelectedItem("Serialized");
	      }});
	}});
  }
  private final JFileChooser fileChooser = new JFileChooser(); {
    fileChooser.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e){
	  File file = fileChooser.getSelectedFile();
	  if (file != null) { 
	    fileField.setText(file.getPath());
	  }
	}});
  }
  private final JButton fileBrowseButton =
    new JButton(new AbstractAction("Browse") {
	public void actionPerformed(ActionEvent e) {
	  String filePath = fileField.getText().trim();
	  if (filePath.length() > 0) { 
	    File file = new File(filePath);
	    while(!file.exists()) { 
	      file = file.getParentFile();
	    }
	    fileChooser.setCurrentDirectory(file);
	    fileChooser.setSelectedFile(file);
	  }
	  fileChooser.showOpenDialog(StrategyPlotter.this);
	}});
  private final JButton viewDataButton = new JButton(new AbstractAction("View"){
      public void actionPerformed(ActionEvent e) { viewData(); }}); {
    viewDataButton.setEnabled(false);
  }


  // Trading Account 
  private static final NumberFormat DOLLAR_FORMAT =
    new DecimalFormat("$#,##0.00");
  private final JFormattedTextField initialCashField =
    new JFormattedTextField(DOLLAR_FORMAT); {
    this.initialCashField.setColumns(10);
  }
  private final JFormattedTextField perTradeFeeField =
    new JFormattedTextField(DOLLAR_FORMAT);{
    this.perTradeFeeField.setColumns(5);
  }
  private final JFormattedTextField perShareTradeCommissionField =
    new JFormattedTextField(DOLLAR_FORMAT);{
    this.perShareTradeCommissionField.setColumns(5);
  }

  // Simulation Run
  private final JButton runButton = new JButton(new AbstractAction("RUN  ") {
      public void actionPerformed(ActionEvent e){
	runButton.setCursor(WAIT_CURSOR);
	runSimulationInThread();
      }});{
    this.runButton.setEnabled(false);
  }
  private static final DateFormat dateFormat =
    new SimpleDateFormat("ddMMMyyyy");
  private final JFormattedTextField startDateField =
    new JFormattedTextField(dateFormat); {
    this.startDateField.setColumns(9);
  }
  private final JFormattedTextField endDateField =
    new JFormattedTextField(dateFormat); {
    this.endDateField.setColumns(9);
  }
  private final JTextField strategyField = new JTextField(30);
  private final JTextField compareIndexSymbolField = new JTextField(); {
    this.compareIndexSymbolField.setColumns(8);
  }
  private final JButton editButton = new JButton("Edit"); {
    editButton.setToolTipText("Not Implemented");
    editButton.setEnabled(false);
  }
  private final JButton compileButton = new JButton("Compile"); {
    compileButton.setToolTipText("Not Implemented");
    compileButton.setEnabled(false);
  }
  
  // reports, logs, and chart
  private static final Font MONOSPACED = Font.decode("Monospaced");
  private final JSplitPane vSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
  private final JTextArea reportArea = new JTextArea(); {
    reportArea.setFont(MONOSPACED);
  }
  private final JTextArea tradeLogArea = new JTextArea();{
    tradeLogArea.setFont(MONOSPACED);
  }
  private final JTextArea monthlyLogArea = new JTextArea();{
    monthlyLogArea.setFont(MONOSPACED);
  }
  private final ChartPanel chartPanel = new ChartPanel(null);

  //// CONSTRUCTOR
  /** Create a Simulation plotter initialized with default values. **/
  public StrategyPlotter() {
    this(DEFAULT_INITIAL_CASH,
	 DEFAULT_PER_TRADE_FEE, DEFAULT_PER_SHARE_TRADE_COMMISSION,
	 DEFAULT_STRATEGY, DEFAULT_DATA_FORMAT, DEFAULT_DATA_PATH);
  }
  /** Create a Simulation plotter with fields initialized with
      given values.
      @param initialCash initial value for account initial cash field 
      @param initialPerTradeFee initial value for account per trade fee field
      @param initialPerShareTradeCommission
      initial value for account per share trade commission field
      @param initialStrategy initial value for strategy field, formatted
      as <code>pkg.ClassName(0.2,true)</code> for a strategy class
      <code>ClassName</code> in package <code>pkg</code> with a 
      a constructor with parameters <code>ClassName(double d, boolean b)</code>.
      @param initialDataFormat initial value for format field,
      must be one of "Metastock", "Serialized", or "SerializedGZ".
      @param initialDataPath initial value for stock data file field.
  **/
  public StrategyPlotter(double initialCash, double initialPerTradeFee,
			 double initialPerShareTradeCommission,
			 String initialStrategy,
			 String initialDataFormat, String initialDataPath) {
    super("StrategyPlotter");
    // account
    this.initialCashField.setValue(initialCash);
    this.perTradeFeeField.setValue(initialPerTradeFee);
    this.perShareTradeCommissionField.setValue(initialPerShareTradeCommission);
    // simulation
    this.strategyField.setText(initialStrategy);
    // data
    this.fileFormatCombo.setSelectedItem(initialDataFormat);//ignored if unknown
    this.fileField.setText(initialDataPath);

    initLayout();
  }

  private void initLayout() {
    Container content = this.getContentPane();
    JComponent controls = Box.createVerticalBox(); {
      JPanel filePanel = new JPanel(new BorderLayout()); {
	filePanel.setBorder(BorderFactory.createTitledBorder
			    ("Stock Market Trading Data"));
	JPanel dataButtons = new JPanel(new GridLayout(2, 1)); {
	  dataButtons.add(this.loadFileButton);
	  dataButtons.add(this.viewDataButton);
	} filePanel.add(dataButtons, BorderLayout.EAST);
	JPanel fileControls = new JPanel(new FlowLayout()); { 
	  fileControls.add(new JLabel("Stock data File:"));
	  fileControls.add(this.fileField);
	  fileControls.add(this.fileBrowseButton);
	  fileControls.add(new JLabel("  Stock data format:"));
	  fileControls.add(this.fileFormatCombo);
	} filePanel.add(fileControls, BorderLayout.CENTER);
      } controls.add(filePanel);
      JComponent simPanel = new JPanel(new BorderLayout()); { 
	simPanel.setBorder(BorderFactory.createTitledBorder("Simulation Run"));
	simPanel.add(this.runButton, BorderLayout.EAST);
	JComponent simControlsPanel = Box.createVerticalBox(); { 
	  JPanel accountControls = new JPanel(new FlowLayout()); {
	    accountControls.add(new JLabel("Initial Cash:"));
	    accountControls.add(this.initialCashField);
	    accountControls.add(new JLabel("  Per Trade Fee:"));
	    accountControls.add(this.perTradeFeeField);
	    accountControls.add(new JLabel("  Per Share Trade Commission:"));
	    accountControls.add(this.perShareTradeCommissionField);
	    accountControls.add(new JLabel("  Compare:"));
	    accountControls.add(this.compareIndexSymbolField);
	  } simControlsPanel.add(accountControls);
	  JPanel runControls = new JPanel(new FlowLayout()); { 
	    runControls.add(this.strategyField);
	    runControls.add(new JLabel("Start Date:"));
	    runControls.add(this.startDateField);
	    runControls.add(new JLabel("  End Date:"));
	    runControls.add(this.endDateField);
	    runControls.add(new JLabel("  Strategy:"));
	    runControls.add(this.strategyField);
	    runControls.add(this.editButton);
	    runControls.add(this.compileButton);	    
	  } simControlsPanel.add(runControls);
	} simPanel.add(simControlsPanel, BorderLayout.CENTER);
      } controls.add(simPanel);
    } content.add(controls, BorderLayout.NORTH);
    JSplitPane vSplit = this.vSplit; {
      vSplit.setResizeWeight(0.5);
      vSplit.add(this.chartPanel, JSplitPane.TOP);
      JSplitPane hSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); {
	hSplit.setResizeWeight(0.5); // enough for no hscrollbar in 1024 width
	JSplitPane reportSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT); {
	  reportSplit.setResizeWeight(0.5);
	  JScrollPane reportScroll = new JScrollPane(this.reportArea); {
	    reportScroll.setBorder
	      (BorderFactory.createTitledBorder("Report"));
	  } reportSplit.add(reportScroll, JSplitPane.TOP);
	  JScrollPane monthScroll = new JScrollPane(this.monthlyLogArea); {
	    monthScroll.setBorder
	      (BorderFactory.createTitledBorder("Monthly Log"));
	  } reportSplit.add(monthScroll, JSplitPane.BOTTOM);
	} hSplit.add(reportSplit, JSplitPane.LEFT);
	JScrollPane tradeScroll = new JScrollPane(this.tradeLogArea); {
	  tradeScroll.setBorder
	    (BorderFactory.createTitledBorder("Trade Log"));
	} hSplit.add(tradeScroll, JSplitPane.RIGHT);
      } vSplit.add(hSplit, JSplitPane.BOTTOM);
    } content.add(vSplit, BorderLayout.CENTER);
  }
  /** Sets title to "title [Strategy Plotter]" **/
  public void setTitle(String title) {
    String className = this.getClass().getName();
    String shortName = className.substring(className.lastIndexOf('.')+1);
    super.setTitle(title == null || title.length() == 0
		   ? shortName
		   : title+" ["+shortName+"]");
  }

  protected void viewData() {
    if (this.histories == null)
      return;
    try { 
      JFrame viewer =
	new StockMarketHistoryViewer(this.loadedHistoriesFile.getPath(),
				     this.histories);
      viewer.setSize(1024,768);
      viewer.setLocationRelativeTo(this);
      viewer.setVisible(true);
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  /** Loads file in separate thread.  Called from load button,
      allows the UI to continue to update while load in progress. **/
  protected void loadFileInThread() {
    Thread thread = new Thread() {
	public void run() {
	  loadFile();
	}};
    thread.start();
  }
  /** Load stock data specified in file field.  If the data is from a
      serialized file, uses a ProgressMonitorInputStream to show
      progress.  Once data is loaded, sets compareHistory to first
      symbol in COMPARE_INDEX_SYMBOLS found in data, and sets
      begin/end dates to the begin/end dates of this symbol, or
      of the first history in the data if no compare symbol was found.
      Shows a dialog when either completed or an error occurs.
  **/
  protected void loadFile() { 
    try { 
      // get file
      String filePath = this.fileField.getText().trim();
      if (filePath.length() == 0)
	throw new IllegalArgumentException("Empty file");
      File file = new File(filePath);

      // choose parser
      StockMarketHistoryFactory parser; { 
	String fileFormat = (String) this.fileFormatCombo.getSelectedItem();
	if ("Metastock".equals(fileFormat))
	  parser = new MetastockParser(true);
	else if ("Serialized".equals(fileFormat))
	  parser = new SerializedStockFilesParser(false);
	else if ("SerializedGZ".equals(fileFormat))
	  parser = new SerializedStockFilesParser(true);
	else throw new IllegalArgumentException
	       ("Unrecognized file format: "+fileFormat);
      }
      // load data using parser
      EventQueue.invokeLater(new Runnable() {
	  public void run() { 
	    runButton.setEnabled(false);
	    viewDataButton.setEnabled(false);
	  }});
      this.histories = null; // allow gc
      this.loadedHistoriesFile = null;
      if (!(parser instanceof SerializedStockFilesParser)) {
	this.histories = parser.loadHistory(file);
      } else {
	// use ProgressMonitorInputStream
	SerializedStockFilesParser serializedParser =
	  (SerializedStockFilesParser) parser;
	InputStream in = new FileInputStream(file);
	ProgressMonitorInputStream pmIn =
	  new ProgressMonitorInputStream(this, file.getName(), in);
	this.histories = serializedParser.loadHistory(pmIn);
	pmIn.close();
      }
      this.loadedHistoriesFile = file;
      if (histories.size() > 0) {

	// initialize compareSymbol
	String compareIndexSymbol = "";
	for (String spSymbol : COMPARE_INDEX_SYMBOLS) { 
	  if (histories.get(spSymbol) != null) {
	    compareIndexSymbol = spSymbol;
	    break;
	  }
	}
	this.compareIndexSymbolField.setText(compareIndexSymbol);

	// initialize start/end fields
	StockHistory sampleHistory = (compareIndexSymbol.length() > 0
				      ? histories.get(compareIndexSymbol)
				      : histories.get(0));
	if (sampleHistory.size() > 0) {
	  this.startDateField.setValue(sampleHistory.get(0).getDate());
	  this.endDateField.setValue
	    (sampleHistory.get(sampleHistory.size() - 1).getDate());
	}
      }
      // display completed dialog
      String msg = "Loaded "+this.histories.size()+" histories";
      JOptionPane.showMessageDialog(this, msg, "Load complete",
				    JOptionPane.INFORMATION_MESSAGE);
    } catch (FileNotFoundException e) {
      showErrorDialog(e, false);
    } catch (Exception e) {
      showErrorDialog(e);
    } finally {
      EventQueue.invokeLater(new Runnable() {
	  public void run() { 
	    runButton.setEnabled(histories != null);
	    viewDataButton.setEnabled(histories != null);
	    loadFileButton.setCursor(null);  // clear wait cursor
	  }});
    }
  }

  /** Loads strategy class and creates instance by calling constructor.
      @param strategyText contains full class name followed by
      parameter list for constructor.  Constructor parameters may be
      int, double, boolean, String.  Constructor parameters are parsed by
      splitting on commas and trimming whitespace.
      <pre>
      mypkg.MyStrategy(12, -345.67, true, false, Alpha Strategy)
      </pre>
  **/
  protected TradingStrategy loadStrategy(String strategyText) 
  throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
	 ExceptionInInitializerError, InstantiationException,
	 InvocationTargetException
  { 
    Pattern strategyPattern =   // matches full.class.Name(args...)
      Pattern.compile("^([A-Za-z](?:[A-Za-z0-9_.]*))\\s*[(](.*)[)]$");
    Matcher matcher = strategyPattern.matcher(strategyText);
    if (!matcher.matches())
      throw new IllegalArgumentException
	("Bad Strategy: "+strategyText+"\n"+
	 "Expected: full.class.name(-123.45, 67, true, false)");

    final String strategyClassName = matcher.group(1).trim();
    String parameters[] = matcher.group(2).split(",");

    // clean parameters
    for (int i = 0; i < parameters.length; i++)
      parameters[i] = parameters[i].trim();
    if (parameters.length == 1 && parameters[0].length() == 0)
      parameters = new String[]{}; // 0 parameters

    // build classpath
    String[] classPath =
      System.getProperty("java.class.path").split(File.pathSeparator);
    ArrayList<URL> classPathURLs = new ArrayList<URL>();
    for (int i = 0; i < classPath.length; i++) {
      String path = classPath[i];
      if (".".equals(path))
	path = System.getProperty("user.dir");
      path = path.replace(File.separatorChar, '/');
      if (!path.endsWith("/") && !path.endsWith(".jar"))
	path += "/";
      try { 
	classPathURLs.add(new File(path).toURL());
      } catch (MalformedURLException e) {
	// bad directory in class path, skip
      }
    }
    final String strategyPackagePrefix =
      strategyClassName.substring
      (0, Math.max(0, strategyClassName.lastIndexOf('.') + 1));
    ClassLoader loader = new URLClassLoader(classPathURLs.toArray(new URL[]{}),
					    this.getClass().getClassLoader()){
	/** Don't search parent for classes with strategyPackagePrefix.
	    Exception: interface TradingStrategy **/
	protected Class<?> loadClass(String className, boolean resolve)
	throws ClassNotFoundException
	{
	  Class<?> loadedClass = findLoadedClass(className);
	  if (loadedClass != null)
	    return loadedClass;
	  if (!className.startsWith(strategyPackagePrefix) ||
	      className.equals(TradingStrategy.class.getName())) {
	    loadedClass = this.getParent().loadClass(className);
	    if (loadedClass != null)
	      return loadedClass;
	  }
	  loadedClass = findClass(className);
	  if (loadedClass != null) {
	    if (resolve) 
	      resolveClass(loadedClass);
	    return loadedClass;
	  } else throw new ClassNotFoundException(className);
	}
      };
    // load class.  Throws ClassNotFoundException if not found.
    Class<?> strategyClass = loader.loadClass(strategyClassName);

    // Make sure it is a TradingStrategy.
    if (!TradingStrategy.class.isAssignableFrom(strategyClass))
      throw new ClassCastException(strategyClass.getName()+
				   " does not implement TradingStrategy");

    // Find constructor compatible with parameters
    Constructor[] constructors = strategyClass.getConstructors();
    findConstructor:
    for (Constructor constructor : constructors) {
      Class<?>[] parameterTypes = constructor.getParameterTypes();
      if (parameterTypes.length != parameters.length)
	continue;
      Object[] values = new Object[parameterTypes.length];
      for (int i = 0; i < parameterTypes.length; i++) {
	if (boolean.class.equals(parameterTypes[i])) {
	  String parameter = parameters[i].toLowerCase();
	  if ("false".equals(parameter))
	    values[i] = Boolean.FALSE;
	  else if ("true".equals(parameter))
	    values[i] = Boolean.TRUE;
	  else continue findConstructor;
	} else if (int.class.equals(parameterTypes[i])) {
	  try { values[i] = new Integer(parameters[i]); } 
	  catch (NumberFormatException e) { continue findConstructor; }
	} else if (double.class.equals(parameterTypes[i])) {
	  try { values[i] = new Double(parameters[i]); }
	  catch (NumberFormatException e) { continue findConstructor; }
	} else if (String.class.equals(parameterTypes[i])) {
	  values[i] = parameters[i];
	} else continue findConstructor; // unsupported parameter type, skip
      }
      // all values matched types, so create instance
      return (TradingStrategy) constructor.newInstance(values);
    }
    throw new NoSuchMethodException(strategyText);
  }

  /** Runs the simuation in a separate thread.
      Called from the run button, uses separate thread so UI
      can continue to update. **/
  protected void runSimulationInThread() {
    Thread thread = new Thread() {
	public void run() { 
	  try {
	    if (StrategyPlotter.this.histories == null)
	      throw new NullPointerException("No market data loaded");
	    runSimulation();
	  } catch (Throwable t) {
	    showErrorDialog(t);
	  } finally {
	    EventQueue.invokeLater(new Runnable() {
		public void run() { 
		  runButton.setCursor(null); // clear wait cursor
		}});
	  }
	}};
    thread.start();
  }

  /** Runs simulation with plots and logs showing progress, then
      produces report when completed. **/
  protected void runSimulation() throws Exception {
    // reset outputs
    this.chartPanel.setChart(null);
    this.reportArea.setText("");
    this.monthlyLogArea.setText("");
    this.tradeLogArea.setText("");

    // create Account
    final double initialCash =
      ((Number) this.initialCashField.getValue()).doubleValue();
    final double perTradeFee =
      ((Number) this.perTradeFeeField.getValue()).doubleValue();
    final double perShareTradeCommission =
      ((Number) this.perShareTradeCommissionField.getValue()).doubleValue();
    final DefaultTradingAccount account =
      new DefaultTradingAccount(this.histories,
				perTradeFee, perShareTradeCommission);

    // add observers
    account.addTradeObserver
      (new TradeTraceObserver
       (true, new PrintWriter(new JTextAreaWriter(this.tradeLogArea), true)));
    account.addTradeObserver
      (new PeriodTraceObserver
       (1, Calendar.MONTH, true,
	new PrintWriter(new JTextAreaWriter(this.monthlyLogArea), true)));

    final BalanceHistoryObserver balanceObserver = new BalanceHistoryObserver();
    account.addTradeObserver(balanceObserver);

    final TradeWinLossObserver winLossObserver = new TradeWinLossObserver();
    account.addTradeObserver(winLossObserver);

    final PeriodWinLossObserver monthObserver =
      new PeriodWinLossObserver(1, Calendar.MONTH, true);
    account.addTradeObserver(monthObserver);

    final String betaCompareIndexSymbol =
      this.compareIndexSymbolField.getText();
    final boolean hasBetaIndex =(histories.get(betaCompareIndexSymbol) != null);
    BetaObserver betaObserver = null;
    if (hasBetaIndex) {
      betaObserver = new BetaObserver(betaCompareIndexSymbol);
      account.addTradeObserver(betaObserver);
    } 

    // create strategy
    final String strategyText = this.strategyField.getText().trim();
    final TradingStrategy strategy = loadStrategy(strategyText);
    this.setTitle(strategy.toString());

    // plot with timer update 
    final BalanceHistoryXYDataset accountDataset =
      new BalanceHistoryXYDataset(balanceObserver);
    final Date startDate = (Date) this.startDateField.getValue();
    final Date endDate = (Date) this.endDateField.getValue();
    final ValueAxis[] yAxes = 
      plotAccountHistory(accountDataset, strategy.toString(),
			 startDate, endDate);
    
    final ActionListener plotUpdater = new PlotUpdater(accountDataset, yAxes);
    final Timer refreshTimer = new Timer(1000, plotUpdater);// 1000msec cycle
    refreshTimer.start();

    // run simulation
    account.initialize(startDate, initialCash);
    final DefaultTradingSimulator simulator =
      new DefaultTradingSimulator(histories);
    simulator.runStrategy(strategy, account, startDate, endDate);
      
    // stop plot timer
    refreshTimer.stop();
    plotUpdater.actionPerformed(null); // one last time.

    // report
    final StringWriter reportWriter = new StringWriter();
    final PrintWriter out = new PrintWriter(reportWriter, true);
    reportSource(out, this.fileField.getText(), startDate, endDate, strategy);
    out.println();
    reportValues(out, initialCash, account);
    if (account.getStockPositionCount() > 0) { 
      out.println();
      reportPositions(out, account);
    }
    out.println();
    reportTrades(out, winLossObserver);
    out.println();
    reportMonths(out, monthObserver);
    out.println();
    reportBeta(out, betaObserver, betaCompareIndexSymbol);

    // display report
    this.reportArea.setText(reportWriter.toString());
    this.reportArea.setCaretPosition(0);
    this.vSplit.resetToPreferredSizes();
  }
  /** Reports datafile, dates, and strategy to out. **/
  protected void reportSource(PrintWriter out, String dataFile,
			      Date startDate, Date endDate,
			      TradingStrategy strategy) { 
    out.println("Data:     "+dataFile);
    out.println("Dates:    "+
		dateFormat.format(startDate)+" - "+dateFormat.format(endDate));
    out.println("Strategy: "+ strategy);
  }
  /** Reports initial cash, cash remaining, stock remaining, final value, and
      total profit/loss to out.**/
  protected void reportValues(PrintWriter out, double initialCash,
			      TradingAccount account) { 
    // report value
    out.println("Initial value:                     "+
		formatDollars(initialCash));
    out.println("  Cash Remaining:  "+
		formatDollars(account.getCurrentCashBalance()));
    out.println("  Stock remaining: "+
		formatDollars(account.getCurrentStockValue()));
    out.println("Final value:                       "+
		formatDollars(account.getCurrentAccountValue()));
    out.println("Total Profit/Loss:                 "+
		formatDollars(account.getCurrentAccountValue()
			      - initialCash));
  }
  /** Reports winning stock position count and average winning position profit,
      losing stock position count and average losing position loss, and
      even stock position count.   Each reported only if nonzero. **/
  protected void reportPositions(PrintWriter out, TradingAccount account) {
    // report remaining positions
    // collect winning and losing positions
    List<StockPosition> winningPositions = new ArrayList<StockPosition>();
    List<StockPosition> losingPositions = new ArrayList<StockPosition>();
    List<StockPosition> evenPositions = new ArrayList<StockPosition>();
    double winningPositionsProfit = 0, losingPositionsLoss = 0;
    for (StockPosition position : account) {
      double totalCurrentValue = account.getCurrentStockValue(position);
      double totalCostBasis = position.getCostBasis() * position.getShares();
      double projectedProfitOrLoss = totalCurrentValue - totalCostBasis;
      if (projectedProfitOrLoss > 0) {
	winningPositions.add(position);
	winningPositionsProfit += projectedProfitOrLoss;
      } else if (projectedProfitOrLoss < 0) { 
	losingPositions.add(position);
	losingPositionsLoss += projectedProfitOrLoss;
      } else if (projectedProfitOrLoss == 0) {
	evenPositions.add(position);
      } else {// in case NaN data somewhere
	System.err.println("Position "+position.getSymbol() + ": "+
			   "value="+totalCurrentValue+", "+
			   "cost="+totalCostBasis);
      }
    }
    if (winningPositions.size() > 0) {
      out.println("Winning positions remaining:       "+
		  winningPositions.size());
      out.println("  Average winning position profit: "+
		  formatDollars(winningPositionsProfit
				/winningPositions.size()));

    }
    if (losingPositions.size() > 0) {
      out.println("Losing positions remaining:        "+
		  losingPositions.size());
      out.println("  Average losing position loss:    "+
		  formatDollars(losingPositionsLoss
				/losingPositions.size()));

    }
    if (evenPositions.size() > 0) {
      out.println("Even position remaining:           "+
		  evenPositions.size());
    }
  }
  /** Reports winning trade count and average winning trade profit,
      losing trade count and average losing trade loss, and
      even trade count (if nonzero). **/
  protected void reportTrades(PrintWriter out,
			      TradeWinLossObserver winLossObserver) { 
    // report trades
    out.println("Winning trade count:               "+
		winLossObserver.getWinningTradeCount());
    out.println("  Average winning trade profit:    "+
		formatDollars(winLossObserver.getAverageWinningTradeProfit()));
    out.println("Losing trade count:                "+
		winLossObserver.getLosingTradeCount());
    out.println("  Average losing trade loss:       "+
		formatDollars(winLossObserver.getAverageLosingTradeLoss()));
    if (winLossObserver.getEvenTradeCount() > 0)
      out.println("Even trade count:                  "+
		  winLossObserver.getEvenTradeCount());
  }
  /* Reports winning month count and best month profit, losing month
     count and worst month loss.  If no winning months, produces "(No
     Profitiable Months)", if no losing months, produces "(No Losing
     Months)". **/
  protected void reportMonths(PrintWriter out,
			      PeriodWinLossObserver monthObserver) { 
    if (monthObserver.getBestProfitPeriodStartDate() != null) { 
      out.println("Winning Month Count:               "+
		  monthObserver.getWinningPeriodCount());
      out.println("  Best Month   (began "+
		  dateFormat.format
		  (monthObserver.getBestProfitPeriodStartDate())+"):  "+
		  formatDollars(monthObserver.getBestPeriodProfit()));
    } else {
      out.println("(No Profitable months)");
    }
    if (monthObserver.getWorstLossPeriodStartDate() != null) { 
      out.println("Losing Month Count:                "+
		  monthObserver.getLosingPeriodCount());
      out.println("  Worst Month  (began "+
		  dateFormat.format
		  (monthObserver.getWorstLossPeriodStartDate())+"):  "+
		  formatDollars(monthObserver.getWorstPeriodLoss()));
    } else {
      out.println("(No Losing months)");
    }
  }
  /** Reports "Beta vs. SYMBOL: " with symbol of compare index and
      value of beta for this run.  **/
  protected void reportBeta(PrintWriter out, BetaObserver betaObserver,
			    String betaCompareIndexSymbol) {
    NumberFormat betaFormat = new DecimalFormat("#0.000");
    if (betaObserver != null) 
      out.println("Beta vs. "+betaCompareIndexSymbol+":                      "+
		  betaFormat.format(betaObserver.computeBeta()));
    else
      out.println("(Beta: No "+betaCompareIndexSymbol+" in data.)");
  }
  private static StringBuffer formatDollars(double d) {
    return fillWidth(-15, DOLLAR_FORMAT.format(d));
  }
  private static StringBuffer fillWidth(int width, String s) {
    StringBuffer buf = new StringBuffer();
    if (width < 0) { 
      width += s.length();
      while (width++ < 0)
	buf.append(' ');
      buf.append(s);
    } else {
      buf.append(s);
      width -= s.length();
      while (width-- > 0)
	buf.append(' ');
    }
    return buf;
  }

  /** Build combined chart containing the account cash/stocks/total
      values chart over dates on bottom and the percentage change
      over dates chart on top. 
      @return y-axes that need to be re-ranged when data changes. **/
  private NumberAxis[] plotAccountHistory(XYDataset accountData, String title,
					  Date startDate, Date endDate) { 
    final DateAxis dateAxis = new DateAxis();
    dateAxis.setRange(startDate, endDate);
    final NumberAxis percentAxis = new NumberAxis("% change"); 
    percentAxis.setAutoRangeIncludesZero(false);
    final NumberAxis priceAxis = new NumberAxis("US$"); 
    priceAxis.setAutoRangeIncludesZero(true);

    boolean useShapes = // use shapes if 3 months or less
      (endDate.getTime() - startDate.getTime() < 93*24*60*60*1000L);
    XYLineAndShapeRenderer accountRenderer = new XYLineAndShapeRenderer();
    accountRenderer.setShapesVisible(useShapes);
    XYPlot accountPlot = new XYPlot(accountData,
				    dateAxis, priceAxis, accountRenderer);
    // compare only total percent data to zoom in on its fluctuations
    // for comparison with the compare index such as S&P500 index.
    // Stock value starts at zero, and cash value becomes close to zero,
    // so they fluctuation widely, so leave them out.
    XYDataset accountTotalPercentData =
      new XYDatasetPercentChangeAdapter
      (new SubSeriesDataset(accountData,
			    BalanceHistoryXYDataset.TOTAL_SERIES));
    XYLineAndShapeRenderer compareRenderer = new XYLineAndShapeRenderer();
    compareRenderer.setShapesVisible(useShapes);
    XYPlot comparePlot = new XYPlot(accountTotalPercentData,
				    dateAxis, percentAxis, compareRenderer);

    String compareIndexSymbol = this.compareIndexSymbolField.getText();
    StockHistory compareHistory = this.histories.get(compareIndexSymbol);
    if (compareHistory != null) {
      XYDataset comparePercentData =
	new OHLCDatasetPercentChangeAdapter
	(new OHLCDatasetSubdomainAdapter
	 (new OHLCDatasetOfStockHistory(compareHistory), startDate, endDate));
      int compareIndex = 1;
      comparePlot.setDataset(compareIndex, comparePercentData);
      XYLineAndShapeRenderer percentRenderer = new XYLineAndShapeRenderer();
      percentRenderer.setShapesVisible(useShapes);
      comparePlot.setRenderer(compareIndex, percentRenderer);
    }
    // share date axis
    CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot(dateAxis);
    combinedPlot.add(comparePlot, 1);
    combinedPlot.add(accountPlot, 1);

    this.chartPanel.setChart(new JFreeChart(title, null, combinedPlot, true));
    return new NumberAxis[]{priceAxis, percentAxis};
  }

  /** Display t in error dialog, dumping stack **/
  protected void showErrorDialog(Throwable t) {
    showErrorDialog(t, true);
  }
  /** Display t in error dialog, optionally dump stack.  **/
  protected void showErrorDialog(Throwable t, boolean dumpStack) {
    if (dumpStack) { 
      while (t.getCause() != null)
	t = t.getCause();
      t.printStackTrace();
    }
    String name = t.getClass().getName();
    name = name.substring(name.lastIndexOf('.') + 1);
    JOptionPane.showMessageDialog(this, t.getMessage(), name,
				  JOptionPane.ERROR_MESSAGE);
  }


  /** Adapter for JFreeChart to access data in a stock history. **/
  private static class BalanceHistoryXYDataset extends AbstractXYDataset {
    BalanceHistoryObserver balanceObserver;
    ValueAxis[] yAxes;
    BalanceHistoryXYDataset(BalanceHistoryObserver balanceObserver) { 
      this.balanceObserver = balanceObserver;
    }
    public DomainOrder getDomainOrder() {
      return DomainOrder.ASCENDING;
    }
    public  static final int TOTAL_SERIES = 0;
    public  static final int STOCKS_SERIES = 1;
    public  static final int CASH_SERIES = 2;
    private static final String[] seriesNames = {
      "Account Total","Account Stocks","Account Cash"};
    public int getSeriesCount() {
      return seriesNames.length;
    } 
    public String getSeriesName(int series) {
      return seriesNames[series];
    }
    public int getItemCount(int series) {
      return this.balanceObserver.getDataPoints().size();
    }
    private BalanceHistoryObserver.DataPoint getItem(int item) {
      return this.balanceObserver.getDataPoints().get(item);
    }
    public double getXValue(int series, int item) {
      return getItem(item).getDate().getTime();
    }
    public Number getX(int series, int item) {
      return new Double(getXValue(series, item));
    }
    public double getYValue(int series, int item) {
      switch(series) {
      case 0: return getItem(item).getTotalValue();
      case 1: return getItem(item).getStockValue();
      case 2: return getItem(item).getCashBalance();
      default: throw new IllegalArgumentException(String.valueOf(series));
      }
    }
    public Number getY(int series, int item) {
      return new Double(getYValue(series, item));
    }
    // make accessible to UpdatePlotAction
    void datasetChanged() { 
      super.fireDatasetChanged();
    }
  }

  /** ActionListener called by Timer during simulation.
      Enqueues itself on the UI event dispatch queue to update plot.
      Updates plot if data has been added since the last time it
      was run. **/
  private static class PlotUpdater implements ActionListener, Runnable {
    BalanceHistoryXYDataset accountDataset;
    ValueAxis[] yAxes;
    int lastDataCount = 0;
    boolean isQueued = false;
    PlotUpdater(BalanceHistoryXYDataset accountDataset,
		ValueAxis[] yAxes) {
      this.accountDataset = accountDataset;
      this.yAxes = yAxes;
    }
    /** Enqueues itself on the UI event dispatch queue to update plot.
	(Only if last run has completed.) **/
    public synchronized void actionPerformed(ActionEvent e) {
      if (!this.isQueued) {
	EventQueue.invokeLater(this);
	this.isQueued = true;
      }
    }
    /** Updates plot if data has been added since the last time it was run.*/
    public void run() { 
      int currentCount = accountDataset.getItemCount(0);
      if (currentCount > this.lastDataCount) {
	for (ValueAxis yAxis : yAxes) {
	  // workaround: get plot to readjust range
	  yAxis.setAutoRange(false); 
	  yAxis.setAutoRange(true);
	}
	accountDataset.datasetChanged();
	this.lastDataCount = currentCount;
      }
      synchronized(this) { 
	this.isQueued = false;
      }
    }
  }
}
