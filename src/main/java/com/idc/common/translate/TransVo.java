package com.idc.common.translate; /**
 * Copyright 2022 bejson.com
 */
import lombok.Data;

import java.util.List;

/**
 * Auto-generated: 2022-03-10 14:28:49
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class TransVo {

    private String from;
    private String to;
    private List<Trans_result> trans_result;

    @Data
    public class Trans_result {
        private String src;
        private String dst;

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public String getDst() {
            return dst;
        }

        public void setDst(String dst) {
            this.dst = dst;
        }
    }
}