import type { Suggestion } from '@/entities/suggestion';
import { useSuggestionAddLike } from '@/entities/suggestion/api/useSuggestionAddLike';
import { useSuggestionRemoveLike } from '@/entities/suggestion/api/useSuggestionRemoveLike';
import { STATUS_LABELS } from '@/entities/suggestion/lib/status';
import { cn } from '@/shared/lib/utils';
import { Button } from '@/shared/ui';
import { ThumbsDown, ThumbsUp } from 'lucide-react';

interface LikesSectionProps {
  projectId: string;
  suggestion: Suggestion;
  className?: string;
}

export function LikesSection({
  projectId,
  suggestion,
  className,
}: LikesSectionProps) {
  const { mutate: addLike } = useSuggestionAddLike(
    suggestion.suggestion_id,
    projectId
  );
  const { mutate: removeLike } = useSuggestionRemoveLike(
    suggestion.suggestion_id,
    projectId
  );
  return (
    <div
      className={cn(
        'flex flex-col sm:flex-row items-center gap-2 md:flex-col md:items-start md:gap-4',
        'w-full sm:justify-between md:w-auto md:justify-start',
        className
      )}
    >
      <div className="px-4 py-2 rounded-full text-sm font-medium md:px-0 md:py-0">
        Статус:{' '}
        <span className="font-bold">{STATUS_LABELS[suggestion.status]}</span>
      </div>
      <div className="flex gap-1">
        <span>Всего лайков:</span>
        <span className="tabular-nums">{suggestion.likes_amount}</span>
      </div>

      <div className="flex gap-1">
        <span>Ваших лайков:</span>
        <span className="tabular-nums">{suggestion.user_likes_amount}</span>
      </div>
      <div className="flex gap-2 items-center">
        <Button variant="outline" onClick={() => addLike()}>
          <ThumbsUp className="w-4 h-4" />
        </Button>
        <Button variant="outline" onClick={() => removeLike()}>
          <ThumbsDown className="w-4 h-4" />
        </Button>
      </div>
    </div>
  );
}
