import { ProjectList } from '@/entities/project';
import { Button } from '@/shared/ui';
import { Plus } from 'lucide-react';
import { CreateProjectDialog } from './create-project-dialog';
import React from 'react';

export const ProjectsPage = (): JSX.Element => {
  const [isOpen, setIsOpen] = React.useState(false);

  return (
    <div className="relative min-h-screen px-4 py-6 flex justify-center items-start">
      <div className="absolute left-4 top-6">
        <Button
          variant="outline"
          size="icon"
          className="size-10"
          onClick={() => setIsOpen(true)}
        >
          <Plus className="size-10 rounded-md " />
        </Button>
      </div>

      <ProjectList />

      <CreateProjectDialog isOpen={isOpen} setIsOpen={setIsOpen} />
    </div>
  );
};
