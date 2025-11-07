import React, { useMemo } from "react";
import { useStyle } from "./style";
import ReactMarkdown, { Components } from "react-markdown";
import remarkGfm from "remark-gfm";
import rehypeRaw from "rehype-raw";
import { getMarkdownRenderConfig } from "./utils";
import MessageFooter from "./components/CopyButton";

interface ResponseBubbleProps {
  content: string;
  timestamp: number;
  isError?: boolean;
  footer?: () => React.ReactNode;
  onReload?: () => void;
}

const ResponseBubble: React.FC<ResponseBubbleProps> = ({
  content,
  timestamp,
  isError = false,
  footer = null,
  onReload,
}) => {
  const { styles } = useStyle();
  const messageId = `${timestamp}`;

  const markdownRenderConfig = useMemo(
    () => getMarkdownRenderConfig(styles),
    [styles]
  );

  const defaultFooter = () => (
    <MessageFooter value={content} onReload={onReload} />
  );

  return (
    <div className={styles.botMessage} key={"responseBubble" + messageId}>
      <div className={styles.messageSender}>AI</div>
      <div className={styles.messageText}>
        <ReactMarkdown
          remarkPlugins={[remarkGfm]}
          rehypePlugins={[rehypeRaw]}
          components={markdownRenderConfig as Components}
        >
          {content}
        </ReactMarkdown>
      </div>
      <div className={styles.messageTime}>
        {new Date(timestamp).toLocaleString()}
      </div>
      {!isError && (footer?.() ?? defaultFooter())}
    </div>
  );
};

export default ResponseBubble;
