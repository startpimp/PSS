package fr.simplgame.pss.server.us;

import fr.simplgame.pss.command.Command;
import fr.simplgame.pss.command.Command.ExecutorType;
import fr.simplgame.pss.event.BotListener;
import fr.simplgame.pss.util.CSV;
import fr.simplgame.pss.util.Loader;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.Objects;

@SuppressWarnings("unused")
public class LanguageManager {

	@Command(name = "setlang", type = ExecutorType.USER)
	public void setLanguage(User user, String[] args, MessageChannel channel, Loader[] loader) {
		String file = "./res/user.csv";

		boolean userExist = false;
		for (String id : Objects.requireNonNull(CSV.getColumn("id", file))) {
			if (id.equals(user.getId())) {
				userExist = true;
				break;
			}
		}

		if (!userExist)
			CSV.addLine(user.getId() + ";english;200;0;0;10", file);

		String userLanguage = CSV.getCell(user.getId(), "language", file);

		if (userLanguage.equalsIgnoreCase(args[0])) {
			channel.sendMessage(loader[0].lang.get("server.us.lm.L1").replace("[NAME]", user.getName())).queue();
			return;
		}

		boolean languageFound = false;
		String[] langs = CSV.getLine("id", "./res/langs.csv").split(";");
		for (String lang : langs) {
			if (!lang.equalsIgnoreCase("id") && lang.equalsIgnoreCase(args[0])) {
				CSV.modifyCell(user.getId(), "language", args[0].toLowerCase(), file);
				languageFound = true;
				break;
			}
		}

		if (languageFound) {
			for (Loader ld : BotListener.loaders) {
				if (ld.getLang().equalsIgnoreCase(args[0]))
					channel.sendMessage(ld.lang.get("server.us.lm.L2")).queue();
			}
		} else
			channel.sendMessage(loader[0].lang.get("server.us.lm.L3")).queue();

	}

	@Command(name = "langs", type = ExecutorType.USER)
	public void getLanguages(MessageChannel channel, Loader[] loader) {

		String[] langs = CSV.getLine("id", "./res/langs.csv").split(";");
		StringBuilder message = new StringBuilder(loader[0].lang.get("server.us.lm.Ls1"));
		message.append("`").append(langs[1]).append("`");
		for (int i = 2; i < langs.length - 1; i++) {
			message.append(loader[0].lang.get("general.mark.comma")).append("`").append(langs[i]).append("`");
		}
		message.append(loader[0].lang.get("general.word.and")).append("`").append(langs[langs.length - 1]).append("`").append(loader[0].lang.get("general.mark.dot"));

		channel.sendMessage(message.toString()).queue();

	}

	@Command(name = "addlang", type = ExecutorType.CONSOLE)
	public void addLanguage(String[] args) {
		Loader l = new Loader(args[0]);
		l.load();
		BotListener.loaders.add(l);
		System.out.println(args[0] + " has been added");
	}

}
