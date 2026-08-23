import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../lib/api'
import type { User } from '../lib/auth'
import type { Permission, Role } from '../lib/types'
import RoleFormModal from '../components/RoleFormModal'

export default function RoleManagePage() {
  const [roles, setRoles] = useState<Role[]>([])
  const [permissions, setPermissions] = useState<Permission[]>([])
  const [users, setUsers] = useState<User[]>([])
  const [loading, setLoading] = useState(true)
  const [formState, setFormState] = useState<'closed' | 'add' | Role>('closed')

  const [assignUserId, setAssignUserId] = useState<number | null>(null)
  const [assignRoleId, setAssignRoleId] = useState<number | null>(null)
  const [assigning, setAssigning] = useState(false)
  const [assignMessage, setAssignMessage] = useState('')

  useEffect(() => {
    Promise.all([api<Role[]>('/role'), api<Permission[]>('/permission'), api<User[]>('/user')])
      .then(([roleData, permissionData, userData]) => {
        setRoles(roleData)
        setPermissions(permissionData)
        setUsers(userData)
        setAssignUserId((prev) => prev ?? userData[0]?.id ?? null)
        setAssignRoleId((prev) => prev ?? roleData[0]?.id ?? null)
      })
      .finally(() => setLoading(false))
  }, [])

  const handleSaved = (role: Role) => {
    setRoles((prev) => {
      const exists = prev.some((r) => r.id === role.id)
      return exists ? prev.map((r) => (r.id === role.id ? role : r)) : [...prev, role]
    })
  }

  const handleDelete = async (role: Role) => {
    if (!window.confirm(`"${role.name}" 역할을 삭제할까요?`)) return
    await api(`/role/${role.id}`, { method: 'DELETE' })
    setRoles((prev) => prev.filter((r) => r.id !== role.id))
  }

  const handleAssign = async () => {
    if (assignUserId == null || assignRoleId == null) return
    setAssigning(true)
    setAssignMessage('')
    try {
      await api('/user/role', {
        method: 'PUT',
        body: JSON.stringify({ userId: assignUserId, roleId: assignRoleId }),
      })
      setAssignMessage('부여되었습니다.')
    } catch {
      setAssignMessage('부여에 실패했습니다.')
    } finally {
      setAssigning(false)
    }
  }

  return (
    <div className="min-h-screen page-bg">
      <header className="border-b border-ink-100 bg-white/80 px-6 py-4 backdrop-blur-md">
        <div className="mx-auto max-w-3xl">
          <Link to="/" className="text-sm text-ink-400 hover:text-brand-700">
            ← 목록으로
          </Link>
        </div>
      </header>

      <main className="mx-auto max-w-3xl px-6 py-8">
        <div className="flex items-center justify-between">
          <h1 className="text-xl font-semibold text-ink-900">역할 관리</h1>
          <button onClick={() => setFormState('add')} className="btn-primary">
            역할 추가
          </button>
        </div>

        <div className="mt-5 overflow-hidden rounded-2xl border border-ink-100 bg-white shadow-sm shadow-ink-900/[0.03]">
          {loading ? (
            <p className="p-6 text-sm text-ink-400">불러오는 중...</p>
          ) : roles.length === 0 ? (
            <p className="p-6 text-sm text-ink-400">등록된 역할이 없습니다.</p>
          ) : (
            <ul className="divide-y divide-ink-50">
              {roles.map((role) => (
                <li key={role.id} className="flex items-center justify-between gap-4 px-6 py-4">
                  <div>
                    <p className="text-sm font-medium text-ink-900">{role.name}</p>
                    <div className="mt-1 flex flex-wrap gap-1">
                      {role.permissions.length === 0 ? (
                        <span className="text-xs text-ink-400">권한 없음</span>
                      ) : (
                        role.permissions.map((p) => (
                          <span
                            key={p}
                            className="rounded-full bg-brand-50 px-2 py-0.5 text-[11px] text-brand-700"
                          >
                            {p}
                          </span>
                        ))
                      )}
                    </div>
                  </div>
                  <div className="flex shrink-0 gap-2">
                    <button onClick={() => setFormState(role)} className="btn-ghost">
                      수정
                    </button>
                    <button
                      onClick={() => handleDelete(role)}
                      className="rounded-lg px-3 py-1.5 text-xs text-ink-500 transition hover:bg-red-50 hover:text-red-500"
                    >
                      삭제
                    </button>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>

        <h2 className="mt-8 text-lg font-semibold text-ink-900">역할 부여</h2>
        <div className="mt-3 flex flex-wrap items-center gap-2 rounded-2xl border border-ink-100 bg-white p-4 shadow-sm shadow-ink-900/[0.03]">
          <select
            value={assignUserId ?? ''}
            onChange={(e) => setAssignUserId(Number(e.target.value))}
            className="field"
          >
            {users.map((u) => (
              <option key={u.id} value={u.id}>
                {u.nickname} ({u.loginId})
              </option>
            ))}
          </select>
          <select
            value={assignRoleId ?? ''}
            onChange={(e) => setAssignRoleId(Number(e.target.value))}
            className="field"
          >
            {roles.map((r) => (
              <option key={r.id} value={r.id}>
                {r.name}
              </option>
            ))}
          </select>
          <button
            onClick={handleAssign}
            disabled={assigning || assignUserId == null || assignRoleId == null}
            className="btn-primary"
          >
            부여
          </button>
          {assignMessage && <span className="text-sm text-ink-500">{assignMessage}</span>}
        </div>
      </main>

      {formState !== 'closed' && (
        <RoleFormModal
          role={formState === 'add' ? undefined : formState}
          permissions={permissions}
          onClose={() => setFormState('closed')}
          onSaved={handleSaved}
        />
      )}
    </div>
  )
}
