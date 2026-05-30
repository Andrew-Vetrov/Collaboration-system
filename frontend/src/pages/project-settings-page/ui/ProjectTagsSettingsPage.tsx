import { useProjectTags } from '@/entities/tag/api/useProjectTags';
import {
  ProjectTagSettingsItem,
  TagCreationDialog,
} from '@/features/tags-menagement';
import { Button } from '@/shared/ui';
import { Plus } from 'lucide-react';
import { useState } from 'react';

interface ProjectTagsSettingsPageProps {
  projectId: string;
}

export const ProjectTagsSettingsPage = ({
  projectId,
}: ProjectTagsSettingsPageProps) => {
  const [isTagCreationDialogOpen, setIsTagCreationDialogOpen] =
    useState<boolean>(false);

  const { data: projectTags, isLoading, isError } = useProjectTags(projectId);

  if (isLoading) {
    return <div>Загрузка тегов...</div>;
  }

  if (isError) {
    return <div>Ошибка загрузки тегов</div>;
  }
  return (
    <>
      <div className="flex flex-col gap-3 mt-2">
        <div className="flex justify-between">
          <div>Список тегов:</div>
          <Button
            variant="outline"
            size="icon"
            className="size-8 sm:size-10"
            onClick={() => setIsTagCreationDialogOpen(true)}
          >
            <Plus className="size-8 sm:size-10 " />
          </Button>
        </div>
        <div className="flex flex-col gap-2">
          {projectTags && projectTags.length > 0 ? (
            projectTags.map(tag => (
              <ProjectTagSettingsItem
                key={tag.tag_id}
                projectId={projectId}
                tag={tag}
              />
            ))
          ) : (
            <div className="text-sm text-muted-foreground">
              В проекте пока нет тегов
            </div>
          )}
        </div>
      </div>
      <TagCreationDialog
        open={isTagCreationDialogOpen}
        setIsOpen={setIsTagCreationDialogOpen}
        projectId={projectId}
      />
    </>
  );
};
