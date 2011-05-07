package com.sarxos.medusa.cli;

import java.util.HashSet;
import java.util.Set;

import org.naturalcli.Command;
import org.naturalcli.ExecutionException;
import org.naturalcli.InvalidSyntaxException;
import org.naturalcli.NaturalCLI;
import org.naturalcli.commands.HelpCommand;

import com.sarxos.medusa.cli.command.AddTraderCommand;
import com.sarxos.medusa.cli.command.AuditQuoteCommand;
import com.sarxos.medusa.cli.command.ObservePaperCommand;


public class CLI {

	public static void main(String[] args) throws ExecutionException, InvalidSyntaxException {

		Set<Command> cs = new HashSet<Command>();
		cs.add(new HelpCommand(cs));
		cs.add(new ObservePaperCommand());
		cs.add(new AuditQuoteCommand());
		cs.add(new AddTraderCommand());

		new NaturalCLI(cs).execute(args);
	}
}
