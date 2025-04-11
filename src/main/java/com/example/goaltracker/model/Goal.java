package com.example.goaltracker.model;

import jakarta.persistence.*;
import lombok.*;



@Entity
@Data
public class Goal {


@Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)


private Long Id;
private String title;
private String description;
private boolean completed;
public Long getId() {
    return Id;
}

public void setId(Long id) {
    this.Id = id;
}

public String getTitle() {
    return title;
}

public void setTitle(String title) {
    this.title = title;
}

public String getDescription() {
    return description;
}

public void setDescription(String description) {
    this.description = description;
}

public boolean isCompleted() {
    return completed;
}

public void setCompleted(boolean completed) {
    this.completed = completed;
}



}
