在企业中实际使用MCP过程中，或在调用第三方MCP服务，需要识别Client侧调用者身份

1. 先启动Restful服务，以101端口对外暴露时间服务
2. 然后启动MCP Server服务，解析Restful方法，对外提供工具
3. 最后启动MCP Client服务，传入对应的请求头配置

在client侧配置的请求头信息，在触发工具时，可观察到携带请求头信息经过MCP Server最终传递到了restful服务
