package HRServices.Records;

import HRServices.Enums.EmployeeDivision;
import HRServices.Enums.EmploymentType;
import HRServices.Enums.WorkLocation;

public record EmploymentInfo(
        String jobTitle,
        String hireDate,
        EmploymentType employmentType,
        EmployeeDivision employeeDivision,
        WorkLocation workLocation) {

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

        text.append(indent).append("Job title: ").append(jobTitle).append('\n');
        text.append(indent).append("Hire date: ").append(hireDate).append('\n');
        text.append(indent).append("Employment type: ").append(employmentType).append('\n');
        text.append(indent).append("Division: ").append(employeeDivision).append('\n');
        text.append(indent).append("Work location: ").append(workLocation).append('\n');
        text.append(workLocation.getLocationInfo().toString(tabLevel + 1));

        return text.toString();
    }

    public static class Builder {
        private String jobTitle;
        private String hireDate;
        private EmploymentType employmentType;
        private EmployeeDivision employeeDivision;
        private WorkLocation workLocation;

        public Builder jobTitle(String jobTitle) {
            this.jobTitle = jobTitle;
            return this;
        }

        public Builder hireDate(String hireDate) {
            this.hireDate = hireDate;
            return this;
        }

        public Builder employmentType(EmploymentType employmentType) {
            this.employmentType = employmentType;
            return this;
        }

        public Builder employeeDivision(EmployeeDivision employeeDivision) {
            this.employeeDivision = employeeDivision;
            return this;
        }

        public Builder workLocation(WorkLocation workLocation) {
            this.workLocation = workLocation;
            return this;
        }

        public EmploymentInfo build() {
            return new EmploymentInfo(
                    jobTitle,
                    hireDate,
                    employmentType,
                    employeeDivision,
                    workLocation
            );
        }
    }
}