package app.model;

/**
 * Represents a station that extends a {@code Facility}.
 * Contains additional information from the CSV: country, time zone, time zone group,
 * and flags indicating whether it is a city, main station, or airport.
 */
public class Station extends Facility {

    /**
     * Country code or identifier where the station is located.
     */
    private String country;
    /**
     * Time zone identifier (e.g., "Europe/Lisbon").
     */
    private String timeZone;
    /**
     * Time zone group, if applicable.
     */
    private String timeZoneGroup;
    /**
     * True if the station represents a city (agglomeration).
     */
    private Boolean isCity;
    /**
     * True if this is the main station of the city/region.
     */
    private Boolean isMainStation;
    /**
     * True if the station is an airport.
     */
    private Boolean isAirport;

    /**
     * Default constructor for frameworks/serialization.
     * Initializes additional fields to null.
     */
    public Station() {
        super();
        this.country = null;
        this.timeZone = null;
        this.timeZoneGroup = null;
        this.isCity = null;
        this.isMainStation = null;
        this.isAirport = null;
    }

    /**
     * Full constructor to create a {@code Station} instance.
     *
     * @param facilityId    inherited facility identifier
     * @param name          facility name
     * @param facilityType  facility type
     * @param lat           latitude
     * @param lon           longitude
     * @param isIntermodal  true if the facility is intermodal
     * @param country       station's country
     * @param timeZone      station's time zone
     * @param timeZoneGroup time zone group
     * @param isCity        true if it represents a city
     * @param isMainStation true if it is the main station
     * @param isAirport     true if it is an airport
     */
    public Station(Integer facilityId,
                   String name,
                   FacilityType facilityType,
                   Double lat,
                   Double lon,
                   Boolean isIntermodal,
                   String country,
                   String timeZone,
                   String timeZoneGroup,
                   Boolean isCity,
                   Boolean isMainStation,
                   Boolean isAirport) {
        super(facilityId, name, facilityType, lat, lon, isIntermodal);
        this.country = country;
        this.timeZone = timeZone;
        this.timeZoneGroup = timeZoneGroup;
        this.isCity = isCity;
        this.isMainStation = isMainStation;
        this.isAirport = isAirport;
    }

    /**
     * Returns the station's country.
     *
     * @return country or null if not set
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the station's country.
     *
     * @param country country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Returns the station's time zone.
     *
     * @return time zone or null if not set
     */
    public String getTimeZone() {
        return timeZone;
    }

    /**
     * Sets the station's time zone.
     *
     * @param timeZone time zone (e.g., "Europe/Lisbon")
     */
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * Returns the time zone group.
     *
     * @return time zone group or null
     */
    public String getTimeZoneGroup() {
        return timeZoneGroup;
    }

    /**
     * Sets the time zone group.
     *
     * @param timeZoneGroup time zone group
     */
    public void setTimeZoneGroup(String timeZoneGroup) {
        this.timeZoneGroup = timeZoneGroup;
    }

    /**
     * Indicates whether the station represents a city.
     *
     * @return true if it is a city, false otherwise, or null if unknown
     */
    public Boolean getIsCity() {
        return isCity;
    }

    /**
     * Sets whether the station represents a city.
     *
     * @param isCity true for city
     */
    public void setIsCity(Boolean isCity) {
        this.isCity = isCity;
    }

    /**
     * Indicates whether this is the main station.
     *
     * @return true if it is the main station, false otherwise, or null if unknown
     */
    public Boolean getIsMainStation() {
        return isMainStation;
    }

    /**
     * Sets whether this is the main station.
     *
     * @param isMainStation true for main station
     */
    public void setIsMainStation(Boolean isMainStation) {
        this.isMainStation = isMainStation;
    }

    /**
     * Indicates whether the station is an airport.
     *
     * @return true if it is an airport, false otherwise, or null if unknown
     */
    public Boolean getIsAirport() {
        return isAirport;
    }

    /**
     * Sets whether the station is an airport.
     *
     * @param isAirport true for airport
     */
    public void setIsAirport(Boolean isAirport) {
        this.isAirport = isAirport;
    }

    /**
     * String representation of the instance, including inherited fields.
     *
     * @return textual representation of the station
     */
    @Override
    public String toString() {
        return "Station{" +
                "facility=" + super.toString() +
                ", country='" + country + '\'' +
                ", timeZone='" + timeZone + '\'' +
                ", timeZoneGroup='" + timeZoneGroup + '\'' +
                ", isCity=" + isCity +
                ", isMainStation=" + isMainStation +
                ", isAirport=" + isAirport +
                '}';
    }
}
