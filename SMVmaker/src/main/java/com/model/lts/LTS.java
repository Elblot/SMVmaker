package com.model.lts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class LTS {

	/* initial state */
	private State initialState;

	/* Transitions list */
	//private HashMap<String, Transition> transitions;
	private Set<Transition> transitions;

	/* Set of states */
	private HashMap<String, State> states;
	

	public LTS() {
		transitions = new HashSet<Transition>();
		states = new HashMap<String, State>(); 
	}
	
	
	
	public void addTransitions(ArrayList<Transition> tr){
		for(Transition t:tr){
			addTransition(t);
		}
	}
	
	
	public void addTransition(Transition transition) {
		//String key = transition.getSource().toString() + transition.getName() + transition.getTarget().toString();
		transitions.add(transition);
	}

	public void addStates(ArrayList<State> st){
		for(State s:st){
			addState(s);
		}
	}

	public void addState(State state) {
		states.put(state.getLabel(), state);
	}

	public void setStates(ArrayList<State> new_states) {
		states = new HashMap<String, State>();
		for(State s:new_states){
			addState(s);
		}
	}

	public State getState(String st) {
		return states.get(st);
	}
	
	/* return the components that communicate with this one */
	public Set<String> getCompo() {
		Set<String> compo = new HashSet<String>();
		for (Transition t : transitions) {
			compo.add(t.getOtherCompo());
		}
		return compo;
	}
	
	/*	
	public void removeState(State state) {
		if (initialState==state){
			initialState=null;
			System.out.println("Plus d etat initial !!");
		}
		if (finalStates.contains(state)){
			finalStates.remove(state);
			if (finalStates.size()==0){
				System.out.println("Plus d etat final !!");
			}
		}
		ArrayList<Transition> suc=state.getSuccesseurs();
		for(Transition t:suc){
			removeTransition(t);
		}
		ArrayList<Transition> pred=state.getPredecesseurs();
		for(Transition t:pred){
			removeTransition(t);
		}
		this.states.remove(state);
	}
	 */

	public State getInitialState() {
		return this.initialState;
	}

	public Set<Transition> getTransitions() {
		/*Set<Transition> list = new HashSet<Transition>();
		list.addAll(transitions.values());
		return(list);*/
		return transitions;
	}	

	public Set<State> getStates() {
		Set<State> list = new HashSet<State>();
		list.addAll(states.values());
		return(list);		
	}

	public void setInitialState(State new_initial_state) {
		new_initial_state.setInit();
		this.initialState = new_initial_state;
	}
	
	public String toString() {
		String lts = this.getStates().size() + " states: ";
		lts = lts + this.getStates().toString();
		lts = lts + "\n" + transitions.size() + " transitions: ";
		lts = lts + this.getTransitions().toString();
		return lts;		
	}
	
	
	/** TODO
public boolean equals(Object o) {
	if (o == this)
		return true;
	else if ((o == null) || (o.getClass() != this.getClass()))
		return false;
	else {
		FSA fsa = (FSA) o;

		boolean initialS = this.initialState.equals(fsa.getInitialState());
		boolean finalS = true;
		boolean trans = true;
		ArrayList<Transition> list = this.getTransitions();
		if (fsa.getTransitions().size() == list.size()) {
			for (State fs:finalStates) {
				finalS = finalS
						&& fsa.getFinalStates().contains(fs);
			}
			for (State fs:fsa.getFinalStates()) {
				finalS = finalS
						&& finalStates.contains(fs);
			}
			for (int i = 0; i < list.size(); i++) {
				trans = trans
						&& list.get(i).equals(
								fsa.getTransitions().get(i));
			}
			return initialS && finalS && trans;
		} else {
			return false;
		}
	}
}
	 **/
}
