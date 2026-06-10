package model;

import java.util.Locale;

public class Employee {
    private int id;
    private String fullName;
    private String department;
    private int salary;
    private int age;

    public Employee() {}

    public Employee(int id, String fullName, String department, int salary, int age) {
        this.id = id;
        this.fullName = fullName;
        this.department = department;
        this.salary = salary;
        this.age = age;
    }

    // Converts Objetct to JSON String
    public String toJsonString() {
        return String.format(Locale.US,
            "{\"id\":%d,\"fullName\":\"%s\",\"department\":\"%s\",\"salary\":%d,\"age\":%d}",
            id,
            escapeJson(fullName),
            escapeJson(department),
            salary,
            age
        );
    }

    // Take care of special characters to JSON
    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // Getters
    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getDepartment() { return department; }
    public int getSalary() { return salary; }
    public int getAge() { return age; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setDepartment(String department) { this.department = department; }
    public void setSalary(int salary) { this.salary = salary; }
    public void setAge(int age) { this.age = age; }

    // Debug if needed to
    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", department='" + department + '\'' +
                ", salary=" + salary +
                ", age=" + age +
                '}';
    }
}