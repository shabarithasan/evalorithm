import React, { createContext, useState, useEffect, useCallback, ReactNode } from 'react';
import { User, Role, RegisterRequest } from '../types';
import { profileService } from '../services';
import { API_BASE_URL } from '../utils/constants';
import { auth } from '../config/firebase';
import { signInWithEmailAndPassword, createUserWithEmailAndPassword, signOut, onAuthStateChanged, GoogleAuthProvider, signInWithPopup } from 'firebase/auth';

interface AuthContextType {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  loginWithGoogle: (role?: Role) => Promise<void>;
  logout: () => void;
  hasRole: (role: Role) => boolean;
  isAdmin: boolean;
  isFaculty: boolean;
  isStudent: boolean;
  refreshUser: () => Promise<void>;
}

export const AuthContext = createContext<AuthContextType>({
  user: null,
  token: null,
  isAuthenticated: false,
  loading: true,
  login: async () => {},
  register: async () => {},
  loginWithGoogle: async (role?: Role) => {},
  logout: () => {},
  hasRole: () => false,
  isAdmin: false,
  isFaculty: false,
  isStudent: false,
  refreshUser: async () => {},
});

export const useAuth = () => React.useContext(AuthContext);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, async (firebaseUser) => {
      if (firebaseUser) {
        const idToken = await firebaseUser.getIdToken();
        setToken(idToken);
        
        // Check if we just registered via the form
        const isRegistering = sessionStorage.getItem('isRegistering');
        if (isRegistering === 'true') {
          try {
            await fetch(API_BASE_URL + '/profile', {
              headers: {
                'Authorization': `Bearer ${idToken}`,
                'X-Register-Role': sessionStorage.getItem('registerRole') || 'ROLE_STUDENT',
                'X-Register-FirstName': sessionStorage.getItem('registerFirstName') || '',
                'X-Register-LastName': sessionStorage.getItem('registerLastName') || ''
              }
            });
          } catch(e) {}
          sessionStorage.removeItem('isRegistering');
          sessionStorage.removeItem('registerRole');
          sessionStorage.removeItem('registerFirstName');
          sessionStorage.removeItem('registerLastName');
        }

        // Fetch user profile from backend
        try {
          console.log('[AuthContext] Fetching profile from backend...');
          const response = await profileService.getCurrentUser();
          console.log('[AuthContext] Profile response:', JSON.stringify(response));
          if (response.success && response.data) {
            console.log('[AuthContext] Setting user with role:', response.data.role);
            setUser(response.data);
          } else {
            console.warn('[AuthContext] Profile response not successful, signing out. Response:', response);
            await auth.signOut();
            setUser(null);
          }
        } catch (err: any) {
          console.error('[AuthContext] FAILED to fetch profile. Signing out. Error:', err?.response?.status, err?.response?.data || err?.message || err);
          await auth.signOut();
          setUser(null);
        }
      } else {
        setToken(null);
        setUser(null);
      }
      setLoading(false);
    });

    return () => unsubscribe();
  }, []);

  const login = useCallback(async (email: string, password: string) => {
    await signInWithEmailAndPassword(auth, email, password);
  }, []);

  const loginWithGoogle = useCallback(async (role?: Role) => {
    if (role) {
      sessionStorage.setItem('isRegistering', 'true');
      sessionStorage.setItem('registerRole', role);
    }
    const provider = new GoogleAuthProvider();
    await signInWithPopup(auth, provider);
  }, []);

  const register = useCallback(async (data: RegisterRequest) => {
    sessionStorage.setItem('isRegistering', 'true');
    sessionStorage.setItem('registerRole', data.role);
    sessionStorage.setItem('registerFirstName', data.firstName);
    sessionStorage.setItem('registerLastName', data.lastName);
    
    await createUserWithEmailAndPassword(auth, data.email, data.password);
  }, []);

  const logout = useCallback(async () => {
    await signOut(auth);
  }, []);

  const hasRole = useCallback((role: Role) => user?.role === role, [user]);

  const refreshUser = useCallback(async () => {
    try {
      const response = await profileService.getCurrentUser();
      if (response.success && response.data) {
        setUser(response.data);
      }
    } catch {}
  }, []);

  const isAdmin = user?.role === 'ROLE_ADMIN';
  const isFaculty = user?.role === 'ROLE_FACULTY';
  const isStudent = user?.role === 'ROLE_STUDENT';

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!token && !!user,
        loading,
        login,
        register,
        loginWithGoogle,
        logout,
        hasRole,
        isAdmin,
        isFaculty,
        isStudent,
        refreshUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};
