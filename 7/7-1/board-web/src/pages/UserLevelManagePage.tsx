import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../lib/api'
import type { User } from '../lib/auth'
import type { UserLevel } from '../lib/types'

export default function UserLevelManagePage() {
  const [users, setUsers] = useState<User[]>([])
  const [levels, setLevels] = useState<UserLevel[]>([])
  const [loading, setLoading] = useState(true)
  const [savingUserId, setSavingUserId] = useState<number | null>(null)

  useEffect(() => {
    Promise.all([api<User[]>('/user'), api<UserLevel[]>('/user-level')])
      .then(([userData, levelData]) => {
        setUsers(userData)
        setLevels(levelData)
      })
      .finally(() => setLoading(false))
  }, [])

  const changeLevel = async (userId: number, userLevelId: number) => {
    setSavingUserId(userId)
    try {
      const updated = await api<User>('/user/user-level', {
        method: 'PUT',
        body: JSON.stringify({ userId, userLevelId }),
      })
      setUsers((prev) => prev.map((u) => (u.id === userId ? updated : u)))
    } finally {
      setSavingUserId(null)
    }
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="border-b border-gray-100 bg-white/80 px-6 py-4 backdrop-blur-md">
        <div className="mx-auto max-w-3xl">
          <Link to="/" className="text-sm text-gray-400 hover:text-gray-600">
            ← 목록으로
          </Link>
        </div>
      </header>

      <main className="mx-auto max-w-3xl px-6 py-8">
        <h1 className="text-xl font-semibold text-gray-900">회원 등급 관리</h1>

        <div className="mt-5 overflow-hidden rounded-2xl border border-gray-100 bg-white shadow-sm">
          {loading ? (
            <p className="p-6 text-sm text-gray-400">불러오는 중...</p>
          ) : (
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-gray-100 text-left text-xs text-gray-400">
                  <th className="px-6 py-3 font-medium">닉네임</th>
                  <th className="px-6 py-3 font-medium">아이디</th>
                  <th className="px-6 py-3 font-medium">등급</th>
                </tr>
              </thead>
              <tbody>
                {users.map((u) => (
                  <tr key={u.id} className="border-b border-gray-50 last:border-0">
                    <td className="px-6 py-3 text-gray-800">{u.nickname}</td>
                    <td className="px-6 py-3 text-gray-500">{u.loginId}</td>
                    <td className="px-6 py-3">
                      <select
                        value={u.userLevel}
                        disabled={savingUserId === u.id}
                        onChange={(e) => changeLevel(u.id, Number(e.target.value))}
                        className="rounded-lg border border-gray-200 px-3 py-1.5 text-sm outline-none transition focus:border-gray-400 disabled:opacity-50"
                      >
                        {levels.map((level) => (
                          <option key={level.id} value={level.id}>
                            {level.name}
                          </option>
                        ))}
                      </select>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </main>
    </div>
  )
}
