// src/components/NotificationsPage.js
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast, ToastContainer } from 'react-toastify';
import { getNotifications } from '../api';
import { parseDate } from '../utils/dateUtils';
import './TaskPage.css';

const NotificationsPage = () => {
    const [notifications, setNotifications] = useState([]);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const fetchNotifications = async () => {
            try {
                const response = await getNotifications();
                setNotifications(response.data);
            } catch (err) {
                const errorMessage = err.response?.data?.error || err.message || 'Unknown error';
                setError(`Failed to load notifications: ${errorMessage}`);
                console.error('Fetch error:', err.response?.data || err);
                toast.error(`Failed to load notifications: ${errorMessage}`);
            }
        };

        fetchNotifications();
    }, [navigate]);

    return (
        <div className="task-page">
            <h1>Notifications</h1>
            {error && <p className="error">{error}</p>}
            {notifications.length > 0 ? (
                <ul className="task-list">
                    {notifications.map((notification, index) => (
                        <li key={index} className="task-item">
                            {notification.message} ({notification.notificationType}) -
                            {parseDate(notification.at).toLocaleString()}
                        </li>
                    ))}
                </ul>
            ) : (
                <p>No notifications yet.</p>
            )}
            <ToastContainer />
        </div>
    );
};

export default NotificationsPage;