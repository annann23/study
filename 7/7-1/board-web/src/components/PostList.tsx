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
}: {
  posts: Post[]
  loading: boolean
  getDetailHref: (post: Post) => string
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
              {thumbnail && (
                <img
                  src={thumbnail}
                  alt=""
                  className="h-16 w-16 shrink-0 rounded-lg object-cover"
                />
              )}
              <div className="min-w-0 flex-1">
                <div className="flex items-center justify-between gap-2">
                  <h3 className="truncate font-medium text-gray-900">{post.title}</h3>
                  <span className="shrink-0 text-xs text-gray-400">{formatDate(post.createdAt)}</span>
                </div>
                <p className="mt-1 line-clamp-2 text-sm text-gray-500">{stripHtml(post.content)}</p>
              </div>
            </Link>
          </li>
        )
      })}
    </ul>
  )
}
