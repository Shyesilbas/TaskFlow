// src/components/MyTasksPage.js
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast, ToastContainer } from 'react-toastify';
import {
    getAllTasks,
    getUpcomingTasks,
    getUndoneTasks,
    getTasksByStatus,
    getTasksByPriority,
    searchTasksByKeyword,
    updateTaskStatus,
    deleteTask,
} from '../api';
import { parseDate } from '../utils/dateUtils';
import TaskDetails from './TaskDetails';
import './styles/TaskPage.css';

const MyTasksPage = () => {
    const [allTasks, setAllTasks] = useState([]);
    const [filteredTasks, setFilteredTasks] = useState([]);
    const [selectedTask, setSelectedTask] = useState(null);
    const [error, setError] = useState('');
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [keywordSearch, setKeywordSearch] = useState('');
    const [isDateFilterActive, setIsDateFilterActive] = useState(false);
    const [filterType, setFilterType] = useState('all');
    const navigate = useNavigate();

    useEffect(() => {
        fetchTasksByFilter('all');
    }, []);

    const fetchTasksByFilter = async (type) => {
        try {
            let response;
            switch (type) {
                case 'all':
                    response = await getAllTasks();
                    setFilteredTasks(response.data);
                    setAllTasks(response.data);
                    break;
                case 'upcoming':
                    response = await getUpcomingTasks();
                    setFilteredTasks(response.data);
                    break;
                case 'undone':
                    response = await getUndoneTasks();
                    setFilteredTasks(response.data);
                    break;
                case 'status':
                    const statusTasks = {
                        TODO: (await getTasksByStatus('TODO')).data,
                        IN_PROGRESS: (await getTasksByStatus('IN_PROGRESS')).data,
                        DONE: (await getTasksByStatus('DONE')).data,
                    };
                    setFilteredTasks(statusTasks);
                    break;
                case 'priority':
                    const priorityTasks = {
                        LOW: (await getTasksByPriority('LOW')).data,
                        MEDIUM: (await getTasksByPriority('MEDIUM')).data,
                        HIGH: (await getTasksByPriority('HIGH')).data,
                    };
                    setFilteredTasks(priorityTasks);
                    break;
                case 'search':
                    if (!keywordSearch) {
                        toast.info('Please enter keywords to search');
                        return;
                    }
                    response = await searchTasksByKeyword(keywordSearch.split(','));
                    setFilteredTasks(response.data);
                    break;
                default:
                    response = await getAllTasks();
                    setFilteredTasks(response.data);
                    break;
            }
        } catch (err) {
            const errorMessage = err.response?.data?.error || err.message || 'Unknown error';
            setError(`Failed to load tasks: ${errorMessage}`);
            toast.error(`Failed to load tasks: ${errorMessage}`);
            console.error('Fetch error:', err);
        }
    };

    const normalizeDate = (dateString) => {
        const date = new Date(dateString);
        date.setHours(0, 0, 0, 0);
        return date;
    };

    const applyDateFilter = () => {
        if (!startDate || !endDate) {
            toast.error('Please select both start and end dates');
            return;
        }
        const startDateTime = normalizeDate(startDate);
        const endDateTime = normalizeDate(endDate);
        endDateTime.setHours(23, 59, 59, 999);

        if (startDateTime > endDateTime) {
            toast.error('Start date must be before end date');
            return;
        }

        setIsDateFilterActive(true);
        setFilteredTasks(prev => {
            if (Array.isArray(prev)) {
                return prev.filter(task => {
                    const taskDate = normalizeDate(parseDate(task.dueDate));
                    return taskDate >= startDateTime && taskDate <= endDateTime;
                });
            } else {
                const filtered = {};
                Object.keys(prev).forEach(key => {
                    filtered[key] = prev[key].filter(task => {
                        const taskDate = normalizeDate(parseDate(task.dueDate));
                        return taskDate >= startDateTime && taskDate <= endDateTime;
                    });
                });
                return filtered;
            }
        });
        toast.success('Date filter applied');
    };

    const clearDateFilter = () => {
        setStartDate('');
        setEndDate('');
        setIsDateFilterActive(false);
        fetchTasksByFilter(filterType);
        toast.info('Date filter cleared');
    };

    const handleSearch = () => {
        if (filterType === 'search') {
            fetchTasksByFilter('search');
        }
    };

    const handleTaskClick = (task) => setSelectedTask(task);
    const closeModal = () => setSelectedTask(null);

    const handleUpdateStatus = async (taskId) => {
        try {
            const response = await updateTaskStatus(taskId);
            const updatedTask = response.data;

            setAllTasks(prev => prev.map(t => t.taskId === taskId ? updatedTask : t));
            setFilteredTasks(prev => {
                if (Array.isArray(prev)) {
                    return prev.map(t => t.taskId === taskId ? updatedTask : t);
                } else {
                    const newFiltered = { ...prev };
                    Object.keys(newFiltered).forEach(key => {
                        newFiltered[key] = newFiltered[key].map(t =>
                            t.taskId === taskId ? updatedTask : t
                        );
                    });
                    return newFiltered;
                }
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
            setAllTasks(prev => prev.filter(t => t.taskId !== taskId));
            setFilteredTasks(prev => {
                if (Array.isArray(prev)) {
                    return prev.filter(t => t.taskId !== taskId);
                } else {
                    const newFiltered = { ...prev };
                    Object.keys(newFiltered).forEach(key => {
                        newFiltered[key] = newFiltered[key].filter(t => t.taskId !== taskId);
                    });
                    return newFiltered;
                }
            });
            closeModal();
            toast.success(response.data);
        } catch (err) {
            toast.error('Failed to delete task.');
            console.error(err);
        }
    };

    const renderTasks = () => {
        if (filterType === 'status') {
            return (
                <div className="status-container">
                    {['TODO', 'IN_PROGRESS', 'DONE'].map(status => (
                        <div key={status} className="status-card">
                            <h2>{status.replace('_', ' ')}</h2>
                            {filteredTasks[status]?.length > 0 ? (
                                <div className="task-card-container">
                                    {filteredTasks[status].map(task => renderTaskCard(task))}
                                </div>
                            ) : (
                                <p>No tasks found.</p>
                            )}
                        </div>
                    ))}
                </div>
            );
        } else if (filterType === 'priority') {
            return (
                <div className="priority-container">
                    {['LOW', 'MEDIUM', 'HIGH'].map(priority => (
                        <div key={priority} className="priority-card">
                            <h2>{priority}</h2>
                            {filteredTasks[priority]?.length > 0 ? (
                                <div className="task-card-container">
                                    {filteredTasks[priority].map(task => renderTaskCard(task))}
                                </div>
                            ) : (
                                <p>No tasks found.</p>
                            )}
                        </div>
                    ))}
                </div>
            );
        } else {
            return (
                <div className="task-card-container">
                    {filteredTasks.length > 0 ? (
                        filteredTasks.map(task => renderTaskCard(task))
                    ) : (
                        <p className="no-tasks">No tasks available.</p>
                    )}
                </div>
            );
        }
    };

    const renderTaskCard = (task) => (
        <div key={task.taskId} className="task-card">
            <div className="task-header">
                <strong className="task-title" onClick={() => handleTaskClick(task)}>
                    {task.title}
                </strong>
                <span className={`priority-${task.taskPriority?.toLowerCase()}`}>
                {task.taskPriority || 'N/A'}
            </span>
            </div>
            <div className="task-body">
                <p className="task-description">Created at: {task.createdAt || ''}</p>
                <p className="task-description">
                    Description: {task.description || 'No description available'}
                </p>
                <p className="task-description">
                    Your Comment: {task.userComment || 'No description available'}
                </p>
                <p className="task-description">Admin Comment: {task.adminComment || ''}</p>
                <div className="task-keywords">
                    Keywords:{' '}
                    {task.keywords ? (
                        Array.isArray(task.keywords) ? (
                            task.keywords.map((keyword, index) => (
                                <span key={index} className="keyword-badge">
                                {keyword.trim()}
                            </span>
                            ))
                        ) : typeof task.keywords === 'string' ? (
                            task.keywords.split(',').map((keyword, index) => (
                                <span key={index} className="keyword-badge">
                                {keyword.trim()}
                            </span>
                            ))
                        ) : (
                            'Invalid format'
                        )
                    ) : (
                        'None'
                    )}
                </div>
                <div className="task-details">
                <span className={`status-${task.status.toLowerCase()}`}>
                    Status: {task.status}
                </span>
                    <span className="due-date">
                    Due: {parseDate(task.dueDate).toLocaleDateString()}
                </span>
                </div>
            </div>
        </div>
    );

    return (
        <div className="task-page">
            <h1>My Tasks</h1>
            {error && <p className="error">{error}</p>}

            <div className="filter-container">
                <h3>Filter Tasks</h3>
                <select
                    value={filterType}
                    onChange={(e) => {
                        setFilterType(e.target.value);
                        if (e.target.value !== 'search') {
                            setKeywordSearch('');
                            fetchTasksByFilter(e.target.value);
                        }
                    }}
                >
                    <option value="all">All Tasks</option>
                    <option value="upcoming">Upcoming Tasks</option>
                    <option value="undone">Undone Tasks</option>
                    <option value="status">By Status</option>
                    <option value="priority">By Priority</option>
                    <option value="search">Search Tasks</option>
                </select>

                {filterType === 'search' && (
                    <div className="search-form">
                        <input
                            type="text"
                            value={keywordSearch}
                            onChange={(e) => setKeywordSearch(e.target.value)}
                            placeholder="Enter keywords (comma separated)"
                        />
                        <button onClick={handleSearch} className="apply-filter">Search</button>
                    </div>
                )}

                <div className="date-filter-container">
                    <h4>Filter by Due Date</h4>
                    <div className="date-inputs">
                        <div className="date-field">
                            <label>Start Date:</label>
                            <input
                                type="date"
                                value={startDate}
                                onChange={(e) => setStartDate(e.target.value)}
                            />
                        </div>
                        <div className="date-field">
                            <label>End Date:</label>
                            <input
                                type="date"
                                value={endDate}
                                onChange={(e) => setEndDate(e.target.value)}
                            />
                        </div>
                    </div>
                    <div className="filter-buttons">
                        <button onClick={applyDateFilter} className="apply-filter">Apply Date Filter</button>
                        {isDateFilterActive && (
                            <button onClick={clearDateFilter} className="clear-filter">Clear Date Filter</button>
                        )}
                    </div>
                </div>
            </div>

            {renderTasks()}

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

export default MyTasksPage;