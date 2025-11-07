import { BASE_URL } from "../const";

interface VideoParams {
  prompt?: string;
}

// 解析视频接口
export const postVideo = async (
  video: File,
  callback: (value: Uint8Array) => void,
  params?: VideoParams
): Promise<Response> => {
  const { prompt } = params || {};

  const formData = new FormData();
  formData.append("prompt", prompt || "");
  formData.append("video", video);
  const res = await fetch(BASE_URL + "/video-qa", {
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
