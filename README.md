# CSE 220 — Employee Records: Encapsulation

## Project manager statement

> As an HR manager, I need a Java application that reads employee data from JSON,
> retrieves employees by ID, and clearly displays their contact information,
> employment details, supervisors, and emergency contacts.

## File layout

```text
repository root/
├── README.md
├── employee_records.json
├── .github/
│   ├── actions/instructor/
│   │   ├── action.yml
│   │   └── auto-grading-config.json
│   └── workflows/
│       └── auto-grading-workflow.yml
└── Assignment/
    ├── pom.xml
    └── src/
        ├── main/java/HRServices/
        │   ├── Main.java
        │   ├── Records/
        │   │   ├── Address.java
        │   │   ├── ContactInfo.java
        │   │   ├── LocationInfo.java
        │   │   ├── EmploymentInfo.java
        │   │   ├── EmergencyContact.java
        │   │   └── EmployeeInfo.java
        │   ├── Enums/
        │   │   ├── State.java
        │   │   ├── Relationship.java
        │   │   ├── EmployeeDivision.java
        │   │   ├── EmploymentType.java
        │   │   └── WorkLocation.java
        │   └── Utilities/
        │       └── ParseEmployeeJson.java
        └── test/java/
            ├── student/
            │   └── StudentTest.java
            └── instructor/
```

Use the package names and capitalization shown above.

**Do not modify any file under `Assignment/src/test/java/instructor/`.**
You may add or change tests under `Assignment/src/test/java/student/`.

Use Java 24 and Maven. Open `Assignment/pom.xml` in IntelliJ and set Main's
working directory to `Assignment`.

## Class components

### Records — HRServices.Records

Preserve these component names, types, and order.

| Record | Components |
| --- | --- |
| `Address` | `String street`, `String city`, `State state`, `String zipCode` |
| `ContactInfo` | `String firstName`, `String lastName`, `Address address`, `String telephone`, `String email` |
| `LocationInfo` | `String building`, `String roomNumber`, `Address address` |
| `EmploymentInfo` | `String jobTitle`, `String hireDate`, `EmploymentType employmentType`, `EmployeeDivision employeeDivision`, `WorkLocation workLocation` |
| `EmergencyContact` | `ContactInfo contactInfo`, `Relationship relationship` |
| `EmployeeInfo` | `String employeeId`, `ContactInfo employeeContact`, `ContactInfo supervisorContact`, `EmploymentInfo employmentInfo`, `EmergencyContact[] emergencyContacts` |

All records need:

- A public static nested `Builder` with field-named methods that return the
  builder for chaining. `build()` returns a new record.
- `toString()` and `toString(int tabLevel)`, using `StringBuilder` and actual
  tabs. Nested records format their own sections.
- `toString()` matching `toString(0)`, no trailing newline, and
  `IllegalArgumentException` for negative tab levels.

EmployeeInfo.Builder starts with an empty `ArrayList<EmergencyContact>` and
accepts that type in `emergencyContacts(...)`. At `build()`, copy the contacts
into an `EmergencyContact[]` sized to the list's current length, preserving
order and duplicates. This fixed-length array belongs to each employee, not a
shared static field.

EmployeeInfo copies arrays on construction and access. Changes to a list,
array, or builder must not change existing records. Its `equals()` and
`hashCode()` compare contact contents, not array identities.

Support zero or more emergency contacts. Keep hire dates as `YYYY-MM-DD`
strings and preserve leading zeroes in ZIP codes. Null checking is not required.

### Enums — HRServices.Enums

| Enum | Values |
| --- | --- |
| `State` | Exactly the 50 U.S. states. Postal abbreviations are constant names and full state names are display names. No DC or territories. |
| `Relationship` | `FRIEND`, `SPOUSE`, `PARTNER`, `NEIGHBOR`, `OTHER` |
| `EmployeeDivision` | `WAREHOUSE`, `TRANSPORTATION`, `SALES`, `MARKETING`, `ENGINEERING`, `MAINTENANCE` |
| `EmploymentType` | `FULL_TIME`, `PART_TIME`, `SEASONAL`, `CONTRACTOR` |
| `WorkLocation` | `LEXINGTON_OFFICE`, `CHICAGO_OFFICE`, `DENVER_OFFICE`, `PHOENIX_WAREHOUSE`, `ATLANTA_DEPOT`, `SEATTLE_OFFICE` |

Each enum provides `getName()`, `getDescription()`, and a display-name
`toString()`. Static `fromString(String)` accepts constant or display names,
ignoring case and surrounding whitespace. Unknown or empty text throws
`IllegalArgumentException`.

WorkLocation also provides `getLocationInfo()`, returning the site's immutable
LocationInfo with its building, room, and Address.

### ParseEmployeeJson — HRServices.Utilities

- Constructor: `ParseEmployeeJson(String filePath)`.
- Method: `HashMap<String, HashMap<String, String>> parse()`.
- The outer key is `employeeId`. Inner maps retain every JSON field unchanged.
- Read on each `parse()` call. An empty array returns an empty map.
- Invalid JSON structure or missing, blank, or duplicate IDs throws
  `IllegalArgumentException`. Unreadable files throw `IOException`.
- Return maps, not records.

The root `employee_records.json` contains 100 valid, flat entries with no null
or blank values. All values are strings. Contact fields use `employee`,
`supervisor`, or `emergencyContactN` prefixes. Each emergency contact has an
`emergencyContactNRelation` field. `emergencyContactCount` gives the number
to build.

### Main — HRServices

Main.java is a demonstration driver for your HRServices classes. It has no
dedicated unit tests.

`main(String[] args)` reads `../employee_records.json` or a supplied file path.
It looks up `EMP0001`, `EMP0002`, and `EMP0003`, manually builds their
records, and prints only those employees in that order. Missing IDs are skipped.

The sample demonstrates one, two, and three emergency contacts, building
each contact list with a loop.

## How grading works

Both test groups run. Test locally with `mvn test` from `Assignment`.

Push your changes to run **Auto grading** and receive a Grade Token.

Grading instructions are in `.github/actions/instructor/action.yml`. The workflow
handles workspace setup before running that action.

| Result | Grade |
| --- | --- |
| Build succeeds and all tests pass | **100 points** |
| Any test fails or errors, the build fails, or no tests are found | **10 points** |

There are no intermediate scores or first-commit bonuses. Setup errors,
cancellation, or timeouts may prevent a token from being issued.

A green workflow does not guarantee 100 points. Check **Score** and **Grade
token** in the GitHub run summary. The submitted grade comes from that run.

## How to submit your grade

In the GitHub run summary, use the **copy-to-clipboard button** in the
**Grade Token** section to copy the entire text string beginning with `ACGT1_`.

Blackboard provides a text field. Paste **only the complete token for the grade
you want submitted**.

**Only one submission is allowed.** Choose the correct token before submitting.
