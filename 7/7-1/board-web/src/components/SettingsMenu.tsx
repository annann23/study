import { useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { useClickOutside } from '../lib/useClickOutside'
import { hasPermission, useAuth } from '../lib/auth'

export default function SettingsMenu() {
  const [open, setOpen] = useState(false)
  const menuRef = useRef<HTMLDivElement>(null)
  const { user } = useAuth()
  const canApproveBoards = hasPermission(user, 'BOARD_UPDATE')

  useClickOutside(menuRef, () => setOpen(false))

  return (
    <div className="relative" ref={menuRef}>
      <button
        onClick={() => setOpen((v) => !v)}
        aria-label="설정"
        className="flex h-9 w-9 items-center justify-center rounded-full text-ink-400 transition hover:bg-brand-50 hover:text-brand-700"
      >
        ⚙
      </button>

      {open && (
        <div className="absolute right-0 mt-2 w-44 overflow-hidden rounded-xl border border-ink-100 bg-white py-1 shadow-lg shadow-ink-900/5">
          <Link
            to="/admin/users"
            onClick={() => setOpen(false)}
            className="block w-full px-4 py-2 text-left text-sm text-ink-700 transition hover:bg-brand-50"
          >
            회원 등급 관리
          </Link>
          <Link
            to="/admin/roles"
            onClick={() => setOpen(false)}
            className="block w-full px-4 py-2 text-left text-sm text-ink-700 transition hover:bg-brand-50"
          >
            역할 관리
          </Link>
          {canApproveBoards && (
            <Link
              to="/admin/boards"
              onClick={() => setOpen(false)}
              className="block w-full px-4 py-2 text-left text-sm text-ink-700 transition hover:bg-brand-50"
            >
              게시판 생성 요청
            </Link>
          )}
        </div>
      )}
    </div>
  )
}
