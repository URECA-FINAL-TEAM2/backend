FROM openjdk:17-jdk-slim

# Pinpoint Agent 복사
COPY --from=pinpoint/pinpoint-agent:latest /pinpoint-agent /pinpoint-agent

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# Pinpoint Agent를 통한 Java 실행
ENTRYPOINT ["java", \
    "-javaagent:/pinpoint-agent/pinpoint-bootstrap.jar", \
    "-Dpinpoint.agentId=${HOSTNAME}", \
    "-Dpinpoint.applicationName=beatuymeongdang-app", \
    "-jar", "/app.jar"]