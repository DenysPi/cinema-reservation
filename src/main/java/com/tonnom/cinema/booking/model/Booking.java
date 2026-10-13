package com.tonnom.cinema.booking.model;

import java.time.LocalDateTime;

import com.tonnom.cinema.screening.model.Screening;
import com.tonnom.cinema.user.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table(name = "bookings")
@Builder 
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="booking_id", nullable = false, unique = true)
    private long id;


    @Enumerated(EnumType.STRING)
    @Column(name="status" ,nullable = false)
    private BookingStatus bookingStatus;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    @Column(name="reference" ,nullable = false)
    private String reference;


    @Column(name="created_at" ,nullable = false)
    private LocalDateTime createdAt;

    @Column(name="expires_at")
    private LocalDateTime expiresAt;

}
