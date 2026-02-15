import { ProjectList } from '@/entities/project';
import { Plus } from 'lucide-react';
import { CreateProjectDialog } from './CreateProjectDialog';
import React, { type JSX } from 'react';
import { Button } from '@/shared/ui';

const ProjectsPage = (): JSX.Element => {
  const [isOpen, setIsOpen] = React.useState(false);

  return (
    <main className="relative min-h-screen flex flex-col">
      <div className="flex-1 flex items-start justify-center py-4">
        <div className="w-full max-w-5xl flex items-start flex-wrap sm:flex-nowrap justify-between gap-x-8 px-4 sm:px-4">
          <div className="shrink-0">
            <Button
              variant="outline"
              size="icon"
              className="size-8 sm:size-10"
              onClick={() => setIsOpen(true)}
            >
              <Plus className="size-8 sm:size-10 rounded-md " />
            </Button>
          </div>

          <ProjectList />
          <div className="shrink-0 w-12" />
        </div>
      </div>
      <CreateProjectDialog isOpen={isOpen} setIsOpen={setIsOpen} />
    </main>
  );
};

export default ProjectsPage;
