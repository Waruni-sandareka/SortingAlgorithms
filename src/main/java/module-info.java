module org.uoc.group.groupassignment {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.uoc.group.groupassignment to javafx.fxml;
    exports org.uoc.group.groupassignment;
}