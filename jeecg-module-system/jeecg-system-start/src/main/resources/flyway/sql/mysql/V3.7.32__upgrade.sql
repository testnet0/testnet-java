
UPDATE  lite_flow_chain set config = 'command: ''xapp -r "/testnet-client/default_tools/xray-plugins/finger/**/*.yml" -t %s -o %s''' where id = '1806615115240423425';
UPDATE client_config set config = 'command: ''xapp -r "/testnet-client/default_tools/xray-plugins/finger/**/*.yml" -t %s -o %s''' where chain_id = '1806615115240423425';

UPDATE  lite_flow_chain set config = 'command: ''xpoc -r "/testnet-client/default_tools/xray-plugins/poc/**/*.yml" -t %s -o %s''' where id = '1810995080047456258';
UPDATE client_config set config = 'command: ''xpoc -r "/testnet-client/default_tools/xray-plugins/poc/**/*.yml" -t %s -o %s''' where chain_id = '1810995080047456258';
