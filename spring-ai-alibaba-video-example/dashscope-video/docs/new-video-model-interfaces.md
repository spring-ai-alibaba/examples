# DashScope 新增视频模型接口文档

## 文档目的

本文档用于交付给后续实现 Agent，在 `spring-ai-alibaba-example` 仓库的 `spring-ai-alibaba-video-example` 模块中，为新增 DashScope 视频模型补充本地示例接口。

实现目标是：在 `VideoController.java` 中增加 13 个接口，每个接口固定对应一个新增视频模型和一组来自真实 DashScope curl 示例的请求参数，调用现有 `DashScopeVideoModel` 生成视频任务。

## 目标仓库与文件

- 目标仓库：`/Users/yingzi/IdeaProjects/spring-ai-alibaba-examples`
- 目标模块：`/Users/yingzi/IdeaProjects/spring-ai-alibaba-examples/spring-ai-alibaba-video-example/dashscope-video`
- 核心文件：`/Users/yingzi/IdeaProjects/spring-ai-alibaba-examples/spring-ai-alibaba-video-example/dashscope-video/src/main/java/com/alibaba/cloud/ai/example/video/VideoController.java`
- 建议补充 HTTP 示例文件：`/Users/yingzi/IdeaProjects/spring-ai-alibaba-examples/spring-ai-alibaba-video-example/dashscope-video/dashscope-video.http`

## 前置依赖要求

后续实现前需要确认 examples 仓库依赖的 `spring-ai-alibaba-dashscope` 版本已经包含以下能力：

- `DashScopeModel.VideoModel` 已包含本文档列出的 13 个新增模型枚举。
- `DashScopeVideoApiConstants.videoGenerationSynthesis2Model` 已将这些模型路由到 `/api/v1/services/aigc/video-generation/video-synthesis`。
- `DashScopeVideoOptions.ParametersOptions` 支持 `audio(Boolean)` 和 `aspectRatio(String)`。
- `DashScopeVideoRequest.VideoParameters` 能正确序列化：
  - `audio` -> `"audio"`
  - `aspectRatio` -> `"aspect_ratio"`
- `media` 必须在请求 JSON 的 `input.media` 下，不应出现在 `parameters` 下。

## 实现方式要求

建议在 `VideoController.java` 中延续已有示例接口风格：

- 每个接口使用 `@GetMapping` 暴露一个本地 GET 路径。
- 每个接口内部构造 `DashScopeVideoOptions`。
- 使用 `DashScopeVideoOptions.builder().withModel(...)` 指定模型。
- 使用 `DashScopeVideoOptions.Media.builder().type(...).url(...).build()` 构造媒体输入。
- 使用 `new VideoPrompt(prompt, options)` 或项目内已有等价调用方式调用 `DashScopeVideoModel`。
- 返回值类型与现有视频生成接口保持一致。

## 新增接口总览

| 序号 | 本地接口路径 | 模型字符串 | 枚举建议 | 场景 |
| --- | --- | --- | --- | --- |
| 1 | `/ai/video/happyhorse/t2v` | `happyhorse-1.0-t2v` | `HAPPYHORSE_1_0_T2V` | HappyHorse 文生视频 |
| 2 | `/ai/video/happyhorse/i2v` | `happyhorse-1.0-i2v` | `HAPPYHORSE_1_0_I2V` | HappyHorse 图生视频-首帧 |
| 3 | `/ai/video/happyhorse/r2v` | `happyhorse-1.0-r2v` | `HAPPYHORSE_1_0_R2V` | HappyHorse 参考生视频 |
| 4 | `/ai/video/happyhorse/video-edit` | `happyhorse-1.0-video-edit` | `HAPPYHORSE_1_0_VIDEO_EDIT` | HappyHorse 视频编辑 |
| 5 | `/ai/video/pixverse/t2v` | `pixverse/pixverse-c1-t2v` | `PIXVERSE_PIXVERSE_C1_T2V` | 爱诗文生视频 |
| 6 | `/ai/video/pixverse/it2v` | `pixverse/pixverse-c1-it2v` | `PIXVERSE_PIXVERSE_C1_IT2V` | 爱诗图生视频-首帧 |
| 7 | `/ai/video/pixverse/kf2v` | `pixverse/pixverse-c1-kf2v` | `PIXVERSE_PIXVERSE_C1_KF2V` | 爱诗图生视频-首尾帧 |
| 8 | `/ai/video/pixverse/r2v` | `pixverse/pixverse-c1-r2v` | `PIXVERSE_PIXVERSE_C1_R2V` | 爱诗参考生视频 |
| 9 | `/ai/video/kling/v3-video-generation` | `kling/kling-v3-video-generation` | `KLING_V3_VIDEO_GENERATION` | 可灵视频生成 |
| 10 | `/ai/video/vidu/text2video` | `vidu/viduq3-turbo_text2video` | `VIDUG3_TURBO_TEXT2VIDEO` | Vidu 文生视频 |
| 11 | `/ai/video/vidu/img2video` | `vidu/viduq3-pro_img2video` | `VIDUG3_PRO_IMG2VIDEO` | Vidu 图生视频-首帧 |
| 12 | `/ai/video/vidu/start-end2video` | `vidu/viduq3-turbo_start-end2video` | `VIDUG3_TURBO_START_END2VIDEO` | Vidu 图生视频-首尾帧 |
| 13 | `/ai/video/vidu/reference2video` | `vidu/viduq3-mix_reference2video` | `VIDUG3_MIX_REFERENCE2VIDEO` | Vidu 参考生视频 |

