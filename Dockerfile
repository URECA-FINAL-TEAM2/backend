FROM openjdk:17-jdk-slim

# 필요한 도구 설치 및 캐시 삭제
RUN apt-get update && \
    apt-get install -y --no-install-recommends wget tar && \
    rm -rf /var/lib/apt/lists/*

# Pinpoint Agent 다운로드 및 설치
RUN mkdir -p /pinpoint-agent && \
    wget -O /pinpoint-agent/pinpoint-agent.tar.gz https://github.com/pinpoint-apm/pinpoint/releases/download/v2.4.1/pinpoint-agent-2.4.1.tar.gz && \
    tar -xzvf /pinpoint-agent/pinpoint-agent.tar.gz -C /pinpoint-agent --strip-components=1 && \
    rm /pinpoint-agent/pinpoint-agent.tar.gz

# JAR 파일 복사 전에 빌드 컨텍스트 크기 확인
COPY build/libs/*.jar app.jar

# 메모리 제한 및 최적화 옵션 추가
ENTRYPOINT ["java", \
    "-Xmx512m", \
    "-Xms256m", \
    "-javaagent:/pinpoint-agent/pinpoint-bootstrap.jar", \
    "-Dpinpoint.agentId=${HOSTNAME}", \
    "-Dpinpoint.applicationName=beautymeongdang-app", \
    "-Dprofiler.transport.grpc.collector.ip=pinpoint-collector", \
    "-Dprofiler.transport.grpc.collector.port=9991", \
    "-Dprofiler.transport.grpc.metadata.port=9992", \
    "-Dprofiler.transport.grpc.stat.port=9993", \
    "-jar", "/app.jar"]