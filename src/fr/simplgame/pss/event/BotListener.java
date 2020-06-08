package fr.simplgame.pss.event;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.simplgame.pss.command.CommandMap;
import fr.simplgame.pss.util.CSV;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class BotListener implements EventListener {

	private final CommandMap commandMap;

	public BotListener(CommandMap commandMap) {
		this.commandMap = commandMap;
	}

	@Override
	public void onEvent(GenericEvent event) {
		if (event instanceof MessageReceivedEvent)
			onMessage((MessageReceivedEvent) event);
		// To getting events name ->
		// System.out.println(event.getClass().getSimpleName());
	}

	private void onMessage(MessageReceivedEvent mre) {

		// If the user is PSS, returning to the top function.
		if (mre.getAuthor().equals(mre.getJDA().getSelfUser()))
			return;

		String message = mre.getMessage().getContentRaw();
		if (message.startsWith(commandMap.getTag())) {
			message = message.replaceFirst(commandMap.getTag(), "");
			if (commandMap.commandUser(mre.getAuthor(), message, mre.getMessage())) {
				if (mre.getTextChannel() != null) {
					mre.getMessage().delete().queue();
				}
			}
		} else {
			verifyMessageContent(mre.getMessage().getContentRaw(),
					getUserLang(mre.getAuthor(), mre.getTextChannel()), mre.getTextChannel());
		}
	}

	public static String getUserLang(User user, MessageChannel channel) {
		// Getting user's language
		String userLang = CSV.getCell(user.getId(), "language", "./res/user.csv");
		if (userLang.equals("NaN")) {
			userLang = "english";

			// Adding user to user.csv
			CSV.addCell(user.getId() + ";english;200;0;0;10", "./res/user.csv");

			channel.sendMessage(
					"You've been added to our language database. To change your language, do : `!lang [language]`. \n"
							+ "To view all languages avaible, do `!languages`.")
					.queue();
		}

		return userLang.toLowerCase();
	}

	public static String getServerLang(Guild guild, MessageChannel channel) {
		// Getting user's language
		String serverLang = CSV.getCell(guild.getId(), "language", "./res/server.csv");
		if (serverLang.equals("NaN")) {
			serverLang = "english";

			// Adding user to user.csv
			CSV.addCell(guild.getId() + ";" + guild.getOwnerId() + ";;engilsh", "./res/server.csv");

			channel.sendMessage(
					"The server has been added to our database due to missing language. To change the language, do : `!server lang [language]`. \n"
							+ "To view all languages avaible, do `!languages`.")
					.queue();
		}

		return serverLang.toLowerCase();
	}

	public static void verifyMessageContent(String message, String lang, MessageChannel channel) {
		lang = lang.toLowerCase();

		List<String> words = new ArrayList<>();

		for (String word : CSV.getColumn(lang, "./res/bannedWords.csv")) {
			Pattern detector = Pattern.compile("(\\s+|)(" + word + ")(\\s+|\\.+|\\?+|!+|)");
			Matcher matcher = detector.matcher(message);

			if (matcher.find()) {
				words.add(matcher.group(2));
			}
		}
		
		if (words.size() >= 1) {
			for (String string : words) {
				System.out.println(string);
			}
		}

	}

}
