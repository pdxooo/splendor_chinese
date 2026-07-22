# 璀璨宝石中文联机版

这是基于 `hexanome-04/splendor` 改造的简体中文多人联机版本，面向家庭、朋友和小型社群部署。

## 游戏模式

- 璀璨宝石：经典版
- 璀璨宝石：东方扩展
- 璀璨宝石：城市扩展
- 璀璨宝石：贸易站

不同模式的牌库和规则相互隔离。经典版按基础规则结算；城市版使用城市替代贵族胜利条件；东方扩展包含宝物袋、虚拟黄金、级联和永久奖励费用；贸易站使用五项贸易能力。

## 中文版功能

- 简体中文大厅、游戏界面、操作提示和聊天
- 五位房间号、房间搜索、回合限时和随机起始顺序
- 创建者可删除自己创建的房间
- 玩家邀请码自助注册，密码只要求非空
- 明置预留牌公开，暗置预留牌只对本人显示
- 服务器强制回合计时，超时取消未完成操作
- 经典胜利结算：完成当前轮后比较分数，同分时发展卡较少者获胜
- 游戏结束一分钟后自动清理房间

## Docker 部署

要求：Git、Docker Engine、Docker Compose 插件。

```bash
git clone --branch chinese-localization --recurse-submodules \
  https://github.com/pdxooo/splendor_chinese.git
cd splendor_chinese/setup/has-server-client
docker compose up -d --build
```

默认访问地址：

```text
http://服务器IP:36104
```

当前架构需要开放 TCP `36104`（网页）、`34172`（大厅）和 `33402`（游戏服务）。不要向公网开放 MySQL 端口。

## 环境变量

在 `setup/has-server-client/.env` 中配置：

```dotenv
SPLENDOR_INVITE_CODE=请替换为足够长的随机邀请码
SPLENDOR_INTERNAL_DELETE_TOKEN=请替换为至少32位的随机内部令牌
```

不要把真实邀请码、内部令牌、SSH 私钥或服务器密码提交到 GitHub。

## 更新

```bash
cd /root/splendor_chinese
git pull origin chinese-localization
git submodule update --init --recursive
cd setup/has-server-client
docker compose up -d --build server client
```

不要执行 `docker compose down -v`，否则数据库卷及注册用户可能被删除。仅修改前端时可只重建 `client`；修改游戏规则时重建 `server`。

## 测试

后端：

```bash
mvn -f server/pom.xml clean test
```

前端：

```bash
cd client
npm install
npm run build
```

## 说明

本项目用于学习与非商业联机体验。原项目版权归原作者所有；游戏名称、规则和美术素材的相关权利归各自权利人所有。
