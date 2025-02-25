// src/components/TasksByDateRangePage.js
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast, ToastContainer } from 'react-toastify';
import { getTasksByDateRange, updateTaskStatus, deleteTask } from '../api';
import { parseDate } from '../utils/dateUtils';
import TaskDetails from './TaskDetails';
import './TaskPage.css';

const TasksByDateRangePage = () => {
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [tasks, setTasks] = useState([]);
    const [selectedTask, setSelectedTask] = useState(null);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const formatDateForBackend = (isoDate) => {
        if (!isoDate) return '';
        const [year, month, day] = isoDate.split('-');
        return `${day}-${month}-${year}`;
    };

    const handleSearch = async () => {
        if (!startDate || !endDate) return;

        const formattedStartDate = formatDateForBackend(startDate);
        const formattedEndDate = formatDateForBackend(endDate);

        try {
            const response = await getTasksByDateRange(formattedStartDate, formattedEndDate);
            setTasks(response.data);
        } catch (err) {
            const errorMessage = err.response?.data?.error || err.message || 'Unknown error';
            setError(`Failed to fetch tasks by date range: ${errorMessage}`);
            console.error('Search error:', err.response?.data || err);
            toast.error(`Failed to fetch tasks: ${errorMessage}`);
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
            <h1>Tasks by Date Range</h1>
            {error && <p className="error">{error}</p>}
            <div className="filter-form">
                <input
                    type="date"
                    value={startDate}
                    onChange={(e) => setStartDate(e.target.value)}
                />
                <input
                    type="date"
                    value={endDate}
                    onChange={(e) => setEndDate(e.target.value)}
                />
                <button onClick={handleSearch}>Search</button>
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

export default TasksByDateRangePage;