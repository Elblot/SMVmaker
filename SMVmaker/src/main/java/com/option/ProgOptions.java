package com.option;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.main.Main;

public class ProgOptions {
	
	public static void setOptions(String[] args) throws Exception {
		final Options options = configParameters();
	    final CommandLineParser parser = new DefaultParser();
	    try {
		    final CommandLine line = parser.parse(options, args);
		    
		    Main.dir = line.getOptionValue("input");
		    Main.output = line.getOptionValue("output");
		    Main.gen = line.hasOption("g");
		    Main.dep = line.getOptionValue("dep");
		    
		    		
		    /*if(!Main.algo.equals("complete") && !Main.algo.equals("compo") && !Main.algo.equals("ASSESSweak") && !Main.algo.equals("ASSESSstrong") && !Main.algo.equals("kbehavior")) {
		    	System.out.println(Main.algo);
		    	throw new Exception();
		    }*/
	    }catch(Exception e) {
	    	System.out.println("Usage : Main -i <dot file>"
	    			);  
	    	System.exit(1);}
	}
	
	private static Options configParameters() {
	
		final Option dirFileOption = Option.builder("i")
				.longOpt("input")
				.desc("LTS describing the behaviour of the component")
				.hasArg(true)
				.argName("input")
				.required(true)
				.build();
		
		final Option depFileOption = Option.builder("d")
				.longOpt("dep")
				.desc("DAG of dependency of the component")
				.hasArg(true)
				.argName("dep")
				.required(false)
				.build();
		
		final Option outputFileOption = Option.builder("o")
				.longOpt("output")
				.desc("NuSMV file to write")
				.hasArg(true)
				.argName("output")
				.required(true)
				.build();
		
		final Option intermediateFileOption = Option.builder("g")
				.longOpt("genIntermediateFile")
				.desc("generate intermediate models created during the process")
				.hasArg(false)
				.argName("genIntermediateFile")
				.required(false)
				.build();
		
	    final Options options = new Options();
	
	    options.addOption(dirFileOption);
	    options.addOption(depFileOption);
	    options.addOption(outputFileOption);
	    options.addOption(intermediateFileOption);
	    
	    
	    return options;
	}
}
