package HRServices.Records;

import HRServices.Enums.Relationship;

public record EmergencyContact(
        ContactInfo contactInfo,
        Relationship relationship) {

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

        text.append(indent)
                .append("Relationship: ")
                .append(relationship)
                .append('\n');

        text.append(indent)
                .append("Contact:")
                .append('\n');

        text.append(contactInfo.toString(tabLevel + 1));

        return text.toString();
    }

    public static class Builder {

        private ContactInfo contactInfo;
        private Relationship relationship;

        public Builder contactInfo(ContactInfo contactInfo) {
            this.contactInfo = contactInfo;
            return this;
        }

        public Builder relationship(Relationship relationship) {
            this.relationship = relationship;
            return this;
        }

        public EmergencyContact build() {
            return new EmergencyContact(
                    contactInfo,
                    relationship
            );
        }
    }
}