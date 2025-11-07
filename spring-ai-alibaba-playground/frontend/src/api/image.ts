import { BASE_URL } from "../const";
import { Result } from "./base";

interface ImageParams {
  style?: string; // 图片格式
  resolution?: string; // 图像宽高，范围在 512 ～ 1440，默认是 1080 * 1080
}

interface Image2TextParams {
  prompt?: string;
}

interface ImageResponse {
  blob: Blob;
  status: number;
}

// 检查blob内容的函数
const inspectBlob = async (blob: Blob): Promise<void> => {
  if (blob.size === 0) {
    console.error("Blob is empty (size 0)");
    return;
  }

  console.log("Blob size:", blob.size, "bytes");
  console.log("Blob type:", blob.type);

  // 读取blob前100字节来检查内容类型
  const slice = blob.slice(0, Math.min(100, blob.size));
  const buffer = await slice.arrayBuffer();
  const view = new Uint8Array(buffer);

  let hexString = "";
  for (let i = 0; i < view.length; i++) {
    hexString += view[i].toString(16).padStart(2, "0") + " ";
    if ((i + 1) % 16 === 0) hexString += "\n";
  }

  console.log("Blob content preview (hex):", hexString);

  // 检查PNG魔术数字 (89 50 4E 47 0D 0A 1A 0A)
  if (view.length >= 8) {
    const isPNG =
      view[0] === 0x89 &&
      view[1] === 0x50 &&
      view[2] === 0x4e &&
      view[3] === 0x47 &&
      view[4] === 0x0d &&
      view[5] === 0x0a &&
      view[6] === 0x1a &&
      view[7] === 0x0a;

    console.log("Is PNG:", isPNG);
  }

  // 检查JPEG魔术数字 (FF D8 FF)
  if (view.length >= 3) {
    const isJPEG = view[0] === 0xff && view[1] === 0xd8 && view[2] === 0xff;

    console.log("Is JPEG:", isJPEG);
  }
};

// 生成图片接口
export const getImage = async (
  prompt: string,
  params?: ImageParams
): Promise<ImageResponse> => {
  const { style, resolution } = params || {};

  try {
    console.log("请求图像生成API，prompt:", prompt);

    const res = await fetch(
      BASE_URL +
        "/text2image?prompt=" +
        encodeURIComponent(prompt) +
        (style ? "&style=" + style : "") +
        (resolution ? "&resolution=" + resolution : ""),
      {
        method: "GET",
      }
    );

    console.log("收到响应:", res.status, res.statusText);
    console.log("Content-Type:", res.headers.get("Content-Type"));

    if (!res.ok) {
      throw new Error(`API error: ${res.status}`);
    }

    // 检查响应类型
    const contentType = res.headers.get("Content-Type") || "";

    if (contentType.includes("application/json")) {
      // 如果是JSON响应
      console.log("处理JSON响应");
      const jsonData = await res.json();
      console.log("JSON数据:", jsonData);

      // 尝试从JSON响应中提取数据
      if (jsonData.data && typeof jsonData.data === "string") {
        // 如果data是base64字符串
        const base64Data = jsonData.data.replace(
          /^data:image\/\w+;base64,/,
          ""
        );
        const binaryData = atob(base64Data);
        const array = new Uint8Array(binaryData.length);
        for (let i = 0; i < binaryData.length; i++) {
          array[i] = binaryData.charCodeAt(i);
        }
        const blob = new Blob([array], { type: "image/png" });
        return { blob, status: res.status };
      } else if (jsonData.data instanceof ArrayBuffer) {
        // 如果data是ArrayBuffer
        const blob = new Blob([jsonData.data], { type: "image/png" });
        return { blob, status: res.status };
      }

      throw new Error("Invalid JSON response format");
    } else if (contentType.includes("image/")) {
      // 如果是直接的图像响应
      console.log("处理二进制图像响应");
      const blob = await res.blob();
      await inspectBlob(blob);
      return { blob, status: res.status };
    } else {
      // 尝试获取blob，作为后备方案
      console.log("未知响应类型，尝试作为blob处理");
      const blob = await res.blob();
      await inspectBlob(blob);
      if (blob.size > 0) {
        return { blob, status: res.status };
      }
      throw new Error(`Unsupported content type: ${contentType}`);
    }
  } catch (error) {
    console.error("图像生成详细错误:", error);
    throw error;
  }
};

// 解析图片接口
export const postImage = async (
  image: File,
  callback: (value: Uint8Array) => void,
  params?: Image2TextParams
): Promise<Response> => {
  const { prompt } = params || {};

  const formData = new FormData();
  formData.append("prompt", prompt || "");
  formData.append("image", image);
  const res = await fetch(BASE_URL + "/image2text", {
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
