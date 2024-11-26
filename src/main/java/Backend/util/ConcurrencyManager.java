package Backend.util;

import Backend.model.Account;
import Backend.model.UserAccount;
import Backend.service.DatabaseHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrencyManager {
    private static final int MAX_THREADS = 10;
    private final ExecutorService executorService;
    public ConcurrencyManager() {
        this.executorService = Executors.newFixedThreadPool(MAX_THREADS);
    }
    public void executeTask(Runnable task) {
        executorService.submit(task);
    }
    public void shutdown() {
        executorService.shutdown();
    }

    public void registerAccount(UserAccount userAccount, DatabaseHandler dbHandler) {
        executeTask(() -> {
            boolean success = userAccount.register(dbHandler);
            if (success) {
                System.out.println("Account registered successfully: " + userAccount.getUsername());
            } else {
                System.out.println("Account registration failed: " + userAccount.getUsername());
            }
        });
    }

    public void loginAccount(UserAccount userAccount, DatabaseHandler dbHandler) {
        executeTask(() -> {
            boolean success = userAccount.login(dbHandler);
            if (success) {
                System.out.println("Account login successfully: " + userAccount.getUsername());
            } else {
                System.out.println("Account login failed: " + userAccount.getUsername());
            }
        });
    }
}
