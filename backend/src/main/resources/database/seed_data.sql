-- ============================================================================
-- FutClub Manager Seed Data
-- ============================================================================
-- This script populates the database with sample data for testing
-- Execute this AFTER schema.sql has been run
-- ============================================================================

-- ============================================================================
-- USERS - Sample accounts for testing (password: "password123" hashed)
-- Note: In production, use proper password hashing (BCrypt, etc.)
-- For now, we'll use a simple representation
-- ============================================================================
INSERT INTO users (username, password_hash, role, is_active) VALUES
('coach.smith', 'hashed_password_1', 'COACH', 1),
('analyst.jones', 'hashed_password_2', 'ANALYST', 1),
('admin.wilson', 'hashed_password_3', 'ADMINISTRATOR', 1);

-- ============================================================================
-- STAFF - Personal information for the users
-- ============================================================================
INSERT INTO staff (full_name, user_id, email, phone, hire_date) VALUES
('John Smith', 1, 'john.smith@futclub.com', '+34-600-111-222', '2023-01-15'),
('Sarah Jones', 2, 'sarah.jones@futclub.com', '+34-600-333-444', '2023-03-01'),
('Michael Wilson', 3, 'michael.wilson@futclub.com', '+34-600-555-666', '2022-08-10');

-- ============================================================================
-- PLAYERS - Sample squad
-- ============================================================================
INSERT INTO players (first_name, last_name, date_of_birth, position, shirt_number, status, overall_rating, fitness_level, nationality, height_cm, weight_kg, preferred_foot, joined_date) VALUES
-- Goalkeepers
('Marc', 'ter Stegen', '1992-04-30', 'GK', 1, 'AVAILABLE', 87, 100, 'Germany', 187, 85, 'RIGHT', '2020-07-01'),
('Iñaki', 'Peña', '1999-03-02', 'GK', 13, 'AVAILABLE', 72, 100, 'Spain', 184, 78, 'RIGHT', '2021-07-01'),

-- Defenders
('Ronald', 'Araújo', '1999-03-07', 'CB', 4, 'INJURED', 84, 40, 'Uruguay', 188, 83, 'RIGHT', '2020-07-01'),
('Jules', 'Koundé', '1998-11-12', 'CB', 23, 'AVAILABLE', 82, 95, 'France', 180, 70, 'RIGHT', '2022-08-01'),
('Andreas', 'Christensen', '1996-04-10', 'CB', 15, 'AVAILABLE', 80, 100, 'Denmark', 187, 79, 'RIGHT', '2022-07-01'),
('Alejandro', 'Balde', '2003-10-18', 'LB', 3, 'AVAILABLE', 78, 100, 'Spain', 175, 70, 'LEFT', '2021-07-01'),
('João', 'Cancelo', '1994-05-27', 'RB', 2, 'AVAILABLE', 85, 95, 'Portugal', 182, 74, 'RIGHT', '2023-09-01'),

-- Midfielders
('Frenkie', 'de Jong', '1997-05-12', 'CM', 21, 'AVAILABLE', 86, 100, 'Netherlands', 181, 74, 'RIGHT', '2019-07-01'),
('Gavi', 'García', '2004-08-05', 'CM', 6, 'AVAILABLE', 81, 100, 'Spain', 173, 69, 'RIGHT', '2021-07-01'),
('Pedri', 'González', '2002-11-25', 'CAM', 8, 'AVAILABLE', 85, 95, 'Spain', 174, 60, 'RIGHT', '2020-07-01'),
('İlkay', 'Gündoğan', '1990-10-24', 'CDM', 22, 'AVAILABLE', 84, 90, 'Germany', 180, 80, 'RIGHT', '2023-07-01'),

-- Forwards
('Robert', 'Lewandowski', '1988-08-21', 'ST', 9, 'AVAILABLE', 89, 95, 'Poland', 185, 81, 'RIGHT', '2022-07-01'),
('Raphinha', 'Dias', '1996-12-14', 'RW', 11, 'AVAILABLE', 83, 100, 'Brazil', 176, 67, 'LEFT', '2022-07-01'),
('Ferran', 'Torres', '2000-02-29', 'LW', 7, 'SUSPENDED', 80, 100, 'Spain', 184, 77, 'RIGHT', '2022-01-01'),
('João', 'Félix', '1999-11-10', 'ST', 14, 'AVAILABLE', 82, 95, 'Portugal', 181, 70, 'RIGHT', '2023-09-01');

