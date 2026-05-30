import { useProjectTags } from '@/entities/tag/api/useProjectTags';
import {
  Button,
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/shared/ui';
import { useEffect, useState, type Dispatch, type SetStateAction } from 'react';

interface TagSortMenuProps {
  projectId: string;
  tagSort: string | undefined;
  setTagSort: Dispatch<SetStateAction<string | undefined>>;
}

export const TagSortMenu = ({
  projectId,
  setTagSort,
  tagSort,
}: TagSortMenuProps) => {
  const { data: projectTags } = useProjectTags(projectId);
  const [tagName, setTagName] = useState<string>('Без сортировки');

  useEffect(() => {
    const selectedTag = projectTags?.find(tag => tag.tag_id === tagSort);
    setTagName(selectedTag?.name ?? 'Без сортировки');
  }, [projectTags, tagSort]);

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="outline">{tagName}</Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent>
        <DropdownMenuGroup>
          <DropdownMenuItem
            onClick={() => {
              setTagName('Без сортировки');
              setTagSort(undefined);
            }}
          >
            Без сортировки
          </DropdownMenuItem>
        </DropdownMenuGroup>
        <DropdownMenuSeparator />
        <DropdownMenuGroup>
          {projectTags?.map(tag => (
            <DropdownMenuItem
              key={tag.tag_id}
              onClick={() => {
                setTagName(tag.name);
                setTagSort(tag.tag_id);
              }}
            >
              {tag.name}
            </DropdownMenuItem>
          ))}
        </DropdownMenuGroup>
      </DropdownMenuContent>
    </DropdownMenu>
  );
};
