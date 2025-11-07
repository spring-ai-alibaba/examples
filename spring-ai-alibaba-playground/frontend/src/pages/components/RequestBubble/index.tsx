import React, { useState, useRef, useEffect } from "react";
import { EditOutlined, CheckOutlined, CloseOutlined } from "@ant-design/icons";
import { Button, Space, theme, Input } from "antd";
import { useStyle } from "./style";

const { TextArea } = Input;

interface RequestBubbleProps {
  content: string;
  timestamp: number;
  onEdit?: () => void;
  onEditConfirm?: (newContent: string) => void;
}

const RequestBubble: React.FC<RequestBubbleProps> = ({
  content,
  timestamp,
  onEdit,
  onEditConfirm,
}) => {
  const { styles } = useStyle();
  const { token } = theme.useToken();
  const [isEditing, setIsEditing] = useState(false);
  const [editContent, setEditContent] = useState(content);
  const [displayContent, setDisplayContent] = useState(content);
  const textAreaRef = useRef<any>(null);

  useEffect(() => {
    setDisplayContent(content);
  }, [content]);

  useEffect(() => {
    if (isEditing && textAreaRef.current) {
      // 使用 Ant Design TextArea 的正确方式
      const textArea = textAreaRef.current.resizableTextArea?.textArea;
      if (textArea) {
        textArea.focus();
        // 将光标移到文本末尾
        const length = editContent.length;
        textArea.setSelectionRange(length, length);
      }
    }
  }, [isEditing]);

  const handleEditClick = () => {
    setIsEditing(true);
    setEditContent(displayContent);
    onEdit?.();
  };

  const handleConfirm = () => {
    if (editContent.trim() && editContent !== displayContent) {
      setDisplayContent(editContent.trim());
      onEditConfirm?.(editContent.trim());
    }
    setIsEditing(false);
  };

  const handleCancel = () => {
    setIsEditing(false);
    setEditContent(displayContent);
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter" && (e.ctrlKey || e.metaKey)) {
      e.preventDefault();
      handleConfirm();
    } else if (e.key === "Escape") {
      e.preventDefault();
      handleCancel();
    }
  };

  return (
    <div className={styles.userMessage}>
      <div className={styles.messageSender}>You</div>
      {isEditing ? (
        <div style={{ marginBottom: token.paddingXS }}>
          <TextArea
            ref={textAreaRef}
            value={editContent}
            onChange={(e) => setEditContent(e.target.value)}
            onKeyDown={handleKeyDown}
            autoSize={{ minRows: 2, maxRows: 8 }}
            style={{ marginBottom: token.paddingXS }}
            placeholder="输入消息内容..."
          />
          <Space size={token.paddingXXS}>
            <Button
              color="primary"
              variant="text"
              size="small"
              onClick={handleConfirm}
              icon={<CheckOutlined />}
              disabled={!editContent.trim()}
              title="确认 (Ctrl+Enter)"
            />
            <Button
              color="default"
              variant="text"
              size="small"
              onClick={handleCancel}
              icon={<CloseOutlined />}
              title="取消 (Esc)"
            />
          </Space>
        </div>
      ) : (
        <>
          <div className={styles.messageText}>{displayContent}</div>
          <div className={styles.messageTime}>
            {new Date(timestamp).toLocaleString()}
          </div>
          {onEditConfirm && (
            <div style={{ marginTop: token.paddingXS }}>
              <Space size={token.paddingXXS}>
                <Button
                  color="default"
                  variant="text"
                  size="small"
                  onClick={handleEditClick}
                  icon={<EditOutlined />}
                  title="编辑消息"
                />
              </Space>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default RequestBubble;
