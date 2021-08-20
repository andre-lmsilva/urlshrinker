FROM openjdk:11-jdk

RUN groupadd tomcat
RUN useradd tomcat -g tomcat
USER tomcat:tomcat

# --- Settings ---
ENV DATABASE_URL=jdbc:hsqldb:mem:pro_urlshrinker;DB_CLOSE_DELAY=-1
ENV DATABASE_USER=sa
ENV DATABASE_PASSWORD=

ENV LOGGING_FILE_PATH=/var/log/url_shrinker
ENV LOGGING_FILE_NAME=url_srinker.log

COPY target/urlshrinker-0.1.0-SNAPSHOT.jar urlshrinker-0.1.0-SNAPSHOT.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "urlshrinker-0.1.0-SNAPSHOT.jar"]
