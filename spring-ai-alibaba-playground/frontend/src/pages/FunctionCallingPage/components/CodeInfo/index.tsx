import * as React from "react";
import { useState } from "react";
import { Steps, Typography, theme, Tabs, Button, Space } from "antd";
import { EyeOutlined, LinkOutlined } from "@ant-design/icons";
import { Image } from "antd";
import { useStyles } from "../../style";
import { motion, AnimatePresence } from "framer-motion";
import { Prism as SyntaxHighlighter } from "react-syntax-highlighter";
import { vscDarkPlus } from "react-syntax-highlighter/dist/esm/styles/prism";
import CardTab from "../../../components/CardTab";
import { codeInfoStepList } from "./constant";

// 添加全局样式来隐藏滚动条

const { Step } = Steps;
const { Paragraph, Title } = Typography;

const codeSummaries: { [key: string]: { code: string; language: string } } = {
  frontend: {
    code: `// FunctionCallingLandingView.tsx
const handleLocalMessage = async (text: string) => {
  // 添加用户消息
  const userMessage: Message = {
    id: generateId(),
    text,
    sender: "user",
    timestamp: new Date(),
  };

  setMessages((prev) => [...prev, userMessage]);
  
  try {
    // 调用MCP API
    const response = await fetch(
      \`/api/v1/mcp?prompt=\${encodeURIComponent(text)}\`,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      }
    );

    const data = await response.json();
    
    // 添加机器人回复
    const botMessage: Message = {
      id: generateId(),
      text: data.data,
      sender: "bot",
      timestamp: new Date(),
    };

    setMessages((prev) => [...prev, botMessage]);
  } catch (error) {
    console.error("Error sending message:", error);
  }
};`,
    language: "typescript",
  },

  webserver: {
    code: `// SAAMcpController.java
@RestController
@Tag(name = "MCP APIs")
@RequestMapping("/api/v1")
public class SAAMcpController {

    private final SAAMcpService mcpService;

    public SAAMcpController(SAAMcpService webSearch) {
        this.mcpService = webSearch;
    }

    @UserIp
    @GetMapping("/mcp")
    @Operation(summary = "DashScope Mcp Chat")
    public String chat(
            HttpServletResponse response,
            @Validated @RequestParam("prompt") String prompt,
            @RequestHeader(value = "chatId", required = false, defaultValue = "spring-ai-alibaba-playground-mcp") String chatId
    ) {
        response.setCharacterEncoding("UTF-8");
        return mcpService.chat(chatId, prompt);
    }
}`,
    language: "java",
  },

  mcpclient: {
    code: `// SAAMcpService.java
@Service
public class SAAMcpService {

    private final ChatClient defaultChatClient;

    public SAAMcpService(ChatModel chatModel, ToolCallbackProvider tools) {
        // Initialize chat client with non-blocking configuration
        this.defaultChatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory()),
                        new SimpleLoggerAdvisor())
                .defaultTools(tools)
                .build();
    }

    public String chat(String chatId, String prompt) {
        return defaultChatClient.prompt()
                .options(DashScopeChatOptions.builder()
                        .withTemperature(0.8)
                        .withResponseFormat(DashScopeResponseFormat.builder()
                                .type(DashScopeResponseFormat.Type.TEXT)
                                .build())
                        .build())
                .user(prompt)
                .advisors(memoryAdvisor ->
                        memoryAdvisor.param(
                                CHAT_MEMORY_CONVERSATION_ID_KEY,
                                chatId
                        ).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100)
                ).call().content();
    }
}`,
    language: "java",
  },

  mcpserver: {
    code: `// mcp-servers-config.json
{
    "mcpServers": {
        "weather": {
            "command": "java",
            "args": [
                "-Dspring.ai.mcp.server.stdio=true",
                "-Dspring.main.web-application-type=none",
                "-Dlogging.pattern.console=",
                "-jar",
                "path/to/weather.jar"
            ],
            "env": {}
        }
    }
}`,
    language: "json",
  },
};

