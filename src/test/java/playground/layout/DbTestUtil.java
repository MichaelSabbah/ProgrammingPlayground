package playground.layout;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

final class DbTestUtil {
	
	private DbTestUtil() {
		
	}
	
	public static void resetAutoIncrementColumns(ApplicationContext applicationContext,
												 String... tableNames) throws SQLException{
		
		String resetSqlTemplate = getResetSqlTemplate(applicationContext);
		DataSource dataSource = applicationContext.getBean(DataSource.class);
		try(Connection dbConnection = dataSource.getConnection()){
			for(String resetSqlArgument : tableNames) {
				try(Statement statement = dbConnection.createStatement()){
					String resetSql = String.format(resetSqlTemplate,resetSqlArgument);
					statement.execute(resetSql);
				}
			}
		}
	}

	private static String getResetSqlTemplate(ApplicationContext applicationContext) {
		Environment environment = applicationContext.getBean(Environment.class);
		return environment.getRequiredProperty("test.reset.sql.template");
	}	
	
}
