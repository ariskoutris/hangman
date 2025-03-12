package hangman;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoadPopUpController{

    @FXML private Button loadBtn;
    @FXML private TextField dictID;
    private String dictionary_id;
    boolean initialized = false;
   
    @FXML protected void handleLoadBtn(ActionEvent event) {
        dictionary_id = dictID.getText();
        initialized = true;
        Stage stage = (Stage) loadBtn.getScene().getWindow();
        stage.close();
    }

    protected String getID() throws OperationCancelledException {
        if (initialized != true) {throw new OperationCancelledException("Operation Cancelled.");}
        return dictionary_id;
    }

    class OperationCancelledException extends Exception {
        public OperationCancelledException(String message) {
            super(message);
        }
    }
}