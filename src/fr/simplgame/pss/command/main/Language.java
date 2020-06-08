package fr.simplgame.pss.command.main;

import fr.simplgame.pss.command.Command;
import fr.simplgame.pss.command.Command.ExecutorType;
import fr.simplgame.pss.event.BotListener;
import fr.simplgame.pss.util.CSV;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class Language {

	@Command(name = "getlang", description = "other_useful", type = ExecutorType.USER)
	public void getLanguages(User user, MessageChannel channel) {

		// Getting user language
		String userLang = BotListener.getUserLang(user, channel);

		// Creating message
		String message = CSV.getCell("getlang_response", userLang, "./res/langs.csv")
				+ CSV.getCell("double_dot", userLang, "./res/langs.csv");

		// Getting languages
		String[] langs = CSV.getLine("id", "./res/langs.csv").split(";");

		message += "`" + langs[1] + "`";
		for (int i = 2; i < langs.length - 1; i++) {
			message += CSV.getCell("comma_and", userLang, "./res/langs.csv") + "`" + langs[i] + "`";
		}
		message += CSV.getCell("and_word", userLang, "./res/langs.csv") + "`" + langs[langs.length - 1] + "`"
				+ CSV.getCell("point", userLang, "./res/langs.csv");
		
		channel.sendMessage(message).queue();
	}
	
	@Command(name = "setlang", description = "other_useful", type = ExecutorType.USER)
	public void setLanguage(User user, MessageChannel channel, String[] args) {

		// Getting user language
		String userLang = BotListener.getUserLang(user, channel);

		// Getting languages
		String[] langs = CSV.getLine("id", "./res/langs.csv").split(";");

		boolean found = false;
		
		for (int i = 1; i < langs.length; i++) {
			if(langs[i].equalsIgnoreCase(args[0])) {
				found = true;
				CSV.modifyCell(user.getId(), "language", args[0], "./res/user.csv");
				userLang = BotListener.getUserLang(user, channel);
			}
		}
		
		if(!found) {
			channel.sendMessage(CSV.getCell("setlang_error", userLang, "./res/langs.csv").replace("[LANG]", args[0])).queue();
		} else {
			channel.sendMessage(CSV.getCell("setlang_response", userLang, "./res/langs.csv").replace("[LANG]", args[0])).queue();
		}
	}

}
