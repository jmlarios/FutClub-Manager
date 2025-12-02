package com.futclub.database.dao;

import com.futclub.database.DatabaseConnection;
import com.futclub.database.DatabaseInitializer;
import com.futclub.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple tests for PlayerDAO CRUD operations.
 */
class PlayerDAOTest {

    private PlayerDAOImpl playerDAO;

    @BeforeEach
    void setUp() throws Exception {
        DatabaseInitializer.initializeDatabase();
        DatabaseInitializer.loadSeedData();
        playerDAO = new PlayerDAOImpl();
    }

    @Test
    void testGetAllPlayers() throws Exception {
        List<Player> players = playerDAO.getAll();
        assertNotNull(players, "Players list should not be null");
        assertTrue(players.size() > 0, "Should have players from seed data");
    }

    @Test
    void testGetPlayerById() throws Exception {
        List<Player> players = playerDAO.getAll();
        assertTrue(players.size() > 0, "Should have at least one player");

        Player firstPlayer = players.get(0);
        Player retrieved = playerDAO.getById(firstPlayer.getPlayerId());
        assertNotNull(retrieved, "Should retrieve player by ID");
        assertEquals(firstPlayer.getPlayerId(), retrieved.getPlayerId());
    }

    @Test
    void testGetPlayersByStatus() throws Exception {
        List<Player> availablePlayers = playerDAO.getByStatus("AVAILABLE");
        assertNotNull(availablePlayers, "Available players list should not be null");
        assertTrue(availablePlayers.size() > 0, "Should have available players");

        for (Player player : availablePlayers) {
            assertEquals("AVAILABLE", player.getStatus(), "All players should have AVAILABLE status");
        }
    }

    @Test
    void testGetPlayerByPosition() throws Exception {
        List<Player> forwards = playerDAO.getByPosition("ST");
        assertNotNull(forwards, "Forwards list should not be null");
        assertTrue(forwards.size() > 0, "Should have forward players");

        for (Player player : forwards) {
            assertEquals("ST", player.getPosition(), "All players should have ST position");
        }
    }
}
