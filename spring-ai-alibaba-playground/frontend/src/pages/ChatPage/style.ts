import { createStyles } from "antd-style";

export const useStyle = createStyles(({ token, css }) => {
  return {
    chat: css`
      height: 100%;
      margin: 0 auto;
      box-sizing: border-box;
      display: flex;
      flex-direction: column;
      padding: ${token.paddingLG}px;
      padding-bottom: 0;
      margin: 0 8%;
      gap: 16px;
      transition: all 0.3s cubic-bezier(0.34, 0.69, 0.1, 1);
      position: relative;
    `,
    messages: css`
      flex: 1;
      margin-bottom: 16px;
      padding-bottom: 40px;
      overflow-y: auto;
    `,
    container: css`
      display: flex;
      flex-direction: column;
      margin: 40px 0 80px;
      height: calc(100vh - 224px);
      width: 100%;
      position: relative;
      overflow: hidden;
    `,
    messagesContainer: css`
      flex: 1;
      overflow-y: auto;
      padding: 16px;
      display: flex;
      flex-direction: column;
      gap: 16px;
      scroll-behavior: smooth;
      height: calc(100vh - 200px);
      &::-webkit-scrollbar {
        width: 6px;
      }
      &::-webkit-scrollbar-track {
        background: transparent;
      }
      &::-webkit-scrollbar-thumb {
        background: ${token.colorTextTertiary};
        border-radius: 3px;
      }
      &::-webkit-scrollbar-thumb:hover {
        background: ${token.colorTextSecondary};
      }
    `,
    sender: css`
      box-shadow: ${token.boxShadow};
      margin-bottom: 50px;
      position: relative;
      border-radius: 12px;
      width: 100%;

      .ant-sender-textarea {
        transition: height 0.3s ease, max-height 0.3s ease;
        min-height: 56px;
      }
      .ant-sender-content {
        background: ${token.colorBgElevated};
      }
    `,
    actionButtons: css`
      display: flex;
      gap: 8px;
      margin-bottom: 8px;
    `,
    chatPageSender: css`
      position: fixed;
      bottom: 12px;
      padding: 0;
      margin-top: 2px;
    `,
    senderContainer: css`
      position: fixed;
      bottom: 32px;
      left: 294px;
      width: calc(100% - 315px);
    `,
    senderContainerCollapsed: css`
      position: fixed;
      bottom: 32px;
      width: calc(100% - 32px);
      padding: 0 16px;
    `,
    landingContainer: css`
      display: flex;
      flex-direction: column;
      height: 100%;
      width: calc(100% - 12px);
    `,
    landingContent: css`
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      margin-bottom: 50px;
    `,
    landingSender: css`
      position: fixed;
      bottom: 80px;
      left: 294px;
      width: calc(100% - 315px);
    `,
    landingSenderCollapsed: css`
      position: fixed;
      bottom: 80px;
      left: 50%;
      transform: translate(-50%);
    `,
    placeholder: css`
      padding-top: 32px;
    `,
  };
});
