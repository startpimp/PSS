package fr.simplgame.pss.event;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.simplgame.pss.PSS;
import fr.simplgame.pss.command.CommandMap;
import fr.simplgame.pss.util.CSV;
import fr.simplgame.pss.util.Loader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;

/**
 * Permet l'écoute de chaque event
 * 
 * @author StartPimp47
 *
 */
public class BotListener implements EventListener {

	private List<Loader> loaders = new ArrayList<>();

	private final CommandMap commandMap;

	/**
	 * Initialisation de la class BotListener
	 * 
	 * @param commandMap Liste de toute les commandes
	 */
	public BotListener(CommandMap commandMap) {
		this.commandMap = commandMap;

		// Loading of all langages
		String[] langs = CSV.getLine("id", "./res/langs.csv").split(";");

		for (int i = 1; i < langs.length - 1; i++) {
			Loader l = new Loader(langs[i]);
			l.load();
			loaders.add(l);
		}
	}

	@Override
	public void onEvent(GenericEvent event) {
		if (event instanceof MessageReceivedEvent)
			onMessage((MessageReceivedEvent) event);
	}

	private void onMessage(MessageReceivedEvent mre) {

		Loader[] lang = new Loader[2];

		for (Loader l : loaders) {
			if (!mre.getAuthor().isBot()) {
				if (getUserLang(mre.getAuthor(), mre.getChannel()).equals(l.getLang()))
					lang[0] = l;
			}
			if (getServerLang(mre.getGuild(), mre.getChannel()).equals(l.getLang()))
				lang[1] = l;
		}

		// If the user is PSS, returning to the top function.
		if (mre.getAuthor().equals(mre.getJDA().getSelfUser()))
			return;

		String message = mre.getMessage().getContentRaw();
		if (message.startsWith(commandMap.getTag())) {
			message = message.replaceFirst(commandMap.getTag(), "");
			if (commandMap.commandUser(mre.getAuthor(), message, mre.getMessage(), lang)) {
				if (mre.getTextChannel() != null) {
					mre.getMessage().delete().queue();
				}
			}
		} else {
			verifyMessageContent(mre.getMessage().getContentRaw(), lang, mre.getMessage().getChannel(), mre.getGuild(),
					mre.getMessage());
		}
	}

	/**
	 * Permet de récupérer la langue de l'utilisateur
	 * 
	 * @param user
	 * @param channel
	 * @return
	 */
	public static String getUserLang(User user, MessageChannel channel) {

		String defaultMessage = "You've been added to our language database. To change your language, do : `"
				+ CommandMap.tag + "lang [language]`. \n" + "To view all languages avaible, do `" + CommandMap.tag
				+ "getlang`.";

		// Getting user's language
		String userLang = CSV.getCell(user.getId(), "language", "./res/user.csv");
		if (userLang.equals("NaN")) {
			userLang = "english";

			// Adding user to user.csv
			CSV.addLine(user.getId() + ";english;200;0;0;10", "./res/user.csv");

			channel.sendMessage(defaultMessage).queue();
		}

		return userLang.toLowerCase();
	}

	public static String getServerLang(Guild guild, MessageChannel channel) {

		String defaultMessage = "The server has been added to our database due to missing language. To change the language, do : `"
				+ CommandMap.tag + "server lang [language]`. \n" + "To view all languages avaible, do `"
				+ CommandMap.tag + "getlang`.";

		// Getting user's language
		String serverLang = CSV.getCell(guild.getId(), "language", "./res/server.csv");
		if (serverLang.equals("NaN")) {
			serverLang = "english";

			// Adding user to user.csv
			CSV.addLine(guild.getId() + ";english;;;true;false;false", "./res/server.csv");

			channel.sendMessage(defaultMessage).queue();
		}

		return serverLang.toLowerCase();
	}

