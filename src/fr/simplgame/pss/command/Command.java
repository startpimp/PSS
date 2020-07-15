package fr.simplgame.pss.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

	String name();

	String description() default "NaN_NaN";
	
	String alias() default "NaN";

	ExecutorType type() default ExecutorType.ALL;

	enum ExecutorType {
		ALL, USER, CONSOLE
	}
}
