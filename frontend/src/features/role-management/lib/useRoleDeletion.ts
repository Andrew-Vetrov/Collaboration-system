import { useState } from 'react';
import { useDeleteUserRole } from '@/entities/role';

export function useRoleDeletion(projectId: string) {
  const [pendingRoleRemoval, setPendingRoleRemoval] = useState<
    Record<string, boolean>
  >({});
  const { mutateAsync: deleteUserRole } = useDeleteUserRole(projectId);

  const handleDeleteRole = async (roleId: string, userId: string) => {
    const pendingKey = `${userId}-${roleId}`;
    setPendingRoleRemoval(prev => ({
      ...prev,
      [pendingKey]: true,
    }));
    try {
      await deleteUserRole({
        roleId: roleId,
        userId: userId,
      });
    } finally {
      setPendingRoleRemoval(prev => {
        const { [pendingKey]: _, ...rest } = prev;
        return rest;
      });
    }
  };

  const isRoleRemovalPending = (userId: string, roleId: string) => {
    return !!pendingRoleRemoval[`${userId}-${roleId}`];
  };

  return {
    handleDeleteRole,
    isRoleRemovalPending,
  };
}
