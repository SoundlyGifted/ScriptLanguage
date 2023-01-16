package main.process.operations;

import java.util.Map;
import main.process.exceptions.WrongScriptExpressionException;

/**
 * This is an abstract class of an Operation Performer that should perform one 
 * of the script operations.
 * 
 * @author SoundlyGifted
 */
public abstract class OperationPerformer {
    
    /**
     * Performs (processes, evaluates, calculates) script operation based on a 
     * script expression given as an input parameter.
     * 
     * @param operationExpression a script language expression that should be 
     * processed, evaluated, calculated (performed).
     * @param vars current variable/value mapping of script variables that were 
     * assigned with all the "set" script operations performed by the time when 
     * this method is called.
     * @throws main.process.exceptions.WrongScriptExpressionException is thrown 
     * when the script operation expression has wrong syntax.
     */
    public abstract void performOperation(String operationExpression, 
            Map<String, Integer> vars) throws WrongScriptExpressionException;
    
    /**
     * Checks whether a script variable has allowed name.
     * 
     * @param name name of a variable.
     * @return true if the variable name is allowed, false otherwise.
     */
    protected boolean isAllowedVarName (String name) {
        String wrongSymbols = name.strip().replaceAll("[a-zA-z$0-9]", "");
        return wrongSymbols.isEmpty();
    }
}
