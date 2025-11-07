export const MOCK_SERVER_RESPONSE = {
  id: "server123",
  name: "GitHub",
  desc: "代码仓库管理、文件操作和GitHub API集成",
  toolList: [
    {
      name: "创建或更新文件",
      params: {
        repository: "Repository name",
        path: "File path",
        content: "File content",
        message: "Commit message",
      },
      desc: "在仓库中创建新文件或更新现有文件",
    },
    {
      name: "搜索仓库",
      params: {
        query: "Search query",
        language: "Programming language",
        sort: "Sort criteria",
      },
      desc: "通过关键字或条件搜索仓库",
    },
  ],
};

export const MOCK_MCP_SERVERS = [
  {
    id: "github-mcp-server",
    name: "GitHub",
    icon: "GithubOutlined",
    description: "代码仓库管理、文件操作和GitHub API集成",
  },
  {
    id: "weather-server",
    name: "Weather Service",
    icon: "CloudOutlined",
    description: "Weather forecast and air quality information",
  },
];

export const GITHUB_TOOLS = [
  {
    id: "create_or_update_file",
    name: "创建或更新文件",
    description: "在仓库中创建新文件或更新现有文件",
  },
  {
    id: "search_repositories",
    name: "搜索仓库",
    description: "通过关键字或条件搜索仓库",
  },
  {
    id: "create_repository",
    name: "创建仓库",
    description: "在GitHub上创建新仓库",
  },
  {
    id: "get_file_contents",
    name: "获取文件内容",
    description: "从仓库中检索文件内容",
  },
];

export const DEFAULT_TOKEN_PLACEHOLDER = "<您的令牌>";

export const MOCK_SERVER_CONFIG = `{
  "mcpservers": {
    "": {
      "command": "npx",
      "args": [
        "-y",
        "mcprouter"
      ],
      "env": {
        "SERVER_KEY": "za119fm9fbmvk8"
      }
    }
  }
}`;
