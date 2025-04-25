FROM testnet0/client-tools:latest
LABEL authors="TestNet"

# 设置工作目录
WORKDIR /testnet-client

ADD ./target/testnet-client-1.0.0.jar ./

CMD ["sh", "-c", "sleep 30;java -Dfile.encoding=utf8 -jar testnet-client-1.0.0.jar"]
