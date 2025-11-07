const BASE_URL = '/api/v1';


// 上传文件接口
export const uploadFile = async (file: File,chatId:string) => {
  const formData = new FormData();
  formData.append('file', file);

  const response = await fetch(`${BASE_URL}/summarizer`, {
    method: 'POST',
    headers: {
      'chatId': chatId,
    },
    body: formData,
  });

  if (!response.ok) {
    throw new Error('文件上传失败');
  }

  return await response.text();
};

// 文件链接接口
export const uploadUrl = async (url: string,chatId:string) => {
  const formData = new FormData();
  formData.append('url', url);

  const response = await fetch(`${BASE_URL}/summarizer`, {
    method: 'POST',
    headers: {
      'chatId': chatId,
    },
    body: formData,
  });

  if (!response.ok) {
    throw new Error('链接上传失败');
  }

  return await response.text();
};

// 文件类型接口
export interface FileResponse {
  success: boolean;
  data?: any;
  message?: string;
}

// 导出支持的文件类型
export const SUPPORTED_FILE_TYPES = [
  'pdf', 'txt', 'csv', 'docx', 'doc', 
  'xlsx', 'xls', 'pptx', 'ppt', 'md', 
  'mobi', 'epub'
];

// 验证文件类型
export const isValidFileType = (filename: string): boolean => {
  const extension = filename.split('.').pop()?.toLowerCase();
  return extension ? SUPPORTED_FILE_TYPES.includes(extension) : false;
};

// 验证URL
export const isValidUrl = (url: string): boolean => {
  try {
    new URL(url);
    return true;
  } catch {
    return false;
  }
};

// 重新生成接口
export const regenerate = async (params: { file?: File, url?: string },chatId:string) => {
  const formData = new FormData();
  if (params.file) {
    formData.append('file', params.file);
  } else if (params.url) {
    formData.append('url', params.url);
  }

  const response = await fetch(`${BASE_URL}/summarizer`, {
    method: 'POST',
    headers: {
      'chatId': chatId,
    },
    body: formData,
  });

  if (!response.ok) {
    throw new Error('重新生成失败');
  }

  return await response.text();
};
