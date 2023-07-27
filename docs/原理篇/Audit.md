# Audit
像是记录用户的操作记录。相关字段位于：org.apereo.inspektr.audit.AuditActionContext

| 字段                     | 含义                                                 |
|------------------------|----------------------------------------------------|
| principal              | WHO，操作主语                                           |
| actionPerformed        | ACTION，操作。参见：org.apereo.cas.audit.AuditableActions |
| resourceOperatedUpon   | WHAT，操作对象                                          |
| whenActionWasPerformed | WHEN，操作时间                                          |
| clientIpAddress        | 操作方IP                                              |
| serverIpAddress        | 服务端IP                                              |
| userAgent              | 浏览器                                                |
| applicationCode        | 应用Code                                             |

默认情况下会将这些AuditRecord输出到控制台，格式如下：
```
=============================================================
WHO: abc
WHAT: [UsernamePasswordCredential(username=abc, source=null, customFields={})]
ACTION: AUTHENTICATION_FAILED
APPLICATION: CAS
WHEN: Sat Jul 15 10:04:03 CST 2023
CLIENT IP ADDRESS: 0:0:0:0:0:0:0:1
SERVER IP ADDRESS: 0:0:0:0:0:0:0:1
=============================================================
```

org.apereo.cas.audit.AuditableActions
AUTHENTICATION_EVENT 登录页面触发
AUTHENTICATION 登录触发

1. 可以考虑存到DB中，参见：org.apereo.inspektr.audit.support.JdbcAuditTrailManager
2. 是否可以考虑做一些登录拦截？比如一分钟只能登录X次。