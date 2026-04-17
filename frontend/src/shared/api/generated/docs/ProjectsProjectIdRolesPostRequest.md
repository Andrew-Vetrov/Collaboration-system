# ProjectsProjectIdRolesPostRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **string** | Название роли (например, \&quot;Designer\&quot;, \&quot;Reviewer\&quot;) | [default to undefined]
**color** | **string** | Цвет роли | [optional] [default to undefined]
**likes_amount** | **number** | Начальное количество лайков, которое даёт эта роль | [optional] [default to 0]

## Example

```typescript
import { ProjectsProjectIdRolesPostRequest } from './api';

const instance: ProjectsProjectIdRolesPostRequest = {
    name,
    color,
    likes_amount,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
