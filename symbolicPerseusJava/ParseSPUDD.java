import java.io.*;
import java.util.*;
import java.lang.*;

class ParseSPUDD {
    private HashMap existingDds;
    private StreamTokenizer stream;

    public Vector<String> varNames;
    public Vector<Vector> valNames;
    public Vector<String> actNames;
    public Vector<String> adjunctNames;
    public Vector<DD[]> actTransitions;
    public Vector<DD[]> actObserve;
    public Vector<DD> actCosts;
    public Vector<DD> adjuncts;
    public DD reward;
    public DD init;
    public DD discount;
    public DD tolerance;
    public DD horizon;
    public boolean unnormalized;
    public int nStateVars;
    public int nObsVars;

    public ParseSPUDD(String fileName) {
	existingDds = new HashMap();
	varNames = new Vector<String>();
	valNames = new Vector<Vector>();
	actNames = new Vector<String>();
	actTransitions = new Vector<DD[]>();
	actObserve = new Vector<DD[]>();
	actCosts = new Vector<DD>();
	adjuncts = new Vector<DD>();
	adjunctNames = new Vector<String>();
	discount = null;
	tolerance = null;
	horizon = null;
	init = DD.one;
	reward = DD.zero;
	unnormalized = false;
	nStateVars = 0;
	nObsVars = 0;

	try {
	    stream = new StreamTokenizer(new FileReader(fileName));
	} catch (FileNotFoundException e) {             				
	    System.out.println("Error: file not found\n");
	    //System.exit(1);
	} 
	stream.wordChars('\'','\'');
	stream.wordChars('_','_');
    }

    private void error(int id) {
	System.out.println("Parse error at line #" + stream.lineno());
	//if (stream.ttype > 0)
	//		System.out.println("ttype = " + Character('a'));
	/* else */ System.out.println("ttype = " + stream.ttype); 
	System.out.println("sval = " + stream.sval); 
	System.out.println("nval = " + stream.nval); 
	System.out.println("ID = " + id);
	//System.exit(1);
    }

    private void error(String errorMessage) {
	System.out.println("Parse error at " + stream.toString() + ": " + errorMessage);
	//System.exit(1);
    }

    public void parsePOMDP(boolean fullyObservable) {
	try {
	    boolean primeVarsCreated = false;
	    while (true) {
		if (!primeVarsCreated && nStateVars > 0 && (fullyObservable || nObsVars > 0)) {
		    primeVarsCreated = true;
		    createPrimeVars();
		}
		stream.nextToken();
		switch(stream.ttype) {
		case '(':
		    stream.nextToken();
		    if (stream.sval.compareTo("variables") == 0) {
			parseVariables();
		    }
		    else if (stream.sval.compareTo("observations") == 0) {
			parseObservations();
		    }
		    else error("Expected \"variables\" or \"observations\"");
		    break;
		case StreamTokenizer.TT_WORD:
		    if (stream.sval.compareTo("unnormalized") == 0) {
			unnormalized = true;
			break;
		    }
		    else if (stream.sval.compareTo("unnormalised") == 0) {
			unnormalized = true;
			break;
		    }
		    else if (stream.sval.compareTo("dd") == 0) {
			parseDDdefinition();
			break;
		    }
		    else if (stream.sval.compareTo("action") == 0) {
			parseAction();
			break;
		    }
		    else if (stream.sval.compareTo("adjunct") == 0) {
			parseAdjunct();
			break;
		    }
		    else if (stream.sval.compareTo("reward") == 0) {
			parseReward();
			break;
		    }
		    else if (stream.sval.compareTo("discount") == 0) {
			parseDiscount();
			break;
		    }
		    else if (stream.sval.compareTo("horizon") == 0) {
			parseHorizon();
			break;
		    }
		    else if (stream.sval.compareTo("tolerance") == 0) {
			parseTolerance();
			break;
		    }
		    else if (stream.sval.compareTo("init") == 0) {
			parseInit();
			break;
		    }
		    error("Expected \"unnormalized\" or \"dd\" or \"action\" or \"reward\"");
		case StreamTokenizer.TT_EOF:

		    // set valNames for actions
		    String[] actNamesArray = new String[actNames.size()];
		    for (int actId=0; actId<actNames.size(); actId++) {
			actNamesArray[actId] = (String)actNames.get(actId);
		    }
		    Global.setValNames(Global.valNames.length+1,actNamesArray);
										
										
		    // set varDomSize with extra action variable
		    int[] varDomSizeArray = new int[Global.varDomSize.length+1];
		    for (int varId=0; varId<Global.varDomSize.length; varId++) {
			varDomSizeArray[varId] = Global.varDomSize[varId];
		    }
		    varDomSizeArray[varDomSizeArray.length-1] = actNamesArray.length;
		    Global.setVarDomSize(varDomSizeArray);
		    return;
		default: 	
		    error(3);
		}
	    }

	} catch (IOException e) {             
	    System.out.println("Error: IOException\n");
            //System.exit(1);
        }
    }

