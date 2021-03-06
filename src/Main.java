//import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.*;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.function.*;
import java.util.Map;

/**
 * A mapping class, which puts a map (loaded from a file) onto the screen.
 * The map can be searched for roads, and you can select intersections to find out what roads
 * meet at that intersection.
 */
public class Main extends GUI {

    // Collections of the roads, nodes, and segments
    private Map<Integer, Road> roadMap;
    private Map<Integer, Node> nodeMap;
    private List<Segment> segmentList;
    private Trie<Road> roadTrie;

    // The Origin location
    private Location origin = Location.newFromLatLon(-36.847622, 174.763444 );

    // The scale
    private double scale = 90;
    private double zoomFactor = 1;

    // The highlighted node
    private Node highlightN;

    // The highlighted segments;
    private List<Road> highlightR;

    // The x and y of a mouse press or drag
    private int dragInitX;
    private int dragInitY;

    /**
     * Constructor
     *
     * Just runs initialise (in the GUI class).
     */
    public Main() {
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

        // Reset some variables
        origin = Location.newFromLatLon(-36.847622, 174.763444 );
        scale = 90;
        zoomFactor = 1;
        highlightN = null;
        highlightR = null;


        // Streams the lines of the file into Nodes, which are put into a map (node
        nodeMap = getStream(nodes).map(array ->
                new Node(Integer.parseInt(array[0]), Double.parseDouble(array[1]), Double.parseDouble(array[2])))
                .collect(Collectors.toMap(node->node.getNodeID(), Function.identity()));

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

        // Add roads to the Trie
        roadTrie = new Trie<Road>();
        roadMap.keySet().stream()
                .map(k->roadMap.get(k))
                .forEach(r -> roadTrie.add(r.getRoadName(), r));
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
        // Don't draw if we have no map
        if(segmentList == null) {return;}

        // Draw segments
        segmentList.forEach(segment -> segment.draw(g, origin, scale));

        // Draw the nodes
        nodeMap.keySet().stream().map(key -> nodeMap.get(key)).forEach(node -> node.draw(g, origin, scale));

        //Highlighted:
        if(highlightN != null) { // Return if there is no highlighted node
            Object[] rNames = highlightN.getSegmentList().stream().map(s -> s.getRoad()).map(r -> r.getRoadName()).distinct().toArray(); // Get an array of all the road names attached to the node
            String nodeDesc = "Intersection ID: " + highlightN.getNodeID() + "\nRoads:";
            for (Object r : rNames) { // Add road names
                nodeDesc += r + ", ";
            }
            nodeDesc = nodeDesc.substring(0, nodeDesc.length() - 2); // Shave the last ", " off the string
            getTextOutputArea().setText(nodeDesc); // Print the string to the output area
        } else if(highlightR != null) {
            String roadDesc = "";
            for(Road r : highlightR) {
                roadDesc += "Road ID: " + r.getRoadID() +
                            " | Road Name: " + r.getRoadName() +
                            " | City: " + r.getRoadCity() +
                            " | Speed Limit: " + r.getSpeedLimitText() +
                            " | Road Type: " + r.getRoadClass() +
                            " | One Way: " + ((r.getOneway()) ? "Yes" : "No ") +
                            " | " + r.getCarPedeBike() +
                            "\n";
            }
            getTextOutputArea().setText(roadDesc); // Print the string to the output area
        }
    }


