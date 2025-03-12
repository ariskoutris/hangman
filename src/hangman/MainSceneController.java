package hangman;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.NoSuchFileException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainSceneController{
    
    private Game game;
    private Dictionary dict;
    private boolean gameActive;
    private int guesses, corr_guesses;

    
    @FXML private Button submitBtn;
    @FXML private Text hpText, secretWord, pointsText, wordCountText, successRateText, gameOutcomeText;
    @FXML private Menu applicationMenu, detailsMenu;
    @FXML private MenuItem startMenuItm, loadMenuItm, createMenuItm, exitMenuItm, dictionaryMenuItm, roundsMenuItm, solutionMenuItm;
    @FXML private ListView<String> probabilityList;
    @FXML private ListView<Integer> cellList;
    @FXML private ImageView hangmanImg;
    @FXML private ComboBox<Integer> cellDropdown;
    @FXML private ComboBox<Character> letterDropdown;

    // Add a map to cache letter choices for each cell
    private Map<Integer, char[]> cellLetterChoices;

    final int ROW_HEIGHT = 24;
    private static String hangman_stages_path = "file:./resources/medialab/hangman_stages/";
    private static final DecimalFormat df = new DecimalFormat("0.0");

   public MainSceneController() {
       game = new Game();
       gameActive = false;
       cellLetterChoices = new HashMap<>();
   }

   @FXML private void initialize() {
       String img_filename = hangman_stages_path + "stage0.png";
       Image image = new Image(img_filename);
       hangmanImg.setImage(image);
       gameOutcomeText.setVisible(false);
   }

    @FXML public void startMenuItmHandler(ActionEvent event) {
        try {
            game.start();
            gameActive = true;
            gameOutcomeText.setVisible(false);
            guesses = -1;
            corr_guesses = -1;
            // Clear the letter choices cache when starting a new game
            cellLetterChoices.clear();
            updateInfo();
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Undefined Dictionary");
    		alert.setHeaderText("Please load a dictionary before pressing start.");
    		alert.showAndWait();
        }
    }

    void updateInfo() {
        secretWord.setText(game.getSecretString());
        pointsText.setText(String.valueOf(game.getPoints()));
        wordCountText.setText(String.valueOf(dict.getWords().length));
        
        int newHP = game.getHP();
        int oldHP = Integer.valueOf(hpText.getText());
        guesses += 1;
        if (newHP >= oldHP) corr_guesses += 1;
        hpText.setText(String.valueOf(newHP));

        String img_filename = hangman_stages_path + "stage" + (6-game.getHP()) + ".png";
        Image image = new Image(img_filename);
        hangmanImg.setImage(image);
        String success_rate;
        if (guesses == 0) { success_rate = String.valueOf(df.format(0)); }
        else { success_rate = String.valueOf(df.format((double) 100*corr_guesses/guesses)); }
        successRateText.setText(success_rate +"%");
        updateChoices();
    }

    void updateChoices() {
        char[] choices;
        List<Integer> cellsList = new ArrayList<Integer>();
        List<String> choicesList = new ArrayList<String>();
        StringBuilder sb;
        boolean[] revCells = game.getRevealedCells();
        cellDropdown.getItems().clear();
        letterDropdown.getItems().clear();
        letterDropdown.setDisable(true);
        cellLetterChoices.clear(); // Clear previous choices
        
        for (int cell = 0; cell < game.getTargetLen(); cell++) {
            if (revCells[cell] == true) continue;
            cellsList.add(cell+1);
            
            // Get and cache the letter choices for this cell
            choices = game.getCellChoices(cell);
            cellLetterChoices.put(cell, choices);
            
            cellDropdown.getItems().add(cell+1);
            sb = new StringBuilder();
            sb.append(choices[0]);
            for (int i = 1; i < choices.length; i++) {
                sb.append(" ");
                sb.append(choices[i]);
            }
	    	choicesList.add(sb.toString());
        }
        ObservableList<String> choicesAList = FXCollections.observableArrayList(choicesList);
        probabilityList.setItems(choicesAList);
        probabilityList.setPrefHeight(choicesAList.size() * ROW_HEIGHT + 2);
        ObservableList<Integer> cellsAList = FXCollections.observableArrayList(cellsList);
        cellList.setItems(cellsAList);
        cellList.setPrefHeight(cellsAList.size() * ROW_HEIGHT);
    }

    @FXML void handleCellDropdownChange(ActionEvent event) {
        if (cellDropdown.getValue() == null) {
            letterDropdown.getItems().clear();
            letterDropdown.setDisable(true);
        } else {
            letterDropdown.setDisable(false);
            letterDropdown.getItems().clear();
            int cell = cellDropdown.getValue()-1;
            
            // Use the cached letter choices instead of generating new ones
            char[] choices = cellLetterChoices.get(cell);
            if (choices != null) {
                for(char c: choices) {
                    letterDropdown.getItems().add(c);
                }
            }
        }	    
    }

    @FXML public void createMenuItmHandler(ActionEvent event) {
        Stage stage = new Stage ();
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("hangman/createPopUp.fxml"));
        try {
            Parent root = loader.load();
            stage.setTitle("Create Dictionary");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            CreatePopUpController controller = loader.getController();
            String[] ids = controller.getIDs();

            Dictionary tmp_dict = new Dictionary(ids[0], ids[1]);
            tmp_dict.saveDictionary();

            Alert alert = new Alert(AlertType.INFORMATION);
    		alert.setTitle("Success");
    		alert.setHeaderText("Dictionary created successfully!");
    		alert.showAndWait();
        } catch(FileNotFoundException e) {
            Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Error");
    		alert.setHeaderText("OpenLibrary doesn't have a book with the given ID.");
    		alert.showAndWait();
        } catch(Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Bad Dictionary");
    		alert.setHeaderText(e.getMessage());
    		alert.showAndWait();
        }
    }

    @FXML public void loadMenuItmHandler(ActionEvent event) {
        Stage stage = new Stage ();
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("hangman/loadPopUp.fxml"));
        try {
            Parent root = loader.load();
            stage.setTitle("Load Dictionary");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            LoadPopUpController controller = loader.getController();
            String id = controller.getID();

            this.dict = new Dictionary(id);
            game.setDict(this.dict);
            
            wordCountText.setText(String.valueOf(dict.getWords().length));

            Alert alert = new Alert(AlertType.INFORMATION);
    		alert.setTitle("Success");
    		alert.setHeaderText("Dictionary loaded successfully!");
    		alert.showAndWait();
        } catch(NoSuchFileException e) {
            Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Error");
    		alert.setHeaderText("There is no stored dictionary with the given ID.");
    		alert.showAndWait();
            return;
        } catch(Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Error");
    		alert.setHeaderText(e.getMessage());
    		alert.showAndWait();
            return;
        } 
    }
    
    @FXML public void exitMenuItmHandler(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }
  
    @FXML public void submitBtnHandler(ActionEvent event) {
        if (!gameActive) {
            Alert alert = new Alert(AlertType.INFORMATION);
    		alert.setTitle("Inactive Game");
    		alert.setHeaderText("Please start a new game by pressing Application > Start.");
    		alert.showAndWait();
            return;
        }

        if (cellDropdown.getValue() == null || letterDropdown.getValue() == null) {
            Alert alert = new Alert(AlertType.INFORMATION);
    		alert.setTitle("Missing Value");
    		alert.setHeaderText("Please pick a cell and letter value before submitting.");
    		alert.showAndWait();
            return;
        }

        int cell = cellDropdown.getValue()-1;
        char letter = letterDropdown.getValue();

        int rflag = game.updateState(cell, letter);
        if (rflag == -2) {
            Alert alert = new Alert(AlertType.INFORMATION);
    		alert.setTitle("Invalid Cell");
    		alert.setHeaderText("This cell is already filled. Please pick a different one.");
    		alert.showAndWait();
        } else if (rflag == 1) {
            updateInfo();
            gameActive = false;
            declareOutcome(true);
        } else if (rflag == -1) {
            updateInfo();
            revealWord();
            gameActive = false;
            declareOutcome(false);
        } else if (rflag == 0) {
            updateInfo();
        }
    }

    @FXML public void dictionaryMenuItmHandler(ActionEvent event) {
    	try {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("hangman/dictionaryPopUp.fxml"));
            Parent root = (Parent) loader.load();
			Stage stage = new Stage();
	        stage.setTitle("MediaLab Hangman");
			stage.setScene(new Scene(root));
		    stage.initModality(Modality.APPLICATION_MODAL);
		    DictionaryPopupController controller = loader.getController();
            controller.initialize(dict);
			stage.show();
		} catch(Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Undefined Dictionary");
    		alert.setHeaderText("Please load a dictionary first.");
    		alert.showAndWait();
		}
    }

    @FXML public void solutionMenuItmHandler(ActionEvent event) {
        if (!gameActive) {
            Alert alert = new Alert(AlertType.INFORMATION);
    		alert.setTitle("Inactive Game");
    		alert.setHeaderText("Please start a new game by pressing Application > Start.");
    		alert.showAndWait();
            return;
        }

        gameActive = false;
        declareOutcome(false);
        hpText.setText("0");
        revealWord();
    }

    void revealWord() {
        String word = game.getTargetWord();
        char[] out = new char[2*word.length()-1];
        for (int i = 0; i < word.length()-1; i++) {
            out[2*i] = word.charAt(i); 
            out[2*i+1] = ' ';
        }
        out[2*word.length()-2] = word.charAt(word.length()-1);
        secretWord.setText(new String(out));
    }

    void declareOutcome(boolean win) {
        if (win) {
            gameOutcomeText.setFill(Color.web("#43d61c"));
            gameOutcomeText.setText("YOU WON!");
            gameOutcomeText.setVisible(true);
        } else {
            gameOutcomeText.setFill(Color.web("#d71a1a"));
            gameOutcomeText.setText("YOU LOST!");
            gameOutcomeText.setVisible(true);
        }

        try(
            FileWriter fw = new FileWriter("./resources/medialab/game_history.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw)
        ) {
            out.print(game.getTargetWord() + " ");
            out.print(guesses + " ");
            out.print(game.getPoints() + " ");
            if (win) out.println("VICTORY");
            else out.println("DEFEAT");
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Rounds Error");
            alert.setHeaderText("Error when saving round stats to game_history.txt.");
            alert.showAndWait();
        }
    }

    @FXML public void roundsMenuItmHandler(ActionEvent event) {
        try {
    		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("hangman/roundsPopUp.fxml"));
            Parent root = (Parent) loader.load();
			Stage stage = new Stage();
	        stage.setTitle("MediaLab Hangman");
			stage.setScene(new Scene(root));
		    stage.initModality(Modality.APPLICATION_MODAL);
		    stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
    }
}