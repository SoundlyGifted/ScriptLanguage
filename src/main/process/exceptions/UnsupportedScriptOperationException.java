package main.process.exceptions;

/**
 * Checked Exception that is thrown when the script operation is not supported
 * in the application.
 * 
 * @author SoundlyGifted
 */
public class UnsupportedScriptOperationException extends Exception{
    
    public UnsupportedScriptOperationException() {
        super();
    }
    
    public UnsupportedScriptOperationException(String message){
        super(message);
    }
    
    public UnsupportedScriptOperationException(String message, Throwable cause){
        super(message, cause);
    }
    
    public UnsupportedScriptOperationException(Throwable cause){
        super(cause);
    }
}