const CodePreview = ({
  codeData,
  isVisible,
  onClose,
  anchorEl,
}: {
  codeData: { code: string; language: string };
  isVisible: boolean;
  onClose: () => void;
  anchorEl: React.RefObject<HTMLElement>;
}) => {
  const { token } = theme.useToken();
  // 获取点击位置作为动画起点
  const [position, setPosition] = React.useState({ top: 0, left: 0 });
  const [heightConstraint, setHeightConstraint] = React.useState<number | null>(
    null
  );
  const previewRef = React.useRef<HTMLDivElement>(null);
  const codePreviewRef = React.useRef<HTMLDivElement>(null);

  // 适应不同的屏幕大小
  const isMobile = window.innerWidth < 768;
  const previewWidth = isMobile ? "90vw" : "800px";

  // 检查元素是否在视口内并调整位置和大小
  const adjustToViewport = React.useCallback(() => {
    if (!previewRef.current || !anchorEl.current) return;

    const previewRect = previewRef.current.getBoundingClientRect();
    const anchorRect = anchorEl.current.getBoundingClientRect();
    const viewportHeight = window.innerHeight;
    const viewportWidth = window.innerWidth;

    // 检查顶部和底部边界
    const isTopVisible = previewRect.top >= 0;
    const isBottomVisible = previewRect.bottom <= viewportHeight;
    const isLeftVisible = previewRect.left >= 0;
    const isRightVisible = previewRect.right <= viewportWidth;

    let newTop = position.top;
    let newLeft = position.left;
    let newHeight: number | null = null;

    // 如果底部超出视口
    if (!isBottomVisible) {
      // 计算超出的高度
      const overflowHeight = previewRect.bottom - viewportHeight;

      // 首先尝试向上移动弹窗
      newTop = position.top - overflowHeight;

      // 检查移动后顶部是否还在视口内
      if (previewRect.top - overflowHeight < 0) {
        // 如果向上移动后顶部也超出，则需要缩小高度
        newTop = 10; // 留出一点边距
        newHeight = viewportHeight - 20; // 两边各留10px边距
      }
    }

    // 如果顶部超出视口
    if (!isTopVisible) {
      newTop = 10; // 顶部留出10px边距
    }

    // 如果右侧超出视口
    if (!isRightVisible) {
      // 尝试放到左侧
      newLeft = anchorRect.left - previewRect.width - 20;

      // 如果左侧也不可行，则居中显示
      if (newLeft < 0) {
        newLeft = (viewportWidth - previewRect.width) / 2;
      }
    }

    // 如果左侧超出视口
    if (!isLeftVisible) {
      newLeft = 10; // 左侧留出10px边距
    }

    // 应用新的位置和高度限制
    setPosition({ top: newTop, left: newLeft });
    if (newHeight !== null) {
      setHeightConstraint(newHeight);
    }
  }, [position]);

  // 监听窗口尺寸变化
  React.useEffect(() => {
    if (!isVisible) return;

    const handleResize = () => adjustToViewport();
    window.addEventListener("resize", handleResize);

    return () => window.removeEventListener("resize", handleResize);
  }, [isVisible, adjustToViewport]);

  // 当弹窗显示或位置变化时调整
  React.useEffect(() => {
    if (!isVisible) return;

    // 设置初始位置
    if (anchorEl.current) {
      const rect = anchorEl.current.getBoundingClientRect();
      setPosition({
        top: rect.top + window.scrollY,
        left: rect.left + window.scrollX + 20,
      });
    }

    // 渲染后检查并调整
    const checkTimer = setTimeout(() => {
      adjustToViewport();
    }, 50);

    return () => clearTimeout(checkTimer);
  }, [isVisible, adjustToViewport]);

  // 持续监控位置
  React.useEffect(() => {
    if (!isVisible || !previewRef.current) return;

    // 创建一个 MutationObserver 来监视DOM变化
    const observer = new MutationObserver(() => {
      adjustToViewport();
    });

    // 观察整个文档的变化
    observer.observe(document.body, {
      childList: true,
      subtree: true,
      attributes: true,
      characterData: true,
    });

    return () => observer.disconnect();
  }, [isVisible, adjustToViewport]);

  // 点击外部关闭
  React.useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        codePreviewRef.current &&
        !codePreviewRef.current.contains(event.target as Node) &&
        anchorEl.current !== event.target &&
        !anchorEl.current?.contains(event.target as Node)
      ) {
        onClose();
      }
    };

    if (isVisible) {
      document.addEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [onClose, isVisible, anchorEl]);

  return (
    <AnimatePresence>
      {isVisible && (
        <motion.div
          ref={previewRef}
          initial={{
            opacity: 0,
            scale: 0.3,
            x: 0,
            y: 0,
            transformOrigin: "top left",
          }}
          animate={{
            opacity: 1,
            scale: 1,
            x: 0,
            y: 0,
            transformOrigin: "top left",
          }}
          exit={{
            opacity: 0,
            scale: 0.3,
            x: 0,
            y: 0,
          }}
          transition={{
            type: "spring",
            stiffness: 300,
            damping: 30,
          }}
          style={{
            position: "fixed",
            top: position.top,
            left: position.left,
            zIndex: 1000,
            background: token.colorBgElevated,
            borderRadius: "8px",
            boxShadow: token.boxShadow,
            maxWidth: previewWidth,
            maxHeight: heightConstraint ? `${heightConstraint}px` : "50vh", // 默认最大高度改为50vh
            overflow: "hidden",
            width: "auto",
          }}
        >
          <div ref={codePreviewRef} style={{ position: "relative" }}>
            <div
              style={{
                padding: "10px 12px",
                borderBottom: `1px solid ${token.colorBorderSecondary}`,
                color: token.colorText,
                fontFamily: token.fontFamily,
                fontSize: "12px",
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
              }}
            >
              <span>{`${codeData.language.toUpperCase()} 代码`}</span>
              <motion.button
                whileHover={{ scale: 1.1 }}
                whileTap={{ scale: 0.95 }}
                style={{
                  background: token.colorFillTertiary,
                  border: "none",
                  borderRadius: "50%",
                  width: "20px",
                  height: "20px",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  cursor: "pointer",
                  fontSize: "10px",
                  color: token.colorText,
                }}
                onClick={onClose}
              >
                ✕
              </motion.button>
            </div>
            <div
              style={{
                maxHeight: heightConstraint
                  ? `${heightConstraint - 40}px`
                  : "calc(50vh - 40px)",
                overflow: "auto",
                background: token.colorBgContainer,
              }}
            >
              <SyntaxHighlighter
                language={codeData.language}
                style={vscDarkPlus}
                customStyle={{
                  margin: 0,
                  padding: "16px",
                  fontSize: "13px",
                  lineHeight: 1.5,
                  borderRadius: "0 0 8px 8px",
                  background: token.colorBgContainer,
                }}
                showLineNumbers={true}
                wrapLines={true}
              >
                {codeData.code}
              </SyntaxHighlighter>
            </div>
          </div>
        </motion.div>
      )}
    </AnimatePresence>
  );
};

