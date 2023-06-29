package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;


public class MainController {

	static Connection connection;
    @FXML
    private Label title;
    
    @FXML
    private VBox todolist;

    @FXML
    private Button addButton;

    @FXML
    private DatePicker newDoDatePicker;
    
    @FXML
    private DatePicker datePicker;
    
    @FXML
    private TextField newToDoTextField;
    


    private static final String DB_NAME = "database.db";
    private static final String CONNECTION_STRING = "jdbc:sqlite:" + "src/application/" + DB_NAME;
    static final String TABLE_TODO = "todos";
    static final String COLUMN_ID = "id";
    static final String COLUMN_NAME = "name";
    static final String COLUMN_DO_DATE = "do_date";
    static final String COLUMN_COMPLETED = "completed";
    static final String COLUMN_TYPE = "type";
    public LocalDate currentDate = LocalDate.now();
    
    public void initialize() {
        setTitle();
        connectToDatabase();
		setNewTextField();
		datePicker.setValue(currentDate);
		loadDueItems(currentDate);
		datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
			currentDate = newValue;
			setTitle();
			removeItemsFromScreen();
			loadDueItems(newValue);
		});
    }
    
    public void openHabitWindow(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Habit.fxml"));
        Parent habitRoot = loader.load();
        Stage habitStage = new Stage();
        habitStage.initModality(Modality.APPLICATION_MODAL);
        habitStage.setScene(new Scene(habitRoot));
        habitStage.showAndWait();
    }
    
    private void setNewTextField() {
        newToDoTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addToDoItem();
            }
        });
    }
    
    private void setTitle() {
    	title.setText("To Do " + currentDate.toString());
    }
    
    private void createNewHBox(CheckBox checkBox, Button deleteButton) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setSpacing(5);
        hbox.getChildren().add(checkBox);    
        hbox.getChildren().add(deleteButton);
        todolist.getChildren().add(hbox);
    }
    
	public void addToDoItem() {
	    TodoItem newItem = TodoItem.fromInput(newToDoTextField.getText(), newDoDatePicker.getValue());
	    
	    if (newItem.getName() != null && newItem.getDoDate() != null) {
	    	CheckBox newCheckBox = createCheckBox(newItem, 0);
	    	Button deleteButton = createDeleteButton(todolist, newItem, newCheckBox);
	        createNewHBox(newCheckBox, deleteButton);
	    	newItem.addToDB();
	    	removeItemsFromScreen();
	    	loadDueItems(currentDate);
	        newToDoTextField.clear();
	        newDoDatePicker.setValue(null);
	    }
	}

	public void editToDoItem(CheckBox checkBox, TodoItem newItem) {
		String oldText = newItem.getName();
	    TextInputDialog dialog = new TextInputDialog(oldText);
	    dialog.setTitle("Edit To Do Item");
	    dialog.setHeaderText(null);
	    dialog.setContentText("Enter new text:");
	    Optional<String> result = dialog.showAndWait();
	    result.ifPresent(newText -> {
	    		newItem.setName(newText);
	    		checkBox.setText(newText);
	    		newItem.updateName();
	    		});
	    
	}
	
 
	private CheckBox createCheckBox(TodoItem newItem, int completed) {
	    CheckBox newCheckBox = new CheckBox();
	    newCheckBox.setText(newItem.getName());
	    newCheckBox.setSelected(completed == 1 ? true : false);
	    newCheckBox.setOnAction(event -> {
	        newItem.setCompleted(newCheckBox.isSelected() ? 1 : 0);
	    });
	    newCheckBox.setOnMouseClicked(event -> {
	        if (event.getClickCount() == 2) {
	            editToDoItem(newCheckBox, newItem);
	        }
	    });
	    return newCheckBox;
	}

	
	private Button createDeleteButton(VBox todolist, TodoItem item, CheckBox checkBox) {
	    Button deleteButton = new Button();
	    deleteButton.getStyleClass().add("delete-button");
	    deleteButton.setOnAction(event -> {
	        Node hbox = deleteButton.getParent();
	        todolist.getChildren().remove(hbox);
	        item.deleteFromDB();
	    });
	    return deleteButton;
	}

	private void removeItemsFromScreen() {
	    ObservableList<Node> items = todolist.getChildren();
	    List<Node> itemsToRemove = new ArrayList<>();

	    for (Node item : items) {
	        if (item instanceof HBox) {
	            HBox hbox = (HBox) item;
	            if (hbox.getChildren().stream().anyMatch(node -> node instanceof CheckBox)) {
	                itemsToRemove.add(item);
	            }
	        }
	    }

	    todolist.getChildren().removeAll(itemsToRemove);
	}
	
	private void loadDueItems(LocalDate doDate) {
		String sql = "SELECT * FROM " + TABLE_TODO + " WHERE " + COLUMN_DO_DATE + " = ?";
        try {
        	PreparedStatement statement = connection.prepareStatement(sql);
			statement.setObject(1, doDate);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				TodoItem newItem = TodoItem.fromResultSet(resultSet);
	            CheckBox newCheckBox = createCheckBox(newItem, newItem.getCompleted());
		        Button deleteButton = createDeleteButton(todolist, newItem, newCheckBox);
	            HBox hbox = new HBox(newCheckBox, deleteButton);
	            todolist.getChildren().add(hbox); 
	            
	           
	            newToDoTextField.clear();
			}
            
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
	}

    
    private void connectToDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(CONNECTION_STRING);
            Statement statement = connection.createStatement();
            statement.executeUpdate(
            		"CREATE TABLE IF NOT EXISTS " + TABLE_TODO +
            		" (" + COLUMN_ID + " STRING PRIMARY KEY, " + COLUMN_NAME + " TEXT, "
            		+ COLUMN_DO_DATE + " DATE, " + COLUMN_TYPE + " STRING, "+ COLUMN_COMPLETED + " BOOLEAN)"
            		);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    
}

