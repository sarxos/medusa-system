package com.sarxos.medusa.cli.command;

import org.naturalcli.Command;
import org.naturalcli.ExecutionException;
import org.naturalcli.ICommandExecutor;
import org.naturalcli.InvalidSyntaxException;
import org.naturalcli.ParseResult;

import com.sarxos.medusa.task.ClearSmesXMessagesTask;


/**
 * Mark all SmesX messages as read.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class ClearSmesXMessagesCommand extends Command implements ICommandExecutor {

	protected static final String syntax = "smesx clear";

	protected static final String help = "Mark all SmesX messages as read";

	public ClearSmesXMessagesCommand() throws InvalidSyntaxException {
		prepare(syntax, help, this);
	}

	@Override
	public void execute(ParseResult pr) throws ExecutionException {
		new ClearSmesXMessagesTask().run();
	}
}
