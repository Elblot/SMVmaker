package com.model.kripke;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
					return rightpart;
				}
		}
		System.err.println("stateK : " + this.toString() + "doesn't contain the parameter :" + parameter);
		return null;
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
	
	public void transformLabel(Set<String> keyWords) {
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
		Set<String> save = new HashSet<String>(keyWords);
		save.removeAll(list);//TODO clone it?
		for (String param: save) {
			parameters.add(param + "=FALSE");
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
