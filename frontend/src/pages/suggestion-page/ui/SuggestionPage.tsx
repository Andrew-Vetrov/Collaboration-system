import { Navigate, useParams } from 'react-router-dom';
import { useSuggestion } from '@/entities/suggestion';
import { Button, Card, CardContent } from '@/shared/ui';
import { STATUS_LABELS } from '@/entities/suggestion/lib/status';
import { ThumbsUp, ThumbsDown } from 'lucide-react';
import { useState } from 'react';
import { EditSuggestionDialog } from './EditSuggestionDialog';
import { useProjectPermissions } from '@/entities/project/api/useProjectPermissions';
import { useAuthMe } from '@/entities/main-user/api/useAuthMe';
import { useProjectUsers } from '@/entities/project/api/useProjectUsers';
import { useComments } from '@/entities/comment/api/useComments';
import { CommentsList } from '@/entities/comment/ui/CommentsList';
import { CommentForm } from '@/pages/suggestion-page/ui/CommentForm';
import type { ReplyState } from '@/entities/comment/model/types';
import { CommentReplyForm } from './CommentReplyForm';
import { formOption, maxCommentLength } from '../lib/utilData';

const SuggestionPage = () => {
  const { projectId, suggestionId } = useParams<{
    projectId: string;
    suggestionId: string;
  }>();

  const [isOpen, setIsOpen] = useState<boolean>(false);
  if (!projectId || !suggestionId) {
    return <Navigate to="/not-found" replace />;
  }

  const { data: permissions } = useProjectPermissions(projectId);
  const { data: currentUser } = useAuthMe();
  const { data: suggestion, isLoading, isError } = useSuggestion(suggestionId);

  const { data: userList } = useProjectUsers(projectId);
  const { data: comments } = useComments(suggestionId);

  const [isReplyOpen, setReplyOpen] = useState<ReplyState>({
    isOpen: false,
    commentId: null,
  });

  if (isLoading) {
    return <div className="p-8 text-center">Загрузка...</div>;
  }

  if (
    isError ||
    !suggestion ||
    suggestion.project_id !== projectId ||
    suggestion.status === 'draft'
  ) {
    return (
      <div className="p-8 text-center">
        <h1 className="text-2xl font-bold mb-2">Предложение не найдено</h1>
        <p className="text-gray-500">
          Такого предложения не существует или у вас нет к нему доступа.
        </p>
      </div>
    );
  }

  const canEdit =
    permissions?.is_admin ||
    (currentUser && suggestion.user_id === currentUser.user_id);

  return (
    <main className="relative min-h-screen flex flex-col">
      <div className="flex-1 flex items-start justify-center py-4">
        <div className="w-full max-w-5xl flex items-start justify-between gap-8 px-4">
          <div className="shrink-0 w-32 min-w-32 flex items-start">
            {canEdit && (
              <Button variant="outline" onClick={() => setIsOpen(true)}>
                Редактировать
              </Button>
            )}
          </div>

          <div className="flex-1 flex flex-col items-center text-center">
            <h1 className="text-4xl font-bold mb-6">{suggestion.name}</h1>
            <Card className="w-full max-w-3xl p-8 bg-card shadow-lg border">
              <CardContent className="p-0">
                <p className="text-lg whitespace-pre-line leading-relaxed text-left">
                  {suggestion.description}
                </p>
              </CardContent>
            </Card>

            <div className="mt-8 w-full mx-auto flex flex-col gap-4">
              <CommentForm
                suggestionId={suggestionId}
                formOptions={formOption}
              />
              <CommentsList
                userList={userList}
                comments={comments}
                maxDepth={maxCommentLength}
                isReplyOpen={isReplyOpen}
                setReplyOpen={setReplyOpen}
                renderReply={commentId => (
                  <CommentReplyForm
                    suggestionId={suggestionId}
                    commentId={commentId}
                    formOptions={formOption}
                    isReplyOpen={isReplyOpen}
                    setReplyOpen={setReplyOpen}
                  />
                )}
              />
            </div>
          </div>

          <div className="shrink-0 flex flex-col items-center gap-4">
            <div className="px-4 py-2  rounded-full text-sm font-medium">
              Статус: {STATUS_LABELS[suggestion.status]}
            </div>
            <div>Всего лайков: {suggestion.likes_amount}</div>
            <div>Ваших лайков: {/** */}</div>
            <div className="relative flex gap-2 items-center">
              <Button variant="outline">
                <ThumbsUp className="w-4 h-4" />
              </Button>
              <Button variant="outline">
                <ThumbsDown className="w-4 h-4" />
              </Button>
            </div>
          </div>
        </div>
      </div>
      {canEdit && (
        <EditSuggestionDialog
          isOpen={isOpen}
          setIsOpen={setIsOpen}
          projectId={projectId}
          suggestionId={suggestionId}
          isAdmin={permissions?.is_admin || false}
          name={suggestion.name}
          description={suggestion.description}
          status={suggestion.status}
        />
      )}
    </main>
  );
};

export default SuggestionPage;
