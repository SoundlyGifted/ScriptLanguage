package main.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import main.io.exceptions.FileReadingException;

/**
 * This class is designed to read a sript line by line from a specified file.
 * 
 * @author SoundlyGifted
 */
public final class ScriptReader {
    
    // Acceptable file extensions
    private static final String[] INPUT_FILE_EXTENSIONS = {"txt"};    
    
    private ScriptReader() {}
    
    /**
     * This method reads a script from a text file line by line.
     * 
     * @param inFileName input text file name including extension.
     * @return Collection of strings (lines read from the file).
     * @throws main.io.exceptions.FileReadingException is thrown when the file 
     * cannot be read or is not an allowed file type.
     */
    public static Collection<String> readScriptFile(String inFileName) 
            throws FileReadingException {
        if (inFileName == null || inFileName.strip().isEmpty()) {
            throw new FileReadingException("File to read not specified");
        }

        File file = new File(inFileName);
         
        if (file.isFile() && file.canRead()) {
            String fileExtension = getFileExtension(file.getName());
            inputFileExtensionCheck(fileExtension);
           
            try (BufferedReader input 
                    = new BufferedReader(new FileReader(inFileName))) {
                Collection<String> lines = new LinkedList<>();
                String line;
                while ((line = input.readLine()) != null) {
                    lines.add(line);
                }
                return new ArrayList<>(lines);
            } catch (IOException ioex) {
                throw new FileReadingException("Error during reading the '" 
                        + inFileName + "' file", ioex);
            }
        }
        throw new FileReadingException("File '" + inFileName + "' not found or "
                + "cannot read the file");
    }
    
    
    private static String getFileExtension(String fileName) {
        
        final String WINDOWS_FILE_SEPARATOR = "\\";
        final String UNIX_FILE_SEPARATOR = "/";
        final String FILE_EXTENSION = ".";
        
        String extension = "";
        
        int indexOfLastExtension = fileName.lastIndexOf(FILE_EXTENSION);
        
        // check last file separator, windows and unix
        int lastSeparatorPosWindows 
                = fileName.lastIndexOf(WINDOWS_FILE_SEPARATOR);
        int lastSeparatorPosUnix 
                = fileName.lastIndexOf(UNIX_FILE_SEPARATOR);
        
        // takes the greater of the two values, which mean last file separator
        int indexOflastSeparator 
                = Math.max(lastSeparatorPosWindows, lastSeparatorPosUnix);
        
        // make sure the file extension appear after the last file separator
        if (indexOfLastExtension > indexOflastSeparator) {
            extension = fileName.substring(indexOfLastExtension + 1);
        }
        return extension;
    }

    
    private static void inputFileExtensionCheck(String fileExtension) 
            throws FileReadingException {
        boolean isAcceptable = false;
        for (String acceptableExtension : INPUT_FILE_EXTENSIONS) {
            if (fileExtension.equals(acceptableExtension)) {
                isAcceptable = true;
            }
        }
        if (isAcceptable == false) {
            StringBuilder sb = new StringBuilder();
            for (String ext : INPUT_FILE_EXTENSIONS) {
                sb.append(ext);
                sb.append(" ");
            }
            throw new FileReadingException("The input file extension is not "
                    + "acceptable. Acceptable extensions: "
                    + sb.toString().strip());
        }
    }
}
