import { BASE_URL } from "../const";
import { Result } from "./base";

// 生成语音接口
export const getAudio = async (prompt: string): Promise<Blob> => {
  const res = await fetch(BASE_URL + "/text2audio?prompt=" + prompt, {
    method: "GET",
  });

  const result: Result<ArrayBuffer> = await res.json();
  return new Blob([result.data], { type: "audio/wav" });
};

// 解析语音接口
export const postAudio = async (
  audio: File,
  callback: (value: Uint8Array) => void
): Promise<Response> => {
  const formData = new FormData();
  formData.append("audio", audio);
  const res = await fetch(BASE_URL + "/audio2text", {
    method: "POST",
    body: formData,
  });

  const reader = res.body?.getReader();
  if (!reader) {
    throw new Error("Failed to get response reader");
  }

  await reader.read().then(function process({ done, value }) {
    if (done) return;
    callback(value);
    return reader.read().then(process);
  });

  return res;
};
