import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';

interface AssignedUser {
  userId: string;
  userName: string;
  email: string;
}

interface Task {
  taskId: string;
  taskName: string;
  taskStatus: string;
  description?: string;
  assignedUsers: AssignedUser[];
  workflowId: string;
}

interface WorkflowData {
  id: string; // this is workflowId like 'workflow-...'
  name: string;
  tasks: Task[];
  completionPercentage: number;
}

const WorkflowDetails: React.FC = () => {
  const { workflowId } = useParams();
  const [workflow, setWorkflow] = useState<WorkflowData | null>(null);
  const navigate = useNavigate();

  const fetchWithRefresh = async (url: string, options: RequestInit = {}) => {
    let accessToken = localStorage.getItem('accessToken');
    const refreshToken = localStorage.getItem('refreshToken');

    const authHeaders = {
      ...options.headers,
      Authorization: `Bearer ${accessToken}`,
    };

    let res = await fetch(url, { ...options, headers: authHeaders });

    if (res.status === 401 && refreshToken) {
      const refreshRes = await fetch('http://localhost:8080/auth/refresh', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken }),
      });

      if (!refreshRes.ok) {
        localStorage.clear();
        window.location.href = '/login';
        throw new Error('Session expired, please log in again.');
      }

      const newTokens = await refreshRes.json();
      localStorage.setItem('accessToken', newTokens.accessToken);
      localStorage.setItem('refreshToken', newTokens.refreshToken);

      const retryHeaders = {
        ...options.headers,
        Authorization: `Bearer ${newTokens.accessToken}`,
      };

      res = await fetch(url, { ...options, headers: retryHeaders });
    }

    return res;
  };

  const fetchWorkflow = async () => {
    try {
      const res = await fetchWithRefresh(`http://localhost:8080/workflow/${workflowId}`);
      if (!res.ok) throw new Error('Failed to fetch workflow details');
      const data = await res.json();

      const normalized: WorkflowData = {
        id: data.workflowId,
        name: data.name,
        tasks: data.tasks,
        completionPercentage: data.completionPercentage,
      };

      setWorkflow(normalized);
    } catch (err: any) {
      toast.error(err.message || 'Error loading workflow');
    }
  };

  useEffect(() => {
    fetchWorkflow();
  }, [workflowId]);



  if (!workflow) return <div>Loading...</div>;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold">Workflow: {workflow.name.replace(/"/g, '')}</h1>
      <p className="text-gray-600">Completion: {workflow.completionPercentage}%</p>

      <div className="space-y-4">
        {workflow.tasks.length > 0 ? (
          workflow.tasks.map((task) => (
            <div key={task.taskId} className="p-4 bg-white border rounded shadow-sm flex justify-between items-start">
              <div>
                <h3 className="font-semibold text-lg">{task.taskName}</h3>
                <p className="text-sm text-gray-600">
                  Assigned to: {task.assignedUsers.map((u) => u.userName).join(', ') || 'N/A'}
                </p>
                <p className="text-sm text-blue-500 mt-1">Status: {task.taskStatus}</p>
              </div>
              <div className="space-x-2">
            {workflow.completionPercentage !== 100 && (
            <button
                onClick={() => navigate(`/admin/task/update/${task.taskId}`)}
                className="px-3 py-1 bg-yellow-500 text-white text-sm rounded hover:bg-yellow-600"
            >
                Update
            </button>
            )}
              </div>
            </div>
          ))
        ) : (
          <p className="text-gray-500">No tasks found in this workflow.</p>
        )}
      </div>

      <button
        onClick={() => navigate(`/admin/workflows/${workflow.id}/create-task`)}
        className="mt-4 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
      >
        + Create Task
      </button>
    </div>
  );
};

export default WorkflowDetails;
