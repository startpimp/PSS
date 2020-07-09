package fr.simplgame.pss.server.jls;

import fr.simplgame.pss.util.Loader;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class JoinAndLeave {
	
	public static void add(MessageChannel channel, Message message, Loader[] loader) {
		if(channel.getType() != ChannelType.TEXT) {
			channel.sendMessage(loader[0].lang.get("command.error")).queue();
			return;
		}
	}
	
}
