package com.player.location.service;


import com.player.location.model.LocationInfo;
import com.player.location.model.Playground;
import com.player.location.tools.Result;
import com.player.location.tools.StaticInfo;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class MoveStepService implements IMoveStepService {




    @Override
    public Result move(List<String> orderList) {
        Playground playground = getPlayground(orderList.get(0));
        if (null == playground) {
            return Result.error(null, "Data Error on line one!");
        }
        List<LocationInfo> infos = findAllStatPointAndOrder(orderList);
        beginTowardAndMove(infos);
        for (LocationInfo info : infos) {
            if (info.getX() > playground.getMaxX() || info.getY() > playground.getMaxY()) {
                return Result.error(null, StaticInfo.ERROR_MSG_OVER_BORDER);
            }
        }
        return Result.ok(infos);
    }

    @Override
    public Result saveFile(MultipartFile files, HttpServletRequest request) {
        String sourceName = files.getOriginalFilename(); // 原始文件名
        String fileType = sourceName.substring(sourceName.lastIndexOf("."));
        if (files.isEmpty() || StringUtils.isBlank(fileType)) {
            return Result.error(null, "File is none");
        }
        String filename= RandomStringUtils.randomAlphanumeric(10)+"."+fileType;


        // Get temp path
        // for example /wabapp/upload/xxx.txt
        String base = request.getSession().getServletContext().getRealPath("/upload");
        File file = new File(base);
        if (!file.exists()) {
            file.mkdirs();
        }

        // upload file
        String path = base + File.separator + filename;
        File upload = new File(path);

        try {
            files.transferTo(upload);
        } catch (IOException e) {
            return Result.error(null, "There are some errors during upload");
        }


        return Result.ok(path);
    }

    @Override
    public Result readFileByLine(String filePath) {
        String lineText;
        List<String> list = new ArrayList<>();
        //*** Read file by line ***
        File file = new File(filePath);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            while ((lineText = br.readLine()) != null) {
                //When current line is empty, skip this line
                if (lineText.trim().length() == 0) {
                    continue;
                }
                list.add(lineText.trim());
            }

        } catch (IOException e) {
            e.printStackTrace();
            file.delete();
            return Result.error(list,"Read file error");
        }
        file.delete();
        return Result.ok(list);

    }

    /**
     * 设置整个场地大小，后期会校验是否越界
     * @param str
     * @return
     */
    private Playground getPlayground(String str) {
        String[] pgArr = str.split("\\s+");
        if (StringUtils.isNumeric(pgArr[0]) && StringUtils.isNumeric(pgArr[1])) {
            Playground playground = new Playground();
            playground.setMaxX(Integer.parseInt(pgArr[0]));
            playground.setMaxY(Integer.parseInt(pgArr[1]));
            return playground;
        }
        return null;
    }

    public static String allChars = "LMR";
    /**
     * 验证指令是否有效
     *
     * @param str
     * @return
     */
    public static Boolean vailOrder(String str) {

        //将字符串转化为字符数组
        char[] chars = str.toCharArray();

        //定义一个字符串c，循环遍历遍历chars数组
        for (char c : chars) {
            Boolean isInclude = allChars.contains(Character.toString(c));
            if (!isInclude) {
                return false;
            }

        }
        return true;
    }

    /**
     * 查询所有人的起始点以及对于的移动指令
     *
     * @param stringList 所有的指令合集
     * @return
     */
    private List<LocationInfo> findAllStatPointAndOrder(List<String> stringList) {
        List<LocationInfo> locationInfos = new ArrayList<>();
        for (String str : stringList) {
            String[] arr = str.split("\\s+");
            //排除初始化数据
            if (arr.length == 2) {
                continue;
            }
            //判定每个人的起始点
            if (StringUtils.isNumeric(arr[0]) && StringUtils.isNumeric(arr[1]) && StaticInfo.DIRECTIONS.contains(arr[2]) && arr.length == 3) {
                LocationInfo locationInfo = new LocationInfo();
                locationInfo.setX(Integer.parseInt(arr[0]));
                locationInfo.setY(Integer.parseInt(arr[1]));
                locationInfo.setToward(arr[2].trim().toUpperCase());
                locationInfos.add(locationInfo);
            } else {
                //解析移动命令并存储到对于人的数据中
                if (vailOrder(str)) {
                    LocationInfo info = locationInfos.get(locationInfos.size() - 1);
                    List<String> orders;
                    if (info.getOrderList() == null) {
                        orders = new ArrayList<>();
                    } else {
                        orders = info.getOrderList();
                    }
                    orders.add(str);
                    info.setOrderList(orders);
                }
            }
        }

        return locationInfos;
    }

    private static String[] towardArr = {"W", "N", "E", "S"};

    /**
     * 移动与方向逻辑
     * @param infos
     */
    public  void beginTowardAndMove(List<LocationInfo> infos) {
        for (LocationInfo locationInfo : infos) {
            for (String str : locationInfo.getOrderList()) {
                char[] chars = str.toCharArray();
                for (char c : chars) {
                    String nexStep = Character.toString(c).toUpperCase();
                    switch (nexStep) {
                        case "L":
                        case "R":
                            locationInfo.setToward(getNextToward(locationInfo.getToward(), nexStep));
                            break;
                        case "M":
                            moveByOrder(locationInfo);
                            break;
                    }
                }
            }
        }
    }

    /**
     * 方向处理
     * @param currentToward
     * @param nextToward
     * @return
     */
    public  String getNextToward(String currentToward, String nextToward) {
        String newToward = currentToward;
        for (int i = 0; i < towardArr.length; i++) {
            if (towardArr[i].equals(currentToward)) {
                switch (nextToward) {
                    case "L":
                        if (i == 0) {
                            newToward = towardArr[3];
                        } else {
                            newToward = towardArr[i - 1];
                        }
                        break;
                    case "R":
                        if (i == 3) {
                            newToward = towardArr[0];
                        } else {
                            newToward = towardArr[i + 1];
                        }
                        break;
                }
            }
        }
        return newToward;
    }

    /**
     * 移动逻辑
     * @param info
     */
    public  void moveByOrder(LocationInfo info) {
        String toward = info.getToward();
        int x = info.getX();
        int y = info.getY();
        switch (toward.toUpperCase()) {
            case "W" -> x = x - 1;
            case "E" -> x = x + 1;
            case "N" -> y = y + 1;
            case "S" -> y = y - 1;
        }
        info.setX(x);
        info.setY(y);
    }
}
