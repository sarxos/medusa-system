package com.sarxos.medusa.cli.command;

import org.naturalcli.Command;
import org.naturalcli.ExecutionException;
import org.naturalcli.ICommandExecutor;
import org.naturalcli.InvalidSyntaxException;
import org.naturalcli.ParseResult;


/**
 * Mark all SmesX messages as read.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class ClearMessagesCommand extends Command implements ICommandExecutor {

	protected static final String syntax = "clear messages";

	protected static final String help = "Mark all SmesX messages as read";

	public ClearMessagesCommand() throws InvalidSyntaxException {
		prepare(syntax, help, this);
	}

	@Override
	public void execute(ParseResult pr) throws ExecutionException {
		// TODO
	}
}
