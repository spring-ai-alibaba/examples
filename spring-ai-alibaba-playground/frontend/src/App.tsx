import {
  LinkOutlined,
  GithubOutlined,
  BulbOutlined,
  BulbFilled,
  WechatWorkOutlined,
  SnippetsOutlined,
  FormOutlined,
  DingtalkOutlined,
} from "@ant-design/icons";
import {
  Button,
  Tooltip,
  Layout,
  theme,
  ConfigProvider,
  App as AntdApp,
} from "antd";
import { useStyle } from "./style";
import { Space } from "antd";
import React, { useEffect, useState } from "react";
import {
  HashRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import { pageComponents } from "./const";
import FunctionMenu from "./pages/components/FunctionMenu";
import { useTheme } from "./hooks/useTheme";
import { ThemeProvider } from "antd-style";
import TipsModalComponent from "./pages/components/TipsModal";

// 定义深色主题和浅色主题的算法
import darkAlgorithm from "antd/es/theme/themes/dark";
import defaultAlgorithm from "antd/es/theme/themes/default";

// 创建自定义主题配置
const customTheme = {
  token: {
    colorPrimary: "#1677ff",
    borderRadius: 6,
  },
};

const Independent: React.FC = () => {
  const { actualTheme, toggleTheme } = useTheme();
  const { styles } = useStyle();
  const isDark = actualTheme === "dark";

  // contact modal
  const [weChatModalVisible, setweChatModalVisible] = useState(false);
  const [dingTalkModalVisible, setDingTalkModalVisible] = useState(false);

  const showDingTalkModal = () => {
    setDingTalkModalVisible(true);
  };

  const showWeChatModal = () => {
    setweChatModalVisible(true);
  };

  // 根据当前主题设置body背景色
  useEffect(() => {
    document.body.style.backgroundColor = isDark ? "#141414" : "#ffffff";
    if (isDark) {
      document.body.classList.add("dark-theme");
    } else {
      document.body.classList.remove("dark-theme");
    }
  }, [isDark]);

  // ==================== Render =================
  return (
    <>
      <TipsModalComponent
        way="WeChat"
        imageLink="dingtalk.png"
        isVisible={dingTalkModalVisible}
        setModalVisible={setDingTalkModalVisible}
      />
      <TipsModalComponent
        way="dingTalk"
        imageLink="wechat.png"
        isVisible={weChatModalVisible}
        setModalVisible={setweChatModalVisible}
      />
      <Space className={styles.topLinkWrapper}>
        <Tooltip title={isDark ? "切换到亮色模式" : "切换到暗色模式"}>
          <Button
            className="theme-toggle-btn"
            icon={isDark ? <BulbFilled /> : <BulbOutlined />}
            onClick={toggleTheme}
          />
        </Tooltip>
        <Tooltip title={"spring-ai-alibaba-examples link"}>
          <a
            href="https://github.com/springaialibaba/spring-ai-alibaba-examples"
            target="_blank"
            rel="noopener noreferrer"
          >
            <Button icon={<SnippetsOutlined />} />
          </a>
        </Tooltip>
        <Tooltip title={"spring-ai-alibaba link"}>
          <a
            href="https://github.com/alibaba/spring-ai-alibaba"
            target="_blank"
            rel="noopener noreferrer"
          >
            <Button icon={<GithubOutlined />} />
          </a>
        </Tooltip>
        <Tooltip title={"官方文档"}>
          <a
            href="https://java2ai.com"
            target="_blank"
            rel="noopener noreferrer"
          >
            <Button icon={<LinkOutlined />} />
          </a>
        </Tooltip>
      </Space>

      <div className={styles.layout}>
        <FunctionMenu />
        {/* 菜单页面容器 */}
        <div className={styles.menuPagesWrapper}>
          <Routes>
            <Route path="/" element={<Navigate to="/chat" replace />} />
            {Object.entries(pageComponents).map(([key, Component]) => (
              <React.Fragment key={key}>
                {/* 类页面路由 */}
                <Route
                  path={`/${key}`}
                  element={
                    <div className={styles.pageWrapper}>
                      <Component />
                    </div>
                  }
                />
                {/* 实例页面路由 */}
                <Route
                  path={`/${key}/:conversationId`}
                  element={
                    <div className={styles.pageWrapper}>
                      <Component />
                    </div>
                  }
                />
              </React.Fragment>
            ))}
          </Routes>

          <Layout.Footer className={styles.footer}>
            © 2024-2025 Spring AI Alibaba Community
          </Layout.Footer>
        </div>
      </div>
    </>
  );
};

const App = () => {
  const { actualTheme } = useTheme();
  const isDark = actualTheme === "dark";

  return (
    <ConfigProvider
      theme={{
        ...customTheme,
        algorithm: isDark ? darkAlgorithm : defaultAlgorithm,
      }}
    >
      <AntdApp>
        <ThemeProvider
          appearance={actualTheme}
          themeMode={actualTheme}
          theme={customTheme}
        >
          <Router>
            <Independent />
          </Router>
        </ThemeProvider>
      </AntdApp>
    </ConfigProvider>
  );
};

export default App;
