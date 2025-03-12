package hangman;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class RoundsPopUpController {
    
    @FXML private TableView<Entry> statsTable;
    @FXML private TableColumn<Entry, String> wordsCol;
    @FXML private TableColumn<Entry, Integer> guessesCol;
    @FXML private TableColumn<Entry, Integer> pointsCol;
    @FXML private TableColumn<Entry, String> outcomeCol;

    @FXML public void initialize() {
        try { 
            wordsCol.setCellValueFactory(new PropertyValueFactory<Entry, String>("word"));
            outcomeCol.setCellValueFactory(new PropertyValueFactory<Entry, String>("outcome"));
            pointsCol.setCellValueFactory(new PropertyValueFactory<Entry, Integer>("points"));
            guessesCol.setCellValueFactory(new PropertyValueFactory<Entry, Integer>("guesses"));
      
            List<Entry> entries = parseEntries();
            statsTable.getItems().setAll(entries);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Entry> parseEntries() throws Exception{
        List<Entry> recent_entries = new ArrayList<Entry>();
        List<Entry> all_entries = new ArrayList<Entry>();
        File f = new File("./resources/medialab/game_history.txt");
        Scanner rdr = new Scanner(f);
        while (rdr.hasNextLine()) {
            String[] data = rdr.nextLine().split(" ");
            Entry e = new Entry(data[0], Integer.valueOf(data[1]), Integer.valueOf(data[2]), data[3]);
            all_entries.add(e);
        }
        int max_i = all_entries.size()-1;
        for (int i = 0; i < Math.min(5,max_i); i++) {
            recent_entries.add(all_entries.get(max_i-i));
        }
        rdr.close();
        return recent_entries;
    }    
}
