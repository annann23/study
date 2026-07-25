import { useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { useClickOutside } from '../lib/useClickOutside'

export default function SettingsMenu() {
  const [open, setOpen] = useState(false)
  const menuRef = useRef<HTMLDivElement>(null)

  useClickOutside(menuRef, () => setOpen(false))

  return (
    <div className="relative" ref={menuRef}>
      <button
        onClick={() => setOpen((v) => !v)}
        aria-label="설정"
        className="flex h-9 w-9 items-center justify-center rounded-full text-gray-400 transition hover:bg-gray-100 hover:text-gray-700"
      >
        ⚙
      </button>

      {open && (
        <div className="absolute right-0 mt-2 w-44 overflow-hidden rounded-xl border border-gray-100 bg-white py-1 shadow-lg">
          <Link
            to="/admin/users"
            onClick={() => setOpen(false)}
            className="block w-full px-4 py-2 text-left text-sm text-gray-700 transition hover:bg-gray-50"
          >
            회원 등급 관리
          </Link>
          <Link
            to="/admin/roles"
            onClick={() => setOpen(false)}
            className="block w-full px-4 py-2 text-left text-sm text-gray-700 transition hover:bg-gray-50"
          >
            역할 관리
          </Link>
        </div>
      )}
    </div>
  )
}
