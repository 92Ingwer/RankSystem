package org.kim.rankSystem.enums;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum PermissionsEnum {
    CREATE_GROUP("Create Group", "rankSystem.createGroup", "Create a new group", Material.BOOK),
    DELETE_GROUP("Delete Group", "rankSystem.deleteGroup", "Delete a group", Material.BOOK),
    EDIT_GROUP("Edit Group", "rankSystem.editGroup", "Edit a group", Material.BOOK),
    LIST_GROUPS("List Groups", "rankSystem.listGroups", "List all groups", Material.BOOK),
    INFO_GROUPS("Info Groups", "rankSystem.infoGroups", "Get info on a group", Material.BOOK);


    private final String name;
    private final String permission;
    private final String description;
    private final Material displayedMaterial;

    PermissionsEnum(String name, String permission, String description, Material displayedMaterial) {
        this.name = name;
        this.permission = permission;
        this.description = description;
        this.displayedMaterial = displayedMaterial;
    }
}
