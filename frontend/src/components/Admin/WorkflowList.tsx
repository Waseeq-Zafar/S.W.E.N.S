import React, { useEffect, useState } from 'react';
import { Workflow } from '../../types';
import { Link, useNavigate } from 'react-router-dom';
import { Plus } from 'lucide-react';

const WorkflowList: React.FC = () => {
  const [workflows, setWorkflows] = useState<Workflow[]>([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchWorkflows = async () => {
      try {
        const accessToken = localStorage.getItem('accessToken');
        let res = await fetch('http://localhost:8080/workflow', {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        });

        // Refresh token logic if expired
        if (res.status === 401) {
          const refreshToken = localStorage.getItem('refreshToken');
          const refreshRes = await fetch('http://localhost:8080/auth/refresh', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ refreshToken }),
          });

          if (!refreshRes.ok) throw new Error('Session expired. Please login again.');

          const newToken = await refreshRes.json();
          localStorage.setItem('accessToken', newToken.accessToken);

          res = await fetch('http://localhost:8080/workflow', {
            headers: { Authorization: `Bearer ${newToken.accessToken}` },
          });
        }

        const data = await res.json(); // ✅ Fix here
        const normalized = data.map((wf: any) => ({
          id: wf.workflowId, // ✅ Use workflowId
          name: wf.name,
          description: wf.description,
          status: wf.status,
          createdAt: wf.createdAt,
          createdBy: wf.createdBy,
        }));
        setWorkflows(normalized);
      } catch (err) {
        console.error(err);
      }
    };

    fetchWorkflows();
  }, []);

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'active':
        return 'bg-green-100 text-green-800';
      case 'inactive':
        return 'bg-red-100 text-red-800';
      case 'draft':
        return 'bg-yellow-100 text-yellow-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Workflows</h1>
          <p className="text-gray-600">Manage your organization's workflows</p>
        </div>
        <Link
          to="/admin/workflows/create"
          className="inline-flex items-center px-4 py-2 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 transition-colors"
        >
          <Plus className="h-4 w-4 mr-2" />
          Create Workflow
        </Link>
      </div>

      {/* Workflow Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {workflows.map((wf) => (
          <div
            key={wf.id}
            onClick={() => navigate(`/admin/workflows/${wf.id}`)}
            className="cursor-pointer bg-white border border-gray-200 rounded-xl shadow-sm p-6 hover:shadow-md transition"
          >
            <div className="flex justify-between items-center mb-2">
              <h3 className="text-lg font-semibold text-gray-900">{wf.name}</h3>
              <span className={`px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(wf.status)}`}>
                {wf.status}
              </span>
            </div>
            <p className="text-sm text-gray-600 mb-4">{wf.description}</p>
            <p className="text-xs text-gray-500">Created by: {wf.createdBy}</p>
            <p className="text-xs text-gray-500">Created: {new Date(wf.createdAt).toLocaleDateString()}</p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default WorkflowList;
