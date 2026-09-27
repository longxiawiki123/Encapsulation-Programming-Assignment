package HRServices.Enums;

import HRServices.Records.Address;
import HRServices.Records.LocationInfo;

/** Each named work site supplies one immutable record of location details. */
public enum WorkLocation {
    LEXINGTON_OFFICE("Lexington Office", "Administrative and facilities office.",
            new LocationInfo("Building A", "201",
                    new Address("100 Bluegrass Meadow Drive", "Lexington", State.KY, "40507"))),


    public static WorkLocation fromString(String value) {
        String text = value.trim();
        for (WorkLocation candidate : values()) {
            if (candidate.name().equalsIgnoreCase(text)
                    || candidate.getName().equalsIgnoreCase(text)) {
                return candidate;
            }
        }
        throw new IllegalArgumentException("Unknown WorkLocation: " + value);
    }

    @Override
    public String toString() {
        return name;
    }
}
