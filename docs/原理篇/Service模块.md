# Service模块
[Service](https://apereo.github.io/cas/6.6.x/services/Service-Management.html)即是对CasClient的抽象，其主要作用是对CasClient进行管理，包括以下几个模块：

| 模块                                      | 说明              |
|-----------------------------------------|-----------------|
| cas-server-core-api-services            | 接口定义            |
| cas-server-core-services-authentication | ??              |
| cas-server-core-services-api            | 具体实现            |
| cas-server-core-services-registry       | ??              |
| cas-server-core-services                | Service相关Bean配置 |

```plantuml
digraph arch {
    api [label="cas-server-core-api-services"];
    "services-api" [label="cas-server-core-services-api"];
    services [label="cas-server-core-services"];
    authentication [label="cas-server-core-services-authentication"]
    registry [label="cas-server-core-services-registry"]
    
    services -> registry
    registry -> "services-api"
    "services-api" -> authentication
    authentication -> api
}
```
## RegisteredService
```plantuml
interface RegisteredService
interface WebBasedRegisteredService
abstract BaseWebBasedRegisteredService
abstract BaseRegisteredService
interface CasModelRegisteredService
class CasRegisteredService

RegisteredService <|-- WebBasedRegisteredService
RegisteredService <|-- BaseRegisteredService
WebBasedRegisteredService <|-- BaseWebBasedRegisteredService
WebBasedRegisteredService <|-- CasModelRegisteredService
BaseWebBasedRegisteredService <|-- CasRegisteredService
CasModelRegisteredService <|-- CasRegisteredService
BaseRegisteredService <|-- BaseWebBasedRegisteredService
```

| 属性                                                    | 来源                | 说明                                                                                                                                |
|-------------------------------------------------------|-------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| RegisteredServiceExpirationPolicy                     | RegisteredService | 过期策略。只有个默认策略DefaultRegisteredServiceExpirationPolicy                                                                              |
| RegisteredServiceAuthenticationPolicy                 | RegisteredService | 哪些AuthenticationHandler是必须的、排除哪些AuthenticationHandler、以及AuthenticationPolicy。只有个默认策略：DefaultRegisteredServiceAuthenticationPolicy |
| RegisteredServiceMatchingStrategy                     | RegisteredService | 用于Service匹配，判断是不是同一个                                                                                                              |
| evaluationOrder                                       | RegisteredService | 用于Service匹配，判断顺序                                                                                                                  |
| boolean matches(Service service);                     | RegisteredService | 用于Service匹配                                                                                                                       |
| boolean matches(String serviceId);                    | RegisteredService | 用于Service匹配                                                                                                                       |
| id, name, friendlyName, serviceId, description        | RegisteredService | Service相关基本属性                                                                                                                     |
| RegisteredServicePublicKey                            | RegisteredService | 用于安全地同CasClient通信                                                                                                                 |
| String getResponseType();                             | RegisteredService | 如何同CasClient通信，默认302跳转                                                                                                            |
| RegisteredServiceUsernameAttributeProvider            | RegisteredService | 登录成功后，如何返回principalId给CasClient。也就是不同的CasClient可以使用不同的principalId，有的可以是username，有的可以是userId。                                      |
| RegisteredServiceAttributeReleasePolicy               | RegisteredService | 登录成功后，返回哪些attributes给当前CasClient。                                                                                                 |
| RegisteredServiceMultifactorPolicy                    | RegisteredService | MFA相关策略                                                                                                                           |
| RegisteredServiceTicketGrantingTicketExpirationPolicy | RegisteredService | TGT过期策略                                                                                                                           |
| Set<String> getEnvironments();                        | RegisteredService | 当前service属于哪个环境，如：test, dev, prod等                                                                                                |
| RegisteredServiceAccessStrategy                       | RegisteredService | 管理当前service是否能接入CAS、SSO                                                                                                           |
| RegisteredServiceContact                              | RegisteredService | Service的联系方式。email, phone等                                                                                                        |
| RegisteredServiceProperty                             | RegisteredService | Service的其它配置属性                                                                                                                    |

### RegisteredServiceMatchingStrategy
用于Service匹配，判断是不是同一个
| 策略                                            | 说明               |
|-----------------------------------------------|------------------|
| LiteralRegisteredServiceMatchingStrategy      | 全文匹配，可忽略大小写      |
| PartialRegexRegisteredServiceMatchingStrategy | 部分正则匹配，find方法    |
| FullRegexRegisteredServiceMatchingStrategy    | 全部正则匹配，matches方法 |

### RegisteredServiceAttributeReleasePolicy
登录成功后，返回哪些attributes给当前CasClient。
```plantuml
interface RegisteredServiceAttributeReleasePolicy
abstract AbstractRegisteredServiceAttributeReleasePolicy
class ReturnAllAttributeReleasePolicy
class PatternMatchingAttributeReleasePolicy
abstract BaseMappedAttributeReleasePolicy
class ReturnMappedAttributeReleasePolicy
class ReturnRestfulAttributeReleasePolicy
class ReturnStaticAttributeReleasePolicy
class ReturnEncryptedAttributeReleasePolicy
class ScriptedRegisteredServiceAttributeReleasePolicy
class ReturnAllowedAttributeReleasePolicy
note bottom: 返回允许的attribute，默认
class DenyAllAttributeReleasePolicy
class GroovyScriptAttributeReleasePolicy
interface RegisteredServiceChainingAttributeReleasePolicy
class ChainingAttributeReleasePolicy

RegisteredServiceAttributeReleasePolicy <|-- AbstractRegisteredServiceAttributeReleasePolicy
RegisteredServiceAttributeReleasePolicy <|-- RegisteredServiceChainingAttributeReleasePolicy
AbstractRegisteredServiceAttributeReleasePolicy <|-- ReturnAllAttributeReleasePolicy
AbstractRegisteredServiceAttributeReleasePolicy <|-- PatternMatchingAttributeReleasePolicy
AbstractRegisteredServiceAttributeReleasePolicy <|-- BaseMappedAttributeReleasePolicy
AbstractRegisteredServiceAttributeReleasePolicy <|-- ReturnStaticAttributeReleasePolicy
AbstractRegisteredServiceAttributeReleasePolicy <|-- ReturnEncryptedAttributeReleasePolicy
AbstractRegisteredServiceAttributeReleasePolicy <|-- ScriptedRegisteredServiceAttributeReleasePolicy
AbstractRegisteredServiceAttributeReleasePolicy <|-- ReturnAllowedAttributeReleasePolicy
AbstractRegisteredServiceAttributeReleasePolicy <|-- DenyAllAttributeReleasePolicy
AbstractRegisteredServiceAttributeReleasePolicy <|-- GroovyScriptAttributeReleasePolicy
BaseMappedAttributeReleasePolicy <|-- ReturnMappedAttributeReleasePolicy
BaseMappedAttributeReleasePolicy <|-- ReturnRestfulAttributeReleasePolicy
RegisteredServiceChainingAttributeReleasePolicy <|-- ChainingAttributeReleasePolicy
```

### RegisteredServiceUsernameAttributeProvider
登录成功后，如何返回principalId给CasClient。也就是不同的CasClient可以使用不同的principalId，有的可以是username，有的可以是userId。
```plantuml
interface RegisteredServiceUsernameAttributeProvider
abstract BaseRegisteredServiceUsernameAttributeProvider
note right: 提供对username大小写转化及加密能力、提供 username@scope 这种形式的返回
class GroovyRegisteredServiceUsernameProvider
note bottom: Groovy脚本相关
class DefaultRegisteredServiceUsernameProvider
note bottom: 不做转化，直接取principalId，且不做加密。<font color="red"><b>默认</b></font>
class AnonymousRegisteredServiceUsernameAttributeProvider
note bottom: 随机字符串，用于匿名登录
class PrincipalAttributeRegisteredServiceUsernameProvider
note bottom: 先从attributes里取，否则用principalId

RegisteredServiceUsernameAttributeProvider <|-- BaseRegisteredServiceUsernameAttributeProvider
BaseRegisteredServiceUsernameAttributeProvider <|-- GroovyRegisteredServiceUsernameProvider
BaseRegisteredServiceUsernameAttributeProvider <|-- DefaultRegisteredServiceUsernameProvider
BaseRegisteredServiceUsernameAttributeProvider <|-- AnonymousRegisteredServiceUsernameAttributeProvider
BaseRegisteredServiceUsernameAttributeProvider <|-- PrincipalAttributeRegisteredServiceUsernameProvider
```

### HTTPSandIMAPS-10000001.json
位于 `cas-server-webapp-resources`模块下，定义了默认的 `CasRegisteredService`，即默认支持所有HTTPS及IMAPS协议的CasClient。  
由于其evaluationOrder较低，可指定一个更高的进行覆盖。
```json
{
  "@class": "org.apereo.cas.services.CasRegisteredService",
  "serviceId": "^(https|imaps)://.*",
  "name": "HTTPS and IMAPS",
  "id": 10000001,
  "description": "This service definition authorizes all application urls that support HTTPS and IMAPS protocols.",
  "evaluationOrder": 10000
}
```

## 其它
- RegisteredServiceAuthenticationPolicyCriteria