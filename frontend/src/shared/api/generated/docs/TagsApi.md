# TagsApi

All URIs are relative to *http://localhost*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**projectsProjectIdTagsGet**](#projectsprojectidtagsget) | **GET** /projects/{project_id}/tags | Получить список тегов проекта|
|[**projectsProjectIdTagsPost**](#projectsprojectidtagspost) | **POST** /projects/{project_id}/tags | Создать новый тег|
|[**projectsProjectIdTagsTagIdDelete**](#projectsprojectidtagstagiddelete) | **DELETE** /projects/{project_id}/tags/{tag_id} | Удалить тег проекта|
|[**projectsProjectIdTagsTagIdPut**](#projectsprojectidtagstagidput) | **PUT** /projects/{project_id}/tags/{tag_id} | Обновить тег проекта|
|[**suggestionsSuggestionIdTagsPost**](#suggestionssuggestionidtagspost) | **POST** /suggestions/{suggestion_id}/tags | Добавить тег к предложению|
|[**suggestionsSuggestionIdTagsTagIdDelete**](#suggestionssuggestionidtagstagiddelete) | **DELETE** /suggestions/{suggestion_id}/tags/{tag_id} | Удалить тег у предложения|

# **projectsProjectIdTagsGet**
> ProjectsProjectIdTagsGet200Response projectsProjectIdTagsGet()


### Example

```typescript
import {
    TagsApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new TagsApi(configuration);

let projectId: string; // (default to undefined)

const { status, data } = await apiInstance.projectsProjectIdTagsGet(
    projectId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **projectId** | [**string**] |  | defaults to undefined|


### Return type

**ProjectsProjectIdTagsGet200Response**

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Список тегов успешно получен |  -  |
|**401** | Не авторизован |  -  |
|**403** | Нет доступа к проекту |  -  |
|**404** | Проект не найден |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **projectsProjectIdTagsPost**
> Tag projectsProjectIdTagsPost(projectsProjectIdTagsPostRequest)


### Example

```typescript
import {
    TagsApi,
    Configuration,
    ProjectsProjectIdTagsPostRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new TagsApi(configuration);

let projectId: string; // (default to undefined)
let projectsProjectIdTagsPostRequest: ProjectsProjectIdTagsPostRequest; //

const { status, data } = await apiInstance.projectsProjectIdTagsPost(
    projectId,
    projectsProjectIdTagsPostRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **projectsProjectIdTagsPostRequest** | **ProjectsProjectIdTagsPostRequest**|  | |
| **projectId** | [**string**] |  | defaults to undefined|


### Return type

**Tag**

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**201** | Тег успешно создан |  -  |
|**400** | Некорректные данные или тег уже существует |  -  |
|**401** | Не авторизован |  -  |
|**403** | Недостаточно прав |  -  |
|**404** | Проект не найден |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **projectsProjectIdTagsTagIdDelete**
> projectsProjectIdTagsTagIdDelete()


### Example

```typescript
import {
    TagsApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new TagsApi(configuration);

let projectId: string; // (default to undefined)
let tagId: string; // (default to undefined)

const { status, data } = await apiInstance.projectsProjectIdTagsTagIdDelete(
    projectId,
    tagId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **projectId** | [**string**] |  | defaults to undefined|
| **tagId** | [**string**] |  | defaults to undefined|


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
|**204** | Тег успешно удалён |  -  |
|**401** | Не авторизован |  -  |
|**403** | Недостаточно прав |  -  |
|**404** | Проект или тег не найдены |  -  |
|**409** | Тег нельзя удалить, пока он используется в предложениях |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **projectsProjectIdTagsTagIdPut**
> Tag projectsProjectIdTagsTagIdPut(projectsProjectIdTagsTagIdPutRequest)


### Example

```typescript
import {
    TagsApi,
    Configuration,
    ProjectsProjectIdTagsTagIdPutRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new TagsApi(configuration);

let projectId: string; // (default to undefined)
let tagId: string; // (default to undefined)
let projectsProjectIdTagsTagIdPutRequest: ProjectsProjectIdTagsTagIdPutRequest; //

const { status, data } = await apiInstance.projectsProjectIdTagsTagIdPut(
    projectId,
    tagId,
    projectsProjectIdTagsTagIdPutRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **projectsProjectIdTagsTagIdPutRequest** | **ProjectsProjectIdTagsTagIdPutRequest**|  | |
| **projectId** | [**string**] |  | defaults to undefined|
| **tagId** | [**string**] |  | defaults to undefined|


### Return type

**Tag**

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Тег успешно обновлён |  -  |
|**400** | Некорректные данные или тег с таким именем уже существует |  -  |
|**401** | Не авторизован |  -  |
|**403** | Недостаточно прав |  -  |
|**404** | Проект или тег не найдены |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **suggestionsSuggestionIdTagsPost**
> suggestionsSuggestionIdTagsPost(suggestionsSuggestionIdTagsPostRequest)


### Example

```typescript
import {
    TagsApi,
    Configuration,
    SuggestionsSuggestionIdTagsPostRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new TagsApi(configuration);

let suggestionId: string; // (default to undefined)
let suggestionsSuggestionIdTagsPostRequest: SuggestionsSuggestionIdTagsPostRequest; //

const { status, data } = await apiInstance.suggestionsSuggestionIdTagsPost(
    suggestionId,
    suggestionsSuggestionIdTagsPostRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **suggestionsSuggestionIdTagsPostRequest** | **SuggestionsSuggestionIdTagsPostRequest**|  | |
| **suggestionId** | [**string**] |  | defaults to undefined|


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
|**200** | Тег успешно добавлен |  -  |
|**401** | Не авторизован |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **suggestionsSuggestionIdTagsTagIdDelete**
> suggestionsSuggestionIdTagsTagIdDelete()


### Example

```typescript
import {
    TagsApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new TagsApi(configuration);

let suggestionId: string; // (default to undefined)
let tagId: string; // (default to undefined)

const { status, data } = await apiInstance.suggestionsSuggestionIdTagsTagIdDelete(
    suggestionId,
    tagId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **suggestionId** | [**string**] |  | defaults to undefined|
| **tagId** | [**string**] |  | defaults to undefined|


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
|**204** | Тег удалён |  -  |
|**401** | Не авторизован |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

