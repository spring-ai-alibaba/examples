import {
  Input,
  Button,
  Tabs,
  message,
  Form,
  Checkbox,
  Empty,
  Typography,
} from "antd";
import {
  formatMcpServerListData,
  generateFormFields,
  name2iconMap,
} from "../../utils";
import { McpServerFormatted, McpToolFormatted, FormField } from "../../types";
import { useStyles } from "../../style";
import React, { useState, useEffect } from "react";
import { getMcpList, runMcp } from "../../../../api/mcp";
import CardTab from "../../../components/CardTab";
import { Light as SyntaxHighlighter } from "react-syntax-highlighter";
import json from "react-syntax-highlighter/dist/esm/languages/hljs/json";
import { githubGist } from "react-syntax-highlighter/dist/esm/styles/hljs";
import { CheckCircleOutlined, CloseCircleOutlined } from "@ant-design/icons";

SyntaxHighlighter.registerLanguage("json", json);

const { Item: FormItem } = Form;

const McpLandingView = () => {
  const { styles } = useStyles();
  const [isExecutingTool, setIsExecutingTool] = useState(false);
  const [executionResult, setExecutionResult] = useState<any>(null);
  const [selectedServer, setSelectedServer] = useState("");
  const [selectedTool, setSelectedTool] = useState("");
  const [form] = Form.useForm();
  const [activeTab, setActiveTab] = useState("intro");

  const [serverList, setServerList] = useState<McpServerFormatted[]>([]);
  const [currentServer, setCurrentServer] = useState<McpServerFormatted | null>(
    null
  );
  const [currentTool, setCurrentTool] = useState<McpToolFormatted | null>(null);
  const [formFields, setFormFields] = useState<FormField[]>([]);

  const adaptApiResponse = (apiServers: any[]): any[] => {
    return apiServers.map((server) => ({
      id: server.id,
      name: server.name,
      desc: server.desc || null,
      env: server.env || {},
      toolList: server.toolList || [],
    }));
  };

  useEffect(() => {
    const loadServers = async () => {
      // setIsFetchingServers(true);
      try {
        const serverData = await getMcpList();
        if (serverData && serverData.code === 10000) {
          const adaptedData = adaptApiResponse(serverData.data);
          const parsedData = formatMcpServerListData(adaptedData);
          setServerList(parsedData);

          if (parsedData.length > 0) {
            setSelectedServer(parsedData[0].id);
            setCurrentServer(parsedData[0]);
          }
        } else {
          message.error("加载 MCP servers: 数据格式无效");
        }
      } catch (error) {
        console.error("加载 MCP servers 失败:", error);
        message.error("加载 MCP servers 失败");
      } finally {
        // setIsFetchingServers(false);
      }
    };

    loadServers();
  }, []);

  useEffect(() => {
    if (currentServer && selectedTool) {
      const tool = currentServer.tools.find((t) => t.id === selectedTool);
      if (tool) {
        setCurrentTool(tool);
        // 生成工具参数的表单字段
        const toolFields = generateFormFields(tool.schema);

        // 生成环境变量的表单字段
        const envFields = Object.entries(currentServer.env || {}).map(
          ([key]) => ({
            key,
            label: `${key}`,
            fieldType: "text",
            required: true,
            description: `请输入 ${key}`,
            placeholder: `请输入 ${key}`,
          })
        );

        // 合并工具参数和环境变量的表单字段
        setFormFields([...envFields, ...toolFields]);
        form.resetFields();
      }
    }
  }, [selectedTool, currentServer, form]);

  const getIconByName = (iconName: string): React.ReactNode => {
    return name2iconMap[iconName] || name2iconMap.Default;
  };

  const handleServerChange = (serverId: string) => {
    setSelectedServer(serverId);
    setSelectedTool("");

    const server = serverList.find((s) => s.id === serverId);
    if (server) {
      setCurrentServer(server);
    }
  };

  const handleToolChange = (value: string) => {
    setSelectedTool(value);
    setActiveTab("form");
  };

  const selectedServerIcon = currentServer?.icon || "CloudOutlined";

  const handleFormSubmit = async (values: any) => {
    if (!currentServer || !currentTool) return;

    setIsExecutingTool(true);
    setExecutionResult(null);

    try {
      const toolName = currentTool.name;
      // 分离环境变量和工具参数
      const envKeys = Object.keys(currentServer.env || {});
      const envValues = Object.fromEntries(
        envKeys.map((key) => [key, values[key]])
      );
      const toolParams = Object.fromEntries(
        Object.entries(values).filter(([key]) => !envKeys.includes(key))
      );

      const params = JSON.stringify({
        ...toolParams,
        env: envValues, // 添加环境变量到请求参数
      });

      const prompt = `${toolName}(${params})`;

      const response = await runMcp(currentServer.id, prompt);

      if (response.code === 10000) {
        setExecutionResult({
          success: true,
          data: response.data,
        });
        message.success(`执行 ${currentTool.name} 成功`);
        setActiveTab("result");
      } else {
        throw new Error(response.message || "执行失败");
      }
    } catch (error) {
      console.error("调用失败", error);
      message.error("调用失败");
      setExecutionResult({
        success: false,
        error: String(error),
      });
      setActiveTab("result");
    } finally {
      setIsExecutingTool(false);
    }
  };

  const toolsToDisplay = currentServer?.tools || [];

  const renderFormItem = (field: FormField) => {
    if (field.fieldType === "checkbox") {
      return (
        <FormItem
          key={field.key}
          label={field.label}
          name={field.key}
          valuePropName="checked"
          rules={[
            {
              required: field.required,
              message: `请选择${field.label}`,
            },
          ]}
        >
          <Checkbox>{field.description}</Checkbox>
        </FormItem>
      );
    }

    return (
      <FormItem
        key={field.key}
        label={field.label}
        name={field.key}
        rules={[
          {
            required: field.required,
            message: `请输入${field.label}`,
          },
        ]}
        tooltip={field.description}
      >
        <Input type={field.fieldType} placeholder={field.placeholder} />
      </FormItem>
    );
  };

  const handleTabChange = (key: string) => {
    setActiveTab(key);
  };

  const renderMcpIntroduction = () => {
    return (
      <div className={styles.introContainer}>
        <h2>什么是 MCP?</h2>
        <p>
          MCP (Model Context Protocol) 是一种允许 AI
          模型调用各种工具和服务的协议。 通过 MCP，AI
          模型可以访问外部功能，如数据查询、计算、代码执行等。
        </p>
        <h3>主要特点</h3>
        <ul>
          <li>工具调用：AI 模型可以调用各种预定义的工具</li>
          <li>参数传递：支持结构化参数传递</li>
          <li>结果返回：工具执行结果会返回给 AI 模型</li>
          <li>跨平台：支持不同的 AI 平台和模型</li>
        </ul>
        <h3>使用方法</h3>
        <ol>
          <li>从左侧选择一个 MCP 服务器</li>
          <li>从中间栏选择一个工具</li>
          <li>填写工具所需的参数</li>
          <li>点击执行按钮运行工具</li>
          <li>在"结果"标签页查看执行结果</li>
        </ol>
      </div>
    );
  };

  const renderToolForm = () => {
    if (!currentTool) {
      return (
        <Empty
          image={Empty.PRESENTED_IMAGE_SIMPLE}
          description={
            <span>
              请先从中间栏选择一个工具
              <br />
              {currentServer
                ? `在 ${currentServer.name} 服务器中有 ${toolsToDisplay.length} 个可用工具`
                : "请先选择服务器"}
            </span>
          }
        />
      );
    }

    return (
      <div className={styles.toolFormContainer}>
        <div className={styles.toolDescription}>{currentTool.description}</div>

        <Form
          form={form}
          layout="vertical"
          className={styles.toolForm}
          onFinish={handleFormSubmit}
        >
          {formFields.map((field) => renderFormItem(field))}

          <FormItem>
            <Button
              type="primary"
              htmlType="submit"
              className={styles.submitButton}
              loading={isExecutingTool}
            >
              执行
            </Button>
          </FormItem>
        </Form>
      </div>
    );
  };

  const renderResult = () => {
    if (!executionResult) {
      return (
        <Empty
          image={Empty.PRESENTED_IMAGE_SIMPLE}
          description={
            <span>
              尚未执行任何工具
              <br />
              {currentTool
                ? `请在"参数表单"标签页执行 ${currentTool.name}`
                : "请先选择一个工具"}
            </span>
          }
        />
      );
    }

    return (
      <div className={styles.resultContainer}>
        <div className={styles.jsonResultContainer}>
          <div className={styles.resultHeader}>
            <span
              className={
                executionResult.success
                  ? styles.successHeader
                  : styles.errorHeader
              }
            >
              {executionResult.success ? (
                <>
                  <CheckCircleOutlined /> 执行成功
                </>
              ) : (
                <>
                  <CloseCircleOutlined /> 执行失败
                </>
              )}
            </span>
          </div>
          <div className={styles.resultContent}>
            {executionResult.success ? (
              <SyntaxHighlighter
                language="json"
                style={githubGist}
                className="syntax-highlighter"
                wrapLines={true}
                wrapLongLines={true}
              >
                {JSON.stringify(executionResult.data, null, 2)}
              </SyntaxHighlighter>
            ) : (
              <div className={styles.errorMessage}>{executionResult.error}</div>
            )}
          </div>
        </div>
      </div>
    );
  };

  return (
    <div className={styles.pageContainer}>
      <div className={styles.container}>
        <div className={styles.selectionPanel}>
          <div className={styles.panelHeader}>
            <Typography.Text className={styles.panelTitle}>
              MCP 服务器
            </Typography.Text>
          </div>
          <div className={styles.serverList}>
            {serverList.map((server) => (
              <div
                key={server.id}
                className={`${styles.serverItem} ${
                  selectedServer === server.id ? styles.serverItemSelected : ""
                }`}
                onClick={() => handleServerChange(server.id)}
              >
                <span className={styles.serverIcon}>
                  {getIconByName(server.icon)}
                </span>
                <span className={styles.serverName}>{server.name}</span>
              </div>
            ))}
          </div>
        </div>

        {/* 中间栏 */}
        <div className={styles.toolsPanel}>
          <div className={styles.panelHeader}>
            <div className={styles.serverInfo}>
              <span className={styles.serverIcon}>
                {getIconByName(selectedServerIcon)}
              </span>
              <span className={styles.serverName}>
                {currentServer?.name || ""}
              </span>
            </div>
          </div>
          {/* TODO： 目前好像没有工具描述 */}
          {/* <div className={styles.serverDescription}>{serverDescription}</div> */}

          <div className={styles.toolsSection}>
            <Typography.Text className={styles.sectionTitle}>
              工具列表
            </Typography.Text>
            <div className={styles.toolsList}>
              {toolsToDisplay.map((tool) => (
                <div
                  key={tool.id}
                  className={`${styles.toolItem} ${
                    selectedTool === tool.id ? styles.toolItemSelected : ""
                  }`}
                  onClick={() => handleToolChange(tool.id)}
                >
                  <span className={styles.toolName}>{tool.name}</span>
                  {/* <ExportOutlined className={styles.toolArrow} /> */}
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* 右边栏 */}
        <div className={styles.connectPanel}>
          <CardTab
            title={currentTool ? `使用 ${currentTool.name}` : "功能体验"}
            activeKey={activeTab}
            onTabChange={handleTabChange}
            defaultActiveKey="intro"
            items={[
              {
                key: "intro",
                label: "MCP 介绍",
                children: renderMcpIntroduction(),
              },
              {
                key: "form",
                label: "参数表单",
                children: renderToolForm(),
              },
              {
                key: "result",
                label: "执行结果",
                children: renderResult(),
              },
            ]}
          ></CardTab>
        </div>
      </div>

      {/* 加载中 */}
      {/* {isFetchingServers && (
        <div className={styles.loadingOverlay}>
          <Spin size="large" />
          <div className={styles.loadingText}>加载 MCP 服务器...</div>
        </div>
      )} */}
    </div>
  );
};

export default McpLandingView;
