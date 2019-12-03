package com.main;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.drools.DroolsTest;
import com.model.GenerateDOT;
import com.model.KripkeToNuSMV;
import com.model.kripke.Kripke;
import com.model.lts.LTS;
import com.option.ProgOptions;

public class Main {

	public static String dir;
	public static boolean gen;	
	public static String output;
	
	public static void main(String[] args) {
		try {
			ProgOptions.setOptions(args);
		} catch (Exception e) {
			System.err.println("pb option");
			System.exit(3);
		}
		System.out.println(dir);
		File input = new File(dir);
		LTS lts = DroolsTest.runDrools(input, gen);
		if (gen) {
			GenerateDOT.printDot(lts, "ModifiedLabel.dot");
		}
		Kripke k = new Kripke(lts);
		if (gen) {
			GenerateDOT.printDot(k, "Kripke.dot");
		}
		File out = new File(output);
		KripkeToNuSMV.build(k, out);
		
		/** TODO 
		 * run NuSMV on the file generated
		 * 
		 * assess the results and give information according to the results 
		 * (number success, which fail, trace in case of failure, advise to 
		 * remediate, warning if too few data in the model,...)
		 */
		
	}

}
