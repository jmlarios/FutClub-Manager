package com.futclub.database.dao;

import com.futclub.database.DatabaseInitializer;
import com.futclub.model.Match;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple tests for MatchDAO operations.
 */
class MatchDAOTest {

    private MatchDAOImpl matchDAO;

    @BeforeEach
    void setUp() throws Exception {
        DatabaseInitializer.initializeDatabase();
        DatabaseInitializer.loadSeedData();
        matchDAO = new MatchDAOImpl();
    }

    @Test
    void testGetAllMatches() throws Exception {
        List<Match> matches = matchDAO.getAll();
        assertNotNull(matches, "Matches list should not be null");
        assertTrue(matches.size() > 0, "Should have matches from seed data");
    }

    @Test
    void testGetUpcomingMatches() throws Exception {
        List<Match> upcomingMatches = matchDAO.getUpcomingMatches();
        assertNotNull(upcomingMatches, "Upcoming matches list should not be null");

        // Check that all upcoming matches are actually scheduled
        for (Match match : upcomingMatches) {
            assertEquals("SCHEDULED", match.getMatchStatus(), "Upcoming matches should have SCHEDULED status");
        }
    }

    @Test
    void testGetCompletedMatches() throws Exception {
        List<Match> completedMatches = matchDAO.getCompletedMatches();
        assertNotNull(completedMatches, "Completed matches list should not be null");

        // Check that all completed matches have COMPLETED status
        for (Match match : completedMatches) {
            assertEquals("COMPLETED", match.getMatchStatus(), "Completed matches should have COMPLETED status");
        }
    }

    @Test
    void testGetMatchById() throws Exception {
        List<Match> matches = matchDAO.getAll();
        assertTrue(matches.size() > 0, "Should have matches");

        Match firstMatch = matches.get(0);
        Match retrieved = matchDAO.getById(firstMatch.getMatchId());
        assertNotNull(retrieved, "Should retrieve match by ID");
        assertEquals(firstMatch.getMatchId(), retrieved.getMatchId());
    }
}
