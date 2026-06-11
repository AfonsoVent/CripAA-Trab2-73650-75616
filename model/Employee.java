package model;

import java.util.Locale;

public class Employee {
    private int id;
    private String fullName;
    private String department;
    private int salary;
    private String birthDate;

    public Employee() {}

    public Employee(int id, String fullName, String department, int salary, String birthDate) {
        this.id = id;
        this.fullName = fullName;
        this.department = department;
        this.salary = salary;
        this.birthDate = birthDate;
    }

    // Converts Objetct to JSON String
    public String toJsonString() {
        return String.format(Locale.US,
            "{\"id\":%d,\"fullName\":\"%s\",\"department\":\"%s\",\"salary\":%d,\"birthDate\":%d}",
            id,
            escapeJson(fullName),
            escapeJson(department),
            salary,
            birthDate
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
    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getDepartment() { return department; }
    public int getSalary() { return salary; }
    public String getBirthDate() { return birthDate; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setDepartment(String department) { this.department = department; }
    public void setSalary(int salary) { this.salary = salary; }
    public void setbirthDate(String birthDate) { this.birthDate = birthDate; }

    // Debug if needed to
    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", department='" + department + '\'' +
                ", salary=" + salary +
                ", birthDate=" + birthDate +
                '}';
    }
}