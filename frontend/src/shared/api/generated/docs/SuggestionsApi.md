# SuggestionsApi

All URIs are relative to *http://localhost*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**projectProjectIdSuggestionsGet**](#projectprojectidsuggestionsget) | **GET** /project/{project_id}/suggestions | Получить список предложений проекта|
|[**projectProjectIdSuggestionsPost**](#projectprojectidsuggestionspost) | **POST** /project/{project_id}/suggestions | Создать предложение или черновик|
|[**suggestionsSuggestionIdDelete**](#suggestionssuggestioniddelete) | **DELETE** /suggestions/{suggestion_id} | Удалить предложение или черновик|
|[**suggestionsSuggestionIdGet**](#suggestionssuggestionidget) | **GET** /suggestions/{suggestion_id} | Получить одно предложение|
|[**suggestionsSuggestionIdLikesDelete**](#suggestionssuggestionidlikesdelete) | **DELETE** /suggestions/{suggestion_id}/likes | Убрать конкретную реакцию|
|[**suggestionsSuggestionIdLikesPost**](#suggestionssuggestionidlikespost) | **POST** /suggestions/{suggestion_id}/likes | Добавить реакцию|
|[**suggestionsSuggestionIdPut**](#suggestionssuggestionidput) | **PUT** /suggestions/{suggestion_id} | Обновить предложение или черновик|

# **projectProjectIdSuggestionsGet**
> ProjectProjectIdSuggestionsGet200Response projectProjectIdSuggestionsGet()

Если указать query параметр status=draft, вернутся только черновики. 

### Example

```typescript
import {
    SuggestionsApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new SuggestionsApi(configuration);

let projectId: string; // (default to undefined)
let status: 'draft' | 'new' | 'discussion' | 'planned' | 'in_progress' | 'accepted' | 'rejected'; // (optional) (default to undefined)

const { status, data } = await apiInstance.projectProjectIdSuggestionsGet(
    projectId,
    status
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **projectId** | [**string**] |  | defaults to undefined|
| **status** | [**&#39;draft&#39; | &#39;new&#39; | &#39;discussion&#39; | &#39;planned&#39; | &#39;in_progress&#39; | &#39;accepted&#39; | &#39;rejected&#39;**]**Array<&#39;draft&#39; &#124; &#39;new&#39; &#124; &#39;discussion&#39; &#124; &#39;planned&#39; &#124; &#39;in_progress&#39; &#124; &#39;accepted&#39; &#124; &#39;rejected&#39;>** |  | (optional) defaults to undefined|


### Return type

**ProjectProjectIdSuggestionsGet200Response**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Список предложений |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **projectProjectIdSuggestionsPost**
> Suggestion projectProjectIdSuggestionsPost(projectProjectIdSuggestionsPostRequest)

Если status не указан или равен \"draft\", создаётся черновик. Если status=\"new\" и заполнены все поля, создаётся опубликованное предложение. 

### Example

```typescript
import {
    SuggestionsApi,
    Configuration,
    ProjectProjectIdSuggestionsPostRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new SuggestionsApi(configuration);

let projectId: string; // (default to undefined)
let projectProjectIdSuggestionsPostRequest: ProjectProjectIdSuggestionsPostRequest; //

const { status, data } = await apiInstance.projectProjectIdSuggestionsPost(
    projectId,
    projectProjectIdSuggestionsPostRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **projectProjectIdSuggestionsPostRequest** | **ProjectProjectIdSuggestionsPostRequest**|  | |
| **projectId** | [**string**] |  | defaults to undefined|


### Return type

**Suggestion**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**201** | Предложение или черновик создано |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **suggestionsSuggestionIdDelete**
> suggestionsSuggestionIdDelete()

Удаляет предложение или черновик по его id

### Example

```typescript
import {
    SuggestionsApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new SuggestionsApi(configuration);

let suggestionId: string; // (default to undefined)

const { status, data } = await apiInstance.suggestionsSuggestionIdDelete(
    suggestionId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **suggestionId** | [**string**] |  | defaults to undefined|


### Return type

void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**204** | Предложение или черновик успешно удалено |  -  |
|**404** | Предложение не найдено |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **suggestionsSuggestionIdGet**
> Suggestion suggestionsSuggestionIdGet()

Нужно для того, чтобы можно было зайти на страницу самого предложения для просмотра контента, связанного с ним

### Example

```typescript
import {
    SuggestionsApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new SuggestionsApi(configuration);

let suggestionId: string; // (default to undefined)

const { status, data } = await apiInstance.suggestionsSuggestionIdGet(
    suggestionId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **suggestionId** | [**string**] |  | defaults to undefined|


### Return type

**Suggestion**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Предложение найдено |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **suggestionsSuggestionIdLikesDelete**
> suggestionsSuggestionIdLikesDelete()


### Example

```typescript
import {
    SuggestionsApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new SuggestionsApi(configuration);

let suggestionId: string; // (default to undefined)

const { status, data } = await apiInstance.suggestionsSuggestionIdLikesDelete(
    suggestionId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **suggestionId** | [**string**] |  | defaults to undefined|


### Return type

void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**204** | Реакция удалена |  -  |
|**404** | Реакция не найдена |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **suggestionsSuggestionIdLikesPost**
> suggestionsSuggestionIdLikesPost()


### Example

```typescript
import {
    SuggestionsApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new SuggestionsApi(configuration);

let suggestionId: string; // (default to undefined)

const { status, data } = await apiInstance.suggestionsSuggestionIdLikesPost(
    suggestionId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **suggestionId** | [**string**] |  | defaults to undefined|


### Return type

void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**201** | Реакция добавлена |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **suggestionsSuggestionIdPut**
> Suggestion suggestionsSuggestionIdPut(suggestionsSuggestionIdPutRequest)

Частично обновляет поля предложения. Для черновиков можно менять status=draft или title/description. При смене status=draft→new черновик публикуется. 

### Example

```typescript
import {
    SuggestionsApi,
    Configuration,
    SuggestionsSuggestionIdPutRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new SuggestionsApi(configuration);

let suggestionId: string; // (default to undefined)
let suggestionsSuggestionIdPutRequest: SuggestionsSuggestionIdPutRequest; //

const { status, data } = await apiInstance.suggestionsSuggestionIdPut(
    suggestionId,
    suggestionsSuggestionIdPutRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **suggestionsSuggestionIdPutRequest** | **SuggestionsSuggestionIdPutRequest**|  | |
| **suggestionId** | [**string**] |  | defaults to undefined|


### Return type

**Suggestion**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Предложение обновлено |  -  |
|**404** | Предложение не найдено |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

