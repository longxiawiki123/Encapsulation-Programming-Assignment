package student;

import HRServices.Records.Address;
import HRServices.Records.ContactInfo;
import HRServices.Enums.State;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("student")
class StudentTest {
    @Test
    void buildsAContactWithAnAddress() {
        Address address = new Address.Builder()
                .street("120 Juniper Lane")
                .city("Lexington")
                .state(State.KY)
                .zipCode("40507")
                .build();

        ContactInfo contact = new ContactInfo.Builder()
                .firstName("Ava")
                .lastName("Bennett")
                .address(address)
                .telephone("859-555-0100")
                .email("ava.bennett@example.com")
                .build();

        assertEquals("Ava", contact.firstName());
        assertEquals(State.KY, contact.address().state());
    }
}
