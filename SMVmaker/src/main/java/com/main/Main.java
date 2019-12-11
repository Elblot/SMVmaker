package com.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.drools.DroolsExec;
import com.model.GenerateDOT;
import com.model.KripkeToNuSMV;
import com.model.kripke.Kripke;
import com.model.lts.LTS;
import com.option.ProgOptions;

public class Main {

	public static String dir;
	public static boolean gen;	
	public static String output;
	public static String dep;

	public static void main(String[] args) {
		try {
			ProgOptions.setOptions(args);
		} catch (Exception e) {
			System.err.println("pb option");
			System.exit(3);
		}
		System.out.println(dir);
		File input = new File(dir);
		LTS lts = DroolsExec.runDrools(input, gen);
		if (gen) {
			GenerateDOT.printDot(lts, "ModifiedLabel.dot");
		}
		Set<String> compo = lts.getCompo();


		HashMap<String, KeyWord> keyWords = makeKeyWords();

		Kripke k = new Kripke(lts, keyWords);

		Set<KeyWord> missing = new HashSet<KeyWord>();
		HashMap<String, LTLProperty> properties = makeProperties(keyWords, compo);
		Set<String> param = k.getParameters();
		Set<LTLProperty> LTLmissing = new HashSet<LTLProperty>();
		for (String k2: keyWords.keySet()) {
			if (!param.contains(k2) & keyWords.get(k2).getNecessary() == 0) {
				missing.add(keyWords.get(k2));
				LTLmissing.addAll(keyWords.get(k2).getLinks());
			}
			/*else if (!param.contains(k2) & keyWords.get(k2).getNecessary() == 1) {
				param.add(k2);
			}*/
		}


		if (!missing.isEmpty()) {
			System.out.println("following keywords are missing in the model:");
			for (KeyWord k2: missing) {
				System.out.println(k2.toString());
			}
			System.out.println("Consequently the following properties, using these keywords will not be verified : ");
			for (LTLProperty ltl : LTLmissing) {
				System.out.println(ltl);
				properties.remove(ltl.getName());
			}
		}

		System.out.println("param :" + param);

		if (gen) {
			GenerateDOT.printDot(k, "Kripke.dot");
		}

		File out = new File(output);
		KripkeToNuSMV.build(k, out, param, properties);


		/** TODO 
		 * run NuSMV on the file generated
		 * 
		 * assess the results and give information according to the results 
		 * (number success, which fail, trace in case of failure, advise to 
		 * remediate, warning if too few data in the model,...)
		 */

	}

	private static HashMap<String, LTLProperty> makeProperties(HashMap<String, KeyWord> keyWords, Set<String> components){
		HashMap<String, LTLProperty> properties = new HashMap<String, LTLProperty>();
		File k = new File(ClassLoader.getSystemClassLoader().getResource("LTLproperties").getFile());
		Set<String> dependencies = makeDep();
		System.out.println("dep : " + dependencies);
		System.out.println("compo : " + components);
		try {
			BufferedReader br = new BufferedReader(new FileReader(k));
			String line = br.readLine();
			while (line != null) {
				String name = line.substring(0, line.indexOf(";;;"));
				line = line.substring(line.indexOf(";;;") + 3);
				String prop = line.substring(0, line.indexOf(";;;"));
				String desc = line.substring(line.indexOf(";;;") + 3);
				if (prop.contains("*dep*")) {
					if (!dependencies.isEmpty()){
						for (String deps:dependencies) {
							String prop2 = prop.replaceAll("[*]dep[*]", "\"" + deps + "\"");
							properties.put(name + "(" + deps + ")", new LTLProperty(name + "(" + deps + ")", prop2, desc, keyWords));
						}
					}
				}
				else if (prop.contains("*compo*")) {
					for (String compo : components) {
						String prop2 = prop.replaceAll("[*]compo[*]", "\"" + compo + "\"");
						properties.put(name + "(" + compo + ")", new LTLProperty(name + "(" + compo + ")", prop2, desc, keyWords));
					}
				}
				else {
					properties.put(name, new LTLProperty(name, prop, desc, keyWords));
				}
				line = br.readLine();

			}

			br.close();
		}catch (IOException e) {
			System.err.println("file src/main/resources/keyWords not found");
			System.exit(3);
		}
		return properties;
	}

	private static Set<String> makeDep(){
		Set<String> res = new HashSet<String>();
		if (dep!=null) {
			System.out.println (dep);
			File file = new File(dep);
			res = DAGmapper.mapping(file);
		}
		return res;
	}

	private static HashMap<String, KeyWord> makeKeyWords(){
		HashMap<String, KeyWord> keyWords = new HashMap<String, KeyWord>();
		File k = new File(ClassLoader.getSystemClassLoader().getResource("keyWords").getFile());
		try {
			BufferedReader br = new BufferedReader(new FileReader(k));
			String line = br.readLine();
			while (line != null) {
				String name = line.substring(0, line.indexOf("|"));
				line = line.substring(line.indexOf("|") + 1);
				int necessary = Integer.valueOf(line.substring(0, line.indexOf("|")));
				line = line.substring(line.indexOf("|") + 1);
				if (line.contains("|")) {
					String description = line.substring(0, line.indexOf("|"));
					String scenario = line.substring(line.indexOf("|") + 1);
					keyWords.put(name, new KeyWord(name, necessary, description, scenario));
				}
				else {
					keyWords.put(name, new KeyWord(name, necessary));
				}
				line = br.readLine();
			}

			br.close();
		}catch (IOException e) {
			System.err.println("file src/main/resources/keyWords not found");
			System.exit(3);
		}

		return keyWords;
	}

}
