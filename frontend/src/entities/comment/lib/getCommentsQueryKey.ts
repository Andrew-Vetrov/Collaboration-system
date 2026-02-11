export const getCommentsQueryKey = (suggestionId: string) => {
  return ['suggestion', suggestionId, 'comments'];
};
