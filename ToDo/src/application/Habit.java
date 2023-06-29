package application;

public class Habit {

	public class Recurrence {

	}
	
	private String name;
	private String description;
	private Recurrence recurrence;

	public Habit(String name, String description, Recurrence recurrence) {
		this.name = name;
		this.description = description;
		this.recurrence = recurrence;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Recurrence getRecurrence() {
		return recurrence;
	}

}
