import React from "react";
import { useStyles } from "../../style";
import { useParams } from "react-router-dom";
import { InputResultProps } from "../../types";
import ResponseBubble from "../../../components/ResponseBubble";
import RequestBubble from "../../../components/RequestBubble";

const OutputResult: React.FC<InputResultProps> = ({
  messages = [],
  title = "Conversation",
}) => {
  const { styles } = useStyles();
  const { conversationId } = useParams<{ conversationId?: string }>();

  const welcomeMessage =
    "你好！我可以帮你查询全球各地的天气信息。例如，你可以问我北京今天的天气怎么样？或上海明天会下雨吗？或纽约本周末的气温如何？请告诉我你想了解哪个地区的天气信息。";

  return (
    <div className={`${styles.card} ${styles.resultPanel}`}>
      <h2 className={styles.panelTitle}>{title}</h2>

      <div className={styles.messagesContainer}>
        {!conversationId ? (
          <ResponseBubble content={welcomeMessage} timestamp={Date.now()} />
        ) : (
          messages.map((message) =>
            message.sender === "user" ? (
              <RequestBubble
                key={message.id}
                content={message.text}
                timestamp={
                  message.timestamp instanceof Date
                    ? message.timestamp.getTime()
                    : message.timestamp
                }
              />
            ) : (
              <ResponseBubble
                key={message.id}
                content={message.text}
                timestamp={
                  message.timestamp instanceof Date
                    ? message.timestamp.getTime()
                    : message.timestamp
                }
                isError={message.isError}
              />
            )
          )
        )}
      </div>
    </div>
  );
};

export default OutputResult;
