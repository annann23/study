import { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { api } from '../lib/api'
import type { Board, Post } from '../lib/types'
import TopNav from '../components/TopNav'
import PostList from '../components/PostList'
import BoardFormModal from '../components/BoardFormModal'

export default function BoardPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const [boards, setBoards] = useState<Board[]>([])
  const [selectedBoardId, setSelectedBoardId] = useState<number | null>(
    searchParams.get('board') ? Number(searchParams.get('board')) : null,
  )
  const [posts, setPosts] = useState<Post[]>([])
  const [loadingPosts, setLoadingPosts] = useState(false)
  const [boardFormState, setBoardFormState] = useState<'closed' | 'add' | Board>('closed')

  useEffect(() => {
    api<Board[]>('/board').then((data) => {
      setBoards(data)
      setSelectedBoardId((current) => {
        if (current != null && data.some((b) => b.id === current)) return current
        return data[0]?.id ?? null
      })
    })
  }, [])

  useEffect(() => {
    if (selectedBoardId == null) return
    setLoadingPosts(true)
    api<Post[]>(`/posts/board/${selectedBoardId}`)
      .then(setPosts)
      .finally(() => setLoadingPosts(false))
  }, [selectedBoardId])

  const selectBoard = (id: number) => {
    setSelectedBoardId(id)
    setSearchParams({ board: String(id) })
  }

  const handleBoardSaved = (board: Board) => {
    setBoards((prev) => {
      const exists = prev.some((b) => b.id === board.id)
      return exists ? prev.map((b) => (b.id === board.id ? board : b)) : [...prev, board]
    })
    if (selectedBoardId == null) selectBoard(board.id)
  }

  const handleBoardDelete = async (board: Board) => {
    if (!window.confirm(`"${board.name}" 게시판을 삭제할까요?`)) return
    await api(`/board/${board.id}`, { method: 'DELETE' })
    setBoards((prev) => {
      const next = prev.filter((b) => b.id !== board.id)
      if (selectedBoardId === board.id) setSelectedBoardId(next[0]?.id ?? null)
      return next
    })
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <TopNav
        boards={boards}
        selectedBoardId={selectedBoardId}
        onSelectBoard={selectBoard}
        onAddBoard={() => setBoardFormState('add')}
        onEditBoard={(board) => setBoardFormState(board)}
        onDeleteBoard={handleBoardDelete}
      />
      <main className="mx-auto max-w-4xl px-6 py-8">
        <div className="mb-4 flex justify-end">
          {selectedBoardId != null && (
            <Link
              to={`/board/${selectedBoardId}/write`}
              className="rounded-lg bg-gray-900 px-4 py-2 text-sm font-medium text-white transition hover:bg-gray-800"
            >
              글쓰기
            </Link>
          )}
        </div>
        <PostList
          posts={posts}
          loading={loadingPosts}
          getDetailHref={(post) => `/board/${post.boardId}/posts/${post.id}`}
          isPrivate={boards.find((b) => b.id === selectedBoardId)?.isPrivate ?? false}
        />
      </main>

      {boardFormState !== 'closed' && (
        <BoardFormModal
          board={boardFormState === 'add' ? undefined : boardFormState}
          onClose={() => setBoardFormState('closed')}
          onSaved={handleBoardSaved}
        />
      )}
    </div>
  )
}
