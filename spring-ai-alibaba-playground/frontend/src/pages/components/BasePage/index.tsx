import React, { useEffect } from "react";
import { Typography } from "antd";
import { useConversationContext } from "../../../stores/conversation.store";
import { useParams } from "react-router-dom";

interface BasePageProps {
  title: string;
  children?: React.ReactNode;
  conversationId?: string | null;
  className?: string;
}

const BasePage: React.FC<BasePageProps> = ({ title, children, className }) => {
  const { chooseActiveConversation, clearActiveConversation } =
    useConversationContext();
  const { conversationId: routeConversationId } = useParams();

  // 监听路由变化
  useEffect(() => {
    if (routeConversationId) {
      // 如果有 conversationId，说明是实例页面
      chooseActiveConversation(routeConversationId);
    } else {
      // 如果没有 conversationId，说明是类页面
      clearActiveConversation();
    }
  }, [routeConversationId]);

  return <div className={className}>{children}</div>;
};

export default BasePage;
