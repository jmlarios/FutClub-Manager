import { useState, useEffect } from 'react';
import Sidebar from '../components/dashboard/Sidebar';
import teamService from '../services/teamService';
import './Dashboard.css';

const Teams = () => {
  const [teams, setTeams] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    founded: '',
  });

  useEffect(() => {
    loadTeams();
  }, []);

  const loadTeams = async () => {
    try {
      setLoading(true);
      const data = await teamService.getUserTeams();
      setTeams(data);
    } catch (error) {
      console.error('Error loading teams:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await teamService.createTeam(formData);
      setShowForm(false);
      setFormData({ name: '', description: '', founded: '' });
      loadTeams();
    } catch (error) {
      console.error('Error creating team:', error);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this team?')) {
      try {
        await teamService.deleteTeam(id);
        loadTeams();
      } catch (error) {
        console.error('Error deleting team:', error);
      }
    }
  };

  return (
    <div className="dashboard-container">
      <Sidebar />
      <div className="dashboard-content">
        <div className="dashboard-header">
          <h1>Teams Management 👥</h1>
          <p>Manage your football teams</p>
        </div>

        <button 
          onClick={() => setShowForm(!showForm)} 
          className="btn btn-primary mb-3"
        >
          {showForm ? 'Cancel' : '+ Create New Team'}
        </button>

        {showForm && (
          <div className="card mb-3">
            <h3 className="mb-3">Create New Team</h3>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label htmlFor="name">Team Name</label>
                <input
                  type="text"
                  id="name"
                  value={formData.name}
                  onChange={(e) => setFormData({...formData, name: e.target.value})}
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="description">Description</label>
                <textarea
                  id="description"
                  value={formData.description}
                  onChange={(e) => setFormData({...formData, description: e.target.value})}
                  rows="3"
                />
              </div>
              <div className="form-group">
                <label htmlFor="founded">Founded Year</label>
                <input
                  type="number"
                  id="founded"
                  value={formData.founded}
                  onChange={(e) => setFormData({...formData, founded: e.target.value})}
                />
              </div>
              <button type="submit" className="btn btn-primary">Create Team</button>
            </form>
          </div>
        )}

        {loading ? (
          <div className="loading">Loading teams...</div>
        ) : (
          <div className="dashboard-grid">
            {teams.length === 0 ? (
              <p className="empty-state">No teams yet. Create your first team!</p>
            ) : (
              teams.map((team) => (
                <div key={team.id} className="card">
                  <h3>{team.name}</h3>
                  <p className="text-secondary mb-2">{team.description || 'No description'}</p>
                  <p className="text-secondary mb-3">
                    Founded: {team.founded || 'N/A'} | Players: {team.playerCount || 0}
                  </p>
                  <div style={{ display: 'flex', gap: '0.5rem' }}>
                    <button className="btn btn-small btn-secondary">View Details</button>
                    <button 
                      className="btn btn-small btn-danger"
                      onClick={() => handleDelete(team.id)}
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

export default Teams;