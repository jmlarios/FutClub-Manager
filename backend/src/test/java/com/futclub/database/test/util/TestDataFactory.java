package com.futclub.database.test.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Utility class for creating test data.
 * Provides factory methods for creating test objects with valid data.
 */
public class TestDataFactory {
    
    /**
     * Create a test player with default values.
     * Note: This creates a minimal player object for testing.
     * Once Player model class exists, this should be updated.
     */
    public static class TestPlayer {
        private int playerId;
        private String firstName;
        private String lastName;
        private Date dateOfBirth;
        private String position;
        private int shirtNumber;
        private String status;
        private int overallRating;
        private int fitnessLevel;
        
        public static TestPlayer createDefault() {
            TestPlayer player = new TestPlayer();
            player.firstName = "Test";
            player.lastName = "Player";
            player.dateOfBirth = Date.valueOf(LocalDate.now().minusYears(25));
            player.position = "ST";
            player.shirtNumber = 99;
            player.status = "AVAILABLE";
            player.overallRating = 75;
            player.fitnessLevel = 90;
            return player;
        }
        
        // Getters and setters
        public int getPlayerId() { return playerId; }
        public void setPlayerId(int playerId) { this.playerId = playerId; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public Date getDateOfBirth() { return dateOfBirth; }
        public String getPosition() { return position; }
        public int getShirtNumber() { return shirtNumber; }
        public String getStatus() { return status; }
        public int getOverallRating() { return overallRating; }
        public int getFitnessLevel() { return fitnessLevel; }
    }
    
    /**
     * Create a test user with default values.
     */
    public static class TestUser {
        private int userId;
        private String username;
        private String passwordHash;
        private String role;
        private boolean isActive;
        
        public static TestUser createDefault() {
            TestUser user = new TestUser();
            user.username = "test.user";
            user.passwordHash = "hashed_password";
            user.role = "COACH";
            user.isActive = true;
            return user;
        }
        
        // Getters and setters
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPasswordHash() { return passwordHash; }
        public String getRole() { return role; }
        public boolean isActive() { return isActive; }
    }
    
    /**
     * Create a test match with default values.
     */
    public static class TestMatch {
        private int matchId;
        private Timestamp matchDate;
        private String opponent;
        private String venue;
        private String competition;
        private int goalsFor;
        private int goalsAgainst;
        private String matchStatus;
        
        public static TestMatch createDefault() {
            TestMatch match = new TestMatch();
            match.matchDate = Timestamp.valueOf(LocalDateTime.now().plusDays(7));
            match.opponent = "Test Opponent";
            match.venue = "HOME";
            match.competition = "League";
            match.goalsFor = 0;
            match.goalsAgainst = 0;
            match.matchStatus = "SCHEDULED";
            return match;
        }
        
        // Getters and setters
        public int getMatchId() { return matchId; }
        public void setMatchId(int matchId) { this.matchId = matchId; }
        public Timestamp getMatchDate() { return matchDate; }
        public String getOpponent() { return opponent; }
        public String getVenue() { return venue; }
        public String getCompetition() { return competition; }
        public int getGoalsFor() { return goalsFor; }
        public int getGoalsAgainst() { return goalsAgainst; }
        public String getMatchStatus() { return matchStatus; }
    }
}
