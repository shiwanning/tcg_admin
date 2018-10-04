package com.tcg.admin.service;

import com.tcg.admin.model.LobbyAccountType;

import java.util.List;

public interface LobbyAccountTypeService {
	
	List<LobbyAccountType> getAll();

    List<LobbyAccountType> getActiveList();

}
