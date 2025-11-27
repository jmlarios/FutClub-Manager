-- ============================================================================
-- FutClub Manager Database Schema
-- ============================================================================
-- This script creates all tables for the FutClub Manager system
-- Execute this script to initialize a new database
-- SQLite version 3.x compatible
-- ============================================================================

-- ============================================================================
-- TABLE: users
-- Stores authentication and role information for all system users
-- ============================================================================
CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    role TEXT NOT NULL CHECK(role IN ('COACH', 'ANALYST', 'ADMINISTRATOR')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    is_active INTEGER DEFAULT 1 CHECK(is_active IN (0, 1))
);

-- Index for faster login queries
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- ============================================================================
-- TABLE: staff
-- Stores personal information for staff members (coaches, analysts, admins)
-- Links to users table for authentication
-- ============================================================================
CREATE TABLE IF NOT EXISTS staff (
    staff_id INTEGER PRIMARY KEY AUTOINCREMENT,
    full_name TEXT NOT NULL,
    user_id INTEGER NOT NULL UNIQUE,
    email TEXT UNIQUE,
    phone TEXT,
    hire_date DATE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Index for faster staff lookups
CREATE INDEX IF NOT EXISTS idx_staff_user_id ON staff(user_id);

-- ============================================================================
-- TABLE: players
-- Stores detailed player information including position, status, and ratings
-- ============================================================================
CREATE TABLE IF NOT EXISTS players (
    player_id INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    date_of_birth DATE NOT NULL,
    position TEXT NOT NULL CHECK(position IN ('GK', 'CB', 'LB', 'RB', 'CDM', 'CM', 'CAM', 'LW', 'RW', 'ST')),
    shirt_number INTEGER UNIQUE CHECK(shirt_number BETWEEN 1 AND 99),
    status TEXT DEFAULT 'AVAILABLE' CHECK(status IN ('AVAILABLE', 'INJURED', 'SUSPENDED', 'UNAVAILABLE')),
    overall_rating INTEGER DEFAULT 50 CHECK(overall_rating BETWEEN 1 AND 99),
    fitness_level INTEGER DEFAULT 100 CHECK(fitness_level BETWEEN 0 AND 100),
    injury_details TEXT,
    joined_date DATE DEFAULT CURRENT_DATE,
    contract_end DATE,
    nationality TEXT,
    height_cm INTEGER,
    weight_kg INTEGER,
    preferred_foot TEXT CHECK(preferred_foot IN ('LEFT', 'RIGHT', 'BOTH')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for common queries
CREATE INDEX IF NOT EXISTS idx_players_position ON players(position);
CREATE INDEX IF NOT EXISTS idx_players_status ON players(status);
CREATE INDEX IF NOT EXISTS idx_players_shirt_number ON players(shirt_number);

-- ============================================================================
-- TABLE: matches
-- Stores match information including opponent, venue, and results
-- ============================================================================
CREATE TABLE IF NOT EXISTS matches (
    match_id INTEGER PRIMARY KEY AUTOINCREMENT,
    match_date DATETIME NOT NULL,
    opponent TEXT NOT NULL,
    venue TEXT NOT NULL CHECK(venue IN ('HOME', 'AWAY', 'NEUTRAL')),
    competition TEXT NOT NULL,
    goals_for INTEGER DEFAULT 0 CHECK(goals_for >= 0),
    goals_against INTEGER DEFAULT 0 CHECK(goals_against >= 0),
    match_status TEXT DEFAULT 'SCHEDULED' CHECK(match_status IN ('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'POSTPONED', 'CANCELLED')),
    attendance INTEGER,
    weather TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for match queries
CREATE INDEX IF NOT EXISTS idx_matches_date ON matches(match_date);
CREATE INDEX IF NOT EXISTS idx_matches_status ON matches(match_status);
CREATE INDEX IF NOT EXISTS idx_matches_competition ON matches(competition);

-- ============================================================================
-- TABLE: training_sessions
-- Stores training session information including date, focus, and location
-- ============================================================================
CREATE TABLE IF NOT EXISTS training_sessions (
    session_id INTEGER PRIMARY KEY AUTOINCREMENT,
    session_date DATETIME NOT NULL,
    focus TEXT NOT NULL,
    location TEXT NOT NULL,
    duration_minutes INTEGER DEFAULT 90 CHECK(duration_minutes > 0),
    intensity TEXT CHECK(intensity IN ('LOW', 'MEDIUM', 'HIGH')),
    coach_id INTEGER,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (coach_id) REFERENCES staff(staff_id) ON DELETE SET NULL
);

-- Indexes for training session queries
CREATE INDEX IF NOT EXISTS idx_training_date ON training_sessions(session_date);
CREATE INDEX IF NOT EXISTS idx_training_coach ON training_sessions(coach_id);

-- ============================================================================
-- TABLE: attendance
-- Tracks player attendance at training sessions
-- Links players to training sessions with attendance status
-- ============================================================================
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id INTEGER PRIMARY KEY AUTOINCREMENT,
    player_id INTEGER NOT NULL,
    session_id INTEGER NOT NULL,
    status TEXT NOT NULL DEFAULT 'PRESENT' CHECK(status IN ('PRESENT', 'ABSENT', 'EXCUSED', 'LATE', 'INJURED')),
    notes TEXT,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player_id) REFERENCES players(player_id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES training_sessions(session_id) ON DELETE CASCADE,
    UNIQUE(player_id, session_id)
);

-- Indexes for attendance queries
CREATE INDEX IF NOT EXISTS idx_attendance_player ON attendance(player_id);
CREATE INDEX IF NOT EXISTS idx_attendance_session ON attendance(session_id);
CREATE INDEX IF NOT EXISTS idx_attendance_status ON attendance(status);

-- ============================================================================
-- TABLE: player_match_stats
-- Stores detailed performance statistics for each player in each match
-- ============================================================================
CREATE TABLE IF NOT EXISTS player_match_stats (
    stats_id INTEGER PRIMARY KEY AUTOINCREMENT,
    player_id INTEGER NOT NULL,
    match_id INTEGER NOT NULL,
    minutes_played INTEGER DEFAULT 0 CHECK(minutes_played >= 0 AND minutes_played <= 120),
    goals INTEGER DEFAULT 0 CHECK(goals >= 0),
    assists INTEGER DEFAULT 0 CHECK(assists >= 0),
    rating REAL CHECK(rating >= 1.0 AND rating <= 10.0),
    shots INTEGER DEFAULT 0 CHECK(shots >= 0),
    shots_on_target INTEGER DEFAULT 0 CHECK(shots_on_target >= 0),
    passes_completed INTEGER DEFAULT 0 CHECK(passes_completed >= 0),
    passes_attempted INTEGER DEFAULT 0 CHECK(passes_attempted >= 0),
    tackles INTEGER DEFAULT 0 CHECK(tackles >= 0),
    interceptions INTEGER DEFAULT 0 CHECK(interceptions >= 0),
    yellow_cards INTEGER DEFAULT 0 CHECK(yellow_cards >= 0),
    red_cards INTEGER DEFAULT 0 CHECK(red_cards >= 0),
    fouls_committed INTEGER DEFAULT 0 CHECK(fouls_committed >= 0),
    fouls_won INTEGER DEFAULT 0 CHECK(fouls_won >= 0),
    was_starter INTEGER DEFAULT 1 CHECK(was_starter IN (0, 1)),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player_id) REFERENCES players(player_id) ON DELETE CASCADE,
    FOREIGN KEY (match_id) REFERENCES matches(match_id) ON DELETE CASCADE,
    UNIQUE(player_id, match_id)
);

-- Indexes for performance queries
CREATE INDEX IF NOT EXISTS idx_stats_player ON player_match_stats(player_id);
CREATE INDEX IF NOT EXISTS idx_stats_match ON player_match_stats(match_id);
CREATE INDEX IF NOT EXISTS idx_stats_rating ON player_match_stats(rating);

-- ============================================================================
-- TABLE: match_events
-- Stores detailed chronological events logged during a match
-- ============================================================================
CREATE TABLE IF NOT EXISTS match_events (
    event_id INTEGER PRIMARY KEY AUTOINCREMENT,
    match_id INTEGER NOT NULL,
    player_id INTEGER,
    event_type TEXT NOT NULL,
    minute INTEGER NOT NULL CHECK(minute BETWEEN 0 AND 130),
    second INTEGER DEFAULT 0 CHECK(second BETWEEN 0 AND 59),
    description TEXT,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (match_id) REFERENCES matches(match_id) ON DELETE CASCADE,
    FOREIGN KEY (player_id) REFERENCES players(player_id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_match_events_match ON match_events(match_id);
CREATE INDEX IF NOT EXISTS idx_match_events_type ON match_events(event_type);

-- ============================================================================
-- VIEWS - Useful pre-defined queries
-- ============================================================================

-- View: Active players with basic info
CREATE VIEW IF NOT EXISTS active_players AS
SELECT 
    player_id,
    first_name || ' ' || last_name AS full_name,
    position,
    shirt_number,
    overall_rating,
    status
FROM players
WHERE status != 'UNAVAILABLE'
ORDER BY shirt_number;

-- View: Upcoming matches
CREATE VIEW IF NOT EXISTS upcoming_matches AS
SELECT 
    match_id,
    match_date,
    opponent,
    venue,
    competition
FROM matches
WHERE match_status = 'SCHEDULED' 
  AND match_date >= date('now')
ORDER BY match_date;

-- View: Recent training sessions with attendance count
CREATE VIEW IF NOT EXISTS training_summary AS
SELECT 
    ts.session_id,
    ts.session_date,
    ts.focus,
    ts.location,
    COUNT(a.attendance_id) AS total_attendees,
    SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count,
    SUM(CASE WHEN a.status = 'ABSENT' THEN 1 ELSE 0 END) AS absent_count
FROM training_sessions ts
LEFT JOIN attendance a ON ts.session_id = a.session_id
GROUP BY ts.session_id
ORDER BY ts.session_date DESC;

-- ============================================================================
-- TRIGGERS - Automatic timestamp updates
-- ============================================================================

-- Trigger: Update players.updated_at on any update
CREATE TRIGGER IF NOT EXISTS update_player_timestamp 
AFTER UPDATE ON players
BEGIN
    UPDATE players 
    SET updated_at = CURRENT_TIMESTAMP 
    WHERE player_id = NEW.player_id;
END;

-- Trigger: Update matches.updated_at on any update
CREATE TRIGGER IF NOT EXISTS update_match_timestamp 
AFTER UPDATE ON matches
BEGIN
    UPDATE matches 
    SET updated_at = CURRENT_TIMESTAMP 
    WHERE match_id = NEW.match_id;
END;

-- ============================================================================
-- END OF SCHEMA
-- ============================================================================
