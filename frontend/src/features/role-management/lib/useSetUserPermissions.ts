import { useState } from 'react';
import { useProjectSetUserPermissions } from '@/entities/project/api/useProjectSetUserPermissions';

export function useSetUserPermissions(projectId: string) {
  const [pendingPermissionUserIds, setPendingPermissionUserIds] = useState<
    Record<string, boolean>
  >({});
  const { mutateAsync: setUserPermissions } =
    useProjectSetUserPermissions(projectId);

  const handlePermissionChange = async (
    userId: string,
    setIsAdmin: boolean
  ) => {
    setPendingPermissionUserIds(prev => ({
      ...prev,
      [userId]: true,
    }));
    try {
      await setUserPermissions({
        userId: userId,
        setIsAdmin: setIsAdmin,
      });
    } finally {
      setPendingPermissionUserIds(prev => {
        const { [userId]: _, ...rest } = prev;
        return rest;
      });
    }
  };

  const isPermissionChangePending = (userId: string) => {
    return !!pendingPermissionUserIds[userId];
  };

  return {
    handlePermissionChange,
    isPermissionChangePending,
  };
}
