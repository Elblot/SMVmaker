package com.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import com.model.kripke.Kripke;
import com.model.kripke.StateK;

public class KripkeToNuSMV {
	
	public static void build(Kripke k, File output){
		try {
			BufferedWriter br = new BufferedWriter(new FileWriter(output));
			br.write("MODULE main\n");
			br.write(makeVar(k));
			br.write(makeInit(k));
			br.write(makeTrans(k));
			br.write(makeLTL());
			br.close();
		}catch (IOException e) {
			
		}
	}
	
	private static String makeVar(Kripke k) {
		String res = "VAR\n";
		Set<String> parameters = k.getParameters();
		for (String param: parameters) {
			Set<String> values = k.getValues(param);
			if (values.size() == 2 & values.contains("TRUE") & values.contains("FALSE")) {
				res = res + "\t" + param + " : boolean;\n";
			}
			else {
				String var = param + " : {";
				for (String value : values) {
					var = var + "\"" + value + "\", ";
				}
				var = var.substring(0, var.length() - 2);
				var = var + "};\n";
				res = res + "\t" + var;
			}
		}
		res = res + "\n";
		return res;
	}
	
	private static String makeInit(Kripke k) {
		String res = "INIT\n\t";
		for (StateK state : k.getInitialStates()) {
			res = res + "(" + getState(k, state) + ") | ";
		}
		res = res.substring(0, res.length() - 2);
		res = res + "\n\n";
		return res;
	}
	
	private static String getState(Kripke k, StateK state) {
		String res = "";
		for (String param : k.getParameters()) {
			res = res + param + " = ";
			String value = state.getValue(param);
			if (value.equals("TRUE") | value.equals("FALSE")){
				res = res + value;
			}
			else {
				res = res + "\"" + value + "\"";
			}
			res = res + " & ";
		}
		res = res.substring(0, res.length() - 2);
		return res;
	}
	
	private static String getNextState(Kripke k, StateK state) {
		String res = "";
		for (String param : k.getParameters()) {
			res = res + "next(" + param + ") = ";
			String value = state.getValue(param);
			if (value.equals("TRUE") | value.equals("FALSE")){
				res = res + value;
			}
			else {
				res = res + "\"" + value + "\"";
			}
			res = res + " & ";
		}
		res = res.substring(0, res.length() - 2);
		return res;
	}
	
	private static String makeTrans(Kripke k) {
		String res = "TRANS\n";
		res = res + "\tcase\n";
		
		for (StateK state: k.getStates()) {
			res = res + "\t\t" + getState(k, state) + " : (";
			for (StateK succ : state.getSuccessors()) {
				res = res + "(" + getNextState(k, succ) + ") | ";
			}
			res = res.substring(0, res.length() - 2);
			res = res + ");\n\n";
		}		
		res = res + "\t\tTRUE : FALSE;\n";
		res = res + "\tesac\n";
		return res;
	}
	
	private static String makeLTL() {
		String res = "";
		// TODO
		return res;
	}
}
