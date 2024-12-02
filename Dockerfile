FROM openjdk:17-jdk-slim AS builder

# JAR 파일 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# Pinpoint Agent 다운로드
RUN apt-get update && apt-get install -y wget unzip \
    && wget https://github.com/pinpoint-apm/pinpoint/releases/download/v2.4.1/pinpoint-agent-2.4.1.tar.gz \
    && tar -xzvf pinpoint-agent-2.4.1.tar.gz \
    && rm pinpoint-agent-2.4.1.tar.gz

# 최종 이미지
FROM openjdk:17-jdk-slim

# 빌더 스테이지에서 복사
COPY --from=builder /app.jar app.jar
COPY --from=builder /pinpoint-agent /pinpoint-agent

# Pinpoint Agent를 통한 Java 실행
ENTRYPOINT ["java", \
    "-javaagent:/pinpoint-agent/pinpoint-bootstrap.jar", \
    "-Dpinpoint.agentId=${HOSTNAME}", \
    "-Dpinpoint.applicationName=beatuymeongdang-app", \
    "-jar", "/app.jar"]