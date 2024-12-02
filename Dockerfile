FROM openjdk:17-jdk-slim

# wget과 tar 설치
RUN apt-get update && apt-get install -y wget tar

# Pinpoint Agent 다운로드 및 설치
RUN wget https://github.com/pinpoint-apm/pinpoint/releases/download/v2.4.1/pinpoint-agent-2.4.1.tar.gz \
    && tar -xzvf pinpoint-agent-2.4.1.tar.gz \
    && mv pinpoint-agent-2.4.1 /pinpoint-agent \
    && rm pinpoint-agent-2.4.1.tar.gz

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# Pinpoint Agent를 통한 Java 실행
ENTRYPOINT ["java", \
    "-javaagent:/pinpoint-agent/pinpoint-bootstrap.jar", \
    "-Dpinpoint.agentId=${HOSTNAME}", \
    "-Dpinpoint.applicationName=beautymeongdang-app", \
    "-Dprofiler.transport.grpc.collector.ip=pinpoint-collector", \
    "-Dprofiler.transport.grpc.collector.port=9991", \
    "-Dprofiler.transport.grpc.metadata.port=9992", \
    "-Dprofiler.transport.grpc.stat.port=9993", \
    "-jar", "/app.jar"]