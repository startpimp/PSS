package fr.simplgame.pss.server.us;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.ArrayList;

import fr.simplgame.pss.command.Command;
import fr.simplgame.pss.command.Command.ExecutorType;
import fr.simplgame.pss.util.CSV;
import fr.simplgame.pss.util.Loader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

@SuppressWarnings("unused")
public class UserManager {

	@Command(name = "userinfo", type = ExecutorType.USER, alias = "ui")
	public void user(Member member, MessageChannel channel, Loader[] loader, Message message, User user) {

		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(loader[0].lang.get("server.us.um.L1"));
		embed.setColor(Color.decode("#ed61ce"));

		if (member != null) {
			if (message.getMentionedMembers().size() >= 1) {
				Member mm = message.getMentionedMembers().get(0);
				sendInformationEmbed(mm, channel, loader[0], embed, null);
			} else if (channel.getType() == ChannelType.TEXT)
				sendInformationEmbed(member, channel, loader[0], embed, null);
		} else {
			sendInformationEmbed(null, channel, loader[0], embed, user);
		}

	}

	private void sendInformationEmbed(Member member, MessageChannel channel, Loader loader, EmbedBuilder embed,
			User user) {

		if (user == null)
			user = member.getUser();

		embed.setThumbnail(user.getAvatarUrl());

		if (member != null && !member.getUser().getName().equals(member.getEffectiveName())) {
			embed.addField(loader.lang.get("server.us.um.L2"), member.getEffectiveName(), true);
			embed.addField(loader.lang.get("server.us.um.L3"), user.getName(), true);
		}

		embed.addField(loader.lang.get("server.us.um.L4"), user.getId(), true);

		embed.addField(loader.lang.get("server.us.um.L5"), loader.lang
				.get("general.word." + CSV.getCell(user.getId(), "language", "./res/user.csv").toLowerCase()), true);

		ArrayList<String> createDate = formDate(user.getTimeCreated());
		String hour = createDate.get(3);

		String timeset = "";
		if (loader.lang.get("general.form.date").contains("[TIMESET]") && Integer.parseInt(hour) > 12) {
			hour = (Integer.parseInt(hour) - 12) + "";
			if (Integer.parseInt(hour) < 10)
				hour = "0" + hour;
			timeset = loader.lang.get("general.word.pm");
		} else if (loader.lang.get("general.form.date").contains("[TIMESET]"))
			timeset = loader.lang.get("general.word.am");

		String dateMsg = loader.lang.get("general.form.date").replace("[DAY]", createDate.get(0))
				.replace("[MONTH]", createDate.get(1)).replace("[YEAR]", createDate.get(2)).replace("[HOUR]", hour + "")
				.replace("[MINUTES]", createDate.get(4) + "").replace("[TIMESET]", timeset);
		embed.addField(loader.lang.get("server.us.um.L6"), dateMsg, true);

		if (member != null) {
			ArrayList<String> joinDate = formDate(member.getTimeJoined());
			hour = joinDate.get(3);

			if (loader.lang.get("general.form.date").contains("[TIMESET]") && Integer.parseInt(hour) > 12) {
				hour = (Integer.parseInt(hour) - 12) + "";
				if (Integer.parseInt(hour) < 10)
					hour = "0" + hour;
				timeset = loader.lang.get("general.word.pm");
			} else if (loader.lang.get("general.form.date").contains("[TIMESET]"))
				timeset = loader.lang.get("general.word.am");

			dateMsg = loader.lang.get("general.form.date").replace("[DAY]", joinDate.get(0))
					.replace("[MONTH]", joinDate.get(1)).replace("[YEAR]", joinDate.get(2)).replace("[HOUR]", hour + "")
					.replace("[MINUTES]", joinDate.get(4) + "").replace("[TIMESET]", timeset);
			embed.addField(loader.lang.get("server.us.um.L8"), dateMsg, true);
		}

		channel.sendMessage(embed.build()).queue();

		String message;
		if (user.isBot() || user.isFake()) {
			message = loader.lang.get("server.us.um.L7");

			if (user.isBot())
				message += "\n - " + loader.lang.get("general.word.robot");
			if (user.isFake())
				message += "\n - " + loader.lang.get("general.word.fake");

			channel.sendMessage(message).queue();

		}
	}

	public static ArrayList<String> formDate(OffsetDateTime date) {
		String day, month, year, hour, minute, second;
		if ((date.getDayOfMonth() + "").length() == 1)
			day = "0" + date.getDayOfMonth();
		else
			day = date.getDayOfMonth() + "";

		if ((date.getMonth().getValue() + "").length() == 1)
			month = "0" + date.getMonth().getValue();
		else
			month = date.getMonth().getValue() + "";

		if ((date.getHour() + "").length() == 1)
			hour = "0" + date.getHour();
		else
			hour = date.getHour() + "";

		if ((date.getMinute() + "").length() == 1)
			minute = "0" + date.getMinute();
		else
			minute = date.getMinute() + "";

		if ((date.getSecond() + "").length() == 1)
			second = "0" + date.getSecond();
		else
			second = date.getSecond() + "";

		year = date.getYear() + "";

		ArrayList<String> list = new ArrayList<>();
		list.add(day);
		list.add(month);
		list.add(year);
		list.add(hour);
		list.add(minute);
		list.add(second);

		return list;
	}

}
