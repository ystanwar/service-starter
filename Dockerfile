FROM openjdk:8-jdk-alpine AS build
WORKDIR /workspace/app
COPY . /workspace/app
ARG ORG_GRADLE_PROJECT_gdusername
ARG ORG_GRADLE_PROJECT_gdtoken
ENV ORG_GRADLE_PROJECT_gdusername=$ORG_GRADLE_PROJECT_gdusername
ENV ORG_GRADLE_PROJECT_gdtoken=$ORG_GRADLE_PROJECT_gdtoken
RUN target=/root/.gradle ./gradlew clean build
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.thoughtworks.PaymentApplication"]