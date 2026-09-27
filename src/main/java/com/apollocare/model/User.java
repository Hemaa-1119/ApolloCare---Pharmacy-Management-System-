package com.apollocare.model;

import java.time.LocalDateTime;

/**
 * Represents a user account (CUSTOMER or ADMIN).
 * Plain Java Bean — no framework annotations.
 */
public class User {

    private int           userId;
    private String        name;
    private String        email;
    private String        password;   // BCrypt hash, never plain text
    private String        phone;
    private String        address;
    private String        role;       // "CUSTOMER" or "ADMIN"
    private LocalDateTime createdAt;

    public User() {}

    // Getters and Setters

    public int getUserId()                    { return userId; }
    public void setUserId(int userId)         { this.userId = userId; }

    public String getName()                   { return name; }
    public void setName(String name)          { this.name = name; }

    public String getEmail()                  { return email; }
    public void setEmail(String email)        { this.email = email; }

    public String getPassword()               { return password; }
    public void setPassword(String password)  { this.password = password; }

    public String getPhone()                  { return phone; }
    public void setPhone(String phone)        { this.phone = phone; }

    public String getAddress()                { return address; }
    public void setAddress(String address)    { this.address = address; }

    public String getRole()                   { return role; }
    public void setRole(String role)          { this.role = role; }

    public LocalDateTime getCreatedAt()                    { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)      { this.createdAt = createdAt; }

    /** Convenience method used in JSP/Servlet logic. */
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    @Override
    public String toString() {
        return "User{userId=" + userId + ", name='" + name + "', email='" + email +
               "', role='" + role + "'}";
    }
}
