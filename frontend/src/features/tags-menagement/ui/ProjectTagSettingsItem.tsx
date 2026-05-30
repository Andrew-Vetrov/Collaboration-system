import { useDeleteProjectTag } from '@/entities/tag/api/useDeleteProjectTag';
import type { Tag } from '@/entities/tag/model/types';
import { TagComponent } from '@/entities/tag/ui/TagComponent';
import { Button } from '@/shared/ui';
import { Pencil, Trash2 } from 'lucide-react';
import { useState } from 'react';
import { TagEditDialog } from './TagEditDialog';

interface ProjectTagSettingsItemProps {
  projectId: string;
  tag: Tag;
}

export const ProjectTagSettingsItem = ({
  projectId,
  tag,
}: ProjectTagSettingsItemProps) => {
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false);
  const {
    mutate: deleteTag,
    isPending: isDeletePending,
    isError: isDeleteError,
  } = useDeleteProjectTag(projectId);

  return (
    <div className="flex flex-col gap-2 rounded-md border p-3">
      <div className="flex items-center justify-between gap-3">
        <TagComponent tag={tag} />
        <div className="flex shrink-0 gap-2">
          <Button
            variant="outline"
            size="icon"
            className="size-8"
            type="button"
            onClick={() => setIsEditDialogOpen(true)}
          >
            <Pencil className="size-4" />
          </Button>
          <Button
            variant="outline"
            size="icon"
            className="size-8"
            type="button"
            disabled={isDeletePending}
            onClick={() => deleteTag({ tagId: tag.tag_id })}
          >
            <Trash2 className="size-4" />
          </Button>
        </div>
      </div>
      {isDeleteError && (
        <p className="text-sm text-red-500">
          Не удалось удалить тег. Возможно, он используется в предложениях.
        </p>
      )}
      <TagEditDialog
        open={isEditDialogOpen}
        setIsOpen={setIsEditDialogOpen}
        projectId={projectId}
        tag={tag}
      />
    </div>
  );
};
