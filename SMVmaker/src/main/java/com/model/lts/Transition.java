package com.model.lts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Transition {

	private String name;
	private String label;
	private String[] parameters;
	
	private State source;
	private State target;
	
	public Transition() {
		source = new State();
		target = new State();		
	}
	
	public Transition(State src, String trans, State dst) {
		parameters = new String[3];
		//name = trans;
		label = trans.substring(0,trans.indexOf("("));
		//parameters[0] = trans.substring(trans.indexOf("(")+1,trans.lastIndexOf(")"));//.split(";", 0);		
		parameters[0] = "event=" + trans;// TODO remove char " if in the event
		parameters[1] = "from=" + getFrom(trans);
		parameters[2] = "to=" + getTo(trans);
		updateName();
		source = src;
		target = dst;
	}
	
	private String getFrom(String trans) {
		String from = "none" ;
		int d = trans.indexOf("Host=");
		if (d != -1) {
			if (trans.indexOf(";", d+5) > d) {
				from = trans.substring(d + 5, trans.indexOf(";", d + 5));
			}
			else {
				from = trans.substring(d + 5, trans.indexOf(")", d + 5));
			}
		}
		return from;
	}
	
	private String getTo(String trans) {
		String to = "none" ;
		int d = trans.indexOf("Dest=");
		if (d != -1) {
			if (trans.indexOf(";", d+5) > d) {
				to = trans.substring(d + 5, trans.indexOf(";", d + 5));
			}
			else {
				to = trans.substring(d + 5, trans.indexOf(")", d + 5));
			}
		}
		return to;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String newname) {
		name = newname;
	}
	
	public void updateName() {
		String newname = "";// label + "(";
		newname = newname + parameters[0];
		for (int i = 1; i < parameters.length; ++i) {
			newname = newname + "," + parameters[i];
		}
		//newname = newname + ")";
		name = newname;
	}
	
	
	public String getLabel() {
		return label;
	}
	
	public boolean contain(String... strings) {//TODO 	eviter les doublons
		for (String param : oldParameters()) {
			String leftpart = "";
			if (param.contains("=")){
				leftpart = param.substring(0, param.indexOf("="));
			}
			else {
				leftpart = param;
			}
			for (String word : strings) {
				if (leftpart.equals(word)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private Set<String> oldParameters(){
		Set<String> param = new HashSet<String>();
		Collections.addAll(param, name.substring(name.indexOf("(")+1,name.lastIndexOf(")")).split(";", 0));
		return param;		
	}
	
	/*public boolean equals(Set<String> params) {
		
	}*/

	// never used 
	public void setLabel(String newlabel) {
		label = newlabel;	
		updateName();
	}
	
	public String[] getParameters() {
		return parameters;
	}
	
	public void addParameter(String newparam) {
		String[] newparameters = new String[parameters.length + 1];
		for (int i = 0; i < parameters.length; ++i ) {
			newparameters[i] = parameters[i];
		}
		newparameters[parameters.length] = newparam;
		parameters = newparameters;
		updateName();
	}
	
	//never used 
	public void setParameters(String[] newparam) {
		parameters = newparam;
	}

	
	public State getSource() {
		return source;
	}
	
	public void setSource(State newsource) {
		source = newsource;
	}
	
	public State getTarget() {
		return target;
	}
	
	public void setTarget(State newtarget) {
		target = newtarget;
	}
	
	public boolean equals(String t) {
		return name.equals(t);
	}
	
	public String toString() {
		return name;				
	}
	
	public boolean isInput() {
		return label.startsWith("?");
	}
	
	public boolean isReq() {
		return !name.contains("esponse");
	}
	
	public boolean isOk() {
		if (!isReq()) {
			return name.contains("=OK");
		}
		return false;
	}
	
}
