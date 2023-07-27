# Plan与PlanConfigurer
CAS里有很多这样结队出现的Bean。其中 Plan做为管理类，一般含有register方法与get方法。PLanConfigurer是一个没有实现的接口，在lambda创建过程中对Plan进行配置。

## 示例
1. 类的定义
    ```java
    public interface AuthenticationEventExecutionPlan {
        boolean registerAuthenticationHandler(AuthenticationHandler handler);
        boolean registerAuthenticationHandlerWithPrincipalResolver(AuthenticationHandler handler, PrincipalResolver principalResolver);
    }
    
    @FunctionalInterface
    public interface AuthenticationEventExecutionPlanConfigurer extends Ordered {
        void configureAuthenticationExecutionPlan(AuthenticationEventExecutionPlan plan) throws Exception;
    }
    ```

2. Spring中配置
```java
@Bean
public AuthenticationEventExecutionPlanConfigurer acceptUsersAuthenticationEventExecutionPlanConfigurer() {
    return plan -> {
        // 具体配置
        plan.registerAuthenticationHandlerWithPrincipalResolver(xx, xx);
    };
}

@Bean
public AuthenticationEventExecutionPlan authenticationEventExecutionPlan(final List<AuthenticationEventExecutionPlanConfigurer> configurers) {
     // 模板代码，几乎不用动
     configurers.forEach(Unchecked.consumer(c -> {
        c.configureAuthenticationExecutionPlan(plan);
     }));
     return plan;
}
```
上面 `AuthenticationEventExecutionPlanConfigurer` 在Spring中生成了个Configurer Bean，这个Bean的 `configureAuthenticationExecutionPlan()` 方法有具体的配置方法。  
下面 `AuthenticationEventExecutionPlan` 中的参数能自动注入上面局创建的Configurer Bean，同时后面代码会调用 `configureAuthenticationExecutionPlan()` 方法，也就是上面“具体配置”部分。

达到了Plan的声明与配置分离的作用，且可以配置多个不同的 `PlanConfigurer` 对同一个 `Plan` 进行配置。