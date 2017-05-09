/*
 * Copyright (c) 2017 Mercuria Energy America, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of
 *  Mercuria Energy America, Inc ("Confidential Information").  You
 *  shall not disclose such Confidential Information and shall use
 *  it only in accordance with the terms of the license agreement you
 *  entered into with Mercuria Energy America, Inc.
 *
 *  JUNGAndJavaFX
 *  jchildress
 */
package fx;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.graph.util.TestGraphs;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationModel;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * A sample showing how to use JUNG's layout classes to position vertices
 * in a graph.
 *
 * @author jeffreyguenther
 * @author timheng
 */
public class JUNGAndJavaFX extends Application {

    private static final int CIRCLE_SIZE = 15; // default circle size

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // setup up the scene.
        Group root = new Group();
        Scene scene = new Scene(root, 800, 400, Color.WHITE);

        // create two groups, one for each visualization
        Group viz1 = new Group();
        Group viz2 = new Group();

        // create a sample graph using JUNG's TestGraphs class.
        Graph<String, Number> graph1 = TestGraphs.getOneComponentGraph();

        // define the layout we want to use for the graph
        // The layout will be modified by the VisualizationModel
        Layout<String, Number> circleLayout = new CircleLayout<>(graph1);

        /*
         * Define the visualization model. This is how JUNG calculates the layout
         * for the graph. It updates the layout object passed in.
         */
        VisualizationModel<String, Number> vm1 = new DefaultVisualizationModel<>(circleLayout, new Dimension(400, 400));

        // draw the graph
        renderGraph(graph1, circleLayout, viz1);


        // Generate a second JUNG sample graph
        Graph<String, Number> graph2 = TestGraphs.getOneComponentGraph();

        // This time use an Isometric layout.
        Layout<String, Number> lay2 = new ISOMLayout<>(graph2);

        // Generate the actual layout
        VisualizationModel<String, Number> vm2 = new DefaultVisualizationModel<>(lay2, new Dimension(400, 400));

        // draw the graph
        renderGraph(graph2, lay2, viz2);

        // move the second viz to beside the first.
        viz2.translateXProperty().set(400);

        root.getChildren().add(viz1);
        root.getChildren().add(viz2);

        stage.setTitle("Displaying Two JUNG Graphs");
        stage.setScene(scene);
        stage.show();

    }

    /**
     * Render a graph to a particular <code>Group</code>
     *
     * @param graph
     * @param layout
     * @param viz
     */
    private void renderGraph(Graph<String, Number> graph, Layout<String, Number> layout, Group viz) {
        // draw the vertices in the graph
        for (String v : graph.getVertices()) {
            // Get the position of the vertex
            Point2D p = layout.apply(v);

            // draw the vertex as a circle
            // add it to the group, so it is shown on screen
            viz.getChildren().add(new Circle(p.getX(), p.getY(), CIRCLE_SIZE));
        }

        // draw the edges
        for (Number n : graph.getEdges()) {
            // get the end points of the edge
            Pair<String> endpoints = graph.getEndpoints(n);

            // Get the end points as Point2D objects so we can use them in the
            // builder
            Point2D pStart = layout.apply(endpoints.getFirst());
            Point2D pEnd = layout.apply(endpoints.getSecond());

            // Draw the line
            // add the edges to the screen
            viz.getChildren().add(new Line(pStart.getX(), pStart.getY(), pEnd.getX(), pEnd.getY()));
        }
    }
}
