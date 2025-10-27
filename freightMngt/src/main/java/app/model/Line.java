package app.model;

/**
 * Represents a railway line in the freight management system.
 * Each line is a route or corridor composed of multiple segments,
 * owned and operated by a specific user or company.
 * This class maintains the line identification and ownership information.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class Line {

    private Integer lineId;
    private User owner;

    /**
     * Default constructor that creates a Line with null values.
     * Initializes all fields to null.
     */

    public Line() {
        this.lineId = null;
        this.owner = null;
    }

    /**
     * Parameterized constructor that creates a Line with specified values.
     *
     * @param lineId the unique identifier for this line
     * @param owner the user or company that owns this line
     */

    public Line(Integer lineId, User owner) {
        this.lineId = lineId;
        this.owner = owner;
    }

    /**
     * Gets the unique identifier of this line.
     *
     * @return the line identifier as an Integer
     */

    public Integer getLineId() {
        return lineId;
    }

    /**
     * Sets the unique identifier of this line.
     *
     * @param lineId the line identifier to set
     */

    public void setLineId(Integer lineId) {
        this.lineId = lineId;
    }

    /**
     * Gets the owner of this line.
     *
     * @return the owner as a User object
     */

    public User getOwner() {
        return owner;
    }

    /**
     * Sets the owner of this line.
     *
     * @param owner the owner to set
     */

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
