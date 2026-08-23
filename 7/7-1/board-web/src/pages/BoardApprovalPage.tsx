import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../lib/api'
import type { Board } from '../lib/types'

export default function BoardApprovalPage() {
  const [boards, setBoards] = useState<Board[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api<Board[]>('/board/pending')
      .then(setBoards)
      .finally(() => setLoading(false))
  }, [])

  const handleApprove = async (board: Board) => {
    const updated = await api<Board>(`/board/${board.id}/approve`, { method: 'PUT' })
    setBoards((prev) => prev.filter((b) => b.id !== updated.id))
  }

  const handleReject = async (board: Board) => {
    if (!window.confirm(`"${board.name}" 요청을 반려할까요?`)) return
    const updated = await api<Board>(`/board/${board.id}/reject`, { method: 'PUT' })
    setBoards((prev) => prev.filter((b) => b.id !== updated.id))
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
        <h1 className="text-xl font-semibold text-ink-900">게시판 생성 요청</h1>

        <div className="mt-5 overflow-hidden rounded-2xl border border-ink-100 bg-white shadow-sm shadow-ink-900/[0.03]">
          {loading ? (
            <p className="p-6 text-sm text-ink-400">불러오는 중...</p>
          ) : boards.length === 0 ? (
            <p className="p-6 text-sm text-ink-400">대기 중인 요청이 없습니다.</p>
          ) : (
            <ul className="divide-y divide-ink-50">
              {boards.map((board) => (
                <li key={board.id} className="flex items-center justify-between gap-4 px-6 py-4">
                  <div>
                    <p className="text-sm font-medium text-ink-900">{board.name}</p>
                    <p className="mt-1 text-xs text-ink-400">요청자 ID: {board.requestedById}</p>
                    {board.reason && (
                      <p className="mt-1 max-w-md text-xs text-ink-500">{board.reason}</p>
                    )}
                  </div>
                  <div className="flex shrink-0 gap-2">
                    <button onClick={() => handleApprove(board)} className="btn-primary">
                      승인
                    </button>
                    <button
                      onClick={() => handleReject(board)}
                      className="rounded-lg px-3 py-1.5 text-xs text-ink-500 transition hover:bg-red-50 hover:text-red-500"
                    >
                      반려
                    </button>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>
      </main>
    </div>
  )
}
