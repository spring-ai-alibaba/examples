import { BASE_URL } from "../const";

interface RagParams {
  chatId?: string;
}

export const getRag = async (
  prompt: string,
  callback?: (value: Uint8Array) => void,
  params?: RagParams
): Promise<Response> => {
  const { chatId } = params || {};

  const url = BASE_URL + "/rag?prompt=" + encodeURIComponent(prompt);

  const headers: HeadersInit = {
    "Content-Type": "application/json",
  };

  if (chatId) {
    headers["chatId"] = chatId;
  }

  const res = await fetch(url.toString(), {
    method: "GET",
    headers: headers,
  });

  const reader = res.body?.getReader();
  if (!reader) {
    throw new Error("Failed to get response reader");
  }

  console.log("RAG reader", reader);
  await reader.read().then(function process({ done, value }) {
    if (done) return;
    if (value) {
      callback?.(value);
    }
    return reader.read().then(process);
  });

  return res;
};
