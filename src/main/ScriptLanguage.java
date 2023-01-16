package main;

import java.util.Collection;
import main.io.KeyboardInputReader;
import main.process.ScriptInterpreter;
import main.process.exceptions.UnsupportedScriptOperationException;
import main.process.exceptions.WrongScriptExpressionException;
import main.io.ScriptReader;
import main.io.exceptions.FileReadingException;

/**
 * Main class of script language interpreter program that interprets and 
 * executes a script written on a custom script language.
 * 
 * @author SoundlyGifted
 */
public class ScriptLanguage {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String fileName; // Name of the file that contains the script.
        Collection<String> scriptLines; // Script lines read from the file.
        
        /* Reading the name of the file as a command line argument or as a 
         * console user keyboard input (if no argument was provided).
         */
        if (args.length > 1) {
            System.out.println("[ScriptLanguage: Command Line Args Error] "
                    + "Application launched with more than one command line "
                    + "argument.");
            waitForUserInput();
            return;
        } else if (args.length > 0) {
            fileName = args[0];
        } else {
            System.out.println("[ScriptLanguage] Application launched with no "
                    + "command line arguments.");
            System.out.print("[ScriptLanguage] Please input the name of the "
                    + "text file (including extension), i.e. 'file.txt': ");
            fileName = KeyboardInputReader.readKeyboardInput();
            System.out.println();
        }

        // Reading the script from the file.
        try {
            scriptLines = ScriptReader.readScriptFile(fileName);            
        } catch (FileReadingException frex) {
            /* Printing to the screen the file reading error message obtained 
             * from the custom exception for the user to check the input file.
             */
            if (frex.getCause() != null) {
                System.out.println("[ScriptLanguage: File Reading Error] " 
                        + frex.getMessage() + ", caused by: " 
                        +  frex.getCause().toString());
            } else {
                System.out.println("[ScriptLanguage: File Reading Error] " 
                        + frex.getMessage());
            }
            waitForUserInput();
            return;
        }
        
        /* Making script interpretation (parsing, validating, calculating / 
         * processing each statement in the script consequently).
        */
        ScriptInterpreter interpreter = new ScriptInterpreter();
        try {
           interpreter.interpretScript(scriptLines);
           System.out.println();
        } catch(UnsupportedScriptOperationException 
                | WrongScriptExpressionException exception) {
            /* Printing to the screen the error message obtained from the custom 
             * exception for the user to revise the script accordingly.
             */
            System.out.println("[ScriptLanguage: Script Error] " 
                    + exception.getMessage());
        }
        waitForUserInput();
    }
    
    
    private static void waitForUserInput() {
        System.out.print("[ScriptLanguage] Program finished running. "
                + "Press Enter to finish... ");
        KeyboardInputReader.readKeyboardInput();
    }
}
