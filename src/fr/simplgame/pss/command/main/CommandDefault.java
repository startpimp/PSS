package fr.simplgame.pss.command.main;

import fr.simplgame.pss.PSS;
import fr.simplgame.pss.command.Command;
import fr.simplgame.pss.command.Command.ExecutorType;

public class CommandDefault {
	
	private PSS pss;
	
	public CommandDefault(PSS bot) {
		pss = bot;
	}
	
	@Command(name="stop", type=ExecutorType.CONSOLE)
	public void stop() {
		pss.setRunning(false);
		System.exit(0);
	}

}
