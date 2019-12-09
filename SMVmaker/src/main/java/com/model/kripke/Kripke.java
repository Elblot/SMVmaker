package com.model.kripke;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import com.model.lts.LTS;
import com.model.lts.State;
import com.model.lts.Transition;

public class Kripke {

	private Set<StateK> states;
	private Set<StateK> init;


	public Kripke (LTS lts) {
		states = new HashSet<StateK>();
		init = new HashSet<StateK>();
		/* create the states */
		for (State st : lts.getStates()) {
			for(Transition tr : st.getSuccesseurs()) {
				Set<String> param = new HashSet<String>();
				param.add("state=" + st.getLabel());
				Collections.addAll(param, tr.getParameters());
				StateK state = new StateK(param);
				state.addSucc(tr.getTarget().getLabel());
				StateK simi = contains(state);				
				if (simi == null) {
					if (st.isInit()){
						state.setInit();
						init.add(state);
					}
					states.add(state);
					state.addSucc(tr.getTarget().getLabel());
				}
				else if (!simi.getSucc().contains(tr.getTarget().getLabel())){
					//System.out.println("simi:" + simi.toString());
					simi.addSucc(tr.getTarget().getLabel());
				}
			}
		}
		/* make the transitions */
		for (StateK st : states) {
			//System.out.println(st.toString());
			//System.out.println(st.getSucc());
			for (String succ : st.getSucc()) {
				for (StateK st2 : states) {
					String label = "state=" + succ;
					if (st2.getParameters().contains(label)) {
						st.addSuccessor(st2);
					}
				}
			}
		}
		transformLabels();
	}

	/*
	 * transform the label for NuSMV
	 * TODO
	 */
	private void transformLabels(){
		Set<String> keyWords = getParameters();
		for (StateK st : getStates()) {
			st.transformLabel(keyWords);
		}
		//System.out.println(keyWords);
	}
	
	public Set<String> getParameters(){
		Set<String> keyWords = new HashSet<String>();
		for (StateK st : getStates()) {
			for (String param: st.getParameters()) {
				String leftpart = "";
				if (param.contains("=")){
					leftpart = param.substring(0, param.indexOf("="));
				}
				else {
					System.err.println("Warning : all parameters should contain a \"= \"");
					leftpart = param;
				}
				keyWords.add(leftpart);
			}
		}
		return keyWords;
	}
	
	public Set<String> getValues(String param){
		if (!getParameters().contains(param)) {
			System.err.println("unknown parameter : " + param);
			return null;
		}
		Set<String> res = new HashSet<String>();
		for (StateK state: states) {
			res.add(state.getValue(param));
		}
		
		
		return res;
	}
	

	/* verify if an equivalent state is already in the model
	 * and return it if it is the case */
	private StateK contains(StateK state) {
		for (StateK st : states) {
			if (st.equals(state)){
				return st;
			}
		}
		return null;
	}

	public Set<StateK> getStates(){
		return states;
	}

	public Set<StateK> getInitialStates(){
		return init;
	}

}
