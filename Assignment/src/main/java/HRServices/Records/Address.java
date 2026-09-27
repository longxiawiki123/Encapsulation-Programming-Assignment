package HRServices.Records;

import HRServices.Enums.State;

public record Address(
        String street,
        String city,
        State state,
        String zipCode) {

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

        text.append(indent).append("Street: ").append(street).append('\n');
        text.append(indent).append("City: ").append(city).append('\n');
        text.append(indent).append("State: ").append(state).append('\n');
        text.append(indent).append("ZIP code: ").append(zipCode);

        return text.toString();
    }

    public static class Builder {
        private String street;
        private String city;
        private State state;
        private String zipCode;

        public Builder street(String street) {
            this.street = street;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder state(State state) {
            this.state = state;
            return this;
        }

        public Builder zipCode(String zipCode) {
            this.zipCode = zipCode;
            return this;
        }

        public Address build() {
            return new Address(street, city, state, zipCode);
        }
    }
}