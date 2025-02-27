// src/components/NotificationsPage.js
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast, ToastContainer } from 'react-toastify';
import { getNotifications, deleteNotifications } from '../api';
import { parseDate } from '../utils/dateUtils';
import './styles/TaskPage.css';

const NotificationsPage = () => {
    const [notifications, setNotifications] = useState([]);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const fetchNotifications = async () => {
            try {
                const response = await getNotifications();
                setNotifications(response.data);
                updateNotificationBadge(response.data, true);
            } catch (err) {
                const errorMessage = err.response?.data?.error || err.message || 'Unknown error';
                setError(errorMessage);
                console.error('Fetch error:', err.response?.data || err);
                toast.error(errorMessage);
            }
        };

        fetchNotifications();
    }, [navigate]);

    const handleDeleteNotifications = async () => {
        try {
            await deleteNotifications();
            setNotifications([]);
            toast.success('All notifications deleted successfully!');
            updateNotificationBadge([]);

            setTimeout(async () => {
                const response = await getNotifications();
                setNotifications(response.data);
                updateNotificationBadge(response.data);
            }, 1000);
        } catch (err) {
            toast.error('Failed to delete notifications.');
            console.error(err);
        }
    };

    const updateNotificationBadge = (notifications, isViewed = false) => {
        const hasNewNotification = notifications.length > 0 && !isViewed;
        localStorage.setItem('hasNewNotification', hasNewNotification ? 'true' : 'false');
        window.dispatchEvent(new Event('storage'));
    };

    return (
        <div className="notifications-page">
            <div className="notifications-header">
                <h1>Notifications</h1>
                {notifications.length > 0 && (
                    <button
                        onClick={handleDeleteNotifications}
                        className="delete-notifications-btn"
                    >
                        Clear All
                    </button>
                )}
            </div>
            {error && <p className="error">{error}</p>}
            {notifications.length > 0 ? (
                <div className="notification-list">
                    {notifications.map((notification, index) => (
                        <div key={index} className="notification-item">
                            <div className="notification-content">
                                <span className={`notification-type type-${notification.notificationType.toLowerCase()}`}>
                                    {notification.notificationType}
                                </span>
                                <p className="notification-message">{notification.message}</p>
                            </div>
                            <span className="notification-timestamp">
                                {parseDate(notification.at).toLocaleString()}
                            </span>
                        </div>
                    ))}
                </div>
            ) : (
                <div className="no-notifications">
                    <p>No notifications yet.</p>
                </div>
            )}
            <ToastContainer />
        </div>
    );
};

export default NotificationsPage;