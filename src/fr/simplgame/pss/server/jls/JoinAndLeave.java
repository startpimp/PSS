package fr.simplgame.pss.server.jls;

import fr.simplgame.pss.server.ServerManager;
import fr.simplgame.pss.util.CSV;
import fr.simplgame.pss.util.Loader;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class JoinAndLeave {

	public static void add(MessageChannel channel, Message message, Loader[] loader, Member member, String arg) {
		
		String[] column = new String[3];
		
		if (arg.equalsIgnoreCase("join")) {
			
			column[0] = "join_channel";
			column[1] = "command.jls.jal.L1";
			
		} else {
			
			column[0] = "leave_channel";
			column[1] = "command.jls.jal.L3";
			
		}

		CSV.modifyCell(message.getGuild().getId(), column[0], message.getMentionedChannels().get(0).getId(),
				"./res/server.csv");
		
		channel.sendMessage(loader[0].lang.get(column[1]).replace("[CHANNEL]",
				message.getMentionedChannels().get(0).getAsMention())).queue();
		
	}

	public static void addMessage(MessageChannel channel, Message message, String arg, Loader loader) {
		String[] column = new String[3];
		if (arg.equalsIgnoreCase("join")) {
			column[0] = "join_message";
			column[1] = "command.jls.jal.L4";
		} else {
			column[0] = "leave_message";
			column[1] = "command.jls.jal.L5";
		}

		if (ServerManager.isAuthorized(message, message.getMember(), loader, true)) {

		}
	}

	public static void roleAdd(MessageChannel channel, Message message, Loader[] loader, Member member) {

		CSV.modifyCell(message.getGuild().getId(), "join_role", message.getMentionedRoles().get(0).getId(),
				"./res/server.csv");
		
		channel.sendMessage(loader[0].lang.get("command.jls.jal.L2").replace("[ROLE]",
				message.getMentionedRoles().get(0).getAsMention())).queue();

	}

}
