package com.serhat.taskFlow.entity;

import com.serhat.taskFlow.entity.enums.MembershipPlan;
import com.serhat.taskFlow.entity.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class AppUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username;
    private String password;
    private String email;
    private String phone;

    @Enumerated(EnumType.STRING)
    private MembershipPlan membershipPlan;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "assignedTo",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<Task> tasks;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;


    @PrePersist
    public void initUser(){
        this.role=Role.CUSTOMER;
        this.membershipPlan=MembershipPlan.BASIC;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}
