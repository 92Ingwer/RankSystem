package org.kim.rankSystem.database;

import org.kim.rankSystem.RankSystem;
import org.kim.rankSystem.objects.RankObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SQLCreate {
    public static void create() {
        RankSystem.getSql().update("CREATE TABLE IF NOT EXISTS playerrank (uuid VARCHAR(3600), rank VARCHAR(3600), permission VARCHAR(3600), until VARCHAR(3600))");
        RankSystem.getSql().update("CREATE TABLE IF NOT EXISTS groups (rankname VARCHAR(3600), displayname VARCHAR(3600), permission VARCHAR(3600))");
        RankSystem.getSql().update("INSERT INTO groups (rankname, displayname, permission) SELECT 'default', 'DEFAULT', NULL WHERE NOT EXISTS (SELECT 1 FROM groups WHERE rankname = 'default')");
    }

    public static void insertUser(UUID uuid) {
        if (!userExists(uuid)) {
            try (PreparedStatement stmt = RankSystem.getSql().getCon().prepareStatement("INSERT INTO playerrank (uuid, rank, permission) VALUES (?, 'default', NULL)")) {
                stmt.setString(1, uuid.toString());
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean userExists(UUID uuid) {
        try {
            ResultSet rs = RankSystem.getSql().getResult("SELECT uuid FROM playerrank WHERE uuid = '" + uuid + "'");
            if (rs.next()) {
                return rs.getString("uuid") != null;
            }
        } catch (SQLException ignored) {
        }
        return false;
    }

    public static String getRankPlayer(UUID uuid) {
        try {
            ResultSet rs = RankSystem.getSql().getResult("SELECT rank FROM playerrank WHERE uuid = '" + uuid + "'");
            if (rs.next()) {
                return rs.getString("rank");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean rankExists(String rank) {
        try {
            ResultSet rs = RankSystem.getSql().getResult("SELECT rankname FROM groups WHERE rankname = '" + rank + "'");
            if (rs.next()) {
                return rs.getString("rankname") != null;
            }
        } catch (SQLException ignored) {
        }
        return false;
    }

    public static void createRank(RankObject rankObject) {
        try (PreparedStatement stmt = RankSystem.getSql().getCon().prepareStatement("INSERT INTO groups (rankname, displayname, permission) VALUES (?, ?, NULL)")) {
            stmt.setString(1, rankObject.getRank());
            stmt.setString(2, rankObject.getDisplayName().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getPermissionsPlayer(UUID uuid) {
        try {
            ResultSet rs = RankSystem.getSql().getResult("SELECT permission FROM playerrank WHERE uuid = '" + uuid + "'");
            if (rs.next()) {
                if (rs.getString("permission") == null) {
                    return new ArrayList<>();
                }
                return new ArrayList<>(Arrays.asList(rs.getString("permission").split(",")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void updatePlayerRanks(UUID uuid, String rank, List<String> permissions) {
        try (PreparedStatement stmt = RankSystem.getSql().getCon().prepareStatement("UPDATE playerrank SET rank = ?, permission = ? WHERE uuid = ?")) {
            stmt.setString(1, rank);
            stmt.setString(2, String.join(",", permissions));
            stmt.setString(3, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<RankObject> getAllRanks() {
        try {
            ResultSet rs = RankSystem.getSql().getResult("SELECT rankname FROM groups");
            List<RankObject> ranks = new ArrayList<>();
            while (rs.next()) {
                String rank = rs.getString("rankname");
                String displayName = getDisplayname(rank);
                RankObject rankObject = new RankObject(rank,displayName, getPermissionRank(rank));
                if (ranks.contains(rankObject)) {
                    continue;
                }
                ranks.add(rankObject);
            }
            return ranks;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteRank(String rank) {
        try (PreparedStatement stmt = RankSystem.getSql().getCon().prepareStatement("DELETE FROM groups WHERE rankname = ?")) {
            stmt.setString(1, rank);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean rankIsUsed(String rank) {
        try {
            ResultSet rs = RankSystem.getSql().getResult("SELECT rank FROM playerrank WHERE rank = '" + rank + "'");
            if (rs.next()) {
                return rs.getString("rank") != null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<UUID> getPlayersWithRank(String rank) {
        try {
            ResultSet rs = RankSystem.getSql().getResult("SELECT uuid FROM playerrank WHERE rank = '" + rank + "'");
            List<UUID> players = new ArrayList<>();
            while (rs.next()) {
                if (players.contains(UUID.fromString(rs.getString("uuid")))) {
                    continue;
                }
                players.add(UUID.fromString(rs.getString("uuid")));
            }
            return players;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void updatePlayerRank(String rank, UUID uuid) {
        try (PreparedStatement stmt = RankSystem.getSql().getCon().prepareStatement("UPDATE playerrank SET rank = ? WHERE uuid = ?")) {
            stmt.setString(1, rank);
            stmt.setString(2, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static LocalDateTime getUntil(UUID uuid) {
        try {
            ResultSet rs = RankSystem.getSql().getResult("SELECT until FROM playerrank WHERE uuid = '" + uuid + "'");
            if (rs.next() && (rs.getString("until") != null && !rs.getString("until").equals("Permanent"))) {
                String until = rs.getString("until");
                return LocalDateTime.parse(until);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void updateUntil(UUID uuid, LocalDateTime until) {
        String untilString = "Permanent";
        if(until != null) {
             untilString = until.toString();
        }
        try (PreparedStatement stmt = RankSystem.getSql().getCon().prepareStatement("UPDATE playerrank SET until = ? WHERE uuid = ?")) {
            stmt.setString(1, untilString);
            stmt.setString(2, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static String getDisplayname(String rank) {
        try {
            ResultSet rs = RankSystem.getSql().getResult("SELECT displayname FROM groups WHERE rankname = '" + rank + "'");
            if (rs.next()) {
                return rs.getString("displayname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static List<String> getPermissionRank(String rank) {
        try {
            ResultSet rs = RankSystem.getSql().getResult("SELECT permission FROM groups WHERE rankname = '" + rank + "'");
            if (rs.next()) {
                if (rs.getString("permission") == null) {
                    return List.of();
                }
                return List.of(rs.getString("permission").split(","));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void updatePermissionRank(String rank, List<String> permissionsList) {
        try (PreparedStatement stmt = RankSystem.getSql().getCon().prepareStatement("UPDATE groups SET permission = ? WHERE rankname = ?")) {
            stmt.setString(1, String.join(",", permissionsList));
            stmt.setString(2, rank);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void updatePermissionPlayer(UUID uuid, List<String> permissionsList) {
        try (PreparedStatement stmt = RankSystem.getSql().getCon().prepareStatement("UPDATE playerrank SET permission = ? WHERE uuid = ?")) {
            stmt.setString(1, String.join(",", permissionsList));
            stmt.setString(2, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}