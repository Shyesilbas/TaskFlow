// src/components/DueDateRequestsPage.js
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast, ToastContainer } from 'react-toastify';
import { getDueDateChangeRequests } from '../api';
import { parseDate } from '../utils/dateUtils';
import './TaskPage.css';

const DueDateRequestsPage = () => {
    const [requests, setRequests] = useState([]);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const fetchRequests = async () => {
            try {
                const response = await getDueDateChangeRequests();
                setRequests(response.data);
            } catch (err) {
                const errorMessage = err.response?.data?.error || err.message || 'Unknown error';
                setError(`Failed to load due date requests: ${errorMessage}`);
                console.error('Fetch error:', err.response?.data || err);
                toast.error(`Failed to load due date requests: ${errorMessage}`);
            }
        };

        fetchRequests();
    }, [navigate]);

    return (
        <div className="task-page">
            <h1>Due Date Change Requests</h1>
            {error && <p className="error">{error}</p>}
            {requests.length > 0 ? (
                <ul className="task-list">
                    {requests.map((request) => (
                        <li key={request.id} className="task-item">
                            <strong>{request.taskTitle}</strong> -
                            Requested: {parseDate(request.requestedDueDate).toLocaleDateString()} ({request.status})
                            <br />
                            <span>User Message: {request.userMessage}</span>
                            <br />
                            <span>Admin Message: {request.adminMessage || 'No response yet'}</span>
                            <br />
                            <span>Created: {parseDate(request.createdAt).toLocaleString()}</span>
                            <span> Updated: {request.updatedAt ? parseDate(request.updatedAt).toLocaleString() : 'Not updated'}</span>
                        </li>
                    ))}
                </ul>
            ) : (
                <p>No due date change requests.</p>
            )}
            <ToastContainer />
        </div>
    );
};

export default DueDateRequestsPage;