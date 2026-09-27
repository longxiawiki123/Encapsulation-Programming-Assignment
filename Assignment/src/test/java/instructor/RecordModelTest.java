package instructor;

import HRServices.Records.Address;
import HRServices.Records.ContactInfo;
import HRServices.Records.EmergencyContact;
import HRServices.Records.EmploymentInfo;
import HRServices.Enums.EmploymentType;
import HRServices.Records.LocationInfo;
import HRServices.Enums.WorkLocation;
import HRServices.Enums.EmployeeDivision;
import HRServices.Records.EmployeeInfo;
import HRServices.Enums.Relationship;
import HRServices.Enums.State;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("instructor")
class RecordModelTest {
    // Reflection keeps exhaustive, field-by-field checks in the instructor suite.
    record Model(Class<?> type, Class<?> builderType, Object[] values, Object[] alternatives) {
        Constructor<?> constructor() throws NoSuchMethodException {
            Class<?>[] types = Arrays.stream(type.getRecordComponents())
                    .map(RecordComponent::getType).toArray(Class<?>[]::new);
            return type.getConstructor(types);
        }

        Object builder(Object[] values) throws ReflectiveOperationException {
            Object builder = builderType.getConstructor().newInstance();
            RecordComponent[] components = type.getRecordComponents();
            for (int i = 0; i < components.length; i++) {
                setField(builder, components[i], values[i]);
            }
            return builder;
        }

        void setField(Object builder, RecordComponent component, Object value)
                throws ReflectiveOperationException {
            Class<?> parameterType = component.getType();
            if (parameterType == EmergencyContact[].class) {
                parameterType = ArrayList.class;
                value = new ArrayList<>(Arrays.asList((EmergencyContact[]) value));
            }
            Object returned = builderType.getMethod(component.getName(), parameterType)
                    .invoke(builder, value);
            assertSame(builder, returned, "Builder setters must support chaining.");
        }

        Object build(Object builder) throws ReflectiveOperationException {
            return builderType.getMethod("build").invoke(builder);
        }

        @Override
        public String toString() {
            return type.getSimpleName();
        }
    }

    static Stream<Model> models() {
        return Stream.of(
                new Model(Address.class, Address.Builder.class,
                        new Object[]{"120 Juniper Lane", "Lexington", State.KY, "40507"},
                        new Object[]{"42 Willow Terrace", "Portland", State.ME, "04101"}),
                new Model(ContactInfo.class, ContactInfo.Builder.class,
                        new Object[]{"Ava", "Bennett", Fixtures.employeeContact().address(),
                                "859-555-0100", "ava.bennett@example.com"},
                        new Object[]{"Léa", "O'Neil", Fixtures.emergencyContact1().address(),
                                "+1 (207) 555-0131", "lea.oneil@example.com"}),
                new Model(LocationInfo.class, LocationInfo.Builder.class,
                        new Object[]{"Building A", "201", Fixtures.employeeContact().address()},
                        new Object[]{"Building B", "012", Fixtures.supervisorContact().address()}),
                new Model(EmploymentInfo.class, EmploymentInfo.Builder.class,
                        new Object[]{"Software Engineer", "2021-06-14", EmploymentType.FULL_TIME,
                                EmployeeDivision.ENGINEERING, WorkLocation.DENVER_OFFICE},
                        new Object[]{"Facilities Technician", "2024-02-29", EmploymentType.PART_TIME,
                                EmployeeDivision.MAINTENANCE, WorkLocation.LEXINGTON_OFFICE}),
                new Model(EmergencyContact.class, EmergencyContact.Builder.class,
                        new Object[]{Fixtures.emergencyContact1(), Relationship.FRIEND},
                        new Object[]{Fixtures.emergencyContact2(), Relationship.NEIGHBOR}),
                new Model(EmployeeInfo.class, EmployeeInfo.Builder.class,
                        new Object[]{"EMP0001", Fixtures.employeeContact(), Fixtures.supervisorContact(),
                                Fixtures.employmentInfo(), Fixtures.emergencyContactArray(2)},
                        new Object[]{"EMP0002", Fixtures.supervisorContact(), Fixtures.emergencyContact1(),
                                new EmploymentInfo("Facilities Technician", "2024-02-29",
                                        EmploymentType.PART_TIME, EmployeeDivision.MAINTENANCE,
                                        WorkLocation.LEXINGTON_OFFICE),
                                Fixtures.emergencyContactArray(3)}));
    }

