package fr.simplgame.pss.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

	public static String hash(String password) {
		String generatedPassword = null;
		try {

			MessageDigest md = MessageDigest.getInstance("MD5");

			md.update(password.getBytes());

			byte[] bytes = md.digest();

			StringBuilder sb = new StringBuilder();
			for (byte aByte : bytes) {
				sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
			}

			generatedPassword = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return generatedPassword;
	}

}
