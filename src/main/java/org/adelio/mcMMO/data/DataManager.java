package org.adelio.mcMMO.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataManager {
    private HikariDataSource dataSource;
    private PlayerManager playerManager;
    private MySQLManager mysqlManager;

    public void init(String host, String db, String user, String pass) {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + "/" + db);
        config.setUsername(user);
        config.setPassword(pass);
        this.dataSource = new HikariDataSource(config);

        DatabaseManager dbControl = new DatabaseManager();
        dbControl.setDataSource(this.dataSource);

        this.mysqlManager = new MySQLManager(dbControl);
        this.playerManager = new PlayerManager(this.mysqlManager);

        this.mysqlManager.setupTable();
        System.out.println("[mcMMO] DB 연결 및 매니저 초기화 완료.");
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public MySQLManager getMysqlManager() {
        return mysqlManager;
    }

    public void save() {
        if (playerManager != null) playerManager.saveAll();
    }
}