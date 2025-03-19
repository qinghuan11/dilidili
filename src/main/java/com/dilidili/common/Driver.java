package com.dilidili.common;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class Driver implements java.sql.Driver {
    static {
        try {
            // 静态块：将驱动注册到 DriverManager 中
            java.sql.DriverManager.registerDriver(new Driver());
        } catch (SQLException e) {
            throw new RuntimeException("无法注册 H2 驱动", e);
        }
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url)) {
            return null; // 如果 URL 不被接受，返回 null
        }
        // 此处通常会创建并返回一个新的数据库连接
        // 对于真正的驱动，这里会连接到 H2 数据库
        throw new SQLException("此示例中方法未实现");
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        if (url == null) {
            return false; // URL 为空则返回 false
        }
        // 检查 URL 是否以 "jdbc:h2:" 开头
        return url.startsWith("jdbc:h2:");
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        // 在此示例中返回一个空数组
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 2;  // 示例主版本号
    }

    @Override
    public int getMinorVersion() {
        return 0;  // 示例次版本号
    }

    @Override
    public boolean jdbcCompliant() {
        return true; // 表示符合 JDBC 规范
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("getParentLogger 未被支持");
    }
}