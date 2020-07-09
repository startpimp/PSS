package fr.simplgame.pss.server.jls;

import fr.simplgame.pss.util.CSV;
import fr.simplgame.pss.util.Loader;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class JoinAndLeave {

	public static void add(MessageChannel channel, Message message, Loader[] loader, Member member) {
		if (channel.getType() != ChannelType.TEXT) {
			channel.sendMessage(loader[0].lang.get("command.error")).queue();
			return;
		}

		boolean isAuthorize = false;
		for (String id : CSV.getCell(message.getGuild().getId(), "moderators", "./res/server.csv").split(";")) {
			if (id.equals(member.getId())) {
				isAuthorize = true;
				break;
			}
		}

		if (!member.isOwner() && !isAuthorize) {
			
		}
	}

}
