import { createStyles } from "antd-style";

export const useStyles = createStyles(({ token, css }) => {
  return {
    container: css`
      display: flex;
      height: calc(100vh - 116px);
      padding: 48px 12px 12px;
      background: ${token.colorBgLayout};
    `,
    pageContainer: css`
      display: flex;
      flex-direction: column;
      padding: 12px;
      background: ${token.colorBgLayout};
      padding: 0;
    `,
    header: css`
      text-align: center;
      padding: 20px 0;
      background: ${token.colorBgContainer};
      border-bottom: 1px solid ${token.colorBorderSecondary};
    `,
    title: css`
      font-size: 24px;
      font-weight: 600;
      color: #f26522;
      margin: 0;
    `,
    subtitle: css`
      font-size: 14px;
      color: ${token.colorTextSecondary};
      margin: 8px 0 0;
    `,
    selectionPanel: css`
      min-width: 232px;
      max-width: 280px;
      height: 100%;
      background: ${token.colorBgContainer};
      border-radius: ${token.borderRadiusLG}px;
      box-shadow: ${token.boxShadow};
      overflow: hidden;
      margin-right: 12px;
    `,
    toolsPanel: css`
      flex: 2;
      height: 100%;
      background: ${token.colorBgContainer};
      border-radius: ${token.borderRadiusLG}px;
      box-shadow: ${token.boxShadow};
      overflow: hidden;
      display: flex;
      flex-direction: column;
      margin-right: 12px;
    `,
    connectPanel: css`
      flex: 3;
      width: 360px;
      height: 100%;
      background: ${token.colorBgContainer};
      border-radius: ${token.borderRadiusLG}px;
      box-shadow: ${token.boxShadow};
      overflow: hidden;
    `,
    panelHeader: css`
      justify-content: space-between;
      align-items: center;
      padding: 12px 16px;
      border-bottom: 1px solid ${token.colorBorderSecondary};
    `,
    panelTitle: css`
      font-size: 16px;
      font-weight: 500;
      color: ${token.colorTextHeading};
    `,
    serverList: css`
      overflow-y: auto;
      height: calc(100% - 50px);
    `,
    serverItem: css`
      display: flex;
      align-items: center;
      padding: 12px 16px;
      cursor: pointer;
      transition: background 0.3s;

      &:hover {
        background: ${token.colorBgTextHover};
      }
    `,
    serverItemSelected: css`
      background: ${token.colorBgTextHover};
    `,
    serverIcon: css`
      margin-right: 4px;
      font-size: 18px;
      width: 24px;
      height: 24px;
      display: flex;
      align-items: center;
      justify-content: center;
    `,
    serverName: css`
      flex: 1;
      font-size: 14px;
    `,
    serverArrow: css`
      color: ${token.colorTextSecondary};
      font-size: 16px;
    `,
    serverInfo: css`
      display: flex;
      align-items: center;
    `,
    // serverDescription: css`
    //   padding: 8px 12px;
    //   font-size: 14px;
    //   color: ${token.colorTextSecondary};
    //   border-bottom: 1px solid ${token.colorBorderSecondary};
    // `,
    toolsSection: css`
      flex: 1;
      display: flex;
      flex-direction: column;
      overflow: hidden;
    `,
    sectionTitle: css`
      padding: 8px 16px;
      font-size: 14px;
      font-weight: 500;
      color: ${token.colorTextSecondary};
    `,
    toolsList: css`
      flex: 1;
      overflow-y: auto;
    `,
    toolItem: css`
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 10px 16px;
      cursor: pointer;
      transition: background 0.3s;

      &:hover {
        background: ${token.colorBgTextHover};
      }
    `,
    toolItemSelected: css`
      background: ${token.colorBgTextHover};
    `,
    toolName: css`
      font-size: 14px;
    `,
    toolArrow: css`
      color: ${token.colorTextSecondary};
      font-size: 16px;
    `,
    tokenInputSection: css`
      padding: 16px;
    `,
    tokenLabel: css`
      font-size: 14px;
      font-weight: 500;
      margin-bottom: 8px;
    `,
    tokenInput: css`
      margin-bottom: 16px;
    `,
    connectButton: css`
      width: 100%;
      border: none;

      &:hover {
        background: #d65415;
      }
    `,
    leftPanel: css`
      width: 33%;
      height: 100%;
      display: flex;
      flex-direction: column;
      padding-right: 16px;
    `,
    rightPanel: css`
      width: 67%;
      height: 100%;
      padding-left: 16px;
    `,
    rightPanelTabs: css`
      height: calc(100% - 50px);
      .ant-tabs-content {
        height: 100%;
      }
      .ant-tabs-tabpane {
        padding: 0 16px;
        height: 100%;
        overflow-y: auto;
      }
      .ant-tabs-nav {
        display: flex;
        justify-content: center;
        width: 100%;
        &::before {
          display: none;
        }
      }
      .ant-tabs-nav-list {
        width: 100%;
        display: flex;
        justify-content: center;
      }
      .ant-tabs-tab {
        flex: 1;
        display: flex;
        justify-content: center;
        margin: 0;
      }
    `,
    card: css`
      background: ${token.colorBgContainer};
      border-radius: ${token.borderRadiusLG}px;
      box-shadow: ${token.boxShadow};
      overflow: auto;
    `,
    codeInfoPanel: css`
      height: 70%;
      margin-bottom: 16px;
      padding: 16px;
      display: flex;
      flex-direction: column;
    `,
    senderWrapper: css`
      background: ${token.colorBgContainer};
      border-radius: ${token.borderRadiusLG}px;
      box-shadow: ${token.boxShadow};
      background-color: "red";
      padding: 16px;
      display: flex;
      flex-direction: column;
      margin-top: auto;
    `,
    sender: css`
      width: 100%;
    `,
    resultPanel: css`
      height: calc(100% - 16px);
      padding: 0 16px 16px;
      display: flex;
      flex-direction: column;
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
      display: flex;
      flex-direction: column;
      gap: 12px;
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
      margin-bottom: 20px;
      height: 100%;
      display: flex;
      flex-direction: column;
      overflow: auto;
    `,
    codeInfoBody: css`
      flex: 1;
      overflow: auto;
      padding: 16px 24px;
    `,
    codeInfoIntro: css`
      font-size: 14px;
      margin-bottom: 16px;
    `,
    codeInfoSteps: css`
      margin-top: 20px;
    `,
    codeInfoStepItem: css`
      margin-bottom: 24px;
    `,
    codeInfoStepTitle: css`
      display: flex;
      justify-content: space-between;
      align-items: center;
      width: 100%;
      margin-bottom: 8px;
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
        margin-bottom: 20px;
      }
    `,
    documentationContainer: css`
      font-size: 14px;
      line-height: 1.6;
      padding: 4px 0;

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

    connectTabs: css`
      width: 100%;
      .ant-tabs-nav {
        margin-bottom: 0;
        padding: 0 16px;
      }
    `,
    configContainer: css`
      padding: 16px;
    `,
    configCode: css`
      position: relative;
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    `,
    copyButton: css`
      position: absolute;
      top: 8px;
      right: 8px;
      z-index: 1;
      background: rgba(255, 255, 255, 0.1);
      border: none;
      color: ${token.colorTextSecondary};

      &:hover {
        background: rgba(255, 255, 255, 0.2);
        color: ${token.colorTextTertiary};
      }
    `,
    clientsSection: css`
      padding: 16px;
    `,
    clientsTitle: css`
      font-size: 16px;
      font-weight: 500;
      margin-bottom: 16px;
      color: ${token.colorTextSecondary};
    `,
    clientsList: css`
      display: flex;
      flex-direction: column;
      gap: 8px;
    `,
    clientItem: css`
      display: flex;
      align-items: center;
      padding: 8px 12px;
      border-radius: ${token.borderRadius}px;
      cursor: pointer;
      transition: background 0.3s;

      &:hover {
        background: ${token.colorBgTextHover};
      }
    `,
    clientIcon: css`
      margin-right: 12px;
      font-size: 16px;
      width: 20px;
      height: 20px;
      display: flex;
      align-items: center;
      justify-content: center;
    `,
    clientName: css`
      flex: 1;
      font-size: 14px;
    `,
    clientArrow: css`
      color: ${token.colorTextSecondary};
      font-size: 14px;
    `,
    toolFormContainer: css`
      padding: 16px;
      display: flex;
      flex-direction: column;
      gap: 16px;
    `,
    toolDescription: css`
      font-size: 14px;
      line-height: 1.5;
      margin-bottom: 16px;
      color: ${token.colorTextSecondary};
    `,
    toolForm: css`
      width: 100%;
    `,
    submitButton: css`
      margin-top: 8px;
      width: 100%;
    `,

    executionResult: css`
      margin-top: 16px;
      border: 1px solid ${token.colorBorder};
      border-radius: 8px;
      overflow: hidden;
    `,
    resultContainer: css`
      height: 100%;
      padding: 16px;
      display: flex;
      flex-direction: column;
      gap: 16px;
    `,
    jsonResultContainer: css`
      flex: 1;
      background: ${token.colorBgContainer};
      border-radius: ${token.borderRadiusLG}px;
      box-shadow: ${token.boxShadowSecondary};
      overflow: hidden;
      display: flex;
      flex-direction: column;
    `,
    resultHeader: css`
      padding: 12px 16px;
      border-bottom: 1px solid ${token.colorBorderSecondary};
      display: flex;
      align-items: center;
      gap: 8px;
    `,
    successHeader: css`
      color: ${token.colorSuccess};
      display: flex;
      align-items: center;
      gap: 8px;
      font-weight: 500;
    `,
    errorHeader: css`
      color: ${token.colorError};
      display: flex;
      align-items: center;
      gap: 8px;
      font-weight: 500;
    `,
    resultContent: css`
      flex: 1;
      overflow: auto;
      position: relative;

      .syntax-highlighter {
        margin: 0 !important;
        padding: 16px !important;
        background: ${token.colorBgContainer} !important;
        font-family: ${token.fontFamilyCode} !important;
        font-size: 13px !important;
        line-height: 1.5 !important;
        height: 100% !important;
      }
    `,
    errorMessage: css`
      padding: 16px;
      color: ${token.colorError};
      font-size: 14px;
      line-height: 1.5;
    `,

    // Loading overlay styles
    loadingOverlay: css`
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background-color: rgba(0, 0, 0, 0.5);
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      z-index: 1000;
    `,
    loadingText: css`
      color: white;
      margin-top: 16px;
      font-size: 16px;
    `,
    introContainer: css`
      /* padding: 0 16px; */
      h2 {
        color: ${token.colorTextHeading};
        margin-bottom: 16px;
      }
      h3 {
        color: ${token.colorTextHeading};
        margin: 16px 0 8px;
      }
      p,
      li {
        color: ${token.colorText};
        line-height: 1.6;
      }
      ul,
      ol {
        padding-left: 20px;
      }
    `,
  };
});
