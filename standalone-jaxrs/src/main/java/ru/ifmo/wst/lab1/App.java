package ru.ifmo.wst.lab1;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import ru.ifmo.wst.lab.Configuration;
import ru.ifmo.wst.lab1.dao.ExterminatusDAO;
import ru.ifmo.wst.lab1.rs.AuthChecker;

import javax.sql.DataSource;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public class App {
    @Value
    private static class User {
        private String username;
        private String password;
    }

    public static void main(String[] args) throws Exception {
        log.info("Start application");
        Configuration conf = new Configuration("config.properties");
        String scheme = conf.get("scheme", "http:");
        String host = conf.get("host", "localhost");
        String port = conf.get("port", "8080");
        String baseUrl = scheme + "//" + host + ":" + port;

        DataSource dataSource = initDataSource();
        ResourceConfig resourceConfig = new ResourceConfig().packages("ru.ifmo.wst");
        resourceConfig.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(new ExterminatusDAO(dataSource)).to(ExterminatusDAO.class);
                bind(new AuthChecker() {
                         List<User> users = Arrays.asList(new User("user", "password"),
                                 new User("test", "test"));

                         @Override
                         public boolean check(String userName, String password) {
                             return users.stream()
                                     .anyMatch(user -> Objects.equals(user.getUsername(), userName) &&
                                             Objects.equals(user.getPassword(), password));
                         }
                     }
                ).to(AuthChecker.class);
            }
        });
        log.info("Start server on {}", baseUrl);
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(baseUrl), resourceConfig);
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
