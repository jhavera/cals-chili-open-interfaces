# The food truck build sheet.
# Stage 1 compiles with a full JDK. Stage 2 is the truck itself: just a JRE,
# the compiled kitchen, and the menu board. No Maven, no downloads.

FROM eclipse-temurin:21-jdk AS build
WORKDIR /src
COPY src/main/java ./java
RUN mkdir /out && javac -d /out $(find java -name "*.java")

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /out ./classes
COPY openapi.yaml ./openapi.yaml

ENV KITCHEN=cal \
    TRUCK_NAME="Cal's Chili Truck" \
    PORT=8080
EXPOSE 8080

CMD ["java", "-cp", "classes", "truck.ChiliTruck"]
