import React from "react";
import { useParams } from "react-router-dom";
import RagConversationView from "./components/RagConversationView";
import RagLandingView from "./components/RagLandingView";

const RagPage: React.FC = () => {
  const { conversationId } = useParams<{ conversationId?: string }>();

  if (conversationId) {
    return <RagConversationView conversationId={conversationId} />;
  } else {
    return <RagLandingView />;
  }
};

export default RagPage;
