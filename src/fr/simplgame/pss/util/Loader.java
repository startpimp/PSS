package fr.simplgame.pss.util;

public class Loader {
	
	private String var = "english";
	
	public Loader(String language) {
		var = language;
		load();
	}
	
	public void load() {
		String file = "./res/langs.csv";
		
		Lang.HELP_TITLE = CSV.getCell("help_title", var, file);
	}
	
	public static class Lang {
		
		public static String HELP_TITLE = "";
		
	}

}
