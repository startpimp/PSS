package fr.simplgame.pss.command.main;

import fr.simplgame.pss.command.Command;
import fr.simplgame.pss.command.Command.ExecutorType;
import fr.simplgame.pss.event.BotListener;
import fr.simplgame.pss.util.CSV;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class Economy {

	@Command(name = "bank", description = "other_economy", type = ExecutorType.USER)
	public void bank(User user, MessageChannel messageChannel) {

		// Getting user's language
		String userLang = BotListener.getUserLang(user, messageChannel);

		// Getting user's money
		String userMoney = CSV.getCell(user.getId(), "money", "./res/user.csv");

		if (!user.hasPrivateChannel())
			user.openPrivateChannel().complete();
		user.openPrivateChannel().queue((channel) -> {

			String message = CSV.getCell("bank_response", userLang, "./res/langs.csv").replace("[MONEY]", userMoney);
			if (Integer.parseInt(userMoney) < 0) {
				message = message
						.replace("[SENTENCE]", CSV.getCell("bank_response_negative", userLang, "./res/langs.csv"))
						.replace("[]", "[-]");
			} else if (Integer.parseInt(userMoney) >= 0) {
				message = message
						.replace("[SENTENCE]", CSV.getCell("bank_response_positive", userLang, "./res/langs.csv"))
						.replace("[]", "[+]");
			}

			message = message.replace("\\n", "\n");

			channel.sendMessage("```css\n" + message + "\n```").queue();
		});

		messageChannel.sendMessage(CSV.getCell("private_message_recieved", userLang, "./res/langs.csv")
				.replace("[USER_MENTION]", user.getAsMention())).queue();
	}

}
