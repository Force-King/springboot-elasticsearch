package com.bi.elasticsearch.api.enums;

/**
 * @author CleverApe
 * @Classname AppIdEnum
 * @Description AppId枚举
 * @Date 2019-03-06 19:14
 * @Version V1.0
 */
public enum AppIdEnum {
    RONG_SHU(5,1216088977,"榕树贷款App");

    private int innerId;
    private int outerId;
    private String description;

    AppIdEnum(int innerId, int outerId, String description) {
        this.innerId = innerId;
        this.outerId = outerId;
        this.description = description;
    }


    public static AppIdEnum getEnumByInnerId(int innerId){
        for(AppIdEnum appIdEnum : AppIdEnum.values()){
            if(appIdEnum.innerId==innerId){
                return appIdEnum;
            }
        }
        return null;
    }

    public int getInnerId() {
        return innerId;
    }

    public AppIdEnum setInnerId(int innerId) {
        this.innerId = innerId;
        return this;
    }

    public int getOuterId() {
        return outerId;
    }

    public AppIdEnum setOuterId(int outerId) {
        this.outerId = outerId;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public AppIdEnum setDescription(String description) {
        this.description = description;
        return this;
    }

}
