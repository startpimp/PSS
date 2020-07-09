package fr.simplgame.pss.server.us;

import java.time.OffsetDateTime;

import fr.simplgame.pss.command.Command;
import fr.simplgame.pss.command.Command.ExecutorType;
import fr.simplgame.pss.util.CSV;
import fr.simplgame.pss.util.Loader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class UserManager {

	@Command(name = "userinfo", type = ExecutorType.USER)
	public void user(Member member, MessageChannel channel, Loader[] loader, Message message, User user) {

		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(loader[0].lang.get("server.us.um.L1"));
		embed.setColor(Color.decode("#ed61ce"));

		if (message.getMentionedMembers().size() >= 1 || channel.getType() == ChannelType.PRIVATE) {
			Member mm = message.getMentionedMembers().get(0);
			sendInformationEmbed(mm, channel, loader[0], embed);
		} else
			sendInformationEmbed(member, channel, loader[0], embed);

	}

	private void sendInformationEmbed(Member member, MessageChannel channel, Loader loader, EmbedBuilder embed, User user) {

		embed.setThumbnail(member.getUser().getAvatarUrl());

		if (member.getNickname() != null && !member.getUser().getName().equals(member.getNickname())) {
			embed.addField(loader.lang.get("server.us.um.L2"), member.getNickname(), true);
			embed.addField(loader.lang.get("server.us.um.L3"), member.getUser().getName(), true);
		}

		embed.addField(loader.lang.get("server.us.um.L4"), member.getId(), true);

		embed.addField(loader.lang.get("server.us.um.L5"),
				loader.lang
						.get("general.word." + CSV.getCell(member.getId(), "language", "./res/user.csv").toLowerCase()),
				true);

		OffsetDateTime createDate = member.getUser().getTimeCreated();
		int hour = createDate.getHour();

		String timeset = "";
		if (loader.lang.get("general.form.date").contains("[TIMESET]") && hour > 12) {
			hour = hour - 12;
			timeset = loader.lang.get("general.word.pm");
		} else if (loader.lang.get("general.form.date").contains("[TIMESET]"))
			timeset = loader.lang.get("general.word.am");

		String dateMsg = loader.lang.get("general.form.date").replace("[DAY]", createDate.getDayOfMonth() + "")
				.replace("[MONTH]", createDate.getMonth().getValue() + "").replace("[YEAR]", createDate.getYear() + "")
				.replace("[HOUR]", hour + "").replace("[MINUTES]", createDate.getMinute() + "")
				.replace("[TIMESET]", timeset);
		embed.addField(loader.lang.get("server.us.um.L6"), dateMsg, true);

		OffsetDateTime joinDate = member.getTimeJoined();
		hour = joinDate.getHour();
		if (loader.lang.get("general.form.date").contains("[TIMESET]") && hour > 12) {
			hour = hour - 12;
			timeset = loader.lang.get("general.word.pm");
		} else if (loader.lang.get("general.form.date").contains("[TIMESET]"))
			timeset = loader.lang.get("general.word.am");

		dateMsg = loader.lang.get("general.form.date").replace("[DAY]", joinDate.getDayOfMonth() + "")
				.replace("[MONTH]", joinDate.getMonth().getValue() + "").replace("[YEAR]", joinDate.getYear() + "")
				.replace("[HOUR]", hour + "").replace("[MINUTES]", joinDate.getMinute() + "")
				.replace("[TIMESET]", timeset);
		embed.addField(loader.lang.get("server.us.um.L8"), dateMsg, true);

		channel.sendMessage(embed.build()).queue();

		String message = "NaN";
		if (member.getUser().isBot() || member.getUser().isFake()) {
			message = loader.lang.get("server.us.um.L7");

			if (member.getUser().isBot())
				message += "\n - " + loader.lang.get("general.word.robot");
			if (member.getUser().isFake())
				message += "\n - " + loader.lang.get("general.word.fake");

			channel.sendMessage(message).queue();

		}
	}

}
