package fr.simplgame.pss.util;

import java.util.ArrayList;
import java.util.List;

public class Loader {

	private String var = "english";

	public Loader(String language) {
		var = language;
		load();
	}

	public String getLang() {
		return var;
	}

	public Lang lang = new Lang();

	public void load() {
		String file = "./res/langs.csv";

		for (String word : CSV.getColumn(var, file)) {
			lang.getVars().add(CSV.getKey(var, word, file) + "=" + word);
		}

	}

	public static class Lang {

		public List<String> vars = new ArrayList<>();

		public List<String> getVars() {
			return vars;
		}

		public String get(String var) {
			for (String string : vars) {
				if (string.split("=")[0].equals(var))
					return string.split("=")[1];
			}
			return "NaN";
		}

	}

}
