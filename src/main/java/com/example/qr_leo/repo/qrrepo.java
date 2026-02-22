package com.example.qr_leo.repo;

import com.example.qr_leo.model.qr_data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface qrrepo extends JpaRepository<qr_data,Integer>{
}
