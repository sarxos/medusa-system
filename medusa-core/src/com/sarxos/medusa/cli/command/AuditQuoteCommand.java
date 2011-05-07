package com.sarxos.medusa.cli.command;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.naturalcli.Command;
import org.naturalcli.ExecutionException;
import org.naturalcli.ICommandExecutor;
import org.naturalcli.InvalidSyntaxException;
import org.naturalcli.ParseResult;

import com.sarxos.medusa.data.QuotesAudit;
import com.sarxos.medusa.market.Symbol;


/**
 * Print list of dates with missing quotes for given symbol.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class AuditQuoteCommand extends Command implements ICommandExecutor {

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
	
	protected static final String syntax = "quote audit <symbol>";
	
	protected static final String help = "Print list of dates with missing quotes for given paper";
	
	
	public AuditQuoteCommand() throws InvalidSyntaxException {
		prepare(syntax, help, this);
	}
	
	@Override
	public void execute(ParseResult pr) throws ExecutionException {
		
		String t = (String) pr.getParameterValue(0);
		Symbol s = null;

		try {
			s = Symbol.valueOf(t);
		} catch (IllegalArgumentException e) {
			System.err.println("Paper '" + t + "' is not supported.");
		}

		if (s == null) {
			return;
		}

		QuotesAudit qa = new QuotesAudit();
		Date[] dates = qa.audit(s);
		for (int i = 0; i < dates.length; i++) {
			System.out.println(DATE_FORMAT.format(dates[i]));
		}
	}
}
