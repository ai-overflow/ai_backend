# Initial build container for spring application
FROM openjdk:11-jdk-oracle as build
WORKDIR /workspace/app

# copy project files to container
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# build jar
RUN --mount=type=cache,target=/root/.m2 ./mvnw install -DskipTests
RUN mv target/ki-0.0.1-SNAPSHOT.war target/ROOT.war

# Runtime container for application
FROM tomcat:jdk11-adoptopenjdk-hotspot

# create the webapps folder and give read/write/execute permissions
RUN mkdir -p /usr/local/tomcat/webapps/
RUN chmod -R a+rwx /usr/local/tomcat/webapps/

# drop privileges
RUN useradd --create-home appuser
USER appuser

# move war from build container to tomcat root
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target
COPY --from=build ${DEPENDENCY}/ROOT.war /usr/local/tomcat/webapps/
EXPOSE 8080

CMD ["catalina.sh", "run"]