    public void parseVariables() {
	try {
	    while (true) {
		if (stream.nextToken() == '(') {
		    if (StreamTokenizer.TT_WORD != stream.nextToken()) error("Expected a variable name");
		    if (varNames.contains(stream.sval)) error("Duplicate variable name");
		    varNames.add(stream.sval);
		    Vector<String> varValNames = new Vector<String>();
		    while (true) {
			if (StreamTokenizer.TT_WORD == stream.nextToken()) {
			    if (varValNames.contains(stream.sval)) error("Duplicate value name");
			    varValNames.add(stream.sval);
			}
			else if (stream.ttype == ')')
			    break;
			else error(4);
		    }
		    valNames.add(varValNames);
		    nStateVars++;
		}
		else if (stream.ttype == ')') {
		    break;
		}
		else error("");
	    } 
	} catch (IOException e) {
            System.out.println("Error: IOException\n");
            //System.exit(1);
        }
    }

    public void createPrimeVars() {
	// create prime variables
	int nVars = varNames.size();
	for (int i=0; i<nVars; i++) {
	    varNames.add((String)varNames.get(i) + "'");
	    valNames.add((Vector)valNames.get(i));
	}

	// set Global.varNames
	String[] varNamesArray = new String[varNames.size()+1];
	for (int i=0; i<varNames.size(); i++) varNamesArray[i] = (String)varNames.get(i);
	varNamesArray[varNames.size()] = new String("action");
	Global.setVarNames(varNamesArray);

	// set Global.valNames and Global.varDomSize
	int[] varDomSize = new int[valNames.size()];
	for (int i=0; i<valNames.size(); i++) {
	    Vector varValNames = (Vector)valNames.get(i);
	    varDomSize[i] = varValNames.size();
	    String[] varValNamesArray = new String[varValNames.size()];
	    for (int j=0; j<varValNames.size(); j++) varValNamesArray[j] = (String)varValNames.get(j);
	    Global.setValNames(i+1,varValNamesArray);
	}
	Global.setVarDomSize(varDomSize);

	// create SAMEvariable dds
	for (int varId=0; varId<Global.varNames.length/2; varId++) {
	    String ddName = new String("SAME") + Global.varNames[varId];
	    DD[] children = new DD[Global.varDomSize[varId]];
	    for (int i=0; i<Global.varDomSize[varId]; i++) {
		DD[] grandChildren = new DD[Global.varDomSize[varId]];
		for (int j=0; j<Global.varDomSize[varId]; j++) {
		    if (i==j) grandChildren[j] = DD.one;
		    else grandChildren[j] = DD.zero;
		}
		children[i] = DDnode.myNew(varId+1,grandChildren);
	    }
	    DD dd = DDnode.myNew(varId+1+Global.varNames.length/2, children);
	    existingDds.put(ddName,dd);
	}

	// create variablevalue dds
	for (int varId=0; varId<Global.varNames.length/2; varId++) {
	    for (int valId=0; valId<Global.varDomSize[varId]; valId++) {
		String ddName = Global.varNames[varId] + Global.valNames[varId][valId];
		DD[] children = new DD[Global.varDomSize[varId]];
		for (int i=0; i<Global.varDomSize[varId]; i++) {
		    if (valId==i) children[i] = DD.one;
		    else children[i] = DD.zero;
		}
		DD dd = DDnode.myNew(varId+1+Global.varNames.length/2,children);
		existingDds.put(ddName,dd);
	    }
	}
    }

