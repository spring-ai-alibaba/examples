import {
  BulbOutlined,
  CloudOutlined,
  EnvironmentOutlined,
  FireOutlined,
  GithubOutlined,
  PictureOutlined,
  QuestionCircleOutlined,
  RobotOutlined,
  SafetyOutlined,
  SearchOutlined,
} from "@ant-design/icons";
import {
  McpServer,
  McpServerFormatted,
  McpTool,
  McpToolFormatted,
  FormField,
} from "./types";
import React from "react";

// 图标组件映射
export const name2iconMap: Record<string, React.ReactNode> = {
  GithubOutlined: <GithubOutlined />,
  EnvironmentOutlined: <EnvironmentOutlined />,
  SearchOutlined: <SearchOutlined />,
  CloudOutlined: <CloudOutlined />,
  BulbOutlined: <BulbOutlined />,
  QuestionCircleOutlined: <QuestionCircleOutlined />,
  RobotOutlined: <RobotOutlined />,
  PictureOutlined: <PictureOutlined />,
  SafetyOutlined: <SafetyOutlined />,
  FireOutlined: <FireOutlined />,
  Default: <CloudOutlined />,
};

/**
 * 获取服务名称对应的图标名称
 */
export const getIconNameByServerName = (serverName: string): string => {
  const iconMapping: Record<string, string> = {
    GitHub: "GithubOutlined",
    "github-mcp-server": "GithubOutlined",
    "my-weather-server": "CloudOutlined",
    高德地图: "EnvironmentOutlined",
    Tavily搜索: "SearchOutlined",
    AWS知识库检索: "CloudOutlined",
    浮墨笔记: "CloudOutlined",
  };

  return iconMapping[serverName] || "Default";
};

/**
 * 格式化MCP服务列表数据
 */
export const formatMcpServerListData = (
  servers: McpServer[]
): McpServerFormatted[] => {
  return servers.map((server) => {
    const iconName = getIconNameByServerName(server.name);

    const tools = server.toolList.map((tool: McpTool) => {
      let parsedParams: Record<string, any> = {};
      let schema: any = null;

      try {
        schema = JSON.parse(tool.params);
        if (schema && schema.properties) {
          parsedParams = Object.entries(schema.properties).reduce(
            (acc, [key, propDef]: [string, any]) => {
              acc[key] = propDef.description || key;
              return acc;
            },
            {} as Record<string, string>
          );
        }
      } catch (error) {
        console.error(`转换表单参数错误 ${tool.name}:`, error);
      }

      return {
        id: tool.name,
        name: tool.name,
        description: tool.desc,
        params: parsedParams,
        schema: schema,
      };
    });

    return {
      id: server.id,
      name: server.name,
      icon: iconName,
      description: server.desc || "",
      tools,
      env: server.env,
    };
  });
};

/**
 * 生成表单字段配置，从JSON schema创建表单字段
 */
export const generateFormFields = (schema: any): FormField[] => {
  // Handle the case where schema might be null or undefined
  if (!schema || typeof schema !== "object") {
    return [];
  }

  const properties = schema.properties || {};

  // Get the required fields array or default to empty array
  const requiredFields = Array.isArray(schema.required) ? schema.required : [];

  return Object.entries(properties).map(([key, propDef]: [string, any]) => {
    const isRequired = requiredFields.includes(key);
    const description = propDef.description || "";

    let fieldType = "text";
    if (propDef.type === "boolean") {
      fieldType = "checkbox";
    } else if (propDef.type === "number" || propDef.type === "integer") {
      fieldType = "number";
    } else if (
      propDef.format === "password" ||
      key.toLowerCase().includes("token") ||
      key.toLowerCase().includes("password")
    ) {
      fieldType = "password";
    }

    return {
      key,
      label: key,
      fieldType,
      placeholder: description || `Enter ${key}`,
      required: isRequired,
      description,
    };
  });
};
