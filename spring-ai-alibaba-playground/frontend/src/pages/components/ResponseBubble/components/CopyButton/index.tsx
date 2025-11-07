import React from "react";
import { CopyOutlined, ReloadOutlined } from "@ant-design/icons";
import { Button, Space, theme } from "antd";

const MessageFooter = (props: { value: string; onReload?: () => void }) => {
  const { token } = theme.useToken();
  return (
    <Space size={token.paddingXXS}>
      <Button
        color="default"
        variant="text"
        size="small"
        onClick={() => {
          navigator.clipboard.writeText(props?.value);
        }}
        icon={<CopyOutlined />}
      />
      {props.onReload && (
        <Button
          color="default"
          variant="text"
          size="small"
          onClick={props.onReload}
          icon={<ReloadOutlined />}
        />
      )}
    </Space>
  );
};

export default MessageFooter;
