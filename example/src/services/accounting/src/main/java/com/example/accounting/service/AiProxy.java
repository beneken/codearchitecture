package com.example.accounting.service;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;

@Service
public class AiProxy {
    private static final String BASE_URL = "<AI Base URL>";
    private static final String MODEL = "<AI model name>";

    private final CredentialsProxy credentialsProxy;
    private OpenAIClient openAIClient;

    public AiProxy(CredentialsProxy credentialsProxy) {
        this.credentialsProxy = credentialsProxy;
    }

    private OpenAIClient getClient()  {
                if (this.openAIClient == null) {
                    String apiKey = credentialsProxy.getApiKey("<TODO>","<TODO>");
                    this.openAIClient = OpenAIOkHttpClient.builder()
                            .apiKey(apiKey)
                            .baseUrl(BASE_URL)
                            .build();
                }

        return this.openAIClient;
    }

    public String executeRequest(String prompt)  {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(MODEL)
                .addUserMessage(prompt)
                .build();

        StringBuilder response = new StringBuilder();
        getClient().chat().completions().create(params).choices().stream()
                .flatMap(choice -> choice.message().content().stream())
                .forEach(response::append);

        return response.toString();
    }
}