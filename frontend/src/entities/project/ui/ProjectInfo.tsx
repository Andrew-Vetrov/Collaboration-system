interface ProjectInfoProps {
  id: string;
}

export const ProjectInfo = ({id} : ProjectInfoProps) : JSX.Element => {
    return (
        <div>Информация о проекте {id}</div>
    )
}