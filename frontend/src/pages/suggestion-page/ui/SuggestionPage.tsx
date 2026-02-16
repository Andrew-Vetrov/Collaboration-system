import { Navigate, useNavigate, useParams } from 'react-router-dom';
import { useSuggestion } from '@/entities/suggestion';
import { Button, Card, CardContent } from '@/shared/ui';
import { STATUS_LABELS } from '@/entities/suggestion/lib/status';
import { ThumbsUp, ThumbsDown } from 'lucide-react';
import { useCallback, useContext, useState } from 'react';
import { EditSuggestionDialog } from './EditSuggestionDialog';
import { useProjectPermissions } from '@/entities/project/api/useProjectPermissions';
import { useAuthMe } from '@/entities/main-user/api/useAuthMe';
import { useProjectUsers } from '@/entities/project/api/useProjectUsers';
import { useComments } from '@/entities/comment/api/useComments';
import { CommentsList } from '@/entities/comment/ui/CommentsList';
import { CommentForm } from '@/pages/suggestion-page/ui/CommentForm';
import { CommentReplyForm } from './CommentReplyForm';
import { formOption, maxReplyDepth } from '../lib/utilData';
import { useCommentDelete } from '@/entities/comment/api/useCommentDelete';
import { LikesSection } from './LikesSection';
import { useSuggestionDelete } from '@/features/suggestion-delete/api/useSuggestionDelete';

const SuggestionPage = () => {
  const { projectId, suggestionId } = useParams<{
    projectId: string;
    suggestionId: string;
  }>();

  const [isOpen, setIsOpen] = useState<boolean>(false);
  if (!projectId || !suggestionId) {
    return <Navigate to="/not-found" replace />;
  }

  const navigate = useNavigate();

  const { data: permissions } = useProjectPermissions(projectId);
  const { data: currentUser } = useAuthMe();
  const { data: suggestion, isLoading, isError } = useSuggestion(suggestionId);
  const { mutate: deleteSuggestion, isPending } = useSuggestionDelete(
    projectId,
    suggestionId
  );
  const handleDeleteSuggestiontMutation = () => {
    deleteSuggestion(undefined, {
      onSuccess: () => {
        navigate(`/projects/${projectId}`);
      },
    });
  };

  const { data: userList } = useProjectUsers(projectId);
  const { data: comments } = useComments(suggestionId);
  const { mutate: deleteCommentMutation } = useCommentDelete(suggestionId);

  const [replyCommentId, setReplyCommentId] = useState<string | null>(null);

  const renderReply = useCallback(
    (commentId: string) => (
      <CommentReplyForm
        suggestionId={suggestionId}
        commentId={commentId}
        formOptions={formOption}
        replyCommentId={replyCommentId}
        setReplyCommentId={setReplyCommentId}
      />
    ),
    [suggestionId, formOption, replyCommentId, setReplyCommentId]
  );

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
        <div className="w-full max-w-5xl flex flex-col md:flex-row gap-6 px-2 md:px-4">
          <div className="shrink-0 md:w-32 md:min-w-32 flex items-start">
            {canEdit && (
              <div className="flex flex-col gap-2 w-full md:w-auto">
                <Button variant="outline" onClick={() => setIsOpen(true)}>
                  Редактировать
                </Button>
                <Button
                  disabled={isPending}
                  variant="outline"
                  onClick={handleDeleteSuggestiontMutation}
                >
                  {isPending ? 'Удаление' : 'Удалить'}
                </Button>
              </div>
            )}
          </div>

          <div className="flex-1 flex flex-col items-center md:text-center text-left">
            <h1 className="text-2xl md:text-4xl font-bold mb-4 md:mb-6">
              {suggestion.name}
            </h1>
            <Card className="w-full max-w-3xl p-4 md:p-8 bg-card shadow-lg border">
              <CardContent className="p-0">
                <p className="text-base md:text-lg whitespace-pre-line leading-relaxed">
                  {suggestion.description}
                </p>
              </CardContent>
            </Card>

            <LikesSection
              suggestion={suggestion}
              className="flex md:hidden md:mt-0 mt-3"
            />

            <div className="order-1 md:order-2 mt-8 w-full flex flex-col gap-4">
              <CommentForm
                suggestionId={suggestionId}
                formOptions={formOption}
              />
              <CommentsList
                userList={userList?.users}
                comments={comments}
                maxReplyDepth={maxReplyDepth}
                replyCommentId={replyCommentId}
                setReplyCommentId={setReplyCommentId}
                isAdmin={permissions?.is_admin || false}
                onDeleteComment={deleteCommentMutation}
                renderReply={commentId => renderReply(commentId)}
              />
            </div>
          </div>

          <LikesSection suggestion={suggestion} className="hidden md:flex" />
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
