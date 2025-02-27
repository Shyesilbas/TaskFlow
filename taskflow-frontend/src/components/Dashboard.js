import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { getAssignedToMe, getTaskStats, getUpcomingTasks, getNotifications, logout } from '../api';
import { parseDate } from '../utils/dateUtils';
import { FiLogOut, FiCalendar, FiCheckCircle, FiList, FiBell } from 'react-icons/fi';
import './styles/Dashboard.css';

const Dashboard = () => {
    const [tasks, setTasks] = useState([]);
    const [stats, setStats] = useState(null);
    const [upcomingTasks, setUpcomingTasks] = useState([]);
    const [notifications, setNotifications] = useState([]);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();
    const username = localStorage.getItem('username') || 'User';

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true);
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
                setError('');
            } catch (err) {
                const errorMessage = err.response?.data?.error || err.message || 'Unknown error';
                setError(`Failed to load data: ${errorMessage}`);
                console.error('Fetch error:', err.response?.data || err);
                toast.error(`Failed to load data: ${errorMessage}`);
            } finally {
                setLoading(false);
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
                                await logout();
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

    const renderStatusLabel = (status) => {
        const statusMap = {
            'todo': 'To Do',
            'inprogress': 'In Progress',
            'in_progress': 'In Progress',
            'done': 'Done',
            'completed': 'Completed'
        };

        const normalizedStatus = status.toLowerCase().replace(/\s+/g, '');
        return <span className={`status-${normalizedStatus}`}>{statusMap[normalizedStatus] || status}</span>;
    };

    const formatDueDate = (dateString) => {
        const date = parseDate(dateString);
        const now = new Date();
        const isOverdue = date < now && date.toDateString() !== now.toDateString();
        const isToday = date.toDateString() === now.toDateString();

        return (
            <span className={`due-date ${isOverdue ? 'overdue' : ''} ${isToday ? 'today' : ''}`}>
                <FiCalendar className="icon" />
                {isToday ? 'Today' : date.toLocaleDateString()}
            </span>
        );
    };

    return (
        <div className="dashboard">
            <header>
                <div className="header-left">
                    <h1>TaskFlow</h1>
                    <span className="welcome-message">Welcome, {username}</span>
                </div>
                <button className="logout-btn" onClick={handleLogout}>
                    <FiLogOut className="icon" /> Logout
                </button>
            </header>

            {error && <div className="error">{error}</div>}

            <div className="dashboard-grid">
                <section className="dashboard-card stats-card">
                    <h2><FiCheckCircle className="card-icon" /> Task Overview</h2>
                    {loading ? (
                        <div className="loading-spinner"></div>
                    ) : stats ? (
                        <div className="stats-grid">
                            <div className="stat-item">
                                <span className="stat-value">{stats.todo || 0}</span>
                                <span className="stat-label">To Do</span>
                            </div>
                            <div className="stat-item">
                                <span className="stat-value">{stats.inProgress || 0}</span>
                                <span className="stat-label">In Progress</span>
                            </div>
                            <div className="stat-item">
                                <span className="stat-value">{stats.completed || 0}</span>
                                <span className="stat-label">Completed</span>
                            </div>
                            <div className="stat-item">
                                <span className="stat-value">{stats.overdue || 0}</span>
                                <span className="stat-label">Overdue</span>
                            </div>
                        </div>
                    ) : (
                        <p className="empty-state">No stats available</p>
                    )}
                </section>

                <section className="dashboard-card tasks-card">
                    <h2><FiList className="card-icon" /> My Tasks</h2>
                    {loading ? (
                        <div className="loading-spinner"></div>
                    ) : tasks.length > 0 ? (
                        <ul className="task-list">
                            {tasks.slice(0, 5).map((task) => (
                                <li key={task.taskId} className="task-item" onClick={() => navigate(`/my-tasks`)}>
                                    <div className="task-header">
                                        <h3 className="task-title">{task.title}</h3>
                                        {renderStatusLabel(task.status)}
                                    </div>
                                    <div className="task-footer">
                                        {formatDueDate(task.dueDate)}
                                        {task.priority && <span className={`priority priority-${task.priority.toLowerCase()}`}>{task.priority}</span>}
                                    </div>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p className="empty-state">No tasks assigned yet</p>
                    )}
                    <button className="view-all-btn" onClick={() => navigate('/my-tasks')}>View All Tasks</button>
                </section>

                <section className="dashboard-card upcoming-card">
                    <h2><FiCalendar className="card-icon" /> Upcoming Tasks</h2>
                    {loading ? (
                        <div className="loading-spinner"></div>
                    ) : upcomingTasks.length > 0 ? (
                        <ul className="task-list">
                            {upcomingTasks.slice(0, 5).map((task) => (
                                <li key={task.taskId} className="task-item" onClick={() => navigate(`/my-tasks`)}>
                                    <div className="task-header">
                                        <h3 className="task-title">{task.title}</h3>
                                    </div>
                                    <div className="task-footer">
                                        {formatDueDate(task.dueDate)}
                                    </div>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p className="empty-state">No upcoming tasks</p>
                    )}
                    <button className="view-all-btn" onClick={() => navigate('/my-tasks')}>View Calendar</button>
                </section>

                <section className="dashboard-card notifications-card">
                    <h2><FiBell className="card-icon" /> Notifications</h2>
                    {loading ? (
                        <div className="loading-spinner"></div>
                    ) : notifications.length > 0 ? (
                        <ul className="notification-list">
                            {notifications.slice(0, 5).map((notification, index) => (
                                <li key={index} className={`notification-item ${notification.read ? 'read' : 'unread'}`}>
                                    <div className="notification-content">
                                        {notification.message}
                                    </div>
                                    <div className="notification-meta">
                                        <span className="notification-time">
                                            {notification.timestamp ? parseDate(notification.timestamp).toLocaleString() : 'Just now'}
                                        </span>
                                        <span className="notification-type">{notification.notificationType}</span>
                                    </div>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p className="empty-state">No notifications yet</p>
                    )}
                    <button className="view-all-btn" onClick={() => navigate('/notifications')}>View All Notifications</button>
                </section>
            </div>

            <ToastContainer position="top-right" autoClose={5000} hideProgressBar={false} newestOnTop pauseOnHover />
        </div>
    );
};

export default Dashboard;