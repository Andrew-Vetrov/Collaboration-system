import { Button } from '@/shared/ui';
import { Loader2 } from 'lucide-react';

interface DeleteUserButtonProps {
  userId: string;
  isLoading: boolean;
  onDelete: (userId: string) => Promise<void>;
}

export function DeleteUserButton({
  userId,
  isLoading,
  onDelete,
}: DeleteUserButtonProps) {
  return (
    <Button
      variant="outline"
      className="w-full h-7"
      disabled={isLoading}
      onClick={() => onDelete(userId)}
    >
      {isLoading ? (
        <Loader2 className="h-4 w-4 animate-spin" />
      ) : (
        'Удалить из проекта'
      )}
    </Button>
  );
}
