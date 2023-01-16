package main.io;

import java.io.IOException;

/**
 * This class is designed to read console user keyboard input.
 * 
 * @author SoundlyGifted
 */
public final class KeyboardInputReader {
    
    private KeyboardInputReader() {}
    
    /**
     * Reads console user keyboard input until the newline (line feed) symbol 
     * '\n' is met (Enter pressed).
     * 
     * @return 
     */
    public static String readKeyboardInput() {
        int i;
        StringBuffer sb = new StringBuffer();
        while(true) {
            try {
                i = System.in.read();
                if (i == '\n') {
                    break;
                }
                sb.append((char) i);
            } catch (IOException ioex) {
                
                break;
            }
        }
        return sb.toString();
    }
}
