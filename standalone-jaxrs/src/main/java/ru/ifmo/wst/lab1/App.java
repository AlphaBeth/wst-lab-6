package ru.ifmo.wst.lab1;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.grizzly.http.server.HttpServer;
import ru.ifmo.wst.lab.Configuration;
import ru.ifmo.wst.lab1.dao.ExterminatusDAO;
import ru.ifmo.wst.lab1.rs.AllExceptionMapper;
import ru.ifmo.wst.lab1.rs.ExterminatusResource;
import ru.ifmo.wst.lab1.rs.ResourceExceptionMapper;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class App {
    public static void main(String[] args) throws Exception {
        log.info("Start application");
        Configuration conf = new Configuration("config.properties");
        String scheme = conf.get("scheme", "http:");
        String host = conf.get("host", "localhost");
        String port = conf.get("port", "8080");
        String baseUrl = scheme + "//" + host + ":" + port;

        DataSource dataSource = initDataSource();
        ExterminatusResource.GLOBAL_DAO = new ExterminatusDAO(dataSource);
        ResourceConfig resourceConfig = new ClassNamesResourceConfig(ExterminatusResource.class, ResourceExceptionMapper.class,
                AllExceptionMapper.class);
        log.info("Start server on {}", baseUrl);
        HttpServer server = GrizzlyServerFactory.createHttpServer(baseUrl, resourceConfig);
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        log.info("Application was successfully started");
        System.in.read();
    }

    @SneakyThrows
    private static DataSource initDataSource() {
        InputStream dsPropsStream = App.class.getClassLoader().getResourceAsStream("datasource.properties");
        Properties dsProps = new Properties();
        dsProps.load(dsPropsStream);
        HikariConfig hikariConfig = new HikariConfig(dsProps);
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        return dataSource;
    }
}
