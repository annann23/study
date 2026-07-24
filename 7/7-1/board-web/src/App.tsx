import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { AuthProvider } from './lib/auth'
import RequireAuth, { RequireGuest } from './lib/RequireAuth'
import LoginPage from './pages/LoginPage'
import SignUpPage from './pages/SignUpPage'
import BoardPage from './pages/BoardPage'
import PostWritePage from './pages/PostWritePage'
import PostDetailPage from './pages/PostDetailPage'

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
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}

export default App
