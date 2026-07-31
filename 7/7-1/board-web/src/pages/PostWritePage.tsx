import { useEffect, useState, type FormEvent } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { api } from "../lib/api";
import type { Board, BoardType, Post } from "../lib/types";
import RichTextEditor from "../components/RichTextEditor";

export default function PostWritePage() {
  const { boardId, postId } = useParams<{ boardId: string; postId?: string }>();
  const isEdit = postId != null;
  const navigate = useNavigate();

  const [board, setBoard] = useState<Board | null>(null);
  const [boardTypes, setBoardTypes] = useState<BoardType[]>([]);
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    let cancelled = false;

    Promise.all([
      api<Board>(`/board/${boardId}`),
      api<BoardType[]>("/board-types"),
      isEdit ? api<Post>(`/posts/${postId}`) : Promise.resolve(null),
    ]).then(([boardData, types, post]) => {
      if (cancelled) return;
      setBoard(boardData);
      setBoardTypes(types);
      if (post) {
        setTitle(post.title);
        setContent(post.content);
      }
      setLoading(false);
    });

    return () => {
      cancelled = true;
    };
  }, [boardId, postId, isEdit]);

  const allowImage =
    boardTypes.find((t) => t.id === board?.boardTypeId)?.name === "갤러리";

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError("");
    setSubmitting(true);
    try {
      if (isEdit) {
        await api("/posts", {
          method: "PUT",
          body: JSON.stringify({
            postId: Number(postId),
            post: { title, content },
          }),
        });
      } else {
        await api("/posts", {
          method: "POST",
          body: JSON.stringify({
            post: { title, content },
            boardId: Number(boardId),
          }),
        });
      }
      navigate(`/?board=${boardId}`);
    } catch {
      setError("저장에 실패했습니다.");
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <p className="mt-16 text-center text-sm text-gray-400">불러오는 중...</p>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="border-b border-gray-100 bg-white/80 px-6 py-4 backdrop-blur-md">
        <div className="mx-auto flex max-w-3xl items-center justify-between">
          <Link
            to={`/?board=${boardId}`}
            className="text-sm text-gray-400 hover:text-gray-600"
          >
            ← {board?.name}(으)로
          </Link>
          <h1 className="text-lg font-semibold text-gray-900">
            {isEdit ? "글 수정" : "글쓰기"}
          </h1>
          <span className="w-16" />
        </div>
      </header>

      <main className="mx-auto max-w-3xl px-6 py-8">
        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <input
            className="rounded-lg border border-gray-200 px-4 py-3 text-lg font-medium outline-none transition focus:border-gray-400"
            placeholder="제목"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            maxLength={20}
            required
            autoFocus
          />
          <RichTextEditor
            content={content}
            onChange={setContent}
            allowImage={allowImage}
          />
          {error && <p className="text-sm text-red-500">{error}</p>}
          <div className="flex justify-end gap-2">
            <Link
              to={`/?board=${boardId}`}
              className="rounded-lg px-4 py-2 text-sm font-medium text-gray-500 transition hover:bg-gray-100"
            >
              취소
            </Link>
            <button
              type="submit"
              disabled={submitting || !title.trim()}
              className="rounded-lg bg-gray-900 px-5 py-2 text-sm font-medium text-white transition hover:bg-gray-800 disabled:opacity-50"
            >
              저장
            </button>
          </div>
        </form>
      </main>
    </div>
  );
}
