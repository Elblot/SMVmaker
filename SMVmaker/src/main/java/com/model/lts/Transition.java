package com.model.lts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
import com.main.LTLProperty;
import com.utils.stringCleaner;

public class Transition {

	private String separator = "|||";

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
		parameters[1] = "from_" + stringCleaner.clean(getFrom(trans)) + "=TRUE";
		parameters[2] = "to_" + stringCleaner.clean(getTo(trans)) + "=TRUE";
		updateName();
		source = src;
		target = dst;
	}

	public boolean isEncrypted() {
		//System.out.println("check encrypted");
		String data = getData();
		//System.out.println(data);
		if (data.length() < 16) {
			return false;
		}
		if (data.isEmpty()) {
			return false;
		}
		//System.out.println("size: " + data.length() + ", data: " + data);
		data = data.replaceAll(":", "");
		data = data.replaceAll("\"", "");
		if (isHex(data)) {
			try {
				byte[] bytes;
				bytes = Hex.decodeHex(data.toCharArray());
				data = new String(bytes);
			} catch (DecoderException e) {
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
		//System.out.println(event);
		while (!event.isEmpty()) {
			//System.out.println(event);
			String param = "";
			if (event.contains(separator)){
				param = event.substring(0, event.indexOf(separator));
			}
			else {
				param = event;
			}
			event = event.replace(param, "");
			String reg = "\\Q" + separator + "\\E"; 
			//String reg = separator.replaceAll("|", "\\|");
			//System.out.println(separator + " " + reg);
			event = event.replaceFirst(reg, "");
			if (!param.contains("Host=") & !param.contains("Dest=") & !param.contains("Protocol=") & 
					(param.contains("data=") || param.contains("body="))) {
				data = data + param.substring(param.indexOf("=")+1, param.length());
			}
		}
		return data;
	}

	private String getFrom(String trans) {
		String from = "none" ;
		int d = trans.indexOf("Host=");
		if (d != -1) {
			if (trans.indexOf(separator, d+5) > d) {
				from = trans.substring(d + 5, trans.indexOf(separator, d + 5));
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
			if (trans.indexOf(separator, d+5) > d) {
				to = trans.substring(d + 5, trans.indexOf(separator, d + 5));
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
		//System.out.println("check contain");
		for (String param : oldParameters()) {
			//System.out.println(param);
			for (String word : strings) {
				if (param.contains(word)) {
					return true;
				}
			}
		}
		return false;
	}

	public HashSet<String> contain2(String... strings) {
		HashSet<String> res = new HashSet<String>();
		for (String param : oldParameters()) {
			for (String word : strings) {
				if (param.contains(word)) {
					res.add(param);
				}
			}
		}
		return res;
	}

	private Set<String> oldParameters(){
		Set<String> param = new HashSet<String>();
		String reg = "\\Q" + separator + "\\E";
		Collections.addAll(param, name.substring(name.indexOf("(")+1,name.lastIndexOf(")")).split(reg, 0));
		//System.out.println(name.substring(name.indexOf("(")+1,name.lastIndexOf(")")));
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
		//System.out.println("param added:" + newparam);
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
		//System.out.println("check input");
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
			return name.contains("=OK") || name.contains("status=0300");
		}
		return false;
	}


	public boolean containSQL() {
		return false; //TODO
	}

	public HashSet<String> blackListed() {
		HashSet<String> res = new HashSet<String>();
		File k = new File(ClassLoader.getSystemClassLoader().getResource("com/wordList/blackList").getFile());
		try {
			BufferedReader br = new BufferedReader(new FileReader(k));
			String line = br.readLine();
			while (line != null) {
				for (String param : oldParameters()) {
					if (param.contains(line)) {
						//br.close();
						res.add(param);
						//return true;
					}
				}
				line = br.readLine();
			}
			br.close();
		}catch (IOException e) {
			System.err.println("problem with blacklisted file");
			System.exit(3);
		}
		return res;//false;
	}

	public HashSet<String> sensitive() {
		File k = new File(ClassLoader.getSystemClassLoader().getResource("com/wordList/sensitive").getFile());
		HashSet<String> res = new HashSet<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(k));
			String line = br.readLine();
			while (line != null) {
				for (String param: oldParameters()) {
					if (param.contains(line)) {
						//br.close();
						res.add(param);
						//return true;
					}
				}
				line = br.readLine();
			}
			br.close();
		}catch (IOException e) {
			System.err.println("problems with sensitive file");
			System.exit(3);
		}
		return res;//false;
	}

	public String getUpdate() {
		String res = "";
		for (String param : oldParameters()) {
			if (param.contains("get-update") |
				param.contains("get_update") |
				param.contains("data") ) {
				res = param;
			}
		}
		return res;
	}
	
	public HashSet<String> containXSS() {
		HashSet<String> res = new HashSet<String>();
		for (String param: oldParameters()) {
			if (param.contains("<script>alert(1);</script>") |
					param.contains("%3Cscript%3Ealert(1);%3C/script%3E") |
					param.contains("%3Cscript%3Ealert(1)%3B%3C%2Fscript%3E") |
					param.contains("%3Cscript%3Ealert%281%29%3B%3C%2Fscript%3E")) {
				res.add(param);

			}
		}
		/*Pattern p = Pattern.compile("(javascript|vbscript|expression|applet|script|embed|object|iframe|frame|frameset)");
		//Pattern p = Pattern.compile("((\\%3C)|<)((\\%2F)|\\/)*[a-z0-9\\%]+((\\%3E)|>)"); 
		Matcher matcher = p.matcher(name);
		//Matcher matcher = p.matcher("<script>alert('XSS')</script>");
		System.out.println(matcher.find());
		return matcher.find();*/
		return res; //contain("<script>alert(1);</script>","%3Cscript%3Ealert(1);%3C/script%3E","%3Cscript%3Ealert(1)%3B%3C%2Fscript%3E", "%3Cscript%3Ealert%281%29%3B%3C%2Fscript%3E");
	}

}
