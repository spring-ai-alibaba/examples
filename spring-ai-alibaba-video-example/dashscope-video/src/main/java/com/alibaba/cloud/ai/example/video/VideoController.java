/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.ai.example.video;

import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import com.alibaba.cloud.ai.dashscope.video.DashScopeVideoOptions;
import com.alibaba.cloud.ai.dashscope.video.VideoModel;
import com.alibaba.cloud.ai.dashscope.video.VideoPrompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Video Example
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 * <p>
 * refer: https://help.aliyun.com/zh/model-studio/image-to-video-api-reference
 */

@RestController
@RequestMapping("/ai/video")
public class VideoController {

    private final VideoModel videoModel;

    public VideoController(VideoModel videoModel) {

        this.videoModel = videoModel;
    }

    /**
     * text to video
     * 模型：wanx2.1-t2v-turbo & wanx2.1-t2v-plus
     * Tips: 视频连接半小时内有效，及时下载保存。
     */
    @GetMapping("/text")
    public String textToVideo() {

        return this.videoModel.call(new VideoPrompt("一只小猫在草地奔跑"))
                .getResult()
                .getOutput()
                .getVideoUrl();
    }

    /**
     * 视频特效
     * 模型：wanx2.1-i2v-turbo & wanx2.1-i2v-plus
     * Tips： 当使用视频特效时，prompt 参数无效，无需填写
     */
    @GetMapping("/tmpl")
    public String videoTmpl() {

        return videoModel.call(new VideoPrompt(DashScopeVideoOptions.builder()
                                .model(DashScopeModel.VideoModel.WANX2_1_I2V_TURBO.getValue())
                                .imageUrl("https://help-static-aliyun-doc.aliyuncs.com/assets/img/zh-CN/6431807371/p906901.png")
                                .template(DashScopeApiSpec.VideoTemplate.FLYING)
                                .build()
                        )
                ).getResult()
                .getOutput()
                .getVideoUrl();
    }

    /**
     * 基于首帧生成视频.
     * 模型：wanx2.1-i2v-turbo & wanx2.1-i2v-plus
     * Tips:
     * 1. 图片需要公网可访问，参考这里获取公网临时地址 https://help.aliyun.com/zh/model-studio/get-temporary-file-url
     * 2. turbo 模型生成速度快，性价比高，默认使用，plus 模型生成速度慢，细节更丰富，效果更好。
     */
    @GetMapping("/first")
    public String firstFrame() {

        return videoModel.call(new VideoPrompt(
                                "一只小猫在草地奔跑",
                                DashScopeVideoOptions.builder()
                                        .model(DashScopeModel.VideoModel.WANX2_1_I2V_TURBO.getValue())
                                        .imageUrl("https://help-static-aliyun-doc.aliyuncs.com/assets/img/zh-CN/6431807371/p906901.png")
                                        .build()
                        )
                ).getResult()
                .getOutput()
                .getVideoUrl();
    }

    /**
     * 基于首尾帧生成视频
     * 使用 wanx2.1-kf2v-plus 模型
     */
    @GetMapping("fl")
    public String firstAndLastFrame() {

        return videoModel.call(new VideoPrompt(
                                "写实风格，一只黑色小猫好奇地看向天空，镜头从平视逐渐上升，最后俯拍小猫好奇的眼神。",
                                DashScopeVideoOptions.builder()
                                        .model(DashScopeModel.VideoModel.WANX2_1_KF2V_PLUS.getValue())
                                        .firstFrameUrl("https://help-static-aliyun-doc.aliyuncs.com/assets/img/zh-CN/8239161571/p944793.png")
                                        .lastFrameUrl("https://help-static-aliyun-doc.aliyuncs.com/assets/img/zh-CN/9239161571/p944794.png")
                                        .build()
                        )
                ).getResult()
                .getOutput()
                .getVideoUrl();
    }

}
