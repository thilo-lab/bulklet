package com.example.demo;

import com.microsoft.sqlserver.jdbc.SQLServerBulkCopy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DemoApplicationTest {

    @Test
    void executeSqlTasksUsesJdbcCalls() throws Exception {
        DemoApplication app = new DemoApplication();
        ConfigurableApplicationContext ctx = mock(ConfigurableApplicationContext.class);
        ReflectionTestUtils.setField(app, "context", ctx);

        Connection sourceConn = mock(Connection.class);
        Connection targetConn = mock(Connection.class);
        Statement sourceStmt1 = mock(Statement.class);
        Statement sourceStmt2 = mock(Statement.class);
        Statement targetStmt = mock(Statement.class);
        ResultSet rs = mock(ResultSet.class);

        when(sourceConn.createStatement()).thenReturn(sourceStmt1, sourceStmt2);
        when(targetConn.createStatement()).thenReturn(targetStmt);
        when(sourceStmt2.executeQuery("SELECT id, name FROM quelle")).thenReturn(rs);

        try (MockedStatic<DriverManager> dm = mockStatic(DriverManager.class);
             MockedConstruction<SQLServerBulkCopy> bulkConst = mockConstruction(SQLServerBulkCopy.class)) {
            dm.when(() -> DriverManager.getConnection(anyString(), any(Properties.class)))
              .thenReturn(sourceConn, targetConn);

            app.executeSqlTasks();

            dm.verify(() -> DriverManager.getConnection(anyString(), any(Properties.class)), times(2));
            verify(targetStmt).executeUpdate(contains("CREATE TABLE ziel"));
            SQLServerBulkCopy bulkCopy = bulkConst.constructed().get(0);
            verify(bulkCopy).setDestinationTableName("ziel");
            verify(bulkCopy).writeToServer(rs);
            verify(bulkCopy).close();
        }
    }
}
