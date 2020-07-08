package fr.simplgame.pss.command.main;

import java.awt.Color;

import fr.simplgame.pss.PSS;
import fr.simplgame.pss.command.Command;
import fr.simplgame.pss.command.Command.ExecutorType;
import fr.simplgame.pss.command.CommandMap;
import fr.simplgame.pss.command.SimpleCommand;
import fr.simplgame.pss.util.Loader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.entities.UserImpl;

public class CommandDefault {

	private PSS pss;
	private CommandMap cm;

	public CommandDefault(PSS bot, CommandMap cm) {
		pss = bot;
		this.cm = cm;
	}

	@Command(name = "stop", type = ExecutorType.CONSOLE)
	public void stop() {
		pss.setRunning(false);
		System.exit(0);
	}

	@Command(name = "help", type = ExecutorType.USER)
	public void help(TextChannel channel, Loader[] loader, User user) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(loader[0].lang.get("command.help.title"));
		embed.setColor(Color.decode("#ed61ce"));

		String commands = "";
		for (SimpleCommand command : cm.getCommands()) {
			if (command.getExecutorType() != ExecutorType.CONSOLE) {
				String syntax = loader[0].lang.get("command." + command.getName() + ".syntax");
				if (syntax.equals("NaN"))
					syntax = "";
				commands += "**" + command.getName() + syntax + loader[0].lang.get("general.mark.double_dot") + "**\n"
						+ loader[0].lang.get("command." + command.getName() + ".desc") + "\n";
			}
		}

		embed.addField(loader[0].lang.get("command.help.field1"), commands, false);
		embed.addField(loader[0].lang.get("command.help.field2"),
				loader[0].lang.get("command.help.required").replace("\\n", "\n"), false);

		if (!user.hasPrivateChannel())
			user.openPrivateChannel().complete();
		((UserImpl) user).getPrivateChannel().sendMessage(embed.build()).queue();
	}

	@Command(name = "ping", type = ExecutorType.USER)
	public void ping(MessageChannel channel, Loader[] loader, User user) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(loader[0].lang.get("command.ping.title"));
		embed.setColor(Color.decode("#ed61ce"));

		embed.addField("",
				loader[0].lang.get("command.ping.field").replace("[PING]", pss.getJda().getGatewayPing() + "")
						.replace("[PING2]", user.getJDA().getGatewayPing() + "").replace("\\n", "\n"),
				false);

		channel.sendMessage(embed.build()).queue();
	}

}
