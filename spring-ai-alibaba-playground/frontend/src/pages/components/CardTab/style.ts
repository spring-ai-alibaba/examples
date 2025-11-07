import { createStyles } from "antd-style";

export const useStyles = createStyles(({ token, css }) => {
  return {
    cardTab: css`
      margin-bottom: 8px;
      height: 100%;
      display: flex;
      flex-direction: column;
      overflow: auto;
      border-radius: 12px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
      transition: all 0.3s ease;

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
        padding-top: 8px;
        &::-webkit-scrollbar {
          width: 0 !important;
          height: 0 !important;
          background: transparent !important;
          /* padding-top: 4px; */
        }
        overflow-x: hidden !important;
        overflow-y: auto !important;
      }
    `,

    cardTabItem: css`
      font-weight: 500;
      /* padding: 4px 6px; */
      padding: 4px 6px;
      cursor: pointer;
      border-radius: 8px;
      transition: all 0.3s ease;
      &:hover {
        color: ${token.colorPrimary};
        background: ${token.colorPrimary}10;
      }
    `,

    activeCardTabItem: css`
      padding: 4px 6px;
      border-radius: 8px;
      color: ${token.colorPrimary};
      background: ${token.colorPrimary}15;
      font-weight: 600;
    `,

    cardTabContent: css`
      margin-top: 16px;
      min-height: 200px;
      overflow: hidden;
    `,
  };
});
