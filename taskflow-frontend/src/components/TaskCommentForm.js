import React, { useState } from 'react';
import api from '../api';
import { toast } from 'react-toastify';

const TaskCommentForm = ({ taskId, onCommentAdded }) => {
    const [comment, setComment] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const response = await api.put('/api/tasks/user/addCommentToTask', { taskId, comment });
            onCommentAdded(response.data);
            setComment('');
            toast.success('Comment added!');
        } catch (err) {
            setError('Failed to add comment.');
            console.error(err);
            toast.error('Failed to add comment.');
        }
    };

    return (
        <form onSubmit={handleSubmit} className="task-comment-form">
            <textarea
                value={comment}
                onChange={(e) => setComment(e.target.value)}
                placeholder="Add a comment..."
                required
            />
            <button type="submit">Add Comment</button>
            {error && <p className="error">{error}</p>}
        </form>
    );
};

export default TaskCommentForm;