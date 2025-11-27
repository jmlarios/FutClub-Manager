package com.futclub.database.dao;

import com.futclub.database.DatabaseConnection;
import com.futclub.model.MatchEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatchEventDAOImpl implements MatchEventDAO {

    @Override
    public MatchEvent getById(int eventId) {
        String sql = "SELECT * FROM match_events WHERE event_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting match event by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<MatchEvent> getAll() {
        List<MatchEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM match_events ORDER BY recorded_at";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                events.add(extractFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all match events: " + e.getMessage());
        }
        return events;
    }

    @Override
    public void insert(MatchEvent event) {
        String sql = "INSERT INTO match_events (match_id, player_id, event_type, minute, second, description) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, event.getMatchId());
            if (event.getPlayerId() == null) {
                pstmt.setNull(2, Types.INTEGER);
            } else {
                pstmt.setInt(2, event.getPlayerId());
            }
            pstmt.setString(3, event.getEventType());
            pstmt.setInt(4, event.getMinute());
            pstmt.setInt(5, event.getSecond());
            pstmt.setString(6, event.getDescription());

            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                event.setEventId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Error inserting match event: " + e.getMessage());
        }
    }

    @Override
    public void update(MatchEvent event) {
        String sql = "UPDATE match_events SET match_id = ?, player_id = ?, event_type = ?, minute = ?, " +
                "second = ?, description = ? WHERE event_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, event.getMatchId());
            if (event.getPlayerId() == null) {
                pstmt.setNull(2, Types.INTEGER);
            } else {
                pstmt.setInt(2, event.getPlayerId());
            }
            pstmt.setString(3, event.getEventType());
            pstmt.setInt(4, event.getMinute());
            pstmt.setInt(5, event.getSecond());
            pstmt.setString(6, event.getDescription());
            pstmt.setInt(7, event.getEventId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating match event: " + e.getMessage());
        }
    }

    @Override
    public void delete(int eventId) {
        String sql = "DELETE FROM match_events WHERE event_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, eventId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting match event: " + e.getMessage());
        }
    }

    @Override
    public List<MatchEvent> getByMatchId(int matchId) {
        List<MatchEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM match_events WHERE match_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, matchId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                events.add(extractFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting match events by match: " + e.getMessage());
        }
        return events;
    }

    @Override
    public List<MatchEvent> getByMatchIdOrdered(int matchId) {
        List<MatchEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM match_events WHERE match_id = ? ORDER BY minute, second";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, matchId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                events.add(extractFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting ordered match events: " + e.getMessage());
        }
        return events;
    }

    @Override
    public List<MatchEvent> getByEventType(String eventType) {
        List<MatchEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM match_events WHERE event_type = ? ORDER BY recorded_at";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, eventType);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                events.add(extractFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting match events by type: " + e.getMessage());
        }
        return events;
    }

    private MatchEvent extractFromResultSet(ResultSet rs) throws SQLException {
        MatchEvent event = new MatchEvent();
        event.setEventId(rs.getInt("event_id"));
        event.setMatchId(rs.getInt("match_id"));
        int playerId = rs.getInt("player_id");
        if (!rs.wasNull()) {
            event.setPlayerId(playerId);
        }
        event.setEventType(rs.getString("event_type"));
        event.setMinute(rs.getInt("minute"));
        event.setSecond(rs.getInt("second"));
        event.setDescription(rs.getString("description"));
        event.setRecordedAt(rs.getTimestamp("recorded_at"));
        return event;
    }
}
