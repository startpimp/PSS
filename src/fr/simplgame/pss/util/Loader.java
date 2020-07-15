package fr.simplgame.pss.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Loader {

	private String var = "english";

	public Loader(String language) {
		var = language;
		load();
	}

	public Loader() {
		load();
	}

	public String getLang() {
		return var;
	}

	public Lang lang = new Lang();

	public void load() {
		String file = "./res/langs.csv";
		lang.getVars().clear();

		for (String word : Objects.requireNonNull(CSV.getColumn(var, file))) {
			System.out.println(var + ";" + word);
			lang.vars.put(CSV.getKey(var, word, file), word);
		}

	}

	public static class Lang {

		public Map<String, String> vars = new HashMap<>();

		public Map<String, String> getVars() {
			return vars;
		}

		public String get(String var) {
			String str = vars.get(var);
			if (str == null)
				return "NaN";
			else
				return str;
		}

	}

}
