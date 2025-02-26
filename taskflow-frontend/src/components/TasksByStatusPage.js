import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast, ToastContainer } from 'react-toastify';
import { getTasksByStatus, updateTaskStatus, deleteTask } from '../api';
import { parseDate } from '../utils/dateUtils';
import TaskDetails from './TaskDetails';
import './styles/TasksByStatus.css';

const TasksByStatusPage = () => {
    const [tasks, setTasks] = useState({ TODO: [], IN_PROGRESS: [], DONE: [] });
    const [selectedTask, setSelectedTask] = useState(null);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        fetchAllTasks();
    }, []);

    const fetchAllTasks = async () => {
        try {
            const statuses = ['TODO', 'IN_PROGRESS', 'DONE'];
            const tasksByStatus = { TODO: [], IN_PROGRESS: [], DONE: [] };

            for (const status of statuses) {
                const response = await getTasksByStatus(status);
                tasksByStatus[status] = response.data;
            }
            setTasks(tasksByStatus);
        } catch (err) {
            const errorMessage = err.response?.data?.error || err.message || 'Unknown error';
            setError(`Failed to load tasks: ${errorMessage}`);
            toast.error(`Failed to load tasks: ${errorMessage}`);
            console.error('Fetch error:', err);
        }
    };

    const handleTaskClick = (task) => setSelectedTask(task);
    const closeModal = () => setSelectedTask(null);

    const handleUpdateStatus = async (taskId) => {
        try {
            const response = await updateTaskStatus(taskId);
            const updatedTask = response.data;
            setTasks(prev => {
                const newTasks = { ...prev };
                // Remove from old status
                Object.keys(newTasks).forEach(status => {
                    newTasks[status] = newTasks[status].filter(t => t.taskId !== taskId);
                });
                // Add to new status
                newTasks[updatedTask.status] = [...newTasks[updatedTask.status], updatedTask];
                return newTasks;
            });
            setSelectedTask(updatedTask);
            toast.success('Task status updated!');
        } catch (err) {
            const errorMessage = err.response?.data?.error || err.message || 'Unknown error';
            toast.error(`Failed to update status: ${errorMessage}`);
            console.error('Update status error:', err);
        }
    };

    const handleDeleteTask = async (taskId) => {
        try {
            const response = await deleteTask(taskId);
            setTasks(prev => ({
                ...prev,
                TODO: prev.TODO.filter(t => t.taskId !== taskId),
                IN_PROGRESS: prev.IN_PROGRESS.filter(t => t.taskId !== taskId),
                DONE: prev.DONE.filter(t => t.taskId !== taskId)
            }));
            closeModal();
            toast.success(response.data);
        } catch (err) {
            toast.error('Failed to delete task.');
            console.error(err);
        }
    };

    return (
        <div className="task-page">
            <h1>Tasks by Status</h1>
            {error && <p className="error">{error}</p>}

            <div className="status-container">
                {['TODO', 'IN_PROGRESS', 'DONE'].map(status => (
                    <div key={status} className="status-card">
                        <h2>{status.replace('_', ' ')}</h2>
                        {tasks[status].length > 0 ? (
                            <ul className="task-list">
                                {tasks[status].map(task => (
                                    <li key={task.taskId} className="task-item">
                                        <strong
                                            className="task-title"
                                            onClick={() => handleTaskClick(task)}
                                        >
                                            {task.title}
                                        </strong> -
                                        <span className={`status-${task.status.toLowerCase()}`}>
                                            {task.status}
                                        </span>
                                        <br />
                                        <span>Due: {parseDate(task.dueDate).toLocaleDateString()}</span>
                                    </li>
                                ))}
                            </ul>
                        ) : (
                            <p>No tasks found.</p>
                        )}
                    </div>
                ))}
            </div>

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

export default TasksByStatusPage;