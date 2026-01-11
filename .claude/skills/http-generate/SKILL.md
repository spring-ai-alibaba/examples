---
name: http-generate
description: Generates HTTP request examples for Spring Boot Web interfaces according to task specification and saves them as .http files in module-generate.md directories
---

# HTTP Generator

This skill automatically analyzes Spring Boot Web controllers and generates HTTP request examples for all REST endpoints, saving them as `.http` files in their respective module directories according to the task specification.

## When to Use

Use this skill when you need to:
- Generate API documentation for testing
- Create HTTP request examples for new modules
- Document existing Spring Boot REST controllers
- Provide ready-to-use request samples for developers
- Automate API testing setup
- Create interface specifications for team collaboration

## How to Use

### Generate HTTP Requests for a Single Module

Execute the HTTP generator script to create request examples for all controllers in a module:

```bash
python .claude/skills/http-generate/scripts/http_generator.py <module_path> [output_file]
```

### Generate HTTP Requests for All Modules

Use the `all` parameter to automatically discover and generate HTTP files for all modules with controllers:

```bash
python .claude/skills/http-generate/scripts/http_generator.py all
```

**Parameters:**
- `module_path` (required): Path to the Spring Boot module directory, or `all` to process all modules
- `output_file` (optional): Custom output file path (default: `{module_name}.http`)

**Examples:**
```bash
# Generate for chat module-generate.md (creates basic/chat/chat.http)
python .claude/skills/http-generate/scripts/http_generator.py basic/chat

# Generate for all modules automatically
python .claude/skills/http-generate/scripts/http_generator.py all

# Generate for graph module-generate.md (creates graph/graph.http)
python .claude/skills/http-generate/scripts/http_generator.py graph/parallel

# Generate with custom output file
python .claude/skills/http-generate/scripts/http_generator.py basic/chat custom-api.http

# Generate for different modules
python .claude/skills/http-generate/scripts/http_generator.py basic/tool
python .claude/skills/http-generate/scripts/http_generator.py basic/image
python .claude/skills/http-generate/scripts/http_generator.py graph/stream
```

### Supported Controller Patterns

The skill automatically detects and generates requests for:

#### HTTP Methods
- **GET** `@GetMapping` - Query operations
- **POST** `@PostMapping` - Create operations
- **PUT** `@PutMapping` - Update operations
- **DELETE** `@DeleteMapping` - Delete operations

#### Parameter Types
- **`@RequestParam`** with default values
- **`@RequestParam`** without default values
- **`@PathVariable`** for path parameters
- **URL encoding** for Chinese characters and special values

#### Controller Annotations
- **`@RestController`** - JSON API endpoints
- **`@Controller`** - MVC controllers
- **`@RequestMapping`** - Base path mapping

### Output Format (Task Specification)

The skill generates `.http` files according to the task specification format:

#### Basic Format
```http
# {Controller类名}的{方法名}方法
{HTTP_METHOD} {完整URL路径}?{参数键值对}

###
# {Controller类名}的{方法名}方法
{HTTP_METHOD} {完整URL路径}?{参数键值对}
```

#### Generated Example
```http
# ChatController类的callChat方法
GET http://localhost:8080/basic/chat/call?query=你好，很高兴认识你，能简单介绍一下自己吗？

###
# ChatController类的streamChat方法
GET http://localhost:8080/basic/chat/stream?query=你好，很高兴认识你，能简单介绍一下自己吗？

###
# ChatController类的callOption方法
GET http://localhost:8080/basic/chat/call/option?query=你好，很高兴认识你，能简单介绍一下自己吗？
```

### Generated Request Features

Each generated HTTP request includes:

- **Method Documentation**: Comment format `# Controller类名的方法名方法`
- **Complete URL**: Full endpoint URL with base path
- **Default Parameters**: All @RequestParam values with defaults
- **Proper Encoding**: URL encoding for Chinese characters
- **Query String**: Properly formatted parameter strings with `?` and `&`
- **Separators**: `###` used to separate different interfaces
- **File Organization**: Grouped by controller and module

### Module Discovery

The skill automatically:

1. **Discovers Modules**: Scans project directories for modules containing controllers
2. **Identifies Controllers**: Files ending with `Controller.java`
3. **Analyzes Annotations**: Extracts mapping annotations
4. **Extracts Parameters**: Finds @RequestParam configurations
5. **Builds URLs**: Constructs complete request URLs
6. **Generates Examples**: Creates ready-to-use HTTP requests

### Supported Spring Boot Patterns

