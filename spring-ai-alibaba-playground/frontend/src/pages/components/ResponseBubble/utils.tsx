import React from "react";
import PureText from "./components/PureText";

export const getMarkdownRenderConfig = (styles: Record<string, string>) => {
  const TextWithoutMargin = ({ children }) => {
    <div className={styles.textWithoutMargin}>{children}</div>;
  };

  return {
    blockquote: ({ children }) => (
      <blockquote className={styles.thinkBlock}>{children}</blockquote>
    ),
    code: ({ children, className }) => (
      <code className={styles.codeInline}>{children}</code>
    ),
    pre: ({ children }) => <pre className={styles.codeBlock}>{children}</pre>,
    p: PureText,
    // h3: ({ children }) => (
    //   <PureText style={{ fontSize: "18px", fontWeight: 800 }}>
    //     {children}
    //   </PureText>
    // ),
    // h4: ({ children }) => (
    //   <PureText style={{ fontSize: "16px", fontWeight: 800 }}>
    //     {children}
    //   </PureText>
    // ),
    h5: ({ children }) => (
      <PureText style={{ fontSize: "14px", fontWeight: 800 }}>
        {children}
      </PureText>
    ),
    h6: ({ children }) => (
      <PureText style={{ fontSize: "12px", fontWeight: 800 }}>
        {children}
      </PureText>
    ),
    ul: ({ children }) => (
      <PureText style={{ paddingLeft: "24px", margin: 0 }}>{children}</PureText>
    ),
    ol: ({ children }) => (
      <PureText style={{ paddingLeft: "24px", margin: 0 }}>{children}</PureText>
    ),
    li: ({ children }) => <PureText style={{ margin: 0 }}>{children}</PureText>,
    think: ({ children }) => {
      return children;
    },
    tool: ({ children }) => {
      return <div className={styles.toolTag}>{children}</div>;
    },
  };
};

const THINK_TAG_REGEX = /<think>([\s\S]*?)<\/think>/g;
// 缓存防止重复
const processedContentCache = new Map<string, string>();
// 直接由上游处理，暂时不需要这个方法了
export const getSseTagProcessor = () => {
  let isProcessing = false;

  const sseTagProcessor = (content: string, timestamp: string) => {
    try {
      isProcessing = true;
      const cachedResult = processedContentCache.get(timestamp);
      if (cachedResult) {
        return cachedResult;
      }

      let result = content;

      // 使用split来计数，性能更好
      const openTagCount = result.split("<think>").length - 1;
      const closeTagCount = result.split("</think>").length - 1;

      // 如果标签未配对完成，先保存当前内容并返回原始内容
      if (openTagCount !== closeTagCount) {
        return content;
      }

      const matches = result.matchAll(THINK_TAG_REGEX);

      const thinkContents: string[] = [];
      let lastIndex = 0;
      let processedResult = "";

      for (const match of matches) {
        const [fullMatch, thinkContent] = match;
        processedResult += result.slice(lastIndex, match.index);
        thinkContents.push(thinkContent.trim());
        lastIndex = (match.index || 0) + fullMatch.length;
      }

      // 添加剩余内容
      processedResult += result.slice(lastIndex);

      if (thinkContents.length > 0) {
        const combinedThinkContent = thinkContents.join("\n\n");
        processedResult = `> ${combinedThinkContent}\n\n${processedResult}`;
      }

      processedContentCache.set(timestamp, processedResult);
      return processedResult;
    } catch (err) {
      console.error("Failed to process content:", err);
      return content;
    } finally {
      isProcessing = false;
    }
  };

  return sseTagProcessor;
};
