//import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.function.*;
import java.util.Map;

/**
 * TODO: This comment
 */
public class Main extends GUI {

    // Collections of the roads, nodes, and segments
    private Map<Integer, Road> roadMap;
    private Map<Integer, Node> nodeMap;
    private List<Segment> segmentList;

    // The Origin location
    private Location origin = Location.newFromLatLon(-36.847622, 174.763444 );

    // The scale
    private double scale = 150;

    /**
     * Constructor
     *
     * Just runs initialise (in the GUI class).
     */
    public Main() {
        // TODO: This?
        super();
    }

    /**
     * Is called when the user has successfully selected a directory to load the
     * data files from. File objects representing the four files of interested
     * are passed to the method. The fourth File, polygons, might be null if it
     * isn't present in the directory.
     *
     * @param nodes
     *            a File for nodeID-lat-lon.tab
     * @param roads
     *            a File for roadID-roadInfo.tab
     * @param segments
     *            a File for roadSeg-roadID-length-nodeID-nodeID-coords.tab
     * @param polygons
     *            a File for polygon-shapes.mp
     */
    protected void onLoad(File nodes, File roads, File segments, File polygons) {

        // Streams the lines of the file into Nodes, which are put into a map (node
        nodeMap = getStream(nodes).map(array ->
                new Node(Integer.parseInt(array[0]), Double.parseDouble(array[1]), Double.parseDouble(array[2])))
                .collect(Collectors.toMap(node->node.getNodeID(), Function.identity())); //TODO: Use .collect to put into a collection

        // Stream the roads file
        roadMap = getStream(roads).skip(1).map(array ->
                new Road(Integer.parseInt(array[0]), Integer.parseInt(array[1]), array[2], array[3], array[4].equals("1"), Integer.parseInt(array[5]), array[6].equals("1"), array[7].equals("1"), array[8].equals("1")))
                .collect(Collectors.toMap(road->road.getRoadID(), Function.identity()));

        // Stream the segments file
        // The Arrays.copyOfRange() splits the array so that it's made up of anything from the beginning of the lat/long onwards.
        segmentList = getStream(segments).skip(1).map(array ->
                new Segment(roadMap.get(Integer.parseInt(array[0])), Double.parseDouble(array[1]), nodeMap.get(Integer.parseInt(array[2])), nodeMap.get(Integer.parseInt(array[3])), Arrays.copyOfRange(array, 4, array.length)))
                .collect(Collectors.toList());

        // Add a reference for each segment for the road and nodes that it is connected to.
        for(Segment s : segmentList) {
            s.getRoad().getSegmentList().add(s);
            s.getNode1().getSegmentList().add(s);
            s.getNode2().getSegmentList().add(s);
        }

        //TODO: Polygons!
    }

    /**
     * Opens the specified file, and returns a stream of its lines.
     *
     * @param f - The file object which is being opened
     * @return A stream of the lines in the file, split using "\t" as the delimiter
     */
    private Stream<String[]> getStream(File f) {
        // https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
        try { // Read the file (doing it this way makes it auto close when the function returns)
            BufferedReader reader = new BufferedReader(new FileReader(f));
            return reader.lines().map(line -> line.split("\t")); // Currently a stream of the split lines.

        } catch(FileNotFoundException e) { // It should (hopefully) never fail
            System.err.println("File Not Found: " + e.getMessage());
            getTextOutputArea().setText("File Not Found: " + e.getMessage());
        }
        return null;
    }

    /**
     * Draws the map.
     *
     * @param g - The graphics object
     */
    protected void redraw(Graphics g) { // TODO: Only draw onscreen things.
        if(segmentList == null) {return;}

        // Draw the segments
        segmentList.forEach(segment -> segment.draw(g, origin, scale));

        // Draw the nodes
        nodeMap.keySet().stream().map(key -> nodeMap.get(key)).forEach(node -> node.draw(g, origin, scale));
    }

    protected void onClick(MouseEvent m) {
        getTextOutputArea().setText("Click!");
    }

    protected void onSearch() {
        getTextOutputArea().setText("Search!");
    }

    protected void onMove(Move m) {
        getTextOutputArea().setText("Move!");
    }

    public static void main(String[] args) {new Main();}
}