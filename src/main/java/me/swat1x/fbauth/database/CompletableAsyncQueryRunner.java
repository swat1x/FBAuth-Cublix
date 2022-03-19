//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.swat1x.fbauth.database;

import org.apache.commons.dbutils.AsyncQueryRunner;
import org.apache.commons.dbutils.QueryRunner;

import java.util.concurrent.ExecutorService;

public class CompletableAsyncQueryRunner extends AsyncQueryRunner {
    private final ExecutorService executorService;
    private final QueryRunner queryRunner;

    public CompletableAsyncQueryRunner(ExecutorService executorService, QueryRunner queryRunner) {
        super(executorService, queryRunner);
        this.executorService = executorService;
        this.queryRunner = queryRunner;
    }

//    public CompletableFuture<int[]> batch(Connection conn, String sql, Object[][] params) {
//        try {
//            return this.asFuture(this.queryRunner.batch(conn, sql, params));
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public CompletableFuture<int[]> batch(String sql, Object[][] params) {
//        try {
//            return this.asFuture(this.queryRunner.batch(sql, params));
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public <T> CompletableFuture<T> query(Connection conn, String sql, ResultSetHandler<T> rsh, Object... params) {
//        try {
//            return this.asFuture((int[]) this.queryRunner.query(conn, sql, rsh, params));
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public <T> CompletableFuture<T> query(Connection conn, String sql, ResultSetHandler<T> rsh) {
//        try {
//            return this.asFuture((int[]) this.queryRunner.query(conn, sql, rsh, rsh));
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
////    public <T> CompletableFuture<T> query(String sql, ResultSetHandler<T> rsh, Object... params) {
////        try {
////            return this.asFuture((int[]) this.queryRunner.query(conn, sql, rsh, params));
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
////        return null;
////        return this.asFuture(() -> {
////            return this.queryRunner.query(sql, rsh, params);
////        });
////    }
////
////    public <T> CompletableFuture<T> query(String sql, ResultSetHandler<T> rsh) {
////        return this.asFuture(() -> {
////            return this.queryRunner.query(sql, rsh);
////        });
////    }
////
////    public CompletableFuture<Integer> update(Connection conn, String sql) {
////        return this.asFuture(() -> {
////            return this.queryRunner.update(conn, sql);
////        });
////    }
////
////    public CompletableFuture<Integer> update(Connection conn, String sql, Object param) {
////        return this.asFuture(() -> {
////            return this.queryRunner.update(conn, sql, param);
////        });
////    }
////
////    public CompletableFuture<Integer> update(Connection conn, String sql, Object... params) {
////        return this.asFuture(() -> {
////            return this.queryRunner.update(conn, sql, params);
////        });
////    }
////
////    public CompletableFuture<Integer> update(String sql) {
////        return this.asFuture(() -> {
////            return this.queryRunner.update(sql);
////        });
////    }
////
////    public CompletableFuture<Integer> update(String sql, Object param) {
////        return this.asFuture(() -> {
////            return this.queryRunner.update(sql, param);
////        });
////    }
////
////    public CompletableFuture<Integer> update(String sql, Object... params) {
////        return this.asFuture(() -> {
////            return this.queryRunner.update(sql, params);
////        });
////    }
////
////    public CompletableFuture<Integer> execute(Connection conn, String sql) {
////        return this.asFuture(() -> {
////            return this.queryRunner.execute(conn, sql, new Object[0]);
////        });
////    }
////
////    public <T> CompletableFuture<T> insert(String sql, ResultSetHandler<T> rsh) {
////        return this.asFuture(() -> {
////            return this.queryRunner.insert(sql, rsh);
////        });
////    }
////
////    public <T> CompletableFuture<T> insert(String sql, ResultSetHandler<T> rsh, Object... params) {
////        return this.asFuture(() -> {
////            return this.queryRunner.insert(sql, rsh, params);
////        });
////    }
////
////    public <T> CompletableFuture<T> insert(Connection conn, String sql, ResultSetHandler<T> rsh) {
////        return this.asFuture(() -> {
////            return this.queryRunner.insert(conn, sql, rsh);
////        });
////    }
////
////    public <T> CompletableFuture<T> insert(Connection conn, String sql, ResultSetHandler<T> rsh, Object... params) {
////        return this.asFuture(() -> {
////            return this.queryRunner.insert(conn, sql, rsh, params);
////        });
////    }
////
////    public <T> CompletableFuture<T> insertBatch(String sql, ResultSetHandler<T> rsh, Object[][] params) {
////        return this.asFuture(() -> {
////            return this.queryRunner.insertBatch(sql, rsh, params);
////        });
////    }
////
////    public <T> CompletableFuture<T> insertBatch(Connection conn, String sql, ResultSetHandler<T> rsh, Object[][] params) {
////        return this.asFuture(() -> {
////            return this.queryRunner.insertBatch(conn, sql, rsh, params);
////        });
////    }
//
//    public CompletableFuture<Void> execute(Connection conn, String sql, Object param) {
//        return this.asVoidFuture(() -> {
//            this.queryRunner.execute(conn, sql, new Object[]{param});
//        });
//    }
//
//    public CompletableFuture<Void> execute(Connection conn, String sql, Object... params) {
//        return this.asVoidFuture(() -> {
//            this.queryRunner.execute(conn, sql, params);
//        });
//    }
//
//    public CompletableFuture<Void> execute(String sql) {
//        return this.asVoidFuture(() -> {
//            this.queryRunner.execute(sql, new Object[0]);
//        });
//    }
//
//    public CompletableFuture<Void> execute(String sql, Object param) {
//        return this.asVoidFuture(() -> {
//            this.queryRunner.execute(sql, new Object[]{param});
//        });
//    }
//
//    public CompletableFuture<Void> execute(String sql, Object... params) {
//        return this.asVoidFuture(() -> {
//            this.queryRunner.execute(sql, params);
//        });
//    }
//
////    private <T> CompletableFuture<T> asFuture(int[] supplier) {
////        return (CompletableFuture<T>) CompletableFuture.supplyAsync(supplier, this.executorService);
////    }
//
//    private CompletableFuture<Void> asVoidFuture(UncheckedRunnable runnable) {
//        return CompletableFuture.runAsync(runnable, this.executorService);
//    }
}
