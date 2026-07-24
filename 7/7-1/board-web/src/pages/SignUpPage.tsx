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
    <div className="mx-auto mt-24 max-w-sm p-8">
      <h1 className="text-2xl font-semibold">회원가입</h1>
      <form onSubmit={handleSubmit} className="mt-6 flex flex-col gap-4">
        <input
          className="rounded border border-gray-300 px-3 py-2"
          placeholder="아이디"
          value={loginId}
          onChange={(e) => setLoginId(e.target.value)}
          required
        />
        <input
          className="rounded border border-gray-300 px-3 py-2"
          placeholder="닉네임"
          value={nickname}
          onChange={(e) => setNickname(e.target.value)}
          required
        />
        <input
          className="rounded border border-gray-300 px-3 py-2"
          type="password"
          placeholder="비밀번호"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        {error && <p className="text-sm text-red-500">{error}</p>}
        <button
          type="submit"
          disabled={submitting}
          className="rounded bg-gray-900 py-2 text-white disabled:opacity-50"
        >
          가입하기
        </button>
      </form>
      <p className="mt-4 text-sm text-gray-500">
        이미 계정이 있으신가요? <Link to="/login" className="text-gray-900 underline">로그인</Link>
      </p>
    </div>
  )
}
