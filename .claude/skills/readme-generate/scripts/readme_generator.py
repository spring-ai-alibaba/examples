#!/usr/bin/env python3
"""
README Generator - Generates README.md files for Spring Boot modules
Usage: python readme_generator.py <module_path> [output_file]
"""

import os
import sys
import re
import shutil
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

    def backup_existing_readme(self, output_path: Path) -> bool:
        """Create backup of existing README.md if it exists"""
        if output_path.exists():
            timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
            backup_path = output_path.parent / f"README_backup_{timestamp}.md"
            try:
                shutil.copy2(output_path, backup_path)
                print(f"📋 Created backup: {backup_path}")
                return True
            except Exception as e:
                print(f"⚠️  Warning: Could not create backup: {e}")
                return False
        return False

    def parse_existing_readme(self, readme_path: Path) -> Dict:
        """Parse existing README.md to extract and preserve content sections"""
        if not readme_path.exists():
            return {'sections': {}, 'raw_content': ''}

        try:
            with open(readme_path, 'r', encoding='utf-8') as f:
                content = f.read()
        except Exception as e:
            print(f"⚠️  Warning: Could not read existing README: {e}")
            return {'sections': {}, 'raw_content': ''}

        sections = {}
        current_section = None
        current_content = []

        lines = content.split('\n')

        for line in lines:
            # Detect section headers
            if line.startswith('#'):
                if current_section:
                    sections[current_section] = '\n'.join(current_content).strip()

                current_section = line.strip()
                current_content = []
            else:
                current_content.append(line)

        # Add the last section
        if current_section:
            sections[current_section] = '\n'.join(current_content).strip()

        return {
            'sections': sections,
            'raw_content': content,
            'has_manual_content': self._has_manual_content(sections)
        }

    def _has_manual_content(self, sections: Dict) -> bool:
        """Check if README contains substantial manual content"""
        manual_indicators = [
            '## 背景', '## 架构设计', '## 快速开始', '## 部署指南',
            '## 开发指南', '## 贡献指南', '## 常见问题', '## 配置说明',
            '## 环境要求', '## 安装步骤', '## 使用指南'
        ]

        for section_title in sections.keys():
            for indicator in manual_indicators:
                if indicator in section_title:
                    return True

        # Check for substantial content in non-generated sections
        for section_title, content in sections.items():
            if not any(keyword in section_title.lower() for keyword in ['接口文档', '模块说明', '技术实现', '测试指导', '注意事项']):
                if len(content) > 200:  # Substantial content
                    return True

        return False

    def merge_content(self, existing_data: Dict, generated_content: str, module_info: Dict) -> str:
        """Merge existing content with generated content using intelligent fusion strategy"""

        if not existing_data['has_manual_content']:
            # No significant manual content, use generated content
            return generated_content

        sections = existing_data['sections']
        merged_content = []

        # Parse generated content into sections for easier processing
        generated_lines = generated_content.split('\n')
        generated_sections = self._parse_sections(generated_lines)

        # Keep existing header if it exists
        main_title = None
        for section_title in sections.keys():
            if section_title.startswith('# ') and not any(keyword in section_title.lower() for keyword in ['模块', '说明', '接口文档', '技术实现', '测试指导', '注意事项']):
                main_title = section_title
                merged_content.append(section_title)
                merged_content.append(sections[section_title])
                break

        if not main_title:
            # Use generated header
            for section_title, content in generated_sections.items():
                if section_title.startswith('# '):
                    merged_content.append(section_title)
                    merged_content.append(content)
                    break

        # Preserve existing manual sections first
        priority_sections = [
            '## 背景', '## 架构设计', '## 快速开始', '## 部署指南',
            '## 开发指南', '## 贡献指南', '## 常见问题', '## 配置说明',
            '## 环境要求', '## 安装步骤', '## 使用指南', '## 功能特性'
        ]

        for section_title in priority_sections:
            for existing_title in sections.keys():
                if section_title in existing_title:
                    merged_content.append(existing_title)
                    merged_content.append(sections[existing_title])
                    break

        # Add generated module description if not present
        has_module_desc = any('模块说明' in title or '模块介绍' in title for title in sections.keys())
        if not has_module_desc and '## 模块说明' in generated_sections:
            merged_content.append('## 模块说明')
            merged_content.append(generated_sections['## 模块说明'])

        # Add generated technical sections (including content)
        tech_sections = ['## 接口文档', '## 技术实现', '## 测试指导', '## 注意事项']
        for section_title in tech_sections:
            if section_title in generated_sections:
                merged_content.append(section_title)
                merged_content.append(generated_sections[section_title])

        # Add any remaining existing sections that weren't processed
        processed_sections = set()
        for section in merged_content:
            if section.startswith('##'):
                processed_sections.add(section.strip())

        for section_title, content in sections.items():
            if section_title.startswith('##') and section_title.strip() not in processed_sections:
                merged_content.append(section_title)
                merged_content.append(content)
                merged_content.append('')

        # Add generation footer
        merged_content.append('---')
        merged_content.append('')
        merged_content.append(f"*此 README.md 由自动化工具融合更新于 {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}*")
        merged_content.append('')
        merged_content.append("*融合策略：保留了原有的技术文档内容，并添加了自动生成的 API 文档部分*")

        return '\n'.join(merged_content)

    def _parse_sections(self, lines: List[str]) -> Dict[str, str]:
        """Parse lines into a dictionary of sections"""
        sections = {}
        current_section = None
        current_content = []

        for line in lines:
            # 只处理 ## 开头的主要章节，不包括 ### 和 ####
            if line.startswith('## '):
                if current_section:
                    sections[current_section] = '\n'.join(current_content).strip()
                current_section = line.strip()
                current_content = []
            else:
                current_content.append(line)

        # Add the last section
        if current_section:
            sections[current_section] = '\n'.join(current_content).strip()

        return sections

    def generate_module_readme(self, module_path: Path, output_file: Optional[str] = None,
                              force_overwrite: bool = False) -> str:
        """Generate README.md for a specific module with intelligent content fusion"""
        if not module_path.exists():
            raise ValueError(f"Module path does not exist: {module_path}")

        print(f"Analyzing module: {module_path}")

        # Extract module information
        module_info = self.extract_module_info(module_path)

        print(f"Found {len(module_info['controllers'])} controllers")
        for controller in module_info['controllers']:
            print(f"  - {controller['name']}: {len(controller['methods'])} methods")

        # Determine output file
        if not output_file:
            output_path = module_path / "README.md"
        else:
            output_path = Path(output_file)

        # Generate new content
        generated_content = self.generate_readme_content(module_info)

        # Check if file exists and decide on merge strategy
        if output_path.exists() and not force_overwrite:
            print(f"📄 Existing README.md found at: {output_path}")

            # Parse existing content
            existing_data = self.parse_existing_readme(output_path)

            if existing_data['has_manual_content']:
                print("🔄 Merging existing content with generated API documentation...")
                final_content = self.merge_content(existing_data, generated_content, module_info)

                # Create backup before merging
                self.backup_existing_readme(output_path)
            else:
                print("📝 No significant manual content found, using generated content...")
                final_content = generated_content

                # Still create backup for safety
                self.backup_existing_readme(output_path)
        else:
            if force_overwrite:
                print("⚠️  Force overwrite mode - creating backup and regenerating...")
                self.backup_existing_readme(output_path)
            else:
                print("📄 No existing README.md found, generating new content...")

            final_content = generated_content

        # Write to file
        try:
            with open(output_path, 'w', encoding='utf-8') as f:
                f.write(final_content)
            print(f"✅ README.md generated successfully: {output_path}")
            return str(output_path)
        except Exception as e:
            print(f"❌ Error writing README.md: {e}")
            raise

