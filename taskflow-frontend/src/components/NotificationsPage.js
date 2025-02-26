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
            } catch (err) {
                const errorMessage = err.response?.data?.error || err.message || 'Unknown error';
                setError(`Failed to load notifications: ${errorMessage}`);
                console.error('Fetch error:', err.response?.data || err);
                toast.error(`Failed to load notifications: ${errorMessage}`);
            }
        };

        fetchNotifications();
    }, [navigate]);

    const handleDeleteNotifications = async () => {
        try {
            await deleteNotifications();
            setNotifications([]);
            toast.success('All notifications deleted successfully!');

            setTimeout(async () => {
                const response = await getNotifications();
                setNotifications(response.data);
            }, 1000);
        } catch (err) {
        }
    };

    return (
        <div className="task-page">
            <h1>Notifications</h1>
            {error && <p className="error">{error}</p>}
            {notifications.length > 0 ? (
                <>
                    <ul className="task-list">
                        {notifications.map((notification, index) => (
                            <li key={index} className="task-item">
                                {notification.message} ({notification.notificationType}) -
                                {parseDate(notification.at).toLocaleString()}
                            </li>
                        ))}
                    </ul>
                    <button
                        onClick={handleDeleteNotifications}
                        style={{
                            padding: '8px 16px',
                            backgroundColor: '#f44336',
                            color: 'white',
                            border: 'none',
                            borderRadius: '4px',
                            cursor: 'pointer',
                            marginTop: '10px',
                            fontSize: '14px',
                        }}
                        onMouseOver={(e) => (e.target.style.backgroundColor = '#da190b')}
                        onMouseOut={(e) => (e.target.style.backgroundColor = '#f44336')}
                    >
                        Delete All Notifications
                    </button>
                </>
            ) : (
                <p>No notifications yet.</p>
            )}
            <ToastContainer />
        </div>
    );
};

export default NotificationsPage;