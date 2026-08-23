import { useRef, useState } from 'react'
import { useAuth } from '../lib/auth'
import { useClickOutside } from '../lib/useClickOutside'
import EditProfileModal from './EditProfileModal'

export default function UserMenu() {
  const { user, logout } = useAuth()
  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState(false)
  const menuRef = useRef<HTMLDivElement>(null)

  useClickOutside(menuRef, () => setOpen(false))

  if (!user) return null

  const roleLabel = user.roles.length > 0 ? user.roles.join(', ') : '역할 없음'

  const initial = user.nickname.charAt(0).toUpperCase()

  return (
    <div className="relative" ref={menuRef}>
      <button
        onClick={() => setOpen((v) => !v)}
        className="flex items-center gap-2 rounded-full py-1 pl-1 pr-3 transition hover:bg-brand-50"
      >
        <span className="flex h-8 w-8 items-center justify-center rounded-full bg-linear-to-br from-brand-600 to-brand-400 text-sm font-medium text-white">
          {initial}
        </span>
        <span className="flex flex-col items-start leading-tight">
          <span className="text-sm font-medium text-ink-700">{user.nickname}</span>
          <span className="text-[11px] text-ink-400">{roleLabel}</span>
        </span>
      </button>

      {open && (
        <div className="absolute right-0 mt-2 w-48 overflow-hidden rounded-xl border border-ink-100 bg-white py-1 shadow-lg shadow-ink-900/5">
          <button
            onClick={() => {
              setEditing(true)
              setOpen(false)
            }}
            className="block w-full px-4 py-2 text-left text-sm text-ink-700 transition hover:bg-brand-50"
          >
            정보 수정
          </button>
          <button
            onClick={() => logout()}
            className="block w-full px-4 py-2 text-left text-sm text-red-500 transition hover:bg-brand-50"
          >
            로그아웃
          </button>
        </div>
      )}

      {editing && <EditProfileModal onClose={() => setEditing(false)} />}
    </div>
  )
}