const CustomDot = ({ index }: { index: number }) => {
  const { styles } = useStyles();
  const [isPreviewVisible, setIsPreviewVisible] = useState(false);
  const buttonRef = React.useRef<HTMLSpanElement>(null);

  const showPreview = (e: React.MouseEvent) => {
    e.stopPropagation();
    setIsPreviewVisible(true);
  };

  const hidePreview = () => {
    setIsPreviewVisible(false);
  };

  return (
    <>
      <motion.span
        ref={buttonRef}
        className={styles.codeInfoIcon}
        onClick={showPreview}
        whileHover={{ scale: 1.2 }}
        whileTap={{ scale: 0.9 }}
        style={{
          display: "inline-flex",
          alignItems: "center",
          justifyContent: "center",
          marginLeft: "8px",
          cursor: "pointer",
        }}
      >
        <EyeOutlined />
      </motion.span>

      <CodePreview
        codeData={codeSummaries[Object.keys(codeSummaries)[index]]}
        isVisible={isPreviewVisible}
        onClose={hidePreview}
        anchorEl={buttonRef}
      />
    </>
  );
};

const ArchitectureFlow: React.FC = () => {
  const { styles } = useStyles();

  return (
    <>
      <Steps
        direction="vertical"
        current={4}
        className={styles.codeInfoSteps}
        progressDot={(iconDot) => iconDot}
      >
        {codeInfoStepList.map((step, i) => (
          <Step
            key={`step-${i}`}
            className={styles.codeInfoStepItem}
            title={
              <div className={styles.codeInfoStepTitle}>
                <span className={styles.codeInfoTitleText}>{step.title}</span>
                <CustomDot index={step.index} />
              </div>
            }
            description={
              <div className={styles.codeInfoStepDesc}>{step.description}</div>
            }
          />
        ))}
      </Steps>
      {/* <Paragraph className={styles.codeInfoIntro}>
        至此，Spring AI 中工具调用流程结束。通过 Spring
        AI，开发者可以很方便的注册和管理工具元数据信息，开发自己的 AI 应用。
      </Paragraph> */}
    </>
  );
};

