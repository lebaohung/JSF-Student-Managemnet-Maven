# Dockerfile for JSF Student Management Application
# Multi-stage build: First build the WAR, then deploy to WildFly

# Stage 1: Build WAR file
FROM maven:3.8-openjdk-8 AS builder

WORKDIR /build

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

# Build WAR file
RUN mvn clean package

# Stage 2: deploy to WildFly
FROM jboss/wildfly:20.0.1.Final

USER root

RUN mkdir -p /opt/jboss/wildfly/modules/org/postgresql/main
RUN curl -o /opt/jboss/wildfly/modules/org/postgresql/main/postgresql-42.2.5.jar \
    https://repo1.maven.org/maven2/org/postgresql/postgresql/42.2.5/postgresql-42.2.5.jar

# Create module.xml
RUN echo '<?xml version="1.0" encoding="UTF-8"?>' > /opt/jboss/wildfly/modules/org/postgresql/main/module.xml && \
    echo '<module xmlns="urn:jboss:module:1.9" name="org.postgresql">' >> /opt/jboss/wildfly/modules/org/postgresql/main/module.xml && \
    echo '    <resources>' >> /opt/jboss/wildfly/modules/org/postgresql/main/module.xml && \
    echo '        <resource-root path="postgresql-42.2.5.jar"/>' >> /opt/jboss/wildfly/modules/org/postgresql/main/module.xml && \
    echo '    </resources>' >> /opt/jboss/wildfly/modules/org/postgresql/main/module.xml && \
    echo '    <dependencies>' >> /opt/jboss/wildfly/modules/org/postgresql/main/module.xml && \
    echo '        <module name="javax.api"/>' >> /opt/jboss/wildfly/modules/org/postgresql/main/module.xml && \
    echo '        <module name="javax.transaction.api"/>' >> /opt/jboss/wildfly/modules/org/postgresql/main/module.xml && \
    echo '    </dependencies>' >> /opt/jboss/wildfly/modules/org/postgresql/main/module.xml && \
    echo '</module>' >> /opt/jboss/wildfly/modules/org/postgresql/main/module.xml


RUN chown -R jboss:jboss /opt/jboss/wildfly/modules/org/postgresql

# Copy WAR
COPY --from=builder /build/target/studentManagement.war /opt/jboss/wildfly/standalone/deployments/
RUN chown jboss:jboss /opt/jboss/wildfly/standalone/deployments/studentManagement.war

USER jboss
EXPOSE 8080

ENV DB_HOST=host.docker.internal
ENV DB_PORT=5432
ENV DB_NAME=jsfstudent
ENV DB_USER=postgres
ENV DB_PASSWORD=postgres

# Use entrypoint script
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]

