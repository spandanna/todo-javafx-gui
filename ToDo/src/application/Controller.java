package application;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.input.KeyCode;


public class Controller {

	private static Connection connection;
	
    @FXML
    private VBox vbox;

    @FXML
    private TextField newToDoTextField;

    @FXML
    private Button addButton;

    @FXML
    private Label title;
    
    @FXML
    private DatePicker newDueDatePicker;

    private static final String DB_NAME = "database.db";
    private static final String CONNECTION_STRING = "jdbc:sqlite:" + "src/application/" + DB_NAME;
    private static final String TABLE_TODO = "todos";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DUE_DATE = "due_date";
    private static final String COLUMN_COMPLETED = "completed";
    private int index = 0;

    public void initialize() {
        title.setText("To Do - " + java.time.LocalDate.now());
        connectToDatabase();
        loadFromDatabase();
        
        // Add listener to the newToDoTextField to detect when the Enter key is pressed
        newToDoTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    addToDoItem();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    
        // Add listener to each checkbox to update the 'completed' column in the database
        for (Node node : vbox.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) node;
                checkBox.setOnAction(event -> {
                    try {
                    	updateCompleted(checkBox.getText(), checkBox.isSelected());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
    	

	private void updateCompleted(String toDoText, boolean completed) throws ClassNotFoundException {
	    String sql = "UPDATE " + TABLE_TODO + " SET " + COLUMN_COMPLETED + " = ? WHERE " + COLUMN_NAME + " = ?";
	    try {
	        PreparedStatement statement = connection.prepareStatement(sql);
	        statement.setInt(1, completed ? 1 : 0);
	        statement.setString(2, toDoText);
	        
	        statement.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	

	public void addToDoItem() throws ClassNotFoundException {
	    String toDoText = newToDoTextField.getText();
	    LocalDate dueDate = newDueDatePicker.getValue();
	    
	    if (toDoText != null && !toDoText.isEmpty()) {
	    	
	        CheckBox newCheckBox = new CheckBox();
	        HBox hbox = new HBox();
	        hbox.setAlignment(Pos.CENTER_LEFT);
	        hbox.setSpacing(5);
	        newCheckBox.setText(toDoText);
	        hbox.getChildren().add(newCheckBox);	
	        Button deleteButton = new Button("x");
	        deleteButton.getStyleClass().add("delete-button");
	        deleteButton.setOnAction(event -> {
	            vbox.getChildren().remove(hbox);
	            try {
	                deleteToDoItemFromDB(toDoText);
	            } catch (ClassNotFoundException e) {
	                e.printStackTrace();
	            }
	        });
	        hbox.getChildren().add(deleteButton);
	        vbox.getChildren().add(index, hbox);
	        addToDoItemToDB(toDoText, dueDate);
	        
	        index++;
	   
	        newToDoTextField.clear();
	        newDueDatePicker.setValue(null);
	    }
	}

	private void deleteToDoItemFromDB(String toDoText) throws ClassNotFoundException {
	    String sql = "DELETE FROM " + TABLE_TODO + " WHERE " + COLUMN_NAME + " = ?";
	    try {
	        PreparedStatement statement = connection.prepareStatement(sql);
	        statement.setString(1, toDoText);
	        statement.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


    private void addToDoItemToDB(String newToDoText, LocalDate dueDate) throws ClassNotFoundException {
    	String sql = "INSERT INTO " + TABLE_TODO + " (" + COLUMN_NAME + ", "
    + COLUMN_COMPLETED + ", " + COLUMN_DUE_DATE  + ") VALUES (?, ?, ?)";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, newToDoText);
            statement.setInt(2, 0);
            statement.setObject(3, dueDate);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    private void loadFromDatabase() {
        String sql = "SELECT * FROM " + TABLE_TODO;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String name = resultSet.getString(COLUMN_NAME);
                Boolean completed = resultSet.getBoolean(COLUMN_COMPLETED);
                String due_date = resultSet.getString(COLUMN_DUE_DATE);
                CheckBox newCheckBox = new CheckBox();
                newCheckBox.setText(name);
                newCheckBox.setSelected(completed);
                // TODO add due date to item
                
                Button deleteButton = new Button("x");
                deleteButton.getStyleClass().add("delete-button");
                deleteButton.setOnAction(e -> {
                    vbox.getChildren().remove(newCheckBox);
                    try {
                        PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM " + TABLE_TODO + " WHERE " + COLUMN_NAME + " = ?");
                        deleteStatement.setString(1, name);
                        deleteStatement.executeUpdate();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });
                
                HBox hbox = new HBox(newCheckBox, deleteButton);
                hbox.setSpacing(10);
                
                vbox.getChildren().add(index, hbox); 
                index++;
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
            		" (" + COLUMN_ID + " INTEGER PRIMARY KEY, " + COLUMN_NAME + " TEXT, "
            		+ COLUMN_DUE_DATE + " DATE, " + COLUMN_COMPLETED + " BOOLEAN)"
            		);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    
}

