package fr.simplgame.pss.event;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.simplgame.pss.PSS;
import fr.simplgame.pss.command.CommandMap;
import fr.simplgame.pss.util.CSV;
import fr.simplgame.pss.util.Loader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

/**
 * Permet l'écoute de chaque event
 * 
 * @author StartPimp47
 *
 */
public class BotListener implements EventListener {

	public static List<Loader> loaders = new ArrayList<>();

	private final CommandMap commandMap;

	/**
	 * Initialisation de la class BotListener
	 * 
	 * @param commandMap Liste de toute les commandes
	 */
	public BotListener(CommandMap commandMap) {
		this.commandMap = commandMap;

		// Loading of all langages
		String[] langs = CSV.getLine("id", "./res/langs.csv").split(";");
		for (String lang : langs) {
			Loader l = new Loader(lang);
			l.load();
			loaders.add(l);
		}

	}

	@Override
	public void onEvent(@NotNull GenericEvent event) {
		if (event instanceof MessageReceivedEvent)
			onMessage((MessageReceivedEvent) event);
		if (event instanceof GuildMemberJoinEvent)
			onJoin((GuildMemberJoinEvent) event);
		if (event instanceof GuildMemberLeaveEvent)
			onLeave((GuildMemberLeaveEvent) event);
	}

	private void onJoin(GuildMemberJoinEvent gmje) {

		if (CSV.getLine(gmje.getGuild().getId(), "./res/server.csv").equals("NaN"))
			CSV.addLine(gmje.getGuild().getId() + ";english; ; ;false;false;false; ; ; ;Welcome [USER];Bye bye [USER]",
					"./res/server.csv");

		String joinId = CSV.getCell(gmje.getGuild().getId(), "join_channel", "./res/server.csv");
		Objects.requireNonNull(gmje.getGuild().getTextChannelById(joinId))
				.sendMessage(CSV.getCell(gmje.getGuild().getId(), "join_message", "./res/server.csv")
						.replace("[USER]", gmje.getMember().getEffectiveName()).replace("[COMMA_DOT]", ";"))
				.queue();

		String roleId = CSV.getCell(gmje.getGuild().getId(), "join_role", "./res/server.csv");
		StringBuilder roleWrong = new StringBuilder("NaN");
		Loader loader = null;
		boolean roleError = false;
		for (Loader l : loaders) {
			if (l.getLang().equals(CSV.getCell(gmje.getGuild().getId(), "language", "./res/server.csv"))) {
				loader = l;
				break;
			}
		}
		String[] roles = roleId.split("_");
		for (String role : roles) {
			try {
				gmje.getGuild().addRoleToMember(gmje.getMember().getId(), Objects.requireNonNull(gmje.getGuild().getRoleById(role)))
						.queue();
			} catch (HierarchyException e) {
				if (roleWrong.toString().equals("NaN"))
					roleWrong = new StringBuilder(Objects.requireNonNull(gmje.getGuild().getRoleById(role)).getAsMention());
				else {
					assert loader != null;
					roleWrong.append(loader.lang.get("general.mark.comma")).append(Objects.requireNonNull(gmje.getGuild().getRoleById(role)).getAsMention());
				}
				roleError = true;
			}
		}

		assert loader != null;
		roleWrong.append(loader.lang.get("general.mark.dot"));
		if (roleError) {
			if (!CSV.getCell(gmje.getGuild().getId(), "log_channel", "./res/server.csv").equals("NaN")) {
				EmbedBuilder embed = new EmbedBuilder();
				embed.setTitle(loader.lang.get("server.error.jls.L1"));
				embed.setDescription(loader.lang.get("server.error.jls.L2").replace("\\n[ROLE]", "\n" + roleWrong));
				embed.setColor(Color.decode("#D61C1C"));
				Objects.requireNonNull(gmje.getGuild()
						.getTextChannelById(CSV.getCell(gmje.getGuild().getId(), "log_channel", "./res/server.csv")))
						.sendMessage(embed.build()).queue();
			}
		}
	}

	private void onLeave(GuildMemberLeaveEvent gmle) {
		if (CSV.getLine(gmle.getGuild().getId(), "./res/server.csv").equals("NaN"))
			CSV.addLine(gmle.getGuild().getId() + ";english; ; ;false;false;false; ; ; ;Welcome [USER];Bye bye [USER]",
					"./res/server.csv");

		String leaveId = CSV.getCell(gmle.getGuild().getId(), "leave_channel", "./res/server.csv");
		Objects.requireNonNull(gmle.getGuild().getTextChannelById(leaveId))
				.sendMessage(CSV.getCell(gmle.getGuild().getId(), "leave_message", "./res/server.csv")
						.replace("[USER]", gmle.getMember().getEffectiveName()).replace("[COMMA_DOT]", ";"))
				.queue();
	}

	private void onMessage(MessageReceivedEvent mre) {

		Loader[] lang = new Loader[2];
		String userLanguage = CSV.getCell(mre.getAuthor().getId(), "language", "./res/user.csv");
		String serverLanguage = "NaN";
		if (mre.getChannel().getType() == ChannelType.TEXT)
			serverLanguage = CSV.getCell(mre.getGuild().getId(), "language", "./res/server.csv");
		if (userLanguage.equals("NaN"))
			lang[0] = new Loader();
		else
			lang[0] = new Loader(userLanguage);
		if (serverLanguage.equals("NaN"))
			lang[1] = new Loader();
		else
			lang[1] = new Loader(serverLanguage);
		
		System.out.println(userLanguage);
		System.out.println(serverLanguage);

		// If the user is PSS, returning to the top function.
		if (mre.getAuthor().equals(mre.getJDA().getSelfUser()))
			return;

		String message = mre.getMessage().getContentRaw();
		if (message.startsWith(commandMap.getTag())) {
			if (mre.getAuthor().isBot())
				return;
			message = message.replaceFirst(commandMap.getTag(), "");
			if (commandMap.commandUser(mre.getAuthor(), message, mre.getMessage(), lang))
				if (mre.getChannel().getType() == ChannelType.TEXT)
					if (Objects.requireNonNull(mre.getGuild().getMemberById(PSS.jda.getSelfUser().getId()))
							.hasPermission(Permission.MESSAGE_MANAGE))
						mre.getMessage().delete().queue();
		}
	}

}
