FROM openjdk:17-jdk-slim

# Pinpoint Agent 경로 정의
ARG PINPOINT_AGENT_PATH=/home/ubuntu/pinpoint-agent-2.4.1
ENV PINPOINT_AGENT=/pinpoint-agent

# Pinpoint Agent 복사
COPY ${PINPOINT_AGENT_PATH} ${PINPOINT_AGENT}

# Application JAR 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# Entry Point 설정
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]
