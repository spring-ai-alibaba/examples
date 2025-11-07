import React from "react";
import { useParams } from "react-router-dom";
import ChatLandingView from "./components/ChatLandingView";
import ChatConversationView from "./components/ChatConversationView";

const ChatPage: React.FC = () => {
  const { conversationId } = useParams<{ conversationId?: string }>();

  if (conversationId) {
    return <ChatConversationView conversationId={conversationId} />;
  } else {
    return <ChatLandingView />;
  }
};

export default ChatPage;
