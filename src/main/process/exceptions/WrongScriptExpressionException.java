package main.process.exceptions;

/**
 * Checked Exception that is thrown when the script expression has wrong syntax.
 * 
 * @author SoundlyGifted
 */
public class WrongScriptExpressionException extends Exception {
    
    public WrongScriptExpressionException() {
        super();
    }
    
    public WrongScriptExpressionException(String message){
        super(message);
    }
    
    public WrongScriptExpressionException(String message, Throwable cause){
        super(message, cause);
    }
    
    public WrongScriptExpressionException(Throwable cause){
        super(cause);
    }
}