    /**
     * Is called whenever a navigation button is pressed. An instance of the
     * Move enum is passed, representing the button clicked by the user.
     *
     * @param m - The movement value. Can be: NORTH, SOUTH, EAST, WEST, ZOOM_IN, ZOOM_OUT
     */
    protected void onMove(Move m) {
        // How much to change by:
        int d = 10;
        double s = 0.1;

        // The change in x/y/scale
        int dx = 0;
        int dy = 0;
        double dz = 0;

        // Work out what is being changed, and by how much.
        switch (m) {
            case NORTH:
                dy -= d;
                break;

            case SOUTH:
                dy += d;
                break;

            case EAST:
                dx += d;
                break;

            case WEST:
                dx -= d;
                break;

            case ZOOM_IN:
                dz += s;
                break;

            case ZOOM_OUT:
                dz -= s;
                break;
        }

        // Get the new location (relative to the screen)
        Point p = origin.asPoint(origin, scale);
        p.x += dx;
        p.y += dy;

        // Convert the location relative to the screen to a Location Object with a latitude and longitude.
        origin = Location.newFromPoint(p, origin, scale);

        // Change the scale.
        zoomFactor += dz;
        if(zoomFactor < 0) {zoomFactor = 0;} // Don't let it zoom out too far
        if(dz != 0) {
            scale = zoomFactor * 90;
        }
    }

    /**
     * Is called whenever the search box is updated. Use getSearchBox to get the
     * JTextField object that is the search box itself.
     */
    protected void onSearch() {
        // Unhighlight everything
        if(highlightR != null) {
            highlightR.stream().forEach(r->r.getSegmentList().stream().forEach(s->s.highlight()));
        }
        // Get the search
        String search = getSearchBox().getText();
        // Get the road
        highlightR = roadTrie.getAll(search);
        if(highlightR == null) {
            return;
        }
        if (highlightR.stream().map(r->r.getRoadName()).anyMatch(s->s.equals(search))) {
            highlightR = roadTrie.get(search);
        }
        // Highlight the new road
        highlightR.stream().forEach(r->r.getSegmentList().stream().forEach(s->s.highlight()));
        // Remove highlighted nodes
        if(highlightN != null) { highlightN.highlight(); }
        highlightN = null;
        //TODO : Print out the rest of the useful info?
    }

    /**
     * Is called when the mouse is clicked (actually, when the mouse is
     * released), and is passed the MouseEvent object for that click.
     */
    protected void onClick(MouseEvent m) {
        // Get the click point
        Point click = new Point(m.getX(), m.getY());

        // Find the closest noce
        double lowest = 1000000;
        Node closest = highlightN;
        for(int n : nodeMap.keySet()) {
            Node node = nodeMap.get(n);
            double dist = node.getDist(click, origin, scale);
            if(dist < lowest) {
                lowest = dist;
                closest = node;
            }
        }

        // highlight that node and unhighlight the old one
        closest.highlight();
        if(highlightN != null) { highlightN.highlight(); }
        highlightN = closest;
        if(highlightR != null) {
            highlightR.stream().forEach(r->r.getSegmentList().stream().forEach(s->s.highlight()));
            highlightR = null;
        }
    }

    /**
     * Allows the user to scroll using the mouse wheel.
     * @param e - The MouseWheelEvent which describes the scrolling
     */
    protected void onScroll(MouseWheelEvent e) {
        zoomFactor += e.getPreciseWheelRotation() * -0.1;
        if(zoomFactor < 0) {zoomFactor = 0;} // Don't let it zoom out too far
        scale = zoomFactor * 90;
    }

    /**
     * Allows the user to pan using a mouse drag.
     * @param e - The mouse event which describes the drag
     */
    protected void onDrag(MouseEvent e) {
        // Get the change in x and y.
        int dx = dragInitX - e.getX();
        int dy = dragInitY - e.getY();
        dragInitX = e.getX();
        dragInitY = e.getY();
        // Get the new location (relative to the screen)
        Point p = origin.asPoint(origin, scale);
        p.x += dx;
        p.y += dy;

        // Convert the location relative to the screen to a Location Object with a latitude and longitude.
        origin = Location.newFromPoint(p, origin, scale);
    }

    /**
     * Sets the initial x and y location for when the mouse is dragged.
     * @param e - The mouse event describing the press
     */
    protected void onPress(MouseEvent e) {
        dragInitX = e.getX();
        dragInitY = e.getY();
    }

    public static void main(String[] args) {new Main();}
}