package instructor;

import HRServices.Records.Address;
import HRServices.Records.ContactInfo;
import HRServices.Records.EmergencyContact;
import HRServices.Enums.EmployeeDivision;
import HRServices.Records.EmployeeInfo;
import HRServices.Records.EmploymentInfo;
import HRServices.Enums.EmploymentType;
import HRServices.Enums.Relationship;
import HRServices.Enums.State;
import HRServices.Enums.WorkLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;

final class Fixtures {
    private Fixtures() {
    }

    static ContactInfo employeeContact() {
        return new ContactInfo("Ava", "Bennett",
                new Address("120 Juniper Lane", "Lexington", State.KY, "40507"),
                "859-555-0100", "ava.bennett@example.com");
    }

    static ContactInfo supervisorContact() {
        return new ContactInfo("Morgan", "Ellis",
                new Address("820 Pine Ridge Street", "Hartford", State.CT, "06103"),
                "860-555-0180", "morgan.ellis@example.com");
    }

    static ContactInfo emergencyContact1() {
        return new ContactInfo("Léa", "O'Neil",
                new Address("42 Willow Terrace", "Portland", State.ME, "04101"),
                "207-555-0131", "lea.oneil@example.com");
    }

    static ContactInfo emergencyContact2() {
        return new ContactInfo("Marco", "Torres",
                new Address("77 Aspen Court", "Reno", State.NV, "89501"),
                "775-555-0162", "marco.torres@example.com");
    }

    static EmploymentInfo employmentInfo() {
        return new EmploymentInfo("Software Engineer", "2021-06-14",
                EmploymentType.FULL_TIME, EmployeeDivision.ENGINEERING,
                WorkLocation.DENVER_OFFICE);
    }

    static ArrayList<EmergencyContact> emergencyContacts(int count) {
        ArrayList<EmergencyContact> contacts = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ContactInfo contact = i == 0 ? emergencyContact1()
                    : i == 1 ? emergencyContact2()
                    : new ContactInfo("Casey" + i, "Brooks",
                            new Address((90 + i) + " Maple Lane", "Lexington", State.KY, "40507"),
                            "859-555-0190", "casey" + i + ".brooks@example.com");
            Relationship relationship = i == 0 ? Relationship.FRIEND
                    : i == 1 ? Relationship.NEIGHBOR : Relationship.OTHER;
            contacts.add(new EmergencyContact(contact, relationship));
        }
        return contacts;
    }

    static EmergencyContact[] emergencyContactArray(int count) {
        return emergencyContacts(count).toArray(new EmergencyContact[count]);
    }

    static EmployeeInfo employee() {
        return employee(2);
    }

    static EmployeeInfo employee(int count) {
        return new EmployeeInfo("EMP0001", employeeContact(), supervisorContact(),
                employmentInfo(), emergencyContactArray(count));
    }

    static LinkedHashMap<String, String> entry() {
        return entry(2);
    }

    static LinkedHashMap<String, String> entry(int count) {
        LinkedHashMap<String, String> fields = new LinkedHashMap<>();
        fields.put("employeeId", "EMP0001");
        fields.put("employeeDivision", "Engineering");
        fields.put("jobTitle", "Software Engineer");
        fields.put("hireDate", "2021-06-14");
        fields.put("employmentType", "Full-time");
        fields.put("workLocation", "Denver Office");
        fields.put("emergencyContactCount", Integer.toString(count));
        addContact(fields, "employee", employeeContact());
        addContact(fields, "supervisor", supervisorContact());
        ArrayList<EmergencyContact> contacts = emergencyContacts(count);
        for (int i = 0; i < contacts.size(); i++) {
            String prefix = "emergencyContact" + (i + 1);
            addContact(fields, prefix, contacts.get(i).contactInfo());
            fields.put(prefix + "Relation", contacts.get(i).relationship().getName());
        }
        return fields;
    }

    private static void addContact(LinkedHashMap<String, String> fields,
                                   String prefix, ContactInfo contact) {
        fields.put(prefix + "FirstName", contact.firstName());
        fields.put(prefix + "LastName", contact.lastName());
        fields.put(prefix + "Street", contact.address().street());
        fields.put(prefix + "City", contact.address().city());
        fields.put(prefix + "State", contact.address().state().name());
        fields.put(prefix + "ZipCode", contact.address().zipCode());
        fields.put(prefix + "Telephone", contact.telephone());
        fields.put(prefix + "Email", contact.email());
    }
}
