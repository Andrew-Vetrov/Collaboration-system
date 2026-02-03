import { useParams } from 'react-router-dom';
import { useSuggestion } from '@/entities/suggestion';
import { Button, Card, CardContent, Textarea } from '@/shared/ui';
import { STATUS_LABELS } from '@/entities/suggestion/lib/status';
import { ThumbsUp, ThumbsDown } from 'lucide-react';

const SuggestionPage = () => {
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
    <main className="relative min-h-screen flex flex-col">
      <div className="flex-1 flex items-start justify-center py-4">
        <div className="w-full max-w-5xl flex items-start justify-between gap-8 px-4">
          <div className="shrink-0">
            <Button variant="outline">Редактировать</Button>
          </div>

          <div className="flex-1 flex flex-col items-center text-center">
            <h1 className="text-4xl font-bold mb-6">{suggestion.name}</h1>
            <Card className="w-full max-w-3xl p-8 bg-card shadow-lg border">
              <CardContent className="p-0">
                <p className="text-lg whitespace-pre-line leading-relaxed text-left">
                  {suggestion.description}
                </p>
              </CardContent>
            </Card>
            <div className="mt-8 w-full mx-auto flex place-items-center gap-4">
              <Textarea
                className="resize-none max-h-40 overflow-auto"
                placeholder="Оставьте комментарий..."
              ></Textarea>
              <Button variant="outline">Отправить</Button>
            </div>
          </div>

          <div className="shrink-0 flex flex-col items-center gap-4">
            <div className="px-4 py-2  rounded-full text-sm font-medium">
              Статус: {STATUS_LABELS[suggestion.status]}
            </div>
            <div>Всего лайков: {suggestion.likes_amount}</div>
            <div>Ваших лайков: {/** */}</div>
            <div className="relative flex gap-2 items-center">
              <Button variant="outline">
                <ThumbsUp className="w-4 h-4" />
              </Button>
              <Button variant="outline">
                <ThumbsDown className="w-4 h-4" />
              </Button>
            </div>
          </div>
        </div>
      </div>
    </main>
  );
};

export default SuggestionPage;
