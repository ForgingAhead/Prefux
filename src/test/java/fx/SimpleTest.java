package fx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import prefux.Constants;
import prefux.FxDisplay;
import prefux.Visualization;
import prefux.action.ActionList;
import prefux.action.RepaintAction;
import prefux.action.assignment.ColorAction;
import prefux.action.assignment.DataColorAction;
import prefux.action.assignment.NodeDegreeSizeAction;
import prefux.action.layout.graph.ForceDirectedLayout;
import prefux.activity.Activity;
import prefux.controls.DragControl;
import prefux.data.Graph;
import prefux.data.io.GraphMLReader;
import prefux.render.DefaultRendererFactory;
import prefux.render.LabelRenderer;
import prefux.render.ShapeRenderer;
import prefux.util.ColorLib;
import prefux.util.PrefuseLib;
import prefux.visual.VisualItem;

public class SimpleTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        Graph graph = null;
        graph = new GraphMLReader().readGraph("socialnet.xml");

        Visualization vis = new Visualization();
        vis.add("graph", graph);

        LabelRenderer r = new LabelRenderer("name");
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setFillMode(ShapeRenderer.SOLID);

        vis.setRendererFactory(new DefaultRendererFactory(shapeRenderer));

        ActionList layout = new ActionList(Activity.INFINITY);
        layout.add(new ForceDirectedLayout("graph"));
        layout.add(new RepaintAction());
        vis.putAction("layout", layout);

        // create our nominal color palette
        // pink for females, baby blue for males
        int[] palette = new int[]{
                ColorLib.rgb(255, 180, 180), ColorLib.rgb(190, 190, 255)
        };

        // map nominal data values to colors using our provided palette
        DataColorAction fill = new DataColorAction("graph.nodes", "gender", Constants.NOMINAL, VisualItem.FILLCOLOR, palette);

        // use black for node text
        ColorAction text = new ColorAction("graph.nodes", VisualItem.TEXTCOLOR, ColorLib.gray(0));

        // use light grey for edges
        ColorAction edges = new ColorAction("graph.edges", VisualItem.STROKECOLOR, ColorLib.gray(200));

        // create an action list containing all color assignments
        ActionList color = new ActionList();
        color.add(fill);
        color.add(text);
        color.add(edges);
        ActionList nodeActions = new ActionList();
        final String NODES = PrefuseLib.getGroupName("graph", Graph.NODES);
        // DataSizeAction size = new DataSizeAction(NODES, "age");
        // nodeActions.add(size);
        NodeDegreeSizeAction size = new NodeDegreeSizeAction(NODES);
        nodeActions.add(size);
        vis.putAction("nodes", nodeActions);

        BorderPane root = new BorderPane();
        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.show();
        FxDisplay display = new FxDisplay(vis);
        vis.run("color");
        vis.run("nodes");
        vis.run("layout");
        root.setCenter(display);
        display.addControlListener(new DragControl());
        System.out.println("here");
    }
}
