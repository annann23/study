import { Link } from 'react-router-dom'
import type { Post } from '../lib/types'
import { stripHtml, firstImageSrc } from '../lib/html'

function formatDate(iso: string) {
  const date = new Date(iso)
  return date.toLocaleString('ko-KR', {
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  })
}

function LevelBadge({ level }: { level: string }) {
  const upper = level.toUpperCase()
  if (upper.includes('VVIP')) {
    return (
      <span className="level-badge-shimmer inline-flex items-center gap-0.5 rounded bg-[linear-gradient(90deg,#fde68a,#f59e0b,#fde68a)] px-1.5 py-0.5 text-[11px] font-semibold text-amber-900 shadow-sm">
        👑 {level}
      </span>
    )
  }
  if (upper.includes('VIP')) {
    return (
      <span className="level-badge-shimmer inline-flex items-center gap-0.5 rounded bg-[linear-gradient(90deg,#e2e8f0,#94a3b8,#e2e8f0)] px-1.5 py-0.5 text-[11px] font-semibold text-slate-700 shadow-sm">
        ⭐ {level}
      </span>
    )
  }
  return <span className="rounded bg-brand-50 px-1.5 py-0.5 text-[11px] text-brand-700">{level}</span>
}

function OperatorBadge() {
  return (
    <span className="inline-flex items-center gap-0.5 rounded bg-linear-to-br from-brand-600 to-brand-400 px-1.5 py-0.5 text-[11px] font-semibold text-white">
      🛠 운영자
    </span>
  )
}

export default function PostList({
  posts,
  loading,
  getDetailHref,
  isPrivate = false,
  view = 'card',
  operatorId = null,
}: {
  posts: Post[]
  loading: boolean
  getDetailHref: (post: Post) => string
  isPrivate?: boolean
  view?: 'card' | 'table'
  operatorId?: number | null
}) {
  if (loading) {
    return <p className="mt-16 text-center text-sm text-ink-400">불러오는 중...</p>
  }

  if (posts.length === 0) {
    return <p className="mt-16 text-center text-sm text-ink-400">아직 게시글이 없습니다.</p>
  }

  if (view === 'table') {
    return (
      <table className="w-full border-t border-brand-200 text-sm">
        <thead>
          <tr className="border-b border-ink-200 bg-brand-50/60 text-ink-500">
            <th className="w-16 px-3 py-2 text-center font-medium">번호</th>
            <th className="px-3 py-2 text-left font-medium">제목</th>
            <th className="w-36 px-3 py-2 text-left font-medium">글쓴이</th>
            <th className="w-40 px-3 py-2 text-center font-medium">작성일</th>
          </tr>
        </thead>
        <tbody>
          {posts.map((post) => (
            <tr key={post.id} className="border-b border-ink-100 transition hover:bg-brand-50/40">
              <td className="px-3 py-2 text-center text-ink-400">{post.id}</td>
              <td className="px-3 py-2">
                <Link to={getDetailHref(post)} className="flex min-w-0 items-center gap-1 text-ink-900 hover:text-brand-700 hover:underline">
                  {isPrivate && <span title="비공개 - 작성자와 관리자만 열람 가능">🔒</span>}
                  <span className="truncate">{post.title}</span>
                </Link>
              </td>
              <td className="px-3 py-2 text-ink-500">
                <div className="flex items-center gap-1">
                  <span className="min-w-0 truncate font-medium">{post.nickName}</span>
                  <span className="shrink-0">
                    {post.userId === operatorId ? <OperatorBadge /> : <LevelBadge level={post.userLevel} />}
                  </span>
                </div>
              </td>
              <td className="px-3 py-2 text-center text-xs text-ink-400">{formatDate(post.createdAt)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    )
  }

  return (
    <ul className="flex flex-col gap-3">
      {posts.map((post) => {
        const thumbnail = firstImageSrc(post.content)
        return (
          <li key={post.id}>
            <Link
              to={getDetailHref(post)}
              className="group flex gap-4 rounded-xl border border-ink-100 bg-white p-5 shadow-sm shadow-ink-900/[0.03] transition hover:-translate-y-0.5 hover:border-brand-200 hover:shadow-md hover:shadow-brand-900/10"
            >
              {!isPrivate && thumbnail && (
                <img
                  src={thumbnail}
                  alt=""
                  className="h-16 w-16 shrink-0 rounded-lg object-cover"
                />
              )}
              <div className="min-w-0 flex-1">
                <div className="flex items-center justify-between gap-2">
                  <h3 className="flex min-w-0 items-center gap-1 font-medium text-ink-900 transition group-hover:text-brand-700">
                    {isPrivate && (
                      <span title="비공개 - 작성자와 관리자만 열람 가능">🔒</span>
                    )}
                    <span className="truncate">{post.title}</span>
                  </h3>
                  <span className="shrink-0 text-xs text-ink-400">{formatDate(post.createdAt)}</span>
                </div>
                <div className="mt-1 flex items-center gap-1.5 text-xs text-ink-400">
                  <span className="font-medium text-ink-500">{post.nickName}</span>
                  {post.userId === operatorId ? <OperatorBadge /> : <LevelBadge level={post.userLevel} />}
                </div>
                {!isPrivate && (
                  <p className="mt-1 line-clamp-2 break-words text-sm text-ink-500">{stripHtml(post.content)}</p>
                )}
              </div>
            </Link>
          </li>
        )
      })}
    </ul>
  )
}
