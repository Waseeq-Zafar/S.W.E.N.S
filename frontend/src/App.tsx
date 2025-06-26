import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/Common/ProtectedRoute';
import DashboardLayout from './components/Layout/DashboardLayout';
import LoginForm from './components/Auth/LoginForm';
import SignupForm from './components/Auth/SignupForm';
import AdminDashboard from './components/Admin/AdminDashboard';
import WorkflowList from './components/Admin/WorkflowList';
import CreateWorkflow from './components/Admin/CreateWorkflow';
import TaskDashboard from './components/User/TaskDashboard';

import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import WorkflowDetails from './components/Admin/WorkflowDetail';
import CreateTask from './components/Admin/CreateTask';
import UpdateTask from './components/Admin/UpdateTask';
import UpdateUserTask from './components/User/UpdateUserTask';
import UserAllTasks from './components/User/UserAllTasks';

const AppRoutes = () => {
  const location = useLocation(); // ✅ Needed for forcing remount

  return (
    <Routes>

      {/* Public Routes */}
      <Route path="/login" element={<LoginForm />} />
      <Route path="/signup" element={<SignupForm />} />

      {/* Admin Routes with Nested Layout */}
      <Route path="/admin" element={<ProtectedRoute requiredRole="admin" />}>
        <Route element={<DashboardLayout />}>
          <Route path="dashboard" element={<AdminDashboard />} />
          <Route path="workflows" element={<WorkflowList />} />
          <Route path="workflows/create" element={<CreateWorkflow />} />

          {/* 👇 Add `key={location.pathname}` here */}
          <Route
            path="workflows/:workflowId"
            element={<WorkflowDetails key={location.pathname} />}
          />

          <Route path="workflows/:workflowId/create-task" element={<CreateTask />} />
          <Route path="/admin/task/update/:taskId" element={<UpdateTask />} />
          <Route path="users" element={<div>Users Management (Coming Soon)</div>} />
          <Route path="analytics" element={<div>Analytics (Coming Soon)</div>} />
          <Route index element={<Navigate to="dashboard" replace />} />
        </Route>
      </Route>

      {/* User Routes with Nested Layout */}
      <Route path="/user" element={<ProtectedRoute requiredRole="user" />}>
        <Route element={<DashboardLayout />}>
          <Route path="dashboard" element={<TaskDashboard />} />
          <Route path="tasks" element={<UserAllTasks />} />
           <Route path="/user/task/update/:taskId" element={<UpdateUserTask/>} />
          <Route index element={<Navigate to="dashboard" replace />} />
        </Route>
      </Route>

      {/* Default Route */}
      <Route path="/" element={<Navigate to="/login" replace />} />

    </Routes>
  );
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <>
          <AppRoutes />
          <ToastContainer position="top-center" autoClose={3000} hideProgressBar={false} />
        </>
      </Router>
    </AuthProvider>
  );
}

export default App;
