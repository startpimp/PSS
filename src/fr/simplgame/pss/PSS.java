package fr.simplgame.pss;

import java.util.Scanner;
import javax.security.auth.login.LoginException;

import fr.simplgame.pss.command.CommandMap;
import fr.simplgame.pss.event.BotListener;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class PSS implements Runnable {
	public static JDA jda = null;
	private final CommandMap commandMap = new CommandMap(this);
	private final Scanner scanner = new Scanner(System.in);

	private boolean running;

	public PSS() throws LoginException {
		PSS.jda = new JDABuilder(AccountType.BOT)
				.setToken("NjY1NTk4NDAxMjg2NTA0NDU4.XhoBVQ.zfVBpi6pnE2PMyyzQBBdzYqRZ3Y")
				.setActivity(Activity.listening(CommandMap.tag + "help")).build();
		jda.addEventListener(new BotListener(commandMap));
		System.out.println("[SYSTEM] PSS connected");
	}

	public JDA getJda() {
		return PSS.jda;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	@Override
	public void run() {
		running = true;
		while (running) {
			if (scanner.hasNextLine())
				commandMap.commandConsole(scanner.nextLine());
		}
		scanner.close();
		System.out.println("[SYSTEM] Bot stopped");
		PSS.jda.shutdown();
		System.exit(0);
	}

	public static void main(String[] args) {
		try {
			PSS bot = new PSS();
			new Thread(bot, "bot").start();
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}
}
