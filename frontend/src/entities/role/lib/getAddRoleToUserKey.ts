export const getAddRoleToUserKey = (projectId: string, userId: string) => {
  return ['projects', projectId, 'roles', userId];
};
