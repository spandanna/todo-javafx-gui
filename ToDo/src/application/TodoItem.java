package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TodoItem {

	private String name;
	private LocalDate dueDate;
	private int completed;
	
	public TodoItem(String name, LocalDate dueDate, int completed) {
		this.name = name;
		this.dueDate = dueDate;
		this.completed = completed;
		
	}
    public static TodoItem fromResultSet(@SuppressWarnings("exports") ResultSet resultSet) throws SQLException {
    	Integer completed = resultSet.getInt(Controller.COLUMN_COMPLETED);
    	
    	String dueDate = resultSet.getString(Controller.COLUMN_DUE_DATE);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        formatter = formatter.withLocale(Locale.UK);
        LocalDate date = LocalDate.parse(dueDate, formatter);
        
        String name = resultSet.getString(Controller.COLUMN_NAME);

        return new TodoItem(name, date, completed);
    }
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public LocalDate getDueDate() {
		return dueDate;
	}
	
	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}
	
	public int getCompleted() {
		return completed;
	}
	
	public void setCompleted(int completed) {
		this.completed = completed;
		updateCompleted();
	}

	public void addToDB() {
    	String sql = "INSERT INTO " + Controller.TABLE_TODO + " (" + Controller.COLUMN_NAME + ", "
    + Controller.COLUMN_COMPLETED + ", " + Controller.COLUMN_DUE_DATE  + ") VALUES (?, ?, ?)";
        try {
            PreparedStatement statement = Controller.connection.prepareStatement(sql);
            statement.setString(1, this.getName());
            statement.setInt(2, this.getCompleted());
            statement.setObject(3, this.getDueDate());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	public void deleteFromDB() {
		String sql = "DELETE FROM " + Controller.TABLE_TODO + " WHERE " + Controller.COLUMN_NAME + " = ?";
	    try {
	        PreparedStatement statement = Controller.connection.prepareStatement(sql);
	        statement.setString(1, this.getName());
	        statement.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public void updateCompleted() {
		String sql = "UPDATE " + Controller.TABLE_TODO + " SET " + Controller.COLUMN_COMPLETED + " = ? WHERE " + Controller.COLUMN_NAME + " = ?";
		try {
		        PreparedStatement statement = Controller.connection.prepareStatement(sql);
		        statement.setInt(1, this.getCompleted());
		        statement.setString(2, this.getName());
		        statement.executeUpdate();
		    } catch (SQLException e) {
		        e.printStackTrace();
	}
}}
