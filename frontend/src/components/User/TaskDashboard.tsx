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

const UserDashboard: React.FC = () => {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [dueSoonTask, setDueSoonTask] = useState<Task | null>(null);
  const [timeLeft, setTimeLeft] = useState('');
  const navigate = useNavigate();

  const calculateTimeLeft = (due: string) => {
    const diff = new Date(due).getTime() - new Date().getTime();
    if (diff <= 0) return 'Expired';
    const hours = Math.floor(diff / (1000 * 60 * 60));
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    const seconds = Math.floor((diff % (1000 * 60)) / 1000);
    return `${hours}h ${minutes}m ${seconds}s left`;
  };

  const refreshToken = async () => {
    const refresh = localStorage.getItem('refreshToken');
    try {
      const res = await fetch('http://localhost:8080/auth/refresh', {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${refresh}`,
        },
      });

      if (!res.ok) throw new Error('Refresh token failed');

      const data = await res.json();
      localStorage.setItem('accessToken', data.accessToken);
      return data.accessToken;
    } catch (err) {
      console.error('Unable to refresh token:', err);
      return null;
    }
  };

  const fetchTasks = async () => {
    let token = localStorage.getItem('accessToken');
    let res = await fetch('http://localhost:8080/task/user', {
      headers: { Authorization: `Bearer ${token}` },
    });

    if (res.status === 401) {
      const newToken = await refreshToken();
      if (!newToken) return;
      res = await fetch('http://localhost:8080/task/user', {
        headers: { Authorization: `Bearer ${newToken}` },
      });
    }

    if (!res.ok) {
      console.error('Failed to fetch tasks');
      return;
    }

    const data = await res.json();
    const active = data.filter((task: Task) => task.status.toLowerCase() !== 'completed');
    setTasks(active);

    // Determine nearest due task
    const nearest = active.reduce((soonest: Task | null, curr: Task) => {
      if (!soonest || new Date(curr.dueDate) < new Date(soonest.dueDate)) {
        return curr;
      }
      return soonest;
    }, null);

    setDueSoonTask(nearest);
    if (nearest) {
      setTimeLeft(calculateTimeLeft(nearest.dueDate));
    }
  };

  useEffect(() => {
    fetchTasks();
  }, []);

  // Live countdown update
  useEffect(() => {
    if (!dueSoonTask) return;

    setTimeLeft(calculateTimeLeft(dueSoonTask.dueDate));

    const interval = setInterval(() => {
      setTimeLeft(calculateTimeLeft(dueSoonTask.dueDate));
    }, 1000); // every second

    return () => clearInterval(interval);
  }, [dueSoonTask]);

  return (
    <div className="p-6 space-y-6 relative">
      <div className="flex justify-between items-start">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Active Tasks</h1>
        </div>

        {dueSoonTask && (
          <div className="bg-red-100 text-red-800 px-4 py-3 rounded-lg shadow-sm border border-red-300 text-sm max-w-sm">
            <div className="font-semibold text-md">⏰ Upcoming Due:</div>
            <div className="font-bold">{dueSoonTask.taskName}</div>
            <div>Due by: {new Date(dueSoonTask.dueDate).toLocaleString()}</div>
            <div className="text-xs mt-1">{timeLeft}</div>
          </div>
        )}
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {tasks.length > 0 ? (
          tasks.map((task) => (
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
              <button
                onClick={() => navigate(`/user/task/update/${task.taskId}`)}
                className="mt-2 px-4 py-2 text-sm rounded bg-blue-600 text-white hover:bg-blue-700 transition"
              >
                Update Task
              </button>
            </div>
          ))
        ) : (
          <div className="text-center text-gray-500 text-lg col-span-full mt-10">
            🚫 No active tasks found.
          </div>
        )}
      </div>
    </div>
  );
};

export default UserDashboard;