    public void parseObservations() {
	try {
	    while (true) {
		if (stream.nextToken() == '(') {
		    if (StreamTokenizer.TT_WORD != stream.nextToken()) error("Expected a variable name");
		    if (varNames.contains(stream.sval)) error("Duplicate variable name");
		    varNames.add(stream.sval);
		    Vector<String> varValNames = new Vector<String>();
		    while (true) {
			if (StreamTokenizer.TT_WORD == stream.nextToken()) {
			    if (varValNames.contains(stream.sval)) error("Duplicate value name");
			    varValNames.add(stream.sval);
			}
			else if (stream.ttype == ')')
			    break;
			else error(4);
		    }
		    valNames.add(varValNames);
		    nObsVars++;
		}
		else if (stream.ttype == ')') {
		    break;
		}
		else error("");
	    } 
	} catch (IOException e) {
            System.out.println("Error: IOException\n");
            //System.exit(1);
        }
    }

    public void parseDDdefinition() {
	try {
	    if (StreamTokenizer.TT_WORD != stream.nextToken()) error("Expected a dd name");
	    String ddName = stream.sval;
	    if (existingDds.get(ddName) != null) error("Duplicate dd name");
	    DD dd = parseDD();
	    existingDds.put(ddName, dd);
	    stream.nextToken();
	    if (stream.sval.compareTo("enddd") != 0) error("Expected \"enddd\"");
	} catch (IOException e) {
            System.out.println("Error: IOException\n");
            //System.exit(1);
        }
    }

