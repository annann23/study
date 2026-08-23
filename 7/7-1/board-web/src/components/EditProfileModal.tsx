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
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-ink-900/30 backdrop-blur-sm">
      <div className="w-full max-w-sm rounded-2xl bg-white p-6 shadow-xl shadow-brand-900/10">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-ink-900">정보 수정</h2>
          <button
            onClick={onClose}
            className="text-ink-400 transition hover:text-brand-700"
            aria-label="닫기"
          >
            ✕
          </button>
        </div>

        {done ? (
          <p className="mt-6 text-sm text-ink-500">저장되었습니다.</p>
        ) : (
          <form onSubmit={handleSubmit} className="mt-5 flex flex-col gap-3">
            <label className="flex flex-col gap-1 text-sm text-ink-500">
              닉네임
              <input
                className="field"
                placeholder="변경할 닉네임"
                value={nickname}
                onChange={(e) => setNickname(e.target.value)}
              />
            </label>
            <label className="flex flex-col gap-1 text-sm text-ink-500">
              비밀번호
              <input
                className="field"
                type="password"
                placeholder="변경할 비밀번호"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </label>
            {error && <p className="text-sm text-red-500">{error}</p>}
            <button type="submit" disabled={submitting} className="btn-primary mt-2 w-full">
              저장
            </button>
          </form>
        )}
      </div>
    </div>,
    document.body,
  )
}
