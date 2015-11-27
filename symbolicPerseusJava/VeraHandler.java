
import java.io.*;
import java.util.*;
import java.lang.*;


class VeraHandler implements Serializable {

    // default values
    public static int nRounds = 5;
    public static int nIterations =30;  // backup iterations  per round
    public static int maxAlphaSetSize = 100;
    public static int numBelStates = 100;
    public static int maxBelStates = 10000;
    public static int episodeLength = 50;  // when generating belief points
    public static double threshold = 0.001;
    public static double explorProb=0.4;

    public static String iofile;
    public static boolean generate=false;
    public static boolean multinits=false;
    public static boolean simulate=false;
    public static boolean havepolicy=false;
    public static int nits=100;
    
    public static void printDotDDs(POMDP pomdp) {
	String fname; 
	FileOutputStream f_out;
	PrintStream outp; 

	try {
	    for (int i=0; i<pomdp.nActions; i++) {
		fname="action_"+i+"_rewFn.dot";
		f_out = new FileOutputStream (fname);
		outp = new PrintStream(f_out);
		pomdp.actions[i].rewFn.printDotDD(outp);
		outp.close();
		f_out.close();
		
		System.out.println("number of state vars "+pomdp.nStateVars);
		for (int k=0; k<pomdp.nStateVars; k++) {
		    
		    fname="action_"+i+"_CPT_"+k+".dot";
		    f_out = new FileOutputStream (fname);
		    outp = new PrintStream(f_out);
		    pomdp.actions[i].transFn[k].printDotDD(outp);
		    outp.close();
		    f_out.close();
		}
		for (int k=0; k<pomdp.nObsVars; k++) {
		    
		    fname="action_"+i+"_OBSF_"+k+".dot";
		    f_out = new FileOutputStream (fname);
		    outp = new PrintStream(f_out);
		    pomdp.actions[i].obsFn[k].printDotDD(outp);
		    outp.close();
		    f_out.close();
		}
	    }
	} catch (IOException err) {
	    System.out.println("file not found error "+err);
	    return;
	}
    }
    public static boolean parseArgs(String [] args) {
	String ioarg;

	if (args.length >= 2) {
	    int numargs = args.length-1;
	    int thearg=1;
	    ioarg=args[thearg];
	    while (numargs > 0 && ioarg.startsWith("-")) {
		if (ioarg.equals("-i"))  {
		    if (numargs-1 > 0 &&  !(args[thearg+1].startsWith("-"))) {
			iofile = args[thearg+1];
			thearg += 2;
			havepolicy=true;
		    } else {
			thearg++;
			numargs++;
		    }
		    generate = false;
		} else if (ioarg.equals("-s")) {
		    if (numargs-1 > 0 &&  !(args[thearg+1].startsWith("-"))) {
			simulate=true;
			nits = Integer.parseInt(args[thearg+1]);
			thearg+=2;
		    } else {
			numargs=0;
		    }
		} else if (ioarg.equals("-g")) {
		    generate=true;
		    thearg++;
		    numargs++;
		} else if (ioarg.equals("-j")) {
		    multinits=true;
		    thearg++;
		    numargs++;
		} else if (ioarg.equals("-b")) {
		    if (numargs-1 > 0 &&  !(args[thearg+1].startsWith("-"))) {
			numBelStates = Integer.parseInt(args[thearg+1]);
			thearg += 2;
		    } else {
			numargs=0;
		    }
		} else if (ioarg.equals("-m")) {
		    if (numargs-1 > 0 &&  !(args[thearg+1].startsWith("-"))) {
			maxAlphaSetSize = Integer.parseInt(args[thearg+1]);
			thearg += 2;
		    } else {
			numargs=0;
		    }
		} else if (ioarg.equals("-t")) {
		    if (numargs-1 > 0 &&  !(args[thearg+1].startsWith("-"))) {
			nIterations = Integer.parseInt(args[thearg+1]);
			thearg += 2;
		    } else {
			numargs=0;
		    }
		}  else if (ioarg.equals("-e")) {
		    if (numargs-1 > 0 &&  !(args[thearg+1].startsWith("-"))) {
			episodeLength = Integer.parseInt(args[thearg+1]);
			thearg += 2;
		    } else {
			numargs=0;
		    }
		}   else if (ioarg.equals("-r")) {
		    if (numargs-1 > 0 &&  !(args[thearg+1].startsWith("-"))) {
			nRounds = Integer.parseInt(args[thearg+1]);
			thearg += 2;
		    } else {
			numargs=0;
		    }
		}  else if (ioarg.equals("-x")) {
		    if (numargs-1 > 0 &&  !(args[thearg+1].startsWith("-"))) {
			explorProb  = Double.parseDouble(args[thearg+1]);
			thearg += 2;
		    } else {
			numargs=0;
		    }
		}  else if (ioarg.equals("-h")) {
		    if (numargs-1 > 0 &&  !(args[thearg+1].startsWith("-"))) {
			threshold  = Double.parseDouble(args[thearg+1]);
			thearg += 2;
		    } else {
			numargs=0;
		    }
		} 
			
		numargs-=2;
		if (numargs > 0)
		    ioarg=args[thearg];

	    }
	    if (numargs != 0) {
		return false;
	    } else {
		return true;
	    }
	} else {
	    return true;
	}
    }
    public static void main(String args[]) {
	if (args.length < 1 || args[0].startsWith("-")) {
	    //usage();
	    return;
	}
	String spuddfile = args[0];
	String basename = spuddfile.substring(0,spuddfile.lastIndexOf("."));
	boolean debug=false;
	// default output/input file
	iofile = basename+".pomdp";
	if (!parseArgs(args)) {
	    //usage();
	    return;
	}
	if (generate) {
	    System.out.println("Solving pomdp in file "+spuddfile+" for \n\t\t "+nRounds+" rounds, \n\t\t "+nIterations+" iterations per round, \n\t\t "+maxAlphaSetSize+" alpha vectors maximum, using \n\t\t "+numBelStates+" belief states generated with an episode length of \n\t\t "+episodeLength+" and an exploration probability of \n\t\t "+explorProb+" and a threshold of \n\t\t "+threshold);

	    POMDP pomdp = new POMDP(spuddfile,debug);

	    if (generate) {
		pomdp.solve(nRounds, numBelStates, maxBelStates, episodeLength, threshold, explorProb, nIterations, maxAlphaSetSize, basename, multinits);
	    }
	    
	    if (generate) {
		FileOutputStream f_out;
		try {
		    // save to disk
		    // Use a FileOutputStream to send data to a file
		    // called myobject.data.
		    f_out = new FileOutputStream (iofile);
		} catch (FileNotFoundException err) {
		    System.out.println("file not found error "+err);
		    return;
		}
		try {
		    // Use an ObjectOutputStream to send object data to the
		    // FileOutputStream for writing to disk.
		    ObjectOutputStream obj_out = new
			ObjectOutputStream (f_out);
		    
		    // Pass our object to the ObjectOutputStream's
		    // writeObject() method to cause it to be written out
		    // to disk.
		    obj_out.writeObject (pomdp);
		} catch (IOException err) {
		    System.out.println("file write error"+err);
		}
	    }
	} else if (simulate) {
	    if (!havepolicy) {
		POMDP pomdp = new POMDP(spuddfile,debug);

		//double polval = pomdp.evaluatePolicyStationary(10,100,true);
		System.out.println("policy value is ");
		pomdp.simulateGeneric(nits);
		
	    } else {
		// Read from disk using FileInputStream.
		FileInputStream f_in;
		try {
		    f_in = new FileInputStream (iofile);
		} catch (FileNotFoundException err) {
		    System.out.println("file not found error "+err);
		    return;
		}
		Object obj;
		ObjectInputStream obj_in;
		try {
		    
		    // Read object using ObjectInputStream.
		    obj_in = new ObjectInputStream (f_in);
		} catch (IOException err) {
		    System.out.println("file read error"+err);
		    return;
		} 
		try {
		    // Read an object.
		    obj = obj_in.readObject ();
		} catch (IOException err) {
		    System.out.println("file read error"+err);
		    return;
		} catch (ClassNotFoundException err) {
		    System.out.println("class error"+err);
		    return;
		} 
		boolean heuristic=false;
		// Is the object that you read in, say, an instance
		// of the POMDP class?
		if (obj instanceof POMDP) {
		    // Cast object to a POMDP
		    POMDP pomdp = (POMDP) obj;
		    
		    pomdp.readFromFile(spuddfile,debug);
		    
		    // do simulation
		    DD belState = pomdp.initialBelState;
		    // use the adjunct initial belief state if there is one
		    if (pomdp.adjunctNames != null) {
			for (int i=0; i<pomdp.adjunctNames.length; i++) 
			    if (pomdp.adjunctNames[i].startsWith("init")) {
				System.out.println("Using "+pomdp.adjunctNames[i]+" adjunct dd as initial state");
			    belState = pomdp.adjuncts[i];
			    }
		    }

		    int actId,cactId;
		    String [] obsnames = new String[pomdp.nObsVars];
		    int nits = 100;
		    String inobs,inact;  
		    DD obsDist;
		    //BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			VeraComm vc = new VeraComm();
			boolean VeraEnd = false;//if received a "ENDOPERATION" signal from Vera
			
			while (nits > 0 || VeraEnd) {
				    //System.out.println("current belief state: ");
				    pomdp.printBeliefState(belState);
				    actId = pomdp.policyQuery(belState,heuristic);
				    System.out.println("action suggested by policy: "+actId+" which is "+pomdp.actions[actId].name);
				    System.out.print("enter action to use:");
				    
				    //VVVVV1: actId, actionName
				    //cancelled: only follow recommandation at this part
				    
				    //vc.send(actId + " " + pomdp.actions[actId].name);
				    inact = vc.readLine();
				    
				    inact = "";//follow the recommadation
				    
				    cactId = pomdp.findActionByName(inact);
				    if (cactId >= 0) {
				    	actId = cactId;
			    }
			    //System.out.println("action used: "+actId+" which is "+pomdp.actions[actId].name);
				//VVVVV2: confirm action##actId
				    //cancelled: follow recommadation
				    //vc.send(actId + "");
			    
			    for (int o=0; o<pomdp.nObsVars; o++) {
					obsnames[o]=pomdp.obsVars[o].valNames[1];
					System.out.print("enter observation "+pomdp.obsVars[o].name+" ["+obsnames[o]+"]: ");
					
					//VVVVV3: observation### observe value, observe status 
					vc.send(pomdp.obsVars[o].name + " " + obsnames[o]);
					inobs = vc.readLine();
					
					for (int k=0; k<pomdp.obsVars[o].arity; k++) {
					    if (inobs.equalsIgnoreCase(pomdp.obsVars[o].valNames[k]))
					    	obsnames[o]=inobs;
					}
			    }
			    System.out.print("observations: ");
			    for (int o=0; o<pomdp.nObsVars; o++)
				System.out.print(" "+obsnames[o]);
			    System.out.println();
			    belState = pomdp.beliefUpdate(belState,actId,obsnames);
			    nits--;
			}
		    

		} else {
		    System.out.println("that file does not contain a pomdp");
		}
	    }
	}
    }
}
