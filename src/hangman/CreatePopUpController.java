package hangman;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreatePopUpController{

    @FXML private Button createBtn;
    @FXML private TextField dictID, openlibID;
    private String dictionary_id, open_library_id;
    boolean initialized = false;
   
    @FXML protected void handleCreateBtn(ActionEvent event) {
        dictionary_id = dictID.getText();
        open_library_id = openlibID.getText();
        initialized = true;
        Stage stage = (Stage) createBtn.getScene().getWindow();
        stage.close();
    }

    protected String[] getIDs() throws OperationCancelledException{
        if (initialized != true) {throw new OperationCancelledException("Operation Cancelled.");}
        String[] IDs = new String[] {dictionary_id, open_library_id};
        return IDs;
    }

    class OperationCancelledException extends Exception {
        public OperationCancelledException(String message) {
            super(message);
        }
    }
}