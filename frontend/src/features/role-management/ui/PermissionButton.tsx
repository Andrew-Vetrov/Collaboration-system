import { Button } from '@/shared/ui';
import { Loader2 } from 'lucide-react';

interface PermissionButtonProps {
  userId: string;
  isAdmin: boolean;
  isLoading: boolean;
  onPermissionChange: (userId: string, setIsAdmin: boolean) => Promise<void>;
}

export function PermissionButton({
  userId,
  isAdmin,
  isLoading,
  onPermissionChange,
}: PermissionButtonProps) {
  return (
    <Button
      variant="outline"
      className="w-full h-7"
      onClick={() => onPermissionChange(userId, !isAdmin)}
      disabled={isLoading}
    >
      {isLoading ? (
        <Loader2 className="h-4 w-4 animate-spin" />
      ) : isAdmin ? (
        'Убрать админку'
      ) : (
        'Назначить админом'
      )}
    </Button>
  );
}