const Documentation: React.FC = () => {
  const { styles } = useStyles();

  return (
    <div className={styles.documentationContainer}>
      <Title level={4}>什么是 Tool Calling?</Title>
      <Image
        src="function-calling-basic-flow.jpg"
        alt="the original image is from https://docs.spring.io/spring-ai/reference/_images/tools/tool-calling-01.jpg"
      />
      <Paragraph>
        Spring AI 允许开发者注册自定义 Java 函数，以便 AI 模型能够通过生成 JSON
        来调用这些函数。 开发者只需实现相应的函数，并通过简单的 @Bean
        定义进行注册，从而简化与 AI 模型的交互过程。 这样，开发者可以更轻松地将
        AI 能力与外部服务连接，实现灵活的功能调用。
      </Paragraph>
      <Title level={4}>Tool Calling 执行流程?</Title>
      <Paragraph>
        Spring AI 通过提供函数元数据，使 AI
        模型能够在需要时调用自定义函数获取信息，例如当前温度。
        开发者只需将函数定义为 @Bean，AI
        模型会自动处理函数调用的请求和响应，从而简化了代码的编写。
        此外，开发者可以在提示中引用多个函数 bean
        名称，以提供更灵活的信息检索能力。
      </Paragraph>
    </div>
  );
};

const Reference: React.FC = () => {
  const { styles } = useStyles();

  return (
    <div className={styles.documentationContainer}>
      {/* <Title level={4}>参考文档</Title> */}
      <Space
        direction="vertical"
        size="middle"
        style={{ width: "100%", marginTop: "16px" }}
      >
        <Button
          type="default"
          block
          className={styles.docLinkButton}
          onClick={() =>
            window.open(
              "https://docs.spring.io/spring-ai/reference/api/functions.html",
              "_blank"
            )
          }
        >
          <LinkOutlined className="link-icon" />
          <div className={styles.docLinkContent}>
            <div className={styles.docLinkTitle}>
              {" "}
              Spring AI Function Calling
            </div>
            <div className={styles.docLinkDescription}>
              如何注册和使用函数调用 API
            </div>
          </div>
          <LinkOutlined className="arrow-icon" />
        </Button>

        <Button
          type="default"
          block
          className={styles.docLinkButton}
          onClick={() =>
            window.open(
              "https://docs.spring.io/spring-ai/reference/api/tools.html",
              "_blank"
            )
          }
        >
          <LinkOutlined className="link-icon" />
          <div className={styles.docLinkContent}>
            <div className={styles.docLinkTitle}>Spring AI Tool Calling</div>
            <div className={styles.docLinkDescription}>
              工具调用 API 的技术规范与实现
            </div>
          </div>
          <LinkOutlined className="arrow-icon" />
        </Button>

        <Button
          type="default"
          block
          className={styles.docLinkButton}
          onClick={() =>
            window.open(
              "https://java2ai.com/docs/dev/tutorials/basics/function-calling/",
              "_blank"
            )
          }
        >
          <LinkOutlined className="link-icon" />
          <div className={styles.docLinkContent}>
            <div className={styles.docLinkTitle}>Spring AI Alibaba Tools</div>
            <div className={styles.docLinkDescription}>
              通义千问/百炼等模型的函数调用与工具集成教程
            </div>
          </div>
          <LinkOutlined className="arrow-icon" />
        </Button>
      </Space>
    </div>
  );
};

const CodeInfo: React.FC = () => {
  const { styles } = useStyles();
  const [activeTab, setActiveTab] = useState("documentation");

  const handleTabChange = (key: string) => {
    setActiveTab(key);
  };

  return (
    <div className={styles.codeInfoContainer}>
      <CardTab
        title="Spring AI Alibaba Tool Calling"
        activeKey={activeTab}
        onTabChange={handleTabChange}
        // defaultActiveKey="documentation"
        items={[
          {
            key: "documentation",
            label: "文档说明",
            children: <Documentation />,
          },
          {
            key: "architecture",
            label: "架构流程",
            children: <ArchitectureFlow />,
          },
          {
            key: "reference",
            label: "参考文档",
            children: <Reference />,
          },
        ]}
        className={styles.codeInfoContainer}
      />
    </div>
  );
};

export default CodeInfo;
