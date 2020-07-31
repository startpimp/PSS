package fr.simplgame.pss.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Fichier permettant la lecture des fichiers CSV sans aucune dificult�
 * 
 * @author StartPimp47
 *
 */
public class CSV {
	/**
	 * S�parateur logique de chaque donn�es par ligne Par d�faut, il est sur ' ; '.
	 */
	public static String separator = ";";

	private static String changeFile(String file) {
		return file.replace("./", "C:\\Users\\Megaport\\Documents\\BOTS\\PSS\\");
	}

	/**
	 * Permet de lire le contenu d'une cellule
	 *
	 * @param key  Nom de ligne (ID)
	 * @param var  Nom de colonne
	 * @param file Chemin vers le fichier cibl�
	 * @return Retourne le contenu de la cellule. Si inconnu, alors "NaN"
	 */
	public static String getCell(String key, String var, String file) {
		int column = -1;

		file = changeFile(file);
		try (FileInputStream fis = new FileInputStream(new File(file))) {

			Scanner sc = new Scanner(fis, "UTF-8");
			while (sc.hasNextLine()) {

				String line = sc.nextLine();
				String[] vars = line.split(separator);

				if (column == -1) {

					for (int i = 0; i < vars.length; i++) {

						if (vars[i].equals(var))
							column = i;

					}

				} else {

					if (vars[0].equals(key)) {

						sc.close();
						return vars[column];

					}

				}

			}
			sc.close();

		} catch (Exception e) {

			e.printStackTrace();
			return "NaN";

		}

		return "NaN";
	}

	/**
	 * R�cup�ration d'un ligne enti�re
	 * 
	 * @param key  Nom de ligne (ID)
	 * @param file Chemin vers le fichier cibl�
	 * @return Retourne la ligne sous forme de String avec le s�parateur. Si
	 *         inconnu, alors "NaN"
	 */
	public static String getLine(String key, String file) {

		file = changeFile(file);
		try (FileInputStream fis = new FileInputStream(new File(file))) {

			Scanner sc = new Scanner(fis);
			while (sc.hasNextLine()) {

				String line = sc.nextLine();
				String[] vars = line.split(separator);

				if (vars[0].equals(key)) {

					sc.close();
					return new String(line.getBytes(), StandardCharsets.UTF_8);

				}

			}

			sc.close();

		} catch (Exception e) {

			e.printStackTrace();
			return "NaN";

		}

		return "NaN";
	}

	/**
	 * R�cup�ration d'un colonne enti�re
	 * 
	 * @param title Nom de colonne
	 * @param file  Chemin vers le fichier cibl�
	 * @return Retourne la colonne sous forme de List. Si inconnu, alors null
	 */
	public static List<String> getColumn(String title, String file) {

		file = changeFile(file);
		List<String> response = new ArrayList<>();
		int column = -1;

		try (FileInputStream fis = new FileInputStream(new File(file))) {

			Scanner sc = new Scanner(fis);
			while (sc.hasNextLine()) {

				String line = sc.nextLine();
				String[] vars = line.split(separator);

				if (column == -1) {

					for (int i = 0; i < vars.length; i++) {

						if (new String(vars[i].getBytes(), StandardCharsets.UTF_8).equals(title))
							column = i;

					}

				} else {

					try {

						response.add(new String(vars[column].getBytes(), StandardCharsets.UTF_8));

					} catch (ArrayIndexOutOfBoundsException e) {

						response.add("NaN");

					}

				}

			}

			sc.close();
			return response;

		} catch (Exception e) {

			e.printStackTrace();
			return null;

		}
	}

	/**
	 * R�cup�ration de l'ID de la cellule
	 * 
	 * @param var  Nom de colonne
	 * @param word Contenu de la cellule
	 * @param file Chemin vers le fichier ciblé
	 * @return Retourne l'ID de la cellule. Si inconnu, alors "NaN"
	 */
	public static String getKey(String var, String word, String file) {

		file = changeFile(file);
		int column = -1;

		try (FileInputStream fis = new FileInputStream(new File(file))) {

			Scanner sc = new Scanner(fis);
			while (sc.hasNextLine()) {

				String line = sc.nextLine();
				String[] vars = line.split(separator);

				if (column == -1) {

					for (int i = 0; i < vars.length; i++) {

						if (new String(vars[i].getBytes(), StandardCharsets.UTF_8).equals(var))
							column = i;

					}

				} else {

					for (String s : vars) {

						if (s.equals(word)) {

							sc.close();
							return new String(vars[0].getBytes(), StandardCharsets.UTF_8);

						}

					}

				}

			}

			sc.close();

		} catch (Exception e) {

			e.printStackTrace();
			return "NaN";

		}

		return "NaN";
	}

	/**
	 * Ajouter une ligne au fichier
	 * 
	 * @param line Ligne enti�re sous forme de String. Chaque donn�e devra �tre
	 *             s�par�e par le s�parateur principal
	 * @param file chemin vers le fichier cibl�
	 */
	public static void addLine(String line, String file) {
		file = changeFile(file);

		try (PrintWriter pw = new PrintWriter(new FileOutputStream(new File(file), true))) {
			pw.println(new String(line.getBytes(), StandardCharsets.UTF_8));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		System.out.println("[CSV] New line added to \"" + file + "\" : " + line);
	}

	/**
	 * Modifier une cellule
	 * 
	 * @param key     Nom de ligne (ID)
	 * @param var     Nom de colonne
	 * @param content Nouveau contenu
	 * @param file    Chemin vers le fichier ciblé
	 */
	public static void modifyCell(String key, String var, String content, String file) {
		file = changeFile(file);
		List<String> lines = new ArrayList<>();
		// Getting all lines
		try (FileInputStream fis = new FileInputStream(new File(file))) {
			Scanner sc = new Scanner(fis);
			while (sc.hasNextLine())
				lines.add(sc.nextLine());
			sc.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		String modifiedLine = null;
		// Getting modified line by his key
		for (String line : lines) {
			String lineKey = line.split(separator)[0];
			if (lineKey.equals(key))
				modifiedLine = line;
		}

		int columnPosition = -1;
		// Getting column position by his name
		for (String name : lines.get(0).split(separator)) {
			columnPosition++;
			if (name.equals(var))
				break;
		}

		assert modifiedLine != null;
		StringBuilder newLine = new StringBuilder(modifiedLine.split(separator)[0]);
		// Modify cell by his key and column position
		for (int i = 1; i < modifiedLine.split(separator).length; i++) {
			if (i == columnPosition)
				newLine.append(separator).append(content);
			else
				newLine.append(separator).append(modifiedLine.split(separator)[i]);
		}

		// Rewriting file with modified contents
		OutputStream os = null;
		try {
			os = new FileOutputStream(new File(file));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		assert os != null;
		try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
			for (String line : lines) {
				if (line.split(separator)[0].equals(key))
					pw.println(newLine);
				else
					pw.println(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		System.out.println("[CSV] Cell modified to \"" + file + "\" at key:" + key + " ; column:" + var
				+ " with content:" + content);
	}
}