> 若实际枚举名与本文档建议不完全一致，以 `DashScopeModel.VideoModel` 中已经存在的枚举为准，但 `getName()` 必须等于表格中的模型字符串。

## 接口详情

### 1. HappyHorse 文生视频

- 本地路径：`GET /ai/video/happyhorse/t2v`
- 模型：`happyhorse-1.0-t2v`
- 场景：文生视频

官方请求 JSON：

```json
{
  "model": "happyhorse-1.0-t2v",
  "input": {
    "prompt": "一座由硬纸板和瓶盖搭建的微型城市，在夜晚焕发出生机。一列硬纸板火车缓缓驶过，小灯点缀其间，照亮前路。"
  },
  "parameters": {
    "resolution": "720P",
    "ratio": "16:9",
    "duration": 5
  }
}
```

Java Options 映射：

```java
DashScopeVideoOptions.builder()
    .withModel(DashScopeModel.VideoModel.HAPPYHORSE_1_0_T2V.getName())
    .withResolution("720P")
    .withRatio("16:9")
    .withDuration(5)
    .build();
```

### 2. HappyHorse 图生视频-基于首帧

- 本地路径：`GET /ai/video/happyhorse/i2v`
- 模型：`happyhorse-1.0-i2v`
- 场景：图生视频，首帧输入

官方请求 JSON：

```json
{
  "model": "happyhorse-1.0-i2v",
  "input": {
    "prompt": "一只猫在草地上奔跑",
    "media": [
      {
        "type": "first_frame",
        "url": "https://cdn.translate.alibaba.com/r/wanx-demo-1.png"
      }
    ]
  },
  "parameters": {
    "resolution": "720P",
    "duration": 5
  }
}
```

Java Options 映射：

```java
DashScopeVideoOptions.builder()
    .withModel(DashScopeModel.VideoModel.HAPPYHORSE_1_0_I2V.getName())
    .withMedia(List.of(DashScopeVideoOptions.Media.builder()
        .type("first_frame")
        .url("https://cdn.translate.alibaba.com/r/wanx-demo-1.png")
        .build()))
    .withResolution("720P")
    .withDuration(5)
    .build();
```

### 3. HappyHorse 参考生视频

- 本地路径：`GET /ai/video/happyhorse/r2v`
- 模型：`happyhorse-1.0-r2v`
- 场景：参考图生成视频

官方请求 JSON：

```json
{
  "model": "happyhorse-1.0-r2v",
  "input": {
    "prompt": "[Image 1]中身着红色旗袍的女性，镜头先以侧面中景勾勒旗袍修身剪裁与S型曲线，随即切换至低角度仰拍，捕捉她轻抬玉手展开[Image 2]中的折扇的同时，[Image 3]中的流苏耳坠随头部转动轻盈摆动的细节，最后推近至面部特写，定格在她指尖轻点扇骨、眼波流转间的含蓄风情，多视角全方位展现东方韵味。",
    "media": [
      {
        "type": "reference_image",
        "url": "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260424/mvzfud/hh-v2v-girl.jpg"
      },
      {
        "type": "reference_image",
        "url": "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260424/fvuihk/hh-v2v2-folding-fan.jpg"
      },
      {
        "type": "reference_image",
        "url": "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260424/imerii/hh-v2v-earrings.jpg"
      }
    ]
  },
  "parameters": {
    "resolution": "720P",
    "ratio": "16:9",
    "duration": 5
  }
}
```

