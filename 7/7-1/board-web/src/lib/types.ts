export type Board = {
  id: number
  boardTypeId: number
  name: string
  isPrivate: boolean
}

export type BoardType = {
  id: number
  name: string
}

export type Post = {
  id: number
  boardId: number
  userId: number
  nickName: string
  userLevel: string
  title: string
  content: string
  createdAt: string
}

export type PageResponse<T> = {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

export type Comment = {
  id: number
  content: string
  userId: number
  nickname: string
  parentId: number | null
  isEdited: boolean | null
  createdAt: string
}

export type LikeStatus = {
  count: number
  likedByMe: boolean
}

export type UserLevel = {
  id: number
  name: string
}

export type Permission = {
  id: number
  name: string
}

export type Role = {
  id: number
  name: string
  permissions: string[]
}
