import { ProjectInfo } from "@/features/project-info";
import { useParams, Navigate } from "react-router-dom"

export const ProjectPage = () : JSX.Element => {
    const {id} = useParams<{id: string}>();
    if (!id) {
        return <Navigate to="/not-found" replace />; // или <NotFoundPage />
    }

    return <ProjectInfo id={id} />;
}