    public DD parseDD() {
	DD dd = null;
	try {
	    // parse DD
	    if (stream.nextToken() == '(') {
						
		// parse DDnode
		if (StreamTokenizer.TT_WORD == stream.nextToken()) {

		    // existingDd
		    dd = (DD)existingDds.get(stream.sval);
		    if (dd != null) {
			if (stream.nextToken() != ')') error("Expected ')'");
		    }

		    // it's not an existing dd so perhaps it's a variable
		    else if (dd == null) {
			int varId = varNames.indexOf(stream.sval);
			if (varId == -1) error("Not an existing dd nor an existing variable");
										
			// parse values
			Vector varValNames = (Vector)valNames.get(varId);
			DD[] children = new DD[varValNames.size()];
			for (int i = 0; i < children.length; i++) children[i] = DD.zero;
			Vector valNamesSoFar = new Vector();
			while (true) {
			    if (stream.nextToken() == '(') {
				stream.nextToken();
				if (valNamesSoFar.contains(stream.sval)) error("Duplicate child");
				int valId = varValNames.indexOf(stream.sval);
				if (valId == -1) error("Invalid value");
				children[valId] = parseDD();
				if (stream.nextToken() != ')') error("Expected ')'");
			    }
			    else if (stream.ttype == ')') break;
			    else error("Expected ')' or '('");
			}
			dd = DDnode.myNew(varId+1,children);
		    }
		}
								
		// parse leaf node
		else if (StreamTokenizer.TT_NUMBER == stream.ttype) {
		    dd = DDleaf.myNew(stream.nval);
		    if (stream.nextToken() != ')') error("Expected ')'");
		}
								
		// Invalid dd
		else error("Invalid DDnode or DDleaf");
	    }

	    // arithmetic operation
	    else if (stream.ttype == '[') {
								
		// parse operator
		int operator = stream.nextToken();
								
		// multiplication
		if (operator == '*') {
		    dd = DD.one;
		    while (stream.nextToken() != ']') {
			stream.pushBack();
			DD newDd = OP.reorder(parseDD());
			dd = OP.mult(dd,newDd);
		    }
		}

		// addition
		else if(operator == '+') {
		    dd = DD.zero;
		    while (stream.nextToken() != ']') {
			stream.pushBack();
			DD newDd = OP.reorder(parseDD());
			dd = OP.add(dd,newDd);
		    }
		}
		// subtraction
		// all arguments other than the first
		// are subtracted from the first
		else if (operator == '-') {
		    dd = DD.zero;
		    DD thenewDd = OP.reorder(parseDD());
		    dd = OP.add(dd,thenewDd);
		    while (stream.nextToken() != ']') {
			stream.pushBack();
			DD newDd = OP.reorder(parseDD());
			dd = OP.sub(dd,newDd);
		    }
		}
		// normalisation over all variables (a belief state)
		else if (operator == '!') {
		    dd = OP.reorder(parseDD());
		    int[] vars = new int[nStateVars];
		    for (int i=0; i<nStateVars; i++) vars[i] = i+1;
		    DD[] dds = new DD[1];
		    dds[0] = dd;
		    dd = OP.div(dd,OP.addMultVarElim(dds,vars));
		    if (stream.nextToken() != ']') error("Expected ']'");
		}		
		// normalisation over a single variable
		else if (operator == '#') {
		    stream.nextToken();
		    int[] vars = new int[1];
		    vars[0] = varNames.indexOf(stream.sval)+1;
		    if (vars[0] == 0) error("Unknown variable name");
		    DD[] dds = new DD[1];
		    dds[0] = OP.reorder(parseDD());
		    dd = OP.div(dds[0],OP.addMultVarElim(dds,vars));
		    if (stream.nextToken() != ']') error("Expected ']'");
		}
		// division
		// first argument is numerator, all others are
		// in denominator
		else if (operator == '$') {
		    dd = DD.zero;
		    DD thenewDd = OP.reorder(parseDD());
		    dd = OP.add(dd,thenewDd);
		    while (stream.nextToken() != ']') {
			stream.pushBack();
			DD newDd = OP.reorder(parseDD());
			dd = OP.div(dd,newDd);
		    }
		}

		// threshold operator - why was this developed? 
		else if (operator == ':') {
		    DD newDd = OP.reorder(parseDD());
		    DD factorDd = OP.reorder(parseDD());
		    DD decreaseFactors = OP.threshold(newDd,1,-1);  // is newDd wherever newDd <= 1, is 0 elsewhere
		    DD increaseFactors = OP.threshold(newDd,1,1); // is newDd wherever newDd >= 1, is 0 elsewhere
		    DD staysameFactors = OP.threshold(newDd,1,0); // is newDd wherever newDd == 1, is 0 elsewhere
		    //DD invFactorDd = OP.div(DD.one,factorDd);
		    dd = OP.mult(increaseFactors,factorDd);
		    DD decDd = OP.div(decreaseFactors,factorDd);
		    dd = OP.add(dd,decDd);
		    dd = OP.add(dd,staysameFactors);
		    //dd.display();
		    if (stream.nextToken() != ']') error("Expected ']'");
		}
		else if (operator == '@') {
		    // three arguments to a sigmoid function
		    // the ordinal value, the mean and the slope
		    int numargs=3;
		    double[] sigparams = new double[3];
		    int thearg=0;
		    while (stream.nextToken() != ']' && thearg < numargs) {
			stream.pushBack();
			DD newDd = OP.reorder(parseDD());
			if (newDd.getVar() == 0) {
			    sigparams[thearg] = newDd.getVal();
			    //System.out.println("sigparams["+thearg+"]="+sigparams[thearg]);
			    thearg++;
			} else {
			    // throw exception
			    throw new IOException();
			}
		    }
		    if (thearg != numargs) {
			throw new IOException();
		    } else {
			double theval = computeSigmoid(sigparams);
			//System.out.println("computing sigmoid with "+sigparams[0]+" "+sigparams[1]+" "+sigparams[2]+" has value "+theval);
			dd = DDleaf.myNew(theval);
		    }
		}
								
		else error("Expected '*' or '+' or '#'  or '!' '/' or '@' or '-'");
	    }
	    else error("Expected '(' or '['");
						
	} catch (IOException e) {
            System.out.println("Error: IOException\n");
            //System.exit(1);
        }
	return dd;
    }
    public double computeSigmoid(double params[]) {
	double theval=((params[0]-params[1])/params[2]);
	return 1.0/(1.0+Math.exp(-theval));
    } 

