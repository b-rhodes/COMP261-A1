import java.util.ArrayList;

/**
 * A road is a sequences of segments, with a name and other properties.
 * These need not be an entire road - a real road that has different properties for some parts
 * will be represented in the data by several road objects, all with the same name.
 */
public class Road {

    // ID of the road
    private int roadID;
    // Type of road
    private int type;
    // Name of road
    private String roadName;
    // City the road is in
    private String roadCity;
    // True if road is one way
    private boolean oneway;
    // Speed limit on the road
    private int speedLimit;
    // True if road is not for cars
    private boolean notforcar;
    // True if road is not for pedestrians
    private boolean notforpede;
    // True if road is not for bicycles
    private boolean notforbicy;

    // The list of all the segments connected to the road
    private ArrayList<Segment> segmentList = new ArrayList<>();

    /**
     * Constructor
     * Create an instance of the road class, with the specified parameters.
     *
     * @param roadID - ID of the road
     * @param type - Type of road
     * @param roadName - Name of road
     * @param roadCity - City the road is in
     * @param oneway - Whether or not the road is one way
     * @param speedLimit - Speed limit on the road
     * @param notforcar - Whether or not cars are allowed on the road
     * @param notforpede - Whether or not pedestrians are allowed on the road
     * @param notforbicy - Whether or not bicycles are allowed on the road
     */
    public Road(int roadID, int type, String roadName, String roadCity, boolean oneway, int speedLimit, boolean notforcar, boolean notforpede, boolean notforbicy) {

        // set the fields.
        this.roadID = roadID;
        this.type = type;
        this.roadName = roadName;
        this.roadCity = roadCity;
        this.oneway = oneway;
        this.speedLimit = speedLimit;
        this.notforcar = notforcar;
        this.notforpede = notforpede;
        this.notforbicy = notforbicy;
    }

    /**
     * @return Returns the speed limit on the road in a string
     */
    public String getSpeedLimitText() {
        switch (speedLimit) {
            case 0: return "5km/h";
            case 1: return "20km/h";
            case 2: return "40km/h";
            case 3: return "60km/h";
            case 4: return "80km/h";
            case 5: return "100km/h";
            case 6: return "110km/h";
            default: return "no limit";
        }
    }

    /**
     * @return Returns the class of the road
     */
    public String getRoadClass() {
        switch (type) {
            case 1: return "Collector";
            case 2: return "Arterial";
            case 3: return "Principal HW";
            case 4: return "Major HW";
            default: return "Residential";
        }
    }

    /**
     * @return A printable string of whether or not the road is one way
     */
    public String getOneWayText() {
        if(oneway) {
            return "one way road";
        }
        return "both directions allowed";
    }

    /**
     * @return A printable string of what is allowed on the road
     */
    public String getCarPedeBike() {
        String s = "";
        if (notforcar) {
            s += "No Cars,";
        }
        if (notforpede) {
            s+= "No Pedestrians,";
        }
        if (notforbicy) {
            s += "No Bikes,";
        }
        if(s.length() > 0) s = s.substring(0, s.length()-2);
        return s;
    }









    //  ------------------------------------------------------------------------------------------------
    //  THE GETTERS AND SETTERS WERE AUTO GENERATED BY INTELLIJ, I HAVE ONLY ADDED THE METHOD COMMENTS.
    //  ------------------------------------------------------------------------------------------------

    /**
     * @return The road ID
     */
    public int getRoadID() {
        return roadID;
    }

    /**
     * @return The type of the road
     */
    public int getType() {
        return type;
    }

    /**
     * @return The name of the road
     */
    public String getRoadName() {
        return roadName;
    }

    /**
     * @return The city the road is in
     */
    public String getRoadCity() {
        return roadCity;
    }

    /**
     * @return Whether or not the road is one way (true if one way)
     */
    public boolean getOneway() {
        return oneway;
    }

    /**
     * @return The speed limit on the road
     */
    public int getSpeedLimit() {
        return speedLimit;
    }

    /**
     * @return Whether or not the road allows cars (true if no cars)
     */
    public boolean getNotforcar() {
        return notforcar;
    }

    /**
     * @return Whether or not the road is for pedestrians (true if no pedestrians)
     */
    public boolean getNotforpede() {
        return notforpede;
    }

    /**
     * @return Whether or not the road is for bicycles (True of not for bicycles)
     */
    public boolean getNotforbicy() {
        return notforbicy;
    }

    /**
     * @return The list containing the segments that make up this road
     */
    public ArrayList<Segment> getSegmentList() {
        return segmentList;
    }
}
