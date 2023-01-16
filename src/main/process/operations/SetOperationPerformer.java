package main.process.operations;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import main.process.exceptions.WrongScriptExpressionException;

/**
 * This class performs "set" script operation.
 * 
 * @author SoundlyGifted
 */
public class SetOperationPerformer extends OperationPerformer{

    // Collection of SET operation math operators supported in the application.
    private final Collection<String> SET_OPERATION_OPERATORS;
    
    /**
     * Creates SetOperationPerformer instance and initializes private field
     * that contains supported SET operation math operators.
     */
    public SetOperationPerformer() {
        /* ArrayList maintains insertion order and will iterate in the 
         * order in which the elements were added. So adding SET operation 
         * operators in the order of their priority.
         */
        SET_OPERATION_OPERATORS = new ArrayList<>(4);
        SET_OPERATION_OPERATORS.add("*");
        SET_OPERATION_OPERATORS.add("/");
        SET_OPERATION_OPERATORS.add("-");
        SET_OPERATION_OPERATORS.add("+");
    }
    
    
    @Override
    public void performOperation(String operationExpression, 
            Map<String, Integer> vars) throws WrongScriptExpressionException {
        // SET operation expression must not be null or empty.
        if (operationExpression == null || operationExpression.strip().isEmpty()) {
            throw new WrongScriptExpressionException("Empty set expression is "
                    + "not allowed.");
        }

        // Parsing SET operation expression.
        Entry<Entry<String, List<String>>, Map<Integer, Integer>> 
                parsingResult = parseSetExpression(operationExpression, vars);
        /* Getting results of parsing: 
         * 1) SET variable
         * 2) list of SET expression members to be resolved into a value.
         * 3) Mapping of open bracket indexes to the priorities of operations 
         * in brackets in the SET expression (based on brackets count).
         */
        Entry<String, List<String>> parsedExpression = parsingResult.getKey();
        String varToAssign = parsedExpression.getKey();
        List<String> expMembers = parsedExpression.getValue();  
        Map<Integer, Integer> openBracketIndexes = parsingResult.getValue();
        
        /* Calculating SET expression and assing the result value 
         * to the SET variable.
        */
        Integer finalResult = null;
        
        if (!openBracketIndexes.isEmpty()) {
            finalResult = calculateExpressionWithBrackets(expMembers, 
                    openBracketIndexes);
        } else {
            finalResult = calculateExpressionWithoutBrackets(expMembers);
        }

        if (finalResult == null) {
            throw new WrongScriptExpressionException("SET statement 'set " 
                    + operationExpression + "' has no result.");
        }
        
        /* Assigning SET expression calculation result to the SET variable by 
         * storing them in the Map as Value and Key respectively.
         */
        vars.put(varToAssign, finalResult);
    }
    
    
    private Entry<Entry<String, List<String>>, Map<Integer, Integer>> 
        parseSetExpression(String operationExpression, Map<String, Integer> vars) 
                throws WrongScriptExpressionException {
        
        // Declare variables to hold results of parsing:
        /* String to hold SET operation variable (for calculated expression 
         * result assignment).
         */
        String varToAssign;
        /* List to hold parsed expression members of a SET operation expression 
         * that need to be resolved (calculated) into a value.
         */
        List<String> expMembers;
        /* Map Entry to hold SET parsing result (i.e., mapping of SET operation 
         * variable to the list of parsed SET expression members that need to
         * be resolved into a value).
         */
        Entry<String, List<String>> parsedExpression;
        /* Mapping of open bracket indexes to the priorities of operations in 
         * brackets (based on brackets count).
         */
        Map<Integer, Integer> openBracketIndexes;
        
        if (!operationExpression.contains("=")) {
            throw new WrongScriptExpressionException("Invalid set statement. "
                    + "Must contain assignment operator '='.");
        } else {
            if (operationExpression
                    .substring(operationExpression.indexOf("=") + 1)
                    .contains("=")) {
                throw new WrongScriptExpressionException("Invalid set "
                        + "statement. Must contain only one assignment operator"
                        + " '='.");
            }
        }

        // Defining variable name to assign the value by this SET operation.
        // Defining expression to assign to the variable by this SET operation.
        String[] partsOfSet = operationExpression.strip().split("=");
        if (partsOfSet.length == 0) {
            throw new WrongScriptExpressionException("Invalid set statement. "
                    + "No variable for value assignment in SET operation");
        } else if (partsOfSet.length == 1) {
            throw new WrongScriptExpressionException("Invalid set statement. "
                    + "No value to be assigned in SET assignment operation");
        }
        // Checking variable name for the variable to be assigned.
        varToAssign = partsOfSet[0].strip();
        String expressionToBeAssigned = partsOfSet[1].strip();
        if (!isAllowedVarName(varToAssign)) {
            throw new WrongScriptExpressionException("Invalid set statement. "
                    + "Variable '" + varToAssign + "' is not a valid variable "
                    + "name.");
        }

        // Adding spaces around brackets for proper further split into array.
        expressionToBeAssigned
                = expressionToBeAssigned
                        .replace("(", " ( ")
                        .replace(")", " ) ")
                        .strip();

        // Parsing expression to assign to the variable.
        expMembers = new ArrayList<>(Arrays.asList(expressionToBeAssigned
                        .replaceAll("\\s+", " ").split(" ")));

        // Variables to define errors in script operation expression.
        String wrongVarName = null;
        String wrongValueFormat = null;
        String notAssignedVarName = null;
        String valueHasMissingOperator = null;
        boolean nowProcessingOperation = false;

        int iterationCount = 0;
        int membersCount = 0; // expression members count - except brackets

        int bracketsCount = 0;
        openBracketIndexes = new HashMap<>();

        for (String m : expMembers) {

            if (m.equals("(") || m.equals(")")) {
                if (m.equals("(")) {
                    bracketsCount++;
                    openBracketIndexes.put(iterationCount, bracketsCount);
                } else {
                    bracketsCount--;
                }
                iterationCount++;
                continue;
            }

            membersCount++;
            if (m.startsWith("$")) {
                // Check if previous member was also a number.
                if (membersCount != 1 && nowProcessingOperation == false) {
                    valueHasMissingOperator = m;
                    break;
                }
                nowProcessingOperation = false;

                if (isAllowedVarName(m)) {
                    Integer value = vars.get(m);
                    if (value != null) {
                        expMembers.set(iterationCount, value.toString());
                    } else {
                        notAssignedVarName = m;
                        break;
                    }
                } else {
                    wrongVarName = m;
                    break;
                }
            } else {
                if (m.length() == 1) {
                    if (SET_OPERATION_OPERATORS.contains(m)) {
                        /* Check if previous member was also an operator.
                         * Check if this operator is the last member of 
                         * expression.
                         */
                        if (nowProcessingOperation
                                || membersCount == expMembers.size()) {
                            wrongValueFormat = m;
                            break;
                        }
                        nowProcessingOperation = true;
                    } else {
                        try {
                            Integer.valueOf(m);
                            // Check if previous member was also a number.
                            if (membersCount != 1
                                    && nowProcessingOperation == false) {
                                wrongValueFormat = m;
                                break;
                            }
                        } catch (NumberFormatException e) {
                            wrongValueFormat = m;
                            break;
                        }
                        nowProcessingOperation = false;
                    }
                } else {
                    // Check if previous member was also a number.
                    if (membersCount != 1 && nowProcessingOperation == false) {
                        valueHasMissingOperator = m;
                        break;
                    }
                    nowProcessingOperation = false;

                    try {
                        Integer.valueOf(m);
                    } catch (NumberFormatException e) {
                        wrongValueFormat = m;
                        break;
                    }
                }
            }
            iterationCount++;
        }

        // Errors found in this script during parsing.
        if (bracketsCount != 0) {
            throw new WrongScriptExpressionException("SET statement 'set "
                    + operationExpression + "' is invalid. Check brackets.");
        } else if (wrongVarName != null) {
            throw new WrongScriptExpressionException("SET statement 'set "
                    + operationExpression + "' is invalid. Variable '"
                    + wrongVarName + "' naming is invalid.");
        } else if (wrongValueFormat != null) {
            throw new WrongScriptExpressionException("SET statement 'set "
                    + operationExpression + "' is invalid. Value '"
                    + wrongValueFormat + "' is not a number or a valid "
                    + "variable name.");
        } else if (notAssignedVarName != null) {
            throw new WrongScriptExpressionException("SET statement 'set "
                    + operationExpression + "' is invalid. Variable "
                    + notAssignedVarName + " has no assigned value.");
        } else if (valueHasMissingOperator != null) {
            throw new WrongScriptExpressionException("SET statement 'set "
                    + operationExpression + "' is invalid. '"
                    + valueHasMissingOperator + "' has no preceding operator.");
        }
        
        // Returning result when no exception was thrown during parsing.
        parsedExpression = new AbstractMap.SimpleEntry<>(varToAssign, expMembers);
        return new AbstractMap.SimpleEntry<>(parsedExpression, openBracketIndexes);
    }


