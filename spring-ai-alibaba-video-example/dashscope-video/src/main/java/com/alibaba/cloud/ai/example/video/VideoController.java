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

import com.alibaba.cloud.ai.dashscope.api.DashScopeVideoApi;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import com.alibaba.cloud.ai.dashscope.video.DashScopeVideoModel;
import com.alibaba.cloud.ai.dashscope.video.DashScopeVideoOptions;
import com.alibaba.cloud.ai.dashscope.video.VideoPrompt;
import com.alibaba.cloud.ai.dashscope.video.VideoResponse;
import com.alibaba.cloud.ai.example.video.util.FileUtil;
import com.alibaba.cloud.ai.example.video.util.VideoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.alibaba.cloud.ai.dashscope.video.DashScopeVideoOptions.InputOptions;
import static com.alibaba.cloud.ai.dashscope.video.DashScopeVideoOptions.ParametersOptions;

/**
 * Video Example
 *
 * @author yuluo，yingzi
 * 本示例是百炼-视频生成所有能力展现示例
 * refer: https://help.aliyun.com/zh/model-studio/image-to-video-api-reference
 */
@RestController
@RequestMapping("/ai/video")
public class VideoController {

	private static final Logger logger = LoggerFactory.getLogger(VideoController.class);

	private static final String API_KEY_ENV = "AI_DASHSCOPE_API_KEY";
	private final String SAVE_PATH = System.getProperty("user.dir") + "/spring-ai-alibaba-video-example/dashscope-video/src/main/resources/";

	private final DashScopeVideoModel videoModel;

    public VideoController() {
		DashScopeVideoApi videoApi = DashScopeVideoApi.builder().apiKey(System.getenv(API_KEY_ENV)).build();
		RetryTemplate retryTemplate = RetryTemplate.builder().maxAttempts(30) // Increase max attempts for video generation
				.fixedBackoff(10000) // 10 seconds between retries
				.build();

		this.videoModel = DashScopeVideoModel.builder()
				.videoApi(videoApi)
				.retryTemplate(retryTemplate)
				.build();

	}

