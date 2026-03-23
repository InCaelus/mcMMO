package org.adelio.mcMMO.data;

import org.adelio.mcMMO.skill.SkillType;
import java.sql.*;
import java.util.UUID;

public class MySQLManager {
    private final DatabaseManager db;

    public MySQLManager(DatabaseManager db) {
        this.db = db;
    }

    public PlayerData loadData(UUID uuid) {
        PlayerData data = new PlayerData(uuid);
        String sql = "SELECT * FROM mmo_data WHERE uuid = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                data.setLevel(SkillType.MINING, rs.getInt("mining_lvl"));
                data.setExp(SkillType.MINING, rs.getDouble("mining_exp"));

                data.setLevel(SkillType.WOODCUTTING, rs.getInt("wood_lvl"));
                data.setExp(SkillType.WOODCUTTING, rs.getDouble("wood_exp"));

                data.setLevel(SkillType.COMBAT, rs.getInt("combat_lvl"));
                data.setExp(SkillType.COMBAT, rs.getDouble("combat_exp"));

                data.addStatPoints(rs.getInt("stat_points"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void saveData(PlayerData data) {

        String sql = "REPLACE INTO mmo_data VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, data.getUuid().toString());

            ps.setInt(2, data.getLevel(SkillType.MINING));
            ps.setDouble(3, data.getExp(SkillType.MINING));

            ps.setInt(4, data.getLevel(SkillType.WOODCUTTING));
            ps.setDouble(5, data.getExp(SkillType.WOODCUTTING));

            ps.setInt(6, data.getLevel(SkillType.COMBAT));
            ps.setDouble(7, data.getExp(SkillType.COMBAT));

            ps.setInt(8, data.getStatPoints());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 테이블 생성 (서버 켜질 때 자동 호출됨)
    public void setupTable() {
        String sql = "CREATE TABLE IF NOT EXISTS mmo_data (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "mining_lvl INT DEFAULT 1, " +
                "mining_exp DOUBLE DEFAULT 0, " +
                "wood_lvl INT DEFAULT 1, " +
                "wood_exp DOUBLE DEFAULT 0, " +
                "combat_lvl INT DEFAULT 1, " +
                "combat_exp DOUBLE DEFAULT 0, " +
                "stat_points INT DEFAULT 0)";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}