import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { AuthProvider } from './lib/auth'
import RequireAuth, { RequireGuest } from './lib/RequireAuth'
import LoginPage from './pages/LoginPage'
import SignUpPage from './pages/SignUpPage'
import BoardPage from './pages/BoardPage'
import PostWritePage from './pages/PostWritePage'
import PostDetailPage from './pages/PostDetailPage'
import UserLevelManagePage from './pages/UserLevelManagePage'
import RoleManagePage from './pages/RoleManagePage'

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route
            path="/login"
            element={
              <RequireGuest>
                <LoginPage />
              </RequireGuest>
            }
          />
          <Route
            path="/sign-up"
            element={
              <RequireGuest>
                <SignUpPage />
              </RequireGuest>
            }
          />
          <Route
            path="/"
            element={
              <RequireAuth>
                <BoardPage />
              </RequireAuth>
            }
          />
          <Route
            path="/board/:boardId/write"
            element={
              <RequireAuth>
                <PostWritePage />
              </RequireAuth>
            }
          />
          <Route
            path="/board/:boardId/posts/:postId/edit"
            element={
              <RequireAuth>
                <PostWritePage />
              </RequireAuth>
            }
          />
          <Route
            path="/board/:boardId/posts/:postId"
            element={
              <RequireAuth>
                <PostDetailPage />
              </RequireAuth>
            }
          />
          <Route
            path="/admin/users"
            element={
              // TODO: MODERATOR/ADMIN 권한만 접근 가능하도록 RequireAuth를 권한 체크 가드로 교체할 것
              <RequireAuth>
                <UserLevelManagePage />
              </RequireAuth>
            }
          />
          <Route
            path="/admin/roles"
            element={
              // TODO: ADMIN 권한만 접근 가능하도록 RequireAuth를 권한 체크 가드로 교체할 것
              <RequireAuth>
                <RoleManagePage />
              </RequireAuth>
            }
          />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}

export default App
