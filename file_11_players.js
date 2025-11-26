import { useState, useEffect } from 'react';
import Sidebar from '../components/dashboard/Sidebar';
import playerService from '../services/playerService';
import teamService from '../services/teamService';
import './Dashboard.css';

const Players = () => {
  const [players, setPlayers] = useState([]);
  const [teams, setTeams] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    position: '',
    jerseyNumber: '',
    teamId: '',
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [playersData, teamsData] = await Promise.all([
        playerService.getAllPlayers(),
        teamService.getUserTeams(),
      ]);
      setPlayers(playersData);
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
      await playerService.createPlayer(formData);
      setShowForm(false);
      setFormData({ name: '', position: '', jerseyNumber: '', teamId: '' });
      loadData();
    } catch (error) {
      console.error('Error creating player:', error);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this player?')) {
      try {
        await playerService.deletePlayer(id);
        loadData();
      } catch (error) {
        console.error('Error deleting player:', error);
      }
    }
  };

  return (
    <div className="dashboard-container">
      <Sidebar />
      <div className="dashboard-content">
        <div className="dashboard-header">
          <h1>Players Management ⚽</h1>
          <p>Manage your team players</p>
        </div>

        <button 
          onClick={() => setShowForm(!showForm)} 
          className="btn btn-primary mb-3"
        >
          {showForm ? 'Cancel' : '+ Add New Player'}
        </button>

        {showForm && (
          <div className="card mb-3">
            <h3 className="mb-3">Add New Player</h3>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label htmlFor="name">Player Name</label>
                <input
                  type="text"
                  id="name"
                  value={formData.name}
                  onChange={(e) => setFormData({...formData, name: e.target.value})}
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="position">Position</label>
                <select
                  id="position"
                  value={formData.position}
                  onChange={(e) => setFormData({...formData, position: e.target.value})}
                  required
                >
                  <option value="">Select Position</option>
                  <option value="Goalkeeper">Goalkeeper</option>
                  <option value="Defender">Defender</option>
                  <option value="Midfielder">Midfielder</option>
                  <option value="Forward">Forward</option>
                </select>
              </div>
              <div className="form-group">
                <label htmlFor="jerseyNumber">Jersey Number</label>
                <input
                  type="number"
                  id="jerseyNumber"
                  value={formData.jerseyNumber}
                  onChange={(e) => setFormData({...formData, jerseyNumber: e.target.value})}
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="teamId">Team</label>
                <select
                  id="teamId"
                  value={formData.teamId}
                  onChange={(e) => setFormData({...formData, teamId: e.target.value})}
                  required
                >
                  <option value="">Select Team</option>
                  {teams.map((team) => (
                    <option key={team.id} value={team.id}>{team.name}</option>
                  ))}
                </select>
              </div>
              <button type="submit" className="btn btn-primary">Add Player</button>
            </form>
          </div>
        )}

        {loading ? (
          <div className="loading">Loading players...</div>
        ) : (
          <div className="dashboard-grid">
            {players.length === 0 ? (
              <p className="empty-state">No players yet. Add your first player!</p>
            ) : (
              players.map((player) => (
                <div key={player.id} className="card">
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
                    <div>
                      <h3>{player.name}</h3>
                      <p className="text-secondary">#{player.jerseyNumber} - {player.position}</p>
                      <p className="text-secondary mt-1">Team: {player.teamName || 'N/A'}</p>
                    </div>
                    <div style={{ fontSize: '2rem' }}>⚽</div>
                  </div>
                  <div style={{ display: 'flex', gap: '0.5rem', marginTop: '1rem' }}>
                    <button className="btn btn-small btn-secondary">View Stats</button>
                    <button 
                      className="btn btn-small btn-danger"
                      onClick={() => handleDelete(player.id)}
                    >
                      Delete
                    </button>
                  </div>
                </div>
              ))
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default Players;