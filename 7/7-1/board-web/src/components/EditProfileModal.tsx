import { useState, type FormEvent } from 'react'
import { createPortal } from 'react-dom'
import { api } from '../lib/api'
import { useAuth, type User } from '../lib/auth'

export default function EditProfileModal({ onClose }: { onClose: () => void }) {
  const { setUser } = useAuth()
  const [nickname, setNickname] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [done, setDone] = useState(false)
  const [submitting, setSubmitting] = useState(false)

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError('')
    setSubmitting(true)
    try {
      if (nickname.trim()) {
        const updated = await api<User>('/user/user', {
          method: 'PUT',
          body: JSON.stringify({ nickname }),
        })
        setUser(updated)
      }
      if (password.trim()) {
        await api('/user/password', { method: 'PUT', body: JSON.stringify({ password }) })
      }
      setDone(true)
    } catch {
      setError('수정에 실패했습니다.')
    } finally {
      setSubmitting(false)
    }
  }

  return createPortal(
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30 backdrop-blur-sm">
      <div className="w-full max-w-sm rounded-2xl bg-white p-6 shadow-xl">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-gray-900">정보 수정</h2>
          <button
            onClick={onClose}
            className="text-gray-400 transition hover:text-gray-600"
            aria-label="닫기"
          >
            ✕
          </button>
        </div>

        {done ? (
          <p className="mt-6 text-sm text-gray-600">저장되었습니다.</p>
        ) : (
          <form onSubmit={handleSubmit} className="mt-5 flex flex-col gap-3">
            <label className="flex flex-col gap-1 text-sm text-gray-600">
              닉네임
              <input
                className="rounded-lg border border-gray-200 px-3 py-2 text-sm outline-none transition focus:border-gray-400"
                placeholder="변경할 닉네임"
                value={nickname}
                onChange={(e) => setNickname(e.target.value)}
              />
            </label>
            <label className="flex flex-col gap-1 text-sm text-gray-600">
              비밀번호
              <input
                className="rounded-lg border border-gray-200 px-3 py-2 text-sm outline-none transition focus:border-gray-400"
                type="password"
                placeholder="변경할 비밀번호"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </label>
            {error && <p className="text-sm text-red-500">{error}</p>}
            <button
              type="submit"
              disabled={submitting}
              className="mt-2 rounded-lg bg-gray-900 py-2 text-sm font-medium text-white transition hover:bg-gray-800 disabled:opacity-50"
            >
              저장
            </button>
          </form>
        )}
      </div>
    </div>,
    document.body,
  )
}
