import { createStyles } from "antd-style";

export const useStyles = createStyles(({ token, css }) => {
  return {
    container: css`
      display: flex;
      height: calc(100vh - 116px);
      padding: 48px 12px 12px;
      background: ${token.colorBgLayout};
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
      height: 100%;
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
      padding: 16px;
      display: flex;
      flex-direction: column;
      margin-top: auto;
    `,
    sender: css`
      width: 100%;
    `,
    resultPanel: css`
      height: calc(100% - 17px);
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
    codeInfoContainer: css`
      margin-bottom: 8px;
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
  };
});
