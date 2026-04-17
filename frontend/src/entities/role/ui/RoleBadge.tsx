import { Heart, Loader, X } from 'lucide-react';
import type { Role } from '../model/types';

interface RoleBadgeProps {
  role: Role;
  onRemove: (roleId: string) => void;
  onClick?: () => void;
  disable?: boolean;
}

export const RoleBadge = ({
  role,
  onRemove,
  onClick,
  disable,
}: RoleBadgeProps) => {
  const isDarkColor =
    role.color && parseInt(role.color.replace('#', ''), 16) < 0xffffff / 2;
  return (
    <div
      className={`border rounded-full px-3 py-1 text-sm font-medium flex gap-1 ${
        isDarkColor ? 'text-white' : 'text-black'
      } ${onClick && 'cursor-pointer'}`}
      style={{ backgroundColor: role.color }}
      onClick={onClick}
    >
      <span className="max-w-60 overflow-hidden ">{role.name}</span>
      <div className="flex items-center gap-0.5 text-xs font-medium">
        <Heart className="w-3.5 h-3.5" />
        <span>{role.likes_amount}</span>
      </div>
      {!disable ? (
        <X
          className="h-4 w-4 hover:cursor-pointer self-center"
          onClick={e => {
            e.stopPropagation();
            onRemove(role.role_id);
          }}
        />
      ) : (
        <Loader className="h-4 w-4 self-center" />
      )}
    </div>
  );
};
