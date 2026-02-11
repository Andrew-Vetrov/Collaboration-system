export type { Comment } from '@/shared/api/generated/api';

export interface ReplyState {
  isOpen: boolean;
  commentId: string | null;
}
