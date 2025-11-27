import { useState, useEffect } from 'react';
import { useAuth } from '../hooks/useAuth';
import teamService from '../services/teamService';
import matchService from '../services/matchService';
import Sidebar from '../components/dashboard/Sidebar';
import './Dashboard.css';

const Dashboard = () => {
  const { user } = useAuth();
  const [teams, setTeams] = useState([]);
  const [recentMatches, setRecentMatches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState({
    totalTeams: 0,
    totalPlayers: 0,
    upcomingMatches: 0,
    winRate: 0,
  });

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      const [teamsData, matchesData] = await Promise.all([
        teamService.getUserTeams(),
        matchService.getAllMatches(),
      ]);

      setTeams(teamsData);
      setRecentMatches(matchesData.slice(0, 5));

      setStats({
        totalTeams: teamsData.length,
        totalPlayers: teamsData.reduce((sum, team) => sum + (team.playerCount || 0), 0),
        upcomingMatches: matchesData.filter(m => new Date(m.date) > new Date()).length,
        winRate: calculateWinRate(matchesData),
      });
    } catch (error) {
      console.error('Error loading dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const calculateWinRate = (matches) => {
    if (matches.length === 0) return 0;
    const wins = matches.filter(m => m.result === 'win').length;
    return Math.round((wins / matches.length) * 100);
  };

  if (loading) {
    return (
      <div className="dashboard-container">
        <Sidebar />
        <div className="dashboard-content">
          <div className="loading">Loading dashboard...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="dashboard-container">
      <Sidebar />
      
      <div className="dashboard-content">
        <div className="dashboard-header">
          <h1>Welcome back, {user?.name}! ⚽</h1>
          <p>Here's what's happening with your football clubs</p>
        </div>

        <div className="stats-grid">
          <div className="stat-card">
            <div className="stat-icon">👥</div>
            <div className="stat-info">
              <h3>{stats.totalTeams}</h3>
              <p>Total Teams</p>
            </div>
          </div>

          <div className="stat-card">
            <div className="stat-icon">⚽</div>
            <div className="stat-info">
              <h3>{stats.totalPlayers}</h3>
              <p>Total Players</p>
            </div>
          </div>

          <div className="stat-card">
            <div className="stat-icon">📅</div>
            <div className="stat-info">
              <h3>{stats.upcomingMatches}</h3>
              <p>Upcoming Matches</p>
            </div>
          </div>

          <div className="stat-card">
            <div className="stat-icon">🏆</div>
            <div className="stat-info">
              <h3>{stats.winRate}%</h3>
              <p>Win Rate</p>
            </div>
          </div>
        </div>

        <div className="dashboard-grid">
          <section className="dashboard-section">
            <h2>Your Teams</h2>
            <div className="teams-list">
              {teams.length === 0 ? (
                <p className="empty-state">No teams yet. Create your first team!</p>
              ) : (
                teams.map((team) => (
                  <div key={team.id} className="team-item">
                    <div className="team-info">
                      <h3>{team.name}</h3>
                      <p>{team.playerCount || 0} players</p>
                    </div>
                    <button className="btn btn-small">View Details</button>
                  </div>
                ))
              )}
            </div>
            <button className="btn btn-primary mt-3">+ Create New Team</button>
          </section>

          <section className="dashboard-section">
            <h2>Recent Matches</h2>
            <div className="matches-list">
              {recentMatches.length === 0 ? (
                <p className="empty-state">No matches scheduled</p>
              ) : (
                recentMatches.map((match) => (
                  <div key={match.id} className="match-item">
                    <div className="match-info">
                      <p className="match-teams">{match.homeTeam} vs {match.awayTeam}</p>
                      <p className="match-date">
                        {new Date(match.date).toLocaleDateString()}
                      </p>
                    </div>
                    <div className="match-score">
                      {match.homeScore !== null ? (
                        <span>{match.homeScore} - {match.awayScore}</span>
                      ) : (
                        <span className="upcoming">Upcoming</span>
                      )}
                    </div>
                  </div>
                ))
              )}
            </div>
          </section>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;