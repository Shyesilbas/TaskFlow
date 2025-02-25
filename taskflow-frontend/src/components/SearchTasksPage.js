// src/components/SearchTasksPage.js
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast, ToastContainer } from 'react-toastify';
import { searchTasksByKeyword, updateTaskStatus, deleteTask } from '../api';
import { parseDate } from '../utils/dateUtils';
import TaskDetails from './TaskDetails';
import './TaskPage.css';

const SearchTasksPage = () => {
    const [keywordSearch, setKeywordSearch] = useState('');
    const [searchedTasks, setSearchedTasks] = useState([]);
    const [selectedTask, setSelectedTask] = useState(null);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSearch = async () => {
        if (!keywordSearch) return;

        try {
            const response = await searchTasksByKeyword(keywordSearch.split(','));
            setSearchedTasks(response.data);
        } catch (err) {
            const errorMessage = err.response?.data?.error || err.message || 'Unknown error';
            setError(`Failed to search tasks: ${errorMessage}`);
            console.error('Search error:', err.response?.data || err);
            toast.error(`Failed to search tasks: ${errorMessage}`);
        }
    };

    const handleTaskClick = (task) => setSelectedTask(task);
    const closeModal = () => setSelectedTask(null);

    const handleUpdateStatus = async (taskId) => {
        try {
            const response = await updateTaskStatus(taskId);
            setSearchedTasks(searchedTasks.map(t => t.taskId === taskId ? response.data : t));
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
            setSearchedTasks(searchedTasks.filter(t => t.taskId !== taskId));
            closeModal();
            toast.success(response.data);
        } catch (err) {
            toast.error('Failed to delete task.');
            console.error(err);
        }
    };

    return (
        <div className="task-page">
            <h1>Search Tasks</h1>
            {error && <p className="error">{error}</p>}
            <div className="search-form">
                <input
                    type="text"
                    value={keywordSearch}
                    onChange={(e) => setKeywordSearch(e.target.value)}
                    placeholder="Enter keywords (comma separated)"
                />
                <button onClick={handleSearch}>Search</button>
            </div>
            {searchedTasks.length > 0 ? (
                <ul className="task-list">
                    {searchedTasks.map((task) => (
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

export default SearchTasksPage;