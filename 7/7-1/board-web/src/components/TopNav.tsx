import type { Board } from '../lib/types'
import { hasAnyPermission, hasPermission, useAuth } from '../lib/auth'
import SettingsMenu from './SettingsMenu'
import UserMenu from './UserMenu'

const SETTINGS_PERMISSIONS = [
  'USER_LEVEL_CREATE',
  'USER_LEVEL_UPDATE',
  'USER_LEVEL_DELETE',
  'USER_LEVEL_ASSIGN',
  'ROLE_CREATE',
  'ROLE_UPDATE',
  'ROLE_DELETE',
  'ROLE_ASSIGN',
]

export default function TopNav({
  boards,
  selectedBoardId,
  onSelectBoard,
  onAddBoard,
  onEditBoard,
  onDeleteBoard,
}: {
  boards: Board[]
  selectedBoardId: number | null
  onSelectBoard: (id: number) => void
  onAddBoard: () => void
  onEditBoard: (board: Board) => void
  onDeleteBoard: (board: Board) => void
}) {
  const { user } = useAuth()
  const canCreateBoard = hasPermission(user, 'BOARD_CREATE')
  const canUpdateBoard = hasPermission(user, 'BOARD_UPDATE')
  const canDeleteBoard = hasPermission(user, 'BOARD_DELETE')
  const canAccessSettings = hasAnyPermission(user, SETTINGS_PERMISSIONS)

  return (
    <header className="sticky top-0 z-10 border-b border-gray-100 bg-white/80 backdrop-blur-md">
      <div className="mx-auto flex max-w-4xl items-center justify-between px-6 py-4">
        <span className="text-lg font-semibold tracking-tight text-gray-900">게시판</span>
        <div className="flex items-center gap-1">
          {canAccessSettings && <SettingsMenu />}
          <UserMenu />
        </div>
      </div>
      <nav className="mx-auto flex max-w-4xl items-center gap-1 overflow-x-auto px-6">
        {boards.map((board) => {
          const active = board.id === selectedBoardId
          return (
            <div key={board.id} className="group relative flex shrink-0 items-center">
              <button
                onClick={() => onSelectBoard(board.id)}
                className={`px-4 py-3 text-sm font-medium transition ${
                  active ? 'text-gray-900' : 'text-gray-400 hover:text-gray-600'
                }`}
              >
                {board.name}
              </button>
              {active && (
                <>
                  <span className="absolute inset-x-3 bottom-0 h-0.5 rounded-full bg-gray-900" />
                  {(canUpdateBoard || canDeleteBoard) && (
                    <span className="mr-1 hidden items-center gap-0.5 group-hover:flex">
                      {canUpdateBoard && (
                        <button
                          onClick={() => onEditBoard(board)}
                          aria-label="게시판 수정"
                          className="rounded p-1 text-xs text-gray-400 transition hover:bg-gray-100 hover:text-gray-700"
                        >
                          ✎
                        </button>
                      )}
                      {canDeleteBoard && (
                        <button
                          onClick={() => onDeleteBoard(board)}
                          aria-label="게시판 삭제"
                          className="rounded p-1 text-xs text-gray-400 transition hover:bg-gray-100 hover:text-red-500"
                        >
                          ✕
                        </button>
                      )}
                    </span>
                  )}
                </>
              )}
            </div>
          )
        })}
        {canCreateBoard && (
          <button
            onClick={onAddBoard}
            aria-label="게시판 추가"
            className="ml-1 flex shrink-0 items-center gap-1 rounded-full px-3 py-1.5 text-sm text-gray-400 transition hover:bg-gray-100 hover:text-gray-700"
          >
            + 게시판
          </button>
        )}
      </nav>
    </header>
  )
}
