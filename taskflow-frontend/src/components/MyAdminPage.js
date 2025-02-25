// src/components/MyAdminPage.js
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast, ToastContainer } from 'react-toastify';
import { getMyAdmin } from '../api';
import './TaskPage.css';

const MyAdminPage = () => {
    const [adminInfo, setAdminInfo] = useState(null);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const fetchAdmin = async () => {
            try {
                const response = await getMyAdmin();
                setAdminInfo(response.data);
            } catch (err) {
                const errorMessage = err.response?.data?.error || err.message || 'Unknown error';
                setError(`Failed to load admin info: ${errorMessage}`);
                console.error('Fetch error:', err.response?.data || err);
                toast.error(`Failed to load admin info: ${errorMessage}`);
            }
        };

        fetchAdmin();
    }, [navigate]);

    return (
        <div className="task-page">
            <h1>My Admin</h1>
            {error && <p className="error">{error}</p>}
            {adminInfo ? (
                <div className="admin-info">
                    <p><strong>Username:</strong> {adminInfo.username}</p>
                    <p><strong>Email:</strong> {adminInfo.email}</p>
                    <p><strong>Phone:</strong> {adminInfo.phone}</p>
                </div>
            ) : (
                <p>No admin assigned.</p>
            )}
            <ToastContainer />
        </div>
    );
};

export default MyAdminPage;