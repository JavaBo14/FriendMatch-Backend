package com.bopao.readexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.bopao.model.domain.User;
import com.bopao.service.UserService;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class BatchImportUser {
    @Resource private UserService userService;

    private ExecutorService executorService = new ThreadPoolExecutor(40, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

    public static void main(String[] args) {
        BatchImportUser batchImportUser=new BatchImportUser();
        batchImportUser.doConcurrencyInsertUsers();
    }

    public void doConcurrencyInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        String fileName = "E:\\星球项目\\yupao-backend\\src\\main\\resources\\prodExcel.xlsx";
        int batchSize = 5000;  // 每批处理5000行
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();

        // 分批异步读取并处理 Excel 数据
        EasyExcel.read(fileName, UserData.class, new UserDataListener(batchSize, executorService, userService))
                .sheet()
                .doRead();

        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println("总耗时: " + stopWatch.getTotalTimeMillis() + "ms");
    }

    public class UserDataListener extends AnalysisEventListener<UserData> {

        private List<User> cachedDataList = new ArrayList<>();
        private final int batchSize;
        private final ExecutorService executorService;
        private final UserService userService;

        public UserDataListener(int batchSize, ExecutorService executorService, UserService userService) {
            this.batchSize = batchSize;
            this.executorService = executorService;
            this.userService = userService;
        }

        @Override
        public void invoke(UserData userData, AnalysisContext context) {
            // 将 Excel 解析的数据转换为 User 实体类
            User user = new User();
            user.setUsername(userData.getUsername());
//            user.setUserAccount(userData.getUserAccount());
//            user.setAvatarUrl(userData.getAvatarUrl());
//            user.setGender(userData.getGender());
//            user.setUserPassword(userData.getUserPassword());
//            user.setPhone(userData.getPhone());
//            user.setEmail(userData.getEmail());
//            user.setTags(userData.getTags());
//            user.setUserStatus(userData.getUserStatus());
//            user.setUserRole(userData.getUserRole());
            user.setPlanetCode(userData.getPlanetCode());

            cachedDataList.add(user);

            // 当达到批量大小时，异步批量插入
            if (cachedDataList.size() >= batchSize) {
                saveData();
                cachedDataList.clear();
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // 数据全部解析完成后，插入剩余数据
            saveData();
        }

        private void saveData() {
            List<User> userList = new ArrayList<>(cachedDataList);
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("threadName: " + Thread.currentThread().getName());
                userService.saveBatch(userList, batchSize);
            }, executorService);
        }
    }
}