    /**
	 * 通义万相-图生视频-基于首帧
	 */
	@GetMapping("/first")
	public ResponseEntity<?> first() {
		String prompt = "一幅都市奇幻艺术的场景。一个充满动感的涂鸦艺术角色。一个由喷漆所画成的少年，正从一面混凝土墙上活过来。他一边用极快的语速演唱一首英文rap，一边摆着一个经典的、充满活力的说唱歌手姿势。场景设定在夜晚一个充满都市感的铁路桥下。灯光来自一盏孤零零的街灯，营造出电影般的氛围，充满高能量和惊人的细节。视频的音频部分完全由他的rap构成，没有其他对话或杂音。";
		String imgUrl = "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250925/wpimhv/rap.png";
		String audioUrl = "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250925/ozwpvi/rap.mp3";
		// Build options matching curl command
		DashScopeVideoOptions options = DashScopeVideoOptions.builder()
				.model(DashScopeModel.VideoModel.WAN26_I2V_FLASH.getName())
				.input(InputOptions.builder()
						.prompt(prompt)
						.imgUrl(imgUrl)
						.audioUrl(audioUrl)
						.build()
				)
				.parameters(ParametersOptions.builder()
						.resolution("720P")
						.promptExtend(true)
						.duration(10)
						.shotType("multi")
					.build())
				.build();

		VideoPrompt videoPrompt = VideoPrompt.builder()
				.options(options)
				.build();

		VideoResponse videoResponse = videoModel.call(videoPrompt);
		String videoUrl = videoResponse.getResult().getOutput().videoUrl();
		logger.info("视频生成成功，URL: {}", videoUrl);

		// 生成文件名
		String fileExtension = VideoUtil.getVideoExtension(videoUrl);
		String fileName = "基于首帧——多镜头叙事" + fileExtension;

		// 保存到当前模块的 resources 目录
		String filePath = SAVE_PATH + fileName;
		boolean success = FileUtil.url2File(videoUrl, filePath);

		if (success) {
			logger.info("视频保存成功: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + videoUrl + ", 已保存到: " + filePath);
		} else {
			logger.error("视频保存失败: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + videoUrl + ", 但保存失败");
		}
	}

	/**
	 * 通义万相-图生视频-基于首尾帧
	 */
	@GetMapping("/first-last")
	public ResponseEntity<?> firstLast() {
		String prompt = "写实风格，一只黑色小猫好奇地看向天空，镜头从平视逐渐上升，最后俯拍它的好奇的眼神。";
		String firstFrameUrl = "https://wanx.alicdn.com/material/20250318/first_frame.png";
		String lastFrameUrl = "https://wanx.alicdn.com/material/20250318/last_frame.png";
		// Build options matching curl command
		DashScopeVideoOptions options = DashScopeVideoOptions.builder()
				.model(DashScopeModel.VideoModel.WAN22_KF2V_FLASH.getName())
				.input(InputOptions.builder()
						.prompt(prompt)
						.firstFrameUrl(firstFrameUrl)
						.lastFrameUrl(lastFrameUrl)
						.build()
				)
				.parameters(ParametersOptions.builder()
						.resolution("480P")
						.promptExtend(true)
						.build())
				.build();

		VideoPrompt videoPrompt = VideoPrompt.builder()
				.options(options)
				.build();

		VideoResponse videoResponse = videoModel.call(videoPrompt);
		String videoUrl = videoResponse.getResult().getOutput().videoUrl();
		logger.info("视频生成成功，URL: {}", videoUrl);

		// 生成文件名
		String fileExtension = VideoUtil.getVideoExtension(videoUrl);
		String fileName = "基于首尾帧——首位帧生视频" + fileExtension;

		// 保存到当前模块的 resources 目录
		String filePath = SAVE_PATH + fileName;
		boolean success = FileUtil.url2File(videoUrl, filePath);

		if (success) {
			logger.info("视频保存成功: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + videoUrl + ", 已保存到: " + filePath);
		} else {
			logger.error("视频保存失败: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + videoUrl + ", 但保存失败");
		}
	}

	/**
	 * 通义万相-图声视频-视频特效
	 */
	@GetMapping("/video-effects")
	public ResponseEntity<?> videoEffects() {
		String imgUrl = "https://cdn.translate.alibaba.com/r/wanx-demo-1.png";
		// Build options matching curl command
		DashScopeVideoOptions options = DashScopeVideoOptions.builder()
				.model(DashScopeModel.VideoModel.WANX21_I2V_TURBO.getName())
				.input(InputOptions.builder()
						.imgUrl(imgUrl)
						.template("flying")
						.build()
				)
				.parameters(ParametersOptions.builder()
						.resolution("720P")
						.build())
				.build();

		VideoPrompt videoPrompt = VideoPrompt.builder()
				.options(options)
				.build();

		VideoResponse videoResponse = videoModel.call(videoPrompt);
		String videoUrl = videoResponse.getResult().getOutput().videoUrl();
		logger.info("视频生成成功，URL: {}", videoUrl);

		// 生成文件名
		String fileExtension = VideoUtil.getVideoExtension(videoUrl);
		String fileName = "视频特效" + fileExtension;

		// 保存到当前模块的 resources 目录
		String filePath = SAVE_PATH + fileName;
		boolean success = FileUtil.url2File(videoUrl, filePath);

		if (success) {
			logger.info("视频保存成功: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + videoUrl + ", 已保存到: " + filePath);
		} else {
			logger.error("视频保存失败: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + videoUrl + ", 但保存失败");
		}
	}

	/**
	 * 通义万相-参考生视频
	 */
	@GetMapping("/reference-video")
	public ResponseEntity<?> referenceVideo() {
		String prompt = "character1一边喝奶茶，一边随着音乐即兴跳舞。";
		String referenceVideoUrl = "https://cdn.wanx.aliyuncs.com/static/demo-wan26/vace.mp4";
		// Build options matching curl command
		DashScopeVideoOptions options = DashScopeVideoOptions.builder()
				.model(DashScopeModel.VideoModel.WAN26_R2V.getName())
				.input(InputOptions.builder()
						.prompt(prompt)
						.referenceVideoUrls(List.of(referenceVideoUrl))
						.build()
				)
				.parameters(ParametersOptions.builder()
						.size("1280*720")
						.duration(5)
						.shotType("multi")
						.build())
				.build();

		VideoPrompt videoPrompt = VideoPrompt.builder()
				.options(options)
				.build();

		VideoResponse videoResponse = videoModel.call(videoPrompt);
		String videoUrl = videoResponse.getResult().getOutput().videoUrl();
		logger.info("视频生成成功，URL: {}", videoUrl);

		// 生成文件名
		String fileExtension = VideoUtil.getVideoExtension(videoUrl);
		String fileName = "参考生视频" + fileExtension;

		// 保存到当前模块的 resources 目录
		String filePath = SAVE_PATH + fileName;
		boolean success = FileUtil.url2File(videoUrl, filePath);

		if (success) {
			logger.info("视频保存成功: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + videoUrl + ", 已保存到: " + filePath);
		} else {
			logger.error("视频保存失败: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + videoUrl + ", 但保存失败");
		}
	}

	/**
	 * 通义万相-文生视频（多镜头叙事）
	 */
	@GetMapping("/t2v-multi-shot")
	public ResponseEntity<?> t2vMultiShot() {
		String prompt = "一幅史诗级可爱的场景。一只小巧可爱的卡通小猫将军，身穿细节精致的金色盔甲，头戴一个稍大的头盔，勇敢地站在悬崖上。他骑着一匹虽小但英勇的战马，说：\\\"青海长云暗雪山，孤城遥望玉门关。黄沙百战穿金甲，不破楼兰终不还。\\\"悬崖下方，一支由老鼠组成的、数量庞大、无穷无尽的军队正带着临时制作的武器向前冲锋。这是一个戏剧性的、大规模的战斗场景，灵感来自中国古代的战争史诗。远处的雪山上空，天空乌云密布。整体氛围是\\\"可爱\\\"与\\\"霸气\\\"的搞笑和史诗般的融合。";
		String audioUrl = "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250923/hbiayh/%E4%BB%8E%E5%86%9B%E8%A1%8C.mp3";

		DashScopeVideoOptions options = DashScopeVideoOptions.builder()
				.model(DashScopeModel.VideoModel.WAN26_T2V.getName())
				.input(InputOptions.builder()
						.prompt(prompt)
						.audioUrl(audioUrl)
						.build()
				)
				.parameters(ParametersOptions.builder()
						.size("1280*720")
						.promptExtend(true)
						.duration(10)
						.shotType("multi")
						.build())
				.build();

		VideoPrompt videoPrompt = VideoPrompt.builder()
				.options(options)
				.build();

		VideoResponse videoResponse = videoModel.call(videoPrompt);
		String responseVideoUrl = videoResponse.getResult().getOutput().videoUrl();
		logger.info("视频生成成功，URL: {}", responseVideoUrl);

		String fileExtension = VideoUtil.getVideoExtension(responseVideoUrl);
		String fileName = "通义万相-文生视频多镜头叙事" + fileExtension;
		String filePath = SAVE_PATH + fileName;
		boolean success = FileUtil.url2File(responseVideoUrl, filePath);

		if (success) {
			logger.info("视频保存成功: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + responseVideoUrl + ", 已保存到: " + filePath);
		} else {
			logger.error("视频保存失败: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + responseVideoUrl + ", 但保存失败");
		}
	}

	/**
	 * 通义万相-通用视频编辑
	 */
	@GetMapping("/image-reference")
	public ResponseEntity<?> imageReference() {
		String prompt = "视频中，一位女孩自晨雾缭绕的古老森林深处款款走出，她步伐轻盈，镜头捕捉她每一个灵动瞬间。当女孩站定，环顾四周葱郁林木时，她脸上绽放出惊喜与喜悦交织的笑容。这一幕，定格在了光影交错的瞬间，记录下女孩与大自然的美妙邂逅。";
		String refImageUrl1 = "http://wanx.alicdn.com/material/20250318/image_reference_2_5_16.png";
		String refImageUrl2 = "http://wanx.alicdn.com/material/20250318/image_reference_1_5_16.png";

		DashScopeVideoOptions options = DashScopeVideoOptions.builder()
				.model(DashScopeModel.VideoModel.WANX21_VACE_PLUS.getName())
				.input(InputOptions.builder()
						.function("image_reference")
						.prompt(prompt)
						.refImagesUrl(List.of(refImageUrl1, refImageUrl2))
						.build()
				)
				.parameters(ParametersOptions.builder()
						.promptExtend(true)
						.objOrBg(List.of("obj", "bg"))
						.size("1280*720")
						.build())
				.build();

		VideoPrompt videoPrompt = VideoPrompt.builder()
				.options(options)
				.build();

		VideoResponse videoResponse = videoModel.call(videoPrompt);
		String responseVideoUrl = videoResponse.getResult().getOutput().videoUrl();
		logger.info("视频生成成功，URL: {}", responseVideoUrl);

		String fileExtension = VideoUtil.getVideoExtension(responseVideoUrl);
		String fileName = "通用视频编辑-多图参考" + fileExtension;
		String filePath = SAVE_PATH + fileName;
		boolean success = FileUtil.url2File(responseVideoUrl, filePath);

		if (success) {
			logger.info("视频保存成功: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + responseVideoUrl + ", 已保存到: " + filePath);
		} else {
			logger.error("视频保存失败: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + responseVideoUrl + ", 但保存失败");
		}
	}

	/**
	 * 通义万相-图生动作
	 */
	@GetMapping("/animate-move")
	public ResponseEntity<?> animateMove() {
		String imageUrl = "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250919/adsyrp/move_input_image.jpeg";
		String videoUrl = "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250919/kaakcn/move_input_video.mp4";

		DashScopeVideoOptions options = DashScopeVideoOptions.builder()
				.model(DashScopeModel.VideoModel.WAN22_ANIMATE_MOVE.getName())
				.input(InputOptions.builder()
						.imageUrl(imageUrl)
						.videoUrl(videoUrl)
						.build()
				)
				.parameters(ParametersOptions.builder()
						.mode("wan-std")
						.build())
				.build();

		VideoPrompt videoPrompt = VideoPrompt.builder()
				.options(options)
				.build();

		VideoResponse videoResponse = videoModel.call(videoPrompt);
		String responseVideoUrl = videoResponse.getResult().getOutput().results().videoUrl();
		logger.info("视频生成成功，URL: {}", responseVideoUrl);

		String fileExtension = VideoUtil.getVideoExtension(responseVideoUrl);
		String fileName = "图生动作" + fileExtension;
		String filePath = SAVE_PATH + fileName;
		boolean success = FileUtil.url2File(responseVideoUrl, filePath);

		if (success) {
			logger.info("视频保存成功: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + responseVideoUrl + ", 已保存到: " + filePath);
		} else {
			logger.error("视频保存失败: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + responseVideoUrl + ", 但保存失败");
		}
	}

	/**
	 * 通义万相-视频换人
	 */
	@GetMapping("/animate-mix")
	public ResponseEntity<?> animateMix() {
		String imageUrl = "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250919/bhkfor/mix_input_image.jpeg";
		String videoUrl = "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250919/wqefue/mix_input_video.mp4";

		DashScopeVideoOptions options = DashScopeVideoOptions.builder()
				.model(DashScopeModel.VideoModel.WAN22_ANIMATE_MIX.getName())
				.input(InputOptions.builder()
						.imageUrl(imageUrl)
						.videoUrl(videoUrl)
						.build()
				)
				.parameters(ParametersOptions.builder()
						.mode("wan-std")
						.build())
				.build();

		VideoPrompt videoPrompt = VideoPrompt.builder()
				.options(options)
				.build();

		VideoResponse videoResponse = videoModel.call(videoPrompt);
		String responseVideoUrl = videoResponse.getResult().getOutput().results().videoUrl();
		logger.info("视频生成成功，URL: {}", responseVideoUrl);

		String fileExtension = VideoUtil.getVideoExtension(responseVideoUrl);
		String fileName = "视频换人" + fileExtension;
		String filePath = SAVE_PATH + fileName;
		boolean success = FileUtil.url2File(responseVideoUrl, filePath);

		if (success) {
			logger.info("视频保存成功: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + responseVideoUrl + ", 已保存到: " + filePath);
		} else {
			logger.error("视频保存失败: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + responseVideoUrl + ", 但保存失败");
		}
	}

	/**
	 * 通义万相-数字人
	 */
	@GetMapping("/s2v")
	public ResponseEntity<?> s2v() {
		String imageUrl = "https://img.alicdn.com/imgextra/i3/O1CN011FObkp1T7Ttowoq4F_!!6000000002335-0-tps-1440-1797.jpg";
		String audioUrl = "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250825/iaqpio/input_audio.MP3";

		DashScopeVideoOptions options = DashScopeVideoOptions.builder()
				.model(DashScopeModel.VideoModel.WAN22_S2V.getName())
				.input(InputOptions.builder()
						.imageUrl(imageUrl)
						.audioUrl(audioUrl)
						.build()
				)
				.parameters(ParametersOptions.builder()
						.resolution("480P")
						.build())
				.build();

		VideoPrompt videoPrompt = VideoPrompt.builder()
				.options(options)
				.build();

		VideoResponse videoResponse = videoModel.call(videoPrompt);
		String responseVideoUrl = videoResponse.getResult().getOutput().results().videoUrl();
		logger.info("视频生成成功，URL: {}", responseVideoUrl);

		String fileExtension = VideoUtil.getVideoExtension(responseVideoUrl);
		String fileName = "数字人视频" + fileExtension;
		String filePath = SAVE_PATH + fileName;
		boolean success = FileUtil.url2File(responseVideoUrl, filePath);

		if (success) {
			logger.info("视频保存成功: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + responseVideoUrl + ", 已保存到: " + filePath);
		} else {
			logger.error("视频保存失败: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + responseVideoUrl + ", 但保存失败");
		}
	}

	/**
	 * 图生舞蹈视频-舞动人像（AnimateAnyone）
	 */
	@GetMapping("/animate-anyone")
	public ResponseEntity<?> animateAnyone() {
		String imageUrl = "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20251224/pkswoc/p883941.png";
		String templateId = "AACT.xxx.xxx-xxx.xxx";

		DashScopeVideoOptions options = DashScopeVideoOptions.builder()
				.model(DashScopeModel.VideoModel.ANIMATE_ANYONE_GEN2.getName())
				.input(InputOptions.builder()
						.imgUrl(imageUrl)
						.templateId(templateId)
						.build()
				)
				.parameters(ParametersOptions.builder()
						.useRefImgBg(false)
						.videoRatio("9:16")
						.build())
				.build();

		VideoPrompt videoPrompt = VideoPrompt.builder()
				.options(options)
				.build();

		VideoResponse videoResponse = videoModel.call(videoPrompt);
		String responseVideoUrl = videoResponse.getResult().getOutput().videoUrl();
		logger.info("视频生成成功，URL: {}", responseVideoUrl);

		String fileExtension = VideoUtil.getVideoExtension(responseVideoUrl);
		String fileName = "图声舞蹈视频-舞动人像AnimateAnyone" + fileExtension;
		String filePath = SAVE_PATH + fileName;
		boolean success = FileUtil.url2File(responseVideoUrl, filePath);

		if (success) {
			logger.info("视频保存成功: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + responseVideoUrl + ", 已保存到: " + filePath);
		} else {
			logger.error("视频保存失败: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + responseVideoUrl + ", 但保存失败");
		}
	}

	/**
	 * 图生演唱视频-悦动人像EMO
	 */
	@GetMapping("/emo")
	public ResponseEntity<?> emo() {
		String imageUrl = "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20251225/onmomb/emo.png";
		String audioUrl = "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250825/aejgyj/input_audio.mp3";

		DashScopeVideoOptions options = DashScopeVideoOptions.builder()
				.model(DashScopeModel.VideoModel.EMO_V1.getName())
				.input(InputOptions.builder()
						.imageUrl(imageUrl)
						.audioUrl(audioUrl)
						.faceBbox(List.of(302, 286, 610, 593))
						.extBbox(List.of(71, 9, 840, 778))
						.build()
				)
				.parameters(ParametersOptions.builder()
						.styleLevel("normal")
						.build())
				.build();

		VideoPrompt videoPrompt = VideoPrompt.builder()
				.options(options)
				.build();

		VideoResponse videoResponse = videoModel.call(videoPrompt);
		String responseVideoUrl = videoResponse.getResult().getOutput().results().videoUrl();
		logger.info("视频生成成功，URL: {}", responseVideoUrl);

		String fileExtension = VideoUtil.getVideoExtension(responseVideoUrl);
		String fileName = "悦动人像EMO" + fileExtension;
		String filePath = SAVE_PATH + fileName;
		boolean success = FileUtil.url2File(responseVideoUrl, filePath);

		if (success) {
			logger.info("视频保存成功: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + responseVideoUrl + ", 已保存到: " + filePath);
		} else {
			logger.error("视频保存失败: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + responseVideoUrl + ", 但保存失败");
		}
	}

	/**
	 * 图生播报视频-灵动人像
	 */
	@GetMapping("/liveportrait")
	public ResponseEntity<?> livePortrait() {
		String imageUrl = "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250911/ynhjrg/p874909.png";
		String audioUrl = "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20251226/fwnqyq/liveportrait_boy.mp3";

		DashScopeVideoOptions options = DashScopeVideoOptions.builder()
				.model(DashScopeModel.VideoModel.LIVEPORTRAIT.getName())
				.input(InputOptions.builder()
						.imageUrl(imageUrl)
						.audioUrl(audioUrl)
						.build()
				)
				.parameters(ParametersOptions.builder()
						.templateId("normal")
						.eyeMoveFreq(0.5)
						.videoFps(30)
						.mouthMoveStrength(1)
						.pasteBack(true)
						.headMoveStrength(0.7)
						.build())
				.build();

		VideoPrompt videoPrompt = VideoPrompt.builder()
				.options(options)
				.build();

		VideoResponse videoResponse = videoModel.call(videoPrompt);
		String responseVideoUrl = videoResponse.getResult().getOutput().results().videoUrl();
		logger.info("视频生成成功，URL: {}", responseVideoUrl);

		String fileExtension = VideoUtil.getVideoExtension(responseVideoUrl);
		String fileName = "灵动人像LivePortrait" + fileExtension;
		String filePath = SAVE_PATH + fileName;
		boolean success = FileUtil.url2File(responseVideoUrl, filePath);

		if (success) {
			logger.info("视频保存成功: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + responseVideoUrl + ", 已保存到: " + filePath);
		} else {
			logger.error("视频保存失败: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + responseVideoUrl + ", 但保存失败");
		}
	}

	/**
	 * 视频口型替换-声动人像
	 */
	@GetMapping("/videoretalk")
	public ResponseEntity<?> videoRetalk() {
		String videoUrl = "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250717/pvegot/input_video_01.mp4";
		String audioUrl = "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250717/aumwir/stella2-%E6%9C%89%E5%A3%B0%E4%B9%A67.wav";

		DashScopeVideoOptions options = DashScopeVideoOptions.builder()
				.model(DashScopeModel.VideoModel.VIDEORETALK.getName())
				.input(InputOptions.builder()
						.videoUrl(videoUrl)
						.audioUrl(audioUrl)
						.refImageUrl("")
						.build()
				)
				.parameters(ParametersOptions.builder()
						.videoExtension(false)
						.build())
				.build();

		VideoPrompt videoPrompt = VideoPrompt.builder()
				.options(options)
				.build();

		VideoResponse videoResponse = videoModel.call(videoPrompt);
		String responseVideoUrl = videoResponse.getResult().getOutput().videoUrl();
		logger.info("视频生成成功，URL: {}", responseVideoUrl);

		String fileExtension = VideoUtil.getVideoExtension(responseVideoUrl);
		String fileName = "视频口型替换" + fileExtension;
		String filePath = SAVE_PATH + fileName;
		boolean success = FileUtil.url2File(responseVideoUrl, filePath);

		if (success) {
			logger.info("视频保存成功: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + responseVideoUrl + ", 已保存到: " + filePath);
		} else {
			logger.error("视频保存失败: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + responseVideoUrl + ", 但保存失败");
		}
	}

	/**
	 * 图生表情包视频-表情包Emoji
	 */
	@GetMapping("/emoji")
	public ResponseEntity<?> emoji() {
		String imageUrl = "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250912/uopnly/emoji-%E5%9B%BE%E5%83%8F%E6%A3%80%E6%B5%8B.png";

		DashScopeVideoOptions options = DashScopeVideoOptions.builder()
				.model(DashScopeModel.VideoModel.EMOJI_V1.getName())
				.input(InputOptions.builder()
						.imageUrl(imageUrl)
						.drivenId("mengwa_kaixin")
						.faceBbox(List.of(212, 194, 460, 441))
						.extBbox(List.of(63, 30, 609, 575))
						.build()
				)
				.build();

		VideoPrompt videoPrompt = VideoPrompt.builder()
				.options(options)
				.build();

		VideoResponse videoResponse = videoModel.call(videoPrompt);
		String responseVideoUrl = videoResponse.getResult().getOutput().videoUrl();
		logger.info("视频生成成功，URL: {}", responseVideoUrl);

		String fileExtension = VideoUtil.getVideoExtension(responseVideoUrl);
		String fileName = "表情包视频" + fileExtension;
		String filePath = SAVE_PATH + fileName;
		boolean success = FileUtil.url2File(responseVideoUrl, filePath);

		if (success) {
			logger.info("视频保存成功: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + responseVideoUrl + ", 已保存到: " + filePath);
		} else {
			logger.error("视频保存失败: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + responseVideoUrl + ", 但保存失败");
		}
	}

	/**
	 * 视频风格重绘
	 */
	@GetMapping("/video-style-transform")
	public ResponseEntity<?> videoStyleTransform() {
		String videoUrl = "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250704/viwndw/%E5%8E%9F%E8%A7%86%E9%A2%91.mp4";

		DashScopeVideoOptions options = DashScopeVideoOptions.builder()
				.model(DashScopeModel.VideoModel.VIDEO_STYLE_TRANSFORM.getName())
				.input(InputOptions.builder()
						.videoUrl(videoUrl)
						.build()
				)
				.parameters(ParametersOptions.builder()
						.style(0)
						.videoFps(15)
						.build())
				.build();

		VideoPrompt videoPrompt = VideoPrompt.builder()
				.options(options)
				.build();

		VideoResponse videoResponse = videoModel.call(videoPrompt);
		String responseVideoUrl = videoResponse.getResult().getOutput().outputVideoUrl();
		logger.info("视频生成成功，URL: {}", responseVideoUrl);

		String fileExtension = VideoUtil.getVideoExtension(responseVideoUrl);
		String fileName = "视频风格重绘" + fileExtension;
		String filePath = SAVE_PATH + fileName;
		boolean success = FileUtil.url2File(responseVideoUrl, filePath);

		if (success) {
			logger.info("视频保存成功: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + responseVideoUrl + ", 已保存到: " + filePath);
		} else {
			logger.error("视频保存失败: {}", filePath);
			return ResponseEntity.ok("视频生成成功，URL: " + responseVideoUrl + ", 但保存失败");
		}
	}

}
