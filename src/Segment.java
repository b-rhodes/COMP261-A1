import java.awt.*;
import java.util.ArrayList;

/**
 * A road segment is a part of a road between two nodes. The only intersections on a road segment are at its ends.
 */
public class Segment {

    // The road the segment belongs to
    private Road road;
    // The length of the segment
    private double length;
    // The segment's start node
    private Node node1;
    // The segment's end node
    private Node node2;
    // A list of all the points along the segment
    private ArrayList<Location> locations = new ArrayList<>();
    // Whether or not this segment is highlighted.
    private boolean highlight = false;

    /**
     * Constructor
     * Create an instance of the segment class.
     *
     * @param road - The road the segment belongs to
     * @param length - The length of the segment
     * @param node1 - The segment's start node
     * @param node2 - The segment's end node
     * @param coords - A list of all the points along the segment
     */
    public Segment(Road road, double length, Node node1, Node node2, String[] coords) {
        this.road = road;
        this.length = length;
        this.node1 = node1;
        this.node2 = node2;

        // Convert coords from String[] to an ArrayList of locations
        for(int i = 0; i < coords.length; i+=2) {
            locations.add(Location.newFromLatLon(Double.parseDouble(coords[i]), Double.parseDouble(coords[i+1]))); // Add a new location to the list of locations
        }
    }

    /**
     * Draws the segment. This will be called during the redraw() method in Main.java
     *
     * @param g - The graphics object
     * @param origin - The Location which indicates the origin of the map
     * @param scale - The current scale of the map
     */
    public void draw(Graphics g, Location origin, double scale) {
        // Set the color
        if(highlight) {g.setColor(Color.RED);} else {g.setColor(Color.BLACK);}

        // Initialise arrays for x and y points.
        int[] xPoints = new int[locations.size()];
        int[] yPoints = new int[locations.size()];

        // Fille the xPoints and yPoints array
        for(int i = 0; i < locations.size(); i++) {
            Point p = locations.get(i).asPoint(origin, scale);
            xPoints[i] = p.x;
            yPoints[i] = p.y;
        }

        // Draw the lines.
        g.drawPolyline(xPoints, yPoints, locations.size());
    }

    /**
     * Toggle whether this is highlighted or not.
     */
    public void highlight() {
        highlight = !highlight;
    }

    //  ------------------------------------------------------------------------------------------------
    //  THE GETTERS AND SETTERS WERE AUTO GENERATED BY INTELLIJ, I HAVE ONLY ADDED THE METHOD COMMENTS.
    //  ------------------------------------------------------------------------------------------------

    /**
     * @return The road the segment belongs to
     */
    public Road getRoad() {
        return road;
    }

    /**
     * @return The length of the segment
     */
    public double getLength() {
        return length;
    }

    /**
     * @return The segment's start node
     */
    public Node getNode1() {
        return node1;
    }

    /**
     * @return The segment's end node
     */
    public Node getNode2() {
        return node2;
    }

    /**
     * @return The list of all the points along the segment
     */
    public ArrayList<Location> getLocations() {
        return locations;
    }
}
