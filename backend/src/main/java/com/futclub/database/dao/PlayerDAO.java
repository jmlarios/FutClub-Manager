package com.futclub.database.dao;

import com.futclub.model.Player;
import java.util.List;

public interface PlayerDAO {
    Player getById(int playerId);
    List<Player> getAll();
    void insert(Player player);
    void update(Player player);
    void delete(int playerId);
    
    List<Player> getByPosition(String position);
    List<Player> getByStatus(String status);
    Player getByShirtNumber(int shirtNumber);
    List<Player> getAvailablePlayers();
    List<Player> getInjuredPlayers();
}
