import { BASE_URL } from "../const";

export interface McpServer {
  id: string;
  name: string;
  description: string;
}

interface McpListResponse {
  data: McpServer[];
  code: number;
  message?: string;
}

interface McpRunResponse {
  data: any;
  code: number;
  message?: string;
}

export const getMcpList = async (): Promise<McpListResponse> => {
  try {
    const res = await fetch(`${BASE_URL}/mcp-list`, {
      method: "GET",
    });

    if (!res.ok) {
      throw new Error(`API request failed with status ${res.status}`);
    }

    const data = await res.json();
    return data as McpListResponse;
  } catch (error) {
    console.error("MCP List API call error:", error);
    // Return a standard error format or rethrow
    return {
      code: -1, // Use a specific code for client-side errors
      data: [],
      message: error instanceof Error ? error.message : "Unknown error",
    };
  }
};

export const runMcp = async (
  id: string, // Server ID
  prompt: string,
  envs?: string
): Promise<McpRunResponse> => {
  try {
    const res = await fetch(`${BASE_URL}/mcp-run`, {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: new URLSearchParams({
        id: id,
        prompt: prompt,
        ...(envs ? { envs } : {}),
      }),
    });

    if (!res.ok) {
      // Attempt to read error message from response body
      let errorBody = "Unknown error";
      try {
        const errorData = await res.json();
        errorBody = errorData.message || JSON.stringify(errorData);
      } catch (e) {
        errorBody = await res.text();
      }
      throw new Error(
        `API request failed with status ${res.status}: ${errorBody}`
      );
    }

    const text = await res.text();
    let parsedData;

    try {
      parsedData = JSON.parse(text);
    } catch (e) {
      return {
        code: 0,
        data: text,
      };
    }

    if (
      parsedData &&
      typeof parsedData === "object" &&
      parsedData.code !== undefined
    ) {
      return parsedData as McpRunResponse;
    } else {
      return {
        code: 0,
        data: parsedData,
      };
    }
  } catch (error) {
    console.error("MCP Run API call error:", error);
    // Return a standard error format
    return {
      code: -1, // Use a specific code for client-side errors
      data: null,
      message: error instanceof Error ? error.message : "Unknown error",
    };
  }
};
