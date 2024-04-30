FROM openjdk:23-jdk-bookworm
LABEL author="Eric Kubenka<code@code-fever.de>"

# Google Chrome
RUN apt-get install -y wget
RUN wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list
RUN apt-get update && apt-get -y install google-chrome-stable

# ChromeDriver
RUN apt-get -y install chromium-driver

RUN mkdir -p /app
COPY ./build/libs/conviva-1-SNAPSHOT.jar /app/conviva-1-SNAPSHOT.jar
COPY ./build/libs/conviva.properties /app/conviva.properties
WORKDIR /app

CMD ["java", "--add-opens=java.base/java.lang=ALL-UNNAMED", "-jar", "conviva-1-SNAPSHOT.jar"]