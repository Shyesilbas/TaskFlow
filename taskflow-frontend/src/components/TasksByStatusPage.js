// src/components/TasksByStatusPage.js
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast, ToastContainer } from 'react-toastify';
import { getTasksByStatus, updateTaskStatus, deleteTask } from '../api';
import { parseDate } from '../utils/dateUtils';
import TaskDetails from './TaskDetails';
import './TaskPage.css';

const TasksByStatusPage = () => {
    const [statusFilter, setStatusFilter] = useState('');
    const [tasks, setTasks] = useState([]);
    const [selectedTask, setSelectedTask] = useState(null);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleFilter = async () => {
        if (!statusFilter) return;

        try {
            const response = await getTasksByStatus(statusFilter);
            setTasks(response.data);
        } catch (err) {
            const errorMessage = err.response?.data?.error || err.message || 'Unknown error';
            setError(`Failed to filter tasks: ${errorMessage}`);
            console.error('Filter error:', err.response?.data || err);
            toast.error(`Failed to filter tasks: ${errorMessage}`);
        }
    };

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
            <h1>Tasks by Status</h1>
            {error && <p className="error">{error}</p>}
            <div className="filter-form">
                <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
                    <option value="">Select Status</option>
                    <option value="TODO">To Do</option>
                    <option value="IN_PROGRESS">In Progress</option>
                    <option value="DONE">Done</option>
                </select>
                <button onClick={handleFilter}>Filter</button>
            </div>
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
                <p>No tasks found.</p>
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

export default TasksByStatusPage;