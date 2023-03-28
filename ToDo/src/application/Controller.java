package application;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
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
    private Label dateLabel;

    private int index = 0;

    public void initialize() {
        dateLabel.setText("To Do - " + java.time.LocalDate.now());
        loadDataFromDatabase();
        
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
                        updateToDoItem(checkBox.getText(), checkBox.isSelected());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
    	

	private void updateToDoItem(String toDoText, boolean completed) throws ClassNotFoundException {
	    String sql = "UPDATE todo SET completed = ? WHERE name = ?";
	    try {
	        Connection connection = connect();
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
        if (toDoText != null && !toDoText.isEmpty()) {
            CheckBox newCheckBox = new CheckBox();
            newCheckBox.setText(toDoText);
            vbox.getChildren().add(index, newCheckBox);
            addToDoItemToDB(toDoText);
            index++;
            newToDoTextField.clear();
        }
    }

    private void addToDoItemToDB(String newToDoText) throws ClassNotFoundException {
    	String sql = "INSERT INTO todo (name, completed) VALUES (?, ?)";
        try {
        	Connection connection = connect();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, newToDoText);
            statement.setInt(2, 0);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadDataFromDatabase() {
        String sql = "SELECT * FROM todo";
        try {
        	Connection connection = connect();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                Boolean completed = resultSet.getBoolean("completed");
                CheckBox newCheckBox = new CheckBox();
                newCheckBox.setText(name);
                newCheckBox.setSelected(completed);
                vbox.getChildren().add(index, newCheckBox); 
//                index++;
//                newToDoTextField.clear();
                }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
	private static Connection connect() throws ClassNotFoundException, SQLException {
    	Class.forName("org.sqlite.JDBC"); //force Java ClassLoader to load class
	    try {
	    	connection = DriverManager.getConnection("jdbc:sqlite:src/application/database.db");
	    } catch (SQLException exception) {
	        return null;
	    }
    return connection;
}

}

