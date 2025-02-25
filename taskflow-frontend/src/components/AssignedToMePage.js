// src/components/AssignedToMePage.js
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast, ToastContainer } from 'react-toastify';
import { getAssignedToMe, updateTaskStatus, deleteTask } from '../api';
import { parseDate } from '../utils/dateUtils';
import TaskDetails from './TaskDetails';
import './TaskPage.css';

const AssignedToMePage = () => {
    const [tasks, setTasks] = useState([]);
    const [selectedTask, setSelectedTask] = useState(null);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const fetchTasks = async () => {
            try {
                const response = await getAssignedToMe();
                setTasks(response.data);
            } catch (err) {
                const errorMessage = err.response?.data?.error || err.message || 'Unknown error';
                setError(`Failed to load tasks: ${errorMessage}`);
                console.error('Fetch error:', err.response?.data || err);
                toast.error(`Failed to load tasks: ${errorMessage}`);
            }
        };

        fetchTasks();
    }, [navigate]);

    const handleTaskClick = (task) => setSelectedTask(task);
    const closeModal = () => setSelectedTask(null);

    const handleUpdateStatus = async (taskId) => {
        try {
            const response = await updateTaskStatus(taskId);
            setTasks(tasks.map(t => t.taskId === taskId ? response.data : t));
            setSelectedTask(response.data);
            toast.success('Task status updated!');
        } catch (err) {
            const errorMessage = err.response?.data?.error || err.message || 'Unknown error';
            toast.error(`Failed to update status: ${errorMessage}`);
            console.error('Update status error:', err.response?.data || err);
        }
    };

    const handleDeleteTask = async (taskId) => {
        try {
            const response = await deleteTask(taskId);
            setTasks(tasks.filter(t => t.taskId !== taskId));
            closeModal();
            toast.success(response.data);
        } catch (err) {
            toast.error('Failed to delete task.');
            console.error(err);
        }
    };

    return (
        <div className="task-page">
            <h1>Tasks Assigned to Me</h1>
            {error && <p className="error">{error}</p>}
            {tasks.length > 0 ? (
                <ul className="task-list">
                    {tasks.map((task) => (
                        <li key={task.taskId} className="task-item">
                            <strong className="task-title" onClick={() => handleTaskClick(task)}>
                                {task.title}
                            </strong> -
                            <span className={`status-${task.status.toLowerCase()}`}>{task.status}</span>
                            <br />
                            <span>Due: {parseDate(task.dueDate).toLocaleDateString()}</span>
                        </li>
                    ))}
                </ul>
            ) : (
                <p>No tasks assigned yet.</p>
            )}

            {selectedTask && (
                <TaskDetails
                    task={selectedTask}
                    onClose={closeModal}
                    onUpdateStatus={handleUpdateStatus}
                    onDeleteTask={handleDeleteTask}
                    parseDate={parseDate}
                />
            )}

            <ToastContainer />
        </div>
    );
};

export default AssignedToMePage;