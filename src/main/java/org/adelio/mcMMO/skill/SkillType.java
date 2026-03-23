package org.adelio.mcMMO.skill;

public enum SkillType {
    MINING("채광"),
    WOODCUTTING("벌목"),
    EXCAVATION("삽질"),
    COMBAT("전투");

    private final String name;
    SkillType(String name) { this.name = name; }
    public String getName() { return name; }
}