    static Stream<Arguments> fields() {
        return models().flatMap(model -> IntStream.range(0, model.values().length)
                .mapToObj(index -> Arguments.of(model, index,
                        model.type().getRecordComponents()[index].getName())));
    }

    private static void assertComponentEquals(Object expected, Object actual) {
        if (expected instanceof EmergencyContact[] contacts) {
            assertArrayEquals(contacts, (EmergencyContact[]) actual);
        } else {
            assertEquals(expected, actual);
        }
    }

    @ParameterizedTest
    @MethodSource("models")
    void recordsAreImmutableAndBuildersArePublicStaticNestedClasses(Model model) {
        assertTrue(model.type().isRecord());
        assertTrue(Modifier.isFinal(model.type().getModifiers()));
        assertEquals(model.type(), model.builderType().getEnclosingClass());
        assertTrue(Modifier.isPublic(model.builderType().getModifiers()));
        assertTrue(Modifier.isStatic(model.builderType().getModifiers()));
        assertFalse(model.type().isAssignableFrom(model.builderType()));
    }

    @ParameterizedTest
    @MethodSource("models")
    void canonicalConstructorAndBuilderPopulateEveryAccessor(Model model) throws Exception {
        Object direct = model.constructor().newInstance(model.values());
        Object built = model.build(model.builder(model.values()));
        assertEquals(direct, built);
        for (int i = 0; i < model.values().length; i++) {
            RecordComponent component = model.type().getRecordComponents()[i];
            assertComponentEquals(model.values()[i], component.getAccessor().invoke(built));
        }
    }

    @ParameterizedTest
    @MethodSource("models")
    void recordsHaveValueEqualityAndConsistentHashCodes(Model model) throws Exception {
        Object first = model.constructor().newInstance(model.values());
        Object second = model.constructor().newInstance(model.values());
        assertEquals(first, first);
        assertEquals(first, second);
        assertEquals(second, first);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, null);
        assertNotEquals(first, "not a record");
    }

    @ParameterizedTest(name = "{0}: changing {2} changes record equality")
    @MethodSource("fields")
    void everyFieldContributesToEquality(Model model, int index, String field) throws Exception {
        Object original = model.constructor().newInstance(model.values());
        Object[] changed = model.values().clone();
        changed[index] = model.alternatives()[index];
        Object replacement = model.build(model.builder(changed));
        assertNotEquals(original, replacement);
        assertComponentEquals(changed[index], model.type().getMethod(field).invoke(replacement));
    }

    @ParameterizedTest
    @MethodSource("models")
    void reusingOrOverwritingABuilderDoesNotMutateAnExistingRecord(Model model) throws Exception {
        Object builder = model.builder(model.values());
        Object original = model.build(builder);
        RecordComponent[] components = model.type().getRecordComponents();
        for (int i = 0; i < components.length; i++) {
            model.setField(builder, components[i], model.alternatives()[i]);
        }
        Object changed = model.build(builder);
        assertEquals(model.constructor().newInstance(model.values()), original);
        assertEquals(model.constructor().newInstance(model.alternatives()), changed);
        assertNotEquals(original, changed);
        assertNotSame(changed, model.build(builder));
    }

    @Test
    void contactStringsArePreservedWithoutUnrequestedNormalization() {
        ContactInfo contact = new ContactInfo(" Léa ", "O'Neil",
                new Address("12 Oak Street, Apt. 4", "Portland", State.ME, "04101-0123"),
                "+1 (207) 555-0131",
                "Lea.O'Neil+work@example.com");
        assertEquals(" Léa ", contact.firstName());
        assertEquals("O'Neil", contact.lastName());
        assertEquals("12 Oak Street, Apt. 4", contact.address().street());
        assertEquals("04101-0123", contact.address().zipCode());
        assertEquals("+1 (207) 555-0131", contact.telephone());
        assertEquals("Lea.O'Neil+work@example.com", contact.email());
    }
}
