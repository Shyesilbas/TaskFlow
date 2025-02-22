package com.serhat.jwt.entity;

import com.serhat.jwt.entity.enums.TokenStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "token")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String token;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiresAt;

    @Enumerated(EnumType.STRING)
    private TokenStatus tokenStatus;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expired_at;

    @PrePersist
     public void initToken(){
        this.tokenStatus=TokenStatus.ACTIVE;
    }


}
