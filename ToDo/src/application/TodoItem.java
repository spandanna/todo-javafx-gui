package application;

import java.time.LocalDate;

public class TodoItem {

	private String name;
	private LocalDate dueDate;
	private boolean completed;
	
	public TodoItem(String name, LocalDate dueDate, boolean completed) {
		this.name = name;
		this.dueDate = dueDate;
		this.completed = completed;
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
	
	public boolean getCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
}
