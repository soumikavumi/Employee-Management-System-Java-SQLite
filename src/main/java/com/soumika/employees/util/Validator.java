package com.soumika.employees.util;

public class Validator {
    public static boolean nonEmpty(String s){ return s != null && !s.trim().isEmpty(); }
    public static boolean empIdFormat(String s){ return s != null && s.matches("[A-Z0-9\\-]{3,12}"); }
    public static boolean deptFormat(String s){ return s != null && s.matches("[A-Za-z\\s]{2,30}"); }
    public static boolean salaryValid(double v){ return v >= 0.0; }
}
