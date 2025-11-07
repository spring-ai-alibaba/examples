import { BASE_URL } from "../const";

export const getChat = async (
  message: string,
  callback: (value: Uint8Array) => void,
  params?: {
    image?: File;
    model?: string;
    chatId?: string;
    deepThink?: boolean;
    onlineSearch?: boolean;
  }
) => {
  const { image, model, chatId, onlineSearch, deepThink } = params || {};
  let res: any;
  console.log("online search", onlineSearch);
  if (image !== undefined) {
    const formData = new FormData();
    formData.append("prompt", message || "");
    formData.append("image", image);
    res = (await fetch(BASE_URL + "/image2text", {
      method: "POST",
      body: formData,
    })) as any;
  } else if (onlineSearch) {
    res = (await fetch(BASE_URL + "/search?query=" + message, {
      method: "GET",
      headers: {
        chatId: chatId ? chatId : "",
      },
    })) as any;
  } else if (deepThink) {
    res = (await fetch(BASE_URL + "/deep-thinking/chat?prompt=" + message, {
      method: "GET",
      headers: {
        model: model ? model : "",
        chatId: chatId ? chatId : "",
      },
    })) as any;
  } else {
    res = (await fetch(BASE_URL + "/search?prompt=" + message, {
      method: "GET",
      headers: {
        model: model ? model : "",
        chatId: chatId ? chatId : "",
      },
    })) as any;
  }

  const reader = res.body.getReader();
  await reader.read().then(function process({ done, value }) {
    if (done) return;

    callback(value);

    return reader.read().then(process);
  });

  return res;
};
