import { SuggestionEditingFields } from '@/features/suggestion-editing-fields/ui/SuggesstionEditingFields';
import { useSuggestionDelete } from '@/features/suggestion-delete/api/useSuggestionDelete';
import { Button } from '@/shared/ui';
import { useParams, Navigate } from 'react-router-dom';
import { useCreateSuggestionFeature } from '@/features/suggestion-create/model/useCreateSuggestionFeature';
import { DraftsList } from '@/entities/suggestion/ui/DraftsList';
import { useEditSuggestionFeature } from '@/pages/create-suggestion-page/model/useEditSuggestionFeature';
import { useSuggestionUpdate } from '@/features/suggestion-update/api/useSuggestionUpdate';

export default function CreateSuggestionPage() {
  const { projectId: id } = useParams<{ projectId: string }>();
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
    <main className="min-h-screen">
      <div className="max-w-5xl mx-auto flex flex-col sm:flex-row gap-8 p-4">
        <aside className="w-full bg-card sm:w-80 shrink-0  rounded-lg shadow p-4 flex flex-col gap-4">
          <h2 className="text-2xl font-bold text-center">Список черновиков</h2>

          <div className="flex-1 overflow-auto">
            <DraftsList clickSuggestion={setCurrentSuggestion} />
          </div>

          <Button onClick={handleCreate}>+ Создать черновик</Button>
        </aside>

        <section className="flex-1 bg-card rounded-lg shadow p-6">
          {currentSuggestion && editFeature ? (
            <SuggestionEditingFields
              register={editFeature.register}
              handleSubmit={editFeature.handleSubmit}
              control={editFeature.control}
              formState={editFeature.formState}
              handlePublish={editFeature.handlePublish}
              handleDelete={editFeature.handleDelete}
              handleBlur={editFeature.handleBlur}
            />
          ) : (
            <div className="h-full flex items-center justify-center text-gray-400">
              Выберите черновик или создайте новый
            </div>
          )}
        </section>
      </div>
    </main>
  );
}
