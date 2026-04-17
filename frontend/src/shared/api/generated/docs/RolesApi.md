# RolesApi

All URIs are relative to *http://localhost*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**projectsProjectIdRolesGet**](#projectsprojectidrolesget) | **GET** /projects/{project_id}/roles | Получить список всех ролей проекта|
|[**projectsProjectIdRolesPost**](#projectsprojectidrolespost) | **POST** /projects/{project_id}/roles | Создание новой роли в проекте|
|[**projectsProjectIdRolesRoleIdDelete**](#projectsprojectidrolesroleiddelete) | **DELETE** /projects/{project_id}/roles/{role_id} | Удаление существующей роли|
|[**projectsProjectIdRolesRoleIdLikesPut**](#projectsprojectidrolesroleidlikesput) | **PUT** /projects/{project_id}/roles/{role_id}/likes | Задать количество лайков для роли|
|[**projectsProjectIdUsersUserIdRolesPost**](#projectsprojectidusersuseridrolespost) | **POST** /projects/{project_id}/users/{user_id}/roles | Добавить роль пользователю|
|[**projectsProjectIdUsersUserIdRolesRoleIdDelete**](#projectsprojectidusersuseridrolesroleiddelete) | **DELETE** /projects/{project_id}/users/{user_id}/roles/{role_id} | Удалить роль у пользователя|

# **projectsProjectIdRolesGet**
> ProjectsProjectIdRolesGet200Response projectsProjectIdRolesGet()


### Example

```typescript
import {
    RolesApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new RolesApi(configuration);

let projectId: string; // (default to undefined)

const { status, data } = await apiInstance.projectsProjectIdRolesGet(
    projectId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **projectId** | [**string**] |  | defaults to undefined|


### Return type

**ProjectsProjectIdRolesGet200Response**

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Список ролей успешно получен |  -  |
|**401** | Не авторизован |  -  |
|**403** | Нет доступа к проекту |  -  |
|**404** | Проект не найден |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **projectsProjectIdRolesPost**
> Role projectsProjectIdRolesPost(projectsProjectIdRolesPostRequest)


### Example

```typescript
import {
    RolesApi,
    Configuration,
    ProjectsProjectIdRolesPostRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new RolesApi(configuration);

let projectId: string; // (default to undefined)
let projectsProjectIdRolesPostRequest: ProjectsProjectIdRolesPostRequest; //

const { status, data } = await apiInstance.projectsProjectIdRolesPost(
    projectId,
    projectsProjectIdRolesPostRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **projectsProjectIdRolesPostRequest** | **ProjectsProjectIdRolesPostRequest**|  | |
| **projectId** | [**string**] |  | defaults to undefined|


### Return type

**Role**

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**201** | Роль успешно создана |  -  |
|**400** | Некорректные данные (например, роль с таким именем уже существует) |  -  |
|**401** | Не авторизован |  -  |
|**403** | Недостаточно прав (только админ проекта) |  -  |
|**404** | Проект не найден |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **projectsProjectIdRolesRoleIdDelete**
> projectsProjectIdRolesRoleIdDelete()


### Example

```typescript
import {
    RolesApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new RolesApi(configuration);

let projectId: string; // (default to undefined)
let roleId: string; // (default to undefined)

const { status, data } = await apiInstance.projectsProjectIdRolesRoleIdDelete(
    projectId,
    roleId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **projectId** | [**string**] |  | defaults to undefined|
| **roleId** | [**string**] |  | defaults to undefined|


### Return type

void (empty response body)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**204** | Роль успешно удалена |  -  |
|**401** | Не авторизован |  -  |
|**403** | Недостаточно прав (только админ) |  -  |
|**404** | Роль или проект не найдены |  -  |
|**409** | Нельзя удалить роль, если она присвоена пользователям |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **projectsProjectIdRolesRoleIdLikesPut**
> Role projectsProjectIdRolesRoleIdLikesPut(projectsProjectIdRolesRoleIdLikesPutRequest)


### Example

```typescript
import {
    RolesApi,
    Configuration,
    ProjectsProjectIdRolesRoleIdLikesPutRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new RolesApi(configuration);

let projectId: string; // (default to undefined)
let roleId: string; // (default to undefined)
let projectsProjectIdRolesRoleIdLikesPutRequest: ProjectsProjectIdRolesRoleIdLikesPutRequest; //

const { status, data } = await apiInstance.projectsProjectIdRolesRoleIdLikesPut(
    projectId,
    roleId,
    projectsProjectIdRolesRoleIdLikesPutRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **projectsProjectIdRolesRoleIdLikesPutRequest** | **ProjectsProjectIdRolesRoleIdLikesPutRequest**|  | |
| **projectId** | [**string**] |  | defaults to undefined|
| **roleId** | [**string**] |  | defaults to undefined|


### Return type

**Role**

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Количество лайков успешно обновлено |  -  |
|**400** | Некорректное значение likes_amount |  -  |
|**401** | Не авторизован |  -  |
|**403** | Недостаточно прав |  -  |
|**404** | Роль или проект не найдены |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **projectsProjectIdUsersUserIdRolesPost**
> projectsProjectIdUsersUserIdRolesPost(projectsProjectIdUsersUserIdRolesPostRequest)


### Example

```typescript
import {
    RolesApi,
    Configuration,
    ProjectsProjectIdUsersUserIdRolesPostRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new RolesApi(configuration);

let projectId: string; // (default to undefined)
let userId: string; // (default to undefined)
let projectsProjectIdUsersUserIdRolesPostRequest: ProjectsProjectIdUsersUserIdRolesPostRequest; //

const { status, data } = await apiInstance.projectsProjectIdUsersUserIdRolesPost(
    projectId,
    userId,
    projectsProjectIdUsersUserIdRolesPostRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **projectsProjectIdUsersUserIdRolesPostRequest** | **ProjectsProjectIdUsersUserIdRolesPostRequest**|  | |
| **projectId** | [**string**] |  | defaults to undefined|
| **userId** | [**string**] |  | defaults to undefined|


### Return type

void (empty response body)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Роль успешно добавлена пользователю |  -  |
|**400** | Пользователь не в проекте или роль уже присвоена |  -  |
|**401** | Не авторизован |  -  |
|**403** | Недостаточно прав |  -  |
|**404** | Проект, пользователь или роль не найдены |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **projectsProjectIdUsersUserIdRolesRoleIdDelete**
> projectsProjectIdUsersUserIdRolesRoleIdDelete()


### Example

```typescript
import {
    RolesApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new RolesApi(configuration);

let projectId: string; // (default to undefined)
let userId: string; // (default to undefined)
let roleId: string; // (default to undefined)

const { status, data } = await apiInstance.projectsProjectIdUsersUserIdRolesRoleIdDelete(
    projectId,
    userId,
    roleId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **projectId** | [**string**] |  | defaults to undefined|
| **userId** | [**string**] |  | defaults to undefined|
| **roleId** | [**string**] |  | defaults to undefined|


### Return type

void (empty response body)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**204** | Роль успешно удалена у пользователя |  -  |
|**400** | У пользователя нет этой роли |  -  |
|**401** | Не авторизован |  -  |
|**403** | Недостаточно прав |  -  |
|**404** | Проект, пользователь или роль не найдены |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

