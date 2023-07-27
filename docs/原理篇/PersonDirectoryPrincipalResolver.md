# PersonDirectoryPrincipalResolver
比较实用的一种实现，可进行Principal的id及属性处理。内有PrincipalResolutionContext，核心的resolve方法就是由这个PrincipalResolutionContext去实现的。
## PrincipalResolutionContext

| 属性                                   | 含义                                       |
|--------------------------------------|------------------------------------------|
| IPersonAttributeDao                  | 获取用户属性                                   |
| PrincipalFactory                     | 用以创建Principal                            |
| returnNullIfNoAttributes             | 如果没有Attributes，连Principal也返回null         |
| principalNameTransformer             | 用以转换PrincipalId的                         |
| principalAttributeNames              | 逗号分隔的属性名称，这部分字段如果查到数据会覆盖PrincipalId      |
| useCurrentPrincipalId                | 生成Principal时，是否优先取当前认证后Principal的id      |
| resolveAttributes                    | 是否处理Attributes                           |
| activeAttributeRepositoryIdentifiers | 激活的RepositoryId，跟 IPersonAttributeDao 有关 |
| IAttributeMerger                     | 用以合并用户属性，但合并的条件有点奇怪                      |


```plantuml
@startuml
participant "PersonDirectoryPrincipalResolver\nresolve" as resolve
resolve -> resolve: extractPrincipalId
note right: 提取principalId。如果是useCurrentPrincipalId，就优先取认证结果principal中的。否则和其次再从crendential中取。
resolve -> PrincipalResolutionContext: getPrincipalNameTransformer
note right: 从PrincipalResolutionContext中获取PrincipalNameTransformer
alt 能获取到PrincipalNameTransformer
    resolve -> PrincipalNameTransformer: transform
    note right
        使用PrincipalNameTransformer进行PrincipalId转换
        <font color="red">这里PrincipalId就处理好了，后面进行Attribute处理</font>
    end note
end 
resolve -> PrincipalResolutionContext: isResolveAttributes
note right: 从PrincipalResolutionContext中判断是否需要处理Attributes
alt 需要处理
    resolve -> PrincipalAttributeRepositoryFetcher: retrieve
    note right
        这里统一在PrincipalAttributeRepositoryFetcher里处理
        但是实际负责处理的还是PrincipalResolutionContext里的IPersonAttributeDao
    end note
    resolve -> IPersonAttributeDao: getPeople
    note right #red: 这里通通过有很多种实现
    resolve -> PrincipalResolutionContext: isReturnNullIfNoAttributes
    note right:  查看配置，如果为true且没查出attribute，则Principal直接返回null。（这个感觉不太适用）
    resolve -> resolve: convertPersonAttributesToPrincipal
    note right #red
        这里还会进行mergeAttributes。将认证得到的Attributes和IPersonAttributeDao得到的Attributes合并。但是条件有点奇怪
        principalAttributeNames将会覆盖最终的PrincipalId
    end note
end alt
resolve -> resolve: buildResolvedPrincipal
note right: 将前面处理好的PrincipalId与Attributes拼装成Principal
@enduml
```