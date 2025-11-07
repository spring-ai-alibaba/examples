import { createStyles } from "antd-style";

export const useStyles = createStyles(({ token, css }) => ({
  container: css`
    min-height: calc(100vh - 116px);
    padding: 48px 12px 12px;
    display: flex;
    flex-direction: column;
    gap: 20px;
  `,
  inputArea: css`
    position: sticky;
    top: 0;
    z-index: 100;
    background: ${token.colorBgContainer};
    padding: 16px 0;
    border-bottom: 1px solid ${token.colorBorderSecondary};
  `,
  sender: css`
    width: 100%;
  `,
  messagesContainer: css`
    flex: 1;
    overflow-y: auto;
    padding: 16px;
    display: flex;
    flex-direction: column;
    gap: 16px;
    scroll-behavior: smooth;
    margin-top: 24px;
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
  messageItem: css`
    width: 100%;
    display: flex;
    flex-direction: column;
  `,
  masonryGrid: css`
    display: flex;
    margin-left: -16px;
    width: auto;
  `,
  masonryColumn: css`
    padding-left: 16px;
    margin-bottom: 64px;
    background-clip: padding-box;
  `,
  imageGallery: css`
    display: flex;
    flex-direction: column;
    gap: 16px;
    margin-bottom: 32px;
    align-items: flex-start;
    width: 100%;
  `,
  imageCard: css`
    width: 350px;
    height: 350px;
    box-shadow: ${token.boxShadowSecondary};
    border-radius: ${token.borderRadiusLG}px;
    overflow: hidden;
    background-color: ${token.colorBgElevated};
    transition: transform 0.3s ease;
    margin-left: 0;
    margin-right: auto;

    &:hover {
      transform: translateY(-4px);
    }
  `,
  overlay: css`
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 2;
    background: linear-gradient(
      to bottom,
      rgba(0, 0, 0, 0.1),
      rgba(0, 0, 0, 0.7)
    );
    display: flex;
    flex-direction: column;
    justify-content: flex-end;
    padding: 20px;
    opacity: 0;
    visibility: hidden;
    transition: all 0.3s ease;
  `,
  prompt: css`
    color: ${token.colorWhite};
    margin: 0 0 16px 0;
    font-size: ${token.fontSizeSM}px;
    line-height: 1.5;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
  `,
  useButton: css`
    align-self: flex-start;
    background: ${token.colorPrimary};
    border: none;
    z-index: 3;

    &:hover {
      background: ${token.colorPrimaryHover} !important;
    }
  `,
}));
