-- 修复xapp模版问题

UPDATE `jeecg-boot`.`client_tools` SET `install_command` = 'curl -sSL https://github.com/chaitin/xapp/releases/download/xapp-0.0.1/xapp_linux_amd64 -o /testnet-client/tools/xapp\nchmod +x /testnet-client/tools/xapp\ncd /testnet-client/tools/\ngit clone --depth 1 --branch main  https://github.com/chaitin/xray-plugins.git' WHERE `script_id` = '1806614988580831233';
