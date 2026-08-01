import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { api, ApiError } from '../lib/api'
import { hasPermission, useAuth } from '../lib/auth'
import type { Board, LikeStatus, Post } from '../lib/types'
import { sanitizeHtml } from '../lib/html'
import CommentSection from '../components/CommentSection'

function formatDateTime(iso: string) {
  return new Date(iso).toLocaleString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' })
}

export default function PostDetailPage() {
  const { postId } = useParams<{ postId: string }>()
  const navigate = useNavigate()
  const { user } = useAuth()

  const [post, setPost] = useState<Post | null>(null)
  const [board, setBoard] = useState<Board | null>(null)
  const [like, setLike] = useState<LikeStatus | null>(null)
  const [error, setError] = useState('')

  useEffect(() => {
    api<Post>(`/posts/${postId}`)
      .then((data) => {
        setPost(data)
        api<Board>(`/board/${data.boardId}`).then(setBoard)
        api<LikeStatus>(`/posts/${data.id}/like`).then(setLike)
      })
      .catch((e: unknown) => {
        const status = e instanceof ApiError ? e.status : 0
        if (status === 403) {
          alert('본인이 작성한 글만 볼 수 있습니다.')
          navigate(-1)
          return
        }
        setError('글을 불러올 수 없습니다.')
      })
  }, [postId, user, navigate])

  if (error) {
    return <p className="mt-16 text-center text-sm text-gray-400">{error}</p>
  }

  const toggleLike = async () => {
    if (!post) return
    await api<boolean>(`/posts/${post.id}/like`, { method: 'POST' })
    setLike((prev) => (prev ? { count: prev.count + (prev.likedByMe ? -1 : 1), likedByMe: !prev.likedByMe } : prev))
  }

  const handleDelete = async () => {
    if (!post || !window.confirm('게시글을 삭제할까요?')) return
    await api('/posts', { method: 'DELETE', body: JSON.stringify({ postId: post.id }) })
    navigate(`/?board=${post.boardId}`)
  }

  if (!post) {
    return <p className="mt-16 text-center text-sm text-gray-400">불러오는 중...</p>
  }

  const isMine = user?.id === post.userId
  const canEdit = hasPermission(user, 'POST_UPDATE_ANY') || (isMine && hasPermission(user, 'POST_UPDATE_OWN'))
  const canDelete = hasPermission(user, 'POST_DELETE_ANY') || (isMine && hasPermission(user, 'POST_DELETE_OWN'))

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="border-b border-gray-100 bg-white/80 px-6 py-4 backdrop-blur-md">
        <div className="mx-auto max-w-3xl">
          <Link to={`/?board=${post.boardId}`} className="text-sm text-gray-400 hover:text-gray-600">
            ← {board?.name ?? '목록'}으로
          </Link>
        </div>
      </header>

      <main className="mx-auto max-w-3xl px-6 py-8">
        <div className="rounded-2xl border border-gray-100 bg-white p-6 shadow-sm">
          <div className="flex items-start justify-between gap-4">
            <h1 className="text-xl font-semibold text-gray-900">{post.title}</h1>
            {(canEdit || canDelete) && (
              <div className="flex shrink-0 gap-2">
                {canEdit && (
                  <Link
                    to={`/board/${post.boardId}/posts/${post.id}/edit`}
                    className="rounded-lg px-3 py-1.5 text-xs text-gray-500 transition hover:bg-gray-100"
                  >
                    수정
                  </Link>
                )}
                {canDelete && (
                  <button
                    onClick={handleDelete}
                    className="rounded-lg px-3 py-1.5 text-xs text-gray-500 transition hover:bg-gray-100 hover:text-red-500"
                  >
                    삭제
                  </button>
                )}
              </div>
            )}
          </div>
          <div className="mt-1 flex items-center gap-2 text-xs text-gray-400">
            <span className="font-medium text-gray-500">{post.nickName}</span>
            <span className="rounded bg-gray-100 px-1.5 py-0.5 text-[11px] text-gray-500">{post.userLevel}</span>
            <span>·</span>
            <span>{formatDateTime(post.createdAt)}</span>
          </div>

          <div
            className="post-content mt-5 text-sm leading-relaxed text-gray-800"
            dangerouslySetInnerHTML={{ __html: sanitizeHtml(post.content) }}
          />

          <button
            onClick={toggleLike}
            disabled={like == null}
            className={`mt-6 flex items-center gap-1.5 rounded-full border px-4 py-1.5 text-sm font-medium transition ${
              like?.likedByMe
                ? 'border-red-200 bg-red-50 text-red-500'
                : 'border-gray-200 text-gray-500 hover:bg-gray-50'
            }`}
          >
            {like?.likedByMe ? '♥' : '♡'} 좋아요 {like?.count ?? 0}
          </button>
        </div>

        <CommentSection postId={post.id} />
      </main>
    </div>
  )
}
