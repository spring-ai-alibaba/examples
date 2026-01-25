# Dashscope-Video 模块

## 视频生成说明
本模块展示如何快速接入百炼-视频生成内容，支持官网上所有的参数、示例
- url连接：https://help.aliyun.com/zh/model-studio/image-to-video-api-reference


## 临时URL
在调用多模态、图像、视频或音频模型时，通常需要传入文件的 URL，该URL通常要求公网访问
- 可通过阿里提供的临时URL服务，地址如下：https://help.aliyun.com/zh/model-studio/get-temporary-file-url

## 示例说明
本模块完成了百炼官网上如下示例的接口实现，对应接口可参考 [dashscope-video.http](./dashscope-video.http)

### 接口实现顺序

| 序号 | 功能名称 | 接口路径 | 生成视频文件 |
|------|---------|---------|------------|
| 1 | 通义万相-图生视频-基于首帧 | `/ai/video/first` | 基于首帧——多镜头叙事.mp4 |
| 2 | 通义万相-图生视频-基于首尾帧 | `/ai/video/first-last` | 基于首尾帧——首位帧生视频.mp4 |
| 3 | 通义万相-图声视频-视频特效 | `/ai/video/video-effects` | 视频特效.mp4 |
| 4 | 通义万相-参考生视频 | `/ai/video/reference-video` | 参考生视频.mp4 |
| 5 | 通义万相-文生视频（多镜头叙事） | `/ai/video/t2v-multi-shot` | 通义万相-文生视频多镜头叙事.mp4 |
| 6 | 通义万相-通用视频编辑 | `/ai/video/image-reference` | 通用视频编辑-多图参考.mp4 |
| 7 | 通义万相-图生动作 | `/ai/video/animate-move` | 图生动作.mp4 |
| 8 | 通义万相-视频换人 | `/ai/video/animate-mix` | 视频换人.mp4 |
| 9 | 通义万相-数字人 | `/ai/video/s2v` | 数字人视频.mp4 |
| 10 | 图生舞蹈视频-舞动人像（AnimateAnyone） | `/ai/video/animate-anyone` | 图声舞蹈视频-舞动人像AnimateAnyone.mp4 |
| 11 | 图生演唱视频-悦动人像EMO | `/ai/video/emo` | 悦动人像EMO.mp4 |
| 12 | 图生播报视频-灵动人像 | `/ai/video/liveportrait` | 灵动人像LivePortrait.mp4 |
| 13 | 视频口型替换-声动人像 | `/ai/video/videoretalk` | 视频口型替换.mp4 |
| 14 | 图生表情包视频-表情包Emoji | `/ai/video/emoji` | 表情包视频.mp4 |
| 15 | 视频风格重绘 | `/ai/video/video-style-transform` | 视频风格重绘.mp4 |

### 接口详细说明

1. **通义万相-图生视频-基于首帧**
   - 功能：基于首帧图片生成多镜头叙事视频
   - 路径：`GET /ai/video/first`
   - 支持音频、提示词扩展、多镜头模式

2. **通义万相-图生视频-基于首尾帧**
   - 功能：基于首尾帧生成视频过渡
   - 路径：`GET /ai/video/first-last`
   - 支持首帧和尾帧图片作为输入

3. **通义万相-图声视频-视频特效**
   - 功能：基于图片生成带特效的视频
   - 路径：`GET /ai/video/video-effects`
   - 支持多种特效模板（如flying）

4. **通义万相-参考生视频**
   - 功能：基于参考视频生成新视频
   - 路径：`GET /ai/video/reference-video`
   - 支持角色参考和动作复制

5. **通义万相-文生视频（多镜头叙事）**
   - 功能：基于文本描述生成多镜头视频
   - 路径：`GET /ai/video/t2v-multi-shot`
   - 支持音频、提示词扩展、自定义尺寸和时长

6. **通义万相-通用视频编辑**
   - 功能：基于参考图片进行视频编辑
   - 路径：`GET /ai/video/image-reference`
   - 支持多图参考、对象和背景编辑

7. **通义万相-图生动作**
   - 功能：根据视频驱动图片生成动作
   - 路径：`GET /ai/video/animate-move`
   - 支持wan-std模式

8. **通义万相-视频换人**
   - 功能：视频中的人脸替换
   - 路径：`GET /ai/video/animate-mix`
   - 支持wan-std模式

9. **通义万相-数字人**
   - 功能：图片+音频生成数字人视频
   - 路径：`GET /ai/video/s2v`
   - 支持多种分辨率

10. **图生舞蹈视频-舞动人像（AnimateAnyone）**
    - 功能：基于图片和模板生成舞蹈视频
    - 路径：`GET /ai/video/animate-anyone`
    - 支持多种舞蹈模板和视频比例

11. **图生演唱视频-悦动人像EMO**
    - 功能：图片+音频生成演唱视频
    - 路径：`GET /ai/video/emo`
    - 支持人脸检测和情感表达

12. **图生播报视频-灵动人像**
    - 功能：图片+音频生成播报视频
    - 路径：`GET /ai/video/liveportrait`
    - 支持眼睛和头部动作参数

13. **视频口型替换-声动人像**
    - 功能：替换视频的口型
    - 路径：`GET /ai/video/videoretalk`
    - 支持参考图片和口型同步

14. **图生表情包视频-表情包Emoji**
    - 功能：生成表情包动画视频
    - 路径：`GET /ai/video/emoji`
    - 支持多种表情驱动模式

15. **视频风格重绘**
    - 功能：对视频进行风格化处理
    - 路径：`GET /ai/video/video-style-transform`
    - 支持多种风格和帧率设置