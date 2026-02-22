package com.example.qr_leo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class qr_data {
    private String Name;
    private String Email;
    private String PhoneNo;
    @Id

    private Integer Id;
    private int Tickets;

    private boolean valid;
}
