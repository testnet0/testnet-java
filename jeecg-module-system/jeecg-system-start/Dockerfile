FROM alpine/java:17-jre

MAINTAINER jeecgos@163.com

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

WORKDIR /testnet-server

EXPOSE 8080

#ADD ./src/main/resources/jeecg ./config/jeecg
#ADD ./target/jeecg-system-start-3.7.4.jar ./
#ENV JAVA_OPTS="-server -Xms1024m -Xmx4096m -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/dumps/oom_dump.hprof"

ADD ./target/jeecg-system-start-3.8.0.jar testnet-start-1.0.0.jar

#CMD sleep 60;java -Djava.security.egd=file:/dev/./urandom -jar jeecg-system-start-3.7.4.jar
CMD ["sh", "-c", "sleep 30; java -Djava.security.egd=file:/dev/./urandom -Dfile.encoding=utf8 -jar testnet-start-1.0.0.jar"]
