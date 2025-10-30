# Mem0 REST API 服务器

Mem0 提供了一个 REST API 服务器（使用 FastAPI 编写）。用户可通过 REST 端点执行所有操作。该 API 还包含 OpenAPI 文档，服务器运行时可通过 `/docs` 端点访问。

## 功能特性

- **创建记忆**：基于消息为用户、代理或运行任务创建记忆。
- **检索记忆**：获取指定用户、代理或运行任务的所有记忆。
- **搜索记忆**：根据查询条件搜索已存储的记忆。
- **更新记忆**：修改现有记忆的内容。
- **删除记忆**：删除特定记忆，或清除某用户、代理或运行任务的全部记忆。
- **重置记忆**：清空某用户、代理或运行任务的所有记忆。
- **OpenAPI 文档**：通过 `/docs` 端点在线查看。

## 运行服务器
请参考 [官方文档](https://docs.mem0.ai/open-source/features/rest-api) 中的说明启动服务器。


  ```bash
  cp ./.env.example .env
  ```

## 编辑.env文件

```text
OPENAI_API_KEY=sk-xxx   #你的阿里云百炼Api Key
```

## 运行docker compose up
```bash
cd <your dir>
docker compose up
```

## 访问swagger
http://localhost:8888/docs

`注意，main.py跟官方略有改动，主要改动在config接口`
