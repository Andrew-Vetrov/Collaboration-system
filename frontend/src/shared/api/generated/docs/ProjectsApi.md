# ProjectsApi

All URIs are relative to *http://localhost*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**projectsGet**](#projectsget) | **GET** /projects | Получить список своих проектов|
|[**projectsPost**](#projectspost) | **POST** /projects | Creating a project|
|[**projectsProjectIdSettingsGet**](#projectsprojectidsettingsget) | **GET** /projects/{project_id}/settings | Получить настройки проекта|
|[**projectsProjectIdSettingsPut**](#projectsprojectidsettingsput) | **PUT** /projects/{project_id}/settings | Обновить настройки проекта|
|[**projectsProjectIdUsersGet**](#projectsprojectidusersget) | **GET** /projects/{project_id}/users | Получить список пользователей проекта|
|[**projectsProjectIdUsersUserIdDelete**](#projectsprojectidusersuseriddelete) | **DELETE** /projects/{project_id}/users/{user_id} | Удалить пользователя из проекта|
|[**projectsProjectIdUsersUserIdPut**](#projectsprojectidusersuseridput) | **PUT** /projects/{project_id}/users/{user_id} | Изменить права пользователя в проекте|

# **projectsGet**
> ProjectsGet200Response projectsGet()


### Example

```typescript
import {
    ProjectsApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new ProjectsApi(configuration);

const { status, data } = await apiInstance.projectsGet();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**ProjectsGet200Response**

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Список проектов пользователя успешно получен |  -  |
|**401** | Не авторизован |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **projectsPost**
> ProjectsPost201Response projectsPost(projectsPostRequest)


### Example

```typescript
import {
    ProjectsApi,
    Configuration,
    ProjectsPostRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new ProjectsApi(configuration);

let projectsPostRequest: ProjectsPostRequest; //

const { status, data } = await apiInstance.projectsPost(
    projectsPostRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **projectsPostRequest** | **ProjectsPostRequest**|  | |


### Return type

**ProjectsPost201Response**

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**201** | Project is successfuly created |  -  |
|**400** | Wrong request |  -  |
|**401** | Не авторизован |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **projectsProjectIdSettingsGet**
> ProjectsProjectIdSettingsGet200Response projectsProjectIdSettingsGet()


### Example

```typescript
import {
    ProjectsApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new ProjectsApi(configuration);

let projectId: string; // (default to undefined)

const { status, data } = await apiInstance.projectsProjectIdSettingsGet(
    projectId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **projectId** | [**string**] |  | defaults to undefined|


### Return type

**ProjectsProjectIdSettingsGet200Response**

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Настройки проекта успешно получены |  -  |
|**400** | Wrong request |  -  |
|**401** | Не авторизован |  -  |
|**403** | Нет доступа к проекту |  -  |
|**404** | Проект не найден |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **projectsProjectIdSettingsPut**
> projectsProjectIdSettingsPut(projectsProjectIdSettingsPutRequest)


### Example

```typescript
import {
    ProjectsApi,
    Configuration,
    ProjectsProjectIdSettingsPutRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new ProjectsApi(configuration);

let projectId: string; // (default to undefined)
let projectsProjectIdSettingsPutRequest: ProjectsProjectIdSettingsPutRequest; //

const { status, data } = await apiInstance.projectsProjectIdSettingsPut(
    projectId,
    projectsProjectIdSettingsPutRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **projectsProjectIdSettingsPutRequest** | **ProjectsProjectIdSettingsPutRequest**|  | |
| **projectId** | [**string**] |  | defaults to undefined|


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
|**200** | Настройки проекта успешно обновлены |  -  |
|**400** | Wrong request |  -  |
|**401** | Не авторизован |  -  |
|**403** | Нет прав для редактирования проекта |  -  |
|**404** | Проект найден |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **projectsProjectIdUsersGet**
> ProjectsProjectIdUsersGet200Response projectsProjectIdUsersGet()


### Example

```typescript
import {
    ProjectsApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new ProjectsApi(configuration);

let projectId: string; // (default to undefined)

const { status, data } = await apiInstance.projectsProjectIdUsersGet(
    projectId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **projectId** | [**string**] |  | defaults to undefined|


### Return type

**ProjectsProjectIdUsersGet200Response**

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Список пользователей успешно получен |  -  |
|**401** | Не авторизован |  -  |
|**403** | Нет доступа к проекту |  -  |
|**404** | Проект не найден |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **projectsProjectIdUsersUserIdDelete**
> projectsProjectIdUsersUserIdDelete()


### Example

```typescript
import {
    ProjectsApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new ProjectsApi(configuration);

let projectId: string; // (default to undefined)
let userId: string; // (default to undefined)

const { status, data } = await apiInstance.projectsProjectIdUsersUserIdDelete(
    projectId,
    userId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **projectId** | [**string**] |  | defaults to undefined|
| **userId** | [**string**] |  | defaults to undefined|


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
|**200** | Пользователь успешно удален из проекта |  -  |
|**400** | Пользователь нет в проекте или неверные данные |  -  |
|**401** | Не авторизован |  -  |
|**403** | Недостаточно прав |  -  |
|**404** | Проект или пользователь не найден |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **projectsProjectIdUsersUserIdPut**
> projectsProjectIdUsersUserIdPut(projectsProjectIdUsersUserIdPutRequest)


### Example

```typescript
import {
    ProjectsApi,
    Configuration,
    ProjectsProjectIdUsersUserIdPutRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new ProjectsApi(configuration);

let projectId: string; // (default to undefined)
let userId: string; // (default to undefined)
let projectsProjectIdUsersUserIdPutRequest: ProjectsProjectIdUsersUserIdPutRequest; //

const { status, data } = await apiInstance.projectsProjectIdUsersUserIdPut(
    projectId,
    userId,
    projectsProjectIdUsersUserIdPutRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **projectsProjectIdUsersUserIdPutRequest** | **ProjectsProjectIdUsersUserIdPutRequest**|  | |
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
|**200** | Права пользователя успешно изменены |  -  |
|**400** | Пользователь нет в проекте или неверные данные |  -  |
|**401** | Не авторизован |  -  |
|**403** | Недостаточно прав для изменения прав |  -  |
|**404** | Проект или пользователь не найден |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

