import { RoleBadge } from '@/entities/role';
import type { Role } from '@/entities/role/model/types';

interface RoleMenuBadgeProps {
  role: Role;
  isUserHasRole: boolean;
  isPending: boolean;
  onAddRole: (roleId: string) => Promise<void>;
  onDeleteRole: (roleId: string) => Promise<void>;
  shouldHide: boolean;
}

export function RoleMenuBadge({
  role,
  isUserHasRole,
  isPending,
  onAddRole,
  onDeleteRole,
  shouldHide,
}: RoleMenuBadgeProps) {
  return (
    <div
      className={`transition-all overflow-hidden ${shouldHide ? 'p-0 m-0 max-h-0 opacity-0' : 'max-h-20 opacity-100 '}`}
    >
      <RoleBadge
        role={role}
        onRemove={roleId => onDeleteRole(roleId)}
        onClick={!isUserHasRole ? () => onAddRole(role.role_id) : undefined}
        disable={isPending}
      />
    </div>
  );
}
