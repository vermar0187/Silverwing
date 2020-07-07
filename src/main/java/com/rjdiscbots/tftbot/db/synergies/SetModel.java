package com.rjdiscbots.tftbot.db.synergies;

public class SetModel {

    private Integer min;

    private Integer max;

    private String style;

    public SetModel() {
    }

    public SetModel(int min, int max, String style) {
        this.min = min;
        this.max = max;
        this.style = style;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
