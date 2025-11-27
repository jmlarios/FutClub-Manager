import { useState } from 'react';
import Sidebar from '../components/dashboard/Sidebar';
import { useAuth } from '../hooks/useAuth';
import './Dashboard.css';

const Profile = () => {
  const { user } = useAuth();
  const [editing, setEditing] = useState(false);
  const [formData, setFormData] = useState({
    name: user?.name || '',
    email: user?.email || '',
  });
  const [message, setMessage] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setMessage('Profile updated successfully!');
      setEditing(false);
      setTimeout(() => setMessage(''), 3000);
    } catch (error) {
      setMessage('Error updating profile');
      console.error('Error updating profile:', error);
    }
  };

  return (
    <div className="dashboard-container">
      <Sidebar />
      <div className="dashboard-content">
        <div className="dashboard-header">
          <h1>Profile Settings 👤</h1>
          <p>Manage your account information</p>
        </div>

        {message && (
          <div className={message.includes('Error') ? 'error-message' : 'success-message'}>
            {message}
          </div>
        )}

        <div className="card" style={{ maxWidth: '600px' }}>
          <div style={{ 
            display: 'flex', 
            alignItems: 'center', 
            gap: '1.5rem',
            marginBottom: '2rem',
            paddingBottom: '2rem',
            borderBottom: '1px solid var(--border-color)'
          }}>
            <div style={{ 
              width: '80px', 
              height: '80px', 
              borderRadius: '50%', 
              backgroundColor: 'var(--primary-color)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontSize: '2rem',
              color: 'white'
            }}>
              {user?.name?.charAt(0).toUpperCase()}
            </div>
            <div>
              <h2>{user?.name}</h2>
              <p className="text-secondary">{user?.email}</p>
              <p className="text-secondary" style={{ fontSize: '0.875rem', marginTop: '0.25rem' }}>
                Member since {new Date().toLocaleDateString()}
              </p>
            </div>
          </div>

          {!editing ? (
            <div>
              <div className="form-group">
                <label>Full Name</label>
                <p style={{ 
                  padding: '0.75rem', 
                  backgroundColor: 'var(--bg-secondary)',
                  borderRadius: '0.5rem',
                  marginTop: '0.5rem'
                }}>
                  {user?.name}
                </p>
              </div>

              <div className="form-group">
                <label>Email Address</label>
                <p style={{ 
                  padding: '0.75rem', 
                  backgroundColor: 'var(--bg-secondary)',
                  borderRadius: '0.5rem',
                  marginTop: '0.5rem'
                }}>
                  {user?.email}
                </p>
              </div>

              <button 
                onClick={() => setEditing(true)} 
                className="btn btn-primary"
              >
                Edit Profile
              </button>
            </div>
          ) : (
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label htmlFor="name">Full Name</label>
                <input
                  type="text"
                  id="name"
                  value={formData.name}
                  onChange={(e) => setFormData({...formData, name: e.target.value})}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="email">Email Address</label>
                <input
                  type="email"
                  id="email"
                  value={formData.email}
                  onChange={(e) => setFormData({...formData, email: e.target.value})}
                  required
                />
              </div>

              <div style={{ display: 'flex', gap: '0.5rem' }}>
                <button type="submit" className="btn btn-primary">
                  Save Changes
                </button>
                <button 
                  type="button"
                  onClick={() => {
                    setEditing(false);
                    setFormData({ name: user?.name || '', email: user?.email || '' });
                  }}
                  className="btn btn-secondary"
                >
                  Cancel
                </button>
              </div>
            </form>
          )}
        </div>

        <div className="card mt-4" style={{ maxWidth: '600px' }}>
          <h3 style={{ marginBottom: '1rem', color: 'var(--text-primary)' }}>Account Statistics</h3>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
            <div style={{ 
              padding: '1rem',
              backgroundColor: 'var(--bg-secondary)',
              borderRadius: '0.5rem',
              textAlign: 'center'
            }}>
              <div style={{ fontSize: '2rem', fontWeight: '700', color: 'var(--primary-color)' }}>
                0
              </div>
              <div style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
                Teams Created
              </div>
            </div>
            <div style={{ 
              padding: '1rem',
              backgroundColor: 'var(--bg-secondary)',
              borderRadius: '0.5rem',
              textAlign: 'center'
            }}>
              <div style={{ fontSize: '2rem', fontWeight: '700', color: 'var(--primary-color)' }}>
                0
              </div>
              <div style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
                Matches Scheduled
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Profile;