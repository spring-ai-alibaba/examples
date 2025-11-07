import { BASE_URL } from "../const";

interface ChatParams {
  model?: string;
  chatId?: string;
  deepThink?: boolean;
  onlineSearch?: boolean;
}

export const getChat = async (
  prompt: string,
  callback?: (value: Uint8Array) => void,
  params?: ChatParams
): Promise<Response> => {
  const { model, chatId, onlineSearch, deepThink } = params || {};

  let res: Response;
  if (onlineSearch) {
    console.log("onlineSearch", onlineSearch);
    res = await fetch(BASE_URL + "/search", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: prompt,
    });
    console.log("联网搜索响应状态:", res.status, res.statusText);
  } else if (deepThink) {
    res = await fetch(BASE_URL + "/deep-thinking/chat", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        model: model || "",
        chatId: chatId || "",
      },
      body: prompt,
    });
  } else {
    res = await fetch(BASE_URL + "/chat", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        model: model || "",
        chatId: chatId || "",
      },
      body: prompt,
    });
  }

  const reader = res.body?.getReader();
  if (!reader) {
    throw new Error("Failed to get response reader");
  }

  await reader.read().then(function process({ done, value }) {
    if (done) return;
    callback?.(value);
    return reader.read().then(process);
  });

  return res;
};
