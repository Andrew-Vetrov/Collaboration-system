import { SuggestionEditingFields } from '@/entities/suggestion/ui/SuggesstionEditingFields';
import { useSuggestionUpdate } from '@/entities/suggestion/api/useSuggestionUpdate';
import { useSuggestionDelete } from '@/entities/suggestion/api/useSuggestionDelete';
import { Button } from '@/shared/ui';
import { useParams, Navigate } from 'react-router-dom';
import { useCreateSuggestionFeature } from '@/entities/suggestion/model/useCreateSuggestionFeature';
import { DraftsList } from '@/entities/suggestion/ui/DraftsList';
import { useEditSuggestionFeature } from '@/entities/suggestion/model/useEditSuggestionFeature';

export function CreateSuggestionPage() {
  const { id } = useParams<{ id: string }>();
  if (!id) {
    return <Navigate to="/not-found" replace />;
  }

  const { handleCreate, currentSuggestion, setCurrentSuggestion } =
    useCreateSuggestionFeature(id);

  const { mutate: handleSuggestionUpdate } = useSuggestionUpdate(
    id,
    currentSuggestion?.suggestion_id ?? ''
  );

  const { mutate: handleSuggestionDelete } = useSuggestionDelete(
    id,
    currentSuggestion?.suggestion_id ?? ''
  );

  const editFeature = useEditSuggestionFeature({
    suggestion: currentSuggestion,
    handleSuggestionUpdate,
    handleSuggestionDelete,
    onClose: setCurrentSuggestion,
  });

  return (
    <main className="flex">
      <div className="flex flex-col">
        <div className="font-bold">Список черновиков</div>
        <DraftsList clickSuggestion={setCurrentSuggestion} />
        <Button onClick={handleCreate}>Создать черновик</Button>
      </div>

      {currentSuggestion && editFeature && (
        <SuggestionEditingFields
          register={editFeature.register}
          handleSubmit={editFeature.handleSubmit}
          control={editFeature.control}
          formState={editFeature.formState}
          handlePublish={editFeature.handlePublish}
          handleDelete={editFeature.handleDelete}
          handleBlur={editFeature.handleBlur}
        />
      )}
    </main>
  );
}
