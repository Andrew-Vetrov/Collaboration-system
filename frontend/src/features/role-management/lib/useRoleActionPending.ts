import { useState } from 'react';

export function useRoleActionPending() {
  const [pendingRoleActionIds, setPendingRoleActionIds] = useState<
    Record<string, boolean>
  >({});

  const setPending = (roleId: string, isPending: boolean) => {
    setPendingRoleActionIds(prev => {
      if (isPending) {
        return {
          ...prev,
          [roleId]: true,
        };
      } else {
        const { [roleId]: _, ...rest } = prev;
        return rest;
      }
    });
  };

  const isPending = (roleId: string) => {
    return !!pendingRoleActionIds[roleId];
  };

  return {
    setPending,
    isPending,
  };
}
