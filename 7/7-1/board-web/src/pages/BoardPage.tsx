import { useEffect, useState, type FormEvent } from 'react'
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
  const [searchKeyword, setSearchKeyword] = useState('')
  const [searchType, setSearchType] = useState<'TITLE' | 'CONTENT'>('TITLE')
  const [view, setView] = useState<'card' | 'table'>(
    () => (localStorage.getItem('postListView') as 'card' | 'table') ?? 'card',
  )

  const changeView = (next: 'card' | 'table') => {
    setView(next)
    localStorage.setItem('postListView', next)
  }

  const selectedBoard = boards.find((b) => b.id === selectedBoardId)

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
    setSearchKeyword('') // 게시판 바뀌면 검색어 초기화
    setLoadingPosts(true)
    api<Post[]>(`/posts/board/${selectedBoardId}`)
      .then(setPosts)
      .finally(() => setLoadingPosts(false))
  }, [selectedBoardId])

  const handleSearch = async (e: FormEvent) => {
    e.preventDefault()
    if (selectedBoardId == null) return
    const keyword = searchKeyword.trim()
    setLoadingPosts(true)
    try {
      // 검색어 없으면 전체 목록으로 복귀
      const path = keyword
        ? `/posts/search?boardId=${selectedBoardId}&keyword=${encodeURIComponent(keyword)}&type=${searchType}`
        : `/posts/board/${selectedBoardId}`
      setPosts(await api<Post[]>(path))
    } finally {
      setLoadingPosts(false)
    }
  }

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
        <div className="mb-4 flex items-center justify-between gap-2">
          {selectedBoardId != null && !selectedBoard?.isPrivate ? (
            <form onSubmit={handleSearch} className="flex gap-2">
              <select
                value={searchType}
                onChange={(e) => setSearchType(e.target.value as 'TITLE' | 'CONTENT')}
                className="rounded-lg border border-gray-200 px-2 py-2 text-sm outline-none transition focus:border-gray-400"
              >
                <option value="TITLE">제목</option>
                <option value="CONTENT">본문</option>
              </select>
              <input
                value={searchKeyword}
                onChange={(e) => setSearchKeyword(e.target.value)}
                placeholder="검색어를 입력하세요"
                className="w-48 rounded-lg border border-gray-200 px-3 py-2 text-sm outline-none transition focus:border-gray-400"
              />
              <button
                type="submit"
                className="rounded-lg border border-gray-200 px-4 py-2 text-sm font-medium text-gray-700 transition hover:bg-gray-100"
              >
                검색
              </button>
            </form>
          ) : (
            <span />
          )}
          <div className="flex shrink-0 items-center gap-2">
            <div className="flex rounded-lg border border-gray-200 p-0.5">
              <button
                type="button"
                onClick={() => changeView('card')}
                title="카드형"
                className={`rounded px-2 py-1 text-sm ${view === 'card' ? 'bg-gray-900 text-white' : 'text-gray-500 hover:bg-gray-100'}`}
              >
                ▦
              </button>
              <button
                type="button"
                onClick={() => changeView('table')}
                title="테이블형"
                className={`rounded px-2 py-1 text-sm ${view === 'table' ? 'bg-gray-900 text-white' : 'text-gray-500 hover:bg-gray-100'}`}
              >
                ☰
              </button>
            </div>
            {selectedBoardId != null && (
              <Link
                to={`/board/${selectedBoardId}/write`}
                className="rounded-lg bg-gray-900 px-4 py-2 text-sm font-medium text-white transition hover:bg-gray-800"
              >
                글쓰기
              </Link>
            )}
          </div>
        </div>
        <PostList
          posts={posts}
          loading={loadingPosts}
          getDetailHref={(post) => `/board/${post.boardId}/posts/${post.id}`}
          isPrivate={boards.find((b) => b.id === selectedBoardId)?.isPrivate ?? false}
          view={view}
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
