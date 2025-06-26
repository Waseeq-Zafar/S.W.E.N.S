import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

interface AssignedUser {
  userId: string;
  userName: string;
  email: string;
}

interface Task {
  taskId: string;
  taskName: string;
  description: string;
  status: string;
  dueDate: string;
  assignedUsers: AssignedUser[];
  createdAt: string;
  updatedAt: string;
  workflowId: string;
}

const UserAllTasks: React.FC = () => {
  const [tasks, setTasks] = useState<Task[]>([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchTasks = async () => {
      try {
        const token = localStorage.getItem('accessToken');
        const res = await fetch('http://localhost:8080/task/user', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!res.ok) throw new Error('Failed to fetch tasks');

        const data = await res.json();
        const completedTasks = data.filter((task: Task) => task.status.toLowerCase() === 'completed');
        setTasks(completedTasks);
      } catch (err) {
        console.error('Error fetching tasks:', err);
      }
    };

    fetchTasks();
  }, []);

  return (
    <div className="p-6 space-y-6">
      <h1 className="text-2xl font-bold text-gray-900">Completed Tasks</h1>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {tasks.map((task) => (
          <div key={task.taskId} className="bg-white rounded-xl border shadow-sm p-6 space-y-3">
            <div className="text-xl font-semibold text-gray-800">{task.taskName}</div>
            <p className="text-gray-600">{task.description}</p>
            <p className="text-sm text-gray-700">
              <strong>Status:</strong> {task.status}
            </p>
            <p className="text-sm text-gray-700">
              <strong>Due:</strong> {new Date(task.dueDate).toLocaleString()}
            </p>
            <p className="text-sm text-gray-700">
              <strong>Assigned to Workflow:</strong> {task.workflowId}
            </p>
            {/* No Update Button for completed tasks */}
          </div>
        ))}
      </div>
    </div>
  );
};

export default UserAllTasks;
