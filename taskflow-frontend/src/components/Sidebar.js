// src/components/Sidebar.js
import React, { useState, useEffect } from 'react';
import { NavLink } from 'react-router-dom';
import './styles/Sidebar.css';

const Sidebar = ({ isOpen, toggleSidebar }) => {
    const [isTasksOpen, setIsTasksOpen] = useState(false);
    const [hasNewNotification, setHasNewNotification] = useState(
        localStorage.getItem('hasNewNotification') === 'true'
    );

    const toggleTasksMenu = () => setIsTasksOpen(!isTasksOpen);

    useEffect(() => {
        const handleStorageChange = () => {
            setHasNewNotification(localStorage.getItem('hasNewNotification') === 'true');
        };

        window.addEventListener('storage', handleStorageChange);
        return () => window.removeEventListener('storage', handleStorageChange);
    }, []);

    return (
        <div className={`sidebar ${isOpen ? 'open' : ''}`}>
            <div className="sidebar-header">
                <h3>TaskFlow</h3>
            </div>
            <nav className="sidebar-nav">
                <NavLink to="/dashboard" onClick={isOpen ? toggleSidebar : null}>
                    Dashboard
                </NavLink>
                <NavLink to="/my-admin" onClick={isOpen ? toggleSidebar : null}>
                    My Admin
                </NavLink>

                <div className="sidebar-dropdown">
                    <span onClick={toggleTasksMenu} className="dropdown-title">
                        My Tasks {isTasksOpen ? '▲' : '▼'}
                    </span>
                    {isTasksOpen && (
                        <div className="dropdown-menu">
                            <NavLink to="/my-tasks" onClick={isOpen ? toggleSidebar : null}>
                                My Tasks
                            </NavLink>
                            <NavLink to="/assigned-to-me" onClick={isOpen ? toggleSidebar : null}>
                                Assigned to Me
                            </NavLink>
                            <NavLink to="/search-tasks" onClick={isOpen ? toggleSidebar : null}>
                                Search Tasks
                            </NavLink>
                        </div>
                    )}
                </div>

                <div className="notification-link">
                    <NavLink to="/notifications" onClick={isOpen ? toggleSidebar : null}>
                        Notifications
                    </NavLink>
                    {hasNewNotification && <span className="notification-badge"></span>}
                </div>

                <NavLink to="/due-date-requests" onClick={isOpen ? toggleSidebar : null}>
                    Due Date Requests
                </NavLink>
            </nav>
        </div>
    );
};

export default Sidebar;