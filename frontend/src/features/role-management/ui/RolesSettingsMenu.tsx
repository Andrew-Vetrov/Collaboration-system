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

export const RolesSettingsMenu = ({
  open,
  setIsOpen,
  projectId,
  roles,
}: RolesSettingsMenuProps) => {
  const { mutate: updateRole, isPending } = useUpdateRoleSettings(projectId);

  const [values, setValues] = useState<Record<string, number>>(() =>
    Object.fromEntries(roles.map(r => [r.role_id, r.likes_amount]))
  );

  const handleChange = (roleId: string, value: string) => {
    setValues(prev => ({
      ...prev,
      [roleId]: Number(value),
    }));
  };

  const handleSave = (roleId: string) => {
    updateRole({
      roleId,
      likesAmount: String(values[roleId]),
    });
  };

  return (
    <Dialog open={open} onOpenChange={setIsOpen}>
      <DialogContent>
        <DialogTitle>Настройки ролей</DialogTitle>

        <div className="flex flex-col gap-4">
          {roles.map(role => (
            <div key={role.role_id} className="flex flex-col gap-2">
              <Label>{role.name}</Label>

              <div className="ml-2 flex gap-2 justify-between">
                <Label className="flex-1">Количество голосов у роли:</Label>
                <div className='flex sm:flex-row flex-col flex-0 gap-2'>
                    <Input
                      type="number"
                      className="flex-0 min-w-30"
                      value={values[role.role_id] ?? 0}
                      onChange={e => handleChange(role.role_id, e.target.value)}
                    />
                    <Button
                      onClick={() => handleSave(role.role_id)}
                      disabled={isPending}
                    >
                      Сохранить
                    </Button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </DialogContent>
    </Dialog>
  );
};
