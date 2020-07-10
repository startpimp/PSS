package fr.simplgame.pss.server.jls;

import fr.simplgame.pss.server.ServerManager;
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

		if (ServerManager.isAuthorized(message, member, loader[0])) {
			CSV.modifyCell(message.getGuild().getId(), "join_channel", message.getMentionedChannels().get(0).getId(),
					"./res/server.csv");
			channel.sendMessage(loader[0].lang.get("command.jls.jal.L1").replace("[CHANNEL]",
					message.getMentionedChannels().get(0).getAsMention())).queue();
		}
	}
	
	public static void roleAdd(MessageChannel channel, Message message, Loader[] loader, Member member) {
		if (channel.getType() != ChannelType.TEXT) {
			channel.sendMessage(loader[0].lang.get("command.error")).queue();
			return;
		}

		if (ServerManager.isAuthorized(message, member, loader[0])) {
			CSV.modifyCell(message.getGuild().getId(), "join_role", message.getMentionedRoles().get(0).getId(),
					"./res/server.csv");
			channel.sendMessage(loader[0].lang.get("command.jls.jal.L2").replace("[ROLE]",
					message.getMentionedRoles().get(0).getAsMention())).queue();
		}
	}

}
