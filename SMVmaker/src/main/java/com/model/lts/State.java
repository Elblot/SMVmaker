package com.model.lts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class State {

	private Set<Transition> successeurs;
	private Set<Transition> predecesseurs;
	private String label;
	
	/* only for this program */
	private boolean init;
	
	public State() {
		successeurs = new LinkedHashSet<Transition>();
		predecesseurs = new LinkedHashSet<Transition>();
		init = false;
	}
	
	public State(String name){
		label = name;
		successeurs = new LinkedHashSet<Transition>();
		predecesseurs = new LinkedHashSet<Transition>();
		init = false;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void addSuccesseur(Transition t){
		if(t.getSource()!=this){
			throw new RuntimeException("add transition "+t+" as successor of "+this+" is problematic");
		}
		successeurs.add(t);
	}
	
	public void addPredecesseur(Transition t){
		if(t.getTarget()!=this){
			throw new RuntimeException("add transition "+t+" as predecessor of "+this+" is problematic");
		}
		predecesseurs.add(t);
	}
	
	public void removeSuccesseur(Transition t){
		successeurs.remove(t);
	}
	
	public void removePredecesseur(Transition t){
		predecesseurs.remove(t);
	}
	
	public void clearSuccesseurs(){
		successeurs=new LinkedHashSet<Transition>();
	}
	
	public void clearPredecesseurs(){
		predecesseurs=new LinkedHashSet<Transition>();
	}
	
	public ArrayList<Transition> getSuccesseurs(){
		return(new ArrayList<Transition>(successeurs));
	}
	
	/* return the target state of a transition labelled label form this state */
	public ArrayList<State> getSuccesseur(String label) {
		ArrayList<State> res = new ArrayList<State>();
		for (Transition succ : getSuccesseurs()) {
			if (succ.equals(label) || succ.getName().contains("call_C") || succ.getName().contains("return_C") ) {
				res.add(succ.getTarget());
			}
		}
		return res;
	}
	
	public ArrayList<Transition> getPredecesseurs(){
		return(new ArrayList<Transition>(predecesseurs));
	}
	
	public void setInit() {
		init = true;
	}
	
	public boolean isInit() {
		return init;
	}
	
	/** TODO
	public boolean equals(Object o){
		if(o==this) return true;
		else if((o==null) || (o.getClass() != this.getClass()))return false;
		else{
			State state = (State)o;
			
		//System.out.println(name+" "+state.getName());
		return this.label.equals(state.getLabel());
		}
	}*/
	
	public String toString(){
		return label;
	}
	
	
}
