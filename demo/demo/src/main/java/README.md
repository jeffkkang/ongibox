# Frontend API Reference

This document summarizes the REST API endpoints provided by the Spring Boot service under `demo/demo`.

## Letter Endpoints

- `GET /letters` – Retrieve a list of all stored letters.
- `GET /letters/{id}` – Retrieve a single letter by its numeric ID. Returns `404` if the letter does not exist.
- `POST /letters` – Create a new letter. Requires JSON body with at least `s3Key`.
- `POST /letters/{id}/trigger-ocr` – Simulate OCR processing for a letter. The sample text is populated in the `ocrText` field.
- `POST /letters/{id}/trigger-pii-removal` – Remove personal information using a prompt-based LLM. Requires `ocrText` to be present.
- `POST /letters/{id}/trigger-analysis` – Run danger analysis using a prompt-based LLM. Requires either `llmRefinedText` or `ocrText`.
- `PUT /letters/{id}/manual-review?humanReviewedDanger={true|false}` – Mark a letter as manually reviewed.

## Storage Endpoints

- `POST /storage/upload` – Upload an image to S3 and create an initial letter record. Responds with the S3 `key`, a presigned URL, and the created `letterId`.
- `GET /storage/url?key={s3Key}&minutes={ttl}` – Obtain a presigned URL for an existing S3 object.

## Prompt Management

- `POST /prompts/system/upload` – Upload the system prompt from a file.
- `POST /prompts/system/set` – Set the system prompt from a string.
- `GET /prompts/system` – Retrieve the current system prompt.
- `POST /prompts/user/upload` – Upload a user prompt from a file.
- `POST /prompts/user/set` – Set the user prompt from a string.
- `GET /prompts/user` – Get the current user prompt.
- `POST /prompts/pii/set` – Set the PII removal prompt.
- `GET /prompts/pii` – Retrieve the PII removal prompt.
- `POST /prompts/pii/upload` – Upload the PII removal prompt from a file.
- `POST /prompts/analysis/set` – Set the analysis prompt.
- `GET /prompts/analysis` – Retrieve the analysis prompt.
- `POST /prompts/analysis/upload` – Upload the analysis prompt from a file.

## Analyze Endpoints

These endpoints demonstrate interaction with external LLM services.

- `POST /analyze/generate` – Send an image URL and system prompt to an external service for analysis.
- `POST /analyze/generate-ollama` – Generate text using the local Ollama service.

## Error Handling

Most endpoints return standard HTTP status codes. In particular, retrieving a letter that does not exist returns `404 Not Found`.

## Next Steps

This document covers the available endpoints. A future guide will walk through common usage flows.