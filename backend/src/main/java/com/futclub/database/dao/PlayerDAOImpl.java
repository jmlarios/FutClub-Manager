package com.futclub.database.dao;

import com.futclub.database.DatabaseConnection;
import com.futclub.model.Player;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PlayerDAOImpl implements PlayerDAO {
    
    @Override
    public Player getById(int playerId) {
        String sql = "SELECT * FROM players WHERE player_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractPlayerFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting player by ID: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public List<Player> getAll() {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM players ORDER BY shirt_number";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                players.add(extractPlayerFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all players: " + e.getMessage());
        }
        return players;
    }
    
    @Override
    public void insert(Player player) {
        String sql = "INSERT INTO players (first_name, last_name, date_of_birth, position, " +
                    "shirt_number, status, overall_rating, fitness_level, injury_details, " +
                    "joined_date, contract_end, nationality, height_cm, weight_kg, preferred_foot) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, player.getFirstName());
            pstmt.setString(2, player.getLastName());
            pstmt.setDate(3, player.getDateOfBirth());
            pstmt.setString(4, player.getPosition());
            pstmt.setInt(5, player.getShirtNumber());
            pstmt.setString(6, player.getStatus());
            pstmt.setInt(7, player.getOverallRating());
            pstmt.setInt(8, player.getFitnessLevel());
            pstmt.setString(9, player.getInjuryDetails());
            pstmt.setDate(10, player.getJoinedDate());
            pstmt.setDate(11, player.getContractEnd());
            pstmt.setString(12, player.getNationality());
            setNullableInt(pstmt, 13, player.getHeightCm());
            setNullableInt(pstmt, 14, player.getWeightKg());
            pstmt.setString(15, player.getPreferredFoot());
            
            pstmt.executeUpdate();
            
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                player.setPlayerId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Error inserting player: " + e.getMessage());
        }
    }
    
    @Override
    public void update(Player player) {
        String sql = "UPDATE players SET first_name = ?, last_name = ?, date_of_birth = ?, " +
                    "position = ?, shirt_number = ?, status = ?, overall_rating = ?, " +
                    "fitness_level = ?, injury_details = ?, joined_date = ?, contract_end = ?, " +
                    "nationality = ?, height_cm = ?, weight_kg = ?, preferred_foot = ? " +
                    "WHERE player_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, player.getFirstName());
            pstmt.setString(2, player.getLastName());
            pstmt.setDate(3, player.getDateOfBirth());
            pstmt.setString(4, player.getPosition());
            pstmt.setInt(5, player.getShirtNumber());
            pstmt.setString(6, player.getStatus());
            pstmt.setInt(7, player.getOverallRating());
            pstmt.setInt(8, player.getFitnessLevel());
            pstmt.setString(9, player.getInjuryDetails());
            pstmt.setDate(10, player.getJoinedDate());
            pstmt.setDate(11, player.getContractEnd());
            pstmt.setString(12, player.getNationality());
            setNullableInt(pstmt, 13, player.getHeightCm());
            setNullableInt(pstmt, 14, player.getWeightKg());
            pstmt.setString(15, player.getPreferredFoot());
            pstmt.setInt(16, player.getPlayerId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating player: " + e.getMessage());
        }
    }
    
    @Override
    public void delete(int playerId) {
        String sql = "DELETE FROM players WHERE player_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting player: " + e.getMessage());
        }
    }
    
    @Override
    public List<Player> getByPosition(String position) {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM players WHERE position = ? ORDER BY overall_rating DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, position);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                players.add(extractPlayerFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting players by position: " + e.getMessage());
        }
        return players;
    }
    
    @Override
    public List<Player> getByStatus(String status) {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM players WHERE status = ? ORDER BY shirt_number";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                players.add(extractPlayerFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting players by status: " + e.getMessage());
        }
        return players;
    }
    
    @Override
    public Player getByShirtNumber(int shirtNumber) {
        String sql = "SELECT * FROM players WHERE shirt_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, shirtNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractPlayerFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting player by shirt number: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public List<Player> getAvailablePlayers() {
        return getByStatus("AVAILABLE");
    }
    
    @Override
    public List<Player> getInjuredPlayers() {
        return getByStatus("INJURED");
    }
    
    private Player extractPlayerFromResultSet(ResultSet rs) throws SQLException {
        Player player = new Player();
        player.setPlayerId(rs.getInt("player_id"));
        player.setFirstName(rs.getString("first_name"));
        player.setLastName(rs.getString("last_name"));
        player.setDateOfBirth(getDate(rs, "date_of_birth"));
        player.setPosition(rs.getString("position"));
        player.setShirtNumber(rs.getInt("shirt_number"));
        player.setStatus(rs.getString("status"));
        player.setOverallRating(rs.getInt("overall_rating"));
        player.setFitnessLevel(rs.getInt("fitness_level"));
        player.setInjuryDetails(rs.getString("injury_details"));
        player.setJoinedDate(getNullableDate(rs, "joined_date"));
        player.setContractEnd(getNullableDate(rs, "contract_end"));
        player.setNationality(rs.getString("nationality"));
        player.setHeightCm(getNullableInt(rs, "height_cm"));
        player.setWeightKg(getNullableInt(rs, "weight_kg"));
        player.setPreferredFoot(rs.getString("preferred_foot"));
        return player;
    }

    private void setNullableInt(PreparedStatement pstmt, int index, Integer value) throws SQLException {
        if (value != null) {
            pstmt.setInt(index, value);
        } else {
            pstmt.setNull(index, Types.INTEGER);
        }
    }

    private Integer getNullableInt(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }

    private Date getDate(ResultSet rs, String column) throws SQLException {
        Date date = getNullableDate(rs, column);
        if (date == null) {
            throw new SQLException("Column " + column + " returned null where a value was expected");
        }
        return date;
    }

    private Date getNullableDate(ResultSet rs, String column) throws SQLException {
        String raw = rs.getString(column);
        if (raw == null || raw.isBlank()) {
            return null;
        }

        String normalized = raw.trim();
        if (normalized.chars().allMatch(Character::isDigit)) {
            try {
                long epochMillis = Long.parseLong(normalized);
                return new Date(epochMillis);
            } catch (NumberFormatException ex) {
                throw new SQLException("Failed to parse epoch millis '" + raw + "' for column " + column, ex);
            }
        }

        int spaceIdx = normalized.indexOf(' ');
        if (spaceIdx > 0) {
            normalized = normalized.substring(0, spaceIdx);
        }

        try {
            return Date.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new SQLException("Failed to parse date value '" + raw + "' for column " + column, ex);
        }
    }
}