Java Options 映射：

```java
DashScopeVideoOptions.builder()
    .withModel(DashScopeModel.VideoModel.HAPPYHORSE_1_0_R2V.getName())
    .withMedia(List.of(
        DashScopeVideoOptions.Media.builder().type("reference_image").url("https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260424/mvzfud/hh-v2v-girl.jpg").build(),
        DashScopeVideoOptions.Media.builder().type("reference_image").url("https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260424/fvuihk/hh-v2v2-folding-fan.jpg").build(),
        DashScopeVideoOptions.Media.builder().type("reference_image").url("https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260424/imerii/hh-v2v-earrings.jpg").build()))
    .withResolution("720P")
    .withRatio("16:9")
    .withDuration(5)
    .build();
```

### 4. HappyHorse 视频编辑

- 本地路径：`GET /ai/video/happyhorse/video-edit`
- 模型：`happyhorse-1.0-video-edit`
- 场景：视频编辑，输入视频和参考图

官方请求 JSON：

```json
{
  "model": "happyhorse-1.0-video-edit",
  "input": {
    "prompt": "让视频中的马头人身角色穿上图片中的条纹毛衣",
    "media": [
      {
        "type": "video",
        "url": "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260409/dozxak/Wan_Video_Edit_33_1.mp4"
      },
      {
        "type": "reference_image",
        "url": "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260415/hynnff/wan-video-edit-clothes.webp"
      }
    ]
  },
  "parameters": {
    "resolution": "720P"
  }
}
```

Java Options 映射：

```java
DashScopeVideoOptions.builder()
    .withModel(DashScopeModel.VideoModel.HAPPYHORSE_1_0_VIDEO_EDIT.getName())
    .withMedia(List.of(
        DashScopeVideoOptions.Media.builder().type("video").url("https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260409/dozxak/Wan_Video_Edit_33_1.mp4").build(),
        DashScopeVideoOptions.Media.builder().type("reference_image").url("https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260415/hynnff/wan-video-edit-clothes.webp").build()))
    .withResolution("720P")
    .build();
```

### 5. 爱诗文生视频

- 本地路径：`GET /ai/video/pixverse/t2v`
- 模型：`pixverse/pixverse-c1-t2v`
- 场景：文生视频

官方请求 JSON：

```json
{
  "model": "pixverse/pixverse-c1-t2v",
  "input": {
    "prompt": "下着雨，赛博城市里，一只浣熊在栏杆上行走。突然他眼睛发出蓝光，变身成一架高科技无人机，快速飞离画面。"
  },
  "parameters": {
    "size": "1280*720",
    "duration": 5,
    "watermark": true
  }
}
```

Java Options 映射：

```java
DashScopeVideoOptions.builder()
    .withModel(DashScopeModel.VideoModel.PIXVERSE_PIXVERSE_C1_T2V.getName())
    .withSize("1280*720")
    .withDuration(5)
    .withWatermark(true)
    .build();
```

### 6. 爱诗图生视频-基于首帧

- 本地路径：`GET /ai/video/pixverse/it2v`
- 模型：`pixverse/pixverse-c1-it2v`
- 场景：图生视频，首帧输入

官方请求 JSON：

```json
{
  "model": "pixverse/pixverse-c1-it2v",
  "input": {
    "media": [
      {
        "type": "image_url",
        "url": "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260121/zlpocv/wan-i2v-haigui.webp"
      }
    ],
    "prompt": "镜头从海龟下方缓缓上移，海龟悠然游动，腹部细节清晰可见。"
  },
  "parameters": {
    "resolution": "720P",
    "duration": 5,
    "audio": false,
    "watermark": true
  }
}
```

Java Options 映射：

