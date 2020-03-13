package com.model.kripke;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.main.KeyWord;

public class StateK {

	private Set<StateK> successors;
	private Set<String> succ;
 	//private Set<TransitionK> predecesseurs;
	private Set<String> parameters;
	
	private boolean init;
	
	public StateK(Set<String> param){
		parameters = param;
		successors = new HashSet<StateK>();
		succ = new HashSet<String>();
		//predecesseurs = new LinkedHashSet<TransitionK>();
		init = false;
	}
	
	public void addSucc(String state) {
		succ.add(state);
	}
	
	public Set<String> getSucc(){
		return succ;
	}
	
	public void addSuccessor(StateK st) {
		successors.add(st);
	}
	
	public Set<StateK> getSuccessors(){
		return successors;
	}
	
	public Set<String> getParameters(){
		return parameters;
	}

	public String getValue(String parameter) {
		//HashSet<String> val = new HashSet<String>();
		for (String param: parameters) {
				String leftpart = "";
				String rightpart = "";
				if (param.contains("=")){
					leftpart = param.substring(0, param.indexOf("="));
					rightpart = param.substring(param.indexOf("=")+1);
				}
				else {
					System.err.println("param have to contain a \"=\"");
				}
				if (parameter.equals(leftpart)) {
					//val.add(rightpart.replaceAll("\"", ""));
					return rightpart.replaceAll("\"", "");
				}
		}
		//System.err.println("stateK : " + this.toString() + "doesn't contain the parameter :" + parameter);
		return "FALSE";
	}
		
	public void setInit() {
		init = true;
	}
	
	public boolean isInit() {
		return init;
	}
	
	public boolean equals(StateK st){
		return getParameters().equals(st.getParameters());
	}
	
	public void transformLabel(Set<String> kparams, HashMap<String, KeyWord> keyWord) {
		Set<String> list = new HashSet<String>();
		for (String param: parameters) {
			String leftpart = "";
			if (param.contains("=")){
				leftpart = param.substring(0, param.indexOf("="));
			}
			else {
				leftpart = param;
			}
			list.add(leftpart);
		}
		Set<String> save = new HashSet<String>(kparams);
		save.removeAll(list);
		for (String param: save) {
			//System.out.println(param);
			/*if (!param.equals("state") & !param.equals("event") && keyWord.get(param.substring(0,param.indexOf("_"))).getBool() == 0) { //TODO automating the detection of non-boolean
				parameters.add(param + "=NULL");
			}
			else {*/
				parameters.add(param + "=FALSE");
			//}
		}
	}
	
	public String toString(){
		String res = "";
		for (String param : parameters) {
			res = res + "," + param;
		}
		return res.replaceFirst(",", "");
	}
	
	/*public Set<StateK> getPredecessors(){
		// TODO for easier tranformation into NuSMV
	}*/
	
	
}
