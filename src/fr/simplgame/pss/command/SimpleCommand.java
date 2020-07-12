package fr.simplgame.pss.command;

import java.lang.reflect.Method;

import fr.simplgame.pss.command.Command.ExecutorType;

/**
 * Permet de r�cup�rer toutes les informations concernant une commande
 * 
 * @author StartPimp47
 *
 */
public final class SimpleCommand {
	private final String name, description, alias;
	private final ExecutorType executor;
	private final Object object;
	private final Method method;

	/**
	 * Cr�er un �l�ment commande
	 * 
	 * @param name        Nom de la commande
	 * @param description Description de la commande
	 * @param executor    D�finire l'ex�cuteur potentiel de cette commande
	 * @param object
	 * @param method
	 */
	public SimpleCommand(String name, String description, ExecutorType executor, Object object, Method method, String alias) {
		super();
		this.name = name;
		this.description = description;
		this.executor = executor;
		this.object = object;
		this.method = method;
		this.alias = alias;
	}

	/**
	 * @return Retourne le nom de la commande
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return Retourne l'alias de la commande
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @return Retourne la description de la commande
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return Retourne l'ex�cuteur potentiel de la commande
	 */
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
