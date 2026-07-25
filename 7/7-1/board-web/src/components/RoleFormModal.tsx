import { useEffect, useState, type FormEvent } from 'react'
import { createPortal } from 'react-dom'
import { api } from '../lib/api'
import type { Permission, Role } from '../lib/types'

type Props = {
  role?: Role
  permissions: Permission[]
  onClose: () => void
  onSaved: (role: Role) => void
}

export default function RoleFormModal({ role, permissions, onClose, onSaved }: Props) {
  const isEdit = role != null
  const [name, setName] = useState(role?.name ?? '')
  const [selectedNames, setSelectedNames] = useState<Set<string>>(new Set(role?.permissions ?? []))
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    setSelectedNames(new Set(role?.permissions ?? []))
  }, [role])

  const togglePermission = (permissionName: string) => {
    setSelectedNames((prev) => {
      const next = new Set(prev)
      if (next.has(permissionName)) next.delete(permissionName)
      else next.add(permissionName)
      return next
    })
  }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError('')
    setSubmitting(true)
    try {
      const permissionIds = permissions.filter((p) => selectedNames.has(p.name)).map((p) => p.id)
      const saved = isEdit
        ? await api<Role>(`/role/${role!.id}`, {
            method: 'PUT',
            body: JSON.stringify({ name, permissionIds }),
          })
        : await api<Role>('/role', {
            method: 'POST',
            body: JSON.stringify({ name, permissionIds }),
          })
      onSaved(saved)
      onClose()
    } catch {
      setError('저장에 실패했습니다.')
    } finally {
      setSubmitting(false)
    }
  }

  return createPortal(
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30 backdrop-blur-sm">
      <div className="w-full max-w-sm rounded-2xl bg-white p-6 shadow-xl">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-gray-900">{isEdit ? '역할 수정' : '역할 추가'}</h2>
          <button onClick={onClose} className="text-gray-400 transition hover:text-gray-600" aria-label="닫기">
            ✕
          </button>
        </div>

        <form onSubmit={handleSubmit} className="mt-5 flex flex-col gap-3">
          <label className="flex flex-col gap-1 text-sm text-gray-600">
            이름
            <input
              className="rounded-lg border border-gray-200 px-3 py-2 text-sm outline-none transition focus:border-gray-400"
              placeholder="역할 이름"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
              autoFocus
            />
          </label>

          <div className="flex flex-col gap-1 text-sm text-gray-600">
            권한
            <div className="grid max-h-56 grid-cols-1 gap-1 overflow-y-auto rounded-lg border border-gray-200 p-2">
              {permissions.map((p) => (
                <label key={p.id} className="flex items-center gap-2 rounded px-2 py-1 text-sm text-gray-700 hover:bg-gray-50">
                  <input
                    type="checkbox"
                    checked={selectedNames.has(p.name)}
                    onChange={() => togglePermission(p.name)}
                  />
                  {p.name}
                </label>
              ))}
            </div>
          </div>

          {error && <p className="text-sm text-red-500">{error}</p>}
          <button
            type="submit"
            disabled={submitting || !name.trim()}
            className="mt-2 rounded-lg bg-gray-900 py-2 text-sm font-medium text-white transition hover:bg-gray-800 disabled:opacity-50"
          >
            저장
          </button>
        </form>
      </div>
    </div>,
    document.body,
  )
}
