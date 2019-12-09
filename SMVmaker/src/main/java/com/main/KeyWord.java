package com.main;

import java.util.HashSet;
import java.util.Set;

public class KeyWord {
	
	private String name;
	private int necessary;
	private String description;
	private String scenario;
	private Set<LTLProperty> links;
	
	public KeyWord(String name, int necessary, String description, String scenario) {
		if (necessary != 0 & necessary != 1 & necessary != 2 ) {
			System.err.println("keyWord: " + name + ", \"necessary\" field must contains 0, 1, or 2.");
			System.exit(1);
		}
		this.setName(name);
		this.setNecessary(necessary);
		this.setDescription(description);
		this.setScenario(scenario);
		links = new HashSet<LTLProperty>();
	}
	
	public KeyWord(String name, int necessary) {
		new KeyWord(name, necessary, "", "");
	}
	
	public Set<LTLProperty> getLinks() {
		return links;
	}
	
	public void addLinks(LTLProperty ltl) {
		links.add(ltl);
	}
	
	public String toString() {
		return "name = " + getName() + "; necessary = " + necessary + "; description = " + description + "; scenario = " + scenario + "; linked to: " + links;//TODO link tostring
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNecessary() {
		return necessary;
	}

	public void setNecessary(int necessary) {
		this.necessary = necessary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getScenario() {
		return scenario;
	}

	public void setScenario(String scenario) {
		this.scenario = scenario;
	}
}
