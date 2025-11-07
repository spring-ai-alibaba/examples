import {
  PlusOutlined,
  PictureOutlined,
  ReadOutlined,
  ToolOutlined,
  DatabaseOutlined,
  ApiOutlined,
} from "@ant-design/icons";
import { Button } from "antd";
import React from "react";
import { FunctionMenuItem } from "../../../types";

export const functionMenuItems: FunctionMenuItem[] = [
  {
    key: "chat",
    icon: <PlusOutlined />,
    label: "对话",
    render: (props: any) => {
      const { item, styles, handleNewChat } = props;
      return (
        <Button
          key={item?.key}
          onClick={handleNewChat}
          type="primary"
          className={styles.newChatBtn}
          icon={<PlusOutlined />}
          block
        >
          新对话
        </Button>
      );
    },
  },
  {
    key: "image-gen",
    icon: <PictureOutlined />,
    label: "图像生成",
  },
  {
    key: "doc-summary",
    icon: <ReadOutlined />,
    label: "文档总结",
  },
  // {
  //   key: "multi-modal",
  //   icon: <PictureOutlined />,
  //   label: "多模态",
  // },
  {
    key: "tool-calling",
    icon: <ToolOutlined />,
    label: "Tool Calling",
  },
  {
    key: "rag",
    icon: <DatabaseOutlined />,
    label: "RAG",
  },
  {
    key: "mcp",
    icon: <ApiOutlined />,
    label: "MCP",
  },
  // {
  //   key: "more-examples",
  //   icon: <PhoneOutlined />,
  //   label: "更多案例",
  // },
];