-- ============================================================================
-- MATCHES - Sample match schedule
-- ============================================================================
INSERT INTO matches (match_date, opponent, venue, competition, goals_for, goals_against, match_status, attendance) VALUES
-- Completed matches
('2024-10-15 21:00:00', 'Real Madrid', 'HOME', 'La Liga', 2, 1, 'COMPLETED', 85000),
('2024-10-22 18:30:00', 'Sevilla FC', 'AWAY', 'La Liga', 1, 1, 'COMPLETED', 42000),
('2024-10-29 21:00:00', 'Bayern Munich', 'HOME', 'Champions League', 3, 0, 'COMPLETED', 88000),
('2024-11-05 16:00:00', 'Valencia CF', 'HOME', 'La Liga', 2, 0, 'COMPLETED', 80000),

-- Upcoming matches
('2025-12-05 21:00:00', 'Atlético Madrid', 'AWAY', 'La Liga', 0, 0, 'SCHEDULED', NULL),
('2025-12-10 18:45:00', 'Inter Milan', 'AWAY', 'Champions League', 0, 0, 'SCHEDULED', NULL),
('2025-12-15 21:00:00', 'Real Betis', 'HOME', 'La Liga', 0, 0, 'SCHEDULED', NULL),
('2025-12-22 16:00:00', 'Girona FC', 'AWAY', 'La Liga', 0, 0, 'SCHEDULED', NULL);

-- ============================================================================
-- TRAINING SESSIONS - Sample training schedule
-- ============================================================================
INSERT INTO training_sessions (session_date, focus, location, duration_minutes, intensity, coach_id) VALUES
-- Past sessions
('2025-11-24 10:00:00', 'Tactical Training - Possession', 'Main Training Ground', 90, 'MEDIUM', 1),
('2025-11-25 10:00:00', 'Physical Conditioning', 'Main Training Ground', 75, 'HIGH', 1),
('2025-11-26 10:00:00', 'Set Pieces Practice', 'Main Training Ground', 60, 'LOW', 1),
('2025-11-27 10:00:00', 'Match Preparation', 'Main Training Ground', 90, 'MEDIUM', 1),
('2025-11-28 10:00:00', 'Recovery Session', 'Indoor Facility', 45, 'LOW', 1),

-- Future sessions
('2025-12-02 10:00:00', 'Team Tactics', 'Main Training Ground', 90, 'MEDIUM', 1),
('2025-12-04 09:00:00', 'Pre-Match Activation', 'Main Training Ground', 45, 'LOW', 1);

-- ============================================================================
-- ATTENDANCE - Sample attendance records for past training sessions
-- ============================================================================
INSERT INTO attendance (player_id, session_id, status, notes) VALUES
-- Session 1 (2024-11-18) - Most players present
(1, 1, 'PRESENT', NULL),
(2, 1, 'PRESENT', NULL),
(3, 1, 'INJURED', 'Knee injury - rehabilitation'),
(4, 1, 'PRESENT', NULL),
(5, 1, 'PRESENT', NULL),
(6, 1, 'PRESENT', NULL),
(7, 1, 'PRESENT', NULL),
(8, 1, 'PRESENT', NULL),
(9, 1, 'PRESENT', NULL),
(10, 1, 'PRESENT', NULL),
(11, 1, 'PRESENT', NULL),
(12, 1, 'PRESENT', NULL),
(13, 1, 'PRESENT', NULL),
(14, 1, 'ABSENT', 'Personal reasons'),
(15, 1, 'PRESENT', NULL),

