package com.example.accounting;

import com.example.accounting.model.invoice.Invoice;
import com.example.accounting.model.invoice.InvoiceRepository;
import com.example.accounting.service.InvoiceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.jakarta.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.jakarta.JsonSchemaGenerator;
import com.example.accounting.service.AiProxy;
import com.example.accounting.model.supplier.SupplierRepository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DocumentProcessor {
    private final Logger log = LoggerFactory.getLogger(DocumentProcessor.class);

    private static final String PROMPT = """
            You will receive an invoice text extracted from a PDF document. 
            Your task is to convert this text into a valid JSON object that 
            strictly follows the provided JSON schema. 
            Please ensure the following requirements are met: 
            1. The JSON must be valid and match the schema exactly.
            2. Dates must use the format YYYY-MM-DD. 
            If these requirements are not met, the system will reject the JSON.
            Return just a JSON String, no extra information.
            """;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private AiProxy aiProxy;

    private String basePrompt;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private void loadBasePrompt() throws JsonProcessingException {
        JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(objectMapper);
        JsonSchema jsonSchema = schemaGen.generateSchema(Invoice.class);
        basePrompt = PROMPT + "Schema " +
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonSchema) + "\nContent: ";
    }

    @RabbitListener(queues = "DocumentInputQueue")
    public void processDocument(String documentText) {
        log.info("Processing Document:");

        Map<String, Object> jsonMap = parseDocument(documentText);
        if (jsonMap == null) return;

        String content = String.valueOf(jsonMap.get("text"));
        String version = String.valueOf(jsonMap.get("version"));
        String fileName = String.valueOf(jsonMap.get("fileName"));
        Map<String, Object> metadata = (Map<String, Object>) jsonMap.get("metadata");

        try {
            if (basePrompt == null) loadBasePrompt();
            String prompt = basePrompt + content;

            String aiResponse = aiProxy.executeRequest(prompt);

            Invoice invoiceObject = objectMapper.readValue(aiResponse, Invoice.class);

            String processedJson = objectMapper.writeValueAsString(Map.of(
                    "version", version,
                    "text", aiResponse,
                    "fileName", fileName,
                    "metadata", metadata
            ));

            log.info("Storing Invoice {}", invoiceObject);
            invoiceService.createInvoice(invoiceObject);
            rabbitTemplate.convertAndSend("ProcessedDocumentQueue", processedJson);
            log.info("Sent to queue {}", "ProcessedDocumentQueue");

        } catch (Exception e) {
            log.error("Error in processing: {}", e.getMessage(), e);
        }
    }

    private Map<String, Object> parseDocument(String documentText) {
        try {
            return objectMapper.readValue(documentText, Map.class);
        } catch (Exception e) {
            log.warn("Document corrupt: {}", documentText);
            return new HashMap<>();
        }
    }
}