```java
DashScopeVideoOptions.builder()
    .withModel(DashScopeModel.VideoModel.PIXVERSE_PIXVERSE_C1_IT2V.getName())
    .withMedia(List.of(DashScopeVideoOptions.Media.builder()
        .type("image_url")
        .url("https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260121/zlpocv/wan-i2v-haigui.webp")
        .build()))
    .withResolution("720P")
    .withDuration(5)
    .withAudio(false)
    .withWatermark(true)
    .build();
```

### 7. 爱诗图生视频-基于首尾帧

- 本地路径：`GET /ai/video/pixverse/kf2v`
- 模型：`pixverse/pixverse-c1-kf2v`
- 场景：首尾帧图生视频

官方请求 JSON：

```json
{
  "model": "pixverse/pixverse-c1-kf2v",
  "input": {
    "media": [
      {
        "type": "first_frame",
        "url": "https://wanx.alicdn.com/material/20250318/first_frame.png"
      },
      {
        "type": "last_frame",
        "url": "https://wanx.alicdn.com/material/20250318/last_frame.png"
      }
    ],
    "prompt": "一只小猫从窗台向下跳跃，轻盈地落在沙发上，然后好奇地环顾四周。"
  },
  "parameters": {
    "resolution": "720P",
    "duration": 5,
    "watermark": true
  }
}
```

Java Options 映射：

```java
DashScopeVideoOptions.builder()
    .withModel(DashScopeModel.VideoModel.PIXVERSE_PIXVERSE_C1_KF2V.getName())
    .withMedia(List.of(
        DashScopeVideoOptions.Media.builder().type("first_frame").url("https://wanx.alicdn.com/material/20250318/first_frame.png").build(),
        DashScopeVideoOptions.Media.builder().type("last_frame").url("https://wanx.alicdn.com/material/20250318/last_frame.png").build()))
    .withResolution("720P")
    .withDuration(5)
    .withWatermark(true)
    .build();
```

### 8. 爱诗参考生视频

- 本地路径：`GET /ai/video/pixverse/r2v`
- 模型：`pixverse/pixverse-c1-r2v`
- 场景：参考图生成视频

官方请求 JSON：

```json
{
  "model": "pixverse/pixverse-c1-r2v",
  "input": {
    "media": [
      {
        "type": "image_url",
        "url": "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260320/knsple/wan-r2v-role-frame.jpg"
      },
      {
        "type": "image_url",
        "url": "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260129/qpzxps/wan-r2v-object4.png"
      },
      {
        "type": "image_url",
        "url": "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260129/wfjikw/wan-r2v-backgroud5.png"
      }
    ],
    "prompt": "男人坐在靠窗的椅子上，手持吉他，在咖啡厅旁演奏一首舒缓的美国乡村民谣"
  },
  "parameters": {
    "size": "1280*720",
    "duration": 5,
    "audio": false,
    "watermark": true
  }
}
```

Java Options 映射：

```java
DashScopeVideoOptions.builder()
    .withModel(DashScopeModel.VideoModel.PIXVERSE_PIXVERSE_C1_R2V.getName())
    .withMedia(List.of(
        DashScopeVideoOptions.Media.builder().type("image_url").url("https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260320/knsple/wan-r2v-role-frame.jpg").build(),
        DashScopeVideoOptions.Media.builder().type("image_url").url("https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260129/qpzxps/wan-r2v-object4.png").build(),
        DashScopeVideoOptions.Media.builder().type("image_url").url("https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260129/wfjikw/wan-r2v-backgroud5.png").build()))
    .withSize("1280*720")
    .withDuration(5)
    .withAudio(false)
    .withWatermark(true)
    .build();
```

### 9. 可灵视频生成

- 本地路径：`GET /ai/video/kling/v3-video-generation`
- 模型：`kling/kling-v3-video-generation`
- 场景：视频生成

官方请求 JSON：

```json
{
  "model": "kling/kling-v3-video-generation",
  "input": {
    "prompt": "一只小猫在月光下奔跑"
  },
  "parameters": {
    "mode": "std",
    "aspect_ratio": "16:9",
    "duration": 5,
    "audio": false,
    "watermark": true
  }
}
```

Java Options 映射：

