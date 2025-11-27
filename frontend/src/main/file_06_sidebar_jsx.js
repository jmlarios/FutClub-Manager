import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import './Sidebar.css';

const Sidebar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const isActive = (path) => location.pathname === path;

  return (
    <div className="sidebar">
      <div className="sidebar-header">
        <h2>⚽ FutClub</h2>
        <p className="sidebar-user">{user?.name}</p>
      </div>

      <nav className="sidebar-nav">
        <Link 
          to="/dashboard" 
          className={`sidebar-link ${isActive('/dashboard') ? 'active' : ''}`}
        >
          <span className="icon">📊</span>
          Dashboard
        </Link>

        <Link 
          to="/teams" 
          className={`sidebar-link ${isActive('/teams') ? 'active' : ''}`}
        >
          <span className="icon">👥</span>
          Teams
        </Link>

        <Link 
          to="/players" 
          className={`sidebar-link ${isActive('/players') ? 'active' : ''}`}
        >
          <span className="icon">⚽</span>
          Players
        </Link>

        <Link 
          to="/matches" 
          className={`sidebar-link ${isActive('/matches') ? 'active' : ''}`}
        >
          <span className="icon">🏆</span>
          Matches
        </Link>

        <Link 
          to="/profile" 
          className={`sidebar-link ${isActive('/profile') ? 'active' : ''}`}
        >
          <span className="icon">👤</span>
          Profile
        </Link>
      </nav>

      <div className="sidebar-footer">
        <button onClick={handleLogout} className="sidebar-link logout-btn">
          <span className="icon">🚪</span>
          Logout
        </button>
      </div>
    </div>
  );
};

export default Sidebar;