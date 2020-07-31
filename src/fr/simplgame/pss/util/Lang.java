package fr.simplgame.pss.util;

import java.util.ArrayList;

public class Lang {

    public String lang = "english";

    public ArrayList<String> vars = new ArrayList<>();


    public ArrayList<String> getVars() {
        return vars;
    }

    public String get(String var) {
        System.out.println(var + "=" + lang);
        return CSV.getCell(var, lang, "./res/langs.csv");
    }

}
