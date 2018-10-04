package com.tcg.admin.controller;


import java.util.List;

import org.springframework.http.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcg.admin.model.LobbyAccountType;
import com.tcg.admin.service.LobbyAccountTypeService;
import com.tcg.admin.to.response.JsonResponseT;

@RestController
@RequestMapping(value = "/resources/vendors", produces = MediaType.APPLICATION_JSON_VALUE)
public class VendorResources {

    @Autowired
    LobbyAccountTypeService lobbyAccountTypeService;

    @GetMapping("/list")
    public JsonResponseT<List<LobbyAccountType>> getAdminRoles() {
        JsonResponseT<List<LobbyAccountType>> response = new JsonResponseT<>(true);
        response.setValue(lobbyAccountTypeService.getAll());
        return response;
    }
    
    @GetMapping("/activeList")
    public JsonResponseT<List<LobbyAccountType>> activeList() {
        JsonResponseT<List<LobbyAccountType>> response = new JsonResponseT<>(true);
        response.setValue(lobbyAccountTypeService.getActiveList());
        return response;
    }

}
