package com.soumika.employees.model;

public class User {
    private Integer id;
    private String username;
    private String passwordHash;
    private String role;

    public User(Integer id, String username, String passwordHash, String role) {
        this.id = id; this.username = username; this.passwordHash = passwordHash; this.role = role;
    }
    public Integer getId(){ return id; }
    public String getUsername(){ return username; }
    public String getPasswordHash(){ return passwordHash; }
    public String getRole(){ return role; }
}
