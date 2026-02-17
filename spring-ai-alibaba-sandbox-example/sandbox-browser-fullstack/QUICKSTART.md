# Browser Use Fullstack - Quick Start Guide

## Overview

This full-stack application demonstrates intelligent browser automation using Spring AI Alibaba's BrowserSandbox. It combines a Spring Boot backend with a React frontend to provide an interactive chat interface where users can instruct an AI agent to perform web browsing tasks.

## Architecture

```
┌─────────────────┐     HTTP/SSE/WebSocket      ┌─────────────────┐
│   React UI      │  ◄───────────────────────►  │  Spring Boot    │
│  (Port 5173)    │                             │   (Port 8080)   │
└─────────────────┘                             └────────┬────────┘
                                                         │
                                                         │ Docker
                                                         ▼
                                              ┌─────────────────┐
                                              │ BrowserSandbox  │
                                              │   Container     │
                                              └─────────────────┘
```

## Prerequisites

1. **Java Development Kit (JDK) 17+**
   ```bash
   java -version
   ```

2. **Docker**
   ```bash
   docker --version
   docker ps  # Verify Docker is running
   ```

3. **Node.js 18+ and npm**
   ```bash
   node --version
   npm --version
   ```

4. **Aliyun DashScope API Key**
   - Visit [Aliyun Bailian Console](https://bailian.console.aliyun.com/)
   - Create an API key
   - Set environment variable: `export AI_DASHSCOPE_API_KEY=your-key`

## Step-by-Step Setup

### 1. Navigate to Project Directory

```bash
cd spring-ai-alibaba-sandbox-example/sandbox-browser-fullstack
```

### 2. Start the Backend

```bash
# Set API key (required)
export AI_DASHSCOPE_API_KEY=your-api-key-here

# Run with Maven (from project root - no need to cd into a backend folder)
mvn spring-boot:run

# Or build and run
mvn clean package
java -jar target/sandbox-browser-fullstack-1.0.0-SNAPSHOT.jar
```

The backend will start on port 8080. You'll see:
```
Browser Use Fullstack Application Started!
Backend API: http://localhost:8080
Frontend UI: http://localhost:5173 (run 'npm run dev' in frontend/)
```

### 3. Start the Frontend

Open a new terminal:

```bash
cd spring-ai-alibaba-sandbox-example/sandbox-browser-fullstack/frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

The frontend will start on port 5173. Access it at http://localhost:5173

## Using the Application

### Basic Interaction

1. **Open the UI**
   - Navigate to http://localhost:5173
   - You'll see the chat interface

2. **Send a Message**
   - Type a request like: "Go to Google and search for Spring AI"
   - Press Enter or click Send

3. **Watch the Agent Work**
   - The AI will process your request
   - Responses stream in real-time
   - You can see what the agent is doing

4. **View Browser Actions**
   - Click "Show Browser" button (appears after first interaction)
   - A VNC panel will display showing the actual browser
   - Watch as the AI navigates, clicks, and types

### Example Commands

Try these commands in the chat:

```
"Navigate to https://www.baidu.com and tell me what you see"
"Go to GitHub and search for spring-ai-alibaba"
"Visit Stack Overflow and find questions about Java"
"Open Amazon and search for laptops"
"Go to YouTube and search for Java tutorials"
```

### Features

**Streaming Responses**
- Responses appear word-by-word for a natural feel
- No waiting for the entire response

**Session Management**
- Each browser tab gets its own session
- Conversations are maintained within sessions
- Sessions persist until page refresh

**VNC Visualization**
- Real-time view of browser actions
- Shows exactly what the AI is doing
- Helpful for debugging and understanding

## Troubleshooting

### Backend Issues

**Error: "Failed to initialize SandboxService"**
```
Solution: Ensure Docker is running
sudo systemctl start docker  # Linux
open -a Docker  # macOS
```

**Error: "API key not found"**
```
Solution: Set the environment variable
export AI_DASHSCOPE_API_KEY=your-key
```

**Port 8080 already in use**
```
Solution: Change port in application.yml
server:
  port: 8081
```

### Frontend Issues

**npm install fails**
```bash
# Clear cache and retry
rm -rf node_modules package-lock.json
npm cache clean --force
npm install
```

**Vite error: "Port 5173 is already in use"**
```bash
# Vite will automatically try next port
# Or specify manually
npm run dev -- --port 5174
```

**CORS errors in browser console**
```
The backend CORS configuration allows localhost:5173
If using different ports, update CorsConfig.java
```

### Docker Issues

**Sandbox containers not starting**
```bash
# Check Docker permissions
sudo usermod -aG docker $USER
# Log out and back in

# Verify Docker is accessible
docker ps
docker images
```

**Container image pull failures**
```bash
# Check internet connection
# May need proxy configuration if behind corporate firewall
```

## Development

### Backend Development

**Hot Reload**
```bash
# Use Spring Boot DevTools (already included)
mvn spring-boot:run
# Auto-restarts on code changes
```

**Adding New Endpoints**
```java
// Add to ChatController.java or create new controller
@GetMapping("/api/status")
public Map<String, String> getStatus() {
    return Map.of("status", "running");
}
```

### Frontend Development

**Component Structure**
```
src/
├── components/
│   ├── ChatInterface.tsx    # Main chat UI
│   ├── MessageList.tsx      # Message display
│   ├── MessageInput.tsx     # Input form
│   └── VNCPanel.tsx         # Browser view
├── services/
│   └── api.ts               # API calls
└── types/
    └── chat.ts              # TypeScript types
```

**Adding New Features**
```typescript
// Example: Add file upload to MessageInput.tsx
const handleFileUpload = async (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    await axios.post('/api/upload', formData);
};
```

## Production Deployment

### Backend

```bash
# Build for production
mvn clean package -DskipTests

# Run production jar
java -jar -Dspring.profiles.active=prod \
    target/sandbox-browser-fullstack-1.0.0-SNAPSHOT.jar
```

### Frontend

```bash
# Build for production
cd frontend
npm run build

# Serve static files (using any static server)
npx serve dist
```

### Docker Compose (Full Stack)

Create `docker-compose.yml`:

```yaml
version: '3.8'
services:
  backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      - AI_DASHSCOPE_API_KEY=${AI_DASHSCOPE_API_KEY}
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock

  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - backend
```

## Performance Considerations

- **Sandbox Pool Size**: Set to 1 for development, increase for production
- **Session Timeout**: Configure in SandboxConfiguration
- **Message History**: Limit conversation length to prevent memory issues
- **VNC Quality**: Adjust iframe size for performance

## Security Notes

- Never commit API keys to version control
- Use environment variables for sensitive data
- Sandbox containers provide isolation but review security settings
- CORS is configured for localhost only - update for production

## Support

For issues and questions:
- Spring AI Alibaba Documentation: https://java2ai.com
- GitHub Issues: https://github.com/alibaba/spring-ai-alibaba/issues

## License

Apache License 2.0