-- Session 2 (2024-11-19) - High intensity, one late
(1, 2, 'PRESENT', NULL),
(2, 2, 'PRESENT', NULL),
(3, 2, 'INJURED', 'Knee injury - rehabilitation'),
(4, 2, 'PRESENT', NULL),
(5, 2, 'PRESENT', NULL),
(6, 2, 'LATE', 'Traffic'),
(7, 2, 'PRESENT', NULL),
(8, 2, 'PRESENT', NULL),
(9, 2, 'PRESENT', NULL),
(10, 2, 'PRESENT', NULL),
(11, 2, 'PRESENT', NULL),
(12, 2, 'PRESENT', NULL),
(13, 2, 'PRESENT', NULL),
(14, 2, 'PRESENT', NULL),
(15, 2, 'PRESENT', NULL),

-- Session 3 (2024-11-20) - Set pieces, full attendance except injured
(1, 3, 'PRESENT', NULL),
(2, 3, 'PRESENT', NULL),
(3, 3, 'INJURED', 'Knee injury - rehabilitation'),
(4, 3, 'PRESENT', NULL),
(5, 3, 'PRESENT', NULL),
(6, 3, 'PRESENT', NULL),
(7, 3, 'PRESENT', NULL),
(8, 3, 'PRESENT', NULL),
(9, 3, 'PRESENT', NULL),
(10, 3, 'PRESENT', NULL),
(11, 3, 'PRESENT', NULL),
(12, 3, 'PRESENT', NULL),
(13, 3, 'EXCUSED', 'National team duty'),
(14, 3, 'PRESENT', NULL),
(15, 3, 'PRESENT', NULL);

-- ============================================================================
-- PLAYER_MATCH_STATS - Sample performance data for completed matches
-- ============================================================================

-- Match 1: vs Real Madrid (2-1 win)
INSERT INTO player_match_stats (player_id, match_id, minutes_played, goals, assists, rating, shots, shots_on_target, passes_completed, passes_attempted, tackles, interceptions, was_starter) VALUES
(1, 1, 90, 0, 0, 7.5, 0, 0, 25, 28, 0, 0, 1),  -- ter Stegen
(4, 1, 90, 0, 0, 8.0, 0, 0, 65, 72, 5, 3, 1),  -- Koundé
(5, 1, 90, 0, 0, 7.8, 1, 0, 58, 64, 4, 2, 1),  -- Christensen
(6, 1, 90, 0, 1, 8.2, 0, 0, 48, 52, 2, 1, 1),  -- Balde
(7, 1, 90, 0, 0, 7.5, 1, 1, 52, 60, 3, 2, 1),  -- Cancelo
(8, 1, 85, 0, 0, 8.5, 2, 1, 78, 85, 1, 2, 1),  -- de Jong
(9, 1, 90, 1, 0, 8.8, 3, 2, 62, 68, 2, 1, 1),  -- Gavi (GOAL)
(10, 1, 75, 0, 1, 8.0, 1, 1, 71, 76, 0, 1, 1), -- Pedri
(12, 1, 90, 1, 0, 9.0, 4, 3, 45, 52, 1, 0, 1), -- Lewandowski (GOAL)
(13, 1, 90, 0, 0, 7.2, 2, 1, 38, 45, 0, 0, 1), -- Raphinha
(15, 1, 15, 0, 0, 6.5, 0, 0, 8, 10, 0, 0, 0),  -- João Félix (sub)
(11, 1, 15, 0, 0, 6.8, 1, 0, 12, 14, 1, 0, 0); -- Gündoğan (sub)

