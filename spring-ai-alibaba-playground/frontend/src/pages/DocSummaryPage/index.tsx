import React, { useState, useEffect } from "react";
import BasePage from "../components/BasePage";
import {
  Button,
  message,
  Space,
  Tooltip,
  Typography,
  List,
  Avatar,
  Modal,
  Input,
} from "antd";
import {
  InboxOutlined,
  InfoCircleOutlined,
  LinkOutlined,
  PaperClipOutlined,
  FileTextOutlined,
  EyeOutlined,
  MessageOutlined,
} from "@ant-design/icons";
import {
  uploadFile,
  uploadUrl,
  isValidFileType,
  isValidUrl,
  regenerate,
} from "../../api/doc";
import ReactMarkdown from "react-markdown";
import { useConversationContext } from "../../stores/conversation.store";
import { MenuPage } from "../../stores/functionMenu.store";
import { useNavigate } from "react-router-dom";
import { useStyle } from "./style";

const { Text } = Typography;

/**
 * 文件历史记录项的数据结构
 */
interface StorageData {
  file?: {
    name: string;
    size: string;
    type: string;
    uploadTime: string;
  };
  link?: string;
  content: string;
  id: string;
}

/**
 * 工具提示组件，显示上传文件的限制信息
 */
const TooltipTitle = () => {
  return (
    <ul style={{ color: "white" }}>
      <li>文件大小：不超过 100 MB 文件数量：最多 50 个</li>
      <li>图片大小：不超过 20 MB 图片数量：最多 10 个</li>
      <li>
        支持文件类型：pdf, txt, csv, docx, doc, xlsx, xls, pptx, ppt, md, mobi,
        epub, png, jpeg, jpg, webp
      </li>
    </ul>
  );
};

/**
 * 文档总结页面组件
 */
