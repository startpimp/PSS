package fr.simplgame.pss.command;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import fr.simplgame.pss.PSS;
import fr.simplgame.pss.command.Command.ExecutorType;
import fr.simplgame.pss.command.main.CommandDefault;
import fr.simplgame.pss.server.ServerManager;
import fr.simplgame.pss.server.us.LanguageManager;
import fr.simplgame.pss.server.us.UserManager;
import fr.simplgame.pss.util.Loader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public final class CommandMap {
	private final Map<String, SimpleCommand> commands = new HashMap<>();
	public static final String tag = ".";
	private final PSS bot;

	public CommandMap(PSS bot) {
		this.bot = bot;

		registerCommands(new CommandDefault(bot, this), new LanguageManager(), new UserManager(), new ServerManager());
	}

	public String getTag() {
		return tag;
	}

	public Collection<SimpleCommand> getCommands() {
		return commands.values();
	}

	public void registerCommands(Object... objects) {
		for (Object object : objects)
			registerCommand(object);
	}

	public void registerCommand(Object object) {
		for (Method method : object.getClass().getDeclaredMethods()) {
			if (method.isAnnotationPresent(Command.class)) {
				Command command = method.getAnnotation(Command.class);
				method.setAccessible(true);
				SimpleCommand simpleCommand = new SimpleCommand(command.name(), command.description(), command.type(),
						object, method, command.alias());
				if (command.alias().equals("NaN"))
					commands.put(command.name(), simpleCommand);
				else
					commands.put(command.name() + "_" + command.alias(), simpleCommand);
			}
		}
	}

	public void commandConsole(String command) {
		Object[] object = getCommand(command);
		if (object[0] == null || ((SimpleCommand) object[0]).getExecutorType() == ExecutorType.USER) {
			System.out.println("Commande inconnue.");
			return;
		}
		try {
			execute(((SimpleCommand) object[0]), command, (String[]) object[1], null, null);
		} catch (Exception exception) {
			System.out.println("La methode " + ((SimpleCommand) object[0]).getMethod().getName()
					+ " n'est pas correctement initialisee.");
		}
	}

	public boolean commandUser(String command, Message message, Loader[] lang) {
		Object[] object = getCommand(command);
		if (object[0] == null || ((SimpleCommand) object[0]).getExecutorType() == ExecutorType.CONSOLE)
			return false;
		try {
			execute(((SimpleCommand) object[0]), command, (String[]) object[1], message, lang);
		} catch (Exception exception) {
			exception.printStackTrace();
			System.out.println("La methode " + ((SimpleCommand) object[0]).getMethod().getName()
					+ " n'est pas correctement initialisee.");
		}
		return true;
	}

	private SimpleCommand simpleCommand;

	private Object[] getCommand(String command) {
		String[] commandSplit = command.split(" ");
		String[] args = new String[commandSplit.length - 1];
		System.arraycopy(commandSplit, 1, args, 0, commandSplit.length - 1);
		simpleCommand = commands.get(commandSplit[0].toLowerCase());
		if (simpleCommand == null) {
			commands.forEach((str, sc) -> {
				String[] strs = str.split("_");
				if (strs.length == 2) {
					if (strs[0].equals(commandSplit[0].toLowerCase()) || strs[1].equals(commandSplit[0].toLowerCase()))
						simpleCommand = sc;
				}
			});
		}
		return new Object[] { simpleCommand, args };
	}

	private void execute(SimpleCommand simpleCommand, String command, String[] args, Message message, Loader[] lang)
			throws Exception {
		Parameter[] parameters = simpleCommand.getMethod().getParameters();
		Object[] objects = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i].getType() == String[].class)
				objects[i] = args;
			else if (parameters[i].getType() == User.class)
				objects[i] = message == null ? null : message.getAuthor();
			else if (parameters[i].getType() == TextChannel.class)
				objects[i] = message == null ? null : message.getTextChannel();
			else if (parameters[i].getType() == PrivateChannel.class)
				objects[i] = message == null ? null : message.getPrivateChannel();
			else if (parameters[i].getType() == Guild.class)
				objects[i] = message == null ? null : message.getGuild();
			else if (parameters[i].getType() == String.class)
				objects[i] = command;
			else if (parameters[i].getType() == Message.class)
				objects[i] = message;
			else if (parameters[i].getType() == JDA.class)
				objects[i] = bot.getJda();
			else if (parameters[i].getType() == MessageChannel.class) {
				assert message != null;
				objects[i] = message.getChannel();
			}
			else if (parameters[i].getType() == Loader[].class) {
				lang[0].load();
				lang[1].load();
				objects[i] = lang;
			} else if (parameters[i].getType() == Member.class) {
				assert message != null;
				objects[i] = message.getMember();
			}
		}
		simpleCommand.getMethod().invoke(simpleCommand.getObject(), objects);
	}
}
