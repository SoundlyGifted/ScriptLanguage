package main.process;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import main.process.exceptions.UnsupportedScriptOperationException;
import main.process.exceptions.WrongScriptExpressionException;
import main.process.operations.OperationPerformer;
import main.process.operations.PrintOperationPerformer;
import main.process.operations.SetOperationPerformer;

/**
 * This class represents Script Interpreter and contains methods to interpret
 * and execute 
 * 
 * @author SoundlyGifted
 */
public class ScriptInterpreter {
    
    // Set of script language operators supported in the application.
    private final Set<String> OPERATORS;
    
    /* Mapping of script language variables to their values obtained by "set" 
     * script language operation during the script interpretation.
     */
    private Map<String, Integer> vars;


    /**
     * Creates instance of Script Interpreter.
     * Initializes private fields that contain supported script operators and 
     * script variables/values mapping.
     */
    public ScriptInterpreter() {
        OPERATORS = new HashSet<>();
        OPERATORS.add("#"); // This is a comment in the script (ignored).
        OPERATORS.add("print"); // This is a print operation in the script.
        OPERATORS.add("set"); // This is an assignment operation in the script.

        vars = new HashMap<>();
    }

    
    /**
     * This method receives script lines collection as the input paramter and 
     * performs script interpretation and execution in the following way.1) Each 
     * script line (statement) is parsed to allocate script operator and the 
     * rest of the line, i.e.the operation (expression to be processed). 2) The 
     * operation expression is parsed, evaluated, and processed based on the 
     * type of the operation (operator) and the whole operation is performed. In 
     * case of any errors in the script the corresponding message is printed to 
     * the screen for the user.
     * 
     * @param scriptLines the collection of script lines o(statements) to be 
     * parsed, evaluated and performed.
     * @throws main.process.exceptions.UnsupportedScriptOperationException is 
     * thrown when the script operation is not supported in the application.
     * @throws main.process.exceptions.WrongScriptExpressionException is thrown 
     * when the script operation expression has wrong syntax.
     */
    public void interpretScript(Collection<String> scriptLines) 
            throws UnsupportedScriptOperationException, 
            WrongScriptExpressionException {
        
        OperationPerformer printPerformer = null;
        OperationPerformer setPerformer = null;
        
        Entry<String, String> parsedLine;
        String operator;
        String operationExpression;
        for (String line : scriptLines) {
            parsedLine = parseLine(line);
            operator = parsedLine.getKey();
            operationExpression = parsedLine.getValue();
            
            switch (operator) {
                case "print":
                    if (printPerformer == null) {
                        printPerformer = new PrintOperationPerformer();
                    }
                    printPerformer.performOperation(operationExpression, vars);
                    break;
                case "set":
                    if (setPerformer == null) {
                        setPerformer = new SetOperationPerformer();
                    }
                    setPerformer.performOperation(operationExpression, vars);
                    break;
                default:
                    break;
            }
        }
        
        /* Initializing a new set of script language variables for the next 
         * script interpretation.
         */
        vars = new HashMap<>();
    }
    
    
    private Entry<String, String> parseLine(String scriptLine) 
            throws UnsupportedScriptOperationException {
        
        Entry<String, String> parsedLine;

        // Ignoring blank lines (empty script language statements).
        if (scriptLine == null || scriptLine.strip().isEmpty()) {
            parsedLine = new AbstractMap.SimpleEntry<>("", "");
            return parsedLine;
        }
        scriptLine = scriptLine.strip();

        String operator;
        String operationExpression;
        
        if (scriptLine.contains(" ")) {
            operator = scriptLine.substring(0, scriptLine.indexOf(" "));
            operationExpression = scriptLine.substring(scriptLine.indexOf(" "))
                    .strip();
        } else {
            operator = scriptLine;
            operationExpression = "";
        }

        /* Checking if the script language operator is supported in this 
         * application.
         */
        boolean isSupportedOperation = false;
        for (String supportedOperator : OPERATORS) {
            if (operator.equals(supportedOperator)) {
                isSupportedOperation = true;
            }
        }
        if (!isSupportedOperation) {
            throw new UnsupportedScriptOperationException("Such operation as '" 
                    + operator + "' is not supported. Program will terminate");
        }
        
        parsedLine = new AbstractMap.SimpleEntry<>(operator, operationExpression);
        return parsedLine;
    }
}