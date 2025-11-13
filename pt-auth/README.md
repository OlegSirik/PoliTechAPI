# Модуль авторизации и аутентификации пользователей

Предполагается кастомная структура учеток пользователей, поверх spring-security



структура токена
```json
{
"iss": "...",
"aud": "...",
"sub": "...",
"preferred_username": "oleg.sirik@pochta.com",        
"client_id":    "frontend-portal",             
"tenant_id":  "some-insurance",
"scope": "...",
"iat": 1730546000,                          
"exp": 1730556000,                         
"jti": "f91d6d4a-001b-4d3d-b8af-35dc9a2358d2"
}
```

Но также возможно передать в заголовках
X-Client-Id
X-Tenant-Id
Но JWT приоритетней
И еще в заголовке может быть X-Account-Id


### TODO 
1. Изменить структуру таблиц под спецификацию