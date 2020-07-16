package fr.simplgame.pss.util;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class Sys {

    public static PrintStream out;

    public Sys() {
        try {
            Sys.out = new PrintStream(System.out, true, "UTF-8");
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
