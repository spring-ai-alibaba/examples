#!/usr/bin/env python3
"""
HTTP Generator - Generates HTTP request examples for Spring Boot Web controllers
Usage: python http_generator.py [module_path] [output_file]
"""

import os
import sys
import re
import ast
from pathlib import Path
from typing import List, Dict, Tuple, Optional

class HttpMethodGenerator:
    def __init__(self, base_url: Optional[str] = None, module_path: Optional[Path] = None):
        # If base_url is provided, use it directly
        if base_url:
            self.base_url = base_url
        else:
            # Try to read port from application.yml
            port = self.read_port_from_config(module_path) if module_path else None
            if port:
                self.base_url = f"http://localhost:{port}"
            else:
                self.base_url = "http://localhost:8080"

        self.method_patterns = {
            'GET': r'@GetMapping\([^)]*["\']([^"\']+)["\']',
            'POST': r'@PostMapping\([^)]*["\']([^"\']+)["\']',
            'PUT': r'@PutMapping\([^)]*["\']([^"\']+)["\']',
            'DELETE': r'@DeleteMapping\([^)]*["\']([^"\']+)["\']',
        }
        self.param_patterns = {
            'request_param': r'@RequestParam\(\s*value\s*=\s*["\']([^"\']+)["\'][^)]*defaultValue\s*=\s*["\']([^"\']+)["\'][^)]*\)',
            'request_param_no_default': r'@RequestParam\(\s*value\s*=\s*["\']([^"\']+)["\'][^)]*?\)',
            'path_variable': r'@PathVariable\(\s*["\']?([^"\']+)["\']?\s*\)',
        }

    def read_port_from_config(self, module_path: Path) -> Optional[int]:
        """Read server port from application.yml or application.properties"""
        if not module_path:
            return None

        # Look for application.yml or application.properties in src/main/resources
        resources_dir = module_path / "src" / "main" / "resources"
        if not resources_dir.exists():
            return None

        # Try application.yml first
        yml_file = resources_dir / "application.yml"
        if yml_file.exists():
            try:
                with open(yml_file, 'r', encoding='utf-8') as f:
                    content = f.read()
                    # Use regex to find server.port in YAML
                    # Matches patterns like:
                    # server:
                    #   port: 8080
                    # or
                    # server.port: 8080
                    patterns = [
                        r'server:\s*\n\s*port:\s*(\d+)',  # YAML format with line break
                        r'server\.port:\s*(\d+)',         # YAML inline format
                    ]
                    for pattern in patterns:
                        match = re.search(pattern, content)
                        if match:
                            return int(match.group(1))
            except Exception:
                pass

        # Try application.yaml
        yaml_file = resources_dir / "application.yaml"
        if yaml_file.exists():
            try:
                with open(yaml_file, 'r', encoding='utf-8') as f:
                    content = f.read()
                    patterns = [
                        r'server:\s*\n\s*port:\s*(\d+)',
                        r'server\.port:\s*(\d+)',
                    ]
                    for pattern in patterns:
                        match = re.search(pattern, content)
                        if match:
                            return int(match.group(1))
            except Exception:
                pass

        # Try application.properties
        props_file = resources_dir / "application.properties"
        if props_file.exists():
            try:
                with open(props_file, 'r', encoding='utf-8') as f:
                    for line in f:
                        line = line.strip()
                        if line.startswith('server.port='):
                            port_str = line.split('=', 1)[1].strip()
                            return int(port_str)
            except Exception:
                pass

        return None

    def extract_base_path(self, content: str) -> str:
        """Extract base path from @RequestMapping annotation and normalize it"""
        match = re.search(r'@RequestMapping\(["\']([^"\']+)["\']', content)
        if match:
            path = match.group(1)
            # Normalize path: ensure it starts with "/" if not empty
            if path and not path.startswith('/'):
                path = '/' + path
            return path
        return ""

    def extract_method_mappings(self, content: str) -> List[Tuple[str, str, str]]:
        """Extract method mappings and return (method, path, method_name)"""
        mappings = []

        # Split content by lines for better processing
        lines = content.split('\n')

        for i, line in enumerate(lines):
            line = line.strip()

            # Check if this line contains a mapping annotation
            for http_method, pattern in self.method_patterns.items():
                match = re.search(pattern, line)
                if match:
                    method_path = match.group(1)

                    # Look for the method definition in the next few lines
                    method_name = None
                    for j in range(i + 1, min(i + 10, len(lines))):
                        next_line = lines[j].strip()
                        # Match method definition
                        method_match = re.match(r'(?:public|private|protected)\s+(?:\w+(?:<[^>]+>)?)\s+(\w+)\s*\(', next_line)
                        if method_match:
                            method_name = method_match.group(1)
                            break

                    if method_name:
                        mappings.append((http_method, method_path, method_name))
                    break

        return mappings

    def extract_parameters(self, method_content: str) -> Dict[str, str]:
        """Extract parameters from method signature"""
        params = {}

        # Extract @RequestParam with defaultValue
        param_matches = re.finditer(self.param_patterns['request_param'], method_content)
        for match in param_matches:
            param_name = match.group(1)
            default_value = match.group(2)
            params[param_name] = default_value

        # Extract @RequestParam without defaultValue
        param_matches = re.finditer(self.param_patterns['request_param_no_default'], method_content)
        for match in param_matches:
            param_name = match.group(1)
            if param_name not in params:
                params[param_name] = ""

        return params

    def extract_full_method_content(self, content: str, method_name: str) -> str:
        """Extract the full method content including signature"""
        # Look for method signature with parameters (handle multi-line parameters)
        lines = content.split('\n')

        for i, line in enumerate(lines):
            if re.search(rf'(?:public|private|protected)\s+[\w<>]+\s+{method_name}\s*\(', line):
                # Found method start, collect until we reach the closing parenthesis
                method_lines = [line]
                j = i + 1
                paren_count = line.count('(') - line.count(')')

                while j < len(lines) and paren_count > 0:
                    method_lines.append(lines[j])
                    paren_count += lines[j].count('(') - lines[j].count(')')
                    j += 1

                # Include the return type and rest of line after parenthesis
                if j < len(lines):
                    # Add the rest of the current line and maybe next lines
                    remaining_content = ' '.join(lines[j:j+3])
                    method_lines.append(remaining_content)

                return '\n'.join(method_lines)

        # Fallback: try simple regex
        signature_pattern = rf'(?:public|private|protected)\s+[\w<>]+\s+{method_name}\s*\([^)]*\)[^;]*'
        match = re.search(signature_pattern, content, re.DOTALL)
        return match.group(0) if match else ""

    def generate_http_request(self, http_method: str, base_path: str,
                            method_path: str, method_name: str,
                            controller_name: str, params: Dict[str, str]) -> str:
        """Generate HTTP request example according to task specification"""
        # Normalize method_path: ensure it starts with "/"
        if method_path and not method_path.startswith('/'):
            method_path = '/' + method_path

        # Construct full URL by concatenating base_path and method_path
        # Both paths should already start with "/" (if non-empty) after normalization
        # We need to avoid double slashes, so we strip the leading "/" from method_path if base_path is not empty
        if base_path:
            if method_path.startswith('/'):
                full_path = base_path + method_path
            else:
                full_path = base_path + '/' + method_path
        else:
            full_path = method_path if method_path else "/"

        url = f"{self.base_url}{full_path}"

        # Generate query parameters
        query_params = []
        for param_name, default_value in params.items():
            if default_value:
                # Encode Chinese characters properly
                if any(ord(c) > 127 for c in default_value):
                    # Simple URL encoding for Chinese characters
                    encoded_value = default_value.replace(' ', '%20')
                else:
                    encoded_value = default_value
                query_params.append(f"{param_name}={encoded_value}")

        # Construct request according to specification
        request = f"# {controller_name}类的{method_name}方法\n"
        request += f"{http_method} {url}"

        if query_params:
            request += "?" + "&".join(query_params)

        request += "\n"
        return request

    def process_java_file(self, file_path: Path) -> Optional[str]:
        """Process a single Java file and generate HTTP requests"""
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()

            # Check if it's a REST controller
            if '@RestController' not in content and '@Controller' not in content:
                return None

            # Extract base path
            base_path = self.extract_base_path(content)

            # Extract method mappings
            mappings = self.extract_method_mappings(content)

            if not mappings:
                return None

            # Extract controller name (without .java extension)
            controller_name = file_path.stem

            # Generate HTTP requests
            http_requests = []

            for i, (http_method, method_path, method_name) in enumerate(mappings):
                # Extract method content for parameter analysis
                method_content = self.extract_full_method_content(content, method_name)
                params = self.extract_parameters(method_content)

                # Generate HTTP request according to specification
                http_request = self.generate_http_request(
                    http_method, base_path, method_path, method_name, controller_name, params
                )
                http_requests.append(http_request)

                # Add ### separator between methods (except for the last one)
                if i < len(mappings) - 1:
                    http_requests.append("###")

            return "\n".join(http_requests)

        except Exception as e:
            print(f"Error processing {file_path}: {e}")
            return None

    def find_controller_files(self, module_path: Path) -> List[Path]:
        """Find all controller files in the module"""
        controller_files = []

        # Look for files ending with Controller.java
        for java_file in module_path.rglob("*.java"):
            if java_file.name.endswith("Controller.java"):
                controller_files.append(java_file)

        return controller_files

    def generate_module_http_file(self, module_path: Path, output_file: Optional[str] = None) -> str:
        """Generate HTTP file for an entire module according to task specification"""
        if not module_path.exists():
            raise ValueError(f"Module path does not exist: {module_path}")

        # Find controller files
        controller_files = self.find_controller_files(module_path)

        if not controller_files:
            print(f"No controller files found in {module_path}")
            return ""

        print(f"Found {len(controller_files)} controller files:")
        for file in controller_files:
            print(f"  - {file}")

        # Generate content for each controller
        all_requests = []

        for i, controller_file in enumerate(controller_files):
            print(f"Processing {controller_file.name}...")
            controller_requests = self.process_java_file(controller_file)

            if controller_requests:
                all_requests.append(controller_requests)

                # Add ### separator between controllers (except for the last one)
                if i < len(controller_files) - 1:
                    all_requests.append("###")

        content = "\n".join(all_requests)

        # Determine output file name according to specification
        if not output_file:
            module_name = module_path.name
            output_file = module_path / f"{module_name}.http"
        else:
            output_file = Path(output_file)

        # Write to file
        with open(output_file, 'w', encoding='utf-8') as f:
            f.write(content)

        print(f"HTTP requests generated: {output_file}")
        print(f"Total controllers processed: {len(controller_files)}")

        return str(output_file)

