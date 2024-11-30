FROM openjdk:17-jdk-slim

# Add Pinpoint Agent
ARG PINPOINT_AGENT_PATH=/home/ubuntu/pinpoint-agent-2.4.1
ENV PINPOINT_AGENT=${PINPOINT_AGENT_PATH}
COPY ${PINPOINT_AGENT} /pinpoint-agent

# Copy application JAR
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# Add JAVA_OPTIONS for Pinpoint Agent
ENV JAVA_OPTS="-javaagent:/pinpoint-agent/pinpoint-bootstrap.jar -Dpinpoint.agentId=beautymeongdang-agent -Dpinpoint.applicationName=beautymeongdang-app"

ENTRYPOINT ["java", "-jar", "${JAVA_OPTS}", "/app.jar"]
