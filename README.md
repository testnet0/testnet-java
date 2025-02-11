TestNet 资产管理系统服务端及客户端
===============
当前最新版本： 2.0（发布时间：2025-02-11）

## 开发环境搭建

IDE建议： IDEA (必须安装lombok插件 )
语言：Java 8+ (支持17)
依赖管理：Maven
基础框架：Spring Boot 2.7.18

## 源码运行指南

- **搭建MySQL数据库**
    - 导入数据：从 [GitHub链接](https://github.com/testnet0/testnet/blob/main/db/testnet.sql) 下载SQL文件并导入到MySQL数据库中。

- **搭建Redis**
    - 安装并启动Redis服务。

- **修改配置文件**
    - 编辑 `jeecg-module-system/jeecg-system-start/src/main/resources/application-dev.yml` 文件，更新数据库和Redis的连接密码。
- **编译项目**
    - 在项目testnet-common目录下执行 `mvn package` 命令进行项目编译。
- **启动项目**
    - 执行项目启动命令或通过IDE启动项目。
