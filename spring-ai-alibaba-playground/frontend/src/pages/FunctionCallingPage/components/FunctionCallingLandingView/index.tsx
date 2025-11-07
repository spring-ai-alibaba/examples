import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Sender } from "@ant-design/x";
import CodeInfo from "../CodeInfo";
import OutputResult from "../OutputResult";
import { useConversationContext } from "../../../../stores/conversation.store";
import { MenuPage } from "../../../../stores/functionMenu.store";
import { useStyles } from "../../style";

const FunctionCallingLandingView = () => {
  const { styles } = useStyles();
  const [inputContent, setInputContent] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();
  const { createConversation } = useConversationContext();

  const handleCreateConversation = (content: string) => {
    if (!content.trim() || isLoading) return;

    setIsLoading(true);
    try {
      const newConversation = createConversation(MenuPage.ToolCalling, content);
      navigate(
        `/tool-calling/${newConversation.id}?prompt=${encodeURIComponent(
          content
        )}`
      );
    } catch (error) {
      console.error("创建MCP对话错误:", error);
      setIsLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.leftPanel}>
        <CodeInfo />
        <div className={styles.senderWrapper}>
          <Sender
            value={inputContent}
            onChange={setInputContent}
            onSubmit={handleCreateConversation}
            placeholder="查询某地的基础设施信息..."
            className={styles.sender}
            loading={isLoading}
          />
        </div>
      </div>

      <div className={styles.rightPanel}>
        <OutputResult messages={[]} title="地图查询&中英翻译功能演示" />
      </div>
    </div>
  );
};

export default FunctionCallingLandingView;
