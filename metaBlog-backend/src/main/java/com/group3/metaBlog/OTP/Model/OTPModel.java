package com.group3.metaBlog.OTP.Model;

import jakarta.persistence.*;
import lombok.*;


@Data
@Builder
@Table(name = "otp")
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OTPModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 6)
    private Integer otp;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private Long expiryTime;

    @Column(nullable = false)
    private Long createdAt;
}
