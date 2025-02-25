import React, { useState } from 'react';
import api from '../api';
import { toast } from 'react-toastify';

const DueDateChangeRequestForm = ({ taskId, onRequestSubmitted }) => {
    const [newDate, setNewDate] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const response = await api.post('/api/tasks/user/request-due-date-change', { taskId, newDate, message });
            onRequestSubmitted(response.data);
            setNewDate('');
            setMessage('');
            toast.success('Due date request submitted!');
        } catch (err) {
            setError('Failed to submit due date request.');
            console.error(err);
            toast.error('Failed to submit due date request.');
        }
    };

    return (
        <form onSubmit={handleSubmit} className="due-date-form">
            <input
                type="datetime-local"
                value={newDate}
                onChange={(e) => setNewDate(e.target.value)}
                required
            />
            <textarea
                value={message}
                onChange={(e) => setMessage(e.target.value)}
                placeholder="Reason for change..."
                required
            />
            <button type="submit">Request Change</button>
            {error && <p className="error">{error}</p>}
        </form>
    );
};

export default DueDateChangeRequestForm;