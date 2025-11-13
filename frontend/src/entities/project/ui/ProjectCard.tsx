import type { ProjectBasic } from '../model/types';

interface ProjectCardProps {
  project: ProjectBasic;
}

export const ProjectCard = ({ project }: ProjectCardProps) => {
  return (
    <div>
      <h3>{project.name}</h3>
      <p>{project.description}</p>
    </div>
  );
};