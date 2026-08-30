# Spring AI Alibaba OrcaRouter Chat Example

This is a chat example for using OrcaRouter LLMs with Spring AI Alibaba.

OrcaRouter is an OpenAI-compatible AI gateway, so this module reuses the
`spring-ai-starter-model-openai` starter and points it at OrcaRouter's
OpenAI-compatible endpoint (`https://api.orcarouter.ai`; the starter appends
`/v1/chat/completions` itself). Model names use the `provider/model` format, for
example `anthropic/claude-sonnet-4.5`, `openai/gpt-4o` or `orcarouter/auto`
(which lets OrcaRouter pick a live model automatically).

## Configuration

| env var | required | description |
|---|---|---|
| `ORCAROUTER_API_KEY` | yes | OrcaRouter API key (created in the OrcaRouter console) |

## Run

```bash
mvn -pl spring-ai-alibaba-chat-example/orcarouter-chat spring-boot:run
```

## Test

```bash
curl "http://localhost:10016/orcarouter/chat-model/simple/chat"
curl "http://localhost:10016/orcarouter/chat-model/stream/chat"
curl "http://localhost:10016/orcarouter/chat-model/custom/chat?model=anthropic/claude-sonnet-4.5"
```

See also [orcarouter-chat.http](./orcarouter-chat.http) for ready-to-use
requests.
