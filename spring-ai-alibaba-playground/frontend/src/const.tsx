import {
  GlobalOutlined,
  ThunderboltOutlined,
  ReadOutlined,
  CommentOutlined,
  FireOutlined,
  HeartOutlined,
  SmileOutlined,
  RobotFilled,
  UserOutlined,
} from "@ant-design/icons";
import React from "react";
import DocSummaryPage from "./pages/DocSummaryPage";
import FunctionCallingPage from "./pages/FunctionCallingPage";
import ImageGenPage from "./pages/ImageGenPage";
import McpPage from "./pages/McpPage";
import MultiModalPage from "./pages/MultiModalPage";
import RagPage from "./pages/RagPage";
import { Bubble, Prompts, Welcome } from "@ant-design/x";
import { Space, GetProp, Tag, Typography, Image } from "antd";
import ReactMarkdown from "react-markdown";
import ChatPage from "./pages/ChatPage";
import { MenuPage } from "./stores/functionMenu.store";
import AnimatedSection from "./pages/components/AnimatedSection";

export const BASE_URL = "/api/v1";
export const DEFAULT_MODEL = "qwen-plus";
export const MAX_IMAGE_SIZE = 2048;

export const pageComponents = {
  [MenuPage.Chat]: ChatPage,
  [MenuPage.ImageGen]: ImageGenPage,
  [MenuPage.DocSummary]: DocSummaryPage,
  [MenuPage.MultiModal]: MultiModalPage,
  [MenuPage.ToolCalling]: FunctionCallingPage,
  [MenuPage.Rag]: RagPage,
  [MenuPage.Mcp]: McpPage,
  // [MenuPage.MoreExamples]: McpPage, // 暂时使用 McpPage 作为占位
} as const;

// 按钮配置列表
export const actionButtonConfig = [
  {
    key: "onlineSearch",
    label: "在线搜索",
    icon: <GlobalOutlined />,
    styleClass: "searchButton",
    baseColor: "#4096ff",
    bgColor: "#e6f4ff",
    activeColor: "#1677ff",
    description: "使用网络搜索获取最新信息",
    tipTitle: "在线搜索默认模型为 DeepSeek-R1.",
  },
  {
    key: "deepThink",
    label: "深度思考",
    icon: <ThunderboltOutlined />,
    styleClass: "thinkButton",
    baseColor: "#9254de",
    bgColor: "#f9f0ff",
    activeColor: "#722ed1",
    description: "深度分析问题并给出详细回答",
    tipTitle: "深度思考模型可以任意选择，各个模型输出效果有差异.",
  },
];

const renderTitle = (icon: React.ReactElement, title: string) => (
  <Space align="start">
    {icon}
    <span>{title}</span>
  </Space>
);

export const placeholderPromptsItems: GetProp<typeof Prompts, "items"> = [
  {
    key: "1",
    label: renderTitle(
      <ReadOutlined style={{ color: "#1890FF" }} />,
      "用户指南"
    ),
    description: "",
    children: [
      {
        key: "2-1",
        icon: <HeartOutlined />,
        description: `如何使用 Spring AI Alibaba 构建聊天机器人？`,
      },
      {
        key: "2-2",
        icon: <SmileOutlined />,
        description: `如何在 Spring AI Alibaba 中使用 RAG？`,
      },
      {
        key: "2-3",
        icon: <CommentOutlined />,
        description: `使用 Spring AI Alibaba 的最佳实践有哪些？`,
      },
    ],
  },
  {
    key: "2",
    label: renderTitle(
      <FireOutlined style={{ color: "#FF4D4F" }} />,
      "常见问题"
    ),
    description: "",
    children: [
      {
        key: "1-1",
        description: `Spring AI Alibaba 是否支持工作流和多智能体？`,
      },
      {
        key: "1-2",
        description: `Spring AI 和 Spring AI Alibaba 之间的关系是什么？`,
      },
      {
        key: "1-3",
        description: `我可以在哪里参与贡献？`,
      },
    ],
  },
];

export const defaultKey = Date.now().toString();
export const defaultConversationsItems = [
  {
    key: defaultKey,
    label: (
      <span>
        对话 1
        <Tag style={{ marginLeft: 8 }} color="green">
          {DEFAULT_MODEL}
        </Tag>
      </span>
    ),
  },
];

export const aiConfig = {
  placement: "start" as "start" | "end",
  avatar: {
    icon: <RobotFilled />,
  },
  styles: {
    content: {
      borderRadius: 16,
    },
  },
  messageRender: (content) => (
    <Typography>
      <ReactMarkdown>{content}</ReactMarkdown>
    </Typography>
  ),
};

export const roles: GetProp<typeof Bubble.List, "roles"> = {
  ai: {
    typing: { step: 5, interval: 20 },
    ...aiConfig,
  },
  aiHistory: {
    ...aiConfig,
  },
  local: {
    placement: "end",
    variant: "shadow",
    avatar: {
      icon: <UserOutlined />,
    },
  },
  file: {
    placement: "end",
    variant: "borderless",
    messageRender: (base64: string) => {
      return (
        <Image src={base64} style={{ maxHeight: 250, paddingRight: 32 }} />
      );
    },
    avatar: <></>,
  },
};

export const conversationsMap: Record<
  string,
  {
    model: string;
    messages: any[];
    params: { onlineSearch: boolean; deepThink: boolean };
  }
> = {};

// 默认会话界面
export const PlaceholderNode = ({ className, onPromptsItemClick }) => {
  return (
    <AnimatedSection>
      <Space direction="vertical" size={16} className={className}>
        <Welcome
          variant="borderless"
          icon="https://mdn.alipayobjects.com/huamei_iwk9zp/afts/img/A*s5sNRo5LjfQAAAAAAAAAAAAADgCCAQ/fmt.webp"
          title="你好，我是 Spring AI Alibaba"
          styles={{
            description: {
              fontSize: 16,
              width: "1000px",
            },
          }}
          description="一个基于 Spring AI Alibaba 框架构建的 AI 助手，通过 RAG 技术嵌入了 Spring AI Alibaba 领域知识。"
        />
        <Prompts
          // title="你想了解什么？"
          items={placeholderPromptsItems}
          styles={{
            list: {
              width: "100%",
            },
            item: {
              flex: 1,
            },
          }}
          onItemClick={onPromptsItemClick}
        />
      </Space>
    </AnimatedSection>
  );
};
