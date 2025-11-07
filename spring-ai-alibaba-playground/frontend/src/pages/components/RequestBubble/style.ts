import { createStyles } from "antd-style";

export const useStyle = createStyles(({ token }) => ({
  userMessage: {
    fontFamily: token.fontFamilyCode,
    display: "flex",
    flexDirection: "column",
    padding: token.padding,
    marginBottom: token.margin,
    backgroundColor: token.colorPrimaryBg,
    borderRadius: token.borderRadius,
    maxWidth: "80%",
    alignSelf: "flex-end",
    marginLeft: "auto",
    willChange: "transform, opacity",
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
  },
  messageTime: {
    fontSize: token.fontSizeSM,
    color: token.colorTextSecondary,
    marginTop: token.marginXXS,
    alignSelf: "flex-end",
  },
}));
