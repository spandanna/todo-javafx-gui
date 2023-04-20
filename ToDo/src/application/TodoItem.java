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
	
	public TodoItem(String id, String name, LocalDate doDate, Integer completed) {
		this.id = id;
		this.name = name;
		this.doDate = doDate;
		this.completed = completed;
		
	}

	public static TodoItem fromInput(String name, LocalDate doDate) {
		return new TodoItem(UUID.randomUUID().toString(), name, doDate, 0);
	}
	
    public static TodoItem fromResultSet(@SuppressWarnings("exports") ResultSet resultSet) throws SQLException {
    	Integer completed = resultSet.getInt(Controller.COLUMN_COMPLETED);
    	String id = resultSet.getString(Controller.COLUMN_ID);
    	String doDate = resultSet.getString(Controller.COLUMN_DO_DATE);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        formatter = formatter.withLocale(Locale.UK);
        LocalDate date = LocalDate.parse(doDate, formatter);
        
        String name = resultSet.getString(Controller.COLUMN_NAME);

        return new TodoItem(id, name, date, completed);
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

	public void addToDB() {
    	String sql = "INSERT INTO " + Controller.TABLE_TODO + 
    			" (" + Controller.COLUMN_ID + ", "
    			+ Controller.COLUMN_NAME + ", "
    			+ Controller.COLUMN_COMPLETED + ", "
    			+ Controller.COLUMN_DO_DATE  + ") VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement statement = Controller.connection.prepareStatement(sql);
            statement.setString(1, this.getId());
            statement.setString(2, this.getName());
            statement.setInt(3, this.getCompleted());
            statement.setObject(4, this.getDoDate());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	public void deleteFromDB() {
		String sql = "DELETE FROM " + Controller.TABLE_TODO + " WHERE " + Controller.COLUMN_ID + " = ?";
	    try {
	        PreparedStatement statement = Controller.connection.prepareStatement(sql);
	        statement.setString(1, this.getId());
	        statement.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public void updateCompleted() {
		String sql = "UPDATE " + Controller.TABLE_TODO + " SET " + Controller.COLUMN_COMPLETED + " = ? WHERE " + Controller.COLUMN_ID + " = ?";
		try {
		        PreparedStatement statement = Controller.connection.prepareStatement(sql);
		        statement.setInt(1, this.getCompleted());
		        statement.setString(2, this.getId());
		        statement.executeUpdate();
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
	}
	
	public void updateName() {
		String sql = "UPDATE " + Controller.TABLE_TODO + " SET " + Controller.COLUMN_NAME + " = ? WHERE " + Controller.COLUMN_ID + " = ?";
		try {
		        PreparedStatement statement = Controller.connection.prepareStatement(sql);
		        statement.setString(1, this.getName());
		        statement.setString(2, this.getId());
		        statement.executeUpdate();
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
	}
}
