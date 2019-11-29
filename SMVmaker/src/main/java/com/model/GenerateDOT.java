/*
 *  GenerateDOT.java
 * 
 *  Copyright (C) 2012-2013 Sylvain Lamprier, Tewfik Ziaidi, Lom Messan Hillah and Nicolas Baskiotis
 * 
 *  This file is part of CARE.
 * 
 *   CARE is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   CARE is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with CARE.  If not, see <http://www.gnu.org/licenses/>.
 */


package com.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Set;

import com.model.kripke.Kripke;
import com.model.kripke.StateK;
import com.model.lts.LTS;
import com.model.lts.State;
import com.model.lts.Transition;


public class GenerateDOT {

	static PrintStream out;

	public GenerateDOT(){}

	/**
	 * Print DOT file from lts
	 */
	public static void printDot(LTS lts, String fileName) {
		//System.out.println("Ecriture dot file dans "+fileName);
		State initialState = lts.getInitialState();
		try {
			File dotFile = new File(fileName);
			FileOutputStream fout = new FileOutputStream(dotFile);
			out = new PrintStream(fout);
			out.println("digraph LTS {");
			out.println("S00" + "[shape=point]");
			for (State st: lts.getStates()){
				String label = st.getLabel();
				out.println(label + "[label=" + label + ",shape=circle];");
			}
			out.println("S00 -> "+initialState.getLabel());
			for (Transition trs : lts.getTransitions()) {
				String source = trs.getSource().getLabel();
				String target = trs.getTarget().getLabel();
				String eventLabel = trs.getName();
				out.println(source + " -> " + target + "[label =\"" + eventLabel
						+  "\"];");
			}
			out.println("}");
			fout.close();
		} catch (IOException o) {
			o.getStackTrace();
		}
	}
	
	/**
	 * Print DOT file from Kripke
	 */
	public static void printDot(Kripke k, String fileName) {
		//System.out.println("Ecriture dot file dans "+fileName);
		Set<StateK> initialStates = k.getInitialStates();
		
		for (StateK st: initialStates){
			//System.out.println(st.toString());
		}
		
		ArrayList<StateK> states = new ArrayList<StateK>(k.getStates());
		try {
			File dotFile = new File(fileName);
			FileOutputStream fout = new FileOutputStream(dotFile);
			out = new PrintStream(fout);
			out.println("digraph LTS {");
			for (int i = 0; i < initialStates.size(); ++i) {
				out.println("Init" + i + "[shape=point]");
			}
			for (StateK st: k.getStates()){
				String label = st.toString();
				int id = states.indexOf(st);
				out.println("S" + id  + "[label=\"" + label + "\"];");
			}
			int i = 0;
			for (StateK st: k.getStates()){
				int source = states.indexOf(st);
				if (st.isInit()) {
					out.println("Init" + i + " -> S" + source);
					++i;
				}
				for (StateK tg: st.getSuccessors()) {
					int target = states.indexOf(tg);
					out.println("S" + source + " -> S" + target);
				}
			}
			out.println("}");
			fout.close();
		} catch (IOException o) {
			o.getStackTrace();
		}
	}

}