FROM anapsix/alpine-java:8_server-jre_unlimited

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

WORKDIR /testnet-server

EXPOSE 8080

ENV JAVA_OPTS="-server -Xms1024m -Xmx4096m -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/dumps/oom_dump.hprof"

ADD ./target/jeecg-system-start-3.7.3.jar testnet-start-1.0.0.jar

CMD ["sh", "-c", "sleep 30; java -Djava.security.egd=file:/dev/./urandom -Dfile.encoding=utf8 -jar testnet-start-1.0.0.jar"]
