import { useEffect, useState, type FormEvent } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { api } from '../lib/api'
import type { Board, PageResponse, Post } from '../lib/types'
import TopNav from '../components/TopNav'
import PostList from '../components/PostList'
import BoardFormModal from '../components/BoardFormModal'

const PAGE_SIZE_OPTIONS = [10, 20, 50, 100]

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
  const [page, setPage] = useState(0)
  const [pageSize, setPageSize] = useState(20)
  const [pageJumpInput, setPageJumpInput] = useState('1')
  const [totalElements, setTotalElements] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [isSearchMode, setIsSearchMode] = useState(false)

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
    setIsSearchMode(false)
    setPage(0)
  }, [selectedBoardId])

  useEffect(() => {
    if (selectedBoardId == null || isSearchMode) return
    setLoadingPosts(true)
    api<PageResponse<Post>>(`/posts/board/${selectedBoardId}?page=${page}&size=${pageSize}`)
      .then((data) => {
        setPosts(data.content)
        setTotalElements(data.totalElements)
        setTotalPages(data.totalPages)
        setPageJumpInput(String(data.number + 1))
      })
      .finally(() => setLoadingPosts(false))
  }, [selectedBoardId, page, pageSize, isSearchMode])

  const handleSearch = async (e: FormEvent) => {
    e.preventDefault()
    if (selectedBoardId == null) return
    const keyword = searchKeyword.trim()
    setLoadingPosts(true)
    try {
      if (!keyword) {
        // 검색어 없으면 전체 목록(페이지네이션)으로 복귀
        setIsSearchMode(false)
        return
      }
      // 검색 결과는 페이지네이션이 없는 별도 엔드포인트
      setIsSearchMode(true)
      const results = await api<Post[]>(
        `/posts/search?boardId=${selectedBoardId}&keyword=${encodeURIComponent(keyword)}&type=${searchType}`,
      )
      setPosts(results)
      setTotalElements(results.length)
      setTotalPages(1)
    } finally {
      setLoadingPosts(false)
    }
  }

  const goToPage = (nextPage: number) => {
    const clamped = Math.max(0, Math.min(nextPage, Math.max(totalPages - 1, 0)))
    setPage(clamped)
  }

  const handlePageJump = (e: FormEvent) => {
    e.preventDefault()
    const parsed = Number(pageJumpInput)
    if (Number.isFinite(parsed)) goToPage(parsed - 1)
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
        {!isSearchMode && selectedBoardId != null && (
          <div className="mt-4 flex flex-wrap items-center justify-between gap-3 text-sm text-gray-600">
            <span>
              총 {totalElements.toLocaleString()}건 · {totalPages.toLocaleString()}페이지
            </span>
            <div className="flex items-center gap-2">
              <select
                value={pageSize}
                onChange={(e) => {
                  setPageSize(Number(e.target.value))
                  setPage(0)
                }}
                className="rounded-lg border border-gray-200 px-2 py-1.5 outline-none transition focus:border-gray-400"
              >
                {PAGE_SIZE_OPTIONS.map((size) => (
                  <option key={size} value={size}>
                    {size}개씩
                  </option>
                ))}
              </select>
              <button
                type="button"
                onClick={() => goToPage(page - 1)}
                disabled={page <= 0}
                className="rounded-lg border border-gray-200 px-3 py-1.5 font-medium text-gray-700 transition hover:bg-gray-100 disabled:cursor-not-allowed disabled:opacity-40"
              >
                이전
              </button>
              <form onSubmit={handlePageJump} className="flex items-center gap-1">
                <input
                  value={pageJumpInput}
                  onChange={(e) => setPageJumpInput(e.target.value)}
                  className="w-16 rounded-lg border border-gray-200 px-2 py-1.5 text-center outline-none transition focus:border-gray-400"
                />
                <span>/ {Math.max(totalPages, 1).toLocaleString()}</span>
              </form>
              <button
                type="button"
                onClick={() => goToPage(page + 1)}
                disabled={page >= totalPages - 1}
                className="rounded-lg border border-gray-200 px-3 py-1.5 font-medium text-gray-700 transition hover:bg-gray-100 disabled:cursor-not-allowed disabled:opacity-40"
              >
                다음
              </button>
            </div>
          </div>
        )}
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
