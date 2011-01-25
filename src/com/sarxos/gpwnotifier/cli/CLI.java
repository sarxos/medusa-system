package com.sarxos.gpwnotifier.cli;

import java.util.HashSet;
import java.util.Set;

import org.naturalcli.Command;
import org.naturalcli.ExecutionException;
import org.naturalcli.NaturalCLI;
import org.naturalcli.commands.HelpCommand;


public class CLI {

	public static void main(String[] args) throws ExecutionException {
		
		Set<Command> cs = new HashSet<Command>();
		cs.add(new HelpCommand(cs));
		cs.add(new ObservePaperCommand());

		new NaturalCLI(cs).execute(args);
	}
	
}
