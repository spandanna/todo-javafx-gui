module ToDo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
	requires org.xerial.sqlitejdbc;
	requires javafx.graphics;
	requires javafx.base;

    opens application to javafx.fxml;
    exports application;
}
