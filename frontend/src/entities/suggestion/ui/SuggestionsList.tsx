import type { JSX } from 'react';
import { useSuggestions } from '../api/useSuggestions';
import { Card, CardDescription, CardTitle } from '@/shared/ui';
import { STATUS_LABELS } from '../lib/status';
import { Heart } from 'lucide-react';
import { Link } from 'react-router-dom';
import { routes } from '@/shared/route';

interface SuggestionsListProps {
  projectId: string;
}

export const SuggestionsList = (props: SuggestionsListProps): JSX.Element => {
  const {
    data: suggestions = [],
    isLoading,
    error,
  } = useSuggestions(props.projectId);

  if (isLoading) {
    return <div>Загрузка предложений...</div>;
  }

  if (error) {
    return <div>Ошибка загрузки предложений</div>;
  }
  return (
    <div className="flex flex-col items-center w-screen">
      <h1 className="text-center text-3xl my-4">Мои предложения</h1>
      <div className="grid gap-4 w-[90%] sm:w-[70%] grid-cols-1">
        {suggestions
          .filter(suggestion => suggestion.status != 'draft')
          .map(suggestion => (
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
                <div className="flex-1 items-start min-w-0 gap-6 mb-4">
                  <CardTitle className="text-lg font-semibold line-clamp-2 mb-2">
                    {suggestion.name}
                  </CardTitle>
                  <CardDescription className="line-clamp-3 leading-relaxed whitespace-pre-line">
                    {suggestion.description}
                  </CardDescription>
                </div>
                <div className="flex flex-col shrink-0 gap-4 items-end">
                  <div className="px-3 py-1.5 rounded-full text-xs font-medium bg-gray-100 text-gray-700">
                    {STATUS_LABELS[suggestion.status] || suggestion.status}
                  </div>{' '}
                  <div className="flex gap-1 text-sm ">
                    <Heart className="" /> {suggestion.likes_amount}
                  </div>
                  <div>{suggestion.placed_at}</div>
                </div>
              </Link>
            </Card>
          ))}
      </div>
    </div>
  );
};
