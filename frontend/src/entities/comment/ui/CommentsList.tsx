import type {
  Comment,
  ProjectUser,
  ProjectUserList,
} from '@/shared/api/generated';
import { Avatar, AvatarFallback, AvatarImage, Button } from '@/shared/ui';
import type { ReplyState } from '../model/types';
import type { Dispatch, JSX, SetStateAction } from 'react';

interface CommentsListProps {
  userList: ProjectUserList | undefined;
  comments: Comment[] | undefined;
  maxDepth: number;
  isReplyOpen: ReplyState;
  setReplyOpen: Dispatch<SetStateAction<ReplyState>>;
  renderReply: (commentId: string) => JSX.Element;
}

export function CommentsList({
  userList,
  comments,
  maxDepth = 3,
  isReplyOpen,
  setReplyOpen,
  renderReply,
}: CommentsListProps) {
  if (!userList || !comments) {
    return null;
  }
  const users = userList.users;
  const userMap = new Map<string, ProjectUser>(users.map(e => [e.user_id, e]));
  const replyParentCommentsTree = new Map<string | null, Comment[]>();
  for (const comment of comments) {
    if (comment.comment_reply_to_id) {
      const parent = comment.comment_reply_to_id;

      if (!replyParentCommentsTree.has(parent)) {
        replyParentCommentsTree.set(parent, []);
      }

      replyParentCommentsTree.get(parent)!.push(comment);
    } else {
      if (!replyParentCommentsTree.has(null)) {
        replyParentCommentsTree.set(null, []);
      }
      replyParentCommentsTree.get(null)!.push(comment);
    }
  }

  const RecursiveCommentHelper = ({
    parentId = null,
    depth = 0,
  }: {
    parentId?: string | null;
    depth?: number;
  }) => {
    const currentCommentsArray = replyParentCommentsTree.get(parentId) ?? [];
    if (currentCommentsArray.length == 0) {
      return null;
    }

    currentCommentsArray.sort(
      (a, b) =>
        new Date(a.placed_at).getTime() - new Date(b.placed_at).getTime()
    );

    const isBelowMaxDepth = depth < maxDepth;

    const childExist = (comment: Comment) => {
      return replyParentCommentsTree.has(comment.comment_id);
    };

    return (
      <div className="flex flex-col gap-4">
        {currentCommentsArray.map(comment => {
          return (
            <div key={comment.comment_id} className="flex flex-col gap-4">
              <div className="flex gap-3">
                <Avatar className="h-9 w-9 shrink-0">
                  <AvatarImage src={userMap.get(comment.user_id)?.avatar_url} />
                  <AvatarFallback className="bg-black text-white font-medium hover:bg-zinc-800 transition-colors" />
                </Avatar>
                <div className="flex flex-col items-start gap-1 w-full">
                  <div className="font-bold max-w-[30vw] truncate">
                    {userMap.get(comment.user_id)?.nickname}
                  </div>
                  <div className="">{comment.text} </div>
                  {(!isReplyOpen.isOpen ||
                    isReplyOpen.commentId != comment.comment_id) && (
                    <Button
                      variant="ghost"
                      className="h-6"
                      onClick={() =>
                        setReplyOpen({
                          isOpen: true,
                          commentId: comment.comment_id,
                        })
                      }
                    >
                      Ответить
                    </Button>
                  )}
                  {isReplyOpen.isOpen &&
                    isReplyOpen.commentId === comment.comment_id &&
                    renderReply(comment.comment_id)}
                </div>
              </div>
              {isBelowMaxDepth && childExist(comment) ? (
                <div className="ml-3 border-l-2 pl-3">
                  {
                    <RecursiveCommentHelper
                      parentId={comment.comment_id}
                      depth={depth + 1}
                    />
                  }
                </div>
              ) : (
                <RecursiveCommentHelper
                  parentId={comment.comment_id}
                  depth={depth + 1}
                />
              )}
            </div>
          );
        })}
      </div>
    );
  };
  return <RecursiveCommentHelper />;
}
