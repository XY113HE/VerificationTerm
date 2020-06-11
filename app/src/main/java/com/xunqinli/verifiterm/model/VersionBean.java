package com.xunqinli.verifiterm.model;

public class VersionBean {

    /**
     * code : 0
     * msg : ok
     * data : {"version":"1.1","createTime":"2020-06-04 15:45:42","apkName":"洗车核销终端.apk","link":"http://192.169.0.54:8888/group1/M00/00/00/wKkANl7YpqGAV9NFADPj2E_SRB4636.apk"}
     */

    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * version : 1.1
         * createTime : 2020-06-04 15:45:42
         * apkName : 洗车核销终端.apk
         * link : http://192.169.0.54:8888/group1/M00/00/00/wKkANl7YpqGAV9NFADPj2E_SRB4636.apk
         */

        private String version;
        private String createTime;
        private String apkName;
        private String link;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getApkName() {
            return apkName;
        }

        public void setApkName(String apkName) {
            this.apkName = apkName;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }
}
