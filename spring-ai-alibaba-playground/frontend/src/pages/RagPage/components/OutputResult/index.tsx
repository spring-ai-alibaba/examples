import React from "react";
import ResponseBubble from "../../../components/ResponseBubble";
import { useStyles } from "../../style";

interface OutputResultProps {
  messages: any[];
  title: string;
}

const OutputResult = ({ messages, title }: OutputResultProps) => {
  const { styles } = useStyles();

  return (
    <div className={`${styles.card} ${styles.resultPanel}`}>
      <div className={styles.resultPanel}>
        <h2 className={styles.panelTitle}>{title}</h2>
        <div className={styles.messagesContainer}>
          <ResponseBubble
            content="你好！我是一个基于RAG技术的智能助手。我可以基于知识库回答你的问题。你可以问我任何问题，我会尽力从知识库中找到相关信息来回答你。"
            timestamp={Date.now()}
          />
        </div>
      </div>
    </div>
  );
};

export default OutputResult;
