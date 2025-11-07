import { createStyles } from "antd-style";

export const useStyle = createStyles(({ token, css }) => {
  return {
    menuCollapsed: css`
      width: 0;
      min-width: 0;
      padding: 0;
      overflow: hidden;
      text-overflow: ellipsis;
      display: none;

      transform: translateX(-999px);
      .functionMenu,
      .chooseModel,
      .conversationsContainer {
        display: none;
      }
    `,
    menu: css`
      background: ${token.colorBgLayout}80;
      max-width: 260px;
      min-width: 0px;
      height: 100%;
      display: flex;
      flex-direction: column;
      padding: 0 12px;
      box-sizing: border-box;
      overflow-y: auto;
      overflow-x: hidden;
      transition: width 0.8s ease-in-out;
    `,
    userProfile: css`
      display: flex;
      height: 30px;
      width: 100%;
      justify-content: flex-end;
      gap: 12px;
      align-items: center;
      padding: 16px 0;
      border-bottom: 1px solid ${token.colorBorderSecondary};
      margin-bottom: 16px;
      img {
        height: 121px;
      }
    `,
    functionMenu: css`
      display: flex;
      flex-direction: column;
      width: 100%;
      gap: 8px;
      margin-bottom: 8px;
      padding: 4px 0;
      border-bottom: 1px solid ${token.colorBorderSecondary};
    `,
    functionMenuItem: css`
      padding: 8px 16px;
      cursor: pointer;
      border-radius: ${token.borderRadius}px;

      &:hover {
        background-color: ${token.colorBgTextHover};
      }

      .anticon {
        font-size: 18px;
        margin-right: 8px;
      }

      span {
        font-size: 15px;
      }
    `,
    chooseModel: css`
      display: flex;
      flex-direction: column;
      color: rgba(0, 0, 0, 0.88);
      margin: 0 0 12px;
      gap: 8px;
    `,
    conversationsContainer: css`
      display: flex;
      flex-direction: column;
      gap: 8px;
      flex: 1;
      overflow: hidden;
      position: relative;
    `,
    conversationsScrollContainer: css`
      height: 100%;
      overflow: auto;
      padding-right: 2px;
      margin-bottom: 48px;
    `,
    conversationItem: css`
      display: flex;
      align-items: center;
      padding: 8px 12px;
      margin: 2px 0;
      cursor: pointer;
      border-radius: ${token.borderRadius}px;
      transition: background-color 0.2s ease;

      &:hover {
        background-color: ${token.colorBgTextHover};
      }

      &.active {
        background-color: rgba(22, 119, 255, 0.1);
      }
    `,
    conversationTitle: css`
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      flex: 1;
    `,
    actionButtonsContainer: css`
      display: flex;
      gap: 4px;
      visibility: hidden;

      .active & {
        visibility: visible;
      }

      .conversationItem:hover & {
        visibility: visible;
      }
    `,
    editButton: css`
      &.ant-btn {
        padding: 0 4px;
        min-width: 24px;
        height: 24px;
      }
    `,
    deleteButton: css`
      &.ant-btn {
        padding: 0 4px;
        min-width: 24px;
        height: 24px;
      }
    `,
    titleEditContainer: css`
      display: flex;
      align-items: center;
      width: 100%;
      gap: 4px;
    `,
    titleInput: css`
      flex: 1;
      font-size: 14px;
    `,
    titleEditButton: css`
      padding: 0 4px;
      font-size: 12px;
      height: 22px;
      min-width: 22px;
    `,
    collapsedMenuBtn: css`
      position: fixed;
      top: 12px;
      left: 12px;
      z-index: 1000;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: ${token.boxShadowSecondary};
      cursor: pointer;
      opacity: 1;
      transform: scale(1);

      &:hover {
        transform: scale(1.05);
      }
    `,
    bottomLinkWrapper: css`
      position: absolute;
      left: 20px;
      bottom: 8px;
      z-index: 10;
    `,
    menuTitle: css`
      color: ${token.colorTextTertiary};
      font-size: 14px;
    `,
  };
});
