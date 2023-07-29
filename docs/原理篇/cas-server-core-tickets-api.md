# cas-server-core-tickets-api

## UniqueTicketIdGenerator
```plantuml
interface UniqueTicketIdGenerator {
    char SEPARATOR = '-';
    int TICKET_SIZE = 24; <font color="red"># bytes</font>
    String getNewTicketId(String prefix);
}

UniqueTicketIdGenerator <|-- DefaultUniqueTicketIdGenerator
DefaultUniqueTicketIdGenerator <|-- HostNameBasedUniqueTicketIdGenerator
UniqueTicketIdGenerator <|-- GroovyUniqueTicketIdGenerator

HostNameBasedUniqueTicketIdGenerator <|-- ProxyTicketIdGenerator
HostNameBasedUniqueTicketIdGenerator <|-- ProxyGrantingTicketIdGenerator
HostNameBasedUniqueTicketIdGenerator <|-- TicketGrantingTicketIdGenerator
HostNameBasedUniqueTicketIdGenerator <|-- ServiceTicketIdGenerator
```
### DefaultUniqueTicketIdGenerator
`[PREFIX]-[SEQUENCE NUMBER]-[RANDOM STRING]-[SUFFIX]`

### HostNameBasedUniqueTicketIdGenerator
`[PREFIX]-[SEQUENCE NUMBER]-[RANDOM STRING]-[HostName]`  
示例： `TGT-1-14YKgGuGQ2lcMJxKhgEbWzlFxK0I0EpKupdR1K-pL-zKOEtAL5qLtBdL2qV6ucI0DKo-akiradeMac-mini`

## TicketGrantingTicketImpl & ServiceTicketImpl
```plantuml
interface Ticket
interface AuthenticationAwareTicket
interface TicketGrantingTicketAwareTicket {
    TicketGrantingTicket getTicketGrantingTicket();
}
note right of TicketGrantingTicketAwareTicket::getTicketGrantingTicket
    对ST来说，是签发它的TGT
    对TGT来说，是它父级的TGT
end note
interface TicketGrantingTicket
abstract AbstractTicket {
    - ExpirationPolicy expirationPolicy
    - String id
    - ZonedDateTime lastTimeUsed
    - ZonedDateTime previousTimeUsed
    - ZonedDateTime creationTime
    - int countOfUses
    - Boolean expired = Boolean.FALSE
    boolean isExpired() <font color="red"># ExpirationPolicy.isExpired || this.expired</font>
    Authentication getAuthentication() <font color="red"># 由TicketGrantingTicket实现</font>
    void markTicketExpired() <font color="red"># this.expired = TRUE</font>
    void update() <font color="red"># update this && update TGT</font>
}
interface AuthenticatedServicesAwareTicketGrantingTicket
interface ProxyGrantingTicketIssuerTicket
interface RenewableServiceTicket
interface ServiceTicket

Ticket <|-- AuthenticationAwareTicket
Ticket <|-- ProxyGrantingTicketIssuerTicket
Ticket <|-- RenewableServiceTicket
AuthenticationAwareTicket <|-- TicketGrantingTicketAwareTicket
TicketGrantingTicketAwareTicket <|-- TicketGrantingTicket
TicketGrantingTicketAwareTicket <|-- AbstractTicket
AuthenticatedServicesAwareTicketGrantingTicket <|-- TicketGrantingTicketImpl

TicketGrantingTicketAwareTicket <|-- ServiceTicket
TicketGrantingTicket <|-- AuthenticatedServicesAwareTicketGrantingTicket
AbstractTicket <|-- TicketGrantingTicketImpl
AbstractTicket <|-- ServiceTicketImpl
ServiceTicket <|-- ServiceTicketImpl
RenewableServiceTicket <|-- ServiceTicketImpl
ProxyGrantingTicketIssuerTicket <|-- ServiceTicketImpl
```

