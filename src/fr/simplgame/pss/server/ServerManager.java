package fr.simplgame.pss.server;

import fr.simplgame.pss.command.Command;
import fr.simplgame.pss.command.Command.ExecutorType;
import fr.simplgame.pss.server.jls.JoinAndLeave;
import fr.simplgame.pss.util.CSV;
import fr.simplgame.pss.util.Loader;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class ServerManager {

	@Command(name = "server", type = ExecutorType.USER, description = "server")
	public void server(String[] args, MessageChannel channel, Message message, Loader[] loader, Member member) {
		if (args[0].equals("join"))
			JoinAndLeave.add(channel, message, loader, member);
		if (args[0].equals("role_join"))
			JoinAndLeave.roleAdd(channel, message, loader, member);
	}

	public static boolean isAuthorized(Message message, Member member, Loader loader) {

		if (member.isOwner())
			return true;
		for (String id : CSV.getCell(message.getGuild().getId(), "moderators", "./res/server.csv").split(";")) {
			if (id.equals(member.getId()))
				return true;
		}

		if (!member.isOwner()) {
			message.getChannel().sendMessage(loader.lang.get("command.jls.L1")).queue();
		}
		return false;
	}

}
