import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080',
});

api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
         console.log('Interceptor - Request URL:', config.url);
         console.log('Interceptor - Token:', token);
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
             console.log('Interceptor - Authorization Header:', config.headers.Authorization);
        } else {
            console.warn('Interceptor - No token found in localStorage');
        }
        return config;
    },
    (error) => Promise.reject(error)
);

api.interceptors.response.use(
    (response) => response,
    (error) => {
        console.error('Interceptor - Response Error:', error.response?.data || error.message);
        if (error.response?.status === 401 || error.response?.data?.error?.includes('No token provided')) {
            localStorage.clear();
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export const getAssignedToMe = () => api.get('/api/tasks/user/assignedToMe');
export const getTasksByDateRange = (startDate, endDate) =>
    api.get(`/api/tasks/user/by-date-range?startDate=${startDate}&endDate=${endDate}`);
export const getTasksByPriority = (priority) =>
    api.get(`/api/tasks/user/tasksByPriority?taskPriority=${priority}`);
export const getTasksByStatus = (status) =>
    api.get(`/api/tasks/user/by-status?taskStatus=${status}`);
export const getUpcomingTasks = () => api.get('/api/tasks/user/upcomingTasks');
export const getTaskStats = () => api.get('/api/tasks/user/taskStats');
export const getUndoneTasks = () => api.get('/api/tasks/user/unDoneTasks');
export const getMyAdmin = () => api.get('/api/tasks/user/myAdmin');
export const getNotifications = () => api.get('/api/tasks/user/myNotifications');
export const getDueDateChangeRequests = () => api.get('/api/tasks/user/my-due-date-change-requests');
export const searchTasksByKeyword = (keywords) =>
    api.get(`/api/tasks/user/searchTask/by-keyword?keyword=${keywords}`);

export const updateTaskStatus = (taskId) =>
    api.put(`/api/tasks/user/update-taskStatus?taskId=${taskId}`);
export const deleteTask = (taskId) =>
    api.delete(`/api/tasks/user/deleteYourTask?taskId=${taskId}`);
export const addCommentToTask = (taskId, comment) =>
    api.put('/api/tasks/user/addCommentToTask', { taskId, comment });
export const requestDueDateChange = (taskId, newDate, message) =>
    api.post('/api/tasks/user/request-due-date-change', { taskId, newDate, message });

export const logout = () => api.post('/auth/logout', {});

export default api;