package com.soumika.employees.util;

import com.soumika.employees.model.Employee;
import java.io.*;
import java.util.List;

public class CsvExporter {
    public static File exportEmployees(List<Employee> list, String filename) throws IOException {
        File f = new File(filename);
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"))) {
            pw.println("emp_id,name,department,salary");
            for (Employee e : list) {
                pw.printf("%s,%s,%s,%.2f%n",
                    safe(e.getEmpId()), safe(e.getName()), safe(e.getDepartment()), e.getSalary());
            }
        }
        return f;
    }
    private static String safe(String s){ return s == null ? "" : s.replace(",", " "); }
}
