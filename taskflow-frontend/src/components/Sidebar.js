// src/components/Sidebar.js
import React from 'react';
import { NavLink } from 'react-router-dom';
import './Sidebar.css';

const Sidebar = ({ isOpen, toggleSidebar }) => {
    return (
        <div className={`sidebar ${isOpen ? 'open' : ''}`}>
            <div className="sidebar-header">
                <h3>TaskFlow</h3>
            </div>
            <nav className="sidebar-nav">
                <NavLink to="/dashboard" onClick={isOpen ? toggleSidebar : null}>Dashboard</NavLink>
                <NavLink to="/my-admin" onClick={isOpen ? toggleSidebar : null}>My Admin</NavLink>
                <NavLink to="/search-tasks" onClick={isOpen ? toggleSidebar : null}>Search Tasks</NavLink>
                <NavLink to="/undone-tasks" onClick={isOpen ? toggleSidebar : null}>Undone Tasks</NavLink>
                <NavLink to="/assigned-to-me" onClick={isOpen ? toggleSidebar : null}>Assigned to Me</NavLink>
                <NavLink to="/tasks-by-priority" onClick={isOpen ? toggleSidebar : null}>Tasks by Priority</NavLink>
                <NavLink to="/tasks-by-status" onClick={isOpen ? toggleSidebar : null}>Tasks by Status</NavLink>
                <NavLink to="/upcoming-tasks" onClick={isOpen ? toggleSidebar : null}>Upcoming Tasks</NavLink>
                <NavLink to="/tasks-by-date-range" onClick={isOpen ? toggleSidebar : null}>Tasks by Date Range</NavLink>
                <NavLink to="/notifications" onClick={isOpen ? toggleSidebar : null}>Notifications</NavLink>
                <NavLink to="/due-date-requests" onClick={isOpen ? toggleSidebar : null}>Due Date Requests</NavLink>
            </nav>
        </div>
    );
};

export default Sidebar;