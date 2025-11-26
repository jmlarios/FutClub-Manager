# FutClub Manager - Database Documentation

## Overview
This document describes the database structure and setup for the FutClub Manager application.

## Database Technology
- **DBMS**: SQLite 3.x
- **JDBC Driver**: org.xerial:sqlite-jdbc:3.45.0.0
- **Database File Location**: `data/futclub.db`

## Database Schema

### Tables

#### 1. **users**
Stores authentication and role information for all system users.
- `user_id` (PK): Auto-incrementing unique identifier
- `username`: Unique username for login
- `password_hash`: Hashed password (use BCrypt in production)
- `role`: User role (COACH, ANALYST, ADMINISTRATOR)
- `created_at`: Account creation timestamp
- `last_login`: Last login timestamp
- `is_active`: Active status (1 = active, 0 = inactive)

#### 2. **staff**
Stores personal information for staff members linked to user accounts.
- `staff_id` (PK): Auto-incrementing unique identifier
- `full_name`: Staff member's full name
- `user_id` (FK → users): Links to authentication account
- `email`: Contact email (unique)
- `phone`: Contact phone number
- `hire_date`: Date of hiring

#### 3. **players**
Stores detailed player information including position, status, and ratings.
- `player_id` (PK): Auto-incrementing unique identifier
- `first_name`, `last_name`: Player's name
- `date_of_birth`: Birth date
- `position`: Playing position (GK, CB, LB, RB, CDM, CM, CAM, LW, RW, ST)
- `shirt_number`: Jersey number (1-99, unique)
- `status`: Current status (AVAILABLE, INJURED, SUSPENDED, UNAVAILABLE)
- `overall_rating`: Player rating (1-99)
- `fitness_level`: Fitness percentage (0-100)
- `injury_details`: Notes about current injury
- `joined_date`: Date joined the club
- `contract_end`: Contract expiration date
- `nationality`: Player's nationality
- `height_cm`, `weight_kg`: Physical measurements
- `preferred_foot`: Dominant foot (LEFT, RIGHT, BOTH)

#### 4. **matches**
Stores match information including opponent, venue, and results.
- `match_id` (PK): Auto-incrementing unique identifier
- `match_date`: Date and time of match
- `opponent`: Opposing team name
- `venue`: Match location (HOME, AWAY, NEUTRAL)
- `competition`: Competition name (e.g., "La Liga", "Champions League")
- `goals_for`: Goals scored by the team
- `goals_against`: Goals conceded
- `match_status`: Current status (SCHEDULED, IN_PROGRESS, COMPLETED, POSTPONED, CANCELLED)
- `attendance`: Number of spectators
- `weather`: Weather conditions
- `notes`: Additional notes

#### 5. **training_sessions**
Stores training session information.
- `session_id` (PK): Auto-incrementing unique identifier
- `session_date`: Date and time of session
- `focus`: Training focus/theme
- `location`: Training location
- `duration_minutes`: Session duration in minutes
- `intensity`: Training intensity (LOW, MEDIUM, HIGH)
- `coach_id` (FK → staff): Assigned coach
- `notes`: Session notes

#### 6. **attendance**
Tracks player attendance at training sessions.
- `attendance_id` (PK): Auto-incrementing unique identifier
- `player_id` (FK → players): Player reference
- `session_id` (FK → training_sessions): Session reference
- `status`: Attendance status (PRESENT, ABSENT, EXCUSED, LATE, INJURED)
- `notes`: Additional notes
- **Constraint**: UNIQUE(player_id, session_id) - one record per player per session

#### 7. **player_match_stats**
Stores detailed performance statistics for each player in each match.
- `stats_id` (PK): Auto-incrementing unique identifier
- `player_id` (FK → players): Player reference
- `match_id` (FK → matches): Match reference
- `minutes_played`: Minutes played (0-120)
- `goals`: Goals scored
- `assists`: Assists provided
- `rating`: Performance rating (1.0-10.0)
- `shots`, `shots_on_target`: Shot statistics
- `passes_completed`, `passes_attempted`: Passing statistics
- `tackles`, `interceptions`: Defensive statistics
- `yellow_cards`, `red_cards`: Disciplinary records
- `fouls_committed`, `fouls_won`: Foul statistics
- `was_starter`: Started the match (1) or came from bench (0)
- **Constraint**: UNIQUE(player_id, match_id) - one record per player per match

### Views

1. **active_players**: Quick view of active players with basic information
2. **upcoming_matches**: List of scheduled future matches
3. **training_summary**: Training sessions with attendance counts

### Triggers

1. **update_player_timestamp**: Automatically updates `players.updated_at` on modification
2. **update_match_timestamp**: Automatically updates `matches.updated_at` on modification

## Setup Instructions

