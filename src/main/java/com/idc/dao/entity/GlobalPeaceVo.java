package com.idc.dao.entity;

import lombok.Data;

@Data
public class GlobalPeaceVo {
    private String country;
    private String rank;
    private String score;
    private String date;

    public GlobalPeaceVo(String country, String rank, String score, String date) {
        this.country = country;
        this.rank = rank;
        this.score = score;
        this.date = date;
    }
}
