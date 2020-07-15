package fr.simplgame.pss.server;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.simplgame.pss.PSS;
import fr.simplgame.pss.command.Command;
import fr.simplgame.pss.command.Command.ExecutorType;
import fr.simplgame.pss.server.jls.JoinAndLeave;
import fr.simplgame.pss.util.CSV;
import fr.simplgame.pss.util.Loader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class ServerManager {

	private List<String> commands = new ArrayList<>();

	public ServerManager() {
		commands.add("join_message");
		commands.add("leave_message");
		commands.add("join");
		commands.add("leave");
		commands.add("rolejoin");
	}

	@Command(name = "server", type = ExecutorType.USER, description = "server")
	public void server(String[] args, MessageChannel channel, Message message, Loader[] loader, Member member) {
		if (channel.getType() != ChannelType.TEXT) {
			channel.sendMessage(loader[0].lang.get("command.error")).queue();
			return;
		}

		if (args.length >= 2) {
			if (args[1].equalsIgnoreCase("message") && (args[0].equals("join") || args[0].equals("leave")))
				JoinAndLeave.addMessage(channel, message, args[0], loader[0]);
		} else {
			if (args[0].equals("help"))
				help(channel, loader[0]);
			if (args[0].equals("join") || args[0].equals("leave"))
				JoinAndLeave.add(channel, message, loader, member, args[0]);
			if (args[0].equals("rolejoin"))
				JoinAndLeave.roleAdd(channel, message, loader, member);
		}
	}

	private void help(MessageChannel channel, Loader loader) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(loader.lang.get("command.server.help.title"));
		embed.setDescription(loader.lang.get("command.help.field1"));
		embed.setColor(Color.decode("#ed61ce"));
		for (String name : commands) {
			String syntax = loader.lang.get("command.server." + name + ".syntax");
			if (syntax.equals("NaN"))
				syntax = "";
			embed.addField("server " + name.replace("_", " ") + syntax,
					loader.lang.get("command.server." + name + ".desc"), true);
		}
		channel.sendMessage(embed.build()).queue();
	}

	public static boolean isAuthorized(Message message, Member member, Loader loader, boolean msg,
			Permission permission) {

		if (!member.hasPermission(permission) && msg) {
			message.getChannel()
					.sendMessage(loader.lang.get("manager.missing.user").replace("[PERMISSION]", permission.getName()))
					.queue();
			return false;
		}

		if (!Objects.requireNonNull(member.getGuild().getMemberById(PSS.jda.getSelfUser().getId())).hasPermission(permission) && msg) {
			message.getChannel()
					.sendMessage(loader.lang.get("manager.missing.bot").replace("[PERMISSION]", permission.getName()))
					.queue();
			return false;
		}

		if (member.isOwner())
			return true;
		for (String id : CSV.getCell(message.getGuild().getId(), "moderators", "./res/server.csv").split(";")) {
			if (id.equals(member.getId()))
				return true;
		}

		if (!member.isOwner() && msg) {
			message.getChannel().sendMessage(loader.lang.get("command.jls.L1")).queue();
		}
		return false;
	}

}
