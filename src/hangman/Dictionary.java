package hangman;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class Dictionary {
    private String open_library_id, dictionary_id;
    private static String medialab_path = "./resources/medialab/";
    private String[] words;

    /**
    * Dictionary constructor specifying the dictionary's ID. 
    * <p>
    * The dictionary_id parameter should correspond to the ID of an already existing dictionary file located in /resources/medialab and named hangman_{dictionary_id}.txt.
    * <p>
    * Initializes  the words array with the words contained in hangman_{dictionary_id}.txt.
    * @param  dictionary_id  an ID the belongs to an already existing dictionary file.
    */
    Dictionary(String dictionary_id) throws Exception {
        this.dictionary_id = dictionary_id;
        Path file = Paths.get(medialab_path + "hangman_" + this.dictionary_id +".txt");
        List<String> contents = Files.readAllLines(file);
        words = contents.toArray(new String[0]);
    }

    /**
    * Dictionary constructor specifying an open library ID and dictionary ID.
    * <p>
    * Gets the decription of a literary work from a json file located in 
    * openlibrary.org/works/{open_library_id}.json, formats it in dictionary 
    * format by keeping only legal words and stores those words in this.words array
    * <p>
    * Also intitializes the string field dictionary_id which is used in case the dictionary is stored locally by calling saveDictionary().
    * <p>
    * If a file with the name /resources/medialab/hangman_{dictionary_id}.txt already exists, it is overwritten.
    * @param  dictionary_id  an ID that will be given to the created dictionary file.
    * @param  open_library_id  an ID that belongs to a literary work of openlibrary.org.
    */
    Dictionary(String dictionary_id, String open_library_id) throws Exception {
        this.open_library_id = open_library_id;
        this.dictionary_id = dictionary_id;
        words = createCorpus(getDescription());
    }

    private String getDescription() throws Exception{
        
        URL target_url = new URL("https://openlibrary.org/works/" + open_library_id + ".json");
        InputStream is = target_url.openStream();
        JsonReader rdr = Json.createReader(is);
	    JsonObject obj = rdr.readObject();
        String description =  obj.getJsonObject("description").toString();

        return description;
    }

    private String[] createCorpus(String description) throws Exception {

        String[] words = description.toUpperCase().replaceAll("[0-9]","").split("\\P{L}+");
        words = Arrays.stream(words).filter(x -> x.length() >= 6).toArray(String[]::new);
        words = Arrays.stream(words).distinct().toArray(String[]::new);
        
        if (words.length < 20) {
            throw new UndersizeException();
        }
        
        String[] long_words = Arrays.stream(words).filter(x -> x.length() >= 9).toArray(String[]::new);
        if (long_words.length < words.length*0.2) {
            throw new UnbalancedException();
        }

        if (words.length != Arrays.stream(words).distinct().toArray(String[]::new).length) {
            throw new InvalidCountExeception();
        }

        int min_len = words[0].length();
        for (int i = 1; i < words.length; i++) {
            min_len = Math.min(min_len, words[i].length());
        }

        if (min_len < 6) {
            throw new InvalidRangeException();
        }
        return words;
    }
    /**
    * Returns an array with the words contained in the dictionary.
    * <p>
    * The words contained in the dictionary are stored in the words class field which is initilialized during the dictionary object's contruction.
    * This method simply returns the words field.
    */
    public String[] getWords() {
        return this.words;
    }

    /**
    * Stores the dictionary's words in a file named /resources/medialab/hangman_{dictionary_id}.txt.
    * <p>
    * if a file with the same name exists it is overwritten, otherwise a new file is created. 
    * <p>
    * The dictionary's words are saved line by line.
    */
    public void saveDictionary() throws Exception {
        Path file = Paths.get(medialab_path + "hangman_" + this.dictionary_id + ".txt");
        Files.write(file, Arrays.asList(this.words), StandardCharsets.UTF_8);
    }

    class InvalidCountExeception extends Exception {
        /**
        * Exception constructor used when the dictionary contains duplicate entries.
        */
        public InvalidCountExeception() {
            super("The dictionary contains duplicate entries");
        }
    }

    class UndersizeException extends Exception {
        /**
        * Exception constructor used when the dictionary has fewer than 20 unique entries.
        */
        public UndersizeException() {
            super("The dictionary has fewer than 20 unique entries");
        }
    }

    class InvalidRangeException extends Exception {
        /**
        * Exception constructor used when the dictionary contains words with less than 6 letters.
        */
        public InvalidRangeException() {
            super("The dictionary contains words with less than 6 letter");
        }
    }

    class UnbalancedException extends Exception {
        /**
        * Exception constructor used when the dictionary doesn't have enough long words. At least 20% of words should consist of 9 or more letters.
        */        
        public UnbalancedException() {
            super("The dictionary doesn't have enough long words");
        }
    }
}