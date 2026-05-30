import type { Tag } from '../model/types';
import { getContrastColor } from '../lib/getContrastColor';

interface TagComponentProps {
  tag: Tag;
}

export const TagComponent = ({ tag }: TagComponentProps) => {
  return (
    <div
      style={{
        backgroundColor: tag.color ?? 'black',
        color: getContrastColor(tag.color ?? 'black'),
      }}
      className="border rounded-xl px-2 py-1 w-fit"
    >
      {tag.name}
    </div>
  );
};
