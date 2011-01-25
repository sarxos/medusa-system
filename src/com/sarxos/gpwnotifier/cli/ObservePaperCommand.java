package com.sarxos.gpwnotifier.cli;

import org.naturalcli.Command;
import org.naturalcli.ExecutionException;
import org.naturalcli.ICommandExecutor;
import org.naturalcli.InvalidSyntaxException;
import org.naturalcli.ParseResult;


public class ObservePaperCommand extends Command implements ICommandExecutor {

	protected static final String syntax = "observe <paper>";
	protected static final String help = "Test tetetet";

	
	public ObservePaperCommand() throws InvalidSyntaxException {
		prepare(syntax, help, this);
	}
	
	@Override
	public void execute(ParseResult pr) throws ExecutionException {
		System.out.println("test");
	}
}
