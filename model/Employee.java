package model;

import java.util.Locale;

public class Employee {
    private String id;
    private String fullName;
    private String department;
    private int salary;
    private String birthDate;
    private boolean bonusEligible;

    public Employee() {}

    public Employee(String id, String fullName, String department, int salary, String birthDate, boolean bonusEligible) {
        this.id = id;
        this.fullName = fullName;
        this.department = department;
        this.salary = salary;
        this.birthDate = birthDate;
        this.bonusEligible = bonusEligible;
    }

    // Converts Objetct to JSON String
    public String toJsonString() {
        return String.format(Locale.US,
            "{\"id\":\"%s\",\"fullName\":\"%s\",\"department\":\"%s\",\"salary\":%d,\"birthDate\":\"%s\",\"bonusEligible\":%b}",
            id,
            escapeJson(fullName),
            escapeJson(department),
            salary,
            birthDate,
            bonusEligible
        );
    }

    // Take care of special characters to JSON
    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // "Special getter" - Convert Date to simple int: [YYYY-MM-DD] → [YYYYMMDD]
    public int getBirthDateAsNumeric() {
        if (this.birthDate == null || this.birthDate.trim().isEmpty()) {
            throw new IllegalStateException("There is no date birth for employee: " + this.id);
        }
        return Integer.parseInt(this.birthDate.replace("-", "").trim());
    }

    // Getters
    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getDepartment() { return department; }
    public int getSalary() { return salary; }
    public String getBirthDate() { return birthDate; }
    public boolean getBonusEligible() { return bonusEligible; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setDepartment(String department) { this.department = department; }
    public void setSalary(int salary) { this.salary = salary; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
    public void setBonusEligible(boolean bonusEligible) { this.bonusEligible = bonusEligible; }
    
    // Debug if needed to
    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", department='" + department + '\'' +
                ", salary=" + salary +
                ", birthDate=" + birthDate +
                ", bonusEligible=" + bonusEligible +
                '}';
    }
}