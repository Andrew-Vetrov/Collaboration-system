import { useAddTagToSuggestion } from '@/entities/tag/api/useAddTagToSuggestion';
import { useDeleteSuggestionTag } from '@/entities/tag/api/useDeleteSuggestionTag';
import { useProjectTags } from '@/entities/tag/api/useProjectTags';
import type { Tag } from '@/entities/tag/model/types';
import { TagComponent } from '@/entities/tag/ui/TagComponent';
import {
  Button,
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/shared/ui';
import { Plus, X } from 'lucide-react';

interface SuggestionTagsManagerProps {
  projectId: string;
  suggestionId: string;
  selectedTags: Tag[];
  canEdit: boolean;
}

export const SuggestionTagsManager = ({
  canEdit,
  projectId,
  selectedTags,
  suggestionId,
}: SuggestionTagsManagerProps) => {
  const { data: projectTags = [] } = useProjectTags(projectId);
  const { mutate: addTag, isPending: isAddPending } = useAddTagToSuggestion(
    projectId,
    suggestionId
  );
  const { mutate: deleteTag, isPending: isDeletePending } =
    useDeleteSuggestionTag(projectId, suggestionId);

  const selectedTagIds = new Set(selectedTags.map(tag => tag.tag_id));
  const availableTags = projectTags.filter(
    tag => !selectedTagIds.has(tag.tag_id)
  );
  const isPending = isAddPending || isDeletePending;

  return (
    <div className="flex flex-col w-full items-start gap-3">
      <div className="flex items-center gap-2">
        <span className="text-xl">Теги:</span>
        {canEdit && availableTags.length > 0 && (
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button
                variant="outline"
                size="icon"
                className="size-8"
                disabled={isPending}
                type="button"
              >
                <Plus className="size-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="start">
              {availableTags.map(tag => (
                <DropdownMenuItem
                  key={tag.tag_id}
                  onClick={() => addTag({ tagId: tag.tag_id })}
                >
                  {tag.name}
                </DropdownMenuItem>
              ))}
            </DropdownMenuContent>
          </DropdownMenu>
        )}
      </div>
      <div className="flex w-full gap-2 flex-wrap">
        {selectedTags.length > 0 ? (
          selectedTags.map(tag => (
            <div key={tag.tag_id} className="flex items-center gap-1">
              <TagComponent tag={tag} />
              {canEdit && (
                <Button
                  variant="outline"
                  size="icon"
                  className="size-7"
                  disabled={isPending}
                  type="button"
                  onClick={() =>
                    deleteTag({
                      suggestionId,
                      tagId: tag.tag_id,
                    })
                  }
                >
                  <X className="size-4" />
                </Button>
              )}
            </div>
          ))
        ) : (
          <span className="text-sm text-muted-foreground">Тегов пока нет</span>
        )}
      </div>
    </div>
  );
};
