package fr.simplgame.pss.util;

import java.util.Objects;

public class Loader {

	private String var = "english";

	public Lang lang = new Lang();

	public Loader(String language) {
		var = language;
		lang.lang = var;
		load();
	}

	public Loader() {
		lang.lang = var;
		load();
	}

	public String getLang() {
		return var;
	}

	public void load() {
		String file = "./res/langs.csv";
		lang.getVars().clear();

		for (String word : Objects.requireNonNull(CSV.getColumn(var, file))) {
			lang.getVars().add(CSV.getKey(var, word, file) + "=" + word);
		}

	}

}
