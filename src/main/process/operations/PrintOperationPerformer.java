package main.process.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import main.process.exceptions.WrongScriptExpressionException;

/**
 * This class performs "print" script operation.
 * 
 * @author SoundlyGifted
 */
public class PrintOperationPerformer extends OperationPerformer {

    /**
     * Default PrintOperationPerformer constructor that creates instance of 
     * PrintOperationPerformer.
     */
    public PrintOperationPerformer() {}
    
    @Override
    public void performOperation(String operationExpression, 
            Map<String, Integer> vars) throws WrongScriptExpressionException {
        if (operationExpression == null || operationExpression.strip().isEmpty()) {
            // Print blank line.
            System.out.println();
            return;
        }
        operationExpression = operationExpression.strip();

        // Collection of parsed strings.
        Collection<String> printStrings = new ArrayList<>();

        char[] chars = operationExpression.toCharArray();
        StringBuffer sb = new StringBuffer();

        /* Variables to define what is now being read 
         * (a string in quotes or a variable).
         */
        boolean quoteIsOpen = false;
        boolean nowReadingVarName = false;
        boolean commaMet = false;

        // Variables to define errors in the script operation expression.
        boolean wrongVarNamingDetected = false;
        boolean wrongSymbolInOperationDetected = false;

        int countChars = 0;
        for (char c : chars) {
            countChars++;
            if (c == '"') {
                quoteIsOpen = !quoteIsOpen;

                if (nowReadingVarName) {
                    wrongSymbolInOperationDetected = true;
                    break;
                }

                if (!quoteIsOpen && !sb.isEmpty()) {
                    if (!printStrings.isEmpty() && !commaMet) {
                        wrongSymbolInOperationDetected = true;
                        break;
                    }
                    printStrings.add(sb.toString());
                    sb.setLength(0);
                    commaMet = false;
                }
            } else {
                if (quoteIsOpen) {
                    sb.append(c);
                } else {
                    // Quote is not opened, and this is not a quote symbol.

                    if (!nowReadingVarName) {
                        if (c != ' ' && c != ',' && c != '$') {
                            wrongSymbolInOperationDetected = true;
                            break;
                        }
                        if (!sb.isEmpty()) {
                            if (!printStrings.isEmpty() && !commaMet) {
                                wrongSymbolInOperationDetected = true;
                                break;
                            }
                            printStrings.add(sb.toString());
                            sb.setLength(0);
                            commaMet = false;
                        }
                    }

                    if (c == ',') {
                        if (nowReadingVarName && !sb.isEmpty()) {
                            String varName = sb.toString().strip();
                            if (isAllowedVarName(varName)) {
                                /* Rresolvind varName into its value via the 
                                 * class field.
                                 */
                                Integer varValue = vars.get(varName);
                                if (varValue != null) {
                                    printStrings.add(varValue.toString());
                                } else {
                                    printStrings.add("[null]");
                                }
                                commaMet = false;
                            } else {
                                wrongVarNamingDetected = true;
                                break;
                            }
                            sb.setLength(0);
                        }
                        if (!printStrings.isEmpty()) {
                            commaMet = true;
                        }
                        nowReadingVarName = false;
                    }

                    if (c == '$') {
                        // Reading of previous variable is not finished yet.
                        if (nowReadingVarName) {
                            wrongSymbolInOperationDetected = true;
                            break;
                        }

                        nowReadingVarName = true;
                        sb.append(c);
                    } else {
                        if (nowReadingVarName) {
                            if (!printStrings.isEmpty() && !commaMet) {
                                wrongSymbolInOperationDetected = true;
                                break;
                            }
                            sb.append(c);
                        }
                    }

                    /* If this is the end of the script expression and a 
                     * variable name is still being read.
                     */
                    if (countChars == chars.length && nowReadingVarName) {
                        nowReadingVarName = false;
                        if (!sb.isEmpty()) {
                            String varName = sb.toString().strip();
                            if (isAllowedVarName(varName)) {
                                /* resolvind varName into its value via the 
                                 * class field.
                                 */
                                Integer varValue = vars.get(varName);
                                if (varValue != null) {
                                    printStrings.add(varValue.toString());
                                } else {
                                    printStrings.add("[null]");
                                }
                                commaMet = false;
                            } else {
                                wrongVarNamingDetected = true;
                                break;
                            }
                            sb.setLength(0);
                        }
                    }
                }
            }
        }
        
        // Errors found in this script operation expression during parsing.
        if (wrongVarNamingDetected) {
            throw new WrongScriptExpressionException("print statement 'print " 
                    + operationExpression + "' is invalid. Check variable "
                            + "naming.");
        }
        if (wrongSymbolInOperationDetected) {
            throw new WrongScriptExpressionException("print statement 'print " 
                    + operationExpression + "' is invalid. Check syntax.");            
        }
        if (quoteIsOpen) {
            throw new WrongScriptExpressionException("print command 'print " 
                    + operationExpression + "' is invalid. Check quote marks.");               
        }

        // Printing the result of the script operation expression.
        sb.setLength(0);
        if (!printStrings.isEmpty()) {
            for (String str : printStrings) {
                sb.append(str);
            }
        }
        System.out.println(sb.toString());
    }
}