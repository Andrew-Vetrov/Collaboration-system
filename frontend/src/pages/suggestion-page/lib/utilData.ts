export const maxCommentLength = 1000;

export const formOption = {
  required: 'Сообщение не может быть пустым',
  minLength: 1,
  maxLength: {
    value: maxCommentLength,
    message:
      'Длина комментария не может превышать ' + maxCommentLength + ' символов',
  },
};
