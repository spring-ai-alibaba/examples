---
name: readme-generate
description: Intelligently generates and fuses README.md files for Spring Boot modules with existing content preservation
---

# README Generator with Content Fusion

This skill automatically analyzes Spring Boot modules and **intelligently fuses** generated API documentation with existing technical content. It preserves important manual documentation while adding comprehensive API documentation, usage examples, technical implementation, testing guidance, and important notes.

## When to Use

Use this skill when you need to:
- **Generate README.md files for new modules**
- **Update existing README.md files without losing valuable content**
- **Merge existing technical documentation with auto-generated API docs**
- **Create consistent documentation across multiple modules**
- **Document API interfaces automatically while preserving existing guides**
- **Provide comprehensive module documentation for developers**

## Key Features

### 🔄 Intelligent Content Fusion
- **Preserves existing technical documentation**: Background, architecture, deployment guides
- **Detects manual content**: Automatically identifies valuable existing sections
- **Smart merging**: Combines generated API docs with existing content
- **Backup protection**: Creates timestamped backups before any modification

### 📋 Backup Strategy
- **Automatic backups**: Timestamped backup files created before any changes
- **Safety first**: Never loses existing content
- **Easy recovery**: Previous versions can be restored anytime

### 🎯 Smart Detection
- Identifies manual sections: Background, Architecture, Quick Start, Deployment, etc.
- Distinguishes between generated content and user-written documentation
- Prioritizes preservation of high-value technical content

## How to Use

### Generate/Fuse README for a Module

```bash
python .claude/skills/readme-generate/scripts/readme_generator.py <module_path> [output_file] [--force]
```

**Parameters:**
- `module_path` (required): Path to the Spring Boot module directory
- `output_file` (optional): Custom output file path (default: `{module_path}/README.md`)
- `--force` (optional): Force complete regeneration instead of fusion

**Examples:**
```bash
# Smart fusion: merges existing docs with new API documentation
python .claude/skills/readme-generate/scripts/readme_generator.py basic/chat

# Force complete regeneration (creates backup first)
python .claude/skills/readme-generate/scripts/readme_generator.py basic/tool --force

# Generate with custom output file
python .claude/skills/readme-generate/scripts/readme_generator.py basic/image custom_README.md

# Update existing README with intelligent merging
python .claude/skills/readme-generate/scripts/readme_generator.py graph/parallel
```

## Content Fusion Strategy

### 🔄 Smart Merging Behavior

**By Default (Fusion Mode):**
1. **Preserves existing manual sections**: Background, Architecture, Deployment Guides, etc.
2. **Adds generated API documentation**: Complete REST API documentation with examples
3. **Intelligent detection**: Automatically identifies valuable existing content
4. **Backup creation**: Timestamped backup before any changes

**With --force Flag:**
1. **Complete regeneration**: Full content replacement
2. **Still creates backup**: Safety is never compromised
3. **Fresh API docs**: All sections regenerated from current code

### 📋 Content Preservation Rules

**Always Preserved:**
- 背景说明 (Background)
- 架构设计 (Architecture Design)
- 快速开始 (Quick Start)
- 部署指南 (Deployment Guide)
- 开发指南 (Development Guide)
- 配置说明 (Configuration Instructions)
- 环境要求 (Environment Requirements)
- 功能特性 (Feature Overview)

**Always Generated/Fresh:**
- 接口文档 (API Documentation) - Complete REST API coverage
- 技术实现 (Technical Implementation) - Current code analysis
- 测试指导 (Testing Guidance) - Latest testing procedures
- 注意事项 (Important Notes) - Current environment requirements

## Generated README Structure

The skill intelligently merges existing documentation with automatically generated sections:

### Preserved Sections (Existing Content)
- **Background & Architecture**: User-written technical documentation
- **Guides & Tutorials**: Step-by-step instructions and best practices
- **Configuration**: Environment setup and deployment instructions
- **Custom Content**: Any valuable manual documentation

### Generated Sections (Fresh Content)

#### 1. 模块功能介绍 (Module Functionality Description)
- Automatically extracts module functionality from controller classes
- Generates comprehensive descriptions based on actual implementation
- Provides clear overview of module purpose and capabilities

#### 2. 接口文档 (API Documentation)
- **Complete API Coverage**: All REST endpoints documented
- **Path Information**: Full endpoint paths with HTTP methods
- **Parameter Details**: All request parameters with default values
- **Response Format**: JSON response structure
- **Feature Lists**: Key capabilities and characteristics
- **Usage Examples**: Ready-to-use curl commands

#### 3. 技术实现 (Technical Implementation)
- **Core Components**: Key Spring Boot and Spring AI Alibaba components
- **Configuration Details**: Environment variables and configuration requirements
- **Dependencies**: Important Maven/Gradle dependencies
- **Architecture Overview**: Module structure and design patterns

