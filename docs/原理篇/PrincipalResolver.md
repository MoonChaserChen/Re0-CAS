# PrincipalResolver
从认证前的Credential生成认证后的Principal。最简单的是将 Credential.getId() 赋给 Principal.getId()。
但实现上可能会有以下两种场景：
1. 为Principal查出额外的一些属性。nick_name, email等。
2. 如果用户以邮箱或手机号登录，通常需要转换成user_name设置到Principal.getId()。

## 相关属性
| 属性                  | 说明                                                        |
|---------------------|-----------------------------------------------------------|
| name                | 唯一名称，基础属性                                                 |
| order               | 顺序，可进行链式处理                                                |
| supports            | 用以判断是否可对Credential进行处理（与AuthenticationHandler一样）          |
| resolve             | 通过Credential（、Principal、AuthenticationHandler）生成Principal |
| IPersonAttributeDao | 用以查出Principal的相关属性                                        |

## 自带实现

| 实现                               | 说明                                                                                  |
|----------------------------------|-------------------------------------------------------------------------------------|
| ProxyingPrincipalResolver        | 直接使用认证前的Credential.id创建Principal                                                    |
| EchoingPrincipalResolver         | 直接使用认证后的Principal                                                                   |
| PersonDirectoryPrincipalResolver | 比较实用的一种实现，参见[PersonDirectoryPrincipalResolver](PersonDirectoryPrincipalResolver.md) |
| ChainingPrincipalResolver        | **链式PrincipalResolver，感觉没啥用，设计冗余了**                                                 |

### ChainingPrincipalResolver
链式PrincipalResolver，每个Resolver的入参Credential.id为上一个Resolver的Principal.id。（**实际看代码好像并没有这个逻辑**）  
想不到有啥应用的地方，看说明好像跟 X.509 有关。但是默认的就是这个Bean，参见：
`org.apereo.cas.config.CasCoreAuthenticationPrincipalConfiguration.CasCoreAuthenticationPrincipalResolutionConfiguration`  
每个PrincipalResolver均执行，得到的PrincipalId组成List，Attributes通过IAttributeMerger进行合并（默认是Map.put的方式覆盖）。最后再使用 PrincipalElectionStrategy 选举出最终结果。

#### PrincipalElectionStrategy
在 ChainingPrincipalResolver 中，会经过多次resolve，最终生成多个Principal。需要通过 PrincipalElectionStrategy 选举出主要的Principal。  
自带两个实现：DefaultPrincipalElectionStrategy 与 ChainingPrincipalElectionStrategy

##### DefaultPrincipalElectionStrategy
```java
@Override
public Principal nominate(final Collection<Authentication> authentications, final Map<String, List<Object>> principalAttributes) {
    // Principal.id 取第1个Authentication中的Principal.id 
    // Principal.attributes 取参数中的 principalAttributes
}

@Override
public Principal nominate(final List<Principal> principals, final Map<String, List<Object>> attributes) {
    // Principal.id 通过 PrincipalElectionStrategyConflictResolver 获取，已有策略：第1个Principal.id、最后1个Principal.id（默认策略）。
    // Principal.attributes 取参数中的 principalAttributes
}
```
##### ChainingPrincipalElectionStrategy
直接使用排序后第1个 PrincipalElectionStrategy 处理的结果，没啥其它逻辑。



## 其它
### PrincipalResolutionExecutionPlan
用以管理PrincipalResolver。
```java
public interface PrincipalResolutionExecutionPlan {
    void registerPrincipalResolver(PrincipalResolver principalResolver);
    Collection<PrincipalResolver> getRegisteredPrincipalResolvers();
}
```
