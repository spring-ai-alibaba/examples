# Spring AI Alibaba Playground

## 快速体验

### 使用 Docker 运行

运行如下命令，可以使用 Docker 快速启动 Playground 项目。请访问 [阿里云百炼 API-KEY](https://bailian.console.aliyun.com/?tab=model#/api-key)获得 API-KEY 并设置 `AI_DASHSCOPE_API_KEY=your_api_key`。

```shell
docker run -d -p 8080:8080 \
  -e AI_DASHSCOPE_API_KEY=your_api_key \
  --name spring-ai-alibaba-playground \
  sca-registry.cn-hangzhou.cr.aliyuncs.com/spring-ai-alibaba/playground:1.0.0.2-x
```

打开浏览器访问 `http://localhost:8080` 查看前端页面：

<p align="center">
    <img src="./images/playground.png" alt="PlayGround" style="max-width: 949px; height: 537px; border-radius: 15px; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);" />
</p>

#### 开启更多组件

Playground 作为一个 AI 智能体应用，依赖大模型等在线服务，需要通过环境变量指定访问凭证。如果要开启 Playground 全部能力，需要通过环境变量指定访问凭证：

  - 【必须】[阿里云百炼 API-KEY](https://bailian.console.aliyun.com/?tab=model#/api-key)，大模型服务，示例 `export AI_DASHSCOPE_API_KEY=xxx`
  - 【可选】[阿里云百炼 创建知识库](https://bailian.console.aliyun.com/?tab=app#/knowledge-base)，知识库服务，默认知识库名为saa-playground-2
  - 【可选】[百度翻译 appId 和 secretKey](https://api.fanyi.baidu.com/product/113)，使用 Tool Call 时必须，示例 `export BAIDU_TRANSLATE_APP_ID=xxx`、`export BAIDU_TRANSLATE_SECRET_KEY=xxx`
  - 【可选】[百度地图 api key](https://lbs.baidu.com/faq/api)，使用 Tool Call 必须，示例 `export BAIDU_MAP_API_KEY=xxx`
  - 【可选】[阿里云 IQS 服务 apikey](https://help.aliyun.com/document_detail/2870227.html)，使用ModuleRag联网搜索必须，示例 `export WEB_SEARCH_TYPE=ModuleRag`、`export IQS_SEARCH_API_KEY=xxx`
  - 【可选】[阿里云 AnalyticDB 向量数据库](https://help.aliyun.com/zh/analyticdb/analyticdb-for-postgresql/getting-started/instances-with-vector-engine-optimization-enabled/)，使用 RAG 时可开启（默认使用内存向量数据库）。先使用 `export VECTOR_STORE_TYPE=analyticdb` 开启 AnalyticDB，然后配置相关参数

示例 Docker 运行命令：

```shell
docker run -d -p 8080:8080 \
  -v "$(pwd)/logs:/app/logs" \
  -e AI_DASHSCOPE_API_KEY=your_api_key \
  -e ADB_ACCESS_KEY_ID=your_access_key \
  -e ADB_ACCESS_KEY_SECRET=your_secret_key \
  -e BAIDU_TRANSLATE_APP_ID=your_app_id \
  -e BAIDU_TRANSLATE_SECRET_KEY=your_secret_key \
  -e BAIDU_MAP_API_KEY=your_api_key \
  -e VECTOR_STORE_TYPE=analyticdb \
  -e IQS_SEARCH_API_KEY=your_api_key \
  --name spring-ai-alibaba-playground \
  sca-registry.cn-hangzhou.cr.aliyuncs.com/spring-ai-alibaba/playground:1.0.0.2-x
```

### 下载源码构建运行

**1. 项目打包**

```shell
mvn clean install -DskipTests
```

> PS: 项目中的前端资源默认放在了 resource/static 目录下，在执行 mvn clean package 时，默认跳过了前端资源打包。如果在启动项目时前端资源加载错误或者二次开发了前端时。
> 需要将 pom 中的 <npm.build.skip> 改为 false，然后重新执行 `mvn clean package` 或者执行 `mvn clean package -Dnpm.build.skip=false`。

**2. 配置环境变量**

请注意，必须要为 Playground 配置环境变量，配置方法参考 Docker 运行一节中的说明。

**3. 运行项目**
```shell
export AI_DASHSCOPE_API_KEY=your_api_key
java -jar ./target/app.jar
```

启动成功后，打开浏览器访问 `http://localhost:8080` 查看前端页面。

## 本地开发

参考以下步骤在本地 IDE 环境中运行项目。

**1. 前端 UI 打包**

```shell
make frontend-build
```

**2. 切换 IDE 工作目录**

Playground 作为 `spring-ai-alibaba-examples` 仓库子项目，有以下两种 IDE 导入方式：
1. 作为独立项目单独导入；
2. 作为整个 spring-ai-alibaba-examples 项目的子 module 导入。

在使用子 module 导入时，需要配置工作目录如下：

<p align="center">
    <img src="./images/run.png" alt="PlayGround" style="max-width: 949px; height: 537px; border-radius: 15px; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);" />
</p>

**3. 导出环境变量**

请注意，必须要为 Playground 配置环境变量，配置方法参考 Docker 运行一节中的说明。

如果不使用阿里云百炼创建知识库，那么需要将配置文件中的 `spring.ai.alibaba.playground.bailian.enable 修改为 false`

**4. 【可选】安装并构建前端资源**

请注意，如果你想不想启动前端页面，可以跳过此步骤！
如果想启动前端页面，执行 `make frontend-dev`

**5. 运行 `SAAPlayGroundApplication`**

项目启动之后，浏览器访问 `http://localhost:8080` 查看前端页面。

## 常见问题与解决方法

1. 如果访问接口时，报 9411 端口相关错误，这是 zipkin 服务未启动原因，不影响接口调用；
2. 注意系统启动正常，但个别功能受阻隘时，请检查相应的 AK 是否配置。
