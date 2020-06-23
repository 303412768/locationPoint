package com.player.location.controller;

import com.player.location.model.LocationInfo;
import com.player.location.model.Playground;

import com.player.location.service.IMoveStepService;
import com.player.location.tools.Result;
import com.player.location.tools.StaticInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
public class CalculateController {

    @Autowired
    public IMoveStepService moveStepService;


    @PostMapping("/upload")
    @ResponseBody
    public Result  playerMove(@RequestParam(value = "txt_file") MultipartFile files,HttpServletRequest request) {
        Result result=moveStepService.saveFile(files, request);
        if (result.getCode() != 200) {
            return result;
        }
        result = moveStepService.readFileByLine(result.object.toString());
        if (result.getCode() != 200) {
            return result;
        }
        result =moveStepService.move((List<String>) result.object);
        return result;
    }
}