### 1. Install Dependencies
```bash
mvn clean install
```

### 2. Initialize Database
The database will be automatically initialized when the application starts for the first time. The following steps occur:
1. Create the `data/` directory if it doesn't exist
2. Execute `schema.sql` to create all tables, views, and triggers
3. Optionally load `seed_data.sql` for testing (controlled by configuration)

### 3. Manual Database Initialization (Optional)
If you need to manually initialize or reset the database:

```bash
# Create data directory
mkdir -p data

# Initialize with SQLite CLI
sqlite3 data/futclub.db < src/main/resources/database/schema.sql
sqlite3 data/futclub.db < src/main/resources/database/seed_data.sql
```

### 4. Verify Installation
Run the test suite to verify database connectivity:
```bash
mvn test
```

## Database Connection

The database connection is managed by the `DatabaseConnection` class:
- Location: `src/main/java/com/futclub/database/DatabaseConnection.java`
- Connection String: `jdbc:sqlite:data/futclub.db`
- Connection is singleton pattern (one connection per application instance)

## Sample Data

The `seed_data.sql` file includes:
- 3 user accounts (coach, analyst, administrator)
- 3 staff members
- 15 players (realistic football squad)
- 8 matches (4 completed, 4 scheduled)
- 7 training sessions
- ~45 attendance records
- ~30 player match statistics records

### Sample Login Credentials
```
Username: coach.smith
Password: password123
Role: COACH

Username: analyst.jones
Password: password123
Role: ANALYST

Username: admin.wilson
Password: password123
Role: ADMINISTRATOR
```
**Note**: In production, implement proper password hashing (BCrypt recommended)!

## DAO Layer Structure

Each entity has a corresponding DAO (Data Access Object):
- **Interface**: Defines contract (e.g., `PlayerDAO.java`)
- **Implementation**: Implements CRUD operations (e.g., `PlayerDAOImpl.java`)

### Standard DAO Methods
Each DAO implements:
- `getById(int id)`: Retrieve single entity by ID
- `getAll()`: Retrieve all entities
- `insert(Entity entity)`: Insert new entity
- `update(Entity entity)`: Update existing entity
- `delete(int id)`: Delete entity by ID

### Additional specialized methods as needed per entity

## Testing

Unit tests are located in `src/test/java/com/futclub/database/dao/`

Run tests:
```bash
mvn test
```

Tests use an in-memory SQLite database (`:memory:`) to avoid affecting the production database.

## Performance Considerations

### Indexes
The schema includes indexes on:
- `users.username` (for login queries)
- `players.position`, `players.status`, `players.shirt_number`
- `matches.match_date`, `matches.match_status`
- `attendance.player_id`, `attendance.session_id`
- Foreign key columns for join performance

### Connection Management
- Use try-with-resources for automatic connection closing
- PreparedStatements prevent SQL injection and improve performance
- Connection pooling can be added later if needed

## Backup and Maintenance

### Backup
Manual backup:
```bash
cp data/futclub.db data/backups/futclub_$(date +%Y%m%d_%H%M%S).db
```

### Reset Database
```bash
rm data/futclub.db
# Application will recreate on next startup
```

## Common Queries

### Get all available players by position
```sql
SELECT * FROM players 
WHERE status = 'AVAILABLE' 
ORDER BY position, overall_rating DESC;
```

### Get top scorers
```sql
SELECT 
    p.first_name || ' ' || p.last_name AS player_name,
    SUM(pms.goals) AS total_goals
FROM players p
JOIN player_match_stats pms ON p.player_id = pms.player_id
GROUP BY p.player_id
ORDER BY total_goals DESC
LIMIT 10;
```

### Get training attendance rate by player
```sql
SELECT 
    p.first_name || ' ' || p.last_name AS player_name,
    COUNT(*) AS sessions_attended,
    SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count,
    ROUND(100.0 * SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) / COUNT(*), 2) AS attendance_rate
FROM players p
JOIN attendance a ON p.player_id = a.player_id
GROUP BY p.player_id
ORDER BY attendance_rate DESC;
```

## Troubleshooting

### Database locked error
- Ensure only one application instance is running
- Check for uncommitted transactions
- Enable WAL mode (Write-Ahead Logging) in database.properties

### Connection timeout
- Check if database file exists and is accessible
- Verify file permissions on `data/` directory
- Check database.properties configuration

### Data integrity issues
- Verify foreign key constraints are enabled
- Check for orphaned records
- Review trigger logic

## Future Enhancements

- Connection pooling implementation
- Automated backup scheduling
- Database migration system (Flyway/Liquibase)
- Query caching layer
- Read replicas for reporting
- Full-text search capabilities

## Contact

For database-related issues, contact the database team member (Omar Issa).
