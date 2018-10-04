package com.tcg.admin.service.impl;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcg.admin.model.LobbyAccountType;
import com.tcg.admin.persistence.springdata.ILobbyAccountTypeRepository;
import com.tcg.admin.service.LobbyAccountTypeService;

@Service
@Transactional
public class LobbyAccountTypeServiceImpl implements LobbyAccountTypeService {

    @Autowired
    ILobbyAccountTypeRepository lobbyAccountTypeRepository;

    @Override
    public List<LobbyAccountType> getAll() {
        return lobbyAccountTypeRepository.findAll();
    }

    @Override
    public List<LobbyAccountType> getActiveList() {
        return lobbyAccountTypeRepository.findByStatusNot(0);
    }
    
}
