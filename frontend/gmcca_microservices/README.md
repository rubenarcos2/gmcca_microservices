# GMCCA_microservices

He realizado una aplicación basada en microservicios que permite la consulta de documentación de un producto (por ejemplo el manual de instrucciones en PDF) para poder obtener información mediante un chatbot. Utiliza la siguiente tecnología:

- Modelo de datos: mxbai-embed-large, extracción de texto y ALIA para el chat
- Ollama como gestor de los modelos de IA
- Chatbot opensource
- Frontend: React
- Backend: SpringBoot + Spring AI
- Módulo de notificaciones (Kafka)
- Módulo de IA (RAG para le extracción de PDF y post-consulta de información)
- Módulo de inventario
- Módulo de pedidos
- Módulo de productos
