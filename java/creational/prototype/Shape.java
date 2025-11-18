package creational.prototype;

/**
 * Prototype interface - declares cloning method
 */
public abstract class Shape implements Cloneable {
    protected String id;
    protected String type;
    protected String color;

    public abstract void draw();

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Clone method - creates a copy of the object
     */
    @Override
    public Shape clone() {
        try {
            return (Shape) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning failed", e);
        }
    }

    @Override
    public String toString() {
        return String.format("%s [id=%s, color=%s]", type, id, color);
    }
}
