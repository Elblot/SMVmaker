package com.main;

import java.util.HashMap;
import java.util.Set;

public class LTLProperty {
	
	private String name;
	private String property;
	private String desc;
	private Set<KeyWord> keyWords;
	
	public LTLProperty(String name, String property, String desc, HashMap<String, KeyWord> keywords) {
		this.property = property;
		this.name = name;
		this.desc = desc; 
		for (String key: keywords.keySet()) {
			if (property.contains(key)){
				keywords.get(key).addLinks(this);
			}
		}
	}
	
	public String getName() {
		return name;
	}

	/*
	public void setName(String name) {
		this.name = name;
	}*/
	
	public String getProperty() {
		return property;
	}

	/*
	public void setProperty(String property) {
		this.property = property;
	}*/

	public String getDesc() {
		return desc;
	}

	/*
	public void setDesc(String desc) {
		this.desc = desc;
	}*/

	public Set<KeyWord> getKeyWords() {
		return keyWords;
	}

	/*
	public void setKeyWords(Set<KeyWord> keyWords) {
		this.keyWords = keyWords;
	}*/
	
	public String toString() {
		return getName();
	}
	
	public String debugToString() {
		return "property " + name + " : " + property + "; " + desc;
	}
	
}
