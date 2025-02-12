FROM testnet0/client-tools:latest
LABEL authors="TestNet"

#ENV TZ=Asia/Shanghai
#ENV JAVA_OPTS="-server -Xms1024m -Xmx4096m -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/dumps/oom_dump.hprof"
#ENV PATH="/testnet-client/default_tools:/testnet-client/tools:${PATH}"
#
#COPY ./tools /testnet-client/default_tools
#RUN chmod +x /testnet-client/default_tools/*
#
#RUN sed -i "s@http://.*deb.debian.org@http://mirrors.tuna.tsinghua.edu.cn@g" /etc/apt/sources.list
## 安装基本工具和依赖
## 安装相关依赖
#RUN apt update && apt install -y --no-install-recommends libpcap-dev nmap python3-pip ttf-wqy-microhei ttf-wqy-zenhei
#
#RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb && apt install -y ./google-chrome-stable_current_amd64.deb && rm google-chrome-stable_current_amd64.deb
#
#RUN apt clean && rm -rf /var/lib/apt/lists/*
#
#RUN cd /testnet-client/default_tools/dirsearch && pip3 install -r requirements.txt -
#
#RUN cd /testnet-client/default_tools/OneForAll && pip3 install -r requirements.txt

# 设置工作目录
WORKDIR /testnet-client

ADD ./target/testnet-client-1.0.0.jar ./

CMD ["sh", "-c", "sleep 30;java -Dfile.encoding=utf8 -jar testnet-client-1.0.0.jar"]