const DocSummaryPage: React.FC = () => {
  const { createConversation, aiCapabilities } = useConversationContext();
  const navigate = useNavigate();
  const { styles } = useStyle();

  // 状态管理
  const [fileHistory, setFileHistory] = useState<StorageData[]>([]);
  const [resultContent, setResultContent] = useState("");
  const [isResultModalOpen, setIsResultModalOpen] = useState(false);
  const [isProcessing, setIsProcessing] = useState(false);
  const [lastUploadedFile, setLastUploadedFile] = useState<File | null>(null);
  const [lastUploadedLink, setLastUploadedLink] = useState("");
  const [isLinkModalOpen, setIsLinkModalOpen] = useState(false);
  const [fileLink, setFileLink] = useState("");
  // 当前对话id
  const [currentId, setCurrentId] = useState<string>("");
  // 是否请求成功
  const [isSuccess, setIsSuccess] = useState(false);
  const [viewContentItem, setViewContentItem] = useState<StorageData | null>(
    null
  );
  const [isViewContentModalOpen, setIsViewContentModalOpen] = useState(false);

  // 组件挂载时从本地存储加载历史记录
  useEffect(() => {
    const loadHistory = () => {
      try {
        const fileHistoryData = localStorage.getItem("fileHistory");
        setFileHistory(fileHistoryData ? JSON.parse(fileHistoryData) : []);
      } catch (error) {
        console.error("读取历史记录失败:", error);
        setFileHistory([]);
      }
    };

    loadHistory();
  }, []);

  /**
   * 处理文件选择
   */
  const chooseFile = async (e: React.MouseEvent) => {
    e.stopPropagation();
    const input = document.createElement("input");
    input.type = "file";
    input.multiple = false;
    input.accept =
      ".pdf, .txt, .csv, .docx, .doc, .xlsx, .xls, .pptx, .ppt, .md, .mobi, .epub";

    input.onchange = async (e: Event) => {
      const file = (e.target as HTMLInputElement).files?.[0];
      if (file) {
        await handleFileUpload(file);
      }
    };
    input.click();
  };

  /**
   * 处理文件上传
   */
  const handleFileUpload = async (file: File) => {
    if (!isValidFileType(file.name)) {
      message.error("不支持的文件类型");
      return;
    }

    try {
      const newConversation = createConversation(MenuPage.Chat);
      setCurrentId(newConversation.id);
      setLastUploadedFile(file);
      setLastUploadedLink("");
      setIsResultModalOpen(true);
      setIsProcessing(true);
      setResultContent("正在上传文件，请稍候...");

      const result = await uploadFile(file, newConversation.id);
      setIsProcessing(false);
      setIsSuccess(true);
      setResultContent(result);
    } catch (error) {
      message.error(`${file.name} 文件上传失败`);
      setResultContent("上传失败：" + (error as Error).message);
      setIsProcessing(false);
      setIsSuccess(false);
    }
  };

  /**
   * 处理拖放文件上传
   */
  const handleDrop = async (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    const file = e.dataTransfer.files[0];
    if (file) {
      await handleFileUpload(file);
    }
  };

  /**
   * 处理添加链接
   */
  const handleAddLink = async () => {
    setIsLinkModalOpen(false);
    if (!fileLink) {
      message.warning("请输入文件链接");
      return;
    }

    if (!isValidUrl(fileLink)) {
      message.error("不支持的文件类型或无效的链接");
      return;
    }

    try {
      const newConversation = createConversation(MenuPage.Chat);
      setCurrentId(newConversation.id);
      setLastUploadedFile(null);
      setLastUploadedLink(fileLink);
      setIsResultModalOpen(true);
      setIsProcessing(true);
      setResultContent("正在处理链接，请稍候...");

      const result = await uploadUrl(fileLink, newConversation.id);
      setIsProcessing(false);
      setIsSuccess(true);
      setResultContent(result);
      setFileLink("");
    } catch (error) {
      message.error("链接处理失败");
      setResultContent("处理失败：" + (error as Error).message);
      setIsProcessing(false);
      setIsSuccess(false);
    }
  };

  /**
   * 查看内容
   */
  const handleViewContent = (item: StorageData, e: React.MouseEvent) => {
    e.stopPropagation();
    setViewContentItem(item);
    setIsViewContentModalOpen(true);
  };

  /**
   * 去对话
   */
  const handleStartChat = (item: StorageData, e: React.MouseEvent) => {
    e.stopPropagation();
    try {
      const { id, content } = item;
      const params = new URLSearchParams();
      params.append("prompt", content);
      params.append("conversationId", id);

      if (aiCapabilities.onlineSearch) {
        params.append("onlineSearch", "true");
      }
      if (aiCapabilities.deepThink) {
        params.append("deepThink", "true");
      }

      // 使用文件名或链接作为对话标题
      const chatTitle = item.file?.name || item.link || "文档解析对话";
      const newConversation = createConversation(MenuPage.Chat, chatTitle);

      navigate(`/chat/${newConversation.id}?${params.toString()}`);
    } catch (error) {
      console.error("创建聊天对话错误:", error);
      message.error("创建对话失败，请重试");
    }
  };

  /**
   * 处理重新生成内容
   */
  const handleRegenerate = async () => {
    setIsProcessing(true);
    try {
      if (!currentId) {
        const newConversation = createConversation(MenuPage.Chat);
        setCurrentId(newConversation.id);
      }
      setResultContent("正在重新生成，请稍候...");
      const result = await regenerate(
        lastUploadedLink
          ? { url: lastUploadedLink }
          : { file: lastUploadedFile! },
        currentId
      );
      setResultContent(result);
      setIsSuccess(true);
    } catch (error) {
      setIsSuccess(false);
      message.error("重新生成失败");
      setResultContent("重新生成失败：" + (error as Error).message);
    } finally {
      setIsProcessing(false);
    }
  };

  /**
   * 处理继续操作，创建新对话并保存历史记录
   */
  const handleContinue = () => {
    const storageData: StorageData = {
      content: resultContent,
      id: currentId,
    };

    if (lastUploadedFile) {
      storageData.file = {
        name: lastUploadedLink ? lastUploadedLink : lastUploadedFile.name,
        size: lastUploadedLink
          ? (lastUploadedFile.size / 1024).toFixed(0) + "KB"
          : "",
        type: lastUploadedLink
          ? lastUploadedFile.name.split(".").pop() || ""
          : "",
        uploadTime: new Date().toLocaleString(),
      };
    }

    if (lastUploadedLink) {
      storageData.link = lastUploadedLink;
    }

    // 更新历史记录
    const updatedHistory = [storageData, ...fileHistory].slice(0, 10);
    localStorage.setItem("fileHistory", JSON.stringify(updatedHistory));
    setFileHistory(updatedHistory);

    // 导航到聊天页面
    try {
      const params = new URLSearchParams();
      // 修复：使用 encodeURIComponent 先编码，确保 URL 安全
      params.append("prompt", encodeURIComponent(resultContent));
      params.append("conversationId", currentId);

      if (aiCapabilities.onlineSearch) {
        params.append("onlineSearch", "true");
      }
      if (aiCapabilities.deepThink) {
        params.append("deepThink", "true");
      }

      navigate(`/chat/${currentId}?${params.toString()}`);
    } catch (error) {
      console.error("创建聊天对话错误:", error);
      message.error("创建对话失败，请重试");
    }
  };

  return (
    <BasePage title="文档总结">
      <div className={styles.docContainer}>
        <div className={styles.titleSection}>
          <Text className={styles.mainTitle}>文档总结</Text>
          <Text className={styles.subTitle}>论文课件、财报合同、翻译总结</Text>
        </div>

        {/* 上传区域 */}
        <div
          className={styles.uploadArea}
          onClick={chooseFile}
          onDragOver={(e) => e.preventDefault()}
          onDrop={handleDrop}
        >
          <p className={styles.uploadIcon}>
            <InboxOutlined />
          </p>
          <Space>
            <Button
              onClick={(e) => {
                e.stopPropagation();
                chooseFile(e);
              }}
              icon={<PaperClipOutlined />}
            >
              浏览文件
            </Button>
            <Button
              onClick={(e) => {
                e.stopPropagation();
                setIsLinkModalOpen(true);
              }}
              icon={<LinkOutlined />}
            >
              文件链接
            </Button>
          </Space>
          <p className={styles.uploadHint}>
            点击上传或者将文件拖拽至此处&nbsp;
            <Tooltip placement="bottom" title={TooltipTitle}>
              <InfoCircleOutlined />
            </Tooltip>
          </p>
        </div>

        {/* 文件历史记录列表 */}
        {fileHistory.length > 0 && (
          <div style={{ marginTop: 24 }}>
            <List
              itemLayout="horizontal"
              dataSource={fileHistory}
              renderItem={(item) => (
                <List.Item className={styles.fileHistoryItem}>
                  <List.Item.Meta
                    avatar={
                      <Avatar
                        icon={
                          item.link ? <LinkOutlined /> : <FileTextOutlined />
                        }
                        style={{
                          backgroundColor: item.link ? "#1890ff" : "#ff4d4f",
                        }}
                      />
                    }
                    title={
                      <span className="file-title">
                        {item.file?.name || item.link}
                      </span>
                    }
                    description={
                      <Space>
                        {item.file && (
                          <>
                            <Text type="secondary">{item.file.size}</Text>
                            <Text type="secondary">{item.file.uploadTime}</Text>
                            <Text type="secondary">{item.file.type}</Text>
                          </>
                        )}
                        {item.link && <Text type="secondary">链接文件</Text>}
                      </Space>
                    }
                  />
                  <div className={styles.actionButtons}>
                    <Button
                      className={`${styles.actionButton} view-content`}
                      icon={<EyeOutlined />}
                      onClick={(e) => handleViewContent(item, e)}
                    >
                      查看内容
                    </Button>
                    <Button
                      className={`${styles.actionButton} start-chat`}
                      icon={<MessageOutlined />}
                      onClick={(e) => handleStartChat(item, e)}
                      type="primary"
                    >
                      去对话
                    </Button>
                  </div>
                </List.Item>
              )}
            />
          </div>
        )}

        {/* 添加链接弹窗 */}
        <Modal
          title="添加文件链接"
          open={isLinkModalOpen}
          onOk={handleAddLink}
          onCancel={() => {
            setIsLinkModalOpen(false);
            setFileLink("");
          }}
          okText="添加链接"
          cancelText="取消"
        >
          <Input
            placeholder="输入包含文件的链接"
            value={fileLink}
            onChange={(e) => setFileLink(e.target.value)}
            style={{ marginBottom: 12 }}
          />
          <div style={{ color: "#666", fontSize: 13 }}>
            <div>
              支持后缀名为 pdf, txt, csv, docx, doc, xlsx, xls, pptx, ppt, md,
              mobi, epub 的链接
            </div>
          </div>
        </Modal>

        {/* 处理结果弹窗 */}
        <Modal
          title="文档处理结果"
          open={isResultModalOpen}
          onCancel={() => {
            setIsResultModalOpen(false);
            setResultContent("");
          }}
          footer={
            <Space>
              <Button
                onClick={handleRegenerate}
                loading={isProcessing}
                disabled={!lastUploadedFile && !lastUploadedLink}
              >
                重新生成
              </Button>
              <Button
                disabled={!isSuccess}
                type="primary"
                onClick={handleContinue}
              >
                继续
              </Button>
            </Space>
          }
          width={800}
        >
          <div style={{ maxHeight: "60vh", overflow: "auto" }}>
            {isProcessing && (
              <div style={{ marginBottom: 16, color: "#1890ff" }}>
                正在处理中...
              </div>
            )}
            <ReactMarkdown>{resultContent}</ReactMarkdown>
          </div>
        </Modal>

        {/* 查看内容弹窗 */}
        <Modal
          title="文件解析内容"
          open={isViewContentModalOpen}
          onCancel={() => {
            setIsViewContentModalOpen(false);
            setViewContentItem(null);
          }}
          footer={null}
          width={800}
        >
          <div style={{ maxHeight: "60vh", overflow: "auto" }}>
            <ReactMarkdown>{viewContentItem?.content || ""}</ReactMarkdown>
          </div>
        </Modal>
      </div>
    </BasePage>
  );
};

export default DocSummaryPage;
