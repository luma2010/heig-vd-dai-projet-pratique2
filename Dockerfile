FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/heih-vd-dai-projet-pratique2-1.0-SNAPSHOT.jar heih-vd-dai-projet-pratique2-1.0-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","/app/heih-vd-dai-projet-pratique2-1.0-SNAPSHOT.jar"]

