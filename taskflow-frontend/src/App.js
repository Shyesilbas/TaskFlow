// src/App.js
import React, { useState } from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import LoginForm from './components/auth/LoginForm';
import RegisterForm from './components/auth/RegisterForm';
import Dashboard from './components/Dashboard';
import AssignedToMePage from './components/AssignedToMePage';
import MyAdminPage from './components/MyAdminPage';
import SearchTasksPage from './components/SearchTasksPage';
import NotificationsPage from './components/NotificationsPage';
import DueDateRequestsPage from './components/DueDateRequestsPage';
import MyTasksPage from './components/MyTasksPage';
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
                        <Route path="/my-tasks" element={<MyTasksPage />} />
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