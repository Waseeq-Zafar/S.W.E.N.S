import React, { useState } from 'react';
import { ArrowLeft } from 'lucide-react';
import { Link } from 'react-router-dom';

const CreateWorkflow: React.FC = () => {
  const [workflowName, setWorkflowName] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

    const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const accessToken = localStorage.getItem('accessToken');

    const makeRequest = async (token: string) => {
      return await fetch('http://localhost:8080/workflow', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(workflowName),
      });
    };

    let response = await makeRequest(accessToken || '');

    if (response.status === 401) {
      // Try to refresh token
      const refreshToken = localStorage.getItem('refreshToken');

      if (!refreshToken) {
        setSuccessMessage('❌ Session expired. Please log in again.');
        localStorage.clear();
        window.location.href = '/login';
        return;
      }

      try {
        const refreshRes = await fetch('http://localhost:8080/auth/refresh', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ refreshToken }),
        });

        if (!refreshRes.ok) {
          setSuccessMessage('❌ Session expired. Please log in again.');
          localStorage.clear();
          window.location.href = '/login';
          return;
        }

        const tokens = await refreshRes.json();
        localStorage.setItem('accessToken', tokens.accessToken);
        localStorage.setItem('refreshToken', tokens.refreshToken);

        // Retry original request with new accessToken
        response = await makeRequest(tokens.accessToken);
      } catch (err) {
        console.error('Error refreshing token:', err);
        setSuccessMessage('❌ Error occurred. Please log in again.');
        localStorage.clear();
        window.location.href = '/login';
        return;
      }
    }

    if (response.status === 201) {
      const result = await response.json();
      console.log('Workflow created:', result);
      setSuccessMessage('✅ Workflow created successfully!');
      setWorkflowName('');
    } else {
      setSuccessMessage('❌ Failed to create workflow.');
    }
  };


  return (
    <div className="space-y-6 max-w-xl mx-auto mt-10">
      <div className="flex items-center space-x-4">
        <Link
          to="/admin/workflows"
          className="inline-flex items-center text-gray-600 hover:text-gray-900"
        >
          <ArrowLeft className="h-5 w-5 mr-1" />
          Back to Workflows
        </Link>
      </div>

      <h1 className="text-2xl font-bold text-gray-900">Create New Workflow</h1>
      <p className="text-gray-600">Only Workflow Name is required to create a new one.</p>

      {successMessage && (
        <div className="p-3 rounded bg-green-100 text-green-800 font-medium border border-green-300">
          {successMessage}
        </div>
      )}

      <form onSubmit={handleSubmit} className="bg-white p-6 rounded shadow space-y-4 border border-gray-200">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Workflow Name
          </label>
          <input
            type="text"
            value={workflowName}
            onChange={(e) => setWorkflowName(e.target.value)}
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Enter workflow name"
            required
          />
        </div>

        <div className="flex justify-end">
          <button
            type="submit"
            className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          >
            Create Workflow
          </button>
        </div>
      </form>
    </div>
  );
};

export default CreateWorkflow;
