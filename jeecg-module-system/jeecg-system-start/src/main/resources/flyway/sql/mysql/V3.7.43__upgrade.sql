UPDATE lite_flow_script
SET script_data = REPLACE(script_data, 'jsonObject.put("title", title);', 'jsonObject.put("webTitle", title);') where id = '1806588765662212097';