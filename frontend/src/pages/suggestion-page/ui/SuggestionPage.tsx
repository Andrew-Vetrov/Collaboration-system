import { useParams } from 'react-router-dom';
import { useSuggestion } from '@/entities/suggestion';

export const SuggestionPage = () => {
  const { id } = useParams<{ id: string }>();

  const { data: suggestion, isLoading, isError } = useSuggestion(id);

  if (isLoading) {
    return <div className="p-8 text-center">Загрузка...</div>;
  }

  if (isError || suggestion === undefined || suggestion === null) {
    return (
      <div className="p-8 text-center">
        <h1 className="text-2xl font-bold mb-2">Предложение не найдено</h1>
        <p className="text-gray-500">
          Такого предложения не существует или у вас нет к нему доступа.
        </p>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto p-8">
      <h1>{suggestion.name}</h1>
      <h1>{suggestion.description}</h1>
    </div>
  );
};
