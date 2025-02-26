import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast, ToastContainer } from 'react-toastify';
import { getAssignedToMe, getTaskStats, getUpcomingTasks, getNotifications, logout } from '../api';
import { parseDate } from '../utils/dateUtils';
import './styles/Dashboard.css';

const Dashboard = () => {
    const [tasks, setTasks] = useState([]);
    const [stats, setStats] = useState(null);
    const [upcomingTasks, setUpcomingTasks] = useState([]);
    const [notifications, setNotifications] = useState([]);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [tasksRes, statsRes, upcomingRes, notificationsRes] = await Promise.all([
                    getAssignedToMe(),
                    getTaskStats(),
                    getUpcomingTasks(),
                    getNotifications(),
                ]);
                setTasks(tasksRes.data);
                setStats(statsRes.data);
                setUpcomingTasks(upcomingRes.data instanceof Array ? upcomingRes.data : []);
                setNotifications(notificationsRes.data);
            } catch (err) {
                const errorMessage = err.response?.data?.error || err.message || 'Unknown error';
                setError(`Failed to load data: ${errorMessage}`);
                console.error('Fetch error:', err.response?.data || err);
                toast.error(`Failed to load data: ${errorMessage}`);
            }
        };

        fetchData();
    }, [navigate]);

    const handleLogout = () => {
        toast(
            <div className="logout-toast">
                <p>Are you sure you want to logout?</p>
                <div className="logout-toast-buttons">
                    <button
                        className="logout-confirm-btn"
                        onClick={async () => {
                            try {
                                const response = await logout();
                                console.log('Logout Response:', response.data);
                                localStorage.clear();
                                navigate('/login');
                                toast.success('Logged out successfully!');
                            } catch (err) {
                                console.error('Logout failed:', err);
                                localStorage.clear();
                                navigate('/login');
                                toast.error('Logout failed, but session cleared.');
                            }
                        }}
                    >
                        Yes
                    </button>
                    <button
                        className="logout-cancel-btn"
                        onClick={() => toast.dismiss()}
                    >
                        Cancel
                    </button>
                </div>
            </div>,
            { position: 'top-center', autoClose: false, closeOnClick: false, draggable: false }
        );
    };

    return (
        <div className="dashboard">
            <header>
                <h1>Welcome to TaskFlow, {localStorage.getItem('username')}</h1>
                <button className="logout-btn" onClick={handleLogout}>
                    Logout
                </button>
            </header>

            {error && <p className="error">{error}</p>}

            <div className="dashboard-grid">
                <section className="dashboard-card stats">
                    <h2>Your Task Stats</h2>
                    {stats ? (
                        <div className="stats-grid">
                            <div className="stat-item"><span>To Do:</span> {stats.todo}</div>
                            <div className="stat-item"><span>In Progress:</span> {stats.inProgress}</div>
                            <div className="stat-item"><span>Completed:</span> {stats.completed}</div>
                            <div className="stat-item"><span>Overdue:</span> {stats.overdue}</div>
                        </div>
                    ) : (
                        <p>Loading stats...</p>
                    )}
                </section>

                <section className="dashboard-card tasks">
                    <h2>Tasks Assigned to You</h2>
                    {tasks.length > 0 ? (
                        <ul>
                            {tasks.slice(0, 5).map((task) => (
                                <li key={task.taskId}>
                                    <strong>{task.title}</strong> - <span className={`status-${task.status.toLowerCase()}`}>{task.status}</span>
                                    <br />
                                    <span>Due: {parseDate(task.dueDate).toLocaleDateString()}</span>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p>No tasks assigned yet.</p>
                    )}
                    <button className="view-all-btn" onClick={() => navigate('/assigned-to-me')}>View All</button>
                </section>

                <section className="dashboard-card upcoming">
                    <h2>Upcoming Tasks</h2>
                    {upcomingTasks.length > 0 ? (
                        <ul>
                            {upcomingTasks.slice(0, 5).map((task) => (
                                <li key={task.taskId}>
                                    <strong>{task.title}</strong> - Due: {parseDate(task.dueDate).toLocaleDateString()}
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p>No upcoming tasks.</p>
                    )}
                    <button className="view-all-btn" onClick={() => navigate('/upcoming-tasks')}>View All</button>
                </section>

                <section className="dashboard-card notifications">
                    <h2>Recent Notifications</h2>
                    {notifications.length > 0 ? (
                        <ul>
                            {notifications.slice(0, 5).map((notification, index) => (
                                <li key={index}>
                                    {notification.message} ({notification.notificationType})
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p>No notifications yet.</p>
                    )}
                    <button className="view-all-btn" onClick={() => navigate('/notifications')}>View All</button>
                </section>
            </div>

            <ToastContainer />
        </div>
    );
};

export default Dashboard;