	public static void verifyMessageContent(String message, Loader[] loader, MessageChannel channel, Guild guild,
			Message msg) {
		Loader serverLang = loader[1];
		Loader userLang = loader[0];
		boolean isGuild = false;
		TextChannel channelT = null;
		try {
			channelT = PSS.jda.getTextChannelById(channel.getId());
		} catch (Exception e) {
			isGuild = false;
		}

		if (channelT != null)
			isGuild = true;

		String logID = "";
		if (isGuild) {
			logID = CSV.getCell(guild.getId(), "log_channel", "./res/server.csv");
			if (logID.equals("NaN") || logID.isEmpty()) {
				channel.sendMessage(
						serverLang.lang.get("logChannelNotAdded").replace("[OWNER]", guild.getOwner().getAsMention()))
						.queue();
				return;
			}
		}

		boolean cLD = Boolean.parseBoolean(CSV.getCell(guild.getId(), "capitalLetter", "./res/server.csv"));
		boolean bWD = Boolean.parseBoolean(CSV.getCell(guild.getId(), "bannedWord", "./res/server.csv"));
		boolean lD = Boolean.parseBoolean(CSV.getCell(guild.getId(), "link", "./res/server.csv"));

		if (cLD) {
			Pattern capitalLetter = Pattern.compile("^([^a-z0-9あ-んア-ンㄱ-희.]+)$");
			Matcher matcher = capitalLetter.matcher(message);
			if (matcher.find()) {
				if (matcher.groupCount() == 1) {
					if (isGuild) {
						EmbedBuilder embed = new EmbedBuilder();
						embed.setTitle(serverLang.lang.get("capitalLetter_title"));
						embed.addField("<@" + msg.getAuthor().getId() + ">",
								"__**" + serverLang.lang.get("capitalLetter_messageDef") + "**__" + message, false);
						embed.setFooter(serverLang.lang.get("userDef") + msg.getAuthor().getName() + "#"
								+ msg.getAuthor().getDiscriminator());
						embed.setColor(Color.RED);
						guild.getTextChannelById(logID).sendMessage(embed.build()).queue();
					}
					msg.delete().queue();
					channel.sendMessage(userLang.lang.get("deletedCL")).queue();
					return;
				}
			}
		}

		if (lD) {
			Pattern link = Pattern.compile("(http(s)?://[a-zA-Z0-9./%?=_#&-\\+]+)");
			Matcher matcher = link.matcher(message);
			if (matcher.find()) {
				if (matcher.groupCount() == 1) {
					EmbedBuilder embed = new EmbedBuilder();
					embed.setTitle("");
					embed.addField("", message, false);
					embed.addField("", "", false);
					return;
				}
			}
		}

		if (bWD) {
			List<String> words = new ArrayList<>();
			for (String word : CSV.getColumn(userLang.getLang(), "./res/bannedWords.csv")) {
				Pattern detector = Pattern.compile("(\\s+|)(" + word.toLowerCase() + ")(\\s+|\\.+|\\?+|!+|)");
				Matcher matcher = detector.matcher(message.toLowerCase());

				if (matcher.find()) {
					words.add(matcher.group(2));
				}
			}

			if (userLang.getLang() != serverLang.getLang()) {
				for (String word : CSV.getColumn(serverLang.getLang(), "./res/bannedWords.csv")) {
					Pattern detector = Pattern.compile("(\\s+|)(" + word.toLowerCase() + ")(\\s+|\\.+|\\?+|!+|)");
					Matcher matcher = detector.matcher(message.toLowerCase());

					if (matcher.find()) {
						words.add(matcher.group(2));
					}
				}
			}

			String foundWords = "";
			if (words.size() >= 1) {
				foundWords += words.get(0);
				for (int i = 1; i < words.size() - 2; i++) {
					foundWords += serverLang.lang.get("comma_and") + words.get(i);
				}
				foundWords += serverLang.lang.get("comma_and") + words.get(words.size() - 1)
						+ serverLang.lang.get("point");
			}
			if (isGuild && words.size() >= 1) {
				EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle(serverLang.lang.get("bannedWords_title"));
				if (userLang.getLang() == serverLang.getLang()) {
					embed.addField(serverLang.lang.get("bannedWords_langUsed"), userLang.getLang(), false);
				} else {
					embed.addField(serverLang.lang.get("bannedWords_langUsed"),
							serverLang.lang.get(userLang.getLang() + "_lang") + serverLang.lang.get("and_word")
									+ serverLang.getLang(),
							false);
				}
				embed.addField(serverLang.lang.get("foundWord"), foundWords, false);
				embed.setFooter(serverLang.lang.get("userDef") + msg.getAuthor().getName() + "#"
						+ msg.getAuthor().getDiscriminator());
				embed.setColor(Color.RED);
				guild.getTextChannelById(logID).sendMessage(embed.build()).queue();
			}
		}

	}

}
