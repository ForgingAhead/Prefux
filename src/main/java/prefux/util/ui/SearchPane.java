package prefux.util.ui;

import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import org.controlsfx.control.textfield.TextFields;

/**
 * JavaFX component that enables keyword search over prefuse data tuples.
 */
public class SearchPane extends GridPane {


    ColumnConstraints matchResultsLabelColumnConstraints;
    ColumnConstraints searchLabelColumnConstraints;
    ColumnConstraints textFieldColumnConstraints;
    RowConstraints rowConstraints;

    Label matchResultsLabel;
    Label searchLabel;
    TextField searchTextField;


    public SearchPane() {
        super();

        this.matchResultsLabel = new Label("          ");
        this.searchLabel = new Label("search >> ");
        this.searchTextField = TextFields.createClearableTextField();

        this.matchResultsLabelColumnConstraints = new ColumnConstraints();
        this.matchResultsLabelColumnConstraints.setHalignment(HPos.LEFT);
        this.matchResultsLabelColumnConstraints.setHgrow(Priority.NEVER);

        this.searchLabelColumnConstraints = new ColumnConstraints();
        this.searchLabelColumnConstraints.setHalignment(HPos.RIGHT);
        this.searchLabelColumnConstraints.setHgrow(Priority.NEVER);

        this.textFieldColumnConstraints = new ColumnConstraints();
        this.textFieldColumnConstraints.setHalignment(HPos.LEFT);
        this.textFieldColumnConstraints.setHgrow(Priority.NEVER);

        this.rowConstraints = new RowConstraints();
        this.rowConstraints.setMinHeight(10.0);
        this.rowConstraints.setPrefHeight(30.0);
        this.rowConstraints.setVgrow(Priority.NEVER);

        getColumnConstraints().add(this.matchResultsLabelColumnConstraints);
        getColumnConstraints().add(this.searchLabelColumnConstraints);
        getColumnConstraints().add(this.textFieldColumnConstraints);

        getRowConstraints().add(this.rowConstraints);


        add(this.matchResultsLabel, 0, 0);
        add(this.searchLabel, 1, 0);
        add(this.searchTextField, 2, 0);

    }
}
