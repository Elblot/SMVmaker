package com.model.lts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.auditability.auditability;

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

	public boolean isEncrypted() {
		//System.out.println(this);
		String data = getData();
		//System.out.println(data);
		if (data.length() < 16) {
			return false;
		}
		if (data.isEmpty()) {
			return false;
		}
		data = data.replaceAll(":", "");
		data = data.replaceAll("\"", "");
		if (isHex(data)) {
			try {
				byte[] bytes;
				bytes = Hex.decodeHex(data.toCharArray());
				data = new String(bytes);
			} catch (DecoderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		//System.out.println("run ent here");
		return auditability.runEnt(data);
	}

	private boolean isHex(String hex) {
		for (char c : hex.toCharArray()) {
			switch (c) {
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'F':
				continue;
			default:
				return false;
			}
		}
		return true;
	}

	private String getData() {
		String data = "";
		String event = parameters[0];
		event = event.substring(event.indexOf("("), event.length() - 1);
		while (!event.isEmpty()) {
			String param = "";
			if (event.contains(";")){
				param = event.substring(0, event.indexOf(";")); //; is the separator
			}
			else {
				param = event;
			}
			event = event.replace(param, "");
			event = event.replaceFirst(";", "");
			if (!param.contains("Host=") & !param.contains("Dest=") & !param.contains("Protocol=") & param.contains("=")) {
				data = data + param.substring(param.indexOf("=")+1, param.length());
			}
		}
		return data;
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

	public String getTo(String trans) {
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

	/*public boolean contain(String... strings) {
		for (String param : oldParameters()) {
			String leftpart = "";
			String rightpart = "";
			if (param.contains("=")){
				leftpart = param.substring(0, param.indexOf("="));
				rightpart = param.substring(param.indexOf("=") + 1);
			}
			else {
				leftpart = param;
				rightpart = param;
			}
			for (String word : strings) {
				if (leftpart.equals(word) | rightpart.equals(word)) {
					return true;
				}
				
			}
		}
		return false;
	}	*/
	
	public boolean contain(String... strings) {
		for (String param : oldParameters()) {
			for (String word : strings) {
				if (param.equals(word)) {
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
	
	/* return the name of the component that communicate with the one modeled */
	public String getOtherCompo() {
		if (isInput()) {
			return getFrom(name);
		}
		else {
			return getTo(name);
		}
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
	
	public boolean containXSS() {
		Pattern p = Pattern.compile("(javascript|vbscript|expression|applet|script|embed|object|iframe|frame|frameset)");
		//Pattern p = Pattern.compile("((\\%3C)|<)((\\%2F)|\\/)*[a-z0-9\\%]+((\\%3E)|>)"); 
		Matcher matcher = p.matcher(name);
		//Matcher matcher = p.matcher("<script>alert('XSS')</script>");
		System.out.println(matcher.find());
		return matcher.find();
	}

}
