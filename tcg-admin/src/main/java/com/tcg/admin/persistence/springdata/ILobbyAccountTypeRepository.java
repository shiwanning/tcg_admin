package com.tcg.admin.persistence.springdata;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tcg.admin.model.LobbyAccountType;

public interface ILobbyAccountTypeRepository extends JpaRepository<LobbyAccountType, Integer> {

    @Query("SELECT a FROM LobbyAccountType a WHERE status != :status")
    List<LobbyAccountType> findByStatusNot(@Param("status") Integer status);

}
