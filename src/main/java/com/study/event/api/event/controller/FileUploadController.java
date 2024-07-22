package com.study.event.api.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class FileUploadController {


    @PostMapping("/file/upload")
    public ResponseEntity<?> upload() {



        return ResponseEntity.ok().body("");
    }
}
