import type { ReactNode } from 'react'
import { Navigate } from 'react-router-dom'
import { useAuth } from './auth'

export default function RequireAuth({ children }: { children: ReactNode }) {
  const { user, loading } = useAuth()

  if (loading) return null
  if (!user) return <Navigate to="/login" replace />
  return <>{children}</>
}

export function RequireGuest({ children }: { children: ReactNode }) {
  const { user, loading } = useAuth()

  if (loading) return null
  if (user) return <Navigate to="/" replace />
  return <>{children}</>
}
