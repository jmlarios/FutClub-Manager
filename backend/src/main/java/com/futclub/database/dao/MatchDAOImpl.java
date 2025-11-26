package com.futclub.database.dao;

import com.futclub.database.DatabaseConnection;
import com.futclub.model.Match;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatchDAOImpl implements MatchDAO {
    
    @Override
    public Match getById(int matchId) {
        String sql = "SELECT * FROM matches WHERE match_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, matchId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractMatchFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting match by ID: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public List<Match> getAll() {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT * FROM matches ORDER BY match_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                matches.add(extractMatchFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all matches: " + e.getMessage());
        }
        return matches;
    }
    
    @Override
    public void insert(Match match) {
        String sql = "INSERT INTO matches (match_date, opponent, venue, competition, goals_for, " +
                    "goals_against, match_status, attendance, weather, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setTimestamp(1, match.getMatchDate());
            pstmt.setString(2, match.getOpponent());
            pstmt.setString(3, match.getVenue());
            pstmt.setString(4, match.getCompetition());
            pstmt.setInt(5, match.getGoalsFor());
            pstmt.setInt(6, match.getGoalsAgainst());
            pstmt.setString(7, match.getMatchStatus());
            pstmt.setInt(8, match.getAttendance());
            pstmt.setString(9, match.getWeather());
            pstmt.setString(10, match.getNotes());
            
            pstmt.executeUpdate();
            
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                match.setMatchId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Error inserting match: " + e.getMessage());
        }
    }
    
    @Override
    public void update(Match match) {
        String sql = "UPDATE matches SET match_date = ?, opponent = ?, venue = ?, competition = ?, " +
                    "goals_for = ?, goals_against = ?, match_status = ?, attendance = ?, weather = ?, notes = ? " +
                    "WHERE match_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, match.getMatchDate());
            pstmt.setString(2, match.getOpponent());
            pstmt.setString(3, match.getVenue());
            pstmt.setString(4, match.getCompetition());
            pstmt.setInt(5, match.getGoalsFor());
            pstmt.setInt(6, match.getGoalsAgainst());
            pstmt.setString(7, match.getMatchStatus());
            pstmt.setInt(8, match.getAttendance());
            pstmt.setString(9, match.getWeather());
            pstmt.setString(10, match.getNotes());
            pstmt.setInt(11, match.getMatchId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating match: " + e.getMessage());
        }
    }
    
    @Override
    public void delete(int matchId) {
        String sql = "DELETE FROM matches WHERE match_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, matchId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting match: " + e.getMessage());
        }
    }
    
    @Override
    public List<Match> getUpcomingMatches() {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT * FROM matches WHERE match_status = 'SCHEDULED' AND match_date >= datetime('now') ORDER BY match_date";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                matches.add(extractMatchFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting upcoming matches: " + e.getMessage());
        }
        return matches;
    }
    
    @Override
    public List<Match> getCompletedMatches() {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT * FROM matches WHERE match_status = 'COMPLETED' ORDER BY match_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                matches.add(extractMatchFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting completed matches: " + e.getMessage());
        }
        return matches;
    }
    
    @Override
    public List<Match> getByCompetition(String competition) {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT * FROM matches WHERE competition = ? ORDER BY match_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, competition);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                matches.add(extractMatchFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting matches by competition: " + e.getMessage());
        }
        return matches;
    }
    
    @Override
    public List<Match> getMatchesInDateRange(Timestamp startDate, Timestamp endDate) {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT * FROM matches WHERE match_date BETWEEN ? AND ? ORDER BY match_date";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, startDate);
            pstmt.setTimestamp(2, endDate);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                matches.add(extractMatchFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting matches in date range: " + e.getMessage());
        }
        return matches;
    }
    
    private Match extractMatchFromResultSet(ResultSet rs) throws SQLException {
        Match match = new Match();
        match.setMatchId(rs.getInt("match_id"));
        match.setMatchDate(rs.getTimestamp("match_date"));
        match.setOpponent(rs.getString("opponent"));
        match.setVenue(rs.getString("venue"));
        match.setCompetition(rs.getString("competition"));
        match.setGoalsFor(rs.getInt("goals_for"));
        match.setGoalsAgainst(rs.getInt("goals_against"));
        match.setMatchStatus(rs.getString("match_status"));
        match.setAttendance(rs.getInt("attendance"));
        match.setWeather(rs.getString("weather"));
        match.setNotes(rs.getString("notes"));
        return match;
    }
}