#### 4. 测试指导 (Testing Guidance)
- **HTTP File Testing**: Direct links to generated .http test files
- **curl Commands**: Command-line testing examples
- **IDE Integration**: IntelliJ IDEA and VS Code testing guidance
- **Parameter Testing**: Different parameter combinations

#### 5. 注意事项 (Important Notes)
- **Environment Setup**: Required environment variables and configurations
- **Network Requirements**: Service connectivity requirements
- **Character Encoding**: UTF-8 support for international content
- **Port Configuration**: Default port usage and customization
- **Common Issues**: Troubleshooting guidance

## Input Analysis Capabilities

The skill automatically analyzes:

### Source Code Analysis
- **Controller Classes**: Extracts REST endpoints and methods
- **Method Annotations**: @GetMapping, @PostMapping, @RequestMapping patterns
- **Parameter Extraction**: @RequestParam with default values
- **Comments and Javadoc**: Method descriptions and documentation

### Configuration Analysis
- **pom.xml**: Maven dependencies and project information
- **application.yml/properties**: Configuration details
- **Main Application Classes**: Application entry points

### File Structure Analysis
- **Existing README.md**: Preserves current descriptions when available
- **HTTP Test Files**: Links to generated .http test files
- **Directory Structure**: Module organization and components

## Generated Content Features

### Automated Content Generation
- **Dynamic Descriptions**: Based on actual code implementation
- **Smart Categorization**: Automatic identification of module types
- **Parameter Extraction**: Complete parameter documentation with defaults
- **URL Construction**: Full endpoint URLs with parameters

### Format Compliance
- **Markdown Standards**: Proper formatting and structure
- **Chinese Support**: Full UTF-8 support for Chinese content
- **Link Generation**: Automatic internal links to related files
- **Code Blocks**: Proper syntax highlighting for code examples

### Consistency and Quality
- **Standardized Structure**: Consistent format across all modules
- **Comprehensive Coverage**: All aspects of the module documented
- **Up-to-date Information**: Based on current source code
- **Professional Presentation**: Clear and developer-friendly documentation

## Module Type Detection

The skill automatically identifies module types based on controller names:

- **Chat Modules**: AI对话功能
- **Tool Modules**: 工具调用功能
- **Image Modules**: 图像处理功能
- **RAG Modules**: RAG增强功能
- **Advisor Modules**: 对话增强功能
- **Stream Modules**: 流式处理功能
- **Parallel Modules**: 并行处理功能
- **Vector Modules**: 向量数据库功能
- **Search Modules**: 搜索功能

## File Organization

### Output Location
- **Default**: `{module_path}/README.md`
- **Custom**: User-specified path
- **Backup**: Existing README.md is backed up automatically

### Integration with Existing Files
- **Preserves**: Existing descriptions and custom content
- **Updates**: Adds missing sections and updates content
- **Backups**: Creates backup of existing README.md before modification

## Customization Options

### Modify Generation Behavior
Edit `scripts/readme_generator.py` to customize:

- **Module Type Detection**: Add new module type patterns
- **Content Templates**: Modify section templates and formats
- **Parameter Extraction**: Enhance parameter analysis logic
- **Description Generation**: Improve automatic description creation

### Extend Functionality
Add support for:
- **Additional File Types**: Analyze more configuration files
- **Advanced Documentation**: Include more technical details
- **Integration Examples**: Add framework-specific examples
- **Testing Automation**: Generate automated test scripts

## Usage Scenarios

### New Module Development
```bash
# After creating a new module-generate.md, generate documentation
python .claude/skills/readme-generate/scripts/readme_generator.py basic/new-module-generate.md
```

### Module Updates
```bash
# After modifying module-generate.md content, update documentation
python .claude/skills/readme-generate/scripts/readme_generator.py basic/updated-module-generate.md
```

### Batch Documentation
```bash
# Generate documentation for multiple modules
for module-generate.md in basic/*; do
  if [ -d "$module" ]; then
    python .claude/skills/readme-generate/scripts/readme_generator.py "$module"
  fi
done
```

## Integration with Development Workflow

### Continuous Documentation
- **Synchronization**: Keep documentation in sync with code changes
- **Automated Updates**: Regenerate documentation after feature changes
- **Quality Assurance**: Ensure all modules have consistent documentation

### Team Collaboration
- **Onboarding**: Helps new developers understand module functionality
- **API Reference**: Provides complete API documentation for all team members
- **Testing Guidance**: Facilitates testing and quality assurance

## Notes

- The skill automatically creates backups of existing README.md files
- All generated content uses UTF-8 encoding for international character support
- Requires Python 3 with no external dependencies
- Supports complex Spring Boot application structures
- Preserves custom content while adding missing sections
- Links automatically to generated HTTP test files when available

## Error Handling

Common issues and solutions:

- **No Controllers Found**: Ensure module contains REST controller classes
- **Missing Dependencies**: Verify Spring Boot and Spring AI dependencies
- **Encoding Issues**: All files are processed with UTF-8 encoding
- **File Permissions**: Ensure write permissions for target directory
- **Complex Structures**: Skill handles nested module structures automatically