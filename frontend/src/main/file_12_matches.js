import { useState, useEffect } from 'react';
import Sidebar from '../components/dashboard/Sidebar';
import matchService from '../services/matchService';
import teamService from '../services/teamService';
import './Dashboard.css';

const Matches = () => {
  const [matches, setMatches] = useState([]);
  const [teams, setTeams] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    homeTeamId: '',
    awayTeamId: '',
    date: '',
    location: '',
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [matchesData, teamsData] = await Promise.all([
        matchService.getAllMatches(),
        teamService.getUserTeams(),
      ]);
      setMatches(matchesData);
      setTeams(teamsData);
    } catch (error) {
      console.error('Error loading data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await matchService.createMatch(formData);
      setShowForm(false);
      setFormData({ homeTeamId: '', awayTeamId: '', date: '', location: '' });
      loadData();
    } catch (error) {
      console.error('Error creating match:', error);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this match?')) {
      try {
        await matchService.deleteMatch(id);
        loadData();
      } catch (error) {
        console.error('Error deleting match:', error);
      }
    }
  };

  const getMatchStatus = (match) => {
    const matchDate = new Date(match.date);
    const now = new Date();
    
    if (match.homeScore !== null && match.awayScore !== null) {
      return 'completed';
    } else if (matchDate > now) {
      return 'upcoming';
    } else {
      return 'in-progress';
    }
  };

  return (
    <div className="dashboard-container">
      <Sidebar />
      <div className="dashboard-content">
        <div className="dashboard-header">
          <h1>Matches Management 🏆</h1>
          <p>Schedule and manage your team matches</p>
        </div>

        <button 
          onClick={() => setShowForm(!showForm)} 
          className="btn btn-primary mb-3"
        >
          {showForm ? 'Cancel' : '+ Schedule New Match'}
        </button>

        {showForm && (
          <div className="card mb-3">
            <h3 className="mb-3">Schedule New Match</h3>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label htmlFor="homeTeamId">Home Team</label>
                <select
                  id="homeTeamId"
                  value={formData.homeTeamId}
                  onChange={(e) => setFormData({...formData, homeTeamId: e.target.value})}
                  required
                >
                  <option value="">Select Home Team</option>
                  {teams.map((team) => (
                    <option key={team.id} value={team.id}>{team.name}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label htmlFor="awayTeamId">Away Team</label>
                <select
                  id="awayTeamId"
                  value={formData.awayTeamId}
                  onChange={(e) => setFormData({...formData, awayTeamId: e.target.value})}
                  required
                >
                  <option value="">Select Away Team</option>
                  {teams.map((team) => (
                    <option key={team.id} value={team.id}>{team.name}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label htmlFor="date">Match Date & Time</label>
                <input
                  type="datetime-local"
                  id="date"
                  value={formData.date}
                  onChange={(e) => setFormData({...formData, date: e.target.value})}
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="location">Location</label>
                <input
                  type="text"
                  id="location"
                  value={formData.location}
                  onChange={(e) => setFormData({...formData, location: e.target.value})}
                  placeholder="Stadium name or address"
                />
              </div>
              <button type="submit" className="btn btn-primary">Schedule Match</button>
            </form>
          </div>
        )}

        {loading ? (
          <div className="loading">Loading matches...</div>
        ) : (
          <div className="dashboard-grid">
            {matches.length === 0 ? (
              <p className="empty-state">No matches scheduled. Create your first match!</p>
            ) : (
              matches.map((match) => {
                const status = getMatchStatus(match);
                return (
                  <div key={match.id} className="card">
                    <div style={{ marginBottom: '1rem' }}>
                      <div style={{ 
                        display: 'inline-block', 
                        padding: '0.25rem 0.75rem', 
                        borderRadius: '1rem',
                        fontSize: '0.75rem',
                        fontWeight: '600',
                        backgroundColor: status === 'completed' ? '#d1fae5' : status === 'upcoming' ? '#dbeafe' : '#fef3c7',
                        color: status === 'completed' ? '#065f46' : status === 'upcoming' ? '#1e40af' : '#92400e'
                      }}>
                        {status.toUpperCase()}
                      </div>
                    </div>
                    <h3>{match.homeTeam} vs {match.awayTeam}</h3>
                    <p className="text-secondary mt-2">
                      📅 {new Date(match.date).toLocaleString()}
                    </p>
                    {match.location && (
                      <p className="text-secondary">📍 {match.location}</p>
                    )}
                    {status === 'completed' && (
                      <div style={{ 
                        fontSize: '1.5rem', 
                        fontWeight: '700', 
                        margin: '1rem 0',
                        textAlign: 'center'
                      }}>
                        {match.homeScore} - {match.awayScore}
                      </div>
                    )}
                    <div style={{ display: 'flex', gap: '0.5rem', marginTop: '1rem' }}>
                      <button className="btn btn-small btn-secondary">
                        {status === 'completed' ? 'View Details' : 'Update Score'}
                      </button>
                      <button 
                        className="btn btn-small btn-danger"
                        onClick={() => handleDelete(match.id)}
                      >
                        Delete
                      </button>
                    </div>
                  </div>
                );
              })
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default Matches;