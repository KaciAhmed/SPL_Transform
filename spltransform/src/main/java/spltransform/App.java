package spltransform;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class App {

	private static final String DEBUT_ANNOTATION_NON_ESPACER = "//";
	private static final String DEBUT_ANNOTATION_ESPACER = "//\\s++";
	public static final String REGEX_TAB = "\\t++";
	public static final String STRING_VIDE = "";
	public static final String ESPACE = " ";
	public static int nbFichierParser = 0;
	public static String inputDirectory = STRING_VIDE;
	private static List<String> lignesTotalsFichier = null;

	public static void main(String[] args) {

		if (args.length > 0) {
			inputDirectory = args[0];
			App app = new App();
			app.transformer(inputDirectory);
		} else {
			System.out.println("Usage <path/to/code/>");
		}
	}

	public void transformer(String inputDirectory) {
		File dossier = new File(inputDirectory);
		this.lireDossier(dossier);
	}

	/****************************
	 * Lecture dossier
	 ***************************************/

	private void lireDossier(File dossier) {
		String nomDeFichier;
		List<String> lignesFichier = null;

		for (File fichier : dossier.listFiles()) {
			if (!fichier.isDirectory()) {
				if (this.estFichierVoulu(fichier)) {
					String nomFichier = "-------------------------" + fichier.getName()
							+ "-----------------------------";
					System.out.println(nomFichier);
					nomDeFichier = fichier.getAbsolutePath();
					lignesFichier = this.lireFichier(nomDeFichier);
					lignesFichier = this.traitementDuFichier(lignesFichier);
					this.ecrireFichiersResultat(nomDeFichier, lignesFichier);
				}
			}
		}
	}

	/****************************
	 * Lecture fichier
	 ***************************************/

	private List<String> lireFichier(String nomDeFichier) {
		List<String> contenuFichier = new ArrayList<String>();
		try {
			FileReader fileReader = new FileReader(nomDeFichier);
			InputStreamReader iReader = new InputStreamReader(new FileInputStream(nomDeFichier), "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(iReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				contenuFichier.add(line);
			}
			iReader.close();
			fileReader.close();
		} catch (Exception e) {
			System.out.println("La lecture du dossier c'est mal passé : " + e.getMessage());
			e.printStackTrace();
		}
		return contenuFichier;
	}

	private List<String> traitementDuFichier(List<String> lignesFichier) {
		List<String> newList = new ArrayList<String>();
		String[] words;
		String[] words2;
		for (String ligne : lignesFichier) {
			if (ligne.contains("/*if[")) {
				words = ligne.split("\\]\\*/");
				newList.add(words[0].replaceAll("/\\*if\\[", "//#if "));
				if (words.length > 1) {
					words2 = words[1].split("/\\*end\\[");
					newList.add(words2[0]);
					if (words2.length > 1) {
						words2[1] = "//#endif ";
						newList.add(words2[1]);
					}
				}
			} else {
				if (ligne.contains("/*end[")) {
					words = ligne.split("/\\*end\\[");
					if (words.length == 1) {
						words[0] = "//#endif ";
						newList.add(words[0]);
					} else {
						newList.add(words[0]);
						if (words.length > 1) {
							words[1] = "//#endif ";
							newList.add(words[1]);
						}
					}
				} else {
					newList.add(ligne);
				}
			}
		}
		return newList;
	}

	public void ecrireFichiersResultat(String output, List<String> lignesFichier) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(output, "UTF-8");
			for (String ligne : lignesFichier) {
				writer.println(ligne);
			}
			writer.close();
		} catch (FileNotFoundException e) {
			System.out.println("Le fichier de sortie n'a pas été crée : " + e.getMessage());
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			System.out.println("L'encodage de sortie n'a pas été reconnu : " + e.getMessage());
			e.printStackTrace();
		}
	}

	private boolean estFichierVoulu(File fichier) {

		final String extensionFichierVoulu = ".java";
		final int indiceFinFichier = fichier.getName().length();
		final int indiceExtensionFichier = indiceFinFichier - extensionFichierVoulu.length();
		final String fichierExtension = fichier.getName().substring(indiceExtensionFichier, indiceFinFichier);

		return fichierExtension.equals(extensionFichierVoulu);
	}

}
