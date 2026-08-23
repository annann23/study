import { useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../lib/auth'

export default function SignUpPage() {
  const { signUp } = useAuth()
  const navigate = useNavigate()
  const [loginId, setLoginId] = useState('')
  const [password, setPassword] = useState('')
  const [nickname, setNickname] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError('')
    setSubmitting(true)
    try {
      await signUp(loginId, password, nickname)
      navigate('/', { replace: true })
    } catch {
      setError('회원가입에 실패했습니다. 아이디를 확인해주세요.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="page-bg relative flex min-h-screen items-center justify-center overflow-hidden px-6">
      <div className="pointer-events-none absolute -top-32 -right-24 h-96 w-96 rounded-full bg-linear-to-br from-brand-300 to-brand-500 opacity-30 blur-3xl" />
      <div className="pointer-events-none absolute -bottom-32 -left-24 h-96 w-96 rounded-full bg-linear-to-tr from-brand-400 to-brand-700 opacity-20 blur-3xl" />

      <div className="relative w-full max-w-sm rounded-2xl border border-ink-100 bg-white/90 p-8 shadow-xl shadow-brand-900/10 backdrop-blur-sm">
        <span className="brand-text text-sm font-bold tracking-wide">게시판</span>
        <h1 className="mt-1 text-2xl font-semibold text-ink-900">회원가입</h1>
        <form onSubmit={handleSubmit} className="mt-6 flex flex-col gap-3">
          <input
            className="field"
            placeholder="아이디"
            value={loginId}
            onChange={(e) => setLoginId(e.target.value)}
            required
          />
          <input
            className="field"
            placeholder="닉네임"
            value={nickname}
            onChange={(e) => setNickname(e.target.value)}
            required
          />
          <input
            className="field"
            type="password"
            placeholder="비밀번호"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          {error && <p className="text-sm text-red-500">{error}</p>}
          <button type="submit" disabled={submitting} className="btn-primary mt-1 w-full">
            가입하기
          </button>
        </form>
        <p className="mt-5 text-sm text-ink-500">
          이미 계정이 있으신가요? <Link to="/login" className="font-medium text-brand-700 hover:underline">로그인</Link>
        </p>
      </div>
    </div>
  )
}
