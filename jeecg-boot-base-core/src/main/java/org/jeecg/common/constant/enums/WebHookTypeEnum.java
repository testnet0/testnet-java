package org.jeecg.common.constant.enums;

import org.jeecg.common.system.annotation.EnumDict;
import org.jeecg.common.system.vo.DictModel;

import java.util.ArrayList;
import java.util.List;

@EnumDict("webHookType")
public enum WebHookTypeEnum {

    DINGTALK("ding_talk",  "钉钉"),
    FEISHU("feishu", "飞书"),
    WEWORK("wework", "企业微信"),
    OTHER("other", "其他");

    WebHookTypeEnum(String type, String note){
        this.type = type;
        this.note = note;
    }

    /**
     * WebHook类型
     */
    String type;

    /**
     * 类型说明
     */
    String note;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    /**
     * 获取字典数据
     * @return
     */
    public static List<DictModel> getDictList(){
        List<DictModel> list = new ArrayList<>();
        DictModel dictModel = null;
        for(WebHookTypeEnum e: WebHookTypeEnum.values()){
            dictModel = new DictModel();
            dictModel.setValue(e.getType());
            dictModel.setText(e.getNote());
            list.add(dictModel);
        }
        return list;
    }

    public static WebHookTypeEnum fromType(String type) {
        for (WebHookTypeEnum webhookType : values()) {
            if (webhookType.getType().equals(type)) {
                return webhookType;
            }
        }
        return OTHER; // 默认返回 OTHER
    }
}
