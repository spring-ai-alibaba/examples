import { createStyles } from "antd-style";

export const useStyles = createStyles(({ token, css }) => ({
  container: css`
    display: flex;
    height: calc(100vh - 116px);
    padding: 48px 12px 12px;
    background: ${token.colorBgLayout};

    /* 全局滚动条隐藏 */
    * {
      &::-webkit-scrollbar {
        width: 0 !important;
        height: 0 !important;
        display: none !important;
        background: transparent !important;
      }
      -ms-overflow-style: none !important;
      scrollbar-width: none !important;
      overflow-x: hidden !important;
    }
  `,
  leftPanel: css`
    width: 33%;
    height: 100%;
    display: flex;
    flex-direction: column;
  `,
  rightPanel: css`
    width: 67%;
    height: 100%;
    padding-left: 8px;
  `,
  card: css`
    background: ${token.colorBgContainer};
    border-radius: ${token.borderRadiusLG}px;
    box-shadow: ${token.boxShadow};
    height: calc(100% - 16px);
  `,

  senderWrapper: css`
    background: ${token.colorBgContainer};
    border-radius: ${token.borderRadiusLG}px;
    box-shadow: ${token.boxShadow};
    padding: 16px;
  `,
  sender: css`
    width: 100%;
    margin-bottom: 6px;
  `,
  resultPanel: css`
    /* height: calc(100% - 16px); */
    padding: 0 16px 16px;
    display: flex;
    flex-direction: column;
  `,
  panelTitle: css`
    font-size: 16px;
    font-weight: 500;
    color: ${token.colorTextHeading};
    margin-bottom: 16px;
  `,
  messageForm: css`
    display: flex;
    flex-direction: column;
    height: calc(100% - 32px);
  `,
  messageInput: css`
    flex-grow: 1;
    margin-bottom: 12px;
    border-radius: ${token.borderRadius}px;
    border: 1px solid ${token.colorBorder};
    padding: 12px;
    resize: none;
    font-size: 14px;
    outline: none;

    &:focus {
      border-color: ${token.colorPrimary};
      box-shadow: 0 0 0 2px ${token.colorPrimaryBg};
    }
  `,
  sendButton: css`
    height: 40px;
    background: ${token.colorPrimary};
    color: white;
    font-size: 14px;
    border: none;
    border-radius: ${token.borderRadius}px;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      background: ${token.colorPrimaryHover};
    }

    &:disabled {
      background: ${token.colorBgContainerDisabled};
      color: ${token.colorTextDisabled};
      cursor: not-allowed;
    }
  `,
  messagesContainer: css`
    /* display: flex; */
    /* flex-direction: column; */
    /* gap: 12px; */
    overflow: scroll;
    height: 100%;
  `,
  emptyMessages: css`
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    color: ${token.colorTextSecondary};
    font-size: 14px;
  `,
  userMessage: css`
    align-self: flex-end;
    max-width: 80%;
    background: ${token.colorPrimaryBg};
    border-radius: ${token.borderRadiusLG}px;
    padding: 12px;
  `,
  botMessage: css`
    align-self: flex-start;
    max-width: 80%;
    background: ${token.colorBgLayout};
    border-radius: ${token.borderRadiusLG}px;
    padding: 12px;
  `,
  messageSender: css`
    font-size: 13px;
    font-weight: 500;
    margin-bottom: 6px;
    color: ${token.colorTextSecondary};
  `,
  messageText: css`
    font-size: 14px;
    white-space: pre-wrap;
    word-break: break-word;
  `,
  messageTime: css`
    font-size: 12px;
    color: ${token.colorTextDescription};
    margin-top: 4px;
  `,

  /* CodeInfo 组件样式 */
  codeInfoContainer: css`
    /* margin-bottom: 8px; */
    height: 100%;
    display: flex;
    flex-direction: column;
    overflow: auto;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    transition: all 0.3s ease;

    /* .ant-card-head {
        border-bottom: 1px solid ${token.colorBorderSecondary}40;
        padding: 12px 24px;
      } */

    .ant-card-head-title {
      font-size: 18px;
      font-weight: 600;
      color: ${token.colorTextHeading};
    }

    &::-webkit-scrollbar {
      width: 0 !important;
      height: 0 !important;
      background: transparent !important;
    }

    .ant-card-body {
      &::-webkit-scrollbar {
        width: 0 !important;
        height: 0 !important;
        background: transparent !important;
      }
      overflow-x: hidden !important;
    }
  `,
  codeInfoBody: css`
    flex: 1;

    overflow: auto;
    padding: 16px 24px;

    &::-webkit-scrollbar {
      display: none;
    }
    -ms-overflow-style: none;
    scrollbar-width: none;
  `,
  codeInfoIntro: css`
    font-size: 14px;
    margin-bottom: 16px;
  `,
  codeInfoSteps: css`
    overflow: hidden;
  `,
  codeInfoStepItem: css`
    /* margin-bottom: 24px; */
  `,
  codeInfoStepTitle: css`
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    /* margin-bottom: 8px; */
  `,
  codeInfoTitleText: css`
    font-weight: 500;
    margin-right: 8px;
  `,
  codeInfoIcon: css`
    font-size: 16px;
    margin-left: 8px;
    color: ${token.colorPrimary};
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: scale(1.2);
      color: ${token.colorPrimaryActive};
    }
  `,
  codeInfoStepDesc: css`
    font-size: 14px;
    margin-top: 4px;
  `,
  codePreview: css`
    background-color: #f5f5f5;
    padding: 14px;
    border-radius: 8px;
    font-family: "SF Mono", "Monaco", "Menlo", "Consolas", monospace;
    font-size: 13px;
    white-space: pre-wrap;
    overflow-x: auto;
    max-height: 600px;
    overflow-y: auto;
    line-height: 1.5;
    color: #333;
    border: 1px solid #e8e8e8;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  `,
  codeInfoTabs: css`
    width: 100%;
    .ant-tabs-nav {
      margin-bottom: 12px;
    }

    .ant-tabs-content {
      overflow: auto;
      overflow-x: hidden !important;

      &::-webkit-scrollbar {
        width: 0 !important;
        height: 0 !important;
        background: transparent !important;
      }
      -ms-overflow-style: none;
      scrollbar-width: none;
    }

    .ant-tabs-tabpane {
      overflow: auto;
      overflow-x: hidden !important;

      &::-webkit-scrollbar {
        width: 0 !important;
        height: 0 !important;
        background: transparent !important;
      }
      -ms-overflow-style: none;
      scrollbar-width: none;
    }
  `,
  documentationContainer: css`
    font-size: 14px;
    line-height: 1.6;
    padding: 4px 0;
    overflow: auto;

    &::-webkit-scrollbar {
      display: none;
    }
    -ms-overflow-style: none;
    scrollbar-width: none;

    h4 {
      margin-top: 24px;
      margin-bottom: 12px;
      font-weight: 600;
      color: ${token.colorTextHeading};
    }

    h4:first-child {
      margin-top: 0;
    }

    ul {
      padding-left: 20px;
      margin: 12px 0;
    }

    li {
      margin-bottom: 8px;
    }

    code {
      background: ${token.colorFillTertiary};
      padding: 2px 4px;
      border-radius: 4px;
      font-family: "SF Mono", "Monaco", "Menlo", "Consolas", monospace;
      font-size: 0.9em;
    }
  `,
  linkList: css`
    list-style-type: none;
    padding-left: 0;
    margin: 12px 0;

    li {
      margin-bottom: 12px;
      padding: 0;
      border-radius: 4px;
    }
  `,
  linkCard: css`
    background: ${token.colorBgContainer};
    border: 1px solid ${token.colorBorderSecondary};
    border-radius: 8px;
    transition: all 0.3s;
    box-shadow: 0 2px 0 rgba(0, 0, 0, 0.02);

    &:hover {
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
      border-color: ${token.colorPrimaryBorderHover};
      transform: translateY(-1px);
    }
  `,
  externalLink: css`
    display: flex;
    align-items: center;
    color: ${token.colorPrimary};
    text-decoration: none;
    transition: color 0.3s;
    padding: 12px 16px;
    border-radius: 8px;
    justify-content: space-between;

    &:hover {
      color: ${token.colorPrimaryHover};
    }

    span {
      flex: 1;
      font-size: 15px;
    }
  `,
  linkIconWrapper: css`
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: ${token.colorPrimaryBg};
    width: 28px;
    height: 28px;
    border-radius: 50%;
    margin-left: 12px;
    transition: all 0.3s;

    .anticon {
      font-size: 14px;
    }

    &:hover {
      background-color: ${token.colorPrimaryBgHover};
    }
  `,

  // New document link button styles
  docLinkButton: css`
    display: flex !important;
    align-items: center !important;
    justify-content: space-between !important;
    text-align: left !important;
    height: auto !important;
    padding: 16px 20px !important;
    border-radius: 8px !important;
    border: 1px solid ${token.colorBorderSecondary} !important;
    background: ${token.colorBgContainer} !important;
    box-shadow: 0 2px 0 rgba(0, 0, 0, 0.02) !important;
    transition: all 0.3s !important;

    &:hover {
      transform: translateY(-2px) !important;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
      border-color: ${token.colorPrimaryBorderHover} !important;
    }

    .link-icon {
      font-size: 18px !important;
      color: ${token.colorPrimary} !important;
      margin-right: 16px !important;
    }

    .arrow-icon {
      font-size: 16px !important;
      color: ${token.colorTextSecondary} !important;
      transition: transform 0.3s !important;
    }

    &:hover .arrow-icon {
      transform: translateX(4px) !important;
      color: ${token.colorPrimary} !important;
    }
  `,
  docLinkContent: css`
    display: flex;
    flex-direction: column;
    flex: 1;
    min-width: 0;
  `,
  docLinkTitle: css`
    font-size: 16px;
    font-weight: 500;
    color: ${token.colorTextHeading};
    margin-bottom: 4px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  `,
  docLinkDescription: css`
    font-size: 14px;
    color: ${token.colorTextSecondary};
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  `,
}));
