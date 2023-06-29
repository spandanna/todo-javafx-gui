package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class HabitController {
	
    @FXML
    private Button saveHabitButton;
    @FXML
    private Button deleteHabitButton;

    @FXML
    private TextField habitNameTextField;

    @FXML
    private TextArea habitDescriptionTextArea;

    @FXML
    private ComboBox<Habit.Recurrence> habitRecurrenceComboBox;

    @FXML
    private ListView<Habit> habitListView;

    private ObservableList<Habit> habits = FXCollections.observableArrayList();

    public void initialize() {
        habitListView.setItems(habits);
        habitListView.setCellFactory(new Callback<ListView<Habit>, ListCell<Habit>>() {
            public ListCell<Habit> call(ListView<Habit> param) {
                return new ListCell<Habit>() {
                    @Override
                    protected void updateItem(Habit habit, boolean empty) {
                        super.updateItem(habit, empty);
                        if (empty || habit == null) {
                            setText(null);
                        } else {
                            setText(habit.getName() + " (" + habit.getRecurrence() + ")");
                        }
                    }
                };
            }
        });
    }
	public void saveHabit() {
	    String name = habitNameTextField.getText();
	    String description = habitDescriptionTextArea.getText();
	    Habit.Recurrence recurrence = habitRecurrenceComboBox.getValue();
	    Habit habit = new Habit(name, description, recurrence);
	    habits.add(habit);
//	    habitListView.getItems().add(habit);
//	    closeHabitDialog();
	}

	@FXML
	private void deleteHabit() {
	    Habit selectedHabit = habitListView.getSelectionModel().getSelectedItem();
	    if (selectedHabit != null) {
	    	habits.remove(selectedHabit);
	        habitListView.getSelectionModel().clearSelection();
	    }
	}

}