#### Example 1: Simple GET Controller
```java
@RestController
@RequestMapping("/basic/chat")
public class ChatController {

    @GetMapping("/call")
    public String callChat(
        @RequestParam(value = "query",
                    defaultValue = "你好，很高兴认识你，能简单介绍一下自己吗？",
                    required = false) String query) {
        return chatClient.prompt(query).call().content();
    }
}
```

**Generated Request:**
```http
# ChatController类的callChat方法
GET http://localhost:8080/basic/chat/call?query=你好，很高兴认识你，能简单介绍一下自己吗？
```

#### Example 2: Complex Parameters
```java
@GetMapping("/expand-translate")
public Map<String, Object> expandAndTranslate(
        @RequestParam(value = "query", defaultValue = "你好", required = false) String query,
        @RequestParam(value = "expander_number", defaultValue = "3", required = false) Integer expanderNumber,
        @RequestParam(value = "translate_language", defaultValue = "english", required = false) String translateLanguage) {
    // implementation
}
```

**Generated Request:**
```http
# ParallelController类的expandAndTranslate方法
GET http://localhost:8080/graph/parallel/expand-translate?query=你好&expander_number=3&translate_language=english
```

#### Example 3: Multiple Controllers
For modules with multiple controllers, the skill generates requests for all with `###` separators:

```http
# TimeController类的call方法
GET http://localhost:8080/basic/tool/time/call?query=请告诉我现在北京时间几点了

###
# TimeController类的callToolFunction方法
GET http://localhost:8080/basic/tool/time/call/function?query=请告诉我现在北京时间几点了

###
# SearchController类的search方法
GET http://localhost:8080/basic/tool/search?query=Spring+AI
```

## File Organization (Task Specification)

### Output Location
- **Default**: `{module_name}/{module_name}.http`
- **File Naming**: Uses module name as filename (e.g., `chat.http`, `tool.http`)
- **Custom**: User-specified path
- **Encoding**: UTF-8 for Chinese characters

### Directory Structure Example
```
basic/
├── chat/
│   ├── chat.http          ← Generated file
│   └── src/main/java/.../ChatController.java
├── tool/
│   ├── tool.http          ← Generated file
│   └── src/main/java/.../TimeController.java
└── image/
    ├── image.http         ← Generated file
    └── src/main/java/.../ImageController.java
```

### Task Specification Compliance

The generated files strictly follow the task specification:

1. **文件命名**: 以模块名称命名，使用 `.http` 后缀 ✅
2. **文件位置**: 放置在对应模块的根目录下 ✅
3. **内容来源**: 基于 Controller 类中的接口方法生成 ✅
4. **注释格式**: 使用 `# Controller类名的方法名方法` ✅
5. **分隔符**: 使用 `###` 分隔不同接口 ✅
6. **参数处理**: 正确处理查询参数拼接 ✅

## Integration with Development Workflow

### Use with IDE HTTP Client
The generated `.http` files work seamlessly with:
- **IntelliJ IDEA HTTP Client**
- **VS Code REST Client**
- **Postman Import**
- **curl commands**

### Automated Documentation
Perfect for:
- **API documentation generation**
- **Testing automation**
- **Developer onboarding**
- **Contract testing**
- **Integration testing setup**

## Customization Options

### Modify Script Behavior
Edit `scripts/http_generator.py` to customize:

- **Base URL**: Change from `http://localhost:8080`
- **Output Format**: Modify request template according to specification
- **Parameter Handling**: Add support for other annotations
- **File Patterns**: Change controller detection logic
- **Encoding**: Adjust URL encoding rules

### Extend Functionality
Add support for:
- **POST body parameters**
- **Header handling**
- **Authentication headers**
- **Multi-parameter requests**
- **File upload examples**

## Notes

- The script automatically discovers all modules with controllers when using `all` parameter
- Only processes files ending with `Controller.java`
- Supports UTF-8 encoding for Chinese parameters
- Requires Python 3 and no external dependencies
- Handles complex Spring Boot annotation patterns
- Preserves parameter defaults from source code
- Groups requests by controller for clarity
- Follows task specification format exactly

## Error Handling

Common issues and solutions:

- **No controllers found**: Ensure module contains `*Controller.java` files
- **Missing @RequestMapping**: Controllers without base mapping will use root path
- **Complex parameters**: Some advanced parameter types may need manual adjustment
- **Encoding issues**: Chinese characters are automatically URL-encoded
- **Module discovery**: Use `all` parameter to auto-discover modules with controllers