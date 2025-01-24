package org.kim.rankSystem.objects;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RankObject {
    private List<String> permissionsList;
    private String rank;
    private String displayName;

    public RankObject(String rank,String displayName, List<String> permissionsList) {
        this.rank = rank;
        this.permissionsList = permissionsList;
        this.displayName = displayName;
    }
    public List<String> getPermissionsList() {
        return new ArrayList<>(this.permissionsList);
    }
}
