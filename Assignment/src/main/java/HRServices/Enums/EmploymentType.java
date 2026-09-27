package HRServices.Enums;

public enum EmploymentType {

    FULL_TIME(
            "Full-time",
            "An employee who works a full-time schedule."
    ),

    PART_TIME(
            "Part-time",
            "An employee who works a part-time schedule."
    ),

    SEASONAL(
            "Seasonal",
            "An employee hired for a particular season."
    ),

    CONTRACTOR(
            "Contractor",
            "A worker engaged for a defined contract."
    );

    private final String name;
    private final String description;

    EmploymentType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static EmploymentType fromString(String value) {
        String text = value.trim();

        for (EmploymentType type : values()) {
            if (type.name().equalsIgnoreCase(text)
                    || type.getName().equalsIgnoreCase(text)) {
                return type;
            }
        }

        throw new IllegalArgumentException(
                "Unknown EmploymentType: " + value
        );
    }

    @Override
    public String toString() {
        return name;
    }
}