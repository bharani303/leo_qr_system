package com.example.qr_leo.controller;

import com.example.qr_leo.model.qr_data;
import com.example.qr_leo.service.qrservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class qrcontroller {
    @Autowired
    qrservice obj;

    @GetMapping("/{id}")
    qr_data geting(@PathVariable int id){
        return obj.getting(id);
    }
    @PutMapping("/scan/{id}")
    public ResponseEntity<String> scan(@PathVariable int id) {
        return ResponseEntity.ok(obj.putting(id));
    }

    @PostMapping("/leo/ticket/add_admin")
    String ticket(@RequestBody qr_data val) throws Exception {
        return obj.addticket(val);
    }
}
