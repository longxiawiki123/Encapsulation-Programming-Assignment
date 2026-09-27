package HRServices.Enums;

import HRServices.Records.Address;
import HRServices.Records.LocationInfo;

public enum WorkLocation {

    LEXINGTON_OFFICE(
            "Lexington Office",
            "Administrative and facilities office.",
            new LocationInfo(
                    "Building A",
                    "201",
                    new Address(
                            "100 Bluegrass Meadow Drive",
                            "Lexington",
                            State.KY,
                            "40507"
                    )
            )
    ),

    CHICAGO_OFFICE(
            "Chicago Office",
            "Regional sales office.",
            new LocationInfo(
                    "Building C",
                    "310",
                    new Address(
                            "240 Lakeshore Grove Avenue",
                            "Chicago",
                            State.IL,
                            "60601"
                    )
            )
    ),

    DENVER_OFFICE(
            "Denver Office",
            "Engineering and product office.",
            new LocationInfo(
                    "Building D",
                    "405",
                    new Address(
                            "360 Aspen Meadow Road",
                            "Denver",
                            State.CO,
                            "80202"
                    )
            )
    ),

    PHOENIX_WAREHOUSE(
            "Phoenix Warehouse",
            "Inventory and order fulfillment center.",
            new LocationInfo(
                    "Warehouse P",
                    "12",
                    new Address(
                            "480 Desert Willow Way",
                            "Phoenix",
                            State.AZ,
                            "85004"
                    )
            )
    ),

    ATLANTA_DEPOT(
            "Atlanta Depot",
            "Transportation planning and dispatch depot.",
            new LocationInfo(
                    "Depot T",
                    "108",
                    new Address(
                            "520 Peach Grove Lane",
                            "Atlanta",
                            State.GA,
                            "30303"
                    )
            )
    ),

    SEATTLE_OFFICE(
            "Seattle Office",
            "Marketing and communications office.",
            new LocationInfo(
                    "Building S",
                    "220",
                    new Address(
                            "640 Evergreen Meadow Street",
                            "Seattle",
                            State.WA,
                            "98101"
                    )
            )
    );

    private final String name;
    private final String description;
    private final LocationInfo locationInfo;

    WorkLocation(
            String name,
            String description,
            LocationInfo locationInfo) {

        this.name = name;
        this.description = description;
        this.locationInfo = locationInfo;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocationInfo getLocationInfo() {
        return locationInfo;
    }

    public static WorkLocation fromString(String value) {
        String text = value.trim();

        for (WorkLocation location : values()) {
            if (location.name().equalsIgnoreCase(text)
                    || location.getName().equalsIgnoreCase(text)) {
                return location;
            }
        }

        throw new IllegalArgumentException(
                "Unknown WorkLocation: " + value
        );
    }

    @Override
    public String toString() {
        return name;
    }
}