```java
DashScopeVideoOptions.builder()
    .withModel(DashScopeModel.VideoModel.KLING_V3_VIDEO_GENERATION.getName())
    .withMode("std")
    .withAspectRatio("16:9")
    .withDuration(5)
    .withAudio(false)
    .withWatermark(true)
    .build();
```

### 10. Vidu 文生视频

- 本地路径：`GET /ai/video/vidu/text2video`
- 模型：`vidu/viduq3-turbo_text2video`
- 场景：文生视频

官方请求 JSON：

```json
{
  "model": "vidu/viduq3-turbo_text2video",
  "input": {
    "prompt": "一只小猫在月光下奔跑"
  },
  "parameters": {
    "size": "1024*576",
    "resolution": "540P",
    "duration": 5,
    "watermark": true
  }
}
```

Java Options 映射：

```java
DashScopeVideoOptions.builder()
    .withModel(DashScopeModel.VideoModel.VIDUG3_TURBO_TEXT2VIDEO.getName())
    .withSize("1024*576")
    .withResolution("540P")
    .withDuration(5)
    .withWatermark(true)
    .build();
```

### 11. Vidu 图生视频-基于首帧

- 本地路径：`GET /ai/video/vidu/img2video`
- 模型：`vidu/viduq3-pro_img2video`
- 场景：图生视频，首帧输入

官方请求 JSON：

```json
{
  "model": "vidu/viduq3-pro_img2video",
  "input": {
    "media": [
      {
        "type": "image",
        "url": "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260121/zlpocv/wan-i2v-haigui.webp"
      }
    ],
    "prompt": "镜头从海龟下方缓缓上移，海龟悠然游动，腹部细节清晰可见。"
  },
  "parameters": {
    "duration": 5,
    "resolution": "720P",
    "watermark": true
  }
}
```

Java Options 映射：

```java
DashScopeVideoOptions.builder()
    .withModel(DashScopeModel.VideoModel.VIDUG3_PRO_IMG2VIDEO.getName())
    .withMedia(List.of(DashScopeVideoOptions.Media.builder()
        .type("image")
        .url("https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260121/zlpocv/wan-i2v-haigui.webp")
        .build()))
    .withDuration(5)
    .withResolution("720P")
    .withWatermark(true)
    .build();
```

### 12. Vidu 图生视频-基于首尾帧

- 本地路径：`GET /ai/video/vidu/start-end2video`
- 模型：`vidu/viduq3-turbo_start-end2video`
- 场景：首尾帧图生视频

官方请求 JSON：

```json
{
  "model": "vidu/viduq3-turbo_start-end2video",
  "input": {
    "media": [
      {
        "type": "image",
        "url": "https://wanx.alicdn.com/material/20250318/first_frame.png"
      },
      {
        "type": "image",
        "url": "https://wanx.alicdn.com/material/20250318/last_frame.png"
      }
    ],
    "prompt": "一只小猫从窗台向下跳跃，轻盈地落在沙发上，然后好奇地环顾四周。"
  },
  "parameters": {
    "resolution": "540P",
    "duration": 5,
    "watermark": true
  }
}
```

Java Options 映射：

```java
DashScopeVideoOptions.builder()
    .withModel(DashScopeModel.VideoModel.VIDUG3_TURBO_START_END2VIDEO.getName())
    .withMedia(List.of(
        DashScopeVideoOptions.Media.builder().type("image").url("https://wanx.alicdn.com/material/20250318/first_frame.png").build(),
        DashScopeVideoOptions.Media.builder().type("image").url("https://wanx.alicdn.com/material/20250318/last_frame.png").build()))
    .withResolution("540P")
    .withDuration(5)
    .withWatermark(true)
    .build();
```

### 13. Vidu 参考生视频

- 本地路径：`GET /ai/video/vidu/reference2video`
- 模型：`vidu/viduq3-mix_reference2video`
- 场景：参考图生成视频

官方请求 JSON：

```json
{
  "model": "vidu/viduq3-mix_reference2video",
  "input": {
    "media": [
      {
        "type": "image",
        "url": "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260320/knsple/wan-r2v-role-frame.jpg"
      },
      {
        "type": "image",
        "url": "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260129/qpzxps/wan-r2v-object4.png"
      },
      {
        "type": "image",
        "url": "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260129/wfjikw/wan-r2v-backgroud5.png"
      }
    ],
    "prompt": "男人坐在靠窗的椅子上，手持吉他，在咖啡厅旁演奏一首舒缓的美国乡村民谣"
  },
  "parameters": {
    "duration": 5,
    "size": "1280*720",
    "resolution": "720P",
    "watermark": true
  }
}
```

