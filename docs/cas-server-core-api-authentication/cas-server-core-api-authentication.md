# cas-server-core-api-authentication
CAS的全称是中央认证服务（Central Authentication Server），因此其一个核心就是认证。
cas-server-core-api-authentication这个模块就是认证相关的一些定义，比较底层。

## 基本概念
### Credential
Credential 简单理解为认证的主体，即通过什么方式来认证。比如：用户名+密码，手机号+验证码，Token等。
![Crendital](../images/module/credentials.png)

org.apereo.cas.configuration.model.core.authentication.AuthenticationPolicyProperties.sourceSelectionEnabled

UsernamePasswordCredential带有source字段，设计目的在于可根据不同的source来选择不同的AuthenticationHandler

如果同时支持用户名/密码登录，手机号验证码登录，应该用哪个Credential呢？
或者说用户名/密码登录时用UsernamePasswordCredential，手机号验证码登录用另一个Credential？