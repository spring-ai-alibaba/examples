import { createStyles } from "antd-style";

export const useStyle = createStyles(({ token }) => ({
  botMessage: {
    fontFamily: token.fontFamilyCode,
    display: "flex",
    flexDirection: "column",
    padding: token.padding,
    marginBottom: token.margin,
    backgroundColor: token.colorBgElevated,
    borderRadius: token.borderRadius,
    width: "calc(100% - 32px)",
    alignSelf: "flex-start",
    marginRight: "auto",
    transition: "all 0.2s ease-out",
    willChange: "transform, height",
    transform: "translateZ(0)",
    backfaceVisibility: "hidden",
  },
  messageSender: {
    fontWeight: "bold",
    marginBottom: token.marginXXS,
  },
  messageText: {
    whiteSpace: "pre-wrap",
    wordBreak: "break-word",
    transition: "all 1s ease-out",
    willChange: "contents, height",
    transform: "translateZ(0)",
    textOverflow: "ellipsis",
    overflow: "hidden",
    display: "-webkit-box",
    WebkitBoxOrient: "vertical",

    pre: {
      background: token.colorBgContainer,
      borderRadius: token.borderRadius,
      padding: token.padding,
      margin: `${token.marginXS}px 0`,
      overflow: "auto",

      "&::-webkit-scrollbar": {
        width: "6px",
        height: "6px",
      },

      "&::-webkit-scrollbar-track": {
        background: "transparent",
      },

      "&::-webkit-scrollbar-thumb": {
        background: token.colorTextTertiary,
        borderRadius: "3px",
      },

      "&::-webkit-scrollbar-thumb:hover": {
        background: token.colorTextSecondary,
      },
    },

    code: {
      fontFamily: token.fontFamilyCode,
      background: token.colorBgContainer,
      padding: "2px 4px",
      borderRadius: token.borderRadiusSM,
    },
  },
  messageTime: {
    fontSize: token.fontSizeSM,
    color: token.colorTextSecondary,
    marginTop: token.marginXXS,
    alignSelf: "flex-end",
  },
  thinkBlock: {
    borderLeft: `4px solid ${token.colorPrimary}`,
    paddingLeft: token.padding,
    margin: `${token.marginXS}px 0`,
    color: token.colorTextSecondary,
    fontStyle: "italic",
    transition: "all 1s ease-out",
    willChange: "contents, height",
    transform: "translateZ(0)",
  },
  codeBlock: {
    "&::-webkit-scrollbar": {
      width: "6px",
      height: "6px",
    },
    "&::-webkit-scrollbar-track": {
      background: "transparent",
    },
    "&::-webkit-scrollbar-thumb": {
      background: token.colorTextTertiary,
      borderRadius: "3px",
    },
    "&::-webkit-scrollbar-thumb:hover": {
      background: token.colorTextSecondary,
    },
  },
  codeInline: {},
  textWithoutMargin: {
    margin: 0,
    fontSize: "16px",
    lineHeight: 1.8,
  },
  toolTag: {
    backgroundColor: token.colorBgContainer,
    borderRadius: "4px",
    borderLeft: "4px solid #1677ff",
    padding: "12px 16px",
    fontSize: token.fontSizeSM,
    color: token.colorText,
    lineHeight: 1.6,
    position: "relative",
    margin: "8px 0",
    display: "block",
    "&::before": {
      content: '"⚙️"',
      marginRight: "8px",
      fontSize: "14px",
    },
  },
}));
