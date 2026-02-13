import type { ProjectUser } from '@/shared/api/generated';
import { createContext, type JSX } from 'react';
import type { Comment } from './types';

interface CommentsContextValue {
  usersMap: Map<string, ProjectUser>;
  commentsTree: Map<string | null, Comment[]>;
  isAdmin: boolean;
  replyCommentId: string | null;
  onReplyComment: (commentId: string | null) => void;
  onDeleteComment: (commentId: string) => void;
  renderReply: (commentId: string) => JSX.Element;
}

export const commentContext = createContext<CommentsContextValue | null>(null);

export const CommentContextProviter = commentContext.Provider;
