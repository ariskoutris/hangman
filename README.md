# Hangman

Hangman is a JavaFX-based Hangman game developed for the Multimedia Technologies course at the National Technical University of Athens.

![demo-image](https://imgur.com/JSpMReu.png)


## Features

- **Dictionary Creation:**  
  The game automatically creates a word list by fetching book descriptions from the Open Library API.

- **Game Mechanics:**  
  - **Random Word:** The game picks a secret word from the word list.
  - **Smart Letter Suggestions:** For each blank in the word, the game shows a list of likely letters.
  - **Scoring System:** Earn points based on how likely a letter was to be correct. Wrong guesses reduce your points and your remaining chances.

- **Statistics and History:**  
  - **Dictionary Stats:** A pie chart displays the word length distribution of the selected dictionary.
  - **Game History:** A pop-up shows your recent game rounds with details like the secret word, guesses, outcome, and points.



## Requirements

- **Java:** Java 8 Update 311  
- **Libraries:** JavaFX, `javax.json-1.1.4.jar` for JSON parsing
