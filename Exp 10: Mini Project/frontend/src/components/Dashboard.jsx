import React, { useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import FileBrowser from './FileBrowser';
import { useNavigate } from 'react-router-dom';

const Dashboard = () => {
    const { user, logout } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <div className="dashboard-container">
            <nav className="navbar">
                <div className="navbar-brand">Drive Clone</div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                    <span style={{ fontWeight: 500 }}>{user?.name}</span>
                    <button onClick={handleLogout} className="btn btn-outline">Logout</button>
                </div>
            </nav>
            <main className="main-content">
                <FileBrowser />
            </main>
        </div>
    );
};

export default Dashboard;
