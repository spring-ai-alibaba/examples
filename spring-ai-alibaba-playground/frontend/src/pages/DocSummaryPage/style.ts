import { createStyles } from "antd-style";

export const useStyle = createStyles(({ token }) => ({
  codeBlock: {
    background: token.colorBgContainer,
    borderRadius: token.borderRadius,
    padding: token.padding,
    margin: token.margin,
  },
  docContainer: {
    padding: token.padding,
    display: "flex",
    flexDirection: "column",
    gap: token.marginSM,
  },
  titleSection: {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    gap: "8px",
    marginBottom: "32px",
  },
  mainTitle: {
    fontSize: "28px",
    fontWeight: "600",
    background: `linear-gradient(120deg, ${token.colorPrimary}, ${token.colorPrimaryActive})`,
    backgroundClip: "text",
    WebkitBackgroundClip: "text",
    WebkitTextFillColor: "transparent",
    marginBottom: "4px",
  },
  subTitle: {
    fontSize: "15px",
    color: token.colorTextSecondary,
    fontWeight: "normal",
    letterSpacing: "0.5px",
  },
  uploadArea: {
    border: `1px dashed ${token.colorBorder}`,
    borderRadius: token.borderRadiusLG,
    backgroundColor: token.colorBgContainer,
    padding: token.paddingLG,
    textAlign: "center",
    cursor: "pointer",
    transition: `border-color ${token.motionDurationMid}`,
    "&:hover": {
      borderColor: token.colorPrimary,
    },
  },
  uploadIcon: {
    fontSize: 48,
    color: token.colorTextSecondary,
    marginBottom: token.marginMD,
  },
  uploadHint: {
    color: token.colorTextSecondary,
    marginTop: token.marginMD,
  },
  fileHistoryItem: {
    margin: "4px 0",
    padding: "12px 16px !important",
    borderRadius: token.borderRadiusLG,
    transition: `all ${token.motionDurationMid}`,
    border: "1px solid transparent",
    backgroundColor: token.colorBgContainer,
    boxShadow: "0 1px 2px rgba(0, 0, 0, 0.03)",

    ".ant-list-item-meta-title": {
      marginBottom: "4px",
    },

    ".file-title": {
      fontSize: "15px",
      transition: `color ${token.motionDurationMid}`,
    },

    ".ant-avatar": {
      transition: `all ${token.motionDurationMid}`,
      transform: "scale(0.9)",
    },

    ".ant-space": {
      marginTop: "2px",
    },

    ".ant-typography": {
      fontSize: "13px",
    },

    "&:hover": {
      backgroundColor: `${token.colorBgContainer}`,
      transform: "translateY(-1px)",
      border: `1px solid ${token.colorPrimaryBorder}`,
      boxShadow: "0 3px 8px rgba(0, 0, 0, 0.08)",

      ".file-title": {
        color: token.colorPrimary,
      },

      ".ant-avatar": {
        transform: "scale(1)",
      },
    },
  },
  actionButtons: {
    display: "flex",
    gap: "8px",
  },
  actionButton: {
    padding: "4px 12px",
    borderRadius: token.borderRadius,
    fontSize: "13px",
    height: "28px",
    transition: `all ${token.motionDurationMid}`,

    "&.view-content": {
      color: token.colorPrimary,
      borderColor: token.colorPrimary,

      "&:hover": {
        color: token.colorPrimaryHover,
        borderColor: token.colorPrimaryHover,
        backgroundColor: token.colorPrimaryBg,
      },
    },

    "&.start-chat": {
      backgroundColor: token.colorPrimary,
      borderColor: token.colorPrimary,
      color: "#fff",

      "&:hover": {
        backgroundColor: token.colorPrimaryHover,
        borderColor: token.colorPrimaryHover,
      },
    },
  },
}));