    // Calculates expression that contain brackets.
    private Integer calculateExpressionWithBrackets(List<String> expMembers,
            Map<Integer, Integer> openBracketIndexes) {
        
        // Variables declaration.
        /* First, each sub-expression in brackets needs to be calculated in 
         * order of priority (based on brackets count) and substituted by the 
         * result value. Then the final expression without brackets need to be 
         * calculated based on preceding calculations of all the expressions in 
         * brackets.
         */
        Integer result; // Calculation result of each sub-expression in brackets.
        List<String> subExpMembers; // List of sub-expression members in brackets.
        
        // Sorting Map of open brackets by priority.
        List<Entry<Integer, Integer>> openBracketsIndexesList
                = new ArrayList<>(openBracketIndexes.entrySet());
        openBracketsIndexesList.sort((Entry<Integer, Integer> entry1,
                Entry<Integer, Integer> entry2) -> {
            Integer entry1Priority = entry1.getValue();
            Integer entry2Priority = entry2.getValue();
            if (entry2Priority > entry1Priority) {
                return 1;
            } else if (entry2Priority < entry1Priority) {
                return -1;
            }
            return 0;
        });

        // Resolving each sub-expression in brackets into a result number.
        // Index of start of an expression in brackets on this iteration.
        Integer startIndex; 
        // Index of end of an expression in brackets on this iteration.
        Integer endIndex;
        /* Value of shift by which the other open bracket indexes must 
         * be shifted to the left after the current bracket expression was 
         * resolved into a single value.
         * The shift is relevant to all the bracket indexes that were greater 
         * than the current index of the iteration (brackets on the right).
         */
        Integer indexShift;
        for (Entry<Integer, Integer> ind : openBracketsIndexesList) {

            Integer currentIndexByPriority = ind.getKey();

            startIndex = currentIndexByPriority;
            endIndex = startIndex + expMembers
                    .subList(startIndex, expMembers.size()).indexOf(")");

            subExpMembers = expMembers.subList(startIndex + 1, endIndex);

            result = calculateExpressionWithoutBrackets(subExpMembers);

            // Removing the expression in brackets, adding the result instead.
            for (int i = startIndex; i <= endIndex; i++) {
                expMembers.remove((int) startIndex);
            }
            expMembers.add(startIndex, result.toString());

            indexShift = endIndex - startIndex;

            // Shifting indexes in original index list.
            int j = 0;
            for (Entry<Integer, Integer> newInd : openBracketsIndexesList) {
                Integer otherIndexInList = newInd.getKey();
                Integer otherIndexInListPriority = newInd.getValue();
                /* Shifting to the left only the affected open bracket indexes 
                 * (i.e., those that were greater than the current index).
                 */
                if (otherIndexInList > currentIndexByPriority) {
                    openBracketsIndexesList.set(j,
                            new AbstractMap.SimpleEntry<>(otherIndexInList
                                    - indexShift, otherIndexInListPriority));
                }
                j++;
            }
        }
        /* Calculating last expression after resolving of all sub-expressions 
         * in brackets.
         */
        return calculateExpressionWithoutBrackets(expMembers);
    }
    
    
    /* Calculates sub-expression inside brackets (or expression that does not
     * contain brackets) that contain only some combination of simple arithmetic 
     * operations.
     */
    private Integer calculateExpressionWithoutBrackets(List<String> 
            parsedExpression) {
        /* Converting array of strings into LinkedList as it is more effective 
         * to modify it.
         */
        List<String> listOfMembers = new LinkedList<>(parsedExpression);

        /* Calculating expression on checked and resolved expression members.
         * Looping over all supported operations (sorted by priority).
         */
        String currentOperation;
        Integer indexOfCurrentOperation;
        for (String supportedOperation : SET_OPERATION_OPERATORS) {
            /* Creating new list to iterate over.
             * This is needed to avoid Concurrent Modification Exception (CME).
             * CME is thrown when changing the collection while iterating over it.
             */
            List<String> listOfMembersCopy = new LinkedList<>(listOfMembers);

            // To loop over expression only if it contains this operation.
            if (listOfMembersCopy.contains(supportedOperation)) {
                for (String mbr : listOfMembersCopy) {

                    indexOfCurrentOperation = null;
                    /* Find operation symbol in this expression members 
                     * collection and record its index.
                     */
                    if (mbr.equals(supportedOperation)) {
                        currentOperation = mbr;
                        if (indexOfCurrentOperation == null) {
                            indexOfCurrentOperation = listOfMembers.indexOf(mbr);
                        } else {
                            indexOfCurrentOperation
                                    = (indexOfCurrentOperation + 1)
                                    + listOfMembers
                                            .subList(indexOfCurrentOperation + 1,
                                                    listOfMembers.size())
                                            .indexOf(mbr);
                        }

                        String result = performBinaryOperation(currentOperation,
                                Integer.valueOf(listOfMembers
                                        .get(indexOfCurrentOperation - 1)),
                                Integer.valueOf(listOfMembers
                                        .get(indexOfCurrentOperation + 1)))
                                .toString();

                        listOfMembers.remove(indexOfCurrentOperation.intValue());
                        listOfMembers.add(indexOfCurrentOperation, result);

                        listOfMembers.remove(indexOfCurrentOperation + 1);
                        listOfMembers.remove(indexOfCurrentOperation - 1);
                    }
                }
            }
        }
        /* In the end the collection of expression members contain only one 
         * member (the result).
         */
        return Integer.valueOf(listOfMembers.get(0));
    }
    
    
    // Performs simple binary operation (math operation with two arguments).
    private Integer performBinaryOperation(String SET_OPERATION_SYMBOL,
            Integer x, Integer y) {
        Integer result = null;
        switch (SET_OPERATION_SYMBOL) {
            case "+":
                result = x + y;
                break;
            case "-":
                result = x - y;
                break;
            case "*":
                result = x * y;
                break;
            case "/":
                result = x / y;
                break;
        }
        return result;
    }
}