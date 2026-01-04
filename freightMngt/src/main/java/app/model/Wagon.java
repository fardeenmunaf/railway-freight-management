package app.model;

/**
 * Represents a railway wagon in the freight management system.
 * Each wagon contains a reference to its model, operator, and service year.
 * The detailed specifications such as type, weight, and dimensions are stored in the WagonModel.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class Wagon {
    private Integer wagonId;
    private WagonModel wagonModel;
    private User operator;
    private Integer serviceYear;
    private Integer startFacility; // FK to FACILITY - current location

    // USLP09 - Transient fields for train assembly (populated by PL/SQL functions)
    private String status; // PARKED, IN_TRANSIT, WAITING
    private String currentLocationName; // Facility name
    private Integer locationId; // Facility ID
    private Double locationLat; // Latitude
    private Double locationLon; // Longitude
    private Double distanceFromDeparture; // Distance in km from departure facility
    private String parkingTrackName; // Track name if parked
    private Integer parkingTrackId; // Track ID if parked
    private Integer parkingPosition; // Position on track if parked
    private String operatorCompanyName; // Company name of operator
    private Integer inActiveFreight; // Count of active freight assignments

    /**
     * Default constructor that creates a Wagon with null values.
     * Initializes all fields to null.
     */

    public Wagon() {
    }

    /**
     * Parameterized constructor that creates a Wagon with specified values.
     *
     * @param wagonId the unique identifier for this wagon
     * @param wagonModel the model of the wagon containing specifications
     * @param operator the user or company operating this wagon
     * @param serviceYear the year the wagon entered service
     */

    public Wagon(Integer wagonId, WagonModel wagonModel, User operator, Integer serviceYear) {
        this.wagonId = wagonId;
        this.wagonModel = wagonModel;
        this.operator = operator;
        this.serviceYear = serviceYear;
    }

    /**
     * Gets the unique identifier of this wagon.
     *
     * @return the wagon identifier as an Integer
     */

    public Integer getWagonId() {
        return wagonId;
    }

    /**
     * Sets the unique identifier of this wagon.
     *
     * @param wagonId the wagon identifier to set
     */

    public void setWagonId(Integer wagonId) {
        this.wagonId = wagonId;
    }

    /**
     * Gets the wagon model of this wagon.
     *
     * @return the wagon model as a WagonModel object
     */

    public WagonModel getWagonModel() {
        return wagonModel;
    }

    /**
     * Sets the wagon model of this wagon.
     *
     * @param wagonModel the wagon model to set
     */

    public void setWagonModel(WagonModel wagonModel) {
        this.wagonModel = wagonModel;
    }

    /**
     * Gets the operator of this wagon.
     *
     * @return the operator as a User object
     */

    public User getOperator() {
        return operator;
    }

    /**
     * Sets the operator of this wagon.
     *
     * @param operator the operator to set
     */

    public void setOperator(User operator) {
        this.operator = operator;
    }

    /**
     * Gets the service year of this wagon.
     *
     * @return the service year as an Integer
     */

    public Integer getServiceYear() {
        return serviceYear;
    }

    /**
     * Sets the service year of this wagon.
     *
     * @param serviceYear the service year to set
     */

    public void setServiceYear(Integer serviceYear) {
        this.serviceYear = serviceYear;
    }

    /**
     * Gets the start facility ID where this wagon is currently located.
     *
     * @return the start facility ID as an Integer
     */
    public Integer getStartFacility() {
        return startFacility;
    }

    /**
     * Sets the start facility ID where this wagon is currently located.
     *
     * @param startFacility the facility ID to set
     */
    public void setStartFacility(Integer startFacility) {
        this.startFacility = startFacility;
    }

    // USLP09 - Getters and Setters for transient fields

    /**
     * Gets the status of this wagon (PARKED, IN_TRANSIT, WAITING).
     * USLP09 transient field.
     *
     * @return the status as a String
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of this wagon.
     *
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the current location name (facility name).
     * USLP09 transient field.
     *
     * @return the location name as a String
     */
    public String getCurrentLocationName() {
        return currentLocationName;
    }

    /**
     * Sets the current location name.
     *
     * @param currentLocationName the location name to set
     */
    public void setCurrentLocationName(String currentLocationName) {
        this.currentLocationName = currentLocationName;
    }

    /**
     * Gets the location facility ID.
     * USLP09 transient field.
     *
     * @return the location ID as an Integer
     */
    public Integer getLocationId() {
        return locationId;
    }

    /**
     * Sets the location facility ID.
     *
     * @param locationId the location ID to set
     */
    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    /**
     * Gets the location latitude.
     * USLP09 transient field.
     *
     * @return the latitude as a Double
     */
    public Double getLocationLat() {
        return locationLat;
    }

    /**
     * Sets the location latitude.
     *
     * @param locationLat the latitude to set
     */
    public void setLocationLat(Double locationLat) {
        this.locationLat = locationLat;
    }

    /**
     * Gets the location longitude.
     * USLP09 transient field.
     *
     * @return the longitude as a Double
     */
    public Double getLocationLon() {
        return locationLon;
    }

    /**
     * Sets the location longitude.
     *
     * @param locationLon the longitude to set
     */
    public void setLocationLon(Double locationLon) {
        this.locationLon = locationLon;
    }

    /**
     * Gets the distance from the departure facility in kilometers.
     * USLP09 transient field.
     *
     * @return the distance as a Double
     */
    public Double getDistanceFromDeparture() {
        return distanceFromDeparture;
    }

    /**
     * Sets the distance from the departure facility.
     *
     * @param distanceFromDeparture the distance to set
     */
    public void setDistanceFromDeparture(Double distanceFromDeparture) {
        this.distanceFromDeparture = distanceFromDeparture;
    }

    /**
     * Gets the parking track name if wagon is parked.
     * USLP09 transient field.
     *
     * @return the track name as a String
     */
    public String getParkingTrackName() {
        return parkingTrackName;
    }

    /**
     * Sets the parking track name.
     *
     * @param parkingTrackName the track name to set
     */
    public void setParkingTrackName(String parkingTrackName) {
        this.parkingTrackName = parkingTrackName;
    }

    /**
     * Gets the parking track ID if wagon is parked.
     * USLP09 transient field.
     *
     * @return the track ID as an Integer
     */
    public Integer getParkingTrackId() {
        return parkingTrackId;
    }

    /**
     * Sets the parking track ID.
     *
     * @param parkingTrackId the track ID to set
     */
    public void setParkingTrackId(Integer parkingTrackId) {
        this.parkingTrackId = parkingTrackId;
    }

    /**
     * Gets the parking position on track if wagon is parked.
     * USLP09 transient field.
     *
     * @return the position as an Integer
     */
    public Integer getParkingPosition() {
        return parkingPosition;
    }

    /**
     * Sets the parking position.
     *
     * @param parkingPosition the position to set
     */
    public void setParkingPosition(Integer parkingPosition) {
        this.parkingPosition = parkingPosition;
    }

    /**
     * Gets the operator company name.
     * USLP09 transient field.
     *
     * @return the company name as a String
     */
    public String getOperatorCompanyName() {
        return operatorCompanyName;
    }

    /**
     * Sets the operator company name.
     *
     * @param operatorCompanyName the company name to set
     */
    public void setOperatorCompanyName(String operatorCompanyName) {
        this.operatorCompanyName = operatorCompanyName;
    }

    /**
     * Gets the count of active freight assignments.
     * USLP09 transient field.
     *
     * @return the count as an Integer
     */
    public Integer getInActiveFreight() {
        return inActiveFreight;
    }

    /**
     * Sets the active freight count.
     *
     * @param inActiveFreight the count to set
     */
    public void setInActiveFreight(Integer inActiveFreight) {
        this.inActiveFreight = inActiveFreight;
    }

    @Override
    public String toString() {
        return "Wagon{" +
                "wagonId=" + wagonId +
                ", wagonModel=" + wagonModel +
                ", operator=" + operator +
                ", serviceYear=" + serviceYear +
                '}';
    }
}
