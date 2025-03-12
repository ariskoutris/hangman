package hangman;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


public class Game{
    
    int points;
    int hp;

    boolean revealedCells[];
    String targetWord;
    int targetLen;

    double[][] cellProbabilities;
    List<String> possibleWords = new ArrayList<>();

    Dictionary dict;
    String[] words;


    
    public String getTargetWord() { return targetWord; }
    public int getTargetLen() { return targetLen; }
    public int getHP() { return hp; }
    public int getPoints() { return points; }
    public boolean[] getRevealedCells() { return revealedCells; }



    Game() {}

    Game(String dictionary_id) throws Exception {
        setDict(dictionary_id);
    }

    Game(Dictionary dictionary) throws Exception {
        dict = dictionary;
        words = dict.getWords();
    }

    public void setDict(String dictionary_id) throws Exception {
        this.dict = new Dictionary(dictionary_id);
        words = dict.getWords();
    }

    public void setDict(Dictionary dictionary) throws Exception {
        this.dict = dictionary;
        words = dict.getWords();
    }

    public void start() throws Exception {
        targetWord = pickRandomWord();
        targetLen = targetWord.length();
        revealedCells = new boolean[targetLen];
        possibleWords = Arrays.stream(words).filter(x -> x.length() == targetLen).collect(Collectors.toList());
        cellProbabilities = new double[targetLen][];
        for (int cell = 0; cell < targetLen; cell++) {
            cellProbabilities[cell] = calculateCellProbabilities(cell);
        }
        points = 0;
        hp = 6;
    }

    private String pickRandomWord() throws IOException {
        Random rand = new Random();
        int picked_index = rand.nextInt(words.length);
        String picked_word = words[picked_index];
        return picked_word;
    }

    public Integer updateState(int cell, char c) {
        if (revealedCells[cell]) {
            return 2;
        }
        c = Character.toUpperCase(c);
        if (targetWord.charAt(cell) == c) {
            revealedCells[cell] = true;
            for (Iterator<String> it = possibleWords.iterator(); it.hasNext();) {
                String word = it.next();
                if (word.charAt(cell) != c) {
                    it.remove();
                }
            }
            updatePoints(cell, c, true);
            boolean win = true;
            for (int i = 0; i < targetLen; i++) {
                if (!revealedCells[i]) {
                    win = false;
                }
            }
            if (win) {
                return 1;
            }
        } else {
            for (Iterator<String> it = possibleWords.iterator(); it.hasNext();) {
                String word = it.next();
                if (word.charAt(cell) == c) {
                    it.remove();
                }
            }
            updatePoints(cell, c, false);
            hp -= 1;
            if (hp == 0) {
                return -1;
            }
        }
        for (int cl = 0; cl < targetLen; cl++) {
            cellProbabilities[cl] = calculateCellProbabilities(cl);
        }
        return 0;
    }

    private void updatePoints(int cell, char c, boolean success) {  
        double probability = cellProbabilities[cell][c - 'A'];
        if (success) {
            if (probability >= 0.6) {
                points += 5;
            } else if (probability >= 0.4) {
                points += 10;
            } else if (probability >= 0.25) {
                points += 15;
            } else {
                points += 30;
            }
        } else {
            points = Math.max(points-15,0);
        }
    }

    public char[] getCellChoices(int cell) {
        Integer[] sorted = argsort(cellProbabilities[cell]);
        
        int nonzero = 0;
        for (double probability : cellProbabilities[cell]) if (probability > 0) nonzero++;

        // add random letters as extra choices if less than 5 nonzero probability letters exist
        int fill = Math.max(5-nonzero,0);

        char[] choices = new char[nonzero+fill];
        for(int i = 0; i < nonzero; i++) choices[i] = (char) (sorted[25-i] + 'A');

        if (fill > 0) {
            Random rand = new Random();
            boolean ifNotExists;
            char choice;
            for (int i = nonzero; i < nonzero+fill; i++) {
                choice = (char) (rand.nextInt(26) + 'A');
                ifNotExists = new String(choices).indexOf(choice) == -1;
                while (!ifNotExists) {
                    choice = (char) (rand.nextInt(26) + 'A');
                    ifNotExists = new String(choices).indexOf(choice) == -1;
                }
                choices[i] = choice;
            }
        }

        return choices;
    }

    private double[] calculateCellProbabilities(int cell) {
        double[] freqs = new double[26]; 
        for (String word : possibleWords) {
            freqs[word.charAt(cell)-'A'] += 1;
        }
        for (int i = 0; i < freqs.length; i++) {
            freqs[i] /= possibleWords.size();
        }
        return freqs;  
    }

    public String getSecretString() {
        char[] secret = new char[2*targetLen-1];
        char c;
        
        for (int i = 0; i < targetLen-1; i++) {
            c = targetWord.charAt(i);
            if (revealedCells[i]) {
                secret[2*i] = c;
            } else {
                secret[2*i] = '_';
            }
            secret[2*i+1] = ' ';
        }
        
        if (revealedCells[targetLen-1]) secret[2*targetLen-2] = targetWord.charAt(targetLen-1);
        else secret[2*targetLen-2] = '_';
 
        String secretS = new String(secret);
        return secretS;
    }

    public void printState() {
        System.out.println("----------------CURRENT STATE----------------");
        System.out.println("Revealed Cells:");
        System.out.println(Arrays.toString(revealedCells));
        System.out.println("Possible Words:");
        System.out.println(Arrays.toString(possibleWords.toArray()));
        System.out.println("HP: " + hp);
        System.out.println("Points: " + points);
        System.out.println("---------------------------------------------");
    }

    private Integer[] argsort(double[] A) {
        Integer[] res = new Integer[A.length];
        for(int i = 0; i < res.length; i++) res[i] = i;
        Arrays.sort(res, Comparator.comparingDouble(o->A[o]));
        return res;
    }

}