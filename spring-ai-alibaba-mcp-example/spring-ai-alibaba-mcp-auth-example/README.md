企业中部署MCP Server服务，需要鉴别MCP Client侧连接过来时用户的身份，本模块基于Web的过滤器机制实现对请求头的鉴权

```angular2html
client/mcp-auth-web-client(修改McpSseClientProperties源码，添加请求头属性)
    - mcp client侧，请求头中添加token-yingzi-1，value为token-yingzi-1

server/mcp-auth-web-server
    - 当mcp client侧建立来的连接请求，有请求头，key为token-yingzi-1，value为token-yingzi-1鉴权放行。否则拒绝连接请求。

```
