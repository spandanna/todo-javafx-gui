package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

public class TodoItem {

	private String id;
	private String name;
	private LocalDate doDate;
	private Integer completed;
	private String type;
	
	public TodoItem(String id, String name, LocalDate doDate, Integer completed, String type) {
		this.id = id;
		this.name = name;
		this.doDate = doDate;
		this.completed = completed;
		this.type = type;
	}

	public static TodoItem fromInput(String name, LocalDate doDate) {
		return new TodoItem(UUID.randomUUID().toString(), name, doDate, 0, "task");
	}
	
    public static TodoItem fromResultSet(ResultSet resultSet) throws SQLException {
    	Integer completed = resultSet.getInt(MainController.COLUMN_COMPLETED);
    	String id = resultSet.getString(MainController.COLUMN_ID);
    	String doDate = resultSet.getString(MainController.COLUMN_DO_DATE);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        formatter = formatter.withLocale(Locale.UK);
        LocalDate date = LocalDate.parse(doDate, formatter);
        
        String name = resultSet.getString(MainController.COLUMN_NAME);
        String type = resultSet.getString(MainController.COLUMN_TYPE);
        return new TodoItem(id, name, date, completed, type);
    }
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public LocalDate getDoDate() {
		return doDate;
	}
	
	public void setDueDate(LocalDate doDate) {
		this.doDate = doDate;
	}
	
	public int getCompleted() {
		return completed;
	}
	
	public void setCompleted(int completed) {
		this.completed = completed;
		updateCompleted();
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public void addToDB() {
    	String sql = "INSERT INTO " + MainController.TABLE_TODO + 
    			" (" + MainController.COLUMN_ID + ", "
    			+ MainController.COLUMN_NAME + ", "
    			+ MainController.COLUMN_COMPLETED + ", "
    			+ MainController.COLUMN_TYPE + ", "
    			+ MainController.COLUMN_DO_DATE  + ") VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement statement = MainController.connection.prepareStatement(sql);
            statement.setString(1, this.getId());
            statement.setString(2, this.getName());
            statement.setInt(3, this.getCompleted());
            statement.setString(4,  this.getType());
            statement.setObject(5, this.getDoDate());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	public void deleteFromDB() {
		String sql = "DELETE FROM " + MainController.TABLE_TODO + " WHERE " + MainController.COLUMN_ID + " = ?";
	    try {
	        PreparedStatement statement = MainController.connection.prepareStatement(sql);
	        statement.setString(1, this.getId());
	        statement.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public void updateCompleted() {
		String sql = "UPDATE " + MainController.TABLE_TODO + " SET " + MainController.COLUMN_COMPLETED + " = ? WHERE " + MainController.COLUMN_ID + " = ?";
		try {
		        PreparedStatement statement = MainController.connection.prepareStatement(sql);
		        statement.setInt(1, this.getCompleted());
		        statement.setString(2, this.getId());
		        statement.executeUpdate();
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
	}
	
	public void updateName() {
		String sql = "UPDATE " + MainController.TABLE_TODO + " SET " + MainController.COLUMN_NAME + " = ? WHERE " + MainController.COLUMN_ID + " = ?";
		try {
		        PreparedStatement statement = MainController.connection.prepareStatement(sql);
		        statement.setString(1, this.getName());
		        statement.setString(2, this.getId());
		        statement.executeUpdate();
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
	}
}
