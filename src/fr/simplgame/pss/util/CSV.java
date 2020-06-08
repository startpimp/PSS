package fr.simplgame.pss.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Fichier permettant la lecture des fichiers CSV sans aucune dificulté
 * 
 * @author StartPimp47
 *
 */
public class CSV {
	/**
	 * Séparateur logique de chaque données par ligne Par défaut, il est sur ' ; '.
	 */
	public static String separator = ";";

	/**
	 * Permet de lire le contenu d'une cellule
	 * 
	 * @param key  Nom de colonne
	 * @param var  Nom de ligne (ID)
	 * @param file Chemin vers le fichier ciblé
	 * @return Retourne le contenu de la cellule. Si inconnu, alors "NaN"
	 */
	public static String getCell(String key, String var, String file) {
		try (FileInputStream fis = new FileInputStream(new File(file))) {
			Scanner sc = new Scanner(fis);
			int column = -1;
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] vars = line.split(separator);
				if (column == -1) {
					for (int i = 0; i < vars.length; i++) {
						if (new String(vars[i].getBytes(), "UTF-8").equals(var))
							column = i;
					}
				} else {
					if (new String(vars[0].getBytes(), "UTF-8").equals(key)) {
						sc.close();
						return new String(vars[column].getBytes(), "UTF-8");
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
	 * Récupération d'un ligne entière
	 * 
	 * @param key  Nom de ligne (ID)
	 * @param file Chemin vers le fichier ciblé
	 * @return Retourne la ligne sous forme de String avec le séparateur. Si
	 *         inconnu, alors "NaN"
	 */
	public static String getLine(String key, String file) {
		try (FileInputStream fis = new FileInputStream(new File(file))) {
			Scanner sc = new Scanner(fis);
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] vars = line.split(separator);
				if (vars[0].equals(key)) {
					sc.close();
					return new String(line.getBytes(), "UTF-8");
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
	 * Récupération d'un colonne entière
	 * 
	 * @param title Nom de colonne
	 * @param file  Chemin vers le fichier ciblé
	 * @return Retourne la colonne sous forme de List. Si inconnu, alors null
	 */
	public static List<String> getColumn(String title, String file) {
		List<String> response = new ArrayList<>();
		try (FileInputStream fis = new FileInputStream(new File(file))) {
			Scanner sc = new Scanner(fis);
			int column = -1;
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] vars = line.split(separator);
				if (column == -1) {
					for (int i = 0; i < vars.length; i++) {
						if (new String(vars[i].getBytes(), "UTF-8").equals(title))
							column = i;
					}
				} else {
					response.add(new String(vars[column].getBytes(), "UTF-8"));
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
	 * Récupération de l'ID de la cellule
	 * 
	 * @param var  Nom de colonne
	 * @param word Contenu de la cellule
	 * @param file Chemin vers le fichier ciblé
	 * @return Retourne l'ID de la cellule. Si inconnu, alors "NaN"
	 */
	public static String getKey(String var, String word, String file) {
		try (FileInputStream fis = new FileInputStream(new File(file))) {
			Scanner sc = new Scanner(fis);
			int column = -1;
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] vars = line.split(separator);
				if (column == -1) {
					for (int i = 0; i < vars.length; i++) {
						if (new String(vars[i].getBytes(), "UTF-8").equals(var))
							column = i;
					}
				} else {
					for (int i = 0; i < vars.length; i++) {
						if (vars[i].equals(word)) {
							sc.close();
							return new String(vars[0].getBytes(), "UTF-8");
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
	 * @param line Ligne entière sous forme de String. Chaque donnée devra être
	 *             séparée par le séparateur principal
	 * @param file chemin vers le fichier ciblé
	 */
	public static void addLine(String line, String file) {
		try (PrintWriter pw = new PrintWriter(new FileOutputStream(new File(file), true))) {
			pw.println(new String(line.getBytes(), "UTF-8"));
			pw.close();
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

		String newLine = modifiedLine.split(separator)[0];
		// Modify cell by his key and column position
		for (int i = 1; i < modifiedLine.split(separator).length; i++) {
			if (i == columnPosition)
				newLine += separator + content;
			else
				newLine += separator + modifiedLine.split(separator)[i];
		}

		// Rewriting file with modified contents
		try (PrintWriter pw = new PrintWriter(new FileOutputStream(new File(file)))) {
			for (String line : lines) {
				if (line.split(separator)[0].equals(key))
					pw.println(newLine);
				else
					pw.println(line);
			}
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		System.out.println("[CSV] Cell modified to \"" + file + "\" at key:" + key + " ; column:" + var
				+ " with content:" + content);
	}
}
