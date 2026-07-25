import { createContext, useContext, useEffect, useState, type ReactNode } from 'react'
import { api, ApiError } from './api'

export type User = {
  id: number
  loginId: string
  nickname: string
  userLevel: number
  roles: string[]
  permissions: string[]
  createdAt: string
}

export function hasPermission(user: User | null, permission: string): boolean {
  return user != null && user.permissions.includes(permission)
}

export function hasAnyPermission(user: User | null, permissions: string[]): boolean {
  return permissions.some((permission) => hasPermission(user, permission))
}

type AuthContextValue = {
  user: User | null
  loading: boolean
  login: (loginId: string, password: string) => Promise<void>
  signUp: (loginId: string, password: string, nickname: string) => Promise<void>
  logout: () => Promise<void>
  setUser: (user: User) => void
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api<User>('/auth/me')
      .then(setUser)
      .catch(() => setUser(null))
      .finally(() => setLoading(false))
  }, [])

  const login = async (loginId: string, password: string) => {
    const loggedInUser = await api<User>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ loginId, password }),
    })
    setUser(loggedInUser)
  }

  const signUp = async (loginId: string, password: string, nickname: string) => {
    await api<User>('/auth/sign-up', {
      method: 'POST',
      body: JSON.stringify({ loginId, password, nickname }),
    })
    await login(loginId, password)
  }

  const logout = async () => {
    await api('/auth/logout', { method: 'POST' })
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, signUp, logout, setUser }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}

export { ApiError }
