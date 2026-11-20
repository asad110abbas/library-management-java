package com.portfolio.library.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String name;
    @Column(nullable = false) private String email;
    private LocalDate joinedOn;
    private boolean active = true;

    protected Member() {}

    public Member(String name, String email, LocalDate joinedOn) {
        this.name = name;
        this.email = email;
        this.joinedOn = joinedOn;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public LocalDate getJoinedOn() { return joinedOn; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
