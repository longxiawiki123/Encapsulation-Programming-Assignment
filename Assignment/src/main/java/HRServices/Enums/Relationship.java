package HRServices.Enums;

public enum Relationship {

    FRIEND("Friend", "A personal friend."),
    SPOUSE("Spouse", "A husband or wife."),
    PARTNER("Partner", "A life partner."),
    NEIGHBOR("Neighbor", "A person who lives nearby."),
    OTHER("Other", "Another trusted emergency contact.");

    private final String name;
    private final String description;

    Relationship(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static Relationship fromString(String value) {
        String text = value.trim();

        for (Relationship relationship : values()) {
            if (relationship.name().equalsIgnoreCase(text)
                    || relationship.getName().equalsIgnoreCase(text)) {
                return relationship;
            }
        }

        throw new IllegalArgumentException("Unknown Relationship: " + value);
    }

    @Override
    public String toString() {
        return name;
    }
}