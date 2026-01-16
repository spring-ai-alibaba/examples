#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
NetworkConfigDemo 接口压力测试脚本
用于测试自定义HTTP客户端的正确性，以及长时间不调用后的连接问题

python3 -m venv .venv
source .venv/bin/activate
pip install requests
python load_test.py

在此目录下生成 json 测试文件，可查看测试结果
"""

import requests
import time
import threading
from concurrent.futures import ThreadPoolExecutor, as_completed
from datetime import datetime
import statistics
import json


class LoadTester:
    def __init__(self, base_url="http://localhost:10000"):
        self.base_url = base_url
        self.call_url = f"{base_url}/cfg/call"
        self.stream_url = f"{base_url}/cfg/stream"
        self.results = {
            "call": [],
            "stream": []
        }
        
    def test_call_endpoint(self, request_id):
        """测试 /cfg/call 接口"""
        start_time = time.time()
        try:
            response = requests.get(self.call_url, timeout=30)
            elapsed = time.time() - start_time
            
            result = {
                "request_id": request_id,
                "status_code": response.status_code,
                "elapsed_time": elapsed,
                "success": response.status_code == 200,
                "response_length": len(response.text),
                "timestamp": datetime.now().strftime("%Y-%m-%d %H:%M:%S")
            }
            
            if not result["success"]:
                result["error"] = f"HTTP {response.status_code}"
                
            return result
            
        except Exception as e:
            elapsed = time.time() - start_time
            return {
                "request_id": request_id,
                "status_code": 0,
                "elapsed_time": elapsed,
                "success": False,
                "error": str(e),
                "timestamp": datetime.now().strftime("%Y-%m-%d %H:%M:%S")
            }
    
    def test_stream_endpoint(self, request_id):
        """测试 /cfg/stream 接口"""
        start_time = time.time()
        try:
            response = requests.get(self.stream_url, stream=True, timeout=30)
            chunks_received = 0
            total_content = ""
            
            for chunk in response.iter_content(chunk_size=1024, decode_unicode=True):
                if chunk:
                    chunks_received += 1
                    total_content += chunk
                    
            elapsed = time.time() - start_time
            
            result = {
                "request_id": request_id,
                "status_code": response.status_code,
                "elapsed_time": elapsed,
                "success": response.status_code == 200,
                "chunks_received": chunks_received,
                "response_length": len(total_content),
                "timestamp": datetime.now().strftime("%Y-%m-%d %H:%M:%S")
            }
            
            if not result["success"]:
                result["error"] = f"HTTP {response.status_code}"
                
            return result
            
        except Exception as e:
            elapsed = time.time() - start_time
            return {
                "request_id": request_id,
                "status_code": 0,
                "elapsed_time": elapsed,
                "success": False,
                "error": str(e),
                "timestamp": datetime.now().strftime("%Y-%m-%d %H:%M:%S")
            }
    
    def run_load_test(self, endpoint_type, num_requests=50, concurrent_workers=10):
        """
        执行压力测试
        
        Args:
            endpoint_type: 'call' 或 'stream'
            num_requests: 总请求数
            concurrent_workers: 并发线程数
        """
        print(f"\n{'='*80}")
        print(f"开始测试 /{endpoint_type} 接口")
        print(f"总请求数: {num_requests}, 并发数: {concurrent_workers}")
        print(f"开始时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        print(f"{'='*80}\n")
        
        test_func = self.test_call_endpoint if endpoint_type == "call" else self.test_stream_endpoint
        
        start_time = time.time()
        results = []
        
        with ThreadPoolExecutor(max_workers=concurrent_workers) as executor:
            futures = [executor.submit(test_func, i+1) for i in range(num_requests)]
            
            completed = 0
            for future in as_completed(futures):
                result = future.result()
                results.append(result)
                completed += 1
                
                # 实时进度显示
                if completed % 10 == 0 or completed == num_requests:
                    success_count = sum(1 for r in results if r["success"])
                    print(f"进度: {completed}/{num_requests} | 成功: {success_count} | 失败: {completed - success_count}")
        
        total_time = time.time() - start_time
        
        # 保存结果
        self.results[endpoint_type] = results
        
        # 打印统计信息
        self._print_statistics(endpoint_type, results, total_time)
        
        return results
    
    def _print_statistics(self, endpoint_type, results, total_time):
        """打印统计信息"""
        print(f"\n{'='*80}")
        print(f"/{endpoint_type} 接口测试结果")
        print(f"{'='*80}")
        
        success_results = [r for r in results if r["success"]]
        failed_results = [r for r in results if not r["success"]]
        
        print(f"\n总体统计:")
        print(f"  总请求数: {len(results)}")
        print(f"  成功请求: {len(success_results)} ({len(success_results)/len(results)*100:.2f}%)")
        print(f"  失败请求: {len(failed_results)} ({len(failed_results)/len(results)*100:.2f}%)")
        print(f"  总耗时: {total_time:.2f}秒")
        print(f"  平均QPS: {len(results)/total_time:.2f}")
        
        if success_results:
            response_times = [r["elapsed_time"] for r in success_results]
            print(f"\n响应时间统计 (成功请求):")
            print(f"  最小值: {min(response_times):.3f}秒")
            print(f"  最大值: {max(response_times):.3f}秒")
            print(f"  平均值: {statistics.mean(response_times):.3f}秒")
            print(f"  中位数: {statistics.median(response_times):.3f}秒")
            if len(response_times) > 1:
                print(f"  标准差: {statistics.stdev(response_times):.3f}秒")
            
            # 百分位数
            sorted_times = sorted(response_times)
            p50 = sorted_times[int(len(sorted_times) * 0.5)]
            p90 = sorted_times[int(len(sorted_times) * 0.9)]
            p95 = sorted_times[int(len(sorted_times) * 0.95)]
            p99 = sorted_times[int(len(sorted_times) * 0.99)]
            
            print(f"\n百分位数:")
            print(f"  P50: {p50:.3f}秒")
            print(f"  P90: {p90:.3f}秒")
            print(f"  P95: {p95:.3f}秒")
            print(f"  P99: {p99:.3f}秒")
        
        if failed_results:
            print(f"\n失败请求详情:")
            error_types = {}
            for r in failed_results:
                error = r.get("error", "Unknown")
                error_types[error] = error_types.get(error, 0) + 1
            
            for error, count in error_types.items():
                print(f"  {error}: {count} 次")
        
        print(f"\n{'='*80}\n")
    
    def wait_and_test(self, wait_minutes=10):
        """等待指定时间后再次测试，模拟长时间不调用的场景"""
        print(f"\n{'#'*80}")
        print(f"等待 {wait_minutes} 分钟后再次测试...")
        print(f"这是为了验证长时间不调用后是否会出现连接失败问题")
        print(f"开始等待时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        print(f"{'#'*80}\n")
        
        # 显示倒计时
        for remaining in range(wait_minutes * 60, 0, -30):
            mins, secs = divmod(remaining, 60)
            print(f"剩余等待时间: {mins:02d}:{secs:02d}")
            time.sleep(30)
        
        print(f"\n等待结束，开始第二轮测试...")
        print(f"结束等待时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n")
    
    def save_results_to_file(self, filename="load_test_results.json"):
        """保存测试结果到文件"""
        with open(filename, 'w', encoding='utf-8') as f:
            json.dump(self.results, f, ensure_ascii=False, indent=2)
        print(f"\n测试结果已保存到: {filename}")


def main():
    """主函数"""
    print(f"\n{'*'*80}")
    print(f"NetworkConfigDemo HTTP 客户端压力测试")
    print(f"{'*'*80}\n")
    
    # 可以根据需要修改以下参数
    BASE_URL = "http://localhost:10000"  # 应用地址
    NUM_REQUESTS = 100  # 每个接口的请求数
    CONCURRENT_WORKERS = 20  # 并发数
    WAIT_MINUTES = 10  # 等待时间（分钟）
    
    tester = LoadTester(base_url=BASE_URL)
    
    # ========== 第一轮测试 ==========
    print("\n" + "="*80)
    print("第一轮压力测试开始")
    print("="*80)
    
    # 测试 /cfg/call 接口
    tester.run_load_test("call", num_requests=NUM_REQUESTS, concurrent_workers=CONCURRENT_WORKERS)
    
    # 短暂休息
    time.sleep(2)
    
    # 测试 /cfg/stream 接口
    tester.run_load_test("stream", num_requests=NUM_REQUESTS, concurrent_workers=CONCURRENT_WORKERS)
    
    # ========== 等待指定时间 ==========
    tester.wait_and_test(wait_minutes=WAIT_MINUTES)
    
    # ========== 第二轮测试 ==========
    print("\n" + "="*80)
    print("第二轮压力测试开始（验证长时间不调用后的连接问题）")
    print("="*80)
    
    # 再次测试 /cfg/call 接口
    print("\n[第二轮] 测试 /cfg/call 接口")
    tester.run_load_test("call", num_requests=NUM_REQUESTS, concurrent_workers=CONCURRENT_WORKERS)
    
    # 短暂休息
    time.sleep(2)
    
    # 再次测试 /cfg/stream 接口
    print("\n[第二轮] 测试 /cfg/stream 接口")
    tester.run_load_test("stream", num_requests=NUM_REQUESTS, concurrent_workers=CONCURRENT_WORKERS)
    
    # ========== 保存结果 ==========
    tester.save_results_to_file()
    
    print("\n" + "="*80)
    print("所有测试完成!")
    print("="*80 + "\n")
    
    # 打印总结
    print("\n总结:")
    print("如果第二轮测试出现较多失败，可能说明存在以下问题:")
    print("1. HTTP连接池的空闲连接被服务器关闭")
    print("2. maxIdleTime 设置过短")
    print("3. 连接池没有正确处理过期连接")
    print("\n建议检查 NetworkConfigDemo 中的:")
    print("- ConnectionProvider 的 maxIdleTime 配置")
    print("- HttpClient 的 responseTimeout 配置")
    print("- 连接池的健康检查机制\n")


if __name__ == "__main__":
    main()