    public void parseAdjunct() {
	try {
	    // parse adjunct name
	    stream.nextToken();
	    if (adjunctNames.contains(stream.sval)) error("Duplicate adjunct name");
	    else adjunctNames.add(stream.sval);
	    
	    // parse the adjunct
	    DD dd = OP.reorder(parseDD());
	    adjuncts.add(dd);
	    
        } catch (IOException e) {
            System.out.println("Error: IOException\n");
	}

    }
    public void parseAction() {

	try {

	    // parse action name
	    stream.nextToken();
	    if (actNames.contains(stream.sval)) error("Duplicate action name");
	    else actNames.add(stream.sval);
	    actCosts.add(DD.zero);
	    DD[] cpts = new DD[nStateVars];
						
	    // parse cpts
	    while (true) {
								
		// endaction
		stream.nextToken();
		if (stream.sval.compareTo("endaction") == 0) break;
								
		// cost
		else if (stream.sval.compareTo("cost") == 0) {
		    DD dd = OP.reorder(parseDD());
		    actCosts.set(actCosts.size()-1,dd);
		}
								
		// observation function
		else if (stream.sval.compareTo("observe") == 0) {
		    DD[] obsCPTs = new DD[nObsVars];
		    while (true) {

			// endobserve
			stream.nextToken();
			if (stream.sval.compareTo("endobserve") == 0) break;

			// obs cpt
			else {
			    int varId = varNames.indexOf(stream.sval);
			    if (varId < nStateVars || varId >= varNames.size()/2) error("Invalid observation name");
			    obsCPTs[varId-nStateVars] = OP.reorder(parseDD());
														
			    // normalize
			    DD[] dds = new DD[1];
			    dds[0] = obsCPTs[varId-nStateVars];
			    int[] vars = new int[1];
			    vars[0] = varId + varNames.size()/2+1;
			    DD normalizationFactor = OP.addMultVarElim(dds,vars);
			    if (unnormalized) 
				obsCPTs[varId-nStateVars] = OP.div(obsCPTs[varId-nStateVars],normalizationFactor);
			    else if (OP.maxAll(OP.abs(OP.sub(normalizationFactor,DD.one))) > 1e-8)
				error("Unnormalized cpt for " + varNames.get(varId) + "'");
			}
		    }
		    actObserve.add(obsCPTs);
		}
								
		// cpt
		else {
		    int varId = varNames.indexOf(stream.sval);
		    if (varId == -1 || varId >= nStateVars) error("Invalid variable name");
		    cpts[varId] = OP.reorder(parseDD());

		    // normalize
		    DD[] dds = new DD[1];
		    dds[0] = cpts[varId];
		    int[] vars = new int[1];
		    vars[0] = varId + varNames.size()/2+1;
		    DD normalizationFactor = OP.addMultVarElim(dds,vars);
		    if (unnormalized) 
			cpts[varId] = OP.div(cpts[varId],normalizationFactor);
		    else if (OP.maxAll(OP.abs(OP.sub(normalizationFactor,DD.one))) > 1e-8)
			error("Unnormalized cpt for " + varNames.get(varId) + "'");
		}
	    }
	    actTransitions.add(cpts);
						
	} catch (IOException e) {
            System.out.println("Error: IOException\n");
            //System.exit(1);
        }
    }

    public void parseReward() {
	reward = OP.reorder(parseDD());
    }

    public void parseInit() {
	init = OP.reorder(parseDD());
        int[] vars = new int[nStateVars];
	for (int i=0; i<nStateVars; i++) vars[i] = i+1;
	DD[] dds = new DD[1];
	dds[0] = init;
	init = OP.div(init,OP.addMultVarElim(dds,vars));
    }

    public void parseDiscount() {
	try {
	    if (stream.nextToken() != StreamTokenizer.TT_NUMBER) error("Expected a number");
	    discount = DDleaf.myNew(stream.nval);
	} catch (IOException e) {
            System.out.println("Error: IOException\n");
            //System.exit(1);
        }
    }

    public void parseHorizon() {
	try {
	    if (stream.nextToken() != StreamTokenizer.TT_NUMBER) error("Expected a number");
	    horizon = DDleaf.myNew(stream.nval);
	} catch (IOException e) {
            System.out.println("Error: IOException\n");
            //System.exit(1);
        }
    }

    public void parseTolerance() {
	try {
	    if (stream.nextToken() != StreamTokenizer.TT_NUMBER) error("Expected a number");
	    tolerance = DDleaf.myNew(stream.nval);
	} catch (IOException e) {
            System.out.println("Error: IOException\n");
            //System.exit(1);
        }
    }

    public static void main(String args[]) {
	ParseSPUDD file = new ParseSPUDD("/h/23/ppoupart/projects/vdcbpi/code/perseus+adds/zmj9p_hidden1_po.txt");
	file.parsePOMDP(false);
    }
}






