package fr.simplgame.pss.command.main;

import java.awt.Color;

import fr.simplgame.pss.PSS;
import fr.simplgame.pss.command.Command;
import fr.simplgame.pss.command.Command.ExecutorType;
import fr.simplgame.pss.command.CommandMap;
import fr.simplgame.pss.command.SimpleCommand;
import fr.simplgame.pss.event.BotListener;
import fr.simplgame.pss.util.CSV;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class CommandDefault {

	private final PSS bot;
	private final CommandMap commandMap;

	public CommandDefault(PSS bot, CommandMap commandMap) {
		this.bot = bot;
		this.commandMap = commandMap;
	}

	@Command(name = "stop", type = ExecutorType.CONSOLE)
	private void stop() {
		bot.setRunning(false);
	}

	@Command(name = "ping", description = "other_useful", type = ExecutorType.USER)
	private void ping(User user, MessageChannel channel, JDA jda) {
		// Getting user language
		String userLang = BotListener.getUserLang(user, channel);

		// Getting the bot's ping
		long ping = jda.getGatewayPing();

		Color color;
		// Setting the response color
		if (ping < 100)
			color = Color.cyan;
		else if (ping < 400)
			color = Color.green;
		else if (ping < 700)
			color = Color.yellow;
		else if (ping < 1000)
			color = Color.orange;
		else
			color = Color.red;

		String message = CSV.getCell("ping_response", userLang, "./res/langs.csv").replace("[NUMBER]", ping + "");

		EmbedBuilder embed = new EmbedBuilder();
		embed.setColor(color);
		embed.setDescription(message);

		channel.sendMessage(embed.build()).queue();
	}

	@Command(name = "botping", type = ExecutorType.CONSOLE)
	private void ping(JDA jda) {
		System.out.println("The bot ping is: " + jda.getGatewayPing() + "ms");
	}

	@Command(name = "help", description = "other_useful", type = ExecutorType.USER)
	private void info(User user, MessageChannel channel) {

		// Getting user language
		String userLang = BotListener.getUserLang(user, channel);

		// Creating embed message
		EmbedBuilder server = new EmbedBuilder();
		server.setTitle(CSV.getCell("help_title", userLang, "./res/langs.csv")
				+ CSV.getCell("dash", userLang, "./res/langs.csv")
				+ CSV.getCell("help_type_other", userLang, "./res/langs.csv"));
		server.setDescription(CSV.getCell("help_embed", userLang, "./res/langs.csv").replace("\\n", "\n"));
		server.setColor(Color.CYAN);

		String usefulCommand = "";
		String economyCommand = "";

		for (SimpleCommand command : commandMap.getCommands()) {

			if (command.getExecutorType() != ExecutorType.CONSOLE) {
				String message = "**" + command.getName()
						+ CSV.getCell(command.getName() + "_syntax", userLang, "./res/langs.csv").replace("NaN", "")
						+ "**" + CSV.getCell("double_dot", userLang, "./res/langs.csv")
						+ CSV.getCell(command.getName() + "_desc", userLang, "./res/langs.csv").replace("\\n", "\n")
						+ "\n";
				if (command.getDescription().equals("other_useful")) {
					usefulCommand += message;
				}
				if (command.getDescription().equals("other_economy")) {
					economyCommand += message;
				}
			}
		}

		server.addField(CSV.getCell("help_usefulfield", userLang, "./res/langs.csv"), usefulCommand, false);
		server.addField(CSV.getCell("help_economyfield", userLang, "./res/langs.csv"), economyCommand, false);

		server.setFooter(CSV.getCell("help_footer", userLang, "./res/langs.csv"));

		// Opening user's private channel
		if (!user.hasPrivateChannel())
			user.openPrivateChannel().complete();
		user.openPrivateChannel().queue((userChannel) -> userChannel.sendMessage(server.build()).complete());

		// Message in user language
		final String message = CSV.getCell("help_response", userLang, "./res/langs.csv").replace("[USER_MENTION]",
				user.getAsMention());

		// Send message
		channel.sendMessage(message).queue();
	}

}