def main():
    # Get arguments
    if len(sys.argv) < 2:
        print("Usage: python readme_generator.py <module_path> [output_file] [--force]")
        print("")
        print("Parameters:")
        print("  module_path    Path to the Spring Boot module directory")
        print("  output_file    Optional custom output file path")
        print("  --force        Force overwrite existing README.md")
        print("")
        print("Examples:")
        print("  python readme_generator.py basic/chat")
        print("  python readme_generator.py basic/chat custom_README.md")
        print("  python readme_generator.py basic/chat --force")
        print("")
        print("Behavior:")
        print("  - If README.md exists and contains manual content, will MERGE by default")
        print("  - Creates timestamped backup before any modification")
        print("  - Use --force to completely regenerate content")
        sys.exit(1)

    module_path = Path(sys.argv[1])
    output_file = None
    force_overwrite = False

    # Parse additional arguments
    for arg in sys.argv[2:]:
        if arg == '--force':
            force_overwrite = True
        else:
            output_file = arg

    if not module_path.exists():
        print(f"❌ Error: Module path does not exist: {module_path}")
        sys.exit(1)

    print(f"🚀 Processing module: {module_path}")
    if force_overwrite:
        print("⚠️  Force overwrite mode enabled")

    # Generate README with intelligent fusion
    generator = ReadmeGenerator()
    try:
        output_path = generator.generate_module_readme(module_path, output_file, force_overwrite)
        print(f"🎉 README.md processed successfully: {output_path}")

        # Show backup info if created
        output_path_obj = Path(output_path)
        backup_files = list(output_path_obj.parent.glob("README_backup_*.md"))
        if backup_files:
            latest_backup = max(backup_files, key=os.path.getctime)
            print(f"📋 Backup created: {latest_backup}")

    except Exception as e:
        print(f"❌ Error processing README.md: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()