package de.hskl.ki.models.container;

import de.hskl.ki.models.container.helper.ContainerMemoryInfo;

public class ContainerStatsResponse {
    private String id;
    private String name;
    private ContainerMemoryInfo memory;

    public ContainerStatsResponse() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ContainerMemoryInfo getMemory() {
        return memory;
    }

    public void setMemory(ContainerMemoryInfo memory) {
        this.memory = memory;
    }

    @Override
    public String toString() {
        return "ContainerStatsResponse{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", memoryInfo=" + memory +
                '}';
    }
}
