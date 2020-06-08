package fr.simplgame.pss.command;

import java.lang.reflect.Method;

import fr.simplgame.pss.command.Command.ExecutorType;

public final class SimpleCommand {
	private final String name, description;
	private final ExecutorType executor;
	private final Object object;
	private final Method method;

	public SimpleCommand(String name, String description, ExecutorType executor, Object object, Method method) {
		super();
		this.name = name;
		this.description = description;
		this.executor = executor;
		this.object = object;
		this.method = method;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public ExecutorType getExecutorType() {
		return executor;
	}

	public Object getObject() {
		return object;
	}

	public Method getMethod() {
		return method;
	}

}
