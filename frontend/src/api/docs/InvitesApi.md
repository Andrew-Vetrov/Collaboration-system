# InvitesApi

All URIs are relative to *http://localhost*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**invitesGet**](#invitesget) | **GET** /invites | Получить список приглашений пользователя (себя)|
|[**invitesInviteIdDelete**](#invitesinviteiddelete) | **DELETE** /invites/{invite_id} | Принять или отклонить приглашение|
|[**projectsProjectIdInvitesGet**](#projectsprojectidinvitesget) | **GET** /projects/{project_id}/invites | Получить все приглашения в этом проекте|
|[**projectsProjectIdInvitesPost**](#projectsprojectidinvitespost) | **POST** /projects/{project_id}/invites | Пригласить пользователя в проект|

# **invitesGet**
> ProjectsProjectIdInvitesGet200Response invitesGet()


### Example

```typescript
import {
    InvitesApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new InvitesApi(configuration);

const { status, data } = await apiInstance.invitesGet();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**ProjectsProjectIdInvitesGet200Response**

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Приглашения получены |  -  |
|**401** | Не авторизован |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **invitesInviteIdDelete**
> invitesInviteIdDelete()


### Example

```typescript
import {
    InvitesApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new InvitesApi(configuration);

let inviteId: string; // (default to undefined)
let accept: boolean; //Прниять или отклонить запрос (default to undefined)

const { status, data } = await apiInstance.invitesInviteIdDelete(
    inviteId,
    accept
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **inviteId** | [**string**] |  | defaults to undefined|
| **accept** | [**boolean**] | Прниять или отклонить запрос | defaults to undefined|


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
|**200** | Successful |  -  |
|**400** | Неверные данные |  -  |
|**401** | Не авторизован |  -  |
|**403** | Недостаточно прав для изменения приглашения |  -  |
|**404** | Приглашение не найдено |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **projectsProjectIdInvitesGet**
> ProjectsProjectIdInvitesGet200Response projectsProjectIdInvitesGet()


### Example

```typescript
import {
    InvitesApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new InvitesApi(configuration);

let projectId: string; // (default to undefined)

const { status, data } = await apiInstance.projectsProjectIdInvitesGet(
    projectId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **projectId** | [**string**] |  | defaults to undefined|


### Return type

**ProjectsProjectIdInvitesGet200Response**

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Приглашения получены |  -  |
|**400** | Неверные данные |  -  |
|**401** | Не авторизован |  -  |
|**403** | Недостаточно прав для просмотра приглашений |  -  |
|**404** | Проект не найден |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **projectsProjectIdInvitesPost**
> projectsProjectIdInvitesPost()


### Example

```typescript
import {
    InvitesApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new InvitesApi(configuration);

let projectId: string; // (default to undefined)
let userEmail: string; // (default to undefined)

const { status, data } = await apiInstance.projectsProjectIdInvitesPost(
    projectId,
    userEmail
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **projectId** | [**string**] |  | defaults to undefined|
| **userEmail** | [**string**] |  | defaults to undefined|


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
|**201** | Приглашение отправлено |  -  |
|**400** | Пользователь уже в проекте, или приглашение уже было отправлено, или неверные данные |  -  |
|**401** | Не авторизован |  -  |
|**403** | Недостаточно прав для приглашения пользователя |  -  |
|**404** | Проект или пользователь не найден |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

