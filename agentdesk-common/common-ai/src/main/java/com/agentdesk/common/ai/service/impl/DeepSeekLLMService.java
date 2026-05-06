package com.agentdesk.common.ai.service.impl;

import com.agentdesk.common.ai.config.LLMConfig;
import com.agentdesk.common.ai.model.ChatMessage;
import com.agentdesk.common.ai.model.ChatRequest;
import com.agentdesk.common.ai.model.ChatResponse;
import com.agentdesk.common.ai.model.ChatResponse.Choice;
import com.agentdesk.common.ai.model.ChatResponse.Usage;
import com.agentdesk.common.ai.service.LLMService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeepSeekLLMService implements LLMService {

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private final LLMConfig config;
    private final ObjectMapper objectMapper;
    private OkHttpClient client;

    @PostConstruct
    public void init() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(config.getTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(config.getTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(config.getTimeout(), TimeUnit.MILLISECONDS)
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .header("Authorization", "Bearer " + config.getApiKey())
                            .build();
                    return chain.proceed(request);
                })
                .build();
    }

    @Override
    public ChatResponse chat(ChatRequest request) {
        try {
            applyDefaults(request);
            String json = objectMapper.writeValueAsString(request);
            RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);
            Request httpRequest = new Request.Builder()
                    .url(config.getBaseUrl() + "/v1/chat/completions")
                    .post(body)
                    .build();

            try (Response response = client.newCall(httpRequest).execute()) {
                String responseBody = response.body().string();
                if (!response.isSuccessful()) {
                    throw new RuntimeException("DeepSeek API error [" + response.code() + "]: " + responseBody);
                }
                return parseResponse(responseBody);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to call DeepSeek API", e);
        }
    }

    @Override
    public ChatResponse chat(List<ChatMessage> messages) {
        ChatRequest request = new ChatRequest();
        request.setMessages(messages);
        return chat(request);
    }

    @Override
    public void chatStream(ChatRequest request, Consumer<String> deltaConsumer) {
        try {
            applyDefaults(request);
            request.setStream(true);
            String json = objectMapper.writeValueAsString(request);
            RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);
            Request httpRequest = new Request.Builder()
                    .url(config.getBaseUrl() + "/v1/chat/completions")
                    .post(body)
                    .build();

            try (Response response = client.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    String responseBody = response.body().string();
                    throw new RuntimeException("DeepSeek API stream error [" + response.code() + "]: " + responseBody);
                }

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isEmpty()) {
                        continue;
                    }
                    if (line.startsWith("data: ")) {
                        String data = line.substring(6);
                        if ("[DONE]".equals(data)) {
                            break;
                        }
                        ChatResponse chunk = objectMapper.readValue(data, ChatResponse.class);
                        if (chunk.getChoices() != null) {
                            for (Choice choice : chunk.getChoices()) {
                                if (choice.getMessage() != null && choice.getMessage().getContent() != null) {
                                    deltaConsumer.accept(choice.getMessage().getContent());
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to call DeepSeek API stream", e);
        }
    }

    private void applyDefaults(ChatRequest request) {
        if (request.getModel() == null) {
            request.setModel(config.getModel());
        }
        if (request.getTemperature() == null) {
            request.setTemperature(config.getTemperature());
        }
        if (request.getMaxTokens() == null) {
            request.setMaxTokens(config.getMaxTokens());
        }
    }

    private ChatResponse parseResponse(String responseBody) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);
        ChatResponse chatResponse = new ChatResponse();
        chatResponse.setId(root.path("id").asText());
        chatResponse.setObject(root.path("object").asText());
        chatResponse.setCreated(root.path("created").asLong());
        chatResponse.setModel(root.path("model").asText());

        List<Choice> choices = new ArrayList<>();
        JsonNode choicesNode = root.path("choices");
        for (JsonNode choiceNode : choicesNode) {
            Choice choice = new Choice();
            choice.setIndex(choiceNode.path("index").asInt());
            JsonNode messageNode = choiceNode.has("message") ? choiceNode.get("message") : choiceNode.get("delta");
            if (messageNode != null) {
                ChatMessage message = objectMapper.treeToValue(messageNode, ChatMessage.class);
                choice.setMessage(message);
            }
            if (choiceNode.has("finish_reason") && !choiceNode.get("finish_reason").isNull()) {
                choice.setFinishReason(choiceNode.get("finish_reason").asText());
            }
            choices.add(choice);
        }
        chatResponse.setChoices(choices);

        JsonNode usageNode = root.path("usage");
        if (!usageNode.isMissingNode() && !usageNode.isNull()) {
            Usage usage = new Usage();
            usage.setPromptTokens(usageNode.path("prompt_tokens").asInt());
            usage.setCompletionTokens(usageNode.path("completion_tokens").asInt());
            usage.setTotalTokens(usageNode.path("total_tokens").asInt());
            chatResponse.setUsage(usage);
        }

        return chatResponse;
    }
}
