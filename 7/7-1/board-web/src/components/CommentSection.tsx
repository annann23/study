import { useEffect, useState, type FormEvent } from 'react'
import { api } from '../lib/api'
import { hasPermission, useAuth } from '../lib/auth'
import type { Comment } from '../lib/types'

function formatDateTime(iso: string) {
  return new Date(iso).toLocaleString('ko-KR', { month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' })
}

function CommentItem({ comment, onChanged }: { comment: Comment; onChanged: (comment: Comment | null) => void }) {
  const { user } = useAuth()
  const [editing, setEditing] = useState(false)
  const [content, setContent] = useState(comment.content)
  const isMine = user?.id === comment.userId
  const canEdit = hasPermission(user, 'COMMENT_UPDATE_ANY') || (isMine && hasPermission(user, 'COMMENT_UPDATE_OWN'))
  const canDelete = hasPermission(user, 'COMMENT_DELETE_ANY') || (isMine && hasPermission(user, 'COMMENT_DELETE_OWN'))

  const handleSave = async () => {
    const updated = await api<Comment>('/comments', {
      method: 'PUT',
      body: JSON.stringify({ commentId: comment.id, content }),
    })
    onChanged(updated)
    setEditing(false)
  }

  const handleDelete = async () => {
    if (!window.confirm('댓글을 삭제할까요?')) return
    await api('/comments', { method: 'DELETE', body: JSON.stringify({ commentId: comment.id }) })
    onChanged(null)
  }

  return (
    <li className="border-b border-gray-100 py-3 last:border-0">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <span className="text-sm font-medium text-gray-900">{comment.nickname}</span>
          <span className="text-xs text-gray-400">{formatDateTime(comment.createdAt)}</span>
          {comment.isEdited && <span className="text-xs text-gray-300">(수정됨)</span>}
        </div>
        {(canEdit || canDelete) && !editing && (
          <div className="flex gap-2">
            {canEdit && (
              <button
                onClick={() => setEditing(true)}
                className="text-xs text-gray-400 transition hover:text-gray-700"
              >
                수정
              </button>
            )}
            {canDelete && (
              <button onClick={handleDelete} className="text-xs text-gray-400 transition hover:text-red-500">
                삭제
              </button>
            )}
          </div>
        )}
      </div>

      {editing ? (
        <div className="mt-2 flex gap-2">
          <input
            className="flex-1 rounded-lg border border-gray-200 px-3 py-1.5 text-sm outline-none focus:border-gray-400"
            value={content}
            onChange={(e) => setContent(e.target.value)}
            autoFocus
          />
          <button
            onClick={handleSave}
            className="rounded-lg bg-gray-900 px-3 py-1.5 text-xs font-medium text-white hover:bg-gray-800"
          >
            저장
          </button>
          <button
            onClick={() => {
              setEditing(false)
              setContent(comment.content)
            }}
            className="rounded-lg px-3 py-1.5 text-xs text-gray-500 hover:bg-gray-100"
          >
            취소
          </button>
        </div>
      ) : (
        <p className="mt-1 text-sm text-gray-600">{comment.content}</p>
      )}
    </li>
  )
}

export default function CommentSection({ postId }: { postId: number }) {
  const { user } = useAuth()
  const [comments, setComments] = useState<Comment[] | null>(null)
  const [content, setContent] = useState('')
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    api<Comment[]>(`/comments/post?postId=${postId}`).then(setComments)
  }, [postId])

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    if (!content.trim()) return
    setSubmitting(true)
    try {
      const created = await api<Comment>('/comments', {
        method: 'POST',
        body: JSON.stringify({ content, postId, userId: user!.id, parentId: null }),
      })
      setComments((prev) => [...(prev ?? []), created])
      setContent('')
    } finally {
      setSubmitting(false)
    }
  }

  const handleChanged = (id: number, updated: Comment | null) => {
    setComments((prev) =>
      updated ? (prev ?? []).map((c) => (c.id === id ? updated : c)) : (prev ?? []).filter((c) => c.id !== id),
    )
  }

  return (
    <section className="mt-8">
      <h2 className="text-sm font-semibold text-gray-900">댓글 {comments?.length ?? 0}개</h2>

      <form onSubmit={handleSubmit} className="mt-3 flex gap-2">
        <input
          className="flex-1 rounded-lg border border-gray-200 px-3 py-2 text-sm outline-none transition focus:border-gray-400"
          placeholder="댓글을 입력하세요"
          value={content}
          onChange={(e) => setContent(e.target.value)}
        />
        <button
          type="submit"
          disabled={submitting || !content.trim()}
          className="rounded-lg bg-gray-900 px-4 py-2 text-sm font-medium text-white transition hover:bg-gray-800 disabled:opacity-50"
        >
          등록
        </button>
      </form>

      {comments == null ? (
        <p className="mt-4 text-sm text-gray-400">불러오는 중...</p>
      ) : comments.length === 0 ? (
        <p className="mt-4 text-sm text-gray-400">첫 댓글을 남겨보세요.</p>
      ) : (
        <ul className="mt-2">
          {comments.map((comment) => (
            <CommentItem
              key={comment.id}
              comment={comment}
              onChanged={(updated) => handleChanged(comment.id, updated)}
            />
          ))}
        </ul>
      )}
    </section>
  )
}
