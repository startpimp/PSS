package fr.simplgame.pss.server.jls;

import fr.simplgame.pss.command.CommandMap;
import fr.simplgame.pss.server.ServerManager;
import fr.simplgame.pss.util.CSV;
import fr.simplgame.pss.util.Loader;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.Objects;

public class JoinAndLeave {

	public static void add(MessageChannel channel, Message message, Loader[] loader, Member member, String arg) {

		if (!ServerManager.isAuthorized(message, member, loader[0], true, Permission.MESSAGE_WRITE))
			return;

		String[] column = new String[3];

		if (arg.equalsIgnoreCase("join")) {

			column[0] = "join_channel";
			column[1] = "command.jls.jal.L1";

		} else {

			column[0] = "leave_channel";
			column[1] = "command.jls.jal.L3";

		}

		if (!(message.getMentionedChannels().size() >= 1)) {
			channel.sendMessage(loader[0].lang.get("general.L1")).queue();
			return;
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

		if (ServerManager.isAuthorized(message, Objects.requireNonNull(message.getMember()), loader, true, Permission.MESSAGE_WRITE)) {
			CSV.modifyCell(
					message.getGuild().getId(), column[0], message.getContentRaw()
							.replace(CommandMap.tag + "server join message ", "").replace(";", "[COMMA_DOT]"),
					"./res/server.csv");
			channel.sendMessage(loader.lang.get(column[1])).queue();
		}
	}

	public static void roleAdd(MessageChannel channel, Message message, Loader[] loader, Member member) {

		if (!ServerManager.isAuthorized(message, member, loader[0], true, Permission.MANAGE_ROLES))
			return;

		StringBuilder value = new StringBuilder(message.getMentionedRoles().get(0).getId());
		StringBuilder msg = new StringBuilder(message.getMentionedRoles().get(0).getAsMention());

		if (!(message.getMentionedRoles().size() >= 1)) {
			channel.sendMessage(loader[0].lang.get("general.L1")).queue();
			return;
		}

		for (int i = 1; i < message.getMentionedRoles().size(); i++) {
			value.append("_").append(message.getMentionedRoles().get(i).getId());
			msg.append(loader[0].lang.get("general.mark.comma")).append(message.getMentionedRoles().get(i).getAsMention());
		}

		CSV.modifyCell(message.getGuild().getId(), "join_role", value.toString(), "./res/server.csv");

		channel.sendMessage(loader[0].lang.get("command.jls.jal.L2").replace("[ROLE]", msg.toString())).queue();

	}

}