Java Options 映射：

```java
DashScopeVideoOptions.builder()
    .withModel(DashScopeModel.VideoModel.VIDUG3_MIX_REFERENCE2VIDEO.getName())
    .withMedia(List.of(
        DashScopeVideoOptions.Media.builder().type("image").url("https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260320/knsple/wan-r2v-role-frame.jpg").build(),
        DashScopeVideoOptions.Media.builder().type("image").url("https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260129/qpzxps/wan-r2v-object4.png").build(),
        DashScopeVideoOptions.Media.builder().type("image").url("https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20260129/wfjikw/wan-r2v-backgroud5.png").build()))
    .withDuration(5)
    .withSize("1280*720")
    .withResolution("720P")
    .withWatermark(true)
    .build();
```

## `dashscope-video.http` 建议追加内容

以下内容仅作为后续 Agent 修改 `dashscope-video.http` 的建议，本次文档生成任务不直接修改该文件。

```http
### HappyHorse 文生视频
GET http://localhost:10081/ai/video/happyhorse/t2v

### HappyHorse 图生视频-首帧
GET http://localhost:10081/ai/video/happyhorse/i2v

### HappyHorse 参考生视频
GET http://localhost:10081/ai/video/happyhorse/r2v

### HappyHorse 视频编辑
GET http://localhost:10081/ai/video/happyhorse/video-edit

### PixVerse 爱诗文生视频
GET http://localhost:10081/ai/video/pixverse/t2v

### PixVerse 爱诗图生视频-首帧
GET http://localhost:10081/ai/video/pixverse/it2v

### PixVerse 爱诗图生视频-首尾帧
GET http://localhost:10081/ai/video/pixverse/kf2v

### PixVerse 爱诗参考生视频
GET http://localhost:10081/ai/video/pixverse/r2v

### Kling 可灵视频生成
GET http://localhost:10081/ai/video/kling/v3-video-generation

### Vidu 文生视频
GET http://localhost:10081/ai/video/vidu/text2video

### Vidu 图生视频-首帧
GET http://localhost:10081/ai/video/vidu/img2video

### Vidu 图生视频-首尾帧
GET http://localhost:10081/ai/video/vidu/start-end2video

### Vidu 参考生视频
GET http://localhost:10081/ai/video/vidu/reference2video
```

## 编译与启动验证

在 examples 仓库根目录执行：

```bash
cd /Users/yingzi/IdeaProjects/spring-ai-alibaba-examples
mvn -pl spring-ai-alibaba-video-example/dashscope-video -DskipTests compile
```

启动示例服务：

```bash
cd /Users/yingzi/IdeaProjects/spring-ai-alibaba-examples
AI_DASHSCOPE_API_KEY=<your-api-key> mvn -pl spring-ai-alibaba-video-example/dashscope-video spring-boot:run
```

Smoke test 示例：

```bash
curl http://localhost:10081/ai/video/happyhorse/t2v
curl http://localhost:10081/ai/video/pixverse/t2v
curl http://localhost:10081/ai/video/kling/v3-video-generation
curl http://localhost:10081/ai/video/vidu/reference2video
```

## 实现验收点

- `VideoController.java` 新增 13 个接口。
- 每个接口模型字符串与本文档完全一致。
- `input.prompt` 与本文档官方 JSON 一致。
- `input.media` 的 `type` 和 `url` 与本文档官方 JSON 一致。
- `parameters` 中字段名与本文档官方 JSON 一致，尤其是：
  - `audio`
  - `aspect_ratio`
  - `watermark`
  - `ratio`
  - `size`
  - `resolution`
  - `duration`
  - `mode`
- 图像、视频、参考图等媒体输入全部位于 `input.media`。
- 不把 `media` 放入 `parameters`。
- 编译通过。
- 至少完成 4 个 smoke test：`happyhorse/t2v`、`pixverse/t2v`、`kling/v3-video-generation`、`vidu/reference2video`。
