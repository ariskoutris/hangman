package hangman;

public class Entry {
    String word, outcome;
    int guesses, points;

    public String getWord() { return word; }
    public String getOutcome() { return outcome; }
    public int getGuesses() { return guesses; }
    public int getPoints() { return points; }

    Entry (String word, Integer guesses, Integer points, String outcome) {
        this.word = word;
        this.guesses = guesses;
        this.points = points;
        this.outcome = outcome;
    }
}
