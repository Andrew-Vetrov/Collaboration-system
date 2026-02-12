# CommentsApi

All URIs are relative to *http://localhost*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**commentIdReplyPost**](#commentidreplypost) | **POST** /{comment_id}/reply | Ответить на комментарий|
|[**commentsCommentIdDelete**](#commentscommentiddelete) | **DELETE** /comments/{comment_id} | Удалить комментарий|
|[**suggestionsSuggestionIdCommentsGet**](#suggestionssuggestionidcommentsget) | **GET** /suggestions/{suggestion_id}/comments | Получить список комментариев к предложению|
|[**suggestionsSuggestionIdCommentsPost**](#suggestionssuggestionidcommentspost) | **POST** /suggestions/{suggestion_id}/comments | Оставить комментарий к предложению|

# **commentIdReplyPost**
> Comment commentIdReplyPost(commentIdReplyPostRequest)


### Example

```typescript
import {
    CommentsApi,
    Configuration,
    CommentIdReplyPostRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new CommentsApi(configuration);

let commentId: string; // (default to undefined)
let commentIdReplyPostRequest: CommentIdReplyPostRequest; //

const { status, data } = await apiInstance.commentIdReplyPost(
    commentId,
    commentIdReplyPostRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **commentIdReplyPostRequest** | **CommentIdReplyPostRequest**|  | |
| **commentId** | [**string**] |  | defaults to undefined|


### Return type

**Comment**

### Authorization

[sessionAuth](../README.md#sessionAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**201** | Ответ создан |  -  |
|**401** | Пользователь не авторизован |  -  |
|**404** | Комментарий или предложение не найдены |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **commentsCommentIdDelete**
> commentsCommentIdDelete()


### Example

```typescript
import {
    CommentsApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new CommentsApi(configuration);

let commentId: string; // (default to undefined)

const { status, data } = await apiInstance.commentsCommentIdDelete(
    commentId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **commentId** | [**string**] |  | defaults to undefined|


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
|**204** | Комментарий успешно удалён |  -  |
|**401** | Пользователь не авторизован |  -  |
|**403** | Недостаточно прав для удаления комментария |  -  |
|**404** | Комментарий не найден |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **suggestionsSuggestionIdCommentsGet**
> Array<Comment> suggestionsSuggestionIdCommentsGet()


### Example

```typescript
import {
    CommentsApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new CommentsApi(configuration);

let suggestionId: string; // (default to undefined)

const { status, data } = await apiInstance.suggestionsSuggestionIdCommentsGet(
    suggestionId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **suggestionId** | [**string**] |  | defaults to undefined|


### Return type

**Array<Comment>**

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Список комментариев |  -  |
|**404** | Проект или предложение не найдены |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **suggestionsSuggestionIdCommentsPost**
> Comment suggestionsSuggestionIdCommentsPost(suggestionsSuggestionIdCommentsPostRequest)


### Example

```typescript
import {
    CommentsApi,
    Configuration,
    SuggestionsSuggestionIdCommentsPostRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new CommentsApi(configuration);

let suggestionId: string; // (default to undefined)
let suggestionsSuggestionIdCommentsPostRequest: SuggestionsSuggestionIdCommentsPostRequest; //

const { status, data } = await apiInstance.suggestionsSuggestionIdCommentsPost(
    suggestionId,
    suggestionsSuggestionIdCommentsPostRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **suggestionsSuggestionIdCommentsPostRequest** | **SuggestionsSuggestionIdCommentsPostRequest**|  | |
| **suggestionId** | [**string**] |  | defaults to undefined|


### Return type

**Comment**

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**201** | Комментарий создан |  -  |
|**401** | Пользователь не авторизован |  -  |
|**404** | Предложение не найдено |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

