package HRServices.Enums;

public enum EmployeeDivision {

    WAREHOUSE(
            "Warehouse",
            "Receives, stores, and prepares inventory."
    ),

    TRANSPORTATION(
            "Transportation",
            "Coordinates transportation and delivery operations."
    ),

    SALES(
            "Sales",
            "Manages customer sales and accounts."
    ),

    MARKETING(
            "Marketing",
            "Promotes products and manages marketing activities."
    ),

    ENGINEERING(
            "Engineering",
            "Designs and maintains technical systems."
    ),

    MAINTENANCE(
            "Maintenance",
            "Maintains company equipment and facilities."
    );

    private final String name;
    private final String description;

    EmployeeDivision(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static EmployeeDivision fromString(String value) {
        String text = value.trim();

        for (EmployeeDivision division : values()) {
            if (division.name().equalsIgnoreCase(text)
                    || division.getName().equalsIgnoreCase(text)) {
                return division;
            }
        }

        throw new IllegalArgumentException(
                "Unknown EmployeeDivision: " + value
        );
    }

    @Override
    public String toString() {
        return name;
    }
}