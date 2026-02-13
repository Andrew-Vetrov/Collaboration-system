import type { Comment, ProjectUser } from '@/shared/api/generated';
import {
  Avatar,
  AvatarFallback,
  AvatarImage,
  Button,
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuTrigger,
} from '@/shared/ui';
import {
  memo,
  useContext,
  useMemo,
  type Dispatch,
  type JSX,
  type SetStateAction,
} from 'react';
import { EllipsisVertical } from 'lucide-react';
import { DropdownMenuItem } from '@radix-ui/react-dropdown-menu';
import {
  commentContext,
  CommentContextProviter,
} from '../model/CommentsContext';

interface CommentsListProps {
  userList: Array<ProjectUser> | undefined;
  comments: Comment[] | undefined;
  maxReplyDepth: number;
  replyCommentId: string | null;
  setReplyCommentId: Dispatch<SetStateAction<string | null>>;
  renderReply: (commentId: string) => JSX.Element;
  isAdmin: boolean;
  onDeleteComment: (commentId: string) => void;
}

export function CommentsList({
  userList,
  comments,
  maxReplyDepth = 3,
  replyCommentId,
  setReplyCommentId,
  renderReply,
  isAdmin,
  onDeleteComment,
}: CommentsListProps) {
  if (!userList || !comments) {
    return null;
  }

  const usersMap = useMemo(
    () => new Map<string, ProjectUser>(userList.map(e => [e.user_id, e])),
    [userList]
  );

  const commentsTree = useMemo(() => {
    const tree = new Map<string | null, Comment[]>();

    for (const comment of comments) {
      const parentId = comment.comment_reply_to_id ?? null;
      if (!tree.has(parentId)) tree.set(parentId, []);
      tree.get(parentId)!.push(comment);
    }

    tree.forEach(arr => {
      arr.sort(
        (a, b) =>
          new Date(a.placed_at).getTime() - new Date(b.placed_at).getTime()
      );
    });

    return tree;
  }, [comments]);

  return (
    <CommentContextProviter
      value={{
        usersMap,
        commentsTree,
        isAdmin,
        replyCommentId,
        onReplyComment: setReplyCommentId,
        onDeleteComment,
        renderReply,
      }}
    >
      <RecursiveCommentHelper
        maxDepth={maxReplyDepth}
        depth={0}
        parentId={null}
      />
    </CommentContextProviter>
  );
}

type CommentProps = {
  parentId?: string | null;
  depth?: number;
  maxDepth: number;
};

const RecursiveCommentHelper = memo(
  ({ parentId = null, depth = 0, maxDepth }: CommentProps) => {
    const context = useContext(commentContext);
    if (!context) return null;
    const {
      usersMap,
      commentsTree,
      isAdmin,
      replyCommentId,
      onReplyComment,
      onDeleteComment,
      renderReply,
    } = context;

    const comments = commentsTree.get(parentId) ?? [];

    if (comments.length === 0) return null;

    const isBelowMaxReplyDepth = depth < maxDepth;

    const hasChildren = (comment: Comment) =>
      commentsTree.has(comment.comment_id);

    return (
      <div className="flex flex-col gap-4">
        {comments.map(comment => {
          const isReplying = replyCommentId === comment.comment_id;

          return (
            <div key={comment.comment_id} className="flex flex-col gap-4">
              <div className="flex gap-3">
                <Avatar className="h-9 w-9 shrink-0">
                  <AvatarImage
                    src={usersMap.get(comment.user_id)?.avatar_url}
                  />
                  <AvatarFallback className="bg-black text-white font-medium hover:bg-zinc-800 transition-colors" />
                </Avatar>
                <div className="flex flex-col items-start gap-1 w-full relative">
                  <div className="font-bold max-w-[30vw] truncate">
                    {usersMap.get(comment.user_id)?.nickname}
                  </div>
                  <div className="flex justify-between item-start w-full">
                    <div className="flex-1 text-start min-w-0 wrap-anywhere whitespace-normal ">
                      {comment.text}{' '}
                    </div>
                    {isAdmin && (
                      <div className="absolute top-0 right-0">
                        <DropdownMenu>
                          <DropdownMenuTrigger className="shrink-0">
                            <EllipsisVertical />
                          </DropdownMenuTrigger>
                          <DropdownMenuContent
                            side="bottom"
                            align="end"
                            className="text-center"
                          >
                            <DropdownMenuItem>
                              <Button
                                variant="ghost"
                                className="text-red-500"
                                onClick={() => {
                                  onDeleteComment(comment.comment_id);
                                }}
                              >
                                Удалить
                              </Button>
                            </DropdownMenuItem>
                          </DropdownMenuContent>
                        </DropdownMenu>
                      </div>
                    )}
                  </div>
                  {!isReplying && (
                    <Button
                      variant="ghost"
                      className="h-6"
                      onClick={() => onReplyComment(comment.comment_id)}
                    >
                      Ответить
                    </Button>
                  )}
                  {isReplying && renderReply(comment.comment_id)}
                </div>
              </div>

              {hasChildren(comment) && (
                <div
                  className={isBelowMaxReplyDepth ? 'ml-3 border-l-2 pl-3' : ''}
                >
                  <RecursiveCommentHelper
                    parentId={comment.comment_id}
                    depth={depth + 1}
                    maxDepth={maxDepth}
                  />
                </div>
              )}
            </div>
          );
        })}
      </div>
    );
  }
);

RecursiveCommentHelper.displayName = 'RecursiveCommentHelper';
