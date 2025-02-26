import React from 'react';
import TaskCommentForm from './TaskCommentForm';
import DueDateChangeRequestForm from './DueDateChangeRequestForm';
import { toast } from 'react-toastify';
import './styles/TaskPage.css';

const TaskDetails = ({ task, onClose, onUpdateStatus, onDeleteTask, parseDate }) => {
    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                <h3>Task Details</h3>
                <p><strong>ID:</strong> {task.taskId}</p>
                <p><strong>Title:</strong> {task.title}</p>
                <p><strong>Description:</strong> {task.description}</p>
                <p><strong>Status:</strong> {task.status}</p>
                <p><strong>Assigned By:</strong> {task.assignedBy}</p>
                <p><strong>Assigned To:</strong> {task.assignedToUsername}</p>
                <p><strong>User Comment:</strong> {task.userComment || 'No comment'}</p>
                <p><strong>Admin Comment:</strong> {task.adminComment || 'No comment'}</p>
                <p><strong>Priority:</strong> {task.taskPriority}</p>
                <p><strong>Created At:</strong> {parseDate(task.createdAt).toLocaleString()}</p>
                <p><strong>Due Date:</strong> {parseDate(task.dueDate).toLocaleString()}</p>
                <p><strong>Keywords:</strong> {task.keywords ? task.keywords.join(', ') : 'None'}</p>
                <TaskCommentForm taskId={task.taskId} onCommentAdded={(updatedTask) => onUpdateStatus(updatedTask)} />
                <DueDateChangeRequestForm taskId={task.taskId} onRequestSubmitted={() => toast.success('Request submitted!')} />
                <button onClick={() => onUpdateStatus(task.taskId)}>Update Status</button>
                <button onClick={() => onDeleteTask(task.taskId)}>Delete Task</button>
                <button className="modal-close-btn" onClick={onClose}>Close</button>
            </div>
        </div>
    );
};

export default TaskDetails;