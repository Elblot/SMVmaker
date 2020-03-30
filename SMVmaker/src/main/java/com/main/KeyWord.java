package com.main;

import java.util.HashSet;
import java.util.Set;

public class KeyWord {
	
	private String name;
	//private int bool;
	private String description;
	private String scenario;
	private Set<LTLProperty> links;
	private Set<String> values;
	
	public KeyWord(String name, String description, String scenario) {
		values = new HashSet<String>();
		this.setName(name);
		this.setDescription(description);
		this.setScenario(scenario);
		links = new HashSet<LTLProperty>();
	}
	
	public KeyWord(String name) {
		new KeyWord(name, "", "");
	}
	
	public Set<LTLProperty> getLinks() {
		return links;
	}
	
	public void addValue(String value) {
		values.add(value);
	}
	
	public Set<String> getValues() {
		return values;
	}
	
	public void addLinks(LTLProperty ltl) {
		links.add(ltl);
	}
	
	public String toString() {
		return "name = " + getName() + "; description = " + description + "; scenario = " + scenario + "; possible value: " + values.toString() + "\n";//TODO link tostring
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
