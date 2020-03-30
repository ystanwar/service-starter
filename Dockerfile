FROM openjdk:8-jdk-alpine AS build
WORKDIR /workspace/app
COPY . /workspace/app

#These are the arguments taken from docker build command
ARG FOR_GRADLE_PROJECT_gdusername
ARG FOR_GRADLE_PROJECT_gdtoken

#these environment variables names should start with ORG_GRADLE_PROJECT_****
#so that gradle understand these as a gradle project properties
ENV ORG_GRADLE_PROJECT_gdusername=$FOR_GRADLE_PROJECT_gdusername
ENV ORG_GRADLE_PROJECT_gdtoken=$FOR_GRADLE_PROJECT_gdtoken
RUN target=/root/.gradle ./gradlew clean build
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)


FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.thoughtworks.PaymentApplication"]