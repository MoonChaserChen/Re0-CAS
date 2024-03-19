# PrincipalNameTransformer与PrincipalResolver
- PrincipalNameTransformer仅进行id处理，通常做一些大小写转换、增加前后缀操作等
- PrincipalResolver可进行id及属性处理，且通常查询DB等进行id转换。同时PrincipalResolver可包括PrincipalNameTransformer步骤。
- 在 `AbstractUsernamePasswordAuthenticationHandler` 里可以看到相应的使用逻辑。用户输入的用户名先经过 PrincipalNameTransformer 处理，再经过 AuthenticationHandler 认证并得到 Principal。

场景示例：  
用户名: akira，邮箱: akira133@163.com 。登录时，用户输入 akira133 + 密码登录，先通过 PrincipalNameTransformer 将用户名转换为 akira133@163.com，然后再认证后通过 PrincipalResolver 转换为 akira。

PS： PrincipalNameTransformer 称为 CredentialTransformer 更恰当一点。
