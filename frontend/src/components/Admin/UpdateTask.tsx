import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';

interface User {
  userId: string;
  userName: string;
  email: string;
}

const UpdateTask: React.FC = () => {
  const { taskId } = useParams();
  const navigate = useNavigate();

  const [status, setStatus] = useState('Start');
  const [description, setDescription] = useState('');
  const [dueDate, setDueDate] = useState('');
  const [assignedUsers, setAssignedUsers] = useState<User[]>([]);
  const [originalUsers, setOriginalUsers] = useState<User[]>([]);
  const [allUsers, setAllUsers] = useState<User[]>([]);
  const [userMenuOpen, setUserMenuOpen] = useState(false);

  const fetchWithRefresh = async (url: string, options = {}): Promise<Response> => {
    let accessToken = localStorage.getItem('accessToken');
    const refreshToken = localStorage.getItem('refreshToken');

    let res = await fetch(url, {
      ...options,
      headers: {
        ...(options as any).headers,
        Authorization: `Bearer ${accessToken}`,
      },
    });

    if (res.status === 401 && refreshToken) {
      const refreshRes = await fetch('http://localhost:8080/auth/refresh', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken }),
      });

      if (!refreshRes.ok) {
        localStorage.clear();
        window.location.href = '/login';
        throw new Error('Session expired. Please login again.');
      }

      const data = await refreshRes.json();
      localStorage.setItem('accessToken', data.accessToken);
      localStorage.setItem('refreshToken', data.refreshToken);

      res = await fetch(url, {
        ...options,
        headers: {
          ...(options as any).headers,
          Authorization: `Bearer ${data.accessToken}`,
        },
      });
    }

    return res;
  };

  useEffect(() => {
    const fetchTask = async () => {
      try {
        const res = await fetchWithRefresh(`http://localhost:8080/task/admin/${taskId}`);
        if (!res.ok) throw new Error('Failed to fetch task');
        const data = await res.json();

        setStatus(data.taskStatus || 'Start');
        setDescription(data.description || '');
        setDueDate(data.dueDate ? new Date(data.dueDate).toISOString().slice(0, 16) : '');
        const formattedUsers = (data.assignedUsers || []).map((u: any) => ({
          userId: u.userId,
          userName: u.userName,
          email: u.email,
        }));
        setAssignedUsers(formattedUsers);
        setOriginalUsers(formattedUsers);
      } catch (err: any) {
        toast.error(err.message || 'Error loading task');
      }
    };

    const fetchAllUsers = async () => {
      try {
        const res = await fetchWithRefresh('http://localhost:8080/task/admin/free');
        const data = await res.json();
        const formatted = data.map((u: any) => ({
          userId: u.id,
          userName: u.name,
          email: u.email,
        }));
        setAllUsers((prev) => [...prev, ...formatted]); // Include original assigned too
      } catch (err: any) {
        toast.error(err.message || 'Error fetching users');
      }
    };

    fetchTask();
    fetchAllUsers();
  }, [taskId]);

  const toggleUser = (user: User) => {
    if (assignedUsers.find((u) => u.userId === user.userId)) {
      setAssignedUsers((prev) => prev.filter((u) => u.userId !== user.userId));
    } else {
      setAssignedUsers((prev) => [...prev, user]);
    }
  };

  const handleUpdate = async () => {
    if (!taskId) return toast.error('Missing task ID');

    const updatedBody = {
      status,
      description,
      dueDate: new Date(dueDate).toISOString(),
      assignedUsers,
    };

    const removedUsers = originalUsers.filter(
      (ou) => !assignedUsers.find((au) => au.userId === ou.userId)
    );
    const addedUsers = assignedUsers.filter(
      (au) => !originalUsers.find((ou) => ou.userId === au.userId)
    );

    try {
      const token = localStorage.getItem('accessToken');

      // 1. Update the task
      const res = await fetchWithRefresh(`http://localhost:8080/task/admin/${taskId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(updatedBody),
      });

      if (!res.ok) throw new Error('Task update failed');

      // 2. Mark removed users as free
      for (const user of removedUsers) {
        await fetchWithRefresh(`http://localhost:8080/task/admin/free`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({ userId: user.userId }),
        });
      }

      // 3. Mark newly added users as assigned (if needed)
      for (const user of addedUsers) {
        await fetchWithRefresh(`http://localhost:8080/task/admin/assign`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({ userId: user.userId }),
        });
      }

      toast.success('✅ Task updated successfully');
      navigate(-1);
    } catch (err: any) {
      toast.error(err.message || 'Error updating task');
    }
  };

  return (
    <div className="max-w-2xl mx-auto space-y-6 p-6 bg-white shadow rounded">
      <h2 className="text-xl font-bold">Update Task</h2>

      <textarea
        className="w-full p-2 border rounded"
        placeholder="Description"
        value={description}
        onChange={(e) => setDescription(e.target.value)}
      />

      <div className="space-y-1">
        <label htmlFor="due-date" className="block text-sm font-medium text-gray-700">
          Due Date
        </label>
        <input
          id="due-date"
          type="datetime-local"
          className="w-full p-2 border rounded"
          value={dueDate}
          onChange={(e) => setDueDate(e.target.value)}
        />
      </div>

      {/* User Selector */}
      <div className="relative">
        <button
          onClick={() => setUserMenuOpen((prev) => !prev)}
          className="w-full p-2 border rounded bg-gray-50 text-left"
        >
          {assignedUsers.length > 0
            ? `Assigned: ${assignedUsers.map((u) => u.userName).join(', ')}`
            : '➕ Attach Users'}
        </button>

        {userMenuOpen && (
          <div className="absolute z-10 mt-2 bg-white shadow-lg border rounded w-full max-h-60 overflow-auto">
            {[...new Map([...assignedUsers, ...allUsers].map(item => [item.userId, item])).values()].map((user) => {
              const isSelected = assignedUsers.some((u) => u.userId === user.userId);
              return (
                <div
                  key={user.userId}
                  className="flex items-center justify-between p-2 hover:bg-gray-100"
                >
                  <div className="flex flex-col">
                    <span className="font-medium">{user.userName}</span>
                    <span className="text-sm text-gray-500">{user.email}</span>
                  </div>
                  <input
                    type="checkbox"
                    checked={isSelected}
                    onChange={() => toggleUser(user)}
                    className="ml-2 cursor-pointer"
                  />
                </div>
              );
            })}
          </div>
        )}
      </div>

      {/* Status Dropdown */}
      <select
        className="w-full p-2 border rounded"
        value={status}
        onChange={(e) => setStatus(e.target.value)}
      >
        <option value="Start">Start</option>
        <option value="In Progress">In Progress</option>
        <option value="Completed">Completed</option>
      </select>

      <div className="flex justify-end gap-4">
        <button
          onClick={() => navigate(-1)}
          className="px-4 py-2 bg-gray-300 rounded hover:bg-gray-400"
        >
          Cancel
        </button>
        <button
          onClick={handleUpdate}
          className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
        >
          Update Task
        </button>
      </div>
    </div>
  );
};

export default UpdateTask;
