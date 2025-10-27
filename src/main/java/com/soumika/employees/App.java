package com.soumika.employees;

import com.soumika.employees.dao.EmployeeDAO;
import com.soumika.employees.dao.UserDAO;
import com.soumika.employees.model.Employee;
import com.soumika.employees.model.User;
import com.soumika.employees.util.PasswordUtil;
import com.soumika.employees.util.Validator;
import com.soumika.employees.util.CsvExporter;

import java.util.*;
import java.util.logging.*;
import java.sql.*;

public class App {

    private static final Logger LOG = Logger.getLogger("EmployeeSystem");
    private static final Scanner IN = new Scanner(System.in);
    private static final EmployeeDAO employeeDAO = new EmployeeDAO();
    private static final UserDAO userDAO = new UserDAO();

    public static void main(String[] args) {
        setupLogging();
        System.out.println("== Employee Management System ==");
        User user = login();
        if (user == null) {
            System.out.println("Exiting.");
            return;
        }
        LOG.info("Login success: " + user.getUsername() + " (" + user.getRole() + ")");
        runMenu(user);
    }

    private static void setupLogging() {
        try {
            LOG.setUseParentHandlers(false);
            Handler file = new FileHandler("employee-system.log", true);
            file.setFormatter(new SimpleFormatter());
            LOG.addHandler(file);
            Handler console = new ConsoleHandler();
            console.setFormatter(new SimpleFormatter());
            LOG.addHandler(console);
            LOG.setLevel(Level.INFO);
        } catch (Exception e) {
            System.err.println("Logging setup failed: " + e.getMessage());
        }
    }

    private static User login() {
        System.out.print("Username: ");
        String u = IN.nextLine().trim();
        System.out.print("Password: ");
        String p = IN.nextLine().trim();

        try {
            User user = userDAO.findByUsername(u);
            if (user != null && user.getPasswordHash().equals(PasswordUtil.sha256(p))) {
                return user;
            } else {
                System.out.println("Invalid credentials.");
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
            return null;
        }
    }

    private static void runMenu(User user) {
        while (true) {
            System.out.println("\n[1] Add Employee");
            System.out.println("[2] List Employees");
            System.out.println("[3] Search by EmpID");
            System.out.println("[4] Update Department/Salary");
            System.out.println("[5] Delete Employee");
            System.out.println("[6] Export CSV");
            System.out.println("[7] Create User (admin)");
            System.out.println("[0] Exit");
            System.out.print("Choose: ");
            String ch = IN.nextLine().trim();
            try {
                switch (ch) {
                    case "1": addEmployee(); break;
                    case "2": listEmployees(); break;
                    case "3": searchEmployee(); break;
                    case "4": updateEmployee(); break;
                    case "5": deleteEmployee(); break;
                    case "6": exportCsv(); break;
                    case "7": 
                        if ("ADMIN".equalsIgnoreCase(user.getRole())) createUser();
                        else System.out.println("Admin only.");
                        break;
                    case "0": return;
                    default: System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                LOG.log(Level.WARNING, "Operation failed", e);
            }
        }
    }

    private static void addEmployee() throws SQLException {
        System.out.print("EmpID (e.g., EMP-101): ");
        String empId = IN.nextLine().trim();
        System.out.print("Name: ");
        String name = IN.nextLine().trim();
        System.out.print("Department: ");
        String dept = IN.nextLine().trim();
        System.out.print("Salary: ");
        String salaryStr = IN.nextLine().trim();

        if (!Validator.empIdFormat(empId)) { System.out.println("Invalid EmpID."); return; }
        if (!Validator.nonEmpty(name)) { System.out.println("Name required."); return; }
        if (!Validator.deptFormat(dept)) { System.out.println("Invalid Department."); return; }

        double salary;
        try { salary = Double.parseDouble(salaryStr); }
        catch (NumberFormatException nfe){ System.out.println("Salary must be a number."); return; }
        if (!Validator.salaryValid(salary)) { System.out.println("Salary must be >= 0."); return; }

        Employee e = new Employee(empId, name, dept, salary);
        boolean ok = employeeDAO.add(e);
        System.out.println(ok ? "Employee added." : "Add failed.");
        if (ok) LOG.info("Added employee " + empId);
    }

    private static void listEmployees() throws SQLException {
        List<Employee> list = employeeDAO.listAll();
        if (list.isEmpty()) System.out.println("(No employees)");
        for (Employee e : list) System.out.println(e);
        LOG.info("Listed " + list.size() + " employees");
    }

    private static void searchEmployee() throws SQLException {
        System.out.print("EmpID: ");
        String empId = IN.nextLine().trim();
        Employee e = employeeDAO.findByEmpId(empId);
        System.out.println(e == null ? "Not found." : e.toString());
        LOG.info("Searched " + empId + " found=" + (e != null));
    }

    private static void updateEmployee() throws SQLException {
        System.out.print("EmpID: ");
        String empId = IN.nextLine().trim();
        System.out.print("New Department (or blank to skip): ");
        String dept = IN.nextLine().trim();
        System.out.print("New Salary (or blank to skip): ");
        String sal = IN.nextLine().trim();

        String newDept = dept.isBlank() ? null : dept;
        Double newSalary = null;
        if (!sal.isBlank()) {
            try { newSalary = Double.parseDouble(sal); }
            catch (NumberFormatException nfe) { System.out.println("Invalid salary."); return; }
            if (!Validator.salaryValid(newSalary)) { System.out.println("Salary must be >= 0."); return; }
        }
        boolean ok = employeeDAO.update(empId, newDept, newSalary);
        System.out.println(ok ? "Updated." : "Update failed.");
        if (ok) LOG.info("Updated employee " + empId);
    }

    private static void deleteEmployee() throws SQLException {
        System.out.print("EmpID: ");
        String empId = IN.nextLine().trim();
        boolean ok = employeeDAO.delete(empId);
        System.out.println(ok ? "Deleted." : "Delete failed.");
        if (ok) LOG.info("Deleted employee " + empId);
    }

    private static void exportCsv() throws Exception {
        List<Employee> list = employeeDAO.listAll();
        if (list.isEmpty()) { System.out.println("Nothing to export."); return; }
        var file = CsvExporter.exportEmployees(list, "employees_export.csv");
        System.out.println("Exported: " + file.getAbsolutePath());
        LOG.info("CSV exported: " + file.getAbsolutePath());
    }

    private static void createUser() throws SQLException {
        System.out.print("New username: ");
        String u = IN.nextLine().trim();
        System.out.print("Password: ");
        String p = IN.nextLine().trim();
        System.out.print("Role (ADMIN/USER, default USER): ");
        String r = IN.nextLine().trim().toUpperCase();
        if (!Validator.nonEmpty(u) || !Validator.nonEmpty(p)) { System.out.println("Username/password required."); return; }
        boolean ok = userDAO.createUser(u, PasswordUtil.sha256(p), r.isBlank() ? "USER" : r);
        System.out.println(ok ? "User created." : "Failed to create user.");
        if (ok) LOG.info("Created user: " + u + " role=" + (r.isBlank() ? "USER" : r));
    }
}
