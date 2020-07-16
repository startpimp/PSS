package fr.simplgame.pss.command.main;

import fr.simplgame.pss.PSS;
import fr.simplgame.pss.command.Command;
import fr.simplgame.pss.command.Command.ExecutorType;
import fr.simplgame.pss.command.CommandMap;
import fr.simplgame.pss.command.SimpleCommand;
import fr.simplgame.pss.server.ServerManager;
import fr.simplgame.pss.util.Loader;
import fr.simplgame.pss.util.Sys;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.entities.UserImpl;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("unused")
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
	}

	@Command(name = "help", type = ExecutorType.USER, alias = "h")
	public void help(MessageChannel channel, Loader[] loader, User user, Message message, Member member) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(loader[0].lang.get("command.help.title"));
		embed.setColor(Color.decode("#ed61ce"));
		embed.setDescription(loader[0].lang.get("command.help.field1"));

		for (SimpleCommand command : cm.getCommands()) {
			if (command.getExecutorType() != ExecutorType.CONSOLE) {
				String syntax = loader[0].lang.get("command." + command.getName() + ".syntax");

				if (syntax.equals("NaN"))
					syntax = "";

				AtomicBoolean sendMessage = new AtomicBoolean(false);

				if (member != null && command.getDescription().equals("server")) {
					if (ServerManager.isAuthorized(message, member, loader[0], false, Permission.ADMINISTRATOR)) {
						sendMessage.set(true);
					}
				} else if (!command.getDescription().equals("iD")) {
					sendMessage.set(true);
				}

				if (sendMessage.get()) {
					if (!command.getAlias().equals("NaN")) {
						Sys.out.println(loader[0].getLang());
						embed.addField(
								command.getName() + syntax + "\n" + loader[0].lang.get("command.help.alias")
										+ command.getAlias(),
								loader[0].lang.get("command." + command.getName() + ".desc"), true);
					} else {
						embed.addField(command.getName() + syntax,
								loader[0].lang.get("command." + command.getName() + ".desc"), true);
					}
				}

			}
		}

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
		embed.setDescription(loader[0].lang.get("command.ping.field").replace("[PING]",
				"**" + pss.getJda().getGatewayPing() + "**"));
		channel.sendMessage(embed.build()).queue();
	}

}
