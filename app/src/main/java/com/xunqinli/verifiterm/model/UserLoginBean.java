package com.xunqinli.verifiterm.model;

public class UserLoginBean extends BaseBean{


    /**
     * code : 0
     * msg : ok
     * data : {"phone":"18888888888","code":"20200518164546000103","deptCode":"20190581201000","name":"18888888888","state":"1"}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * phone : 18888888888
         * code : 20200518164546000103
         * deptCode : 20190581201000
         * name : 18888888888
         * state : 1
         */

        private String phone;
        private String code;
        private String deptCode;
        private String name;
        private String state;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDeptCode() {
            return deptCode;
        }

        public void setDeptCode(String deptCode) {
            this.deptCode = deptCode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }
}
