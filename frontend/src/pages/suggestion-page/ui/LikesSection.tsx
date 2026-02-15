import type { Suggestion } from '@/entities/suggestion';
import { STATUS_LABELS } from '@/entities/suggestion/lib/status';
import { cn } from '@/shared/lib/utils';
import { Button } from '@/shared/ui';
import { ThumbsDown, ThumbsUp } from 'lucide-react';

interface LikesSectionProps {
  suggestion: Suggestion;
  className?: string;
}

export function LikesSection({ suggestion, className }: LikesSectionProps) {
  return (
    <div
      className={cn(
        'flex flex-col sm:flex-row items-center gap-2 md:flex-col md:items-center md:gap-4',
        'w-full sm:justify-between md:w-auto md:justify-start',
        className
      )}
    >
      <div className="px-4 py-2 rounded-full text-sm font-medium md:px-0 md:py-0">
        Статус:{' '}
        <span className="font-bold">{STATUS_LABELS[suggestion.status]}</span>
      </div>
      <div>Всего лайков: {suggestion.likes_amount}</div>
      <div>Ваших лайков: {/** */}</div>
      <div className="flex gap-2 items-center">
        <Button variant="outline">
          <ThumbsUp className="w-4 h-4" />
        </Button>
        <Button variant="outline">
          <ThumbsDown className="w-4 h-4" />
        </Button>
      </div>
    </div>
  );
}
