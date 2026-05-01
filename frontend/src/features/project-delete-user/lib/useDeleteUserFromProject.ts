import { useState } from 'react';
import { useProjectDeleteUser } from '@/entities/project/api/useProjectDeleteUser';

export function useDeleteUserFromProject(projectId: string) {
  const [pendingDeleteUserIds, setPendingDeleteUserIds] = useState<
    Record<string, boolean>
  >({});
  const { mutateAsync: deleteUser } = useProjectDeleteUser(projectId);

  const handleDeleteUser = async (userId: string) => {
    setPendingDeleteUserIds(prev => ({
      ...prev,
      [userId]: true,
    }));
    try {
      await deleteUser({
        userId: userId,
        isCurrentUser: false,
      });
    } finally {
      setPendingDeleteUserIds(prev => {
        const { [userId]: _, ...rest } = prev;
        return rest;
      });
    }
  };

  const isDeleteUserPending = (userId: string) => {
    return !!pendingDeleteUserIds[userId];
  };

  return {
    handleDeleteUser,
    isDeleteUserPending,
  };
}
