import { useEditor, EditorContent } from '@tiptap/react'
import StarterKit from '@tiptap/starter-kit'
import Image from '@tiptap/extension-image'
import Underline from '@tiptap/extension-underline'
import Link from '@tiptap/extension-link'
import Placeholder from '@tiptap/extension-placeholder'
import { useEffect, useRef } from 'react'
import type { Editor } from '@tiptap/react'

function fileToDataUrl(file: File): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => resolve(reader.result as string)
    reader.onerror = reject
    reader.readAsDataURL(file)
  })
}

function ToolbarButton({
  onClick,
  active,
  label,
  title,
}: {
  onClick: () => void
  active?: boolean
  label: string
  title: string
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      title={title}
      className={`rounded px-2 py-1.5 text-sm transition hover:bg-gray-100 ${
        active ? 'bg-gray-100 text-gray-900' : 'text-gray-500'
      }`}
    >
      {label}
    </button>
  )
}

function Toolbar({ editor, allowImage }: { editor: Editor; allowImage: boolean }) {
  const fileInputRef = useRef<HTMLInputElement>(null)

  const handleImagePick = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    e.target.value = ''
    if (!file) return
    const dataUrl = await fileToDataUrl(file)
    editor.chain().focus().setImage({ src: dataUrl }).run()
  }

  const handleLink = () => {
    const previousUrl = editor.getAttributes('link').href as string | undefined
    const url = window.prompt('링크 주소', previousUrl ?? 'https://')
    if (url === null) return
    if (url === '') {
      editor.chain().focus().extendMarkRange('link').unsetLink().run()
      return
    }
    editor.chain().focus().extendMarkRange('link').setLink({ href: url }).run()
  }

  return (
    <div className="flex flex-wrap items-center gap-0.5 rounded-t-lg border border-b-0 border-gray-200 bg-gray-50 p-1">
      <ToolbarButton
        title="제목1"
        label="H1"
        active={editor.isActive('heading', { level: 1 })}
        onClick={() => editor.chain().focus().toggleHeading({ level: 1 }).run()}
      />
      <ToolbarButton
        title="제목2"
        label="H2"
        active={editor.isActive('heading', { level: 2 })}
        onClick={() => editor.chain().focus().toggleHeading({ level: 2 }).run()}
      />
      <span className="mx-1 h-4 w-px bg-gray-200" />
      <ToolbarButton
        title="굵게"
        label="B"
        active={editor.isActive('bold')}
        onClick={() => editor.chain().focus().toggleBold().run()}
      />
      <ToolbarButton
        title="기울임"
        label="I"
        active={editor.isActive('italic')}
        onClick={() => editor.chain().focus().toggleItalic().run()}
      />
      <ToolbarButton
        title="밑줄"
        label="U"
        active={editor.isActive('underline')}
        onClick={() => editor.chain().focus().toggleUnderline().run()}
      />
      <ToolbarButton
        title="취소선"
        label="S"
        active={editor.isActive('strike')}
        onClick={() => editor.chain().focus().toggleStrike().run()}
      />
      <span className="mx-1 h-4 w-px bg-gray-200" />
      <ToolbarButton
        title="목록"
        label="•"
        active={editor.isActive('bulletList')}
        onClick={() => editor.chain().focus().toggleBulletList().run()}
      />
      <ToolbarButton
        title="번호 목록"
        label="1."
        active={editor.isActive('orderedList')}
        onClick={() => editor.chain().focus().toggleOrderedList().run()}
      />
      <ToolbarButton
        title="인용"
        label="❝"
        active={editor.isActive('blockquote')}
        onClick={() => editor.chain().focus().toggleBlockquote().run()}
      />
      <ToolbarButton
        title="코드 블록"
        label="{ }"
        active={editor.isActive('codeBlock')}
        onClick={() => editor.chain().focus().toggleCodeBlock().run()}
      />
      <ToolbarButton title="링크" label="🔗" active={editor.isActive('link')} onClick={handleLink} />
      {allowImage && (
        <>
          <ToolbarButton title="사진" label="🖼" onClick={() => fileInputRef.current?.click()} />
          <input
            ref={fileInputRef}
            type="file"
            accept="image/*"
            className="hidden"
            onChange={handleImagePick}
          />
        </>
      )}
      <span className="mx-1 h-4 w-px bg-gray-200" />
      <ToolbarButton
        title="실행 취소"
        label="↺"
        onClick={() => editor.chain().focus().undo().run()}
      />
      <ToolbarButton title="다시 실행" label="↻" onClick={() => editor.chain().focus().redo().run()} />
    </div>
  )
}

export default function RichTextEditor({
  content,
  onChange,
  allowImage,
}: {
  content: string
  onChange: (html: string) => void
  allowImage: boolean
}) {
  const editor = useEditor({
    extensions: [
      StarterKit,
      Underline,
      Image,
      Link.configure({ openOnClick: false }),
      Placeholder.configure({ placeholder: '내용을 입력하세요...' }),
    ],
    content,
    onUpdate: ({ editor }) => onChange(editor.getHTML()),
    editorProps: {
      attributes: {
        class:
          'post-editor min-h-64 rounded-b-lg border border-gray-200 px-3 py-2 outline-none focus:border-gray-400',
      },
    },
  })

  useEffect(() => {
    if (editor && content !== editor.getHTML()) {
      editor.commands.setContent(content)
    }
  }, [content, editor])

  if (!editor) return null

  return (
    <div className="flex flex-col">
      <Toolbar editor={editor} allowImage={allowImage} />
      <EditorContent editor={editor} />
    </div>
  )
}
