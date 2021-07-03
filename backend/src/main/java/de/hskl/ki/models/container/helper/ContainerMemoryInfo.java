package de.hskl.ki.models.container.helper;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ContainerMemoryInfo {
    private long total;
    private long used;
    @JsonProperty("used_percentage")
    private double usedPercentage;

    public ContainerMemoryInfo() {
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getUsed() {
        return used;
    }

    public void setUsed(long used) {
        this.used = used;
    }

    public double getUsedPercentage() {
        return usedPercentage;
    }

    public void setUsedPercentage(double usedPercentage) {
        this.usedPercentage = usedPercentage;
    }

    @Override
    public String toString() {
        return "ContainerMemoryInfo{" +
                "total=" + total +
                ", used=" + used +
                ", usedPercentage=" + usedPercentage +
                '}';
    }
}