def discover_modules_with_controllers(project_path: Path) -> List[Path]:
    """Discover all modules that contain controller files, excluding parent modules"""
    modules_with_controllers = []

    # Look for directories that contain controller files
    for item in project_path.iterdir():
        if item.is_dir() and not item.name.startswith('.') and item.name != '.claude':
            # Check if this directory contains controller files
            controller_files = list(item.rglob("*Controller.java"))
            if controller_files:
                # Check if this is a parent module (contains subdirectories with controllers)
                # If it has subdirectories that also contain controllers, it's likely a parent module
                has_submodules_with_controllers = False

                for subitem in item.iterdir():
                    if subitem.is_dir():
                        sub_controller_files = list(subitem.rglob("*Controller.java"))
                        # Check if subdirectory has its own controllers (not just inherited from parent)
                        sub_direct_controllers = [f for f in sub_controller_files if f.parent == subitem or 'src/main/java' in str(f.parent)]
                        if sub_direct_controllers:
                            has_submodules_with_controllers = True
                            break

                # Only add if it's not a parent module with submodules containing controllers
                if not has_submodules_with_controllers:
                    modules_with_controllers.append(item)

    return sorted(modules_with_controllers)

def discover_all_submodules_with_controllers(project_path: Path) -> List[Path]:
    """Discover all submodules (not parent modules) that contain controller files directly"""
    submodules_with_controllers = set()

    # Find all controller files and extract their module paths
    all_controllers = list(project_path.rglob("*Controller.java"))

    for controller_file in all_controllers:
        # Convert to absolute path and extract parts
        abs_path = controller_file.resolve()
        path_parts = abs_path.parts

        # Find 'src/main/java' in the path
        src_java_idx = -1
        for i, part in enumerate(path_parts):
            if part == 'src' and i + 2 < len(path_parts) and path_parts[i + 1] == 'main' and path_parts[i + 2] == 'java':
                src_java_idx = i
                break

        if src_java_idx > 0:
            # The module is everything before 'src'
            module_path = Path(*path_parts[:src_java_idx])
            submodules_with_controllers.add(module_path)

    return sorted(list(submodules_with_controllers))

