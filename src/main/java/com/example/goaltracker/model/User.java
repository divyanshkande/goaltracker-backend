package com.example.goaltracker.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;



import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    // One user can have many goals
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    
    private List<Goal> goals = new ArrayList<>();

    public User() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }

    public void addGoal(Goal goal) {
        goals.add(goal);
        goal.setUser(this);
    }

    public void removeGoal(Goal goal) {
        goals.remove(goal);
        goal.setUser(null);
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        // You can return roles or authorities here. For simplicity, assuming one role for now.
        return List.of(() -> "ROLE_USER");  // Example role, modify as needed
    }
    public boolean isAccountNonExpired() {
        return true;  // Implement logic based on your requirements
    }

    public boolean isAccountNonLocked() {
        return true;  // Implement logic based on your requirements
    }

    public boolean isCredentialsNonExpired() {
        return true;  // Implement logic based on your requirements
    }

    
    public boolean isEnabled() {
        return true;  // Implement logic based on your requirements
    }



}
