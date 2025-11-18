package com.sib.ibanklosucl.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sib.ibanklosucl.service.VLBREservice;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/breapi")
@Slf4j
@Tag(name = "Ibanklos application APIs", description = "APIs for Ibanklos")

public class VLBREcontroller {

    @Autowired
    private VLBREservice vlbreservice;

    @GetMapping("/getAmberData")
    public ResponseEntity<String> getAmberDatas(@RequestParam String winum, @RequestParam Long slno) {
        try {
            String result = vlbreservice.getAmberDatas(winum, slno);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("failure");
        }
    }
    @PostMapping("/breFetch")
    public ResponseEntity<String> fetchBreDetails(@RequestParam String winum, @RequestParam Long slno) {
        try {
            String result = vlbreservice.getAmberDatas(winum, slno);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("failure");
        }

    }
}
