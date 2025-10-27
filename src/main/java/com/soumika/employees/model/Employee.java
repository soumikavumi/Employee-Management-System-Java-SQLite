package com.soumika.employees.model;

public class Employee {
    private Integer id;        // db primary key
    private String empId;      // business id
    private String name;
    private String department;
    private double salary;

    public Employee(String empId, String name, String department, double salary) {
        this.empId = empId; this.name = name; this.department = department; this.salary = salary;
    }
    public Employee(Integer id, String empId, String name, String department, double salary) {
        this(empId, name, department, salary); this.id = id;
    }

    public Integer getId() { return id; }
    public String getEmpId() { return empId; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public double getSalary() { return salary; }

    public void setName(String v){ this.name=v; }
    public void setDepartment(String v){ this.department=v; }
    public void setSalary(double v){ this.salary=v; }

    @Override public String toString(){
        return empId + " | " + name + " | " + department + " | " + salary;
    }
}
