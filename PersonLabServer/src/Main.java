import main.*;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.internal.SessionImpl;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * Программа PersonLab Server
 * @author Matvei Baranov
 * @version 5.0
 */
public class Main {
    public static Logger logger = Logger.getLogger("ServerLog");
    public static Commands commands = new Commands();

    public static void main(String[] args) {
        try {
            SessionFactoryImpl sessionFactory = (SessionFactoryImpl) getHibernateSessionFactory();
            Runtime.getRuntime().addShutdownHook(new Thread(sessionFactory::close));
            ClientApplicationManager clientManager= new ClientApplicationManager(logger);
            SQLManager sqlManager = new SQLManager(sessionFactory,logger,clientManager);
            if (sqlManager.authenticateUser("testuser","test")==0){
                sqlManager.addUser("testuser","test");
            }
            commands.load(sqlManager);
            UDPDatagramServer server = new UDPDatagramServer(InetAddress.getLocalHost(), 25555, commands, sessionFactory, sqlManager,logger,clientManager);
            System.out.println("Программа PersonLab Server 5.0. Ожидаем соединение.");
            server.run();
        } catch (UnknownHostException e) {
            System.out.println("Неизвестный хост");
            logger.log(Level.SEVERE,"Неизвестный хост", e);
        }
    }
    private static SessionFactory getHibernateSessionFactory() {
        var environmentFile = "env.dev";
        var isProduction = System.getenv("PROD");
        if (isProduction != null && isProduction.equals("true")) {
            environmentFile = ".env";
        }
        Dotenv dotenv = Dotenv.configure()
                .filename(environmentFile)
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
        var url = dotenv.get("DB_URL");
        var user = dotenv.get("DB_USER");
        var password = dotenv.get("DB_PASSWORD");

        if (url == null || url.isEmpty() || user == null || user.isEmpty() || password == null || password.isEmpty()) {
            System.out.println("В .env файле не обнаружены данные для подключения к базе данных");
            System.exit(1);
        }
        return Hibernate.getSessionFactory(url, user, password, logger);
    }
}