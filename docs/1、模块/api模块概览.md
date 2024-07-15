# api模块概览
```plantuml
digraph G {
    "cas-server-core-api-monitor"
    "cas-server-core-api-protocol"
    "cas-server-core-api-util"
    "cas-server-core-api" -> "cas-server-core-api-logout"
    "cas-server-core-api" -> "cas-server-core-api-validation"
    "cas-server-core-api-audit" -> "cas-server-core-api-configuration"
    "cas-server-core-api-audit" -> "cas-server-core-api"
    "cas-server-core-api-audit" -> "cas-server-core-api-events"
    "cas-server-core-api-audit" -> "cas-server-core-api-web"
    "cas-server-core-api-authentication" -> "cas-server-core-api-monitor"
    "cas-server-core-api-authentication" -> "cas-server-core-api-configuration-model"
    "cas-server-core-api-configuration" -> "cas-server-core-api-webflow"
    "cas-server-core-api-configuration-model" -> "cas-server-core-api-util"
    "cas-server-core-api-configuration-model" -> "cas-server-core-api-protocol"
    "cas-server-core-api-cookie" -> "cas-server-core-api-webflow"
    "cas-server-core-api-events" -> "cas-server-core-api-validation"
    "cas-server-core-api-logout" -> "cas-server-core-api-services"
    "cas-server-core-api-mfa" -> "cas-server-core-api-services"
    "cas-server-core-api-services" -> "cas-server-core-api-ticket"
    "cas-server-core-api-throttle" -> "cas-server-core-api-authentication"
    "cas-server-core-api-ticket" -> "cas-server-core-api-authentication"
    "cas-server-core-api-validation" -> "cas-server-core-api-services"
    "cas-server-core-api-web" -> "cas-server-core-api-authentication"
    "cas-server-core-api-webflow" -> "cas-server-core-api-services"
}
```


| 模块                                      | 说明         |
|-----------------------------------------|------------| 
| cas-server-core-api-util                | 一些工具类或接口定义 |
| cas-server-core-api-protocol            | CAS协议相关常量  |
| cas-server-core-api-monitor             | 监控、健康检查    |
| cas-server-core-api-configuration-model | CAS配置项汇总   |
| cas-server-core-api-authentication      | 核心认证       |
| cas-server-core-api-ticket              | Ticket票据服务 |
| cas-server-core-api-throttle            | 限流         |
| cas-server-core-api-services            | 服务抽象       |
| cas-server-core-api-web                 | Web服务相关    |
| cas-server-core-api-logout              | 单点退出       |
| cas-server-core-api-validation          | ST验证       |
| cas-server-core-api-webflow             | webflow相关  |
| cas-server-core-api-mfa                 | 多因素认证      |
| cas-server-core-api                     | 核心接口定义     |
| cas-server-core-api-events              | CAS事件      |
| cas-server-core-api-configuration       | ??         |
| cas-server-core-api-cookie              | cookie相关   |
| cas-server-core-api-audit               | audit相关    |
