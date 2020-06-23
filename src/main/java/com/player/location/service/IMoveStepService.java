package com.player.location.service;


import com.player.location.model.LocationInfo;
import com.player.location.model.Playground;
import com.player.location.tools.Result;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IMoveStepService {

    //init size for playground
//    String initPlayground(int x, int y);
//
//    Result initPlayerLocation(Playground playground, LocationInfo locationInfo);

    Result move(List<String> orderList);

    Result saveFile(MultipartFile files, HttpServletRequest request);

    Result readFileByLine(String filePath);




}
