package com.tcg.admin.controller;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcg.admin.service.config.AdminConfig;

@RestController
@RequestMapping(value = "/resources/version", produces = MediaType.APPLICATION_JSON_VALUE)
public class VersionResource {

    @GetMapping
    public String getVersion() {
        return AdminConfig.getFullVersion();
    }
}
