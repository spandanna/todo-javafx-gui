package application;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.input.KeyCode;


public class Controller {

    @FXML
    private VBox vbox;

    @FXML
    private TextField newToDoTextField;

    @FXML
    private Button addButton;

    @FXML
    private Label dateLabel;

    private int index = 0;

    public void initialize() {
        dateLabel.setText("To Do - " + java.time.LocalDate.now());
    

        // Add listener to the newToDoTextField to detect when the Enter key is pressed
    	newToDoTextField.setOnKeyPressed(event -> {
        if (event.getCode() == KeyCode.ENTER) {
            addToDoItem();
        }
    });
}
    public void addToDoItem() {
        String toDoText = newToDoTextField.getText();
        if (toDoText != null && !toDoText.isEmpty()) {
            CheckBox newCheckBox = new CheckBox();
            newCheckBox.setText(toDoText);
            vbox.getChildren().add(index, newCheckBox);
            index++;
            newToDoTextField.clear();
        }
    }

}

