package com.soumika.employees.dao;

import com.soumika.employees.db.Database;
import com.soumika.employees.model.Employee;

import java.sql.*;
import java.util.*;

public class EmployeeDAO {

    public boolean add(Employee e) throws SQLException {
        String sql = "INSERT INTO employees(emp_id, name, department, salary) VALUES(?,?,?,?)";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getEmpId());
            ps.setString(2, e.getName());
            ps.setString(3, e.getDepartment());
            ps.setDouble(4, e.getSalary());
            return ps.executeUpdate()==1;
        }
    }

    public List<Employee> listAll() throws SQLException {
        List<Employee> out = new ArrayList<>();
        String sql = "SELECT id, emp_id, name, department, salary FROM employees ORDER BY id DESC";
        try (Connection c = Database.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                out.add(new Employee(
                    rs.getInt("id"),
                    rs.getString("emp_id"),
                    rs.getString("name"),
                    rs.getString("department"),
                    rs.getDouble("salary")
                ));
            }
        }
        return out;
    }

    public Employee findByEmpId(String empId) throws SQLException {
        String sql = "SELECT id, emp_id, name, department, salary FROM employees WHERE emp_id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, empId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()) {
                    return new Employee(
                        rs.getInt("id"),
                        rs.getString("emp_id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getDouble("salary")
                    );
                }
                return null;
            }
        }
    }

    public boolean update(String empId, String department, Double salary) throws SQLException {
        StringBuilder sb = new StringBuilder("UPDATE employees SET ");
        List<Object> params = new ArrayList<>();
        boolean first = true;

        if (department != null) { sb.append("department=?"); params.add(department); first=false; }
        if (salary != null) { if(!first) sb.append(", "); sb.append("salary=?"); params.add(salary); }

        sb.append(" WHERE emp_id=?"); params.add(empId);

        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sb.toString())) {
            for (int i=0;i<params.size();i++) {
                Object p = params.get(i);
                if (p instanceof String) ps.setString(i+1, (String)p);
                else if (p instanceof Double) ps.setDouble(i+1, (Double)p);
            }
            return ps.executeUpdate()==1;
        }
    }

    public boolean delete(String empId) throws SQLException {
        String sql = "DELETE FROM employees WHERE emp_id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, empId);
            return ps.executeUpdate()==1;
        }
    }
}
