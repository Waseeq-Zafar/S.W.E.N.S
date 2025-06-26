import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { toast } from 'react-toastify';

interface BackendUser {
  id: string;
  name: string;
  email: string;
  role: string;
}

interface AssignedUser {
  userId: string;
  userName: string;
  email: string;
}

const CreateTask: React.FC = () => {
  const { workflowId } = useParams();
  const [taskName, setTaskName] = useState('');
  const [description, setDescription] = useState('');
  const [status, setStatus] = useState('Start');
  const [dueDate, setDueDate] = useState('');
  const [allUsers, setAllUsers] = useState<BackendUser[]>([]);
  const [selectedUsers, setSelectedUsers] = useState<AssignedUser[]>([]);
  const [userMenuOpen, setUserMenuOpen] = useState(false);

  const navigate = useNavigate();

  const fetchFreeUsers = async () => {
    try {
      const token = localStorage.getItem('accessToken');
      const res = await fetch('http://localhost:8080/task/admin/free', {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!res.ok) throw new Error('Failed to fetch users');
      const data = await res.json();
      setAllUsers(data);
    } catch (err: any) {
      toast.error(err.message || 'Error loading users');
    }
  };

  const toggleUser = (user: BackendUser) => {
    const mappedUser: AssignedUser = {
      userId: user.id,
      userName: user.name,
      email: user.email,
    };

    const alreadySelected = selectedUsers.find((u) => u.userId === user.id);
    if (alreadySelected) {
      setSelectedUsers((prev) => prev.filter((u) => u.userId !== user.id));
    } else {
      setSelectedUsers((prev) => [...prev, mappedUser]);
    }
  };

  const handleSubmit = async () => {
    if (!workflowId) return toast.error('Missing workflowId');
    if (!taskName || !description || !dueDate) return toast.error('Fill all fields');
    if (selectedUsers.length === 0) return toast.error('Select at least one user');

    const body = {
      description,
      status,
      taskName,
      dueDate: new Date(dueDate).toISOString(),
      assignedUsers: selectedUsers,
      workflowId,
    };

    try {
      const token = localStorage.getItem('accessToken');
      const res = await fetch('http://localhost:8080/task/admin', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(body),
      });

      if (!res.ok) throw new Error('Task creation failed');
      toast.success('✅ Task created successfully');
      navigate(`/admin/workflows/${workflowId}`);
    } catch (err: any) {
      toast.error(err.message || 'Error creating task');
    }
  };

  return (
    <div className="max-w-2xl mx-auto space-y-6 p-6 bg-white shadow rounded">
    <h2 className="text-xl font-bold">Create Task</h2>

    <input
    className="w-full p-2 border rounded"
    placeholder="Task Name"
    value={taskName}
    onChange={(e) => setTaskName(e.target.value)}
    />

    <textarea
    className="w-full p-2 border rounded mt-2"
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
          onClick={() => {
            if (!userMenuOpen) fetchFreeUsers();
            setUserMenuOpen((prev) => !prev);
          }}
          className="w-full p-2 border rounded bg-gray-50 text-left"
        >
          {selectedUsers.length > 0
            ? `Assigned to: ${selectedUsers.map((u) => u.userName).join(', ')}`
            : '➕ Attach Users'}
        </button>

        {userMenuOpen && (
          <div className="absolute z-10 mt-2 bg-white shadow-lg border rounded w-full max-h-60 overflow-auto">
            {allUsers.map((user) => {
              const isSelected = selectedUsers.some((u) => u.userId === user.id);
              return (
                <div
                  key={user.id}
                  className="flex items-center justify-between p-2 hover:bg-gray-100"
                >
                  <div className="flex flex-col">
                    <span className="font-medium">{user.name}</span>
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

      {/* Buttons */}
      <div className="flex justify-end gap-4">
        <button
          onClick={() => navigate(-1)}
          className="px-4 py-2 bg-gray-300 rounded hover:bg-gray-400"
        >
          Cancel
        </button>

        <button
          disabled={selectedUsers.length === 0}
          onClick={handleSubmit}
          className={`px-4 py-2 rounded text-white ${
            selectedUsers.length > 0
              ? 'bg-blue-600 hover:bg-blue-700'
              : 'bg-gray-400 cursor-not-allowed'
          }`}
        >
          Assign
        </button>
      </div>
    </div>
  );
};

export default CreateTask;
