#!/usr/bin/env python3
"""
README Generator - Generates README.md files for Spring Boot modules
Usage: python readme_generator.py <module_path> [output_file]
"""

import os
import sys
import re
from pathlib import Path
from typing import List, Dict, Tuple, Optional
from datetime import datetime

class ReadmeGenerator:
    def __init__(self):
        self.base_url = "http://localhost:8080"

    def extract_module_info(self, module_path: Path) -> Dict:
        """Extract module information from various sources"""
        module_info = {
            'name': module_path.name,
            'description': '',
            'controllers': [],
            'pom_info': {},
            'config_files': [],
            'main_class': None,
            'http_file': None
        }

        # 1. Check for existing README.md to extract description
        readme_path = module_path / "README.md"
        if readme_path.exists():
            try:
                with open(readme_path, 'r', encoding='utf-8') as f:
                    content = f.read()
                    # Extract first paragraph as description
                    lines = content.split('\n')
                    for line in lines[2:10]:  # Skip title and empty lines
                        if line.strip() and not line.startswith('#'):
                            module_info['description'] = line.strip()
                            break
            except Exception as e:
                print(f"Warning: Could not read existing README.md: {e}")

        # 2. Find controllers
        for java_file in module_path.rglob("*.java"):
            if java_file.name.endswith("Controller.java"):
                controller_info = self.analyze_controller(java_file)
                if controller_info:
                    module_info['controllers'].append(controller_info)

        # 3. Find main application class
        for java_file in module_path.rglob("*.java"):
            if java_file.name.endswith("Application.java"):
                module_info['main_class'] = java_file.stem
                break

        # 4. Find configuration files
        for config_file in module_path.rglob("application.yml"):
            module_info['config_files'].append(config_file)
        for config_file in module_path.rglob("application.properties"):
            module_info['config_files'].append(config_file)

        # 5. Find HTTP test file
        http_file = module_path / f"{module_path.name}.http"
        if http_file.exists():
            module_info['http_file'] = http_file.name

        # 6. Extract POM information
        pom_file = module_path / "pom.xml"
        if pom_file.exists():
            module_info['pom_info'] = self.analyze_pom(pom_file)

        return module_info

    def analyze_controller(self, controller_file: Path) -> Optional[Dict]:
        """Analyze a controller file to extract interface information"""
        try:
            with open(controller_file, 'r', encoding='utf-8') as f:
                content = f.read()

            if '@RestController' not in content and '@Controller' not in content:
                return None

            # Extract base path
            base_path_match = re.search(r'@RequestMapping\(["\']([^"\']+)["\']', content)
            base_path = base_path_match.group(1) if base_path_match else ""

            # Extract methods
            methods = []

            # Split content by lines for better processing
            lines = content.split('\n')

            for i, line in enumerate(lines):
                line = line.strip()

                # Check for mapping annotations
                mapping_match = re.search(r'@(?:GetMapping|PostMapping|PutMapping|DeleteMapping|RequestMapping)\([^)]*["\']([^"\']+)["\']', line)
                if mapping_match:
                    method_path = mapping_match.group(1)

                    # Look for method definition in next few lines
                    method_name = None
                    for j in range(i + 1, min(i + 10, len(lines))):
                        next_line = lines[j].strip()
                        method_match = re.match(r'(?:public|private|protected)\s+[\w<>]+\s+(\w+)\s*\(', next_line)
                        if method_match:
                            method_name = method_match.group(1)
                            break

                    if method_name:
                        # Extract parameters
                        param_section = content[i:i+200]  # Look in the next 200 characters
                        param_pattern = r'@RequestParam\([^)]*value\s*=\s*["\']([^"\']+)["\'][^)]*defaultValue\s*=\s*["\']([^"\']+)["\'][^)]*\)'
                        params = re.findall(param_pattern, param_section)

                        # Extract method comment if available
                        description = ''
                        for k in range(max(0, i-10), i):
                            comment_line = lines[k].strip()
                            if comment_line.startswith('*/'):
                                break
                            if comment_line.startswith('*') and not comment_line.startswith('**'):
                                desc_part = comment_line.replace('*', '').strip()
                                if desc_part:
                                    description = desc_part
                                    break

                        full_path = base_path + method_path if base_path else method_path
                        if not full_path.startswith('/'):
                            full_path = '/' + full_path

                        methods.append({
                            'name': method_name,
                            'path': full_path,
                            'params': params,
                            'description': description
                        })

            
            return {
                'name': controller_file.stem,
                'base_path': base_path,
                'methods': methods
            }

        except Exception as e:
            print(f"Warning: Could not analyze controller {controller_file}: {e}")
            return None

    def analyze_pom(self, pom_file: Path) -> Dict:
        """Extract information from pom.xml"""
        try:
            with open(pom_file, 'r', encoding='utf-8') as f:
                content = f.read()

            info = {}

            # Extract artifactId
            artifact_match = re.search(r'<artifactId>([^<]+)</artifactId>', content)
            if artifact_match:
                info['artifact_id'] = artifact_match.group(1)

            # Extract dependencies
            deps = re.findall(r'<dependency>.*?<artifactId>([^<]+)</artifactId>.*?</dependency>', content, re.DOTALL)
            if deps:
                info['dependencies'] = deps

            return info

        except Exception as e:
            print(f"Warning: Could not analyze POM file {pom_file}: {e}")
            return {}

    def generate_module_description(self, module_info: Dict) -> str:
        """Generate module description based on controller information"""
        if module_info['description']:
            return module_info['description']

        # Generate description from controllers
        controllers = module_info['controllers']
        if not controllers:
            return f"本模块是 {module_info['name']} 模块"

        module_types = []
        for controller in controllers:
            name = controller['name'].lower()
            if 'chat' in name:
                module_types.append('AI对话')
            elif 'time' in name:
                module_types.append('时间工具')
            elif 'search' in name:
                module_types.append('搜索功能')
            elif 'image' in name:
                module_types.append('图像处理')
            elif 'rag' in name:
                module_types.append('RAG增强')
            elif 'advisor' in name:
                module_types.append('对话增强')
            elif 'stream' in name:
                module_types.append('流式处理')
            elif 'parallel' in name:
                module_types.append('并行处理')
            elif 'vector' in name:
                module_types.append('向量数据库')

        if module_types:
            return f"本模块演示 Spring AI Alibaba 的{', '.join(module_types)}功能"
        else:
            return f"本模块是 {module_info['name']} 模块，包含 {len(controllers)} 个控制器"

    def generate_readme_content(self, module_info: Dict) -> str:
        """Generate complete README.md content"""
        module_name = module_info['name'].title()
        description = self.generate_module_description(module_info)
        controllers = module_info['controllers']

        # Header
        content = f"# {module_name} 模块\n\n"

        # Module Description
        content += "## 模块说明\n\n"
        content += f"{description}。\n\n"

        # API Documentation
        if controllers:
            content += "## 接口文档\n\n"

            for controller in controllers:
                if controller['methods']:
                    content += f"### {controller['name']} 接口\n\n"

                    for i, method in enumerate(controller['methods'], 1):
                        content += f"#### {i}. {method['name']} 方法\n\n"
                        content += f"**接口路径：** `GET {method['path']}`\n\n"

                        if method['description']:
                            content += f"**功能描述：** {method['description']}\n\n"
                        else:
                            content += f"**功能描述：** 提供 {method['name']} 相关功能\n\n"

                        content += "**主要特性：**\n"
                        content += "- 基于 Spring Boot REST API 实现\n"
                        if method['params']:
                            content += "- 支持自定义查询参数\n"
                        content += "- 返回 JSON 格式响应\n"
                        content += "- 支持 UTF-8 编码\n\n"

                        content += "**使用场景：**\n"
                        if 'chat' in method['name'].lower():
                            content += "- AI 对话交互\n"
                            content += "- 智能问答系统\n"
                        elif 'time' in method['name'].lower():
                            content += "- 时间相关查询\n"
                            content += "- 工具调用示例\n"
                        elif 'search' in method['name'].lower():
                            content += "- 信息检索\n"
                            content += "- 知识查询\n"
                        else:
                            content += "- 数据处理和响应\n"
                        content += "- API 集成测试\n\n"

                        content += "**示例请求：**\n"
                        content += "```bash\n"
                        url = f"{self.base_url}{method['path']}"
                        if method['params']:
                            params = "&".join([f"{name}={value}" for name, value in method['params']])
                            url += f"?{params}"
                        content += f"GET {url}\n"
                        content += "```\n\n"

                    content += "\n"

        # Technical Implementation
        content += "## 技术实现\n\n"
        content += "### 核心组件\n"
        content += "- **Spring Boot**: 应用框架\n"
        content += "- **Spring AI Alibaba**: AI 功能集成\n"

        if controllers:
            content += "- **REST Controller**: HTTP 接口处理\n"

        if module_info['pom_info'].get('dependencies'):
            deps = module_info['pom_info']['dependencies'][:5]  # Show first 5 dependencies
            for dep in deps:
                content += f"- **{dep}**: 核心依赖\n"

        content += "\n### 配置要点\n"
        content += "- 需要配置 `AI_DASHSCOPE_API_KEY` 环境变量\n"
        content += "- 默认端口：8080\n"
        if controllers:
            content += "- 默认上下文路径：/basic\n"

        content += "\n## 测试指导\n\n"

        # HTTP file testing
        if module_info['http_file']:
            content += "### 使用 HTTP 文件测试\n"
            content += f"模块根目录下提供了 **[{module_info['http_file']}](./{module_info['http_file']})** 文件，包含所有接口的测试用例：\n"
            content += "- 可在 IDE 中直接执行\n"
            content += "- 支持参数自定义\n"
            content += "- 提供默认示例参数\n\n"

        # curl testing
        if controllers:
            content += "### 使用 curl 测试\n"
            for controller in controllers[:2]:  # Show first 2 controllers
                for method in controller['methods'][:1]:  # Show first method per controller
                    url = f"{self.base_url}{method['path']}"
                    if method['params']:
                        params = "&".join([f"{name}={value}" for name, value in method['params']])
                        url += f"?{params}"
                    content += f"```bash\n# {method['name']} 接口测试\ncurl \"{url}\"\n```\n\n"

        # Notes
        content += "## 注意事项\n\n"
        content += "1. **环境变量**: 确保 `AI_DASHSCOPE_API_KEY` 已正确设置\n"
        content += "2. **网络连接**: 需要能够访问阿里云 DashScope 服务\n"
        content += "3. **字符编码**: 所有响应使用 UTF-8 编码，支持中文内容\n"
        content += "4. **端口配置**: 确保端口 8080 未被占用\n\n"

        # Generation info
        content += f"---\n\n"
        content += f"*此 README.md 由自动化工具生成于 {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}*\n"

        return content

    def generate_module_readme(self, module_path: Path, output_file: Optional[str] = None) -> str:
        """Generate README.md for a specific module"""
        if not module_path.exists():
            raise ValueError(f"Module path does not exist: {module_path}")

        print(f"Analyzing module: {module_path}")

        # Extract module information
        module_info = self.extract_module_info(module_path)

        print(f"Found {len(module_info['controllers'])} controllers")
        for controller in module_info['controllers']:
            print(f"  - {controller['name']}: {len(controller['methods'])} methods")

        # Generate content
        content = self.generate_readme_content(module_info)

        # Determine output file
        if not output_file:
            output_file = module_path / "README.md"
        else:
            output_file = Path(output_file)

        # Write to file
        with open(output_file, 'w', encoding='utf-8') as f:
            f.write(content)

        print(f"README.md generated: {output_file}")
        return str(output_file)

def main():
    # Get arguments
    if len(sys.argv) < 2:
        print("Usage: python readme_generator.py <module_path> [output_file]")
        print("Example: python readme_generator.py basic/chat")
        print("Example: python readme_generator.py basic/chat custom_README.md")
        sys.exit(1)

    module_path = Path(sys.argv[1])
    output_file = sys.argv[2] if len(sys.argv) > 2 else None

    if not module_path.exists():
        print(f"Error: Module path does not exist: {module_path}")
        sys.exit(1)

    print(f"Generating README.md for module: {module_path}")

    # Generate README
    generator = ReadmeGenerator()
    try:
        output_path = generator.generate_module_readme(module_path, output_file)
        print(f"✅ README.md generated successfully: {output_path}")
    except Exception as e:
        print(f"❌ Error generating README.md: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()