// src/App.js
import React, { useState } from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import LoginForm from './components/LoginForm';
import RegisterForm from './components/RegisterForm';
import Dashboard from './components/Dashboard';
import AssignedToMePage from './components/AssignedToMePage';
import MyAdminPage from './components/MyAdminPage';
import SearchTasksPage from './components/SearchTasksPage';
import UndoneTasksPage from './components/UndoneTasksPage';
import TasksByPriorityPage from './components/TasksByPriorityPage';
import TasksByStatusPage from './components/TasksByStatusPage';
import UpcomingTasksPage from './components/UpcomingTasksPage';
import TasksByDateRangePage from './components/TasksByDateRangePage';
import NotificationsPage from './components/NotificationsPage';
import DueDateRequestsPage from './components/DueDateRequestsPage';
import './App.css';

function App() {
    const [isSidebarOpen, setIsSidebarOpen] = useState(false);
    const toggleSidebar = () => setIsSidebarOpen(!isSidebarOpen);

    return (
        <Router>
            <div className="App">
                <Sidebar isOpen={isSidebarOpen} toggleSidebar={toggleSidebar} />
                <div className={`main-content ${isSidebarOpen ? 'shifted' : ''}`}>
                    <span className="sidebar-toggle-span" onClick={toggleSidebar}>
                        {isSidebarOpen ? '✖' : '☰'}
                    </span>
                    <Routes>
                        <Route path="/login" element={<LoginForm />} />
                        <Route path="/register" element={<RegisterForm />} />
                        <Route path="/dashboard" element={<Dashboard />} />
                        <Route path="/assigned-to-me" element={<AssignedToMePage />} />
                        <Route path="/my-admin" element={<MyAdminPage />} />
                        <Route path="/search-tasks" element={<SearchTasksPage />} />
                        <Route path="/undone-tasks" element={<UndoneTasksPage />} />
                        <Route path="/tasks-by-priority" element={<TasksByPriorityPage />} />
                        <Route path="/tasks-by-status" element={<TasksByStatusPage />} />
                        <Route path="/upcoming-tasks" element={<UpcomingTasksPage />} />
                        <Route path="/tasks-by-date-range" element={<TasksByDateRangePage />} />
                        <Route path="/notifications" element={<NotificationsPage />} />
                        <Route path="/due-date-requests" element={<DueDateRequestsPage />} />
                        <Route path="/" element={<Dashboard />} />
                    </Routes>
                </div>
            </div>
        </Router>
    );
}

export default App;