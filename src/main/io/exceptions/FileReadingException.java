package main.io.exceptions;

/**
 * Checked Exception that is thrown when file cannot be read or is not an 
 * allowed file type.
 * 
 * @author SoundlyGifted
 */
public class FileReadingException extends Exception {
    
    public FileReadingException() {
        super();
    }
    
    public FileReadingException(String message){
        super(message);
    }
    
    public FileReadingException(String message, Throwable cause){
        super(message, cause);
    }
    
    public FileReadingException(Throwable cause){
        super(cause);
    }    
}
