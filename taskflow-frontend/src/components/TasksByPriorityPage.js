// src/components/TasksByPriorityPage.js
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast, ToastContainer } from 'react-toastify';
import { getTasksByPriority, updateTaskStatus, deleteTask } from '../api';
import { parseDate } from '../utils/dateUtils';
import TaskDetails from './TaskDetails';
import './styles/TasksByPriority.css';

const TasksByPriorityPage = () => {
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [allTasks, setAllTasks] = useState({ LOW: [], MEDIUM: [], HIGH: [] });
    const [filteredTasks, setFilteredTasks] = useState({ LOW: [], MEDIUM: [], HIGH: [] });
    const [selectedTask, setSelectedTask] = useState(null);
    const [error, setError] = useState('');
    const [isDateFilterActive, setIsDateFilterActive] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        fetchAllTasks();
    }, []);

    const fetchAllTasks = async () => {
        try {
            const priorities = ['LOW', 'MEDIUM', 'HIGH'];
            const tasksByPriority = { LOW: [], MEDIUM: [], HIGH: [] };

            for (const priority of priorities) {
                const response = await getTasksByPriority(priority);
                tasksByPriority[priority] = response.data;
            }
            setAllTasks(tasksByPriority);
            setFilteredTasks(tasksByPriority);
        } catch (err) {
            const errorMessage = err.response?.data?.error || err.message || 'Unknown error';
            setError(`Failed to load tasks: ${errorMessage}`);
            toast.error(`Failed to load tasks: ${errorMessage}`);
            console.error('Fetch error:', err);
        }
    };

    // Date comparisons için tarihi normalize et (saat: 00:00:00 olarak ayarla)
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

        // Saat kısmını normalize ederek, sadece gün karşılaştırması yapılmasını sağla
        const startDateTime = normalizeDate(startDate);
        // Bitiş tarihi için günün sonunu al, böylece o günün tüm görevleri dahil edilir
        const endDateTime = normalizeDate(endDate);
        endDateTime.setHours(23, 59, 59, 999);

        if (startDateTime > endDateTime) {
            toast.error('Start date must be before end date');
            return;
        }

        console.log("Filtering between:", startDateTime, "and", endDateTime);

        const filtered = {
            LOW: allTasks.LOW.filter(task => {
                // parseDate kullanarak tarihi doğru formatta al
                const taskDate = normalizeDate(parseDate(task.dueDate));
                console.log("Task date for", task.title, ":", taskDate);

                // >= ve <= kullanarak tam olarak karşılaştır
                return taskDate >= startDateTime && taskDate <= endDateTime;
            }),
            MEDIUM: allTasks.MEDIUM.filter(task => {
                const taskDate = normalizeDate(parseDate(task.dueDate));
                return taskDate >= startDateTime && taskDate <= endDateTime;
            }),
            HIGH: allTasks.HIGH.filter(task => {
                const taskDate = normalizeDate(parseDate(task.dueDate));
                return taskDate >= startDateTime && taskDate <= endDateTime;
            })
        };

        setFilteredTasks(filtered);
        setIsDateFilterActive(true);
        toast.success('Date filter applied');
    };

    const clearDateFilter = () => {
        setStartDate('');
        setEndDate('');
        setFilteredTasks(allTasks);
        setIsDateFilterActive(false);
        toast.info('Date filter cleared');
    };

    const handleTaskClick = (task) => setSelectedTask(task);
    const closeModal = () => setSelectedTask(null);

    const handleUpdateStatus = async (taskId) => {
        try {
            const response = await updateTaskStatus(taskId);
            const updatedTask = response.data;

            // Update both allTasks and filteredTasks
            setAllTasks(prev => ({
                ...prev,
                [updatedTask.priority]: prev[updatedTask.priority].map(t =>
                    t.taskId === taskId ? updatedTask : t
                )
            }));

            setFilteredTasks(prev => ({
                ...prev,
                [updatedTask.priority]: prev[updatedTask.priority].map(t =>
                    t.taskId === taskId ? updatedTask : t
                )
            }));

            setSelectedTask(updatedTask);
            toast.success('Task status updated!');
        } catch (err) {
            const errorMessage = err.response?.data?.error || err.message || 'Unknown error';
            toast.error(`Failed to update status: ${errorMessage}`);
        }
    };

    const handleDeleteTask = async (taskId) => {
        try {
            const response = await deleteTask(taskId);

            // Update both allTasks and filteredTasks
            setAllTasks(prev => ({
                ...prev,
                LOW: prev.LOW.filter(t => t.taskId !== taskId),
                MEDIUM: prev.MEDIUM.filter(t => t.taskId !== taskId),
                HIGH: prev.HIGH.filter(t => t.taskId !== taskId)
            }));

            setFilteredTasks(prev => ({
                ...prev,
                LOW: prev.LOW.filter(t => t.taskId !== taskId),
                MEDIUM: prev.MEDIUM.filter(t => t.taskId !== taskId),
                HIGH: prev.HIGH.filter(t => t.taskId !== taskId)
            }));

            closeModal();
            toast.success(response.data);
        } catch (err) {
            toast.error('Failed to delete task.');
            console.error(err);
        }
    };

    // Tarih formatını düzgün görüntülemek için yardımcı fonksiyon
    const formatDateDisplay = (dateString) => {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toLocaleDateString();
    };

    return (
        <div className="task-page">
            <h1>Tasks by Priority</h1>
            {error && <p className="error">{error}</p>}

            <div className="date-filter-container">
                <h3>Filter by Due Date</h3>
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
                    <button onClick={applyDateFilter} className="apply-filter">Apply Filter</button>
                    {isDateFilterActive && (
                        <button onClick={clearDateFilter} className="clear-filter">Clear Filter</button>
                    )}
                </div>
                {isDateFilterActive && (
                    <div className="filter-active">
                        <span>Filtered by date: {formatDateDisplay(startDate)} - {formatDateDisplay(endDate)}</span>
                    </div>
                )}
            </div>

            <div className="priority-container">
                {['LOW', 'MEDIUM', 'HIGH'].map(priority => (
                    <div key={priority} className="priority-card">
                        <h2>{priority}</h2>
                        {filteredTasks[priority].length > 0 ? (
                            <ul className="task-list">
                                {filteredTasks[priority].map(task => (
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

export default TasksByPriorityPage;