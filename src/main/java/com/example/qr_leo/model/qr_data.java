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
    @Id
    private int Id;
    private String Name;
    private String Email;
    private String PhoneNo;
    private int Tickets;
    private Boolean valid;

}