## TGT签发ST
```java
public interface TicketGrantingTicket {
    // id - 签发ST的id
    // Service - 为哪个Service签发ST
    // ExpirationPolicy - ST的过期策略
    // credentialProvided 是否是首次全局登录时的签发
    // ServiceTicketSessionTrackingPolicy 将TGT为哪个Service签发了ST记录下来
    ServiceTicket grantServiceTicket(String id,
                                     Service service,
                                     ExpirationPolicy expirationPolicy,
                                     boolean credentialProvided,
                                     ServiceTicketSessionTrackingPolicy trackingPolicy);
}
```

TGT签发ST流程：
```plantuml
TicketGrantingTicketImpl -> TicketGrantingTicketImpl: new ServiceTicketImpl
note right: 生成ST
TicketGrantingTicketImpl -> DefaultServiceTicketSessionTrackingPolicy: track
note right: 记录签发关系（哪个TGT为哪个Service签发了ST）
DefaultServiceTicketSessionTrackingPolicy -> TicketGrantingTicketImpl: update
note right: 更新TGT使用时间、次数
DefaultServiceTicketSessionTrackingPolicy -> ServiceTicketImpl
note right: 更新刚签发ST.service.principal <font color="red">这里有点不太明白</font>
DefaultServiceTicketSessionTrackingPolicy -> CAS配置: 查看 onlyTrackMostRecentSession 配置
alt onlyTrackMostRecentSession
    DefaultServiceTicketSessionTrackingPolicy -> TicketGrantingTicketImpl
    note right: 删除TGT里保存的Service
    DefaultServiceTicketSessionTrackingPolicy -> TicketRegistry: deleteTicket
    note right: 删除TicketRegistry里的ServiceTicket
end alt
note right
    onlyTrackMostRecentSession 默认为 True
    即默认情况下，TGT在为一个Service签发ST前会删掉已签发的ST
    但是这里判断是否为同一个Service是通过normalizePath（不带参数请求路径）来判断的
end note
```

### ST签发时机
在登录流程中，登录成功生成TGT时或发现已有TGT是有效的时候会签发ST。
```java
public class ServiceTicketRequestWebflowEventResolver extends AbstractCasWebflowEventResolver {
    @Override
    public Set<Event> resolveInternal(final RequestContext context) {
        if (isRequestAskingForServiceTicket(context)) {
            LOGGER.trace("Authentication request is asking for service tickets");
            val source = grantServiceTicket(context);
            return source != null ? CollectionUtils.wrapSet(source) : null;
        }
        return null;
    }
    // other code ignored.
}
```
真正执行的地方在Webflow中的 `GenerateServiceTicketAction`

### TGT与ST的关联
AuthenticatedServicesAwareTicketGrantingTicket保存了已签发 ServiceTicket.id <--> Service 的一个Map。  
同时ServiceTicket也会保存对应的Service。
```plantuml
class AuthenticatedServicesAwareTicketGrantingTicket {
    ST1.id <-> Service1
    ST2.id <-> Service2
    ST3.id <-> Service3
}

class ST {
    Service
}

AuthenticatedServicesAwareTicketGrantingTicket::Service2 -right-> ST::Service
```

### ST的ExpirationPolicy
`org.apereo.cas.ticket.factory.DefaultServiceTicketFactory.determineExpirationPolicyForService`  
先看Service级别配置，再看全局配置。

```
# 默认全局配置（只能用一次，有效期10s。换句话说全局登录后如果10s后CasClient再来验证ST的有效性就会失败。）
cas.ticket.st.number-of-uses=1
cas.ticket.st.time-to-kill-in-seconds=10
```

### TGT的ExpirationPolicy
`org.apereo.cas.ticket.factory.DefaultTicketGrantingTicketFactory.getTicketGrantingTicketExpirationPolicy`
同样先看Service级别的配置，再看全局配置。

```
# 配置有很多级，简单情况用这一级就可以。TGT默认creationTime后8小时内且lastTimeUsed后2小时内有效。
# 这种配置格式可参考 java.time.Duration#parse，也可以直接配置为数字（单位：秒）
cas.ticket.tgt.primary.max-time-to-live-in-seconds=PT8H
cas.ticket.tgt.primary.time-to-kill-in-seconds=PT2H
```
