package com.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import com.main.LTLProperty;
import com.model.kripke.Kripke;
import com.model.kripke.StateK;

public class KripkeToNuSMV {
	
	public static void build(Kripke k, File output, Set<String> param, HashMap<String, LTLProperty> properties){
		try {
			BufferedWriter br = new BufferedWriter(new FileWriter(output));
			br.write("MODULE main\n");
			br.write(makeVar(k, param));
			br.write(makeInit(k, param));
			br.write(makeTrans(k, param));
			br.write(makeLTL(properties, param));
			br.close();
		}catch (IOException e) {
			
		}
	}
	
	private static String makeVar(Kripke k, Set<String> parameters) {
		String res = "VAR\n";
		//Set<String> parameters = k.getParameters();
		for (String param: parameters) {
			Set<String> values = k.getValues(param);
			if (values.size() == 0 | (values.size() == 2 & values.contains("TRUE") & values.contains("FALSE")) |
				values.size() == 1 & ( values.contains("TRUE") | values.contains("FALSE"))) {
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
	
	private static String makeInit(Kripke k, Set<String> parameters) {
		String res = "INIT\n\t";
		for (StateK state : k.getInitialStates()) {
			res = res + "(" + getState(k, state, parameters) + ") | ";
		}
		res = res.substring(0, res.length() - 2);
		res = res + "\n\n";
		return res;
	}
	
	private static String getState(Kripke k, StateK state, Set<String> parameters) {
		String res = "";
		for (String param : parameters) {
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
	
	private static String getNextState(Kripke k, StateK state, Set<String> parameters) {
		String res = "";
		for (String param : parameters) {
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
	
	private static String makeTrans(Kripke k, Set<String> parameters) {
		String res = "TRANS\n";
		res = res + "\tcase\n";
		
		for (StateK state: k.getStates()) {
			res = res + "\t\t" + getState(k, state, parameters) + " : (";
			for (StateK succ : state.getSuccessors()) {
				res = res + "(" + getNextState(k, succ, parameters) + ") | ";
			}
			res = res.substring(0, res.length() - 2);
			res = res + ");\n\n";
		}		
		res = res + "\t\tTRUE : FALSE;\n";
		res = res + "\tesac\n";
		return res;
	}
	
	private static String makeLTL(HashMap<String, LTLProperty> properties, Set<String> param) {
		String res = "";
		for (LTLProperty prop: properties.values()) {
			res = res + "\n/-- " + prop.getName() + " : " + prop.getDesc() + " --/\n";
			res = res + "LTLSPEC\n" + prop.getProperty() + "\n";
		}
		return res;
	}
}
