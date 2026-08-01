import { useEffect, useState, type FormEvent } from 'react'
import { createPortal } from 'react-dom'
import { api } from '../lib/api'
import type { Board, BoardType } from '../lib/types'

type Props = {
  board?: Board
  onClose: () => void
  onSaved: (board: Board) => void
}

export default function BoardFormModal({ board, onClose, onSaved }: Props) {
  const isEdit = board != null
  const [name, setName] = useState(board?.name ?? '')
  const [boardTypes, setBoardTypes] = useState<BoardType[]>([])
  const [boardTypeId, setBoardTypeId] = useState<number | null>(board?.boardTypeId ?? null)
  const [isPrivate, setIsPrivate] = useState(false)
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    if (isEdit) return
    api<BoardType[]>('/board-types').then((types) => {
      setBoardTypes(types)
      setBoardTypeId((prev) => prev ?? types[0]?.id ?? null)
    })
  }, [isEdit])

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError('')
    setSubmitting(true)
    try {
      const saved = isEdit
        ? await api<Board>('/board/name', {
            method: 'PUT',
            body: JSON.stringify({ boardId: board!.id, name }),
          })
        : await api<Board>('/board', {
            method: 'POST',
            body: JSON.stringify({ name, boardTypeId, isPrivate }),
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
          <h2 className="text-lg font-semibold text-gray-900">
            {isEdit ? '게시판 수정' : '게시판 추가'}
          </h2>
          <button
            onClick={onClose}
            className="text-gray-400 transition hover:text-gray-600"
            aria-label="닫기"
          >
            ✕
          </button>
        </div>

        <form onSubmit={handleSubmit} className="mt-5 flex flex-col gap-3">
          <label className="flex flex-col gap-1 text-sm text-gray-600">
            이름
            <input
              className="rounded-lg border border-gray-200 px-3 py-2 text-sm outline-none transition focus:border-gray-400"
              placeholder="게시판 이름"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
              autoFocus
            />
          </label>

          {!isEdit && (
            <label className="flex flex-col gap-1 text-sm text-gray-600">
              카테고리
              <select
                className="rounded-lg border border-gray-200 px-3 py-2 text-sm outline-none transition focus:border-gray-400"
                value={boardTypeId ?? ''}
                onChange={(e) => setBoardTypeId(Number(e.target.value))}
              >
                {boardTypes.map((type) => (
                  <option key={type.id} value={type.id}>
                    {type.name}
                  </option>
                ))}
              </select>
            </label>
          )}

          {!isEdit && (
            <label className="flex items-center gap-2 text-sm text-gray-600">
              <input
                type="checkbox"
                checked={isPrivate}
                onChange={(e) => setIsPrivate(e.target.checked)}
              />
              비공개 게시판 (작성자와 관리자만 열람 가능)
            </label>
          )}

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
