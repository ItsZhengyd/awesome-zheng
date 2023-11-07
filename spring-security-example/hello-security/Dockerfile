## 使用官方的Java运行时环境作为基础镜像
FROM openjdk:21-ea-21-jdk-slim as builder
## 设置工作目录
WORKDIR application
ARG JAR_FILE=target/*.jar
## 将项目中的jar文件复制到工作目录
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM openjdk:21-ea-21-jdk-slim
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

## 打包命令 docker build -t my-boot:1.1 .