-- Match 2: vs Sevilla (1-1 draw)
INSERT INTO player_match_stats (player_id, match_id, minutes_played, goals, assists, rating, shots, shots_on_target, passes_completed, passes_attempted, tackles, interceptions, was_starter) VALUES
(1, 2, 90, 0, 0, 6.8, 0, 0, 22, 26, 0, 0, 1),  -- ter Stegen
(4, 2, 90, 0, 0, 7.0, 0, 0, 58, 68, 6, 2, 1),  -- Koundé
(5, 2, 90, 0, 0, 6.8, 0, 0, 54, 62, 3, 1, 1),  -- Christensen
(6, 2, 90, 0, 0, 7.2, 1, 0, 45, 51, 3, 2, 1),  -- Balde
(7, 2, 90, 0, 1, 7.5, 2, 1, 48, 58, 2, 1, 1),  -- Cancelo
(8, 2, 90, 0, 0, 7.8, 1, 0, 72, 81, 2, 3, 1),  -- de Jong
(9, 2, 90, 0, 0, 7.0, 1, 1, 58, 65, 1, 0, 1),  -- Gavi
(10, 2, 90, 1, 0, 7.5, 2, 2, 65, 72, 0, 1, 1), -- Pedri (GOAL)
(12, 2, 90, 0, 0, 6.5, 3, 1, 35, 42, 0, 0, 1), -- Lewandowski
(13, 2, 90, 0, 0, 6.8, 2, 0, 32, 41, 1, 0, 1), -- Raphinha
(15, 2, 90, 0, 0, 7.0, 1, 0, 28, 35, 0, 0, 1); -- João Félix

-- Match 3: vs Bayern Munich (3-0 win)
INSERT INTO player_match_stats (player_id, match_id, minutes_played, goals, assists, rating, shots, shots_on_target, passes_completed, passes_attempted, tackles, interceptions, was_starter) VALUES
(1, 3, 90, 0, 0, 8.5, 0, 0, 28, 30, 0, 0, 1),  -- ter Stegen
(4, 3, 90, 1, 0, 8.8, 2, 1, 68, 74, 4, 4, 1),  -- Koundé (GOAL)
(5, 3, 90, 0, 0, 8.2, 0, 0, 62, 68, 5, 3, 1),  -- Christensen
(6, 3, 90, 0, 1, 8.5, 1, 0, 52, 58, 2, 2, 1),  -- Balde
(7, 3, 90, 0, 1, 8.0, 1, 1, 58, 65, 3, 1, 1),  -- Cancelo
(8, 3, 90, 0, 0, 9.0, 1, 1, 85, 90, 2, 3, 1),  -- de Jong
(9, 3, 90, 1, 1, 9.2, 3, 2, 68, 74, 3, 1, 1),  -- Gavi (GOAL + ASSIST)
(10, 3, 90, 0, 0, 8.8, 2, 1, 78, 82, 1, 2, 1), -- Pedri
(12, 3, 90, 1, 0, 9.5, 5, 4, 42, 48, 0, 0, 1), -- Lewandowski (GOAL)
(13, 3, 90, 0, 0, 7.8, 3, 2, 40, 48, 1, 0, 1), -- Raphinha
(15, 3, 90, 0, 0, 7.5, 2, 1, 32, 38, 0, 0, 1); -- João Félix

-- ============================================================================
-- MATCH EVENTS - Sample timeline entries for completed matches
-- ============================================================================
INSERT INTO match_events (match_id, player_id, event_type, minute, second, description) VALUES
(1, 9, 'GOAL', 23, 12, 'Gavi finishes low into the corner after a through ball'),
(1, 12, 'GOAL', 78, 4, 'Lewandowski converts a penalty to put the team ahead'),
(1, 7, 'YELLOW_CARD', 54, 19, 'João Cancelo booked for a tactical foul'),
(2, 10, 'GOAL', 67, 45, 'Pedri levels the match with a curled effort from the edge'),
(3, 4, 'GOAL', 12, 8, 'Ronald Araújo scores a towering header from a corner'),
(3, NULL, 'SUBSTITUTION', 60, 0, 'Raphinha replaced by Ferran Torres to stretch the pitch');

-- ============================================================================
-- VERIFICATION QUERIES
-- ============================================================================
-- Uncomment to verify data insertion:

-- SELECT 'Users:', COUNT(*) FROM users;
-- SELECT 'Staff:', COUNT(*) FROM staff;
-- SELECT 'Players:', COUNT(*) FROM players;
-- SELECT 'Matches:', COUNT(*) FROM matches;
-- SELECT 'Training Sessions:', COUNT(*) FROM training_sessions;
-- SELECT 'Attendance Records:', COUNT(*) FROM attendance;
-- SELECT 'Player Match Stats:', COUNT(*) FROM player_match_stats;

-- ============================================================================
-- END OF SEED DATA
-- ============================================================================
