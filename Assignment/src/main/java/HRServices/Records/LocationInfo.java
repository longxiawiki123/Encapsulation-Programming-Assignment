package HRServices.Records;

public record LocationInfo(
        String building,
        String roomNumber,
        Address address) {

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int tabLevel) {
        if (tabLevel < 0) {
            throw new IllegalArgumentException("tabLevel must not be negative.");
        }

        String indent = "\t".repeat(tabLevel);
        StringBuilder text = new StringBuilder();

        text.append(indent).append("Building: ").append(building).append('\n');
        text.append(indent).append("Room: ").append(roomNumber).append('\n');
        text.append(indent).append("Address:").append('\n');
        text.append(address.toString(tabLevel + 1));

        return text.toString();
    }

    public static class Builder {
        private String building;
        private String roomNumber;
        private Address address;

        public Builder building(String building) {
            this.building = building;
            return this;
        }

        public Builder roomNumber(String roomNumber) {
            this.roomNumber = roomNumber;
            return this;
        }

        public Builder address(Address address) {
            this.address = address;
            return this;
        }

        public LocationInfo build() {
            return new LocationInfo(building, roomNumber, address);
        }
    }
}