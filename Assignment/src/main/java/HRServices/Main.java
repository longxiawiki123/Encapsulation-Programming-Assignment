package HRServices;

import HRServices.Enums.EmployeeDivision;
import HRServices.Enums.EmploymentType;
import HRServices.Enums.Relationship;
import HRServices.Enums.State;
import HRServices.Enums.WorkLocation;
import HRServices.Records.Address;
import HRServices.Records.ContactInfo;
import HRServices.Records.EmergencyContact;
import HRServices.Records.EmployeeInfo;
import HRServices.Records.EmploymentInfo;
import HRServices.Utilities.ParseEmployeeJson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws IOException {
        String filePath = args.length == 0 ? "../employee_records.json" : args[0];
        ParseEmployeeJson parser = new ParseEmployeeJson(filePath);
        HashMap<String, HashMap<String, String>> employees = parser.parse();

        // Look up three employees by ID instead of printing the entire map.
        String[] employeeIds = {"EMP0001", "EMP0002", "EMP0003"};
        for (String employeeId : employeeIds) {
            // An alternate input file may not contain every example ID.
            if (!employees.containsKey(employeeId)) {
                continue;
            }
            HashMap<String, String> fields = employees.get(employeeId);

            Address employeeAddress = new Address.Builder()
                    .street(fields.get("employeeStreet"))
                    .city(fields.get("employeeCity"))
                    .state(State.fromString(fields.get("employeeState")))
                    .zipCode(fields.get("employeeZipCode"))
                    .build();

            ContactInfo employeeContact = new ContactInfo.Builder()
                    .firstName(fields.get("employeeFirstName"))
                    .lastName(fields.get("employeeLastName"))
                    .address(employeeAddress)
                    .telephone(fields.get("employeeTelephone"))
                    .email(fields.get("employeeEmail"))
                    .build();

            Address supervisorAddress = new Address.Builder()
                    .street(fields.get("supervisorStreet"))
                    .city(fields.get("supervisorCity"))
                    .state(State.fromString(fields.get("supervisorState")))
                    .zipCode(fields.get("supervisorZipCode"))
                    .build();

            ContactInfo supervisorContact = new ContactInfo.Builder()
                    .firstName(fields.get("supervisorFirstName"))
                    .lastName(fields.get("supervisorLastName"))
                    .address(supervisorAddress)
                    .telephone(fields.get("supervisorTelephone"))
                    .email(fields.get("supervisorEmail"))
                    .build();

            EmploymentInfo employmentInfo = new EmploymentInfo.Builder()
                    .jobTitle(fields.get("jobTitle"))
                    .hireDate(fields.get("hireDate"))
                    .employmentType(EmploymentType.fromString(fields.get("employmentType")))
                    .employeeDivision(EmployeeDivision.fromString(fields.get("employeeDivision")))
                    .workLocation(WorkLocation.fromString(fields.get("workLocation")))
                    .build();

            ArrayList<EmergencyContact> emergencyContacts = new ArrayList<>();
            int contactCount = Integer.parseInt(fields.get("emergencyContactCount"));
            if (contactCount < 0) {
                throw new IllegalArgumentException("emergencyContactCount must not be negative.");
            }
            for (int number = 1; number <= contactCount; number++) {
                String prefix = "emergencyContact" + number;
                Address address = new Address.Builder()
                        .street(fields.get(prefix + "Street"))
                        .city(fields.get(prefix + "City"))
                        .state(State.fromString(fields.get(prefix + "State")))
                        .zipCode(fields.get(prefix + "ZipCode"))
                        .build();

                ContactInfo contact = new ContactInfo.Builder()
                        .firstName(fields.get(prefix + "FirstName"))
                        .lastName(fields.get(prefix + "LastName"))
                        .address(address)
                        .telephone(fields.get(prefix + "Telephone"))
                        .email(fields.get(prefix + "Email"))
                        .build();

                EmergencyContact emergencyContact = new EmergencyContact.Builder()
                        .contactInfo(contact)
                        .relationship(Relationship.fromString(fields.get(prefix + "Relation")))
                        .build();
                emergencyContacts.add(emergencyContact);
            }

            EmployeeInfo employee = new EmployeeInfo.Builder()
                    .employeeId(employeeId)
                    .employeeContact(employeeContact)
                    .supervisorContact(supervisorContact)
                    .employmentInfo(employmentInfo)
                    .emergencyContacts(emergencyContacts)
                    .build();

            System.out.println(employee);
            System.out.println();
        }
    }
}
