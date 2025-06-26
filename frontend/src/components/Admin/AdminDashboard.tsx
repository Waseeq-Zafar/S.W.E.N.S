import React, { useEffect, useState } from 'react';
import { Users, CheckCircle } from 'lucide-react';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';

interface Workflow {
  id: string;
  name: string;
  completionPercentage: number;
}

const AdminDashboard: React.FC = () => {
  const [totalUsers, setTotalUsers] = useState<number>(0);
  const [activeUsers, setActiveUsers] = useState<number>(0);
  const [workflows, setWorkflows] = useState<Workflow[]>([]);

  const navigate = useNavigate();

  const fetchWithAuth = async (url: string): Promise<Response> => {
    const token = localStorage.getItem('accessToken');
    let res = await fetch(url, { headers: { Authorization: `Bearer ${token}` } });
    if (res.status === 401) {
      const refreshToken = localStorage.getItem('refreshToken');
      const refreshRes = await fetch('http://localhost:8080/auth/refresh', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken }),
      });
      if (!refreshRes.ok) {
        toast.error('Session expired, please log in again.');
        localStorage.clear();
        window.location.href = '/login';
        throw new Error('Session expired');
      }
      const data = await refreshRes.json();
      localStorage.setItem('accessToken', data.accessToken);
      res = await fetch(url, { headers: { Authorization: `Bearer ${data.accessToken}` } });
    }
    return res;
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [userRes, freeRes, wfRes] = await Promise.all([
          fetchWithAuth('http://localhost:8080/task/admin/role?role=user'),
          fetchWithAuth('http://localhost:8080/task/admin/free'),
          fetchWithAuth('http://localhost:8080/workflow'),
        ]);

        const users = await userRes.json();
        setTotalUsers(users.length);

        const freeUsers = await freeRes.json();
        setActiveUsers(freeUsers.length);

        const wfRaw = await wfRes.json();
        const wfData: Workflow[] = wfRaw.map((wf: any) => ({
          id: wf.workflowId, // 👈 use workflowId as id
          name: wf.name,
          completionPercentage: wf.completionPercentage,
        }));
        setWorkflows(wfData);
      } catch (err: any) {
        console.error(err);
        toast.error(err.message);
      }
    };

    fetchData();
  }, []);

  const ongoing = workflows.filter(wf => wf.completionPercentage < 100);
  const completed = workflows.filter(wf => wf.completionPercentage === 100);

  return (
    <div className="space-y-8">
      <header>
        <h1 className="text-2xl font-bold">Admin Dashboard</h1>
        <p className="text-gray-600">Overview of your organization's activity</p>
      </header>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {[{ name: 'Total Users', value: totalUsers, icon: Users, bgIcon: 'bg-blue-100 text-blue-600' },
          { name: 'Active Users', value: activeUsers, icon: CheckCircle, bgIcon: 'bg-green-100 text-green-600' }].map(stat => {
          const Icon = stat.icon;
          return (
            <div key={stat.name} className="flex items-center bg-white p-6 rounded-lg shadow-sm border">
              <div className={`p-3 rounded-full ${stat.bgIcon}`}>
                <Icon className="h-8 w-8"/>
              </div>
              <div className="ml-4">
                <p className="text-gray-600">{stat.name}</p>
                <p className="text-2xl font-semibold">{stat.value}</p>
              </div>
            </div>
          );
        })}
      </div>

      <section>
        <h2 className="text-xl font-semibold">Ongoing Workflows</h2>
        {ongoing.length ? (
          <ul className="space-y-2 mt-4">
            {ongoing.map(wf => (
              <li
                key={wf.id}
                onClick={() => navigate(`/admin/workflows/${wf.id}`)}
                className="bg-white p-4 rounded-lg shadow-sm border cursor-pointer hover:bg-gray-100 transition"
              >
                <p className="font-medium">{wf.name.replace(/"/g, "")}</p>
                <p className="text-sm text-gray-500">Progress: {wf.completionPercentage}%</p>
              </li>
            ))}
          </ul>
        ) : (
          <p className="text-gray-500 mt-2">No ongoing workflows.</p>
        )}
      </section>
    </div>
  );
};

export default AdminDashboard;
