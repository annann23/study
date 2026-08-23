import { useEffect, useState, type FormEvent } from 'react'
import { createPortal } from 'react-dom'
import { api } from '../lib/api'
import type { Board, BoardType } from '../lib/types'

type Props = {
  board?: Board
  mode?: 'create' | 'request'
  onClose: () => void
  onSaved: (board: Board) => void
}

export default function BoardFormModal({ board, mode = 'create', onClose, onSaved }: Props) {
  const isEdit = board != null
  const isRequest = !isEdit && mode === 'request'
  const [name, setName] = useState(board?.name ?? '')
  const [boardTypes, setBoardTypes] = useState<BoardType[]>([])
  const [boardTypeId, setBoardTypeId] = useState<number | null>(board?.boardTypeId ?? null)
  const [isPrivate, setIsPrivate] = useState(false)
  const [reason, setReason] = useState('')
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
        : isRequest
          ? await api<Board>('/board/request', {
              method: 'POST',
              body: JSON.stringify({ name, boardTypeId, reason }),
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
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-ink-900/30 backdrop-blur-sm">
      <div className="w-full max-w-sm rounded-2xl bg-white p-6 shadow-xl shadow-brand-900/10">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-ink-900">
            {isEdit ? '게시판 수정' : isRequest ? '게시판 생성 요청' : '게시판 추가'}
          </h2>
          <button
            onClick={onClose}
            className="text-ink-400 transition hover:text-brand-700"
            aria-label="닫기"
          >
            ✕
          </button>
        </div>

        <form onSubmit={handleSubmit} className="mt-5 flex flex-col gap-3">
          <label className="flex flex-col gap-1 text-sm text-ink-500">
            이름
            <input
              className="field"
              placeholder="게시판 이름"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
              autoFocus
            />
          </label>

          {!isEdit && (
            <label className="flex flex-col gap-1 text-sm text-ink-500">
              카테고리
              <select
                className="field"
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

          {!isEdit && !isRequest && (
            <label className="flex items-center gap-2 text-sm text-ink-500">
              <input
                type="checkbox"
                checked={isPrivate}
                onChange={(e) => setIsPrivate(e.target.checked)}
                className="accent-brand-600"
              />
              비공개 게시판 (작성자와 관리자만 열람 가능)
            </label>
          )}

          {isRequest && (
            <>
              <label className="flex flex-col gap-1 text-sm text-ink-500">
                요청 사유
                <textarea
                  className="field"
                  placeholder="게시판이 필요한 이유를 적어주세요"
                  value={reason}
                  onChange={(e) => setReason(e.target.value)}
                  rows={3}
                />
              </label>
              <p className="text-xs text-ink-400">
                요청한 게시판은 관리자 승인 후 생성되며, 요청자가 운영자로 지정됩니다.
              </p>
            </>
          )}

          {error && <p className="text-sm text-red-500">{error}</p>}
          <button
            type="submit"
            disabled={submitting || !name.trim()}
            className="btn-primary mt-2 w-full"
          >
            저장
          </button>
        </form>
      </div>
    </div>,
    document.body,
  )
}
