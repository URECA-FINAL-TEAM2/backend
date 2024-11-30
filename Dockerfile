FROM openjdk:17-jdk-slim

# JAR 파일 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# JAVA_OPTS 설정 (Pinpoint는 실행 시 환경 변수로 설정)
ENV JAVA_OPTS=""

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
