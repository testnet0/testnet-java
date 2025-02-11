FROM ubuntu:22.04
LABEL authors="TestNet"
ENV TZ Asia/Shanghai
WORKDIR /testnet

ENV GOPROXY https://goproxy.cn,direct
ENV GOROOT="/usr/local/go"
ENV GOPATH=$HOME/go
ENV PATH="${PATH}:${GOROOT}/bin:${GOPATH}/bin"

ADD ./target/testnet-client-0.0.1-SNAPSHOT.jar ./
# 将启动脚本复制到容器中
COPY ../../../../Downloads/CodeDownload/testnet_build/client/start.sh /usr/local/bin/start.sh
# 赋予执行权限
RUN chmod +x /usr/local/bin/start.sh
CMD /usr/local/bin/start.sh
