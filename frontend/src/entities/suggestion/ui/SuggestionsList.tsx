import { useState, type JSX } from 'react';
import { useSuggestions } from '../api/useSuggestions';
import {
  Avatar,
  AvatarImage,
  Card,
  CardDescription,
  CardTitle,
} from '@/shared/ui';
import { STATUS_LABELS } from '../lib/status';
import { Heart } from 'lucide-react';
import { Link } from 'react-router-dom';
import { routes } from '@/shared/route';
import { parseDateTime } from '@/shared/lib/utils';
import { useProjectUsers } from '@/entities/project/api/useProjectUsers';
import { TagComponent } from '@/entities/tag/ui/TagComponent';
import { TagSortMenu } from '@/pages/project-page/ui/TagSortMenu';

interface SuggestionsListProps {
  projectId: string;
}

export const SuggestionsList = (props: SuggestionsListProps): JSX.Element => {
  const [tagSort, setTagSort] = useState<string | undefined>(undefined);

  const {
    data: suggestions = [],
    isLoading: isSuggestionsLoading,
    isError: isSuggestionsError,
  } = useSuggestions(props.projectId, tagSort);

  const {
    data: projectUsers,
    isLoading: isProjectUsersLoading,
    isError: isProjectUsersError,
  } = useProjectUsers(props.projectId);

  const suggestionsWithUsers = suggestions
    .map(suggestion => {
      const user = projectUsers?.users.find(
        user => user.user_id === suggestion.user_id
      );

      if (!user) return null;

      return {
        ...suggestion,
        user,
      };
    })
    .filter(Boolean);

  if (isSuggestionsLoading || isProjectUsersLoading) {
    return <div>Загрузка предложений...</div>;
  }

  if (isSuggestionsError || isProjectUsersError) {
    return <div>Ошибка загрузки предложений</div>;
  }

  return (
    <div className="flex flex-col items-center w-full">
      <h1 className="text-center text-3xl my-4">Предложения</h1>
      <div className="grid gap-4 w-[90%] sm:w-[70%] grid-cols-1">
        <div className="w-full flex justify-start">
          <TagSortMenu
            projectId={props.projectId}
            setTagSort={setTagSort}
            tagSort={tagSort}
          />
        </div>
        {suggestionsWithUsers
          .filter(suggestion => suggestion?.status !== 'draft')
          .map(suggestion => {
            if (!suggestion) return null;

            const parsedTime = parseDateTime(suggestion.placed_at || '');

            return (
              <Card
                key={suggestion.suggestion_id}
                className="p-6 hover:shadow-lg transition-shadow duration-200"
              >
                <Link
                  className="flex flex-col sm:flex-row sm:justify-between gap-4"
                  to={routes.suggestionRoute(
                    props.projectId,
                    suggestion.suggestion_id || ''
                  )}
                >
                  <div className="flex flex-col flex-1 min-w-0 gap-4">
                    <div>
                      <CardTitle className="text-lg font-semibold line-clamp-2 mb-2">
                        {suggestion.name}
                      </CardTitle>

                      <CardDescription className="line-clamp-3 leading-relaxed whitespace-pre-line">
                        {suggestion.description}
                      </CardDescription>
                    </div>

                    {suggestion.tags && suggestion.tags.length > 0 && (
                      <div className="flex flex-wrap gap-2">
                        {suggestion.tags.map(tag => (
                          <TagComponent key={tag.tag_id} tag={tag} />
                        ))}
                      </div>
                    )}

                    <div className="flex gap-2 items-center min-w-0">
                      <Avatar className="h-9 w-9 shrink-0">
                        <AvatarImage
                          src={suggestion.user?.avatar_url}
                          alt="Аватар пользователя"
                        />
                      </Avatar>

                      <div className="min-w-0 max-w-full break-all text-left">
                        {suggestion.user?.nickname ||
                          'Неизвестный пользователь'}
                      </div>
                    </div>
                  </div>

                  <div className="flex flex-col shrink-0 gap-4 items-end">
                    <div className="px-3 py-1.5 rounded-full text-xs font-medium bg-gray-100 text-gray-700">
                      {STATUS_LABELS[suggestion.status] || suggestion.status}
                    </div>

                    <div className="flex flex-col gap-2 items-end">
                      <div className="flex gap-1 text-sm">
                        <Heart className="fill-(--color-filled-heart)" />
                        {suggestion.user_likes_amount}
                      </div>

                      <div className="flex gap-1 text-sm">
                        <Heart />
                        {suggestion.likes_amount}
                      </div>
                    </div>

                    <div className="text-xs text-gray-500">
                      {parsedTime?.time} {parsedTime?.date.day}-
                      {parsedTime?.date.month}-{parsedTime?.date.year}
                    </div>
                  </div>
                </Link>
              </Card>
            );
          })}
      </div>
    </div>
  );
};
