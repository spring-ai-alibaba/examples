---
name: readme-generate
description: Generates comprehensive README.md files for Spring Boot modules based on task specification requirements
---

# README Generator

This skill automatically analyzes Spring Boot modules and generates comprehensive README.md files according to the specified format requirements, including module functionality, API documentation, usage examples, technical implementation, testing guidance, and important notes.

## When to Use

Use this skill when you need to:

- Generate README.md files for new modules
- Update existing README.md files when module content changes
- Create consistent documentation across multiple modules
- Document API interfaces automatically
- Provide comprehensive module documentation for developers

## How to Use

### Generate README for a Module

Execute the README generator script to create comprehensive documentation:

```bash
python .claude/skills/readme-generate/scripts/readme_generator.py <module_path> [output_file]
```

**Parameters:**
- `module_path` (required): Path to the Spring Boot module directory
- `output_file` (optional): Custom output file path (default: `{module_path}/README.md`)

**Examples:**
```bash
# Generate for chat module-generate.md (creates basic/chat/README.md)
python .claude/skills/readme-generate/scripts/readme_generator.py basic/chat

# Generate for tool module-generate.md with custom output
python .claude/skills/readme-generate/scripts/readme_generator.py basic/tool custom_README.md

# Generate for image module-generate.md
python .claude/skills/readme-generate/scripts/readme_generator.py basic/image

# Update existing README
python .claude/skills/readme-generate/scripts/readme_generator.py graph/parallel
```

## Generated README Structure

The skill generates README.md files that strictly follow the task specification:

### 1. 模块功能介绍 (Module Functionality Description)
- Automatically extracts module functionality from controller classes
- Generates comprehensive descriptions based on actual implementation
- Provides clear overview of module purpose and capabilities

### 2. 接口文档 (API Documentation)
- **Complete API Coverage**: All REST endpoints documented
- **Path Information**: Full endpoint paths with HTTP methods
- **Parameter Details**: All request parameters with default values
- **Response Format**: JSON response structure
- **Feature Lists**: Key capabilities and characteristics

### 3. 使用示例 (Usage Examples)

- **HTTP Requests**: Ready-to-use curl commands
- **Parameter Examples**: Default parameter values for testing
- **Integration Examples**: Different usage scenarios
- **Tool Integration**: Links to corresponding .http files

### 4. 技术实现 (Technical Implementation)
- **Core Components**: Key Spring Boot and Spring AI Alibaba components
- **Configuration Details**: Environment variables and configuration requirements
- **Dependencies**: Important Maven/Gradle dependencies
- **Architecture Overview**: Module structure and design patterns

### 5. 测试指导 (Testing Guidance)
- **HTTP File Testing**: Direct links to generated .http test files
- **curl Commands**: Command-line testing examples
- **IDE Integration**: IntelliJ IDEA and VS Code testing guidance
- **Parameter Testing**: Different parameter combinations

### 6. 注意事项 (Important Notes)
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