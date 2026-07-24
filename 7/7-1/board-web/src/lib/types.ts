export type Board = {
  id: number
  boardTypeId: number
  name: string
}

export type BoardType = {
  id: number
  name: string
}

export type Post = {
  id: number
  boardId: number
  userId: number
  title: string
  content: string
  createdAt: string
}

export type Comment = {
  id: number
  content: string
  userId: number
  nickname: string
  isEdited: boolean | null
  createdAt: string
}

export type LikeStatus = {
  count: number
  likedByMe: boolean
}
