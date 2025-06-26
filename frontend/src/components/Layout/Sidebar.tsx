import React from 'react';
import { 
  LayoutDashboard, 
  Workflow, 
  Users, 
  CheckSquare, 
  Plus,
  TrendingUp
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { useLocation, Link } from 'react-router-dom';

const Sidebar: React.FC = () => {
  const { user } = useAuth();
  const location = useLocation();

  const adminNavItems = [
    { icon: LayoutDashboard, label: 'Dashboard', path: '/admin/dashboard' },
    { icon: Workflow, label: 'Workflows', path: '/admin/workflows' },
    { icon: Plus, label: 'Create Workflow', path: '/admin/workflows/create' },
    { icon: Users, label: 'Users', path: '/admin/users' },
    { icon: TrendingUp, label: 'Analytics', path: '/admin/analytics' },
  ];

  const userNavItems = [
    { icon: LayoutDashboard, label: 'Dashboard', path: '/user/dashboard' },
    { icon: CheckSquare, label: 'All Tasks', path: '/user/tasks' },
  ];

  const navItems = user?.role === 'admin' ? adminNavItems : userNavItems;

  return (
    <div className="w-64 bg-white shadow-sm border-r border-gray-200 h-full">
      <nav className="mt-8 px-4">
        <ul className="space-y-2">
          {navItems.map((item) => {
            const Icon = item.icon;
            const isActive = location.pathname === item.path;
            
            return (
              <li key={item.path}>
                <Link
                  to={item.path}
                  className={`flex items-center space-x-3 px-4 py-3 rounded-lg transition-colors ${
                    isActive
                      ? 'bg-blue-50 text-blue-700 border-r-2 border-blue-700'
                      : 'text-gray-700 hover:bg-gray-50 hover:text-gray-900'
                  }`}
                >
                  <Icon className="h-5 w-5" />
                  <span className="font-medium">{item.label}</span>
                </Link>
              </li>
            );
          })}
        </ul>
      </nav>
    </div>
  );
};

export default Sidebar;