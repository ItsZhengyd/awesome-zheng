本示例目标：
- spring security + jpa
- jwt自定义认证细节(如虽然携带token，但是前后两次ip变化了；如自定义token刷新策略)
- 用户认证信息及权限缓存到redis