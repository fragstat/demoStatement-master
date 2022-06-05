package com.example.demo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Operation {
    private LocalDateTime date;
    private String title;
    private String desc;
    private BigDecimal sum;
    private PayPointDto src;
    private PayPointDto dst;

    public Operation(LocalDateTime date, String title, String desc, BigDecimal sum) {
        this.date = date;
        this.title = title;
        this.desc = desc;
        this.sum = sum;
    }

    public Operation(LocalDateTime date, String title, String desc, BigDecimal sum, PayPointDto src, PayPointDto dst) {
        this.date = date;
        this.title = title;
        this.desc = desc;
        this.sum = sum;
        this.src = src;
        this.dst = dst;
    }

    public Operation() {
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public PayPointDto getSrc() {
        return src;
    }

    public void setSrc(PayPointDto src) {
        this.src = src;
    }

    public PayPointDto getDst() {
        return dst;
    }

    public void setDst(PayPointDto dst) {
        this.dst = dst;
    }
}
