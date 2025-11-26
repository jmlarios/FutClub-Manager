package com.futclub.database.dao;

import com.futclub.database.DatabaseConnection;
import com.futclub.model.PlayerMatchStats;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerMatchStatsDAOImpl implements PlayerMatchStatsDAO {
    
    @Override
    public PlayerMatchStats getById(int statsId) {
        String sql = "SELECT * FROM player_match_stats WHERE stats_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, statsId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractStatsFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting player match stats by ID: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public List<PlayerMatchStats> getAll() {
        List<PlayerMatchStats> statsList = new ArrayList<>();
        String sql = "SELECT * FROM player_match_stats ORDER BY stats_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                statsList.add(extractStatsFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all player match stats: " + e.getMessage());
        }
        return statsList;
    }
    
    @Override
    public void insert(PlayerMatchStats stats) {
        String sql = "INSERT INTO player_match_stats (player_id, match_id, minutes_played, goals, assists, " +
                    "rating, shots, shots_on_target, passes_completed, passes_attempted, tackles, interceptions, " +
                    "yellow_cards, red_cards, fouls_committed, fouls_won, was_starter) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, stats.getPlayerId());
            pstmt.setInt(2, stats.getMatchId());
            pstmt.setInt(3, stats.getMinutesPlayed());
            pstmt.setInt(4, stats.getGoals());
            pstmt.setInt(5, stats.getAssists());
            pstmt.setDouble(6, stats.getRating());
            pstmt.setInt(7, stats.getShots());
            pstmt.setInt(8, stats.getShotsOnTarget());
            pstmt.setInt(9, stats.getPassesCompleted());
            pstmt.setInt(10, stats.getPassesAttempted());
            pstmt.setInt(11, stats.getTackles());
            pstmt.setInt(12, stats.getInterceptions());
            pstmt.setInt(13, stats.getYellowCards());
            pstmt.setInt(14, stats.getRedCards());
            pstmt.setInt(15, stats.getFoulsCommitted());
            pstmt.setInt(16, stats.getFoulsWon());
            pstmt.setBoolean(17, stats.isWasStarter());
            
            pstmt.executeUpdate();
            
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                stats.setStatsId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Error inserting player match stats: " + e.getMessage());
        }
    }
    
    @Override
    public void update(PlayerMatchStats stats) {
        String sql = "UPDATE player_match_stats SET player_id = ?, match_id = ?, minutes_played = ?, goals = ?, " +
                    "assists = ?, rating = ?, shots = ?, shots_on_target = ?, passes_completed = ?, " +
                    "passes_attempted = ?, tackles = ?, interceptions = ?, yellow_cards = ?, red_cards = ?, " +
                    "fouls_committed = ?, fouls_won = ?, was_starter = ? WHERE stats_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, stats.getPlayerId());
            pstmt.setInt(2, stats.getMatchId());
            pstmt.setInt(3, stats.getMinutesPlayed());
            pstmt.setInt(4, stats.getGoals());
            pstmt.setInt(5, stats.getAssists());
            pstmt.setDouble(6, stats.getRating());
            pstmt.setInt(7, stats.getShots());
            pstmt.setInt(8, stats.getShotsOnTarget());
            pstmt.setInt(9, stats.getPassesCompleted());
            pstmt.setInt(10, stats.getPassesAttempted());
            pstmt.setInt(11, stats.getTackles());
            pstmt.setInt(12, stats.getInterceptions());
            pstmt.setInt(13, stats.getYellowCards());
            pstmt.setInt(14, stats.getRedCards());
            pstmt.setInt(15, stats.getFoulsCommitted());
            pstmt.setInt(16, stats.getFoulsWon());
            pstmt.setBoolean(17, stats.isWasStarter());
            pstmt.setInt(18, stats.getStatsId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating player match stats: " + e.getMessage());
        }
    }
    
    @Override
    public void delete(int statsId) {
        String sql = "DELETE FROM player_match_stats WHERE stats_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, statsId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting player match stats: " + e.getMessage());
        }
    }
    
    @Override
    public List<PlayerMatchStats> getByMatchId(int matchId) {
        List<PlayerMatchStats> statsList = new ArrayList<>();
        String sql = "SELECT * FROM player_match_stats WHERE match_id = ? ORDER BY player_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, matchId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                statsList.add(extractStatsFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting stats by match: " + e.getMessage());
        }
        return statsList;
    }
    
    @Override
    public List<PlayerMatchStats> getByPlayerId(int playerId) {
        List<PlayerMatchStats> statsList = new ArrayList<>();
        String sql = "SELECT * FROM player_match_stats WHERE player_id = ? ORDER BY match_id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                statsList.add(extractStatsFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting stats by player: " + e.getMessage());
        }
        return statsList;
    }
    
    @Override
    public PlayerMatchStats getByPlayerAndMatch(int playerId, int matchId) {
        String sql = "SELECT * FROM player_match_stats WHERE player_id = ? AND match_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId);
            pstmt.setInt(2, matchId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractStatsFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting stats by player and match: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public List<PlayerMatchStats> getTopScorers(int limit) {
        List<PlayerMatchStats> statsList = new ArrayList<>();
        String sql = "SELECT player_id, SUM(goals) as total_goals FROM player_match_stats " +
                    "GROUP BY player_id ORDER BY total_goals DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                PlayerMatchStats stats = new PlayerMatchStats();
                stats.setPlayerId(rs.getInt("player_id"));
                stats.setGoals(rs.getInt("total_goals"));
                statsList.add(stats);
            }
        } catch (SQLException e) {
            System.err.println("Error getting top scorers: " + e.getMessage());
        }
        return statsList;
    }
    
    @Override
    public List<PlayerMatchStats> getTopRatedPlayers(int limit) {
        List<PlayerMatchStats> statsList = new ArrayList<>();
        String sql = "SELECT player_id, AVG(rating) as avg_rating FROM player_match_stats " +
                    "WHERE minutes_played > 0 GROUP BY player_id ORDER BY avg_rating DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                PlayerMatchStats stats = new PlayerMatchStats();
                stats.setPlayerId(rs.getInt("player_id"));
                stats.setRating(rs.getDouble("avg_rating"));
                statsList.add(stats);
            }
        } catch (SQLException e) {
            System.err.println("Error getting top rated players: " + e.getMessage());
        }
        return statsList;
    }
    
    private PlayerMatchStats extractStatsFromResultSet(ResultSet rs) throws SQLException {
        PlayerMatchStats stats = new PlayerMatchStats();
        stats.setStatsId(rs.getInt("stats_id"));
        stats.setPlayerId(rs.getInt("player_id"));
        stats.setMatchId(rs.getInt("match_id"));
        stats.setMinutesPlayed(rs.getInt("minutes_played"));
        stats.setGoals(rs.getInt("goals"));
        stats.setAssists(rs.getInt("assists"));
        stats.setRating(rs.getDouble("rating"));
        stats.setShots(rs.getInt("shots"));
        stats.setShotsOnTarget(rs.getInt("shots_on_target"));
        stats.setPassesCompleted(rs.getInt("passes_completed"));
        stats.setPassesAttempted(rs.getInt("passes_attempted"));
        stats.setTackles(rs.getInt("tackles"));
        stats.setInterceptions(rs.getInt("interceptions"));
        stats.setYellowCards(rs.getInt("yellow_cards"));
        stats.setRedCards(rs.getInt("red_cards"));
        stats.setFoulsCommitted(rs.getInt("fouls_committed"));
        stats.setFoulsWon(rs.getInt("fouls_won"));
        stats.setWasStarter(rs.getBoolean("was_starter"));
        return stats;
    }
}
