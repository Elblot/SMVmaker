package com.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.drools.DroolsExec;
import com.model.GenerateDOT;
import com.model.KripkeToNuSMV;
import com.model.kripke.Kripke;
import com.model.lts.LTS;
import com.option.ProgOptions;
import com.utils.stringCleaner;

public class Main {

	public static String dir;
	public static boolean gen;	
	public static String output;
	public static String dep;
	public static HashMap<String, KeyWord> keyWords;

	public static void main(String[] args) {
		try {
			ProgOptions.setOptions(args);
		} catch (Exception e) {
			System.err.println("pb option");
			System.exit(3);
		}
		keyWords = makeKeyWords();
		File input = new File(dir);	
		//keyWords.get("getUpdate").addValue("testvalue");
		LTS lts = DroolsExec.runDrools(input, gen);
		//System.out.println(keyWords.toString());
		if (gen) {
			GenerateDOT.printDot(lts, "ModifiedLabel.dot");
		}
		Set<String> compo = lts.getCompo();
		Kripke k = new Kripke(lts, keyWords);
		//Set<KeyWord> missing = new HashSet<KeyWord>();
		Set<String> param = k.getParameters();
		HashMap<String, LTLProperty> properties = makeProperties(keyWords, compo, param);
		//Set<LTLProperty> LTLmissing = new HashSet<LTLProperty>();
		/*for (String k2: keyWords.keySet()) { TODO warning if property cannot be verified
			if (!param.contains(k2) & keyWords.get(k2).getNecessary() == 0) {
				missing.add(keyWords.get(k2));
				LTLmissing.addAll(keyWords.get(k2).getLinks());
			}
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
		}*/

		//System.out.println("param :" + param);

		if (gen) {
			GenerateDOT.printDot(k, "Kripke.dot");
		}

		File out = new File(output);
		//KripkeToNuSMV.build(k, out, k, properties);
		KripkeToNuSMV.build(k, out, param, properties);


		/** TODO 
		 * run NuSMV on the file generated
		 * 
		 * assess the results and give information according to the results 
		 * (number success, which fail, trace in case of failure, advise to 
		 * remediate, warning if too few data in the model,...)
		 */

	}

	private static HashMap<String, LTLProperty> makeProperties(HashMap<String, KeyWord> keyWords, Set<String> components, Set<String> param){
		HashMap<String, LTLProperty> properties = new HashMap<String, LTLProperty>();
		File k = new File(ClassLoader.getSystemClassLoader().getResource("LTLproperties").getFile());
		Set<String> dependencies = makeDep();
		//System.out.println("dep : " + dependencies);
		//System.out.println("compo : " + components);
		try {
			BufferedReader br = new BufferedReader(new FileReader(k));
			String line = br.readLine();
			while (line != null) {
				//System.out.println("\n"+line);
				String name = line.substring(0, line.indexOf(";;;"));
				line = line.substring(line.indexOf(";;;") + 3);
				String prop = line.substring(0, line.indexOf(";;;"));
				String desc = line.substring(line.indexOf(";;;") + 3);
				HashSet<String> vars = new HashSet<String>();
				if (prop.contains("*dep*")) {
					//System.out.println("chat");
					vars.add("dep");
				}
				if (prop.contains("*compo*")) {
					//System.out.println("chat");
					vars.add("compo");
				}
				if (prop.contains("*getUpdate*")) {
					vars.add("getUpdate");
				}
				if (prop.contains("*searchUpdate*")) {
					vars.add("searchUpdate");
				}
				if (prop.contains("*sensitive*")) {
					vars.add("sensitive");
				}
				if (prop.contains("*credential*")) {
					vars.add("credential");
				}
				if (prop.contains("*encrypted*")) {
					vars.add("encrypted");
				}
				if (prop.contains("*blackListedWord*")) {
					vars.add("blackListedWord");
				}
				if (prop.contains("*XSS*")) {
					vars.add("XSS");
				}
				if (prop.contains("*SQLinjection*")) {
					vars.add("SQLinjection");
				}
				if (vars.isEmpty()) {
					properties.put(name, new LTLProperty(name, prop, desc, keyWords));
				}
				else {
					HashSet<String> propertytype = new HashSet<String>();
					propertytype.add(prop);
					for (String var: vars) {
						HashSet<String> propertyinst = new HashSet<String>();
						for (String type: propertytype) {
							if (var.equals("dep")) {
								for (String deps : dependencies) {
									param.add(var + "_" + stringCleaner.clean(deps));
									propertyinst.add(type.replaceAll("[*]dep[*]", stringCleaner.clean(deps)));
								}
							}
							else if (var.equals("compo")) {
								for (String compo : components) {
									param.add(var + "_" + stringCleaner.clean(compo));
									propertyinst.add(type.replaceAll("[*]compo[*]", stringCleaner.clean(compo)));
								}
							}
							else {
								//System.out.println(var);
								//System.out.println(keyWords.toString());
								for (String value: keyWords.get(var + "_d").getValues()) {
									param.add(var + "_" + value);
									//System.out.println("var:" + var +  "\nvalue:" + value);
									propertyinst.add(type.replaceAll("[*]" + var + "[*]", value));
									
								}
							}
						}
						//System.out.println(propertyinst);
						propertytype = propertyinst;				
					}
					int i = 0;
					for (String propinst: propertytype) {
						properties.put(name + "(" + i + ")", new LTLProperty(name + "(" + i + ")", propinst, desc, keyWords));
						param.addAll(addParam(propinst));
						i++;
					}
				}
				line = br.readLine();
			}
			br.close();
		}catch (IOException e) {
			System.err.println("problem with file src/main/resources/keyWords");
			System.exit(3);
		}
		/*for (LTLProperty p : properties.values()) {
			System.out.println(p.getProperty());
		}*/
		return properties;
	}

	private static Set<String> addParam(String prop){
		HashSet<String> res = new HashSet<String>();
		String p = prop.replaceAll("[(]", " ");
		p = p.replaceAll("[)]", " ");
		p = p.replaceAll("->", " ");
		p = p.replaceAll("!", " ");
		String[] params = p.split(" ");
		for (String param : params){
			if (param.contains("_")) {
				res.add(param);
			}
		}
		return res;
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
		
		/*ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("keyWords");
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(inputStream, writer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String query = writer.toString();
		System.out.println(query);*/
		URL ress = ClassLoader.getSystemClassLoader().getResource("keyWords");
		
		
		File k = new File(ress.getFile());
		//File k = new File(.getResource("keyWords").getFile());
		//System.out.println(k);
		try {
			BufferedReader br = new BufferedReader(new FileReader(k));
			String line = br.readLine();
			while (line != null) {
				String name = line.substring(0, line.indexOf("|"));
				line = line.substring(line.indexOf("|") + 1);
				if (line.contains("|")) {
					String description = line.substring(0, line.indexOf("|"));
					if (line.contains("|")) {
						String scenario = line.substring(line.indexOf("|") + 1);
						keyWords.put(name, new KeyWord(name, description, scenario));
					}
				}
				else {
					keyWords.put(name, new KeyWord(name));
				}
				line = br.readLine();
			}

			br.close();
		}catch (IOException e) {
			System.err.println("problem with file src/main/resources/keyWords");
			System.exit(3);
		}

		return keyWords;
	}

}