def generate_all_modules(project_path: Path) -> List[str]:
    """Generate HTTP files for all modules with controllers"""
    modules = discover_all_submodules_with_controllers(project_path)
    generated_files = []

    print(f"Discovered {len(modules)} modules with controllers:")
    for module in modules:
        print(f"  - {module}")

    print("\nGenerating HTTP files...")

    for module in modules:
        try:
            print(f"\nProcessing module: {module}")
            # Create a new generator for each module to read the correct port
            generator = HttpMethodGenerator(module_path=module)
            output_path = generator.generate_module_http_file(module)
            if output_path:
                generated_files.append(output_path)
        except Exception as e:
            print(f"Error processing module {module}: {e}")

    return generated_files

def main():
    # Get arguments
    if len(sys.argv) < 2:
        print("Usage: python http_generator.py <module_path|'all'> [output_file]")
        print("Examples:")
        print("  python http_generator.py basic/chat                    # Generate for specific module")
        print("  python http_generator.py all                           # Generate for all modules")
        print("  python http_generator.py basic/chat custom.http        # Generate with custom output file")
        sys.exit(1)

    module_arg = sys.argv[1]
    output_file = sys.argv[2] if len(sys.argv) > 2 else None

    if module_arg.lower() == 'all':
        # Generate for all modules
        project_path = Path('.')
        print(f"Discovering modules in project: {project_path.absolute()}")

        generated_files = generate_all_modules(project_path)

        if generated_files:
            print(f"\n✅ Successfully generated {len(generated_files)} HTTP files:")
            for file_path in generated_files:
                print(f"  - {file_path}")
        else:
            print("❌ No HTTP files were generated. Check if modules contain controllers.")

    else:
        # Generate for specific module
        module_path = Path(module_arg)

        if not module_path.exists():
            print(f"Error: Module path does not exist: {module_path}")
            sys.exit(1)

        print(f"Generating HTTP requests for module: {module_path}")

        # Generate HTTP file with module_path to read the correct port
        generator = HttpMethodGenerator(module_path=module_path)
        try:
            output_path = generator.generate_module_http_file(module_path, output_file)
            print(f"✅ HTTP file generated successfully: {output_path}")
        except Exception as e:
            print(f"❌ Error generating HTTP file: {e}")
            sys.exit(1)

if __name__ == "__main__":
    main()