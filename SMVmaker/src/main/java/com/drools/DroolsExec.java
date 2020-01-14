package com.drools;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.main.LTSmapper;
import com.model.GenerateDOT;
import com.model.lts.LTS;
import com.model.lts.Transition;


/**
 * This is a sample class to launch a rule.
 */
public class DroolsExec {

	/*
	 * run drools in order to put new labels in the transitions
	 */
	public static final LTS runDrools(File input, boolean gen) {
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieSession kSession = kContainer.newKieSession("ksession-rules");
		LTS lts = LTSmapper.mapping(input);
		Set<Transition> transitions = lts.getTransitions();
		System.out.println("lts mapped");
		//System.out.println(transitions.toString());
		for (Transition t : transitions) {
			kSession.insert(t);
		}
		System.out.println("launch rule:");
		int fired = kSession.fireAllRules();
		System.out.println( "Number of modified Transitions = " + fired );
		//printArray(lts.getTransitions());//lts is transformed
		
		// TODO?? remove useless param in transitions
	
		return lts;
	}

	/*
	 * pretty print of the set of transitions 
	 */
	public static void printArray(Set<Transition> trans) {
		System.out.println("Transitions list:");
		for (Transition t : trans) {
			System.out.println(t.toString());
		}
	}
}