package fr.simplgame.pss.event;

import java.util.ArrayList;
import java.util.List;

import fr.simplgame.pss.command.CommandMap;
import fr.simplgame.pss.util.CSV;
import fr.simplgame.pss.util.Loader;
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

	public static List<Loader> loaders = new ArrayList<>();

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
		for (String lang : langs) {
			Loader l = new Loader(lang);
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
		String userLanguage = CSV.getCell(mre.getAuthor().getId(), "language", "./res/user.csv");
		String serverLanguage = CSV.getCell(mre.getGuild().getId(), "language", "./res/server.csv");
		if (userLanguage.equals("NaN"))
			lang[0] = new Loader();
		else
			lang[0] = new Loader(userLanguage);
		if (serverLanguage.equals("NaN"))
			lang[1] = new Loader();
		else
			lang[1] = new Loader(serverLanguage);

		// If the user is PSS, returning to the top function.
		if (mre.getAuthor().equals(mre.getJDA().getSelfUser()))
			return;

		String message = mre.getMessage().getContentRaw();
		if (message.startsWith(commandMap.getTag())) {
			if (mre.getAuthor().isBot())
				return;
			message = message.replaceFirst(commandMap.getTag(), "");
			if (commandMap.commandUser(mre.getAuthor(), message, mre.getMessage(), lang)) {
				if (mre.getTextChannel() != null) {
					mre.getMessage().delete().queue();
				}
			}
		}
	}

}
