import type { ProjectUser } from '@/entities/project/model/types';
import {
  RoleBadge,
  useAddRoleToUser,
  useDeleteProjectRole,
  useProjetctRoles,
} from '@/entities/role';
import {
  Button,
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuTrigger,
} from '@/shared/ui';
import { Checkbox } from '@/shared/ui/checkbox';
import { useState } from 'react';
import { RoleCreationDialog } from './RoleCreationDialog';
import { Settings2 } from 'lucide-react';
import { RolesSettingsMenu } from './RolesSettingsMenu';

interface RoleDropdownMenuProps {
  projectId: string;
  user: ProjectUser;
}

export const RoleDropdownMenu = ({
  user,
  projectId,
}: RoleDropdownMenuProps) => {
  const [isAllRolesCheckBoxOn, setIsAllRolesCheckBoxOn] =
    useState<boolean>(false);
  const [isRoleCreationDialogOpen, setIsRoleCreationDialogOpen] =
    useState<boolean>(false);
  const [isRoleSettingsDialogOpen, setIsRoleSettingsDialogOpen] =
    useState<boolean>(false);
  const [pendingRoleActionIds, setPendingRoleActionIds] = useState<
    Record<string, boolean>
  >({});

  const { data: projectRoles } = useProjetctRoles(projectId);
  const { mutateAsync: addRoleToUser } = useAddRoleToUser(
    projectId,
    user.user_id
  );
  const { mutateAsync: deleteProjectRole } = useDeleteProjectRole(projectId);

  const userRolesIds: string[] = user.roles?.map(role => role.role_id) ?? [];
  return (
    <>
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button variant="outline" className="w-full h-7">
            Добавить роль
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent className="flex flex-col sm:max-w-80 max-w-70 max-h-80 min-w-[100px] gap-3 p-3">
          <div className='flex justify-between gap-1'>
            <Button className='flex-1' onClick={() => setIsRoleCreationDialogOpen(true)}>
              Создать роль
            </Button>
            <Button onClick={() => setIsRoleSettingsDialogOpen(true)}>
              <Settings2 />
            </Button>
          </div>
          <div className="w-full flex gap-2">
            <Checkbox
              className="h-6 w-6 cursor-pointer"
              checked={isAllRolesCheckBoxOn}
              onCheckedChange={() => {
                setIsAllRolesCheckBoxOn(prev => !prev);
              }}
            />
            <div>Показать все роли</div>
          </div>
          <div className="flex flex-col gap-2 items-start">
            {projectRoles?.map((role, index) => {
              const includeInUserRoles = userRolesIds.includes(role.role_id);
              const shouldHide = !isAllRolesCheckBoxOn && includeInUserRoles;
              const pendingKey = role.role_id;
              return (
                <div
                  key={`${projectId}-${role.role_id}-${index}-${user.user_id}`}
                  className={`transition-all overflow-hidden ${shouldHide ? 'p-0 m-0 max-h-0 opacity-0' : 'max-h-20 opacity-100 '}`}
                >
                  <RoleBadge
                    role={role}
                    onRemove={async roleId => {
                      setPendingRoleActionIds(prev => ({
                        ...prev,
                        [pendingKey]: true,
                      }));
                      try {
                        await deleteProjectRole({ roleId: roleId });
                      } finally {
                        setPendingRoleActionIds(prev => {
                          const { [pendingKey]: _, ...rest } = prev;
                          return rest;
                        });
                      }
                    }}
                    onClick={
                      !includeInUserRoles
                        ? async () => {
                            setPendingRoleActionIds(prev => ({
                              ...prev,
                              [pendingKey]: true,
                            }));
                            try {
                              await addRoleToUser(role.role_id);
                            } finally {
                              setPendingRoleActionIds(prev => {
                                const { [pendingKey]: _, ...rest } = prev;
                                return rest;
                              });
                            }
                          }
                        : undefined
                    }
                    disable={!!pendingRoleActionIds[pendingKey]}
                  />
                </div>
              );
            })}
          </div>
        </DropdownMenuContent>
      </DropdownMenu>

      {isRoleCreationDialogOpen && (
        <RoleCreationDialog
          projectId={projectId}
          open={isRoleCreationDialogOpen}
          setIsOpen={setIsRoleCreationDialogOpen}
        />
      )}
      {isRoleSettingsDialogOpen && (
        <RolesSettingsMenu
          open={isRoleSettingsDialogOpen}
          setIsOpen={setIsRoleSettingsDialogOpen}
          projectId={projectId}
          roles={projectRoles ?? []}
        />
      )}
    </>
  );
};
