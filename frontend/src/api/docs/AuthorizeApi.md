# AuthorizeApi

All URIs are relative to *http://localhost*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**authCallbackGet**](#authcallbackget) | **GET** /auth/callback | Callback после авторизации|
|[**authGet**](#authget) | **GET** /auth | Начало авторизации через Google|

# **authCallbackGet**
> authCallbackGet()

Google перенаправляет сюда пользователя после авторизации. Сервер получает code, обменивает его на access_token, запрашивает профиль пользователя (email, name) и сохраняет сессию. Затем редиректит обратно на /projects. 

### Example

```typescript
import {
    AuthorizeApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new AuthorizeApi(configuration);

let code: string; //Код авторизации, выданный Google. (default to undefined)

const { status, data } = await apiInstance.authCallbackGet(
    code
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **code** | [**string**] | Код авторизации, выданный Google. | defaults to undefined|


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
|**401** | Редирект на главную страницу после успешного входа. |  * Location -  <br>  |
|**400** | Ошибка авторизации. |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **authGet**
> authGet()

Перенаправляет пользователя на страницу авторизации Google OAuth2.

### Example

```typescript
import {
    AuthorizeApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new AuthorizeApi(configuration);

const { status, data } = await apiInstance.authGet();
```

### Parameters
This endpoint does not have any parameters.


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
|**401** | Редирект на Google OAuth2. |  * Location - URL авторизации Google с параметрами (client_id, redirect_uri, scope, response_type) <br>  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

