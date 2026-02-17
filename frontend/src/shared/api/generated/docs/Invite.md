# Invite


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**invite_id** | **string** | Идентификатор приглашения | [default to undefined]
**project_id** | **string** | Идентификатор проекта | [default to undefined]
**email** | **string** | Email приглашённого пользователя | [default to undefined]
**invited_at** | **string** | Дата и время отправки приглашения | [default to undefined]
**sender_nickname** | **string** |  | [default to undefined]
**project_name** | **string** |  | [default to undefined]
**receiver_nickname** | **string** |  | [default to undefined]
**receiver_avatar** | **string** |  | [optional] [default to undefined]

## Example

```typescript
import { Invite } from './api';

const instance: Invite = {
    invite_id,
    project_id,
    email,
    invited_at,
    sender_nickname,
    project_name,
    receiver_nickname,
    receiver_avatar,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
