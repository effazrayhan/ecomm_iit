package com.shwandashop;

public class Employee {
    private int employeeId;
    private String eid;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String hireDate;
    private double salary;
    private String status;
    private String role;

    public Employee() {}

    public Employee(int employeeId, String eid, String name, String email, String phone,
                   String address, String hireDate, double salary, String status, String role) {
        this.employeeId = employeeId;
        this.eid = eid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.hireDate = hireDate;
        this.salary = salary;
        this.status = status;
        this.role = role;
    }

    // Getters and Setters
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getEid() { return eid; }
    public void setEid(String eid) { this.eid = eid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getHireDate() { return hireDate; }
    public void setHireDate(String hireDate) { this.hireDate = hireDate; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", eid='" + eid + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", hireDate='" + hireDate + '\'' +
                ", salary=" + salary +
                ", status='" + status + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
