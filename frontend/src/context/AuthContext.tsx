import React, {
  createContext,
  useContext,
  useState,
  useEffect,
  ReactNode,
} from 'react';
import { User, AuthContextType } from '../types';

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchUserFromToken = async () => {
      let accessToken = localStorage.getItem('accessToken');
      const refreshToken = localStorage.getItem('refreshToken');

      if (!accessToken) {
        setLoading(false);
        return;
      }

      try {
        let res = await fetch('http://localhost:8080/auth/validate', {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        });

        // If access token is invalid, try refreshing it
        if (res.status === 401 && refreshToken) {
          const refreshRes = await fetch('http://localhost:8080/auth/refresh', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ refreshToken }),
          });

          if (!refreshRes.ok) {
            throw new Error('Session expired');
          }

          const refreshData = await refreshRes.json();
          localStorage.setItem('accessToken', refreshData.accessToken);
          localStorage.setItem('refreshToken', refreshData.refreshToken);
          accessToken = refreshData.accessToken;

          // Retry validation
          res = await fetch('http://localhost:8080/auth/validate', {
            headers: {
              Authorization: `Bearer ${accessToken}`,
            },
          });

          if (!res.ok) {
            throw new Error('Re-validation failed after refresh');
          }
        }

        const data = await res.json();
        const userData: User = {
          email: data.email,
          role: data.role,
          name: localStorage.getItem('userName') || '',
          id: '',
          createdAt: '',
          isActive: true,
        };

        setUser(userData);
      } catch (err) {
        console.error('Auth validation failed:', err);
        logout();
      } finally {
        setLoading(false);
      }
    };

    fetchUserFromToken();
  }, []);

  const logout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userName');
    localStorage.removeItem('userEmail');
    setUser(null);
  };

  const value: AuthContextType = {
    user,
    setUser,
    logout,
    loading,
    setLoading,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};
