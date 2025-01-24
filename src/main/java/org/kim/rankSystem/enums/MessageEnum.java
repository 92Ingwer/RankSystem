package org.kim.rankSystem.enums;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.kim.rankSystem.RankSystem;

public enum MessageEnum {
    PREFIX("messages.PREFIX"),
    CREATE_GROUP_USAGE("messages.CREATE_GROUP_USAGE"),
    LIST_GROUP_USAGE("messages.LIST_GROUP_USAGE"),
    DELETE_GROUP_USAGE("messages.DELETE_GROUP_USAGE"),
    GROUP_INFO_USAGE("messages.GROUP_INFO_USAGE"),
    GROUP_GIVE_USAGE("messages.GROUP_GIVE_USAGE"),
    GROUP_EDIT_USAGE("messages.GROUP_EDIT_USAGE"),
    RANK_EXISTS("messages.RANK_EXISTS"),
    RANK_DOES_NOT_EXIST("messages.RANK_DOES_NOT_EXIST"),
    SUCESSFULLY_CREATED_GROUP("messages.SUCESSFULLY_CREATED_GROUP"),
    LIST_RANK("messages.LIST_RANK"),
    PLAYER_NOT_FOUND("messages.PLAYER_NOT_FOUND"),
    ADDITIONAL_PERMISSIONS("messages.ADDITIONAL_PERMISSIONS"),
    UNTIL_PERMANENT("messages.UNTIL_PERMANENT"),
    UNTIL_TEMPORARY("vUNTIL_TEMPORARY"),
    RANK_SET("messages.RANK_SET"),
    GROUP_EDIT_MANUELL("messages.GROUP_EDIT_MANUELL"),
    PLAYER_RANK_NOT_FOUND("messages.PLAYER_RANK_NOT_FOUND"),
    PLAYER_PERMISSION_ADDED("messages.PLAYER_PERMISSION_ADDED"),
    PLAYER_PERMISSION_REMOVED("messages.PLAYER_PERMISSION_REMOVED"),
    RANK_PERMISSION_ADDED("messages.RANK_PERMISSION_ADDED"),
    RANK_PERMISSION_REMOVED("messages.RANK_PERMISSION_REMOVED"),
    SET_SIGN("messages.SET_SIGN"),
    PLAYER_NEVER_ONLINE("messages.PLAYER_NEVER_ONLINE"),
    PLAYER_EDITED_SIGN("messages.PLAYER_EDITED_SIGN"),
    STANDARD_RANK_CANNOT_BE_DELETED("messages.STANDARD_RANK_CANNOT_BE_DELETED"),
    RANK_DELETED("messages.RANK_DELETED"),
    NOT_PERMISSION("messages.NOT_PERMISSION");


    @Getter
    private final String path;
    @Getter
    @Setter
    private String message;
    MessageEnum(String path) {
        this.path = path;
        FileConfiguration config = RankSystem.getInstance().getConfig();
        this.message = mm(config.getString(path, ""));
    }

    public static String mm(String msg) {
        return MiniMessage.miniMessage().serialize(MiniMessage.miniMessage().deserialize(msg));
    }
    public static void loadPrefixes(FileConfiguration config) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        for (MessageEnum messageEnum : values()) {
            String rawValue = config.getString(messageEnum.getPath(), "Default Prefix");
            messageEnum.setMessage(miniMessage.serialize(miniMessage.deserialize(rawValue)));
        }
    }
}
