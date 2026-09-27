package HRServices.Records;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/** Immutable employee info. */
public record EmployeeInfo(
        String employeeId,
        ContactInfo employeeContact,
        ContactInfo supervisorContact,
        EmploymentInfo employmentInfo,
        EmergencyContact[] emergencyContacts) {

    public EmployeeInfo {
        // Keep the record independent of the caller's mutable array.
        emergencyContacts = emergencyContacts.clone();
    }

    @Override
    public EmergencyContact[] emergencyContacts() {
        // Do not expose the record's internal array.
        return emergencyContacts.clone();
    }

    /** Compare contact contents, not array identities. */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EmployeeInfo employee)) {
            return false;
        }
        return Objects.equals(employeeId, employee.employeeId)
                && Objects.equals(employeeContact, employee.employeeContact)
                && Objects.equals(supervisorContact, employee.supervisorContact)
                && Objects.equals(employmentInfo, employee.employmentInfo)
                && Arrays.equals(emergencyContacts, employee.emergencyContacts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, employeeContact, supervisorContact,
                employmentInfo, Arrays.hashCode(emergencyContacts));
    }

    @Override
    public String toString() {
        return toString(0);
    }

    /** Formats this record with the requested number of leading tabs. */
    public String toString(int tabLevel) {
        if (tabLevel < 0) {
            throw new IllegalArgumentException("tabLevel must not be negative.");
        }
        String indent = "\t".repeat(tabLevel);
        StringBuilder text = new StringBuilder();
        text.append(indent).append("Employee ID: ").append(employeeId).append('\n');
        text.append(indent).append("Employment:").append('\n');
        text.append(employmentInfo.toString(tabLevel + 1)).append('\n');
        text.append(indent).append("Employee contact:").append('\n');
        text.append(employeeContact.toString(tabLevel + 1)).append('\n');
        text.append(indent).append("Supervisor contact:").append('\n');
        text.append(supervisorContact.toString(tabLevel + 1)).append('\n');
        text.append(indent).append("Emergency contacts: ").append(emergencyContacts.length);
        for (int i = 0; i < emergencyContacts.length; i++) {
            text.append('\n').append(indent).append('\t')
                    .append("Emergency contact ").append(i + 1).append(':').append('\n');
            text.append(emergencyContacts[i].toString(tabLevel + 2));
        }
        return text.toString();
    }

    /** A nested builder, not a subclass: records themselves are final. */
    public static class Builder {
        private String employeeId;
        private ContactInfo employeeContact;
        private ContactInfo supervisorContact;
        private EmploymentInfo employmentInfo;
        private ArrayList<EmergencyContact> emergencyContacts = new ArrayList<>();

        public Builder employeeId(String employeeId) {
            this.employeeId = employeeId;
            return this;
        }

        public Builder employeeContact(ContactInfo employeeContact) {
            this.employeeContact = employeeContact;
            return this;
        }

        public Builder supervisorContact(ContactInfo supervisorContact) {
            this.supervisorContact = supervisorContact;
            return this;
        }

        public Builder employmentInfo(EmploymentInfo employmentInfo) {
            this.employmentInfo = employmentInfo;
            return this;
        }

        public Builder emergencyContacts(ArrayList<EmergencyContact> emergencyContacts) {
            this.emergencyContacts = emergencyContacts;
            return this;
        }

        public EmployeeInfo build() {
            // The builder's growable list becomes an exact-length array per employee.
            EmergencyContact[] contacts = new EmergencyContact[emergencyContacts.size()];
            for (int i = 0; i < contacts.length; i++) {
                contacts[i] = emergencyContacts.get(i);
            }
            return new EmployeeInfo(employeeId,
                    employeeContact,
                    supervisorContact,
                    employmentInfo,
                    contacts);
        }
    }
}
