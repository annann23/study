import { Link } from 'react-router-dom'
import type { Post } from '../lib/types'
import { stripHtml, firstImageSrc } from '../lib/html'

function formatDate(iso: string) {
  const date = new Date(iso)
  return date.toLocaleDateString('ko-KR', { month: 'long', day: 'numeric' })
}

export default function PostList({
  posts,
  loading,
  getDetailHref,
  isPrivate = false,
}: {
  posts: Post[]
  loading: boolean
  getDetailHref: (post: Post) => string
  isPrivate?: boolean
}) {
  if (loading) {
    return <p className="mt-16 text-center text-sm text-gray-400">불러오는 중...</p>
  }

  if (posts.length === 0) {
    return <p className="mt-16 text-center text-sm text-gray-400">아직 게시글이 없습니다.</p>
  }

  return (
    <ul className="flex flex-col gap-3">
      {posts.map((post) => {
        const thumbnail = firstImageSrc(post.content)
        return (
          <li key={post.id}>
            <Link
              to={getDetailHref(post)}
              className="flex gap-4 rounded-xl border border-gray-100 bg-white p-5 shadow-sm transition hover:-translate-y-0.5 hover:shadow-md"
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
                  <h3 className="flex min-w-0 items-center gap-1 font-medium text-gray-900">
                    {isPrivate && (
                      <span title="비공개 - 작성자와 관리자만 열람 가능">🔒</span>
                    )}
                    <span className="truncate">{post.title}</span>
                  </h3>
                  <span className="shrink-0 text-xs text-gray-400">{formatDate(post.createdAt)}</span>
                </div>
                <div className="mt-1 flex items-center gap-1.5 text-xs text-gray-400">
                  <span className="font-medium text-gray-500">{post.nickName}</span>
                  <span className="rounded bg-gray-100 px-1.5 py-0.5 text-[11px] text-gray-500">{post.userLevel}</span>
                </div>
                {!isPrivate && (
                  <p className="mt-1 line-clamp-2 text-sm text-gray-500">{stripHtml(post.content)}</p>
                )}
              </div>
            </Link>
          </li>
        )
      })}
    </ul>
  )
}
