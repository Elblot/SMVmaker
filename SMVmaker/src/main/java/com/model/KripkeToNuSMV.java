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
			System.out.println("begin writting");
			makeVar(br, k, param);
			System.out.println("param done");
			makeInit(br, k, param);
			System.out.println("init done");
			makeTrans(br, k, param);
			System.out.println("tran done");
			makeLTL(br, properties, param);
			System.out.println("done");
			br.close();
		}catch (IOException e) {
			System.err.println("Failed to write the smv file");
		}
	}

	private static void makeVar(BufferedWriter br, Kripke k, Set<String> parameters) {
		try{
			br.write("VAR\n");
			//Set<String> parameters = k.getParameters();
			for (String param: parameters) {
				Set<String> values = k.getValues(param);
				//System.out.println("param:" + param  + "\nvalue:" + values);
				if (values.size() == 0 | (values.size() == 2 & values.contains("TRUE") & values.contains("FALSE")) |
						values.size() == 1 & ( values.contains("TRUE") | values.contains("FALSE"))) {
					br.write("\t" + param + " : boolean;\n");
				}
				else {
					String var = param + " : {";
					for (String value : values) {
						var = var + "\"" + value + "\", ";
					}
					var = var.substring(0, var.length() - 2);
					var = var + "};\n";
					br.write("\t" + var);
				}
			}
			br.write("\n");
		}catch (IOException e) {
			System.err.println("Failed to write the smv file");
		}
		//return res;
	}

	private static void makeInit(BufferedWriter br, Kripke k, Set<String> parameters) {
		String res = "INIT\n\t";
		for (StateK state : k.getInitialStates()) {
			res = res + "(" + getState(k, state, parameters) + ") | ";
		}
		res = res.substring(0, res.length() - 2);
		res = res + "\n\n";
		try {
			br.write(res);
		}catch (IOException e) {
			System.err.println("Failed to write the smv file");
		}
	}

	private static String getState(Kripke k, StateK state, Set<String> parameters) {
		String res = "";
		for (String param : parameters) {
			res = res + param + " = ";
			String value = state.getValue(param);
			if (value != null && (value.contains("TRUE") | value.contains("FALSE"))){
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
			if (value != null && (value.contains("TRUE") | value.contains("FALSE"))){
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

	private static void makeTrans(BufferedWriter br, Kripke k, Set<String> parameters) {
		try {
			br.write("TRANS\n");
			br.write("\tcase\n");
			for (StateK state: k.getStates()) {
				String res = "\t\t" + getState(k, state, parameters) + " : (";
				for (StateK succ : state.getSuccessors()) {
					res = res + "(" + getNextState(k, succ, parameters) + ") | ";
				}
				res = res.substring(0, res.length() - 2);
				res = res + ");\n\n";
				br.write(res);
				//System.out.println("one trans:" + state.toString());
			}		
			br.write("\t\tTRUE : FALSE;\n");
			br.write("\tesac\n");
		}catch (IOException e) {
			System.err.println("Failed to write the smv file");
		}

		//return res;
	}

	private static void makeLTL(BufferedWriter br, HashMap<String, LTLProperty> properties, Set<String> param) {
		try {
			for (LTLProperty prop: properties.values()) {
				br.write("\n/-- " + prop.getName() + " : " + prop.getDesc() + " --/\n");
				br.write("LTLSPEC\n" + prop.getProperty() + "\n");
			}
		}catch (IOException e) {
			System.err.println("Failed to write the smv file");
		}
	}
}
