import { Button } from '@/shared/ui';
import { Loader2 } from 'lucide-react';

interface DeleteRoleButtonProps {
  roleId: string;
  userId: string;
  isLoading: boolean;
  onDelete: (roleId: string, userId: string) => Promise<void>;
}

export function DeleteRoleButton({
  roleId,
  userId,
  isLoading,
  onDelete,
}: DeleteRoleButtonProps) {
  return (
    <Button
      variant="ghost"
      size="sm"
      disabled={isLoading}
      onClick={() => onDelete(roleId, userId)}
      className="h-6 w-6 p-0 hover:bg-destructive hover:text-destructive-foreground"
    >
      {isLoading ? (
        <Loader2 className="h-3 w-3 animate-spin" />
      ) : (
        <span className="text-lg">×</span>
      )}
    </Button>
  );
}
