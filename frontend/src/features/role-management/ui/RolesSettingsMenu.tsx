import { useState } from 'react';
import { useUpdateRoleSettings } from '@/entities/role';
import {
  Button,
  Dialog,
  DialogContent,
  DialogTitle,
  Input,
  Label,
} from '@/shared/ui';
import type { Role } from '@/entities/role';
import type { Dispatch, SetStateAction } from 'react';

interface RolesSettingsMenuProps {
  open: boolean;
  setIsOpen: Dispatch<SetStateAction<boolean>>;
  projectId: string;
  roles: Role[];
}

type RoleSettingsState = {
  likesAmount: number;
  name: string;
  color: string;
};

export const RolesSettingsMenu = ({
  open,
  setIsOpen,
  projectId,
  roles,
}: RolesSettingsMenuProps) => {
  const { mutate: updateRole, isPending } = useUpdateRoleSettings(projectId);

  const [roleSettings, setRoleSettings] = useState<
    Record<string, RoleSettingsState>
  >(() =>
    Object.fromEntries(
      roles.map(role => [
        role.role_id,
        {
          likesAmount: role.likes_amount,
          name: role.name,
          color: role.color ?? '#000000',
        },
      ])
    )
  );

  const handleChange = (
    roleId: string,
    field: keyof RoleSettingsState,
    value: string
  ) => {
    setRoleSettings(prev => ({
      ...prev,
      [roleId]: {
        ...prev[roleId],
        [field]: field === 'likesAmount' ? Number(value) : value,
      },
    }));
  };

  const handleSave = (roleId: string) => {
    const role = roleSettings[roleId];

    updateRole({
      roleId,
      likesAmount: String(role.likesAmount),
      name: role.name,
      color: role.color,
    });
  };

  return (
    <Dialog open={open} onOpenChange={setIsOpen}>
      <DialogContent>
        <DialogTitle>Настройки ролей</DialogTitle>

        <div className="flex flex-col gap-6 max-h-[80vh] overflow-y-scroll">
          {roles.map(role => (
            <div
              key={role.role_id}
              className="flex flex-col gap-3 border rounded-lg p-3"
            >
              <Label className="font-bold">{role.name}</Label>

              <div className="flex flex-col gap-2">
                <Label className="text-sm">Название роли</Label>

                <Input
                  value={roleSettings[role.role_id]?.name ?? ''}
                  onChange={e =>
                    handleChange(role.role_id, 'name', e.target.value)
                  }
                />
              </div>

              <div className="flex flex-col gap-2">
                <Label className="text-sm">Цвет роли</Label>

                <Input
                  type="color"
                  className="h-10 w-20 p-1"
                  value={roleSettings[role.role_id]?.color ?? '#000000'}
                  onChange={e =>
                    handleChange(role.role_id, 'color', e.target.value)
                  }
                />
              </div>

              <div className="flex flex-col gap-2">
                <Label className="text-sm">Количество голосов у роли</Label>

                <Input
                  type="number"
                  value={roleSettings[role.role_id]?.likesAmount ?? 0}
                  onChange={e =>
                    handleChange(role.role_id, 'likesAmount', e.target.value)
                  }
                />
              </div>

              <Button
                onClick={() => handleSave(role.role_id)}
                disabled={isPending}
              >
                Сохранить
              </Button>
            </div>
          ))}
        </div>
      </DialogContent>
    </Dialog>
  );
};
