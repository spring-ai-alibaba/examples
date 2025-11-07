export const codeInfoStepList = [
  {
    title: "用户侧",
    description:
      "输入能够触发工具调用的 Prompt 提示词，对应工具函数的 Description 描述；",
    index: 0,
  },
  {
    title: "AI 大模型",
    description:
      "AI 大模型判断是否调用函数，此时的 finish_reason 字段为 `TOOL_CALL`；",
    index: 1,
  },
  {
    title: "Spring AI",
    description:
      "Spring AI 在已经注册工具函数元数据中查找对应的函数，并组装参数发起调用；",
    index: 2,
  },
  {
    title: "AI 大模型",
    description: "接受工具函数调用响应，并返回最终结果给用户。",
    index: 3,
  },
];
