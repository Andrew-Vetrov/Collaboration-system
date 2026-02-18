import { useProjectInvites } from '@/entities/invites/api/useProjectInvites';
import { useReplyToInvite } from '@/entities/invites/api/useReplyToInvite';
import { useProjectDeleteUser } from '@/entities/project/api/useProjectDeleteUser';
import { Avatar, AvatarFallback, AvatarImage, Button } from '@/shared/ui';
import { Loader2, X } from 'lucide-react';
import { useState } from 'react';
import { Navigate } from 'react-router-dom';
import { InviteUserDialog } from './InviteUserDialog';

interface ProjectInvitesContentProps {
  projectId: string;
}

export function ProjectInvitesContent({
  projectId,
}: ProjectInvitesContentProps) {
  const {
    data: invites,
    isLoading: projectInvitesLoading,
    isError: projectInvitesError,
  } = useProjectInvites(projectId);

  const { mutate: deleteInvite, isPending } = useReplyToInvite();

  const [isDialogOpen, setIsDialogOpen] = useState<boolean>(false);

  if (projectInvitesLoading) {
    return (
      <div className="flex h-[50vh] items-center justify-center">
        <Loader2 className="h-10 w-10 animate-spin text-primary" />
      </div>
    );
  }

  if (projectInvitesError || !invites) {
    return <Navigate to="/not-found" replace />;
  }

  return (
    <div className="flex flex-col gap-4">
      <div className="flex flex-col sm:flex-row gap-4 sm:ml-4">
        <div className="text-xl sm:text-start text-center">
          Список приглашений
        </div>
        <Button
          variant="outline"
          onClick={() => {
            setIsDialogOpen(true);
          }}
        >
          Добавить пользователя
        </Button>
      </div>
      <InviteUserDialog
        isOpen={isDialogOpen}
        projectId={projectId}
        setIsOpen={setIsDialogOpen}
      />
      <ul className="flex flex-col gap-3">
        {invites.map(invite => (
          <li key={invite.invite_id}>
            <div className="flex flex-col sm:flex-row gap-2 rounded-lg border bg-card p-2">
              <div className="flex w-full gap-2">
                <div className="pt-2">
                  <Avatar className="h-9 w-9 min-w-9 shrink-0 ">
                    <AvatarImage src={invite.receiver_avatar} className="" />
                    <AvatarFallback>
                      {invite.receiver_nickname?.slice(0, 2).toUpperCase()}
                    </AvatarFallback>
                  </Avatar>
                </div>
                <div className="flex flex-col gap-1 w-full">
                  <div>{invite.email}</div>
                  <div>{invite.receiver_nickname}</div>
                </div>
              </div>
              <Button
                variant="ghost"
                className="self-center hover:shadow-lg transition-shadow duration-200"
                disabled={isPending}
                onClick={() => {
                  deleteInvite({
                    inviteId: invite.invite_id,
                    inviteResponse: false,
                    projectId: projectId,
                  });
                }}
              >
                <X />
              